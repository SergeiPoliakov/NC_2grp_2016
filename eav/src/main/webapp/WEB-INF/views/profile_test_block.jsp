<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 29.01.2017
  Time: 21:49
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
    <title>Change Profile Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <%@include file='header.jsp' %>

</head>
<body>

<div class="container">


    <div class="page-header">
        <h1>Netcracker
            <small>Изменение профиля ${user.email}</small>
        </h1>
    </div>
    <div class="container">

        <div class="row">
            <div class="container col-lg-2 pull-right">


                <img src="https://calendar.google.com/googlecalendar/images/calendarlogo/calendar_logo_ru_41px_1x_r1.png" class="img-polaroid"
                     width="200">
                <div class="form-group ">
                    <%--Тестовый блок гугл-календаря, потом джаваскриптом можем подставлять идентификатор пользоателя и грузить его календарь, а расположить блок на нужной странице--%>
                    <label for="InputImg">Общий календарь</label>
                    <div class="form-group ">
                        <iframe src="https://calendar.google.com/calendar/embed?showTitle=0&amp;showPrint=0&amp;showTabs=0&amp;showCalendars=0&amp;showTz=0&amp;height=600&amp;wkst=2&amp;bgcolor=%23ffffff&amp;src=netcracker.thesecondgroup%40gmail.com&amp;color=%231B887A&amp;ctz=Europe%2FMoscow"
                                style="border-width:0" width="250" height="450" frameborder="0" scrolling="no"></iframe>
                    </div>
                </div>

            </div>
            <div class="row">
                <div class="container col-lg-4 pull-right">

                    <img src="http://nc2.hop.ru/upload/${user.id}/avatar/avatar_${user.id}.png"
                         onerror="this.src = 'http://nc2.hop.ru/upload/default/avatar.png'" class="img-polaroid"
                         width="200">


                    <div class="form-group ">
                        <%--Загрузка картинки-аватара--%>
                        <label for="InputImg">Загрузка изображения</label>
                        <form method="POST" action="/uploadAvatar" enctype="multipart/form-data">
                            <input type="hidden" name="MAX_FILE_SIZE" value="20971520"><%--Ограничение на максимальный размер файла = 20 Мб со стороны клиента--%>
                            Файл: <input name="file" type="file" id="InputImg"
                                         accept="image/jpeg, image/png, image/gif"> <%--Ограничение на тип файла со стороны клиента--%>
                            <input type="submit" value="Загрузить"> Загрузить
                        </form>
                    </div>


                    <img src="https://lifehacker.ru/wp-content/uploads/2014/11/01_Comp-2.png" class="img-polaroid"
                         width="200">
                    <div class="form-group ">
                        <%--Кнопка подключения календаря--%>
                        <label for="InputImg">Подключите Google-календарь</label>
                        <div class="form-group ">
                            <a href="/addCalendar">
                                <button type="button" class="btn btn-info"><span class="glyphicon glyphicon-calendar"
                                                                                 aria-hidden="true"> Подключить</span>
                                </button>
                            </a>

                            <a href="/synchronizeCalendar">
                                <button type="button" class="btn btn-info"><span class="glyphicon glyphicon-calendar"
                                                                                 aria-hidden="true"> Синхронизировать</span>
                                </button>
                            </a>
                        </div>
                    </div>

                    <div class="form-group ">
                        <a href="/advancedSettings"> Расширенные настройки </a>
                    </div>

                </div>


                <div class="container col-lg-4">
                    <form action="/changeProfile/${user.id}" method="post">

                        <div class="form-group  ">
                            <label for="InputName1">Введите Имя</label>
                            <input type="text" class="form-control " name="name" id="InputName1" value=${user.name}>
                        </div>

                        <div class="form-group ">
                            <label for="InputSurname1">Введите фамилию</label>
                            <input type="text" class="form-control" name="surname" id="InputSurname1"
                                   value=${user.surname}>
                        </div>

                        <div class="form-group ">
                            <label for="InputMiddleName1">Введите отчество</label>
                            <input type="text" class="form-control" name="middle_name" id="InputMiddleName1"
                                   value=${user.middleName}>
                        </div>

                        <div class="form-group  ">
                            <label for="InputAge1">Введите вашу дату рождения</label>
                            <input type="date" class="form-control" name="ageDate" id="InputAge1" value=${user.ageDate}>
                        </div>

                        <div class="form-group  ">
                            <label>Выберите ваш пол</label>
                            <div class="radio">
                                <label>
                                    <input type="radio" name="sex" id="Gender1" value="Мужской" checked>
                                    Мужской
                                </label>
                            </div>
                            <div class="radio">
                                <label>
                                    <input type="radio" name="sex" id="Gender2" value="Женский">
                                    Женский
                                </label>
                            </div>
                        </div>

                        <div class="form-group  ">
                            <label for="InputCity1">Введите ваш город</label>
                            <input type="text" class="form-control" name="city" id="InputCity1" value=${user.city}>
                        </div>

                        <div class="form-group  ">
                            <label for="InputPhone1">Введите ваш номер телефона</label>
                            <input type="text" class="form-control" name="phone" id="InputPhone1" value=${user.phone}>
                        </div>

                        <div class="form-group ">
                            <label for="TextArea1">Расскажите немного о себе</label>
                            <textarea rows="3" class="form-control" name="info"
                                      id="TextArea1">${user.additional_field}</textarea>
                        </div>

                        <button type="submit" class="btn btn-info col-lg-5 col-lg-offset-4">Сохранить</button>

                    </form>
                </div>
            </div>
        </div>

    </div>

    <%@include file='footer.jsp' %>

</body>
</html>

