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
    <title>Main Page</title>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>

<div class="container  ">
    <div class="row" >
        <div class="well logo  col-lg-3 col-lg-offset-5 ">
            <legend class="logo">Вход</legend>
            <form action="/login" method="post">
                <fieldset class="account-info">
                    <label>
                        <input type="text" name="email" placeholder="Введите имя">
                    </label>
                    <label>
                        <input type="password" name="password"placeholder="Введите пароль">
                    </label>
                </fieldset>
                <label>
                    <input type="checkbox" name="remember"> Запомнить
                </label>
                <fieldset class="account-action">
                    <input class="btn btn-sm" type="submit" name="submit" value="Войти">

                    <a href="registration.html">Регистрация</a>
                </fieldset>
            </form>

        </div>

    </div>
</div>

</body>
</html>