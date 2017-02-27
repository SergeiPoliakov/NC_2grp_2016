<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 27.02.2017
  Time: 15:11
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
    <title>Advanced Settings Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <%@include file='header.jsp'%>



</head>
<body>
<div class="container">
<div class="container">
    <div class="page-header">
        <h1>Netcracker <small>Расширенные настройки профиля ${user.email}</small></h1>
    </div>
    <form action="/generatePhoneCode" method="get">
        <div class="form-group col-lg-offset-4 col-lg-5">

            <button type="submit" >Отправить код</button>

        </div>
    </form>

    <form action="/confirmedPhone" method="post">
        <div class="form-group  col-lg-offset-4 col-lg-5">
        <label for="InputCode">Введите код подтверждения</label>
        <input type="text" class="form-control " name="codeUser" id="InputCode">
            <button type="submit" >Подтвердить</button>
        </div>
    </form>
</div>

    <div class="form-group ">
        <a href="/profile"> К обычным настройкам </a>
    </div>

<div class="container">
    <form action="" method="post">
 <!--  Тут будут настройки оповещения для email и телефона с кнопкой Сохранить. Нужен фронтенд. -->
    </form>
</div>
</div>

<%@include file='footer.jsp'%>

</body>
</html>
