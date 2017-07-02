package com.dmitr.romashov.servlets;

import com.dmitr.romashov.Person;
import com.dmitr.romashov.Word;
import org.postgresql.jdbc.PgArray;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Дмитрий on 09.06.2017.
 */
@WebServlet(name = "DictionaryServlet", urlPatterns = "/dict")
public class DictionaryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Word> words = new ArrayList<>();
        List<Integer> wordsId = new ArrayList<>();
        Map<String, List<Word>> loginWordsMap = new HashMap<>();

        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession session = request.getSession();
        if(servletContext.getAttribute("loginWordsMap") != null){
            loginWordsMap = (HashMap<String, List<Word>>)servletContext.getAttribute("loginWordsMap");

        }

        String login = (String)session.getAttribute("login");
        int personId = 0;

        if (login == null){

            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if(loginWordsMap.containsKey(login)){
            request.setAttribute("words", loginWordsMap.get(login));
            request.getRequestDispatcher("/dictionary.jsp").forward(request, response);
            return;
        }


        Person person = (Person)session.getAttribute("person");
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

        try (PreparedStatement selectWordsStatement = connection.prepareStatement("SELECT englishname, russianname, transcription, partofspeech, knowledge, id, other_russian_name, other_transcription, other_part_of_speech" +
                " FROM word JOIN person_word on word.id = person_word.word_id WHERE person_word.person_id = ?");) {
            selectWordsStatement.setInt(1, personId);
            try (ResultSet resultSet = selectWordsStatement.executeQuery()) {
                String english, russian, transcription, partOfSpeech, otherRussian, otherTranscription, otherPartOfSpeech ;
                int knowledge, wordId;
                while (resultSet.next()){
                    english = resultSet.getString(1);
                    russian = resultSet.getString(2);
                    transcription = resultSet.getString(3);
                    partOfSpeech = resultSet.getString(4);
                    knowledge = resultSet.getInt(5);
                    wordId = resultSet.getInt(6);
                    otherRussian = resultSet.getString(7);
                    otherTranscription = resultSet.getString(8);
                    otherPartOfSpeech = resultSet.getString(9);
                    if (!otherRussian.equals("")) russian = otherRussian;
                    if (!otherTranscription.equals("")) transcription = otherTranscription;
                    if (!otherPartOfSpeech.equals("")) partOfSpeech = otherPartOfSpeech;
                    Word word = new Word();
                    word.setEnglishName(english);
                    word.setRussianName(russian);
                    word.setTranscription(transcription);
                    word.setPartOfSpeech(partOfSpeech);
                    word.setKnowledge(knowledge);
                    word.setWordId(wordId);
                    words.add(word);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error with query");
            e.printStackTrace();
        }

        loginWordsMap.put(login, words);
        servletContext.setAttribute("loginWordsMap", loginWordsMap);

        request.setAttribute("words", words);
        request.getRequestDispatcher("/dictionary.jsp").forward(request, response);

    }
}
