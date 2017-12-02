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
            <a class="navbar-brand" href="#">Plain Web socket</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
        </ul>
    </div>
</nav>

<div class="container">

    <div class="row">
        <div class="col-md-6">

            <div id="content">
            </div>

            <div class="row">
                <div class="col-md-9">
                    <input type="text" id="message" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <button id="send" class="btn" onclick="send()">Send</button>
                </div>
            </div>

        </div>
    </div>

</div>

<script>

    var ws = new WebSocket('ws://localhost:8080/sample-chat/web-socket');

    ws.onopen = function () {
        document.getElementById('content').innerHTML = document.getElementById('content').innerHTML + '<div>Connected to server...</div><br>';
    };

    ws.onmessage = function (data) {
        document.getElementById('content').innerHTML = document.getElementById('content').innerHTML + createTextNode(data.data);
    };

    function send() {
        var message = document.getElementById('message').value;
        ws.send(message);
    }

    function createTextNode(msg) {
        return '<div class= "row spring.websocket.chat-message">' +
                '<div class="col-md-9">' +
                msg +
                '</div>' +
                '</div>';
    }

</script>
</body>
</html>
