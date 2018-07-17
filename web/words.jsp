<%@ page import="java.util.List" %>
<%@ page import="com.dmitr.romashov.Word" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Дмитрий
  Date: 07.05.2017
  Time: 11:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>Words</title>
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/blog.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
<%!
    private static int rightNumberCount;
    private static String rightAnswer = "вернуть";
    int taskNumber, wordNumber;
    List<Word> words;
    String englishName;
    String transcription = "";
    String partOfSpeech = "";
%>
<%
    HttpSession httpSession = request.getSession();
    taskNumber = (int) httpSession.getAttribute("taskNumber");
    wordNumber = (int) httpSession.getAttribute("wordNumber");
    words = (List<Word>) httpSession.getAttribute("trainingWords");
    Word trainingWord = words.get(wordNumber);
    String isRight = (String) request.getAttribute("isRight");
%>
<%request.setCharacterEncoding("UTF-8");%>
<%@include file="header.jsp" %>
<div class="container" style="margin-left: 25%">

    <div class="jumbotron" style="background: white; margin-bottom: 50px">

    </div>

    <div class="row">

        <div class="col-sm-8 blog-main">

            <div>
                <%
                    switch (taskNumber) {
                        case 0:
                            // слово на английском, вводим на русском
                            englishName = trainingWord.getEnglishName();
                            if (trainingWord.getPartOfSpeech() != null) {
                                partOfSpeech = trainingWord.getPartOfSpeech();
                            }
                            if (trainingWord.getTranscription() != null) {
                                transcription = trainingWord.getTranscription();
                            }
                            String russian = trainingWord.getRussianName();
                %>
                <h2 style="margin-bottom: 20px;font-size: 60px;"><%= trainingWord.getEnglishName()%>?</h2>
                <p><%= partOfSpeech%>
                </p>
                <p><%= transcription%>
                </p>
                <p>Type the Russian for the English above and press Check then Next</p>
                <form action="learnwords" method="post">
                    <%
                        if (isRight != null) {
                    %>
                    <p><input type="text" name="answer" value=<%= russian %>></p>
                    <%
                    } else {
                    %>
                    <p><input type="text" name="answer"></p>
                    <%
                        }
                        ;
                    %>
                    <input type="submit" name="check" value="Check" class="btn btn-lg btn-primary">
                    <input type="submit" name="next" value="Next" class="btn btn-lg btn-primary">
                </form>

                <p>Word memory <%=trainingWord.getKnowledge()%>
                </p>
                <%
                    if (isRight != null && isRight.equals("yes")) {
                %>
                <p style="color: green">Right answer</p>
                <%
                    }
                    if (isRight != null && isRight.equals("no")) {
                %>
                <p style="color: red">Error</p>
                <%
                    }
                %>


                <%
                        break;
                    case 1:
                        String russianName = trainingWord.getRussianName();
                        if (trainingWord.getPartOfSpeech() != null) {
                            partOfSpeech = trainingWord.getPartOfSpeech();
                        }
                        String english = trainingWord.getEnglishName();

                %>
                <h2 style="margin-bottom: 20px;font-size: 60px;"><%= russianName%>?</h2>
                <p><%= partOfSpeech%>
                </p>
                <p>Type the English for the Russian above and press Check then Next</p>
                <form action="learnwords" method="post">
                    <%
                        if (isRight != null) {
                    %>
                    <p><input type="text" name="answer" value=<%= english %>></p>
                    <%
                    } else {
                    %>
                    <p><input type="text" name="answer"></p>
                    <%
                        }
                        ;
                    %>
                    <input type="submit" name="check" value="Check" class="btn btn-lg btn-primary">
                    <input type="submit" name="next" value="Next" class="btn btn-lg btn-primary">
                </form>
                <p>Word memory <%=trainingWord.getKnowledge()%>
                </p>
                <%
                    if (isRight != null && isRight.equals("yes")) {
                %>
                <p style="color: green">Right answer</p>
                <%
                    }
                    if (isRight != null && isRight.equals("no")) {
                %>
                <p style="color: red">Error</p>
                <%
                    }
                %>
                <%
                            break;
                    }
                %>

            </div>

        </div>
    </div>

</div>

</body>
</html>
