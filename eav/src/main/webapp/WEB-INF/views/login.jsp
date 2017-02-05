<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 19.12.2016
  Time: 17:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resources/css/main.css" rel="stylesheet">
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>

<c:if test="${not empty error}">
    ${error}
</c:if>

<div class="container  ">
    <div class="row" >
        <div class="well logo  col-lg-3 col-lg-offset-5 ">
            <legend class="logo">Вход</legend>
            <form action="<%=request.getContextPath()%>/j_spring_security_check" method='POST'>
                <fieldset class="account-info">
                    <label>
                        <input type="text" name="username" placeholder="Введите имя">
                    </label>
                    <label>
                        <input type="password" name="password" placeholder="Введите пароль">
                    </label>
                </fieldset>
                <input id="remember_me"
                       name="_spring_security_remember_me"
                       type="checkbox"/> <!-- Флажок "запомнить" -->
                <label for="remember_me" class="inline">Запомнить</label>
                <fieldset class="account-action">
                    <input class="btn btn-sm" type="submit" name="submit" value="Войти">

                    <a href="addUser">Регистрация</a>
                </fieldset>
            </form>

        </div>

    </div>
</div>

</body>
</html>