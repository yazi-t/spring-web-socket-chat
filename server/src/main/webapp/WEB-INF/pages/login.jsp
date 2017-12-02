<%--
  Appication : Spring websocket chat example
  author : Yasitha Thilakaratne
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.5/css/bootstrap.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>

</head>
<body>

<c:url value="/process_login" var="loginUrl"/>

<div class="container login-box">

    <form action="${loginUrl}" method="post">
        <c:if test="${param.error != null}">
            <p class="login-error">
                Invalid username and password.
            </p>
        </c:if>
        <c:if test="${param.logout != null}">
            <p class="login-error">
                You have been logged out.
            </p>
        </c:if>
        <p>
            <label for="username">Username</label>
            <input type="text" id="username" name="username" class="form-control"/>
        </p>

        <p>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control"/>
        </p>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" class="btn">Log in</button>
        <a class="pull-right" href="<c:url value="/" />">Back</a>
    </form>

</div>

</body>
</html>
