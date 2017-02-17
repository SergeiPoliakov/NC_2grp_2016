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
    <title>${dataObject.getValue(1)} ${dataObject.getValue(2)}</title>
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
                <h4 class="card-title text-center">${dataObject.getValue(1)} ${dataObject.getValue(3)} ${dataObject.getValue(2)}</h4>
                <div class="card-title text-center">
                    <small class=" text-muted"><span
                            class="glyphicon glyphicon-user"></span> ${dataObject.getValue(4)} </small>
                </div>
                <div class="profile-userpic">
                    <img src="https://fshoke.com/wp-content/uploads/2016/01/Sean-Penn-mixed-with-Leonardo-DiCaprio.jpg" class="img-responsive"  alt='Изображение' >
                </div>
                <ul class="list-group list-group-my list-group-flush">
                    <li class="list-group-item" id="userAge">Дата рождения: ${dataObject.getValue(5)}</li>
                    <li class="list-group-item">Город: ${dataObject.getValue(9)}</li>
                    <li class="list-group-item">Пол: ${dataObject.getValue(8)}</li>
                    <li class="list-group-item">О себе: ${dataObject.getValue(10)}</li>
                </ul>
            </div>
        </div>
        <!-- Список шаблонов задач -->
        <div class="col-md-6">
            <div class="card pull-right" style="width: 30rem;">
                <div class="card-title">
                    <h3 class="text-center" id="cardsholder">Ваши шаблоны</h3>
                </div>
                <ul class="list-group list-group-my list-group-flush text-center navi mCustomScrollbar" data-mcs-theme="minimal-dark" id="cardsholderItems">
                    <li class="list-group-item list-group-item-info">РАЗ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-danger">ДВА ШАБЛОН</li>
                    <li class="list-group-item list-group-item-info">ТРИ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-info">ЧЕТЫРЕ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-danger">ПЯТЬ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-warning">ШЕСТЬ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-danger">СЕМЬ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-warning">ВОСЕМЬ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-info">ДЕВЯТЬ ШАБЛОН</li>
                    <li class="list-group-item list-group-item-warning">ДЕСЯТЬ ШАБЛОН</li>
                </ul>
                <button type="button" class="btn btn-primary btn-block" id="templateAddButton">Добавить</button>
            </div>
        </div>
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
    <div id="taskmodal" class="modal fade">
        <div class="modal-dialog">
            <form id="eventForm" name="creation" action="/userAddEvent" method="post">
                <div class="modal-content">
                    <!-- Заголовок модального окна -->
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title text-center">Создание новой задачи</h4>
                    </div>
                    <!-- Основное содержимое модального окна -->
                    <div class="modal-body">
                        <div class='row '>
                            <div class='col-md-6'>
                                <div class="input-group">
                                    <span class="input-group-addon">Название</span>
                                    <input type="text" class="form-control" name="name" id="taskName" placeholder="Введите название задачи">
                                </div>
                            </div>
                            <div class='col-md-6'>
                                <div class="input-group">
                                    <div type="text" class="hidden" name="eventId" id="taskID" value = "eventId"></div>
                                    <span class="input-group-addon">Приоритет</span>
                                    <select type="text" id="taskPriority" name="priority" class="selectpicker form-control" title="Выберите приоритет">
                                        <option style="background: #d9534f; color: #fff;" value="Style1">Высокий</option>
                                        <option style="background: #f0ad4e; color: #fff;" value="Style2">Средний</option>
                                        <option style="background: #5bc0de; color: #fff;" value="Style3" selected>Низкий</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <!-- DateTime Pickers -->
                        <div class='row top-buffer-2'>
                            <div class='col-md-6'>
                                <div class='input-group date' id='datetimepicker1'>
                                    <span class="input-group-addon">Начало</span>
                                    <input type='text' name="date_begin" class="form-control" id="taskStartTime" />
                                    <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                </div>
                            </div>
                            <div class='col-md-6'>
                                <div class='input-group date' id='datetimepicker2'>
                                    <span class="input-group-addon">Окончание</span>
                                    <input type='text' name="date_end" class="form-control" id="taskEndTime" />
                                    <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                </div>
                            </div>
                        </div>
                        <div class="row top-buffer-2">
                            <div class="col-md-12">
                                <div class="form-group">
                                    <div class="input-group-addon textarea-addon">Дополнительная информация</div>
                                    <textarea type='text' name="info" class="form-control noresize textarea-for-modal" rows="5" id="taskAddInfo"></textarea>
                                </div>
                            </div>
                        </div>
                        <ul class="list-group list-group-my">
                            <li class="list-group-item">
                                Сохранить шаблон
                                <div class="material-switch pull-right">
                                    <input id="SaveTemplateCheckBox" type="checkbox"/>
                                    <label for="SaveTemplateCheckBox" class="label-primary"></label>
                                </div>
                            </li>
                        </ul>
                    </div>
                    <!-- Футер модального окна -->
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                        <button type="submit" class="btn btn-primary" id="modalAddButton">Добавить</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div id="log"></div>
</div>

<%@include file='footer.jsp'%>

<script type="text/javascript">
    // Поле дополнительная информация eventID : info
    var addInfoArray = {
    <c:forEach items="${allEvents}" var="event">${event.id}: '${event.getValue(104)}',</c:forEach>
    };
    // Настройка кастомного скроллбара
    $("#cardsholderItems").mCustomScrollbar({
        scrollInertia: 275
    });
    // Modal datetimepickers для создания новой задачи
    $(function () {
        $('#datetimepicker1').datetimepicker({
            locale: 'ru'
            //format: "DD/MM/YYYY hh:mm"
        });
        $('#datetimepicker2').datetimepicker({
            locale: 'ru',
            useCurrent: false
        });
        $("#datetimepicker1").on("dp.change", function (e) {
            $('#datetimepicker2').data("DateTimePicker").minDate(e.date);
        });
        $("#datetimepicker2").on("dp.change", function (e) {
            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
        });
    });
    // Нажатие кнопки под шаблонами
    document.getElementById('templateAddButton').onclick = function() {
        $("#modalAddButton").html('Добавить');
        $('#taskmodal').modal('show');
        $('#taskmodal').on('shown.bs.modal', function () {
            $("#SaveTemplateCheckBox").prop("checked", true);
            $('#taskName').val("Новая задача");
            $('#taskName').focus();
            $('#taskName').select();
        })
    };
    // Нажатие кнопки "Добавить" в всплывающем окне
    document.getElementById('modalAddButton').onclick = function() {
        $('#taskmodal').modal('hide');
    };

    // Открытие полной картинки при нажатии
    $(function() {
        $('.profile-userpic').on('click', function() {
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
        {id: ${event.id}, content: '${event.name}', start: new Date(getDateFromString('${event.getValue(101)}')), end: new Date(getDateFromString('${event.getValue(102)}')), className: '${event.getValue(105)}'},
        </c:forEach>
    ]);

    // Configuration for the Timeline
    var options = {
        locale: 'RU',
        editable: true,
        selectable: true,
        stack: false,
        multiselect: true,
        dataAttributes: 'all',
        itemsAlwaysDraggable: true,
        zoomMin: 60000, // 1 минута
        zoomMax: 157700000000, //5 лет

        // Добавление задачи
        onAdd: function (item, callback) {
            $('#eventForm').attr('action', '/userAddEvent');
            $('#taskName').val("Новая задача");
            $("#modalAddButton").html('Добавить');
            document.getElementById('taskStartTime').value = toLocaleDateTimeString(item.start);
            document.getElementById('taskEndTime').value = toLocaleDateTimeString(item.start);
            $('#taskmodal').modal('show');
            document.getElementById('modalAddButton').onclick = function() {
                $('#taskmodal').modal('hide');
                // Изменение элемента на таймлайне, наверное уже не нужно, т.к. сервер сам перегружает данные, но на всякий пусть останется
                /*item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                 item.start = getDateFromString(document.getElementById('taskStartTime').value);
                 item.end = getDateFromString(document.getElementById('taskEndTime').value);
                 item.content = document.getElementById('taskName').value;
                 $('#taskmodal').modal('hide');
                 callback(item);
                 createTooltip();*/
            };
            callback(null);
        },

        // Удаление задачи
        onRemove: function (item, callback) {
            $('#eventForm').attr('action', '/userRemoveEvent/'+item.id);
            $( "#eventForm" ).submit();
            callback(item);
        },

        // Обновление задачи
        onUpdate: function (item, callback) {
            $('#eventForm').attr('action', '/userChangeEvent/'+item.id);
            $("#modalAddButton").html('Сохранить');
            $('#taskStartTime').val(toLocaleDateTimeString(item.start));
            $('#taskEndTime').val(toLocaleDateTimeString(item.end));
            $('#taskID').val(item.id);
            $('#taskName').val(item.content);
            $('#taskAddInfo').val(addInfoArray[item.id]);
            $('#taskPriority').val(item.className);
            $('#taskPriority').selectpicker('refresh');
            $('#taskmodal').modal('show');
            document.getElementById('modalAddButton').onclick = function() {
                $('#taskmodal').modal('hide');
                // Изменение элемента на таймлайне, наверное уже не нужно, т.к. сервер сам перегружает данные, но на всякий пусть останется
                /*item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                 item.start = getDateFromString(document.getElementById('taskStartTime').value);
                 item.end = getDateFromString(document.getElementById('taskEndTime').value);
                 item.content = document.getElementById('taskName').value;
                 $('#taskmodal').modal('hide');
                 callback(item);
                 createTooltip();*/
            };
            callback(null);
        },

        // Перемещение задачи
        onMove: function (item, callback) {
            $('#eventForm').attr('action', '/userChangeEvent/'+item.id);
            $("#modalAddButton").html('Сохранить');
            $('#taskStartTime').val(toLocaleDateTimeString(item.start));
            $('#taskEndTime').val(toLocaleDateTimeString(item.end));
            $('#taskID').val(item.id);
            $('#taskName').val(item.content);
            $('#taskPriority').val(item.className);
            $('#taskPriority').selectpicker('refresh');
            $('#taskAddInfo').val(addInfoArray[item.id]);
            $( "#eventForm" ).submit();
            callback(item);
        }
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
    $("#userAge").html('Возраст: ' + declOfNum(getAge(getDateFromString('${user.ageDate}')), ['год', 'года', 'лет']));
</script>
</body>
</html>
