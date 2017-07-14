package com.dmitr.romashov.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.dmitr.romashov.Person;

@WebServlet(name = "StartServlet", urlPatterns = "/start")
public class StartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String personLogin = "";
        int personId = 0;
        HttpSession session = request.getSession();
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        session.setAttribute("isLogged", true);


        if(cookies == null){
            response.sendRedirect("login.jsp");
        }
        else {

            boolean isRedirect = true;

            for (Cookie cookie: cookies){
                if (cookie.getName().equals("login")){
                    //cookie.setValue("Dmitriy");
                    isRedirect = false;
                    personLogin = cookie.getValue();
                    try(PreparedStatement getPersonIdStatement = connection.prepareStatement("SELECT person.id from person " +
                            "WHERE login = ? ")) {

                        getPersonIdStatement.setString(1, personLogin);
                        try(ResultSet resultSet = getPersonIdStatement.executeQuery()) {
                            while ( resultSet.next()){
                                personId = resultSet.getInt(1);
                            }

                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        response.sendRedirect("login.jsp");
                        return;
                    }
                    session.setAttribute("login", personLogin);
                    session.setAttribute("personId", personId);
                    Person person = new Person(personLogin, personId);
                    session.setAttribute("person", person);
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    //request.setAttribute("login", personLogin); //не показывает в dictionary jsp
                }
            }

            if (isRedirect){
                response.sendRedirect("login.jsp");
            }
        }

    }
}
