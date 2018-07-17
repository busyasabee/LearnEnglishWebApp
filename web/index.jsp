<%--
  Created by IntelliJ IDEA.
  User: Дмитрий
  Date: 06.05.2017
  Time: 9:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>LearnEnglishApp - Start page</title>
    <link href="css/bootstrap.css" rel="stylesheet">
</head>
<body style="background: white">
<%!
    String personLogin;
    int counter = 0;
%>
<%
    Cookie[] cookies = request.getCookies();

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
                session.setAttribute("login", personLogin);
            }
        }

        if (isRedirect){
            response.sendRedirect("login.jsp");
        }
    }
%>
<%@include file = "header.jsp" %>

<div class="container" >
    <div class="jumbotron" style="padding:10px;padding-left: 30px; border: 4px solid grey;background-color: white; width: 70%">
        <h1>Dictionary</h1>
        <p>View your  words</p>
        <form action="dict" method="GET">
            <input type="submit" value="Go &raquo" class="btn btn-lg btn-primary"/>
        </form>
    </div>
    <div class="jumbotron" style="padding:10px;padding-left: 30px; border: 4px solid grey;background-color: white; width: 70%">
        <h1>Words</h1>
        <p>Begin training words</p>
        <form action="learnwords" method="GET">
            <input type="submit" value="Start &raquo" class="btn btn-lg btn-primary"/>
        </form>
    </div>

</div>
</body>
</html>
