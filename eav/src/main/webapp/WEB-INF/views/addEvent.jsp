<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 29.01.2017
  Time: 16:10
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
    <title>Add Event Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resources/css/main.css" rel="stylesheet">
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>


<div class="container">
    <div class="page-header">
        <h1>Netcracker <small>Создание события</small></h1>
    </div>

    <form name="creation" action="/addEvent" method="post">

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="name">Введите название</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Название">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="date_begin">Введите дату начала</label>
            <input type="date" class="form-control" name="date_begin" id="date_begin" placeholder="Начало">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="date_end">Введите дату окончания</label>
            <input type="date" class="form-control " name="date_end" id="date_end" placeholder="Окончание">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="priority">Введите приоритет</label>
            <input type="text" class="form-control " name="priority" id="priority" placeholder="Приоритет">
        </div>

        <div class="form-group col-lg-offset-4 col-lg-5">
            <label for="info">Введите информацию о событии</label>
            <input type="text" class="form-control" name="info" id="info" placeholder="Информация">
        </div>

        <button type="submit"  class="btn-lg btn-success col-lg-5 col-lg-offset-4">Создать</button>
    </form>
</div>


<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>

</body>
</html>
