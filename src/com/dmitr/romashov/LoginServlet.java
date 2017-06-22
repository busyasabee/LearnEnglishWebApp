package com.dmitr.romashov;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Дмитрий on 02.06.2017.
 */
@WebServlet(name = "loginServlet", urlPatterns = "/loginServlet")
public class LoginServlet extends HttpServlet {
    int cookiesTime = 2_592_000; // month

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        // подключиться к бд, проверить, есть ли такой пользователь
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        String passwordFromDb = "";
        String saltFromDb = "";
        String hashPass = "";
        boolean isLogged = true;
        int personId = 0;


        try (PreparedStatement selectUserStatement = connection.prepareStatement("SELECT id, passwordhash, salt FROM person WHERE login = ?  ");) {
            selectUserStatement.setString(1, login);
            try (ResultSet resultSet = selectUserStatement.executeQuery();) {
                resultSet.next();
                personId = resultSet.getInt("id");
                passwordFromDb = resultSet.getString("passwordhash");
                saltFromDb = resultSet.getString("salt");

            }

        } catch (SQLException e) {
            e.printStackTrace();
            //isLogged = false;
            //session.setAttribute("isLogged", isLogged);
            request.setAttribute("errorHappen", "yes");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;

        }

        hashPass = DigestUtils.sha1Hex(password + saltFromDb);

        if (hashPass.equals(passwordFromDb)){
            Person person = new Person(login, personId);
            Cookie cookieLogin = new Cookie("login", login);
            cookieLogin.setMaxAge(cookiesTime);
            Cookie cookiePassword = new Cookie("password", password);
            cookiePassword.setMaxAge(cookiesTime);

            response.addCookie(cookieLogin);
            response.addCookie(cookiePassword);
            //session.setAttribute("isLogged", isLogged);
            session.setAttribute("login", login);
            session.setAttribute("person", person);
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        }
        else {
            isLogged = false;
            //session.setAttribute("isLogged", isLogged);
            request.setAttribute("errorHappen", "yes");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
