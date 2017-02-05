<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 01.02.2017
  Time: 14:57
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
    <title>Change Profile Page</title>


    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>
<body>

<%@include file='header.jsp'%>


<div class="container">


    <div class="page-header">
        <h1>Netcracker <small>Просмотр профиля ${user.login}</small></h1>
    </div>
    <div class="container">
        <div class="row">
            <div class="container col-lg-5 pull-right">

                <img src=".." class="img-polaroid ">

                <div class="form-group ">
                    <label>Изображение</label>
                </div>
            </div>
            <div class="container col-lg-5">
                <form>

                    <div class="form-group  ">
                        <label for="InputName1">Имя</label>
                        <input type="text" class="form-control " name="name" id="InputName1" value=${user.name}>
                    </div>

                    <div class="form-group ">
                        <label for="InputSurname1">Фамилия</label>
                        <input type="text" class="form-control" name="surname" id="InputSurname1" value=${user.surname}>
                    </div>

                    <div class="form-group ">
                        <label for="InputMiddleName1">Отчество</label>
                        <input type="text" class="form-control" name="middle_name" id="InputMiddleName1" value=${user.middleName}>
                    </div>

                    <div class="form-group  ">
                        <label for="InputAge1">Дату Рождения</label>
                        <input type="date" class="form-control" name="ageDate" id="InputAge1" value=${user.ageDate}>
                    </div>

                    <div class="form-group  ">
                        <label >Пол</label>
                        <div class="radio">
                            <label>
                                <input type="radio" name="sex"  id="Gender1" value="М" checked>
                                Мужской
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="sex" id="Gender2" value="Ж">
                                Женский
                            </label>
                        </div>
                    </div>

                    <div class="form-group  ">
                        <label for="InputCountry1">Страна</label>
                        <input type="text" class="form-control" name="country" id="InputCountry1" value=${user.country}>
                    </div>

                    <div class="form-group ">
                        <label for="TextArea1">Немного о себе</label>
                        <textarea rows="3" class="form-control" name="info" id="TextArea1" >${user.additional_field}</textarea>
                    </div>



                </form>
            </div>
        </div>
    </div>

</div>

<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; NetCracker</small>
</footer>
</body>
</html>

