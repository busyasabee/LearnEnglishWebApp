<%--
  Created by IntelliJ IDEA.
  User: Дмитрий
  Date: 06.06.2017
  Time: 19:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>LearnEnglishWebApp - Login</title>
    <link href="css/bootstrap.css" rel="stylesheet">
</head>
<body>
<%@include file = "headerLogin.jsp" %>
<div class="container" >
    <div class="jumbotron" style="background: white">

        <p><b style="font-size: xx-large ">Login or <a href="register.jsp">register</a></b></p>
        <br>
        <form action="/loginServlet" method="POST">
            <p><b style="font-size: x-large ">Login</b></p>
            Username: <br> <input type="text" name="login"> <br><br>
            Password:<br> <input type="password" name="password"> <br><br>
            <input type="submit" value="Login" class="btn btn-lg btn-primary"/>
            <%

                String errorHappen;
                errorHappen = (String) request.getAttribute("errorHappen");

                if ( errorHappen!= null && errorHappen.equals("yes")){
            %>
            <p></p>
            <p> Sorry, username or password error or maybe you are not registered!</p>
            <%
                }
            %>
        </form>
    </div>
</div>


</body>
</html>
