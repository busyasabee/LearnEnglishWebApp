package com.dmitr.romashov;

import org.apache.commons.codec.digest.DigestUtils;

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
@WebServlet("/learnwords")
public class LearnWordsServlet extends HttpServlet {
    public static final String HTML_START = "<html><body>";
    public static final String HTML_END = "</body></html>";
    private static int wordMemory = 0;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String answer = request.getParameter("answer");
        if(answer.equals("a2")){
            wordMemory+=1;
        }
        HttpSession session = request.getSession();
        session.setAttribute("wordMemory", wordMemory);
        response.sendRedirect("/words.jsp");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println(HTML_START + "<h2>Deployed?</h2>" + HTML_END);
        String hash = DigestUtils.sha1Hex("key");
        String[] variants = new String[]{"вернуть", "занять", "получить", "забрать"};
        //response.setContentType("text/html;charset=UTF-8");
        request.getSession().setAttribute("englishWord", "Retrieve");
        request.getSession().setAttribute("firstVariant", "ааааааааааааааа");
        request.getSession().setAttribute("secondVariant", "ббб");
        request.getSession().setAttribute("thirdVariant", "вввв");
        request.getSession().setAttribute("fourthVariant", "дддд");
        response.sendRedirect("/words.jsp");
        //request.getRequestDispatcher("/words.jsp").forward(request, response); // так почему-то не работали русские символы


    }
}
