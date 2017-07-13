package com.dmitr.romashov.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Дмитрий on 04.06.2017.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        Cookie cookieLogin = new Cookie("login", "");
        Cookie cookiePassword = new Cookie("password", "");
        HttpSession session = request.getSession();
        session.setAttribute("login", null);
        session.setAttribute("person", null);
        cookieLogin.setMaxAge(0);
        cookiePassword.setMaxAge(0);
        response.addCookie(cookieLogin);
        response.addCookie(cookiePassword);

        request.getRequestDispatcher("/login.jsp").forward(request, response);

    }
}
