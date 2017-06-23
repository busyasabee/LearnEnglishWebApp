package com.dmitr.romashov;

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
import java.util.List;

/**
 * Created by Дмитрий on 23.06.2017.
 */
@WebServlet(name = "CheckWordServlet", urlPatterns = "/check")
public class CheckWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        request.setCharacterEncoding("UTF-8");
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        int taskNumber = 0;
        int wordNumber = 0;
        List<Word> trainingWords = new ArrayList<>();
        int personId = 0;
        int wordId = 0;

        taskNumber = (int) session.getAttribute("taskNumber");
        wordNumber = (int) session.getAttribute("wordNumber");
        trainingWords = (List<Word>) session.getAttribute("words");
        Word trainingWord = trainingWords.get(wordNumber);
        String rightAnswer = "";
        String login = (String)session.getAttribute("login");

        switch (taskNumber){
            case 0:
                rightAnswer = trainingWord.getRussianName();
                break;
            case 1:
                rightAnswer = trainingWord.getEnglishName();
                break;
        }

        String answer = request.getParameter("answer");

        if (rightAnswer.equals(answer)) {
            trainingWord.setKnowledge(trainingWord.getKnowledge() + 1);
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
            wordId = trainingWord.getWordId();
            session.setAttribute("person", new Person(login,personId));
            try (PreparedStatement insertPersonWordStatement
                         = connection.prepareStatement("UPDATE person_word SET knowledge = ? WHERE person_id = ? and word_id = ?")) {

                insertPersonWordStatement.setInt(1, trainingWord.getKnowledge());
                insertPersonWordStatement.setInt(2, personId);
                insertPersonWordStatement.setInt(3, wordId);
                insertPersonWordStatement.executeUpdate();

            }
            catch (Exception e){
                e.printStackTrace();
            }
            request.setAttribute("isRight", "yes");

        }
        else {
            request.setAttribute("isRight", "no");
        }

        request.getRequestDispatcher("/words.jsp").forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
