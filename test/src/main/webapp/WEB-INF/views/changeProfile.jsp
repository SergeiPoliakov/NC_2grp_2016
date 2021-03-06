<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 07.01.2017
  Time: 16:53
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
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/main.css" rel="stylesheet">
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="/resources/js/bootstrap.min.js"></script>
<div class="container">


    <div class="page-header">
        <h1>Netcracker <small>Изменение профиля ${user.email}</small></h1>
    </div>
    <div class="container">
        <div class="row">
            <div class="container col-lg-5 pull-right">

                <img src=".." class="img-polaroid ">

                <div class="form-group ">
                    <label for="InputImg">Загрузить изображение</label>
                    <input type="file" id="InputImg">
                </div>
            </div>
            <div class="container col-lg-5">
                <form action="/changeProfile/${user.id}" method="post">
                    <div class="form-group  ">
                        <label for="InputNickName1">Введите Никнейм</label>
                        <input type="text" class="form-control " name="nickname" id="InputNickName1" value=${user.nickname}>
                    </div>
                    <div class="form-group  ">
                        <label for="InputName1">Введите Имя</label>
                        <input type="text" class="form-control " name="name" id="InputName1" value=${user.name}>
                    </div>
                    <div class="form-group ">
                        <label for="InputSurname1">Введите фамилию</label>
                        <input type="text" class="form-control" name="surname" id="InputSurname1" value=${user.surname}>
                    </div>
                    <div class="form-group  ">
                        <label for="InputAge1">Введите ваш возраст</label>
                        <input type="text" class="form-control" name="age" id="InputAge1" value=${user.age}>
                    </div>
                    <div class="form-group  ">
                        <label for="InputCity1">Введите ваш город</label>
                        <input type="text" class="form-control" name="city" id="InputCity1" value=${user.city}>
                    </div>
                    <div class="form-group  ">
                        <label for="InputPhone1">Введите номер вашего телефона</label>
                        <input type="text" class="form-control" name="phone" id="InputPhone1" value=${user.phone}>
                    </div>
                    <div class="form-group  ">
                        <label >Выберите ваш пол</label>
                        <div class="radio">
                            <label>
                                <input type="radio" name="sex"  id="Gender1" value="М" checked>
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
                    <div class="form-group   ">
                        <label for="InputPassword1">Введите пароль</label>
                        <input type="password" class="form-control" name="password" id="InputPassword1" value=${user.password}>
                    </div>
                    <div class="form-group ">
                        <label for="TextArea1">Расскажите немного о себе</label>
                        <textarea rows="3" class="form-control" name="info" id="TextArea1" value=${user.info}></textarea>
                    </div>

                    <button type="submit" class="btn-lg btn-success col-lg-5 col-lg-offset-4">Сохранить</button>

                </form>
            </div>
        </div>
    </div>

</div>

<footer class="navbar-static-bottom navbar-inverse">
    <small>&copy; Netcracker</small>
</footer>
</body>
</html>

