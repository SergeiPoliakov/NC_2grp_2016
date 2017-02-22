<%--
  Created by IntelliJ IDEA.
  User: Костя
  Date: 02.02.2017
  Time: 0:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 249 СТРОКА, ДОБАВЛЕНИЕ ЗАДАЧИ -->
<html>
<head>
    <title>${user.name} ${user.surname}</title>
    <%@include file='header.jsp'%>

    <meta charset="UTF-8">

    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/tipped.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/vis.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/tlmain.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/jquery.mCustomScrollbar.min.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <script type="text/javascript" src="/resources/js/moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="/resources/js/tipped.js"> </script>
    <script type="text/javascript" src="/resources/js/vis.js"> </script>
    <script type="text/javascript" src="/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/resources/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="/resources/js/bootstrap-select.min.js"> </script>
    <script type="text/javascript" src="/resources/js/jquery.mCustomScrollbar.concat.min.js"> </script>
</head>
<body>
<div class="container top-buffer-20">
    <!-- Информация о пользователе -->
    <div class="row">
        <div class="col-md-6">
            <div class="card" style="width: 30rem;">
                <h4 class="card-title text-center">${user.surname} ${user.name} ${user.middleName}</h4>
                <div class="card-title text-center">
                    <small class=" text-muted"><span
                            class="glyphicon glyphicon-user"></span> ${user.login} </small>
                </div>
                <div class="profile-userpic">
                    <img src="${user.picture}" onerror="this.src = 'http://netcracker.hop.ru/upload/default/avatar.png'" class="img-responsive"  alt='Изображение' >
                </div>
                <div class="profile-userbuttons">
                    <a href="/sendMessage/${user.id}"><button type="button" class="btn btn-info btn-xs"><span class="glyphicon glyphicon-envelope" aria-hidden="true"> К  чату</span></button></a>
                </div>
                <ul class="list-group list-group-my list-group-flush">
                    <li class="list-group-item" id="userAge">Дата рождения: ${user.ageDate}</li>
                    <li class="list-group-item">Город: ${user.city}</li>
                    <li class="list-group-item">Пол: ${user.city}</li>
                    <li class="list-group-item">О себе: ${user.additional_field}</li>
                </ul>
            </div>
        </div>
        <!-- Список шаблонов задач -->

    </div>
    <!-- Timeline и кнопки -->
    <div class="row">
        <div class="col-md-12">
            <h4>Расписание</h4>
            <div class="btn-group btn-group-justified" role="group" aria-label="...">
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-default timeline-menu-button" id="showTodayButton">Cегодня</button>
                </div>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-default timeline-menu-button" id="showWeekButton">Неделя</button>
                </div>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-default timeline-menu-button" id="showMonthButton">Месяц</button>
                </div>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-default timeline-menu-button" id="showYearButton">Год</button>
                </div>
            </div>
            <div id="visualization"></div>
        </div>
    </div>
    <!-- Форма вывода полноразмерного изображения -->

    <!-- Форма для создания новой задачи -->

    <div id="log"></div>
</div>

<%@include file='footer.jsp'%>

<script type="text/javascript">
    // Поле дополнительная информация eventID : info
    var addInfoArray = {
    <c:forEach items="${allEvents}" var="event">${event.id}:'${event.info}',</c:forEach>
    };
    // Настройка кастомного скроллбара

    // Modal datetimepickers для создания новой задачи

    // Нажатие кнопки под шаблонами

    // Нажатие кнопки "Добавить" в всплывающем окне


    // Открытие полной картинки при нажатии

</script>

<script type="text/javascript">
    // TIMELINE FILL, SETUP AND CREATE
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var container = document.getElementById('visualization');
    // Create a DataSet (allows two way data-binding)
    var items = new vis.DataSet([
        <c:forEach items="${allObject}" var="event">
        {id: ${event.id}, content: '${event.name}', start: new Date(getDateFromString('${event.date_begin}')), end: new Date(getDateFromString('${event.date_end}')), className: '${event.priority}'},
        </c:forEach>
    ]);

    // Configuration for the Timeline
    var options = {
        locale: 'RU',
        editable: false,
        selectable: false,
        stack: false,
        multiselect: false,
        dataAttributes: 'all',
        itemsAlwaysDraggable: false,
        zoomMin: 60000, // 1 минута
        zoomMax: 157700000000, //5 лет

        // Добавление задачи


        // Удаление задачи


        // Обновление задачи


        // Перемещение задачи

    };
    // Create a Timeline
    var timeline = new vis.Timeline(container, items, options);
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Вывод информации, при наведении на элемент
    function createTooltip(){
        Tipped.create('.vis-item', function(element) {
                var itemId = $(element).attr('data-id');
                var item = items.get(itemId);
                return {
                    title: item.content,
                    content: toLocaleDateTimeString(item.start) + ' - ' + toLocaleDateTimeString(item.end)
                }
            },
            {
                position: 'bottom',
                behavior: 'hide',
                cache: false
            }
        );
    }

    // Преобразовать дату в строку формата DD.MM.YYYY hh:mm
    function toLocaleDateTimeString(dateString){
        var eventTime = dateString.toLocaleTimeString();
        var eventTimeAfter = eventTime.substring(0, eventTime.length-3);
        if (eventTimeAfter.length < 5)
            eventTimeAfter = '0' + eventTimeAfter;
        var startDate = dateString.toLocaleDateString() + ' ' + eventTimeAfter;
        return startDate;
    }

    // Получить дату из строки вида DD.MM.YYYY hh:mm
    function getDateFromString(dateString){
        var reggie = /(\d{2}).(\d{2}).(\d{4}) (\d{2}):(\d{2})/;
        var dateArray = reggie.exec(dateString);
        var dateObject = new Date(
            (+dateArray[3]),
            (+dateArray[2])-1, // Careful, month starts at 0!
            (+dateArray[1]),
            (+dateArray[4]),
            (+dateArray[5])
        );
        return dateObject;
    }

    // Получить возраст
    function getAge(dateString) {
        var today = new Date();
        var birthDate = new Date(dateString);
        var age = today.getFullYear() - birthDate.getFullYear();
        var m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }
    // Склонение существительных после числительных
    function declOfNum(number, titles) {
        cases = [2, 0, 1, 1, 1, 2];
        return number + ' ' + titles[ (number%100>4 && number%100<20)? 2 : cases[(number%10<5)?number%10:5] ];
    }
    // Просмотр сегодняшнего дня
    document.getElementById('showTodayButton').onclick = function() {
        var currentDate = new Date();
        currentDate.setHours(0,0,0,0);
        var nextDay = new Date(currentDate);
        nextDay.setDate(nextDay.getDate() + 1);
        timeline.setWindow(currentDate, nextDay);
    };

    // Просмотр недели
    document.getElementById('showWeekButton').onclick = function() {
        var currentDate = new Date();
        var monday = new Date();
        currentDate.setHours(0,0,0,0);
        var day = currentDate.getDay() || 7;
        if( day !== 1 )
            monday.setHours(-24 * (day - 1));
        var inWeek = new Date(monday);
        inWeek.setDate(monday.getDate()+7);
        timeline.setWindow(monday, inWeek);
    };

    // Просмотр месяца
    document.getElementById('showMonthButton').onclick = function() {
        var currentDate = new Date();
        currentDate.setHours(0,0,0,0);
        var firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
        var lastDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1);
        timeline.setWindow(firstDay, lastDay);
    };

    // Просмотр года
    document.getElementById('showYearButton').onclick = function() {
        var currentDate = new Date();
        currentDate.setHours(0,0,0,0);
        var firstDay = new Date(currentDate.getFullYear(), 0, 1);
        var lastDay = new Date(currentDate.getFullYear(), 12, 0);
        timeline.setWindow(firstDay, lastDay);
    };



    // Запись в лог для отладки
    function logEvent(event, properties) {
        var log = document.getElementById('log');
        var msg = document.createElement('div');
        msg.innerHTML = 'event=' + JSON.stringify(event) + ', ' +
            'properties=' + JSON.stringify(properties);
        log.firstChild ? log.insertBefore(msg, log.firstChild) : log.appendChild(msg);
    }
    createTooltip();
    // Установка возраста
    $("#userAge").html('Возраст: ' + declOfNum(getAge(getDateFromString('${dataObject.getValue(5)}')), ['год', 'года', 'лет']));
</script>
</body>
</html>


