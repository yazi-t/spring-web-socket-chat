<%--
  Appication : Spring websocket chat example
  author : Yasitha Thilakaratne
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.5/css/bootstrap.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
</head>
<body>

<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Notification sender</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
        </ul>
    </div>
</nav>

<div class="container">

    <form action="<c:url value="/sse/add-event" />" class="form-inline">
            <div class="form-group">
                <label for="data">Notification:</label>
                <input type="text" class="form-control" name="data" id="data">
            </div>
            <button type="submit" class="btn btn-default">Send</button>

    </form>

</div>

</body>
</html>
