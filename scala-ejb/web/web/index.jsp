<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Scala EJB Service</title>
    <script type="text/javascript">
        function focusForm() {
            document.getElementById('inputForm').arg.select();
            document.getElementById('inputForm').arg.focus();
        }
    </script>
    <style type="text/css">
        * {
            font-family: 'Arial', sans-serif;
        }
    </style>
</head>
<body onload="focusForm();">
<h2>Scala EJB</h2>
<hr>
<form id="inputForm" action="index.jsp">
    Your name: <input type="text" name="arg" value="${param.arg}">
    <button type="submit">Generate Message!</button>
</form>
<hr>
<h3>Message from Server:</h3>

<div>
    <c:choose>
        <c:when test="${!empty param.arg}">
            <pre style="color:blue;"><jsp:include page="TestService"/></pre>
        </c:when>
        <c:otherwise>
            <pre style="color:red;">Please enter your name!</pre>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
