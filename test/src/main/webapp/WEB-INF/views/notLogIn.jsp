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
<div class="container" style="width: 100%;" >
    <div class="navbar">
        <div class="navbar-inner ">
            <a class="navbar-brand" href="/">Netcracker</a>
            <ul class="nav nav-pills">
                <li class="active"><a href="/">Главная</a></li>
            </ul>
        </div>
    </div>
</div>
<div class="container ">
    <div class="alert alert-danger">
        <button type="button" class="close" data-dismiss="alert">?</button>
        <h4>Вы не ввели учетные данные!</h4>
        Войдите под своими учетными данными или зарегистрируйтесь.
    </div>
</div>


  <div class="container">
        <div class="row">
            <div class=" well notLogin col-lg-4 col-lg-offset-4 ">
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
    <footer class="navbar-static-bottom navbar-inverse">
        <small>&copy; Netcracker</small>
    </footer>
 </body>
</html>