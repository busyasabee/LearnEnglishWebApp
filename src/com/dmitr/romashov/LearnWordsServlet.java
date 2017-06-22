package com.dmitr.romashov;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

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

        int taskNumber = 1;
        int wordNumber = 1;
        List<Word> words = new ArrayList<>();
        List<Word> allPersonWords = new ArrayList<>();
        Map<String, List<Word>> loginWordsMap = new HashMap<>();
        ServletContext servletContext = getServletContext();
        Connection connection = (Connection) servletContext.getAttribute("dbConnection");
        HttpSession session = request.getSession();
        if (session.getAttribute("taskNumber")!= null){
            taskNumber = (int)session.getAttribute("taskNumber");
        }
        if (session.getAttribute("wordNumber")!= null){
            wordNumber = (int)session.getAttribute("taskNumber");
        }
        String login = (String)session.getAttribute("login");
        int personId = 0;

        if (login == null){
            //response.sendRedirect("/.jsp");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        if(servletContext.getAttribute("loginWordsMap") != null){
            loginWordsMap = (HashMap<String, List<Word>>)servletContext.getAttribute("loginWordsMap");
        }
        if(loginWordsMap.containsKey(login)){
            // надо сделать выбор пяти самых плохо изученных слов
            allPersonWords = loginWordsMap.get(login);
            Collections.sort(allPersonWords);
            request.setAttribute("words", loginWordsMap.get(login));
            request.setAttribute("wordNumber", wordNumber);
            request.setAttribute("taskNumber", taskNumber);
            request.getRequestDispatcher("/dictionary.jsp").forward(request, response);
            return;
        }
        else {
            // делаем sql запрос, получаем слова
        }
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
