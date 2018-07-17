package com.dmitr.romashov.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");

        // найти хэш от пароля, сгенерировать соль

        StringBuilder salt = new StringBuilder();
        int saltLength = 6;

        for (int i = 0; i < saltLength; i++) {
            salt.append((char) (new Random().nextInt(74) + 48));
        }

        String saltStr = salt.toString();
        String hashPass = DigestUtils.sha1Hex(password + saltStr);

        try (PreparedStatement checkDublicateStatement = connection.prepareStatement("SELECT FROM person WHERE login = ?")){
            checkDublicateStatement.setString(1, login);
            try (ResultSet resultSet = checkDublicateStatement.executeQuery()){
                if (resultSet.next()){
                    String error = "Sorry, but the user with this login already exists";
                    request.setAttribute("error", error);
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try (PreparedStatement insertPersonStatement = connection.prepareStatement("INSERT INTO person (login, passwordhash, salt) VALUES (?, ?, ?)  ")) {
            insertPersonStatement.setString(1, login);
            insertPersonStatement.setString(2, hashPass);
            insertPersonStatement.setString(3, saltStr);
            insertPersonStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("/login.jsp");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
