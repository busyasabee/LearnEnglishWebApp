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
import java.util.List;
import java.util.Map;

/**
 * Created by Дмитрий on 29.06.2017.
 */
@WebServlet(name = "EditWordServlet", urlPatterns = "/editWord")
public class EditWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String newEnglishName, newRussianName, newTranscription, newPartOfSpeech;
        newEnglishName = newRussianName = newTranscription = newPartOfSpeech = "";

        int oldWordId = Integer.parseInt(request.getParameter("wordId"));
        newEnglishName = request.getParameter("english");
        newRussianName = request.getParameter("russian");
        newTranscription = request.getParameter("transcription");
        newPartOfSpeech = request.getParameter("partOfSpeech");
        Word newWord = new Word(newEnglishName, newRussianName, newTranscription, newPartOfSpeech, 0);

        ServletContext servletContext = getServletContext();
        Map<String, List<Word>> loginWordsMap = (Map<String, List<Word>>) servletContext.getAttribute("loginWordsMap");
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        Person person = (Person) session.getAttribute("person");
        int personId = 0;
        int wordId = 0;
        boolean isWordExist = false;

        if (person != null){
            personId = person.getPerson_id();
        }
        else {
            try(PreparedStatement getPersonIdStatement = connection.prepareStatement("SELECT person.id from person " +
                    "WHERE login = ? ")) {

                getPersonIdStatement.setString(1, login);
                try(ResultSet resultSet = getPersonIdStatement.executeQuery()) {
                    while ( resultSet.next()){
                        personId = resultSet.getInt(1);
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        for (Word word: loginWordsMap.get(login)) {
            if (newEnglishName.equals(word.getEnglishName()) && newRussianName.equals(word.getRussianName())){
                if (!newTranscription.equals(word.getTranscription())){
                    word.setTranscription(newTranscription);
                }

                if (!newPartOfSpeech.equals(word.getPartOfSpeech())){
                    word.setPartOfSpeech(newPartOfSpeech);
                }

                wordId = word.getWordId();
                isWordExist = true;
            }
        }

        if (isWordExist){
            try (PreparedStatement updateWordStatement = connection.prepareStatement("UPDATE word SET transcription =" +
                    "?, partofspeech = ? WHERE word.id = ?")){

                updateWordStatement.setString(1, newTranscription);
                updateWordStatement.setString(2, newPartOfSpeech);
                updateWordStatement.setInt(3, wordId);
                updateWordStatement.executeUpdate();

            } catch (SQLException e) {
                // добавить сообщение об ошибке
                e.printStackTrace();
            }

            try (PreparedStatement updatePersonWordStatement = connection.prepareStatement("UPDATE person_word SET " +
                    "other_transcription = ?, other_part_of_speech = ? WHERE word_id = ? AND person_id = ?")){
                updatePersonWordStatement.setString(1, newTranscription);
                updatePersonWordStatement.setString(2, newPartOfSpeech);
                updatePersonWordStatement.setInt(3, wordId);
                updatePersonWordStatement.setInt(4, personId);
                updatePersonWordStatement.executeUpdate();

            } catch (SQLException e) {
                // добавить сообщение об ошибке
                e.printStackTrace();
            }

        }
        else {
            try (PreparedStatement addWordStatement = connection.prepareStatement("INSERT INTO word (englishname, russianname,transcription,partofspeech) VALUES " +
                    " (?, ?, ?, ?) returning id;" )){

                addWordStatement.setString(1, newEnglishName);
                addWordStatement.setString(2, newRussianName);
                addWordStatement.setString(3, newTranscription);
                addWordStatement.setString(4, newPartOfSpeech);

                addWordStatement.executeQuery();

                try (ResultSet resultSet = addWordStatement.getResultSet()){
                    resultSet.next();
                    wordId = resultSet.getInt("id");
                    newWord.setWordId(wordId);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (PreparedStatement changeWordIdStatement = connection.prepareStatement("UPDATE person_word SET word_id = ? WHERE person_id = ? AND word_id = ?")){

                changeWordIdStatement.setInt(1, wordId);
                changeWordIdStatement.setInt(2, personId);
                changeWordIdStatement.setInt(3, oldWordId);
                changeWordIdStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            List<Word> personWords = loginWordsMap.get(login);
            personWords.removeIf( p -> p.getWordId() == oldWordId);
            personWords.add(newWord);

        }

        request.setAttribute("words", loginWordsMap.get(login));
        request.getRequestDispatcher("/dictionary.jsp").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        int wordId = Integer.parseInt(request.getParameter("id"));
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        Map<String, List<Word>> loginWordsMap =  (Map<String, List<Word>> )servletContext.getAttribute("loginWordsMap");
        Word editedWord = new Word();

        for (Word word:loginWordsMap.get(login)) {
            if (word.getWordId() == wordId){
                editedWord = word;
                break;
            }
        }

        request.setAttribute("editedWord", editedWord);
        request.setAttribute("wordId", wordId);
        request.getRequestDispatcher("/editWord.jsp").forward(request, response);

    }
}
