package com.dmitr.romashov.servlets;

import com.dmitr.romashov.Person;
import com.dmitr.romashov.Word;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@WebServlet("/learnwords")
public class LearnWordsServlet extends HttpServlet {
    private static int wordMemory = 0;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        request.setCharacterEncoding("UTF-8");
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        int taskNumber = 0;
        int wordNumber = 0;
        int numberTrainingWords = 5;
        List<Word> trainingWords = new ArrayList<>();
        int personId = 0;
        int wordId = 0;
        int maxTaskNumber = 2;
        String isChecked = "no";

        taskNumber = (int) session.getAttribute("taskNumber");
        wordNumber = (int) session.getAttribute("wordNumber");
        trainingWords = (List<Word>) session.getAttribute("trainingWords");
        isChecked = (String) session.getAttribute("isChecked");

        Word trainingWord = trainingWords.get(wordNumber);
        String rightAnswer = "";
        //String login = (String) session.getAttribute("login");

        switch (taskNumber) {
            case 0:
                rightAnswer = trainingWord.getRussianName();
                break;
            case 1:
                rightAnswer = trainingWord.getEnglishName();
                break;
        }

        String answer = request.getParameter("answer");
        Enumeration fields = request.getParameterNames();

        if (fields.hasMoreElements()) {
            while (fields.hasMoreElements()) {
                String field = (String) fields.nextElement();
                if (field.equals("check")) {
                    if(isChecked == null || isChecked.equals("no")){
                        if (rightAnswer.equals(answer)) {
                            trainingWord.setKnowledge(trainingWord.getKnowledge() + 1);
                            Person person = (Person) session.getAttribute("person");
                            personId = person.getPerson_id();
                            wordId = trainingWord.getWordId();

                            try (PreparedStatement insertPersonWordStatement
                                         = connection.prepareStatement("UPDATE person_word SET knowledge = ? WHERE person_id = ? and word_id = ?")) {

                                insertPersonWordStatement.setInt(1, trainingWord.getKnowledge());
                                insertPersonWordStatement.setInt(2, personId);
                                insertPersonWordStatement.setInt(3, wordId);
                                insertPersonWordStatement.executeUpdate();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            request.setAttribute("isRight", "yes");
                        } else {
                            request.setAttribute("isRight", "no");
                        }

                        session.setAttribute("isChecked", "yes");

                    }
                    else {
                        if (rightAnswer.equals(answer)) {
                            request.setAttribute("isRight", "yes");
                        }
                        else {
                            request.setAttribute("isRight", "no");
                        }
                    }

                    request.getRequestDispatcher("/words.jsp").forward(request, response);
                    return;
                } else if (field.equals("next")) {
                    wordNumber += 1;
                    if (wordNumber > numberTrainingWords - 1) { // переходим к следующему заданию
                        wordNumber = 0;
                        taskNumber += 1;
                    }

                    session.setAttribute("wordNumber", wordNumber);
                    session.setAttribute("taskNumber", taskNumber);
                    session.setAttribute("isChecked", "no");

                    if (taskNumber >= maxTaskNumber) {
                        request.setAttribute("words", trainingWords);
                        request.getRequestDispatcher("/wordsTrained.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/words.jsp").forward(request, response);
                    }
                    return;
                }

            }
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int taskNumber = 0;
        int wordNumber = 0;
        int numberTrainingWords = 5;
        boolean firstTime = true; // надо получить слова
        List<Word> trainingWords = new ArrayList<>();
        List<Word> allPersonWords = new ArrayList<>();
        int allWordsCount = 0;
        Map<String, List<Word>> loginWordsMap = new HashMap<>();
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession session = request.getSession();

        String login = "";
        int personId = 0;

//        if (login == null) {
//            request.getRequestDispatcher("/login.jsp").forward(request, response);
//            return;
//        }

        if (servletContext.getAttribute("loginWordsMap") != null) {
            loginWordsMap = (HashMap<String, List<Word>>) servletContext.getAttribute("loginWordsMap");
        }

        Person person = (Person) session.getAttribute("person");
        personId = person.getPerson_id();
        login = person.getLogin();
//        if (person != null) {
//            personId = person.getPerson_id();
//        } else {
//            try (PreparedStatement getPersonIdStatement = connection.prepareStatement("SELECT person.id from person " +
//                    "WHERE login = ? ")) {
//
//                getPersonIdStatement.setString(1, login);
//                try (ResultSet resultSet = getPersonIdStatement.executeQuery()) {
//                    while (resultSet.next()) {
//                        personId = resultSet.getInt(1);
//                    }
//
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        try {
            if (loginWordsMap.containsKey(login)) {
                // выбор пяти самых плохо изученных слов
                allPersonWords = loginWordsMap.get(login);
                Collections.sort(allPersonWords);
                allWordsCount = allPersonWords.size();

                if (allWordsCount < numberTrainingWords) {
                    trainingWords = allPersonWords.subList(0, allWordsCount);
                } else {
                    trainingWords = allPersonWords.subList(0, numberTrainingWords);
                }


            } else {
                // делаем sql запрос, получаем слова

                try (PreparedStatement selectWordsStatement = connection.prepareStatement("SELECT englishname, russianname, transcription, partofspeech, knowledge, id" +
                        " FROM word JOIN person_word on word.id = person_word.word_id WHERE person_word.person_id = ?  ORDER BY knowledge LIMIT 5");) {
                    selectWordsStatement.setInt(1, personId);
                    try (ResultSet resultSet = selectWordsStatement.executeQuery()) {
                        String english;
                        String russian;
                        String transcription;
                        String partOfSpeech;
                        int knowledge;
                        int wordId;
                        while (resultSet.next()) {
                            english = resultSet.getString(1);
                            russian = resultSet.getString(2);
                            transcription = resultSet.getString(3);
                            partOfSpeech = resultSet.getString(4);
                            knowledge = resultSet.getInt(5);
                            wordId = resultSet.getInt(6);
                            Word word = new Word(wordId, english, russian, knowledge, transcription, partOfSpeech);
                            trainingWords.add(word);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.setAttribute("trainingWords", trainingWords);
        session.setAttribute("wordNumber", wordNumber);
        session.setAttribute("taskNumber", taskNumber);
        request.getRequestDispatcher("/words.jsp").forward(request, response);
        //response.sendRedirect("/words.jsp");

    }
}
