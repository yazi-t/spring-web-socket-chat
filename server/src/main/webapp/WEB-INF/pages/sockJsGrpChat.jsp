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
            <a class="navbar-brand" href="#">Spring Websocket, Stomp Js: Group chat</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
        </ul>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-6">

            <div>
                Nickname: <input type="text" id="from" class="form-control" placeholder="Choose a nickname"/>
            </div>


            <div id="conversationDiv">
                <p id="response"></p>

                <div class="row">
                    <div class="col-md-9">
                        <input type="text" id="text" class="form-control" placeholder="Write a message..."/>
                    </div>
                    <div class="col-md-3">
                        <button id="sendMessage" class="btn" onclick="sendMessage();">Send</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<script src="<c:url value="/webjars/sockjs-client/0.3.4/sockjs.js" />" type="text/javascript"></script>
<script src="<c:url value="/js/stomp.js"/>" type="text/javascript"></script>
<script type="text/javascript">
    var stompClient = null;

    document.addEventListener("DOMContentLoaded", function () {
        connect();
    });

    function connect() {
        var socket = new SockJS('<c:url value="/grp-chat"/>');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/messages', function (messageOutput) {
                showMessageOutput(JSON.parse(messageOutput.body));
            });
        }, function (err) {
            alert('error' + err);
        });
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    function sendMessage() {
        var from = document.getElementById('from').value;
        var text = document.getElementById('text').value;
        console.log("Sending... " + JSON.stringify({'from': from, 'text': text}));
        stompClient.send("/app/grp-chat", {}, JSON.stringify({'from': from, 'text': text}));
    }

    function showMessageOutput(messageOutput) {
        console.log("Received... " + JSON.stringify(messageOutput));
        document.getElementById('response').innerHTML = document.getElementById('response').innerHTML + createTextNode(messageOutput);
    }

    function createTextNode(messageObj) {
        return '<div class= "row chat-message">' +
                '<div class="col-md-4">' +
                messageObj.from +
                '<br><small>' + messageObj.time + '</small>' +
                '</div>' +
                '<div class="col-md-8"><b>' +
                messageObj.message +
                '</b></div>' +
                '</div>';
    }
</script>
</body>
</html>
