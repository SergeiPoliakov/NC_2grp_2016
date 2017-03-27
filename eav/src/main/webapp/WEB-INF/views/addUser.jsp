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

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/validator.min.js"></script>
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
    <div class="row">
        <div class="col-lg-4 col-lg-offset-4">
            <div class="well bs-component">
                <form role="form" id="form" action="/addUser" method="post" data-toggle="validator">
                    <fieldset>
                        <legend>Регистрация</legend>
                        <div class="form-group has-feedback">
                            <label class="control-label" for="surname">Введите фамилию</label>
                            <input data-toggle="tooltip" type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="name">Введите Имя</label>
                            <input data-toggle="tooltip" type="text" class="form-control " name="name" id="name" placeholder="Имя" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="middle_name">Введите отчество</label>
                            <input data-toggle="tooltip" type="text" class="form-control " name="middle_name" id="middle_name" placeholder="Отчество" pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов">
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="nickname">Введите никнейм</label>
                            <input data-toggle="tooltip" type="text" class="form-control " name="nickname" id="nickname" placeholder="Никнейм" title="Только буквы и цифры, не менее 3 символов"
                                   pattern="[A-Za-zА-яа-яЁё0-9]{3,}">
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="ageUser">Введите дату рождения</label>
                            <input data-toggle="tooltip" type="date" class="form-control" name="ageUser" id="ageUser" placeholder="Дата рождения" >
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="email">Введите email</label>
                            <input data-toggle="tooltip" type="email" class="form-control" name="email" id="email" placeholder="Email" >
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="phone">Введите номер телефона</label>
                            <input data-toggle="tooltip" type="text" class="form-control" name="phone" id="phone" placeholder="Телефон" pattern="[1-9]{11}" title="Введите корректный номер телефона">
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>

                        <div class="form-group has-feedback">
                            <label class="control-label" for="password">Введите пароль</label>
                            <input data-toggle="tooltip" type="password" class="form-control" name="password" id="password" placeholder="Пароль" >
                            <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                        </div>
                        <div class="text-center">
                            <button type="submit"  class="btn btn-success">Зарегистрироваться</button>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </div>
</div>



<script type="text/javascript" src="/resources/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/resources/js/common.js"></script>
</body>
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>
</html>