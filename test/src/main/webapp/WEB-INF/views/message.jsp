<%--
  Created by IntelliJ IDEA.
  User: Hroniko (Anatoly Bedarev)
  Date: 25.12.2016
  Time: 18:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
    <title>CreateMessagePage</title>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">
</head>

<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<div class="container">


    <div class="page-header">
        <h1>NetCrackerTest <small>Создание сообщения</small></h1>
    </div>
    <form name="registration" action="/message" method="post">
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="name">Введите заголовок</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Название">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="to_id">Введите ID получателя</label>
            <input type="text" class="form-control " name="to_id" id="to_id" placeholder="Название">
        </div>


        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="date_begin">Введите дату доставки</label>
            <input type="date" class="form-control " name="date_beginStr" id="date_begin" placeholder="Дата доставки">
        </div>


        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="body">Введите текст сообщения</label>
            <input type="text" class="form-control " name="body" id="body" placeholder="Сообщение">
        </div>


        <button type="submit"  class="btn-lg btn-success col-lg-5 col-lg-offset-4">Отправить сообщение</button>
    </form>
</div>

<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>
</body>
</html>