package com.dmitr.romashov.servlets;

import com.dmitr.romashov.Person;

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

@WebServlet(name = "CheckCookieServlet", urlPatterns = "/checkCookies")
public class CheckCookieServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int personId = (int)request.getAttribute("personId");
        String hashPass = (String) request.getAttribute("hashPass");
        String path = (String) request.getAttribute("path");
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession httpSession = request.getSession();
        String login = "";

        try (PreparedStatement selectIdAndPassStatement = connection.prepareStatement("SELECT id, login, passwordhash from person WHERE id = ? and passwordhash = ?")){
            selectIdAndPassStatement.setInt(1, personId);
            selectIdAndPassStatement.setString(2, hashPass);
            try(ResultSet resultSet = selectIdAndPassStatement.executeQuery()) {
                if (resultSet.next()){
                    login = resultSet.getString("login");
                    httpSession.setAttribute("isAuthorized", "yes");
                    httpSession.setAttribute("personId", personId);
                    Person person = new Person(login, personId);
                    httpSession.setAttribute("person", person);
                    request.getRequestDispatcher(path).forward(request, response);
                }
                else {
                    httpSession.setAttribute("isAuthorized", "no");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int t = 3;

    }
}
