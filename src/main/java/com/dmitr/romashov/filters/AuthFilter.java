package com.dmitr.romashov.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/learnwords", "/dict"})
public class AuthFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("doFilter is execute");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        String uriPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        System.out.println("context path: " + httpRequest.getContextPath() + " path: " + uriPath);

        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        int personId = 0;
        String hashPass = "";
        String isAuthorized = (String) session.getAttribute("isAuthorized");

        if (isAuthorized != null && isAuthorized.equals("yes")) {
            httpRequest.getRequestDispatcher(uriPath).forward(httpRequest, httpResponse);
        } else if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("id")) {
                    personId = Integer.parseInt(cookie.getValue());
                }
                if (cookie.getName().equals("password")) {
                    hashPass = cookie.getValue();
                }
            }

            httpRequest.setAttribute("personId", personId);
            httpRequest.setAttribute("hashPass", hashPass);
            httpRequest.setAttribute("path", uriPath);
            httpRequest.getRequestDispatcher("/checkCookies").forward(httpRequest, httpResponse);
        } else {
            httpRequest.getRequestDispatcher("login.jsp").forward(httpRequest, httpResponse);

        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
