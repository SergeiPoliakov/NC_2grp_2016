<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 29.01.2017
  Time: 21:49
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
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <%@include file='header.jsp'%>



</head>
<body>

<div class="container">


    <div class="page-header">
        <h1>Netcracker <small>Изменение профиля ${user.email}</small></h1>
    </div>
    <div class="container">
        <div class="row">
            <div class="container col-lg-5 pull-right">

                <img src="http://netcracker.hop.ru/upload/${user.id}/avatar/avatar_${user.id}.png"
                     onerror="this.src = 'http://netcracker.hop.ru/upload/default/avatar.png'" class="img-polaroid" width="200">


                <div class="form-group ">
                    <%--Загрузка картинки-аватара--%>
                    <label for="InputImg">Загружка изображения</label>
                    <form method="POST" action="/uploadAvatar" enctype="multipart/form-data">
                        <input type="hidden" name="MAX_FILE_SIZE" value="20971520"><%--Ограничение на максимальный размер файла = 20 Мб со стороны клиента--%>
                        Файл: <input name="file" type="file" id="InputImg"
                                     accept="image/jpeg, image/png, image/gif"> <%--Ограничение на тип файла со стороны клиента--%>
                        <input type="submit" value="Загрузить"> Загрузить
                    </form>

                </div>
            </div>
            <div class="container col-lg-5">
                <form action="/changeProfile/${user.id}" method="post">

                    <div class="form-group  ">
                        <label for="InputName1">Введите Имя</label>
                        <input type="text" class="form-control " name="name" id="InputName1" value=${user.name}>
                    </div>

                    <div class="form-group ">
                        <label for="InputSurname1">Введите фамилию</label>
                        <input type="text" class="form-control" name="surname" id="InputSurname1" value=${user.surname}>
                    </div>

                    <div class="form-group ">
                        <label for="InputMiddleName1">Введите отчество</label>
                        <input type="text" class="form-control" name="middle_name" id="InputMiddleName1" value=${user.middleName}>
                    </div>

                    <div class="form-group  ">
                        <label for="InputAge1">Введите вашу дату рождения</label>
                        <input type="date" class="form-control" name="ageDate" id="InputAge1" value=${user.ageDate}>
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
                                <input type="radio" name="sex" id="Gender2" value="Ж">
                                Женский
                            </label>
                        </div>
                    </div>

                    <div class="form-group  ">
                        <label for="InputCity1">Введите ваш город</label>
                        <input type="text" class="form-control" name="city" id="InputCity1" value=${user.city}>
                    </div>

                    <div class="form-group  ">
                        <label for="InputNickName1">Введите Никнейм</label>
                        <input type="text" class="form-control " name="nickname" id="InputNickName1" value=${user.login}>
                    </div>

                    <div class="form-group ">
                        <label for="TextArea1">Расскажите немного о себе</label>
                        <textarea rows="3" class="form-control" name="info" id="TextArea1" >${user.additional_field}</textarea>
                    </div>

                    <button type="submit" class="btn-lg btn-success col-lg-5 col-lg-offset-4">Сохранить</button>

                </form>
            </div>
        </div>
    </div>

</div>

<%@include file='footer.jsp'%>

</body>
</html>

