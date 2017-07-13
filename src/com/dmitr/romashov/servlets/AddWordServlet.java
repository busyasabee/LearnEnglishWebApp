package com.dmitr.romashov.servlets;

import com.dmitr.romashov.Person;
import com.dmitr.romashov.Word;

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

@WebServlet(name = "AddWordServlet", urlPatterns = "/addWord")
public class AddWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, List<Word>> loginWordsMap = new HashMap<>();
        request.setCharacterEncoding("UTF-8");

        ServletContext servletContext = getServletContext();
        if (servletContext.getAttribute("loginWordsMap") != null) {
            loginWordsMap = (HashMap<String, List<Word>>) servletContext.getAttribute("loginWordsMap");

        }

        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession session = request.getSession();
        Person person = (Person)session.getAttribute("person");
        int personId = 0;
        personId = person.getPerson_id();
        String login = person.getLogin();//(String) session.getAttribute("login");

//        if (login == null) {
//            request.getRequestDispatcher("/login.jsp").forward(request, response);
//            return;
//        }

        String englishName = request.getParameter("englishName");
        String russianName = request.getParameter("russianName");
        String transcription = request.getParameter("transcription");
        String partOfSpeech = request.getParameter("partOfSpeech");
        int knowledge = 0;
        Word newWord = new Word(englishName, russianName, transcription, partOfSpeech, knowledge);
        List<Word> personWords = new ArrayList<>();
        boolean wordExist = false;
        int wordId = 0;
        boolean wordAdded = false;


//        if (person != null){
//            personId = person.getPerson_id();
//        }
//        else {
//            try(PreparedStatement getPersonIdStatement = connection.prepareStatement("SELECT person.id from person " +
//                    "WHERE login = ? ")) {
//
//                getPersonIdStatement.setString(1, login);
//                try(ResultSet resultSet = getPersonIdStatement.executeQuery()) {
//                    while ( resultSet.next()){
//                        personId = resultSet.getInt(1);
//                    }
//
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        out:
        for (String loginKey : loginWordsMap.keySet()) {
            personWords = loginWordsMap.get(loginKey);

            for (Word word:personWords) {
                if (word.equals(newWord)){
                    wordExist = true;
                    wordId = word.getWordId();
                    newWord.setWordId(wordId);
                    break out;
                }
            }
        }
        try {

            if (wordExist) {
                personWords = loginWordsMap.get(login);
                personWords.add(newWord);

            } else {
                try (PreparedStatement insertNewWordStatement
                             = connection.prepareStatement("INSERT INTO word(englishname, russianname, transcription, partofspeech) VALUES " +
                        "(?,?,?,?) returning id;")) {
                    insertNewWordStatement.setString(1, englishName);
                    insertNewWordStatement.setString(2, russianName);
                    insertNewWordStatement.setString(3, transcription);
                    insertNewWordStatement.setString(4, partOfSpeech);

                    insertNewWordStatement.executeQuery();
                    try (ResultSet resultSet = insertNewWordStatement.getResultSet()) {

                        resultSet.next();
                        wordId = resultSet.getInt("id");
                        newWord.setWordId(wordId);
                    }

                }

                personWords = loginWordsMap.get(login);
                personWords.add(newWord);

            }

            try (PreparedStatement insertPersonWordStatement
                         = connection.prepareStatement("INSERT INTO person_word(person_id, word_id, knowledge) VALUES " +
                    "(?,?,?)")) {

                insertPersonWordStatement.setInt(1, personId);
                insertPersonWordStatement.setInt(2, wordId);
                insertPersonWordStatement.setInt(3, knowledge);
                insertPersonWordStatement.executeUpdate();

            }
        } catch (Exception e) {
            e.printStackTrace();
            personWords = loginWordsMap.get(login);
            request.setAttribute("words", personWords);
            request.setAttribute("wordAdded", wordAdded);
            request.getRequestDispatcher("/dictionary.jsp").forward(request, response);
            return;
        }

        wordAdded = true;
        request.setAttribute("words", personWords);
        request.setAttribute("wordAdded", wordAdded);
        request.getRequestDispatcher("/dictionary.jsp").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
