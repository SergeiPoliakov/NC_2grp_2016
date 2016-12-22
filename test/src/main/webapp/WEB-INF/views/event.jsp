<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 19.12.2016
  Time: 23:54
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
    <title>CreateEvent Page</title>
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">

</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<div class="container">


    <div class="page-header">
        <h1>NetCrackerTest <small>Создание события</small></h1>
    </div>
    <form name="registration" action="/event" method="post">
        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="name">Введите название</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Название">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="date_begin">Введите дату начала</label>
            <input type="datetime-local" class="form-control " name="date_beginStr" id="date_begin" placeholder="Дата начала">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="date_end">Введите дату окончания</label>
            <input type="datetime-local" class="form-control " name="date_endStr" id="date_end" placeholder="Дата окончания">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="priority">Введите приоритет события</label>
            <input type="number" class="form-control " name="priority" id="priority" placeholder="Приоритет">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="info">Введите дополнительную информацию</label>
            <input type="text" class="form-control " name="info" id="info" placeholder="Дополнительная информация">
        </div>


        <button type="submit"  class="btn-lg btn-success col-lg-5 col-lg-offset-4">Создать событие</button>
    </form>
</div>

<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>
</body>
</html>
