<%--
  Appication : Spring websocket chat example
  author : Yasitha Thilakaratne
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Chat WebSocket</title>

    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.5/css/bootstrap.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
</head>
<body>

<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Notification Receiver</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
        </ul>
    </div>
</nav>

<div class="container">

    <div id="content" class="jumbotron">
    </div>

</div>

<script type="text/javascript">

    var source = new EventSource('<c:url value="/sse/subscribe" />');
    source.addEventListener('group1', function (event) {
        document.getElementById('content').innerHTML = document.getElementById('content').innerHTML +
                '<div>' + event.data + '</div></br>';
    });
</script>
</body>
</html>
