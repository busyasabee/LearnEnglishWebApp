
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
    <div style="font-size: large;padding:10px;padding-left: 30px; background-color: white; width: 70%">
        <b>Trained words</b>
    </div>
    <div class="jumbotron"
         style="padding:10px;padding-left: 30px;background-color: white; width: 70%">

        <table width="100%">
            <tr>
                <th>English</th>
                <th>Russian</th>
                <th>Pronunciation</th>
                <th>Part of Speech</th>
                <th>Word memory</th>
            </tr>
            <c:forEach var="word" items="${words}">
                <tr>
                    <td><c:out value="${word.englishName}"/></td>
                    <td><c:out value="${word.russianName}"/></td>
                    <td><c:out value="${word.transcription}"/></td>
                    <td><c:out value="${word.partOfSpeech}"/></td>
                    <td><c:out value="${word.knowledge}"/></td>
                </tr>

            </c:forEach>

        </table>

    </div>
    <div style="font-size: medium;padding-left: 30px;background-color: white; width: 70%">
        <form action="tasks.jsp" method="get">
            <input type="submit" value="Exit" class="btn btn-lg btn-primary"/>
        </form>
    </div>

</div>


</body>
</html>
