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
    <form name="registration" action="/registration" method="post">
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="name">Введите Имя</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Имя">
        </div>
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="surname">Введите фамилию</label>
            <input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия">
        </div>
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="age">Введите ваш возраст</label>
            <input type="text" class="form-control" name="age" id="age" placeholder="Возраст">
        </div>
        <div class="form-group  col-lg-offset-4 col-lg-5">
            <label for="city">Введите ваш город</label>
            <input type="text" class="form-control"  name="city" id="city" placeholder="Город">
        </div>
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label>Выберите ваш пол</label>
            <div class="radio">
                <label>
                    <input type="radio" name="sex" id="Gender1" value="М" checked>
                    Мужской
                </label>
            </div>
            <div class="radio">
                <label>
                    <input type="radio" name="sex" id="Gender2" value="Ж" checked>
                    Женский
                </label>
            </div>
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
