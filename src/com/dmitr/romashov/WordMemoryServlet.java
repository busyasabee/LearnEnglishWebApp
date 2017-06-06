package com.dmitr.romashov;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Дмитрий on 07.05.2017.
 */
@WebServlet("/wordMemory")
public class WordMemoryServlet extends HttpServlet {
    private static int wordMemory = 0;
    public static final String HTML_START="<html><body>";
    public static final String HTML_END="</body></html>";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String answer = request.getParameter("answer");
        if(answer.equals("a2")){
            wordMemory+=1;
        }
        HttpSession session = request.getSession();
        session.setAttribute("wordMemory", wordMemory);
//        PrintWriter out = response.getWriter();
//        out.println(HTML_START + "<h2>" + answer + "</h2>" +HTML_END);
        response.sendRedirect("/words.jsp");

    }
}
