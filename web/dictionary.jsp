<%--
  Created by IntelliJ IDEA.
  User: Дмитрий
  Date: 09.06.2017
  Time: 21:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>LearnEnglishApp - Start page</title>
    <link href="css/bootstrap.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<jsp:include page="header.jsp"></jsp:include>
<%request.setCharacterEncoding("UTF-8");%>
<div class="container">
    <div class="jumbotron"
         style="padding:10px;padding-left: 30px;border: 4px solid grey; background-color: white; width: 70%">
        <table width="100%">
            <tr>
                <th>English</th>
                <th>Russian</th>
                <th>Pronunciation</th>
                <th>Part of Speech</th>
            </tr>
            <c:forEach var="word" items="${words}">
                <tr>
                    <td><c:out value="${word.englishName}"/></td>
                    <td><c:out value="${word.russianName}"/></td>
                    <td><c:out value="${word.transcription}"/></td>
                    <td><c:out value="${word.partOfSpeech}"/></td>

                </tr>

            </c:forEach>

        </table>

    </div>
    <div style="font-size: medium;padding-left: 30px;background-color: white; width: 70%">
        <b> Add word:</b>
        <br><br>
        <form action="addWord" method="post">
            English: <br><input type="text" name="englishName"> <br><br>
            Russian: <br><input type="text" name="russianName"> <br><br>
            Pronunciation: <br><input type="text" name="transcription"> <br><br>
            Part of Speech: <br><input type="text" name="partOfSpeech"> <br><br>
            <input type="submit" value="Add" class="btn btn-lg btn-primary"/>

        </form>
        <c:if test="${wordAdded==false}">
            <p><b>Error have happened! Please, try again</b></p>
        </c:if>
    </div>

</div>


</body>
</html>
