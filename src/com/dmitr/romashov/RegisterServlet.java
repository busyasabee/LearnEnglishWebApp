package com.dmitr.romashov;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Created by Дмитрий on 05.06.2017.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String role = "";
        int roleId = 0;

        if (login.equals("admin") && password.equals("admin")) {
            role = "admin";
        } else {
            role = "user";
        }

        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");

        try (PreparedStatement findRoleIdStatement = connection.prepareStatement("SELECT id FROM role WHERE role.rolename = ?  ");) {
            findRoleIdStatement.setString(1, role);
            try (ResultSet resultSet = findRoleIdStatement.executeQuery();) {
                resultSet.next();
                roleId = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // найти хэш от пароля, сгенерировать соль

        StringBuilder salt = new StringBuilder();
        int saltLength = 6;

        for (int i = 0; i < saltLength; i++) {
            salt.append((char) (new Random().nextInt(74) + 48));
        }

        String saltStr = salt.toString();
        String hashPass = DigestUtils.sha1Hex(password + saltStr);


        try (PreparedStatement insertPersonStatement = connection.prepareStatement("INSERT INTO person (login, passwordhash, salt, role_id) VALUES (?, ?, ?, ?)  ");) {
            insertPersonStatement.setString(1, login);
            insertPersonStatement.setString(2, hashPass);
            insertPersonStatement.setString(3, saltStr);
            insertPersonStatement.setInt(4, roleId);
            insertPersonStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("/login.jsp");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
