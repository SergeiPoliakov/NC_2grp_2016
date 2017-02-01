<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 31.01.2017
  Time: 16:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Event Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resources/css/main.css" rel="stylesheet">
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>

<div class="container">
    <div class="page-header">
        <h1>Netcracker <small>Редактирование события</small></h1>
    </div>

        <form name="edit" action="/changeEvent/${event.id}" method="post">

            <div class="form-group col-lg-offset-4 col-lg-5">
                <label for="InputName1">Введите название</label>
                <input type="text" class="form-control " name="name" id="InputName1" value=${event.name}>
            </div>

            <div class="form-group col-lg-offset-4 col-lg-5">
                <label for="InputDateBegin1">Введите дату начала</label>
                <input type="date" class="form-control" name="date_begin" id="InputDateBegin1"
                       value=${event.date_begin}>
            </div>

            <div class="form-group col-lg-offset-4 col-lg-5">
                <label for="InputDateEnd1">Введите дату окончания</label>
                <input type="date" class="form-control" name="date_end" id="InputDateEnd1" value=${event.date_end}>
            </div>

            <div class="form-group col-lg-offset-4 col-lg-5">
                <label for="InputPriority1">Введите приоритет</label>
                <input type="text" class="form-control" name="priority" id="InputPriority1" value=${event.priority}>
            </div>

            <div class="form-group col-lg-offset-4 col-lg-5">
                <label for="TextArea1">Введите информацию о событии</label>
                <textarea rows="3" class="form-control" name="info" id="TextArea1">${event.info}</textarea>
            </div>


            <button type="submit" class="btn-lg btn-success col-lg-5 col-lg-offset-4">Сохранить</button>

        </form>
    </div>


<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>
</body>
</html>
