package com.dmitr.romashov;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Created by Дмитрий on 02.06.2017.
 */
@WebServlet(name = "loginServlet", urlPatterns = "/loginServlet")
public class LoginServlet extends HttpServlet {
    int cookiesTime = 2_592_000; // month

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String login = request.getParameter("login");
        //login = "Dmitriy";
        String password = request.getParameter("password");
        // подключиться к бд, проверить, есть ли такой пользователь

        Cookie cookieLogin = new Cookie("login", login);
        cookieLogin.setMaxAge(cookiesTime);
        Cookie cookiePassword = new Cookie("password", password);
        cookiePassword.setMaxAge(cookiesTime);


        response.addCookie(cookieLogin);
        response.addCookie(cookiePassword);
        response.sendRedirect("/index.jsp");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
