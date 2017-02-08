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

    <title>${user.name} ${user.surname}</title>

    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/tipped.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/vis.min.css">

    <link rel="stylesheet" type="text/css" href="/resources/css/tlmain.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>


    <script type="text/javascript" src="/resources/js/moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="/resources/js/enscroll-0.6.2.min.js"> </script>
    <script type="text/javascript" src="/resources/js/tipped.js"> </script>
    <script type="text/javascript" src="/resources/js/vis.js"> </script>

    <script type="text/javascript" src="/resources/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="/resources/js/bootstrap-select.min.js"> </script>


    <%@include file='header.jsp'%>
    <%@include file='leftMenu.jsp'%>

</head>
<body>

<div class="container top-buffer-20">
    <!-- Информация о пользователе -->
    <div class="row">
        <div class="col-md-6">
            <div class="card" style="width: 30rem;">
                <h3 class="card-title text-center">${user.name} ${user.surname}</h3>
                <a href="#" class="thumbnail thumbnail-my card-img-top">
                    <img src="http://s5.postimg.org/4xpbh5oo3/user_000000_128.png" alt='Изображение'>
                </a>
                <ul class="list-group list-group-my list-group-flush">
                    <li class="list-group-item" id="userAge">Дата рождения: ${user.ageDate}</li>
                    <li class="list-group-item">Город: ${user.country}</li>
                    <li class="list-group-item">Пол: ${user.sex.toLowerCase()}</li>
                    <li class="list-group-item">О себе: ${user.additional_field}</li>
                </ul>
                <button type="button" class="btn btn-success btn-block">Отслеживать</button>
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
    <div class="modal fade" id="imagemodal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <img src="" class="imagepreview" style="width: 100%;" >
                </div>
            </div>
        </div>
    </div>
    <!-- Форма для создания новой задачи -->

</div>

<%@include file='footer.jsp'%>

<script type="text/javascript">

    // Scrollbar для кардхолдера

    // Modal datetimepickers для создания новой задачи

    // Нажатие кнопки под шаблонами



    // Открытие полной картинки при нажатии
    $(function() {
        $('.thumbnail').on('click', function() {
            $('.imagepreview').attr('src', $(this).find('img').attr('src'));
            $('#imagemodal').modal('show');
        });
    });
</script>

<script type="text/javascript">
    // TIMELINE FILL, SETUP AND CREATE
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var container = document.getElementById('visualization');
    // Create a DataSet (allows two way data-binding)
    var items = new vis.DataSet([
        <c:forEach items="${allEvents}" var="event">
        {id: ${event.id}, content: '${event.name}', start: new Date(getDateFromString('${event.date_begin}')), end: new Date(getDateFromString('${event.date_end}')), className: '${event.priority}'},
        </c:forEach>
    ]);

    // Configuration for the Timeline
    var options = {
        locale: 'RU',
        editable: false,
        selectable: false,
        stack: false,
        multiselect: true,
        dataAttributes: 'all',
        itemsAlwaysDraggable: false,
        zoomMin: 60000, // 1 минута
        zoomMax: 157700000000, //5 лет

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

    timeline.on('itemover', function (event, properties) {
        //logEvent(event, properties);
    });

    items.on('*', function (event, properties) {
        logEvent(event, properties);
    });

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
    $("#userAge").html('Возраст: ' + getAge(getDateFromString('${user.ageDate}')));
</script>
</body>
</html>




