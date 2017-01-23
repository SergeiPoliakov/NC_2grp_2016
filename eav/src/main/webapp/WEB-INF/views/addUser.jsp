<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 18.01.2017
  Time: 15:28
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
    <title>Registration Page</title>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">

</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<div class="container">


    <div class="page-header">
        <h1>NetCrackerTest <small>Регистрация</small></h1>
    </div>
    <form name="registration" action="/addUser" method="post">
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="name">Введите Имя</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Имя">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="surname">Введите фамилию</label>
            <input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="middle_name">Введите отчество</label>
            <input type="text" class="form-control " name="middle_name" id="middle_name" placeholder="Отчество">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="nickname">Введите никнейм</label>
            <input type="text" class="form-control " name="nickname" id="nickname" placeholder="Никнейм">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="ageUser">Введите дату рождения</label>
            <input type="date" class="form-control" name="ageUser" id="ageUser" placeholder="Возраст">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="email">Введите email</label>
            <input type="email" class="form-control" name="email" id="email" placeholder="Email">
        </div>

        <div class="form-group  col-lg-offset-4 col-lg-5">
            <label for="password">Введите пароль</label>
            <input type="password" class="form-control" name="password" id="password" placeholder="Пароль">
        </div>

        <button type="submit"  class="btn-lg btn-success col-lg-5 col-lg-offset-4">Зарегистрироваться</button>
    </form>
</div>

<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>
</body>
</html>