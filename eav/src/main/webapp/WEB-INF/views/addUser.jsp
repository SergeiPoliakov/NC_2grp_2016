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

    <link href="/resources/css/common.css" rel="stylesheet">

    <title>Registration Page</title>

</head>
<body>

<%@include file='headerGuest.jsp'%>

<div class="container">
    <div class="alert alert-success hidden" id="success-alert">
        <h2>Успех</h2>
        <div>Ваши данные были успешно отправлены.</div>
    </div>
</div>


<div class="container">
    <div class="page-header">
        <h1>NetCrackerTest <small>Регистрация</small></h1>
    </div>
    <form role="form" id="form" action="/addUser" method="post">

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="surname">Введите фамилию</label>
            <input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="name">Введите Имя</label>
            <input type="text" class="form-control " name="name" id="name" placeholder="Имя" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="middle_name">Введите отчество</label>
            <input type="text" class="form-control " name="middle_name" id="middle_name" placeholder="Отчество" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="nickname">Введите никнейм</label>
            <input type="text" class="form-control " name="nickname" id="nickname" placeholder="Никнейм" title="Только буквы и цифры, не менее 3 символов"
                   pattern="[A-Za-zА-яа-яЁё0-9]{3,}">
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="ageUser">Введите дату рождения</label>
            <input type="date" class="form-control" name="ageUser" id="ageUser" placeholder="Дата рождения" >
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback col-lg-offset-4 col-lg-5">
            <label class="control-label" for="email">Введите email</label>
            <input type="email" class="form-control" name="email" id="email" placeholder="Email" >
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <div class="form-group has-feedback  col-lg-offset-4 col-lg-5">
            <label class="control-label" for="password">Введите пароль</label>
            <input type="password" class="form-control" name="password" id="password" placeholder="Пароль" >
            <span class="glyphicon form-control-feedback"></span>
        </div>

        <button type="submit"  class="btn-lg btn-success col-lg-5 col-lg-offset-4">Зарегистрироваться</button>

    </form>
</div>



<script type="text/javascript" src="/resources/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/resources/js/common.js"></script>

<%@include file='footer.jsp'%>



</body>
</html>