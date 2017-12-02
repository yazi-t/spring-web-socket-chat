<%--
  Appication : Spring websocket chat example
  author : Yasitha Thilakaratne
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
            <a class="navbar-brand" href="#">Spring Websocket, Stomp Js: User-to-user chat</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="javascript:void(0)" onclick="document.getElementById('logout').submit();">
                <span class="glyphicon glyphicon-log-out"></span> Logout</a>
            </li>
            <form id="logout" action="<c:url value="/logout"/>" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </ul>
    </div>
</nav>

<div class="container">
    <p>You are logged in as ${username}</p>
    <br/>

    <div class="row">
        <div class="col-md-6">

            <div id="conversationDiv">

                <div id="response"></div>

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

        <div class="col-md-6">
            <h4>Active users</h4>

            <ul id="active-users" class="list-group">
                <c:choose>
                    <c:when test="${fn:length(onlineUsers) == 0}">
                        <p><i>No active users found.</i></p>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted">click on user to begin chat</p>
                    </c:otherwise>
                </c:choose>
                <c:forEach var="username" items="${onlineUsers}" varStatus="i">
                    <li class="list-group-item"><a class="active-user" href="javascript:void(0)"
                                                   onclick="setSelectedUser('${username}')">${username}</a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<script src="<c:url value="/webjars/sockjs-client/0.3.4/sockjs.js" />" type="text/javascript"></script>
<script src="<c:url value="/js/stomp.js"/>" type="text/javascript"></script>
<script type="text/javascript">
    var stompClient = null;
    var selectedUsername = null;
    var from = '${username}';

    document.addEventListener("DOMContentLoaded", function () {
        connect();
    });

    function connect() {
        var socket = new SockJS('<c:url value="/chat"/>');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/queue/messages', function (messageOutput) {
                showMessageOutput(JSON.parse(messageOutput.body));
            });

            stompClient.subscribe('/topic/active', function (messageOutput) {
                updateUsers(JSON.parse(messageOutput.body));
            });
        }, function (err) {
            console.log('Connection lost: ' + err);
            document.getElementById('logout').submit();
        });
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    function sendMessage() {
        var text = document.getElementById('text').value;
        document.getElementById('text').value = '';
        if (selectedUsername == null) {
            alert('Please select a user.');
            return;
        }
        stompClient.send("/app/chat", {},
                JSON.stringify({'from': from, 'text': text, 'recipient': selectedUsername}));
    }

    function setSelectedUser(username) {
        selectedUsername = username;
        document.getElementById('response').innerHTML = '';
    }

    function showMessageOutput(messageOutput) {
        console.log(JSON.stringify(messageOutput));
        if (messageOutput.from != from) {
            selectedUsername = messageOutput.from;
        }
        document.getElementById('response').innerHTML = document.getElementById('response').innerHTML + createTextNode(messageOutput);
    }

    function createTextNode(messageObj) {
        return '<div class= "row chat-message ' + (messageObj.myMsg ? ' my-message' : '') + '">' +
                '<div class="col-md-4">' +
                messageObj.from +
                '<br><small>' + messageObj.time + '</small>' +
                '</div>' +
                '<div class="col-md-8"><b>' +
                messageObj.message +
                '</b></div>' +
                '</div>';
    }

    function updateUsers(userList) {
        console.log('List of users : ' + userList);
        var activeUserUL = document.getElementById('active-users');

        var index;
        activeUserUL.innerHTML = '';
        if (userList.length == 0) {
            activeUserUL.innerHTML = '<p><i>No active users found.</i></p>';
            return;
        }
        activeUserUL.innerHTML = '<p class="text-muted">click on user to begin chat</p>';

        for (index = 0; index < userList.length; ++index) {
            if (userList[index] != from) {
                activeUserUL.innerHTML = activeUserUL.innerHTML + createUserNode(userList[index], index);
            }
        }
    }

    function createUserNode(username, index) {
        return '<li class="list-group-item">' +
                '<a class="active-user" href="javascript:void(0)" onclick="setSelectedUser(\'' + username + '\')">' + username + '</a>' +
                '</li>';
    }
</script>
</body>
</html>

