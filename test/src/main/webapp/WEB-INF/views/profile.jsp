<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 07.01.2017
  Time: 14:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Profile</title>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">

</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<div class="navbar ">
    <div class="navbar-inner ">
        <ul class="nav nav-pills">
            <li class="active"><a href="/mainLogin">Главная</a></li>

            <li class="active pull-right"><a href="/logout">Выход</a></li>
            <li class="pull-right"><a href="/changeProfile">Изменить профиль</a></li>

        </ul>
    </div>
</div>
<div class="container base">
    <div class="row">
        <div class="container col-lg-4 pull-left">
            <div class="container">
                <img src=".." class="img-polaroid ">
                <ul>
                    <li>Никнейм: ${user.nickname}</li>
                    <li>Имя: ${user.name}</li>
                    <li>Фамилия: ${user.surname}</li>
                    <li>Возраст: ${user.age}</li>
                    <li>Почта: ${user.email}</li>
                    <li>Город: ${user.city}</li>
                    <li>Пол: ${user.sex}</li>
                    <li>Телефон: ${user.phone}</li>
                </ul>
            </div>
            <div class="container clear">

            </div>

        </div>
        <div class="container col-lg-8">
            <div class="container about col-lg-10">
                <p>Очень очень коротко о себе: ${user.info}</p>
            </div>


        </div>
    </div>
</div>
<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; Netcracker</small>
</footer>
</body>
