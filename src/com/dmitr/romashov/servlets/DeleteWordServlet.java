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
 * Created by Дмитрий on 02.07.2017.
 */
@WebServlet(name = "DeleteWordServlet", urlPatterns = "/deleteWord")
public class DeleteWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int wordId = Integer.parseInt(request.getParameter("id"));
        ServletContext servletContext = getServletContext();
        Map<String, List<Word>> loginWordsMap = (Map<String, List<Word>>) servletContext.getAttribute("loginWordsMap");
        HttpSession session = request.getSession();
        String login = (String) session.getAttribute("login");
        List<Word> words = loginWordsMap.get(login);
        words.removeIf(word -> word.getWordId() == wordId);
        Person person = (Person) session.getAttribute("person");
        int personId = person.getPerson_id();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");

        try (PreparedStatement deletePersonWordStatement = connection.prepareStatement("DELETE FROM person_word " +
                "WHERE person_id = ? and word_id = ?")){
            deletePersonWordStatement.setInt(1, personId);
            deletePersonWordStatement.setInt(2, wordId);
            deletePersonWordStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement selectRecordsWithWordStatement = connection.prepareStatement("SELECT person_id, word_id " +
                "FROM person_word WHERE word_id = ?")){
            selectRecordsWithWordStatement.setInt(1, wordId);
            try (ResultSet resultSet = selectRecordsWithWordStatement.executeQuery()){
                if (!resultSet.next()){
                    try (PreparedStatement deleteWordStatement = connection.prepareStatement("DELETE FROM word WHERE " +
                            "word.id = ?")){
                        deleteWordStatement.setInt(1, wordId);
                        deleteWordStatement.executeUpdate();

                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("words", words);
        request.getRequestDispatcher("/dictionary.jsp").forward(request, response);
    }
}
