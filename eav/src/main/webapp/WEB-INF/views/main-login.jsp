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
    <title>Ваше расписание</title>
    <%@include file='header.jsp'%>

    <meta charset="UTF-8">

    <style>
        .prokrutka {
            height: 400px; /* высота нашего блока */
            width: 100%; /* ширина нашего блока */
            background: #fff; /* цвет фона, белый */
            border: 1px solid #C1C1C1; /* размер и цвет границы блока */
            overflow-x: auto; /* прокрутка по горизонтали */
            overflow-y: auto; /* прокрутка по вертикали */
        }
        .prok {
            height: 40px; /* высота нашего блока */
            width: 100%; /* ширина нашего блока */
            background: #fff; /* цвет фона, белый */
            border: 1px solid #C1C1C1; /* размер и цвет границы блока */
        }
    </style>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tipped.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/vis.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tlmain.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/jquery.mCustomScrollbar.min.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/tipped.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/vis.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap-select.min.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.mCustomScrollbar.concat.min.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/validator.min.js"></script>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

    <!-- Подгружаем стили и скрипты для загрузки статистик и для свайпинга бокового меню: -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/stat_load.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/stat_menu_swipe.js"></script>

    <script type="text/javascript">
        google.charts.load('current', {packages:['wordtree']});
        google.charts.setOnLoadCallback(drawChart);

        function drawChart() {
            var data = google.visualization.arrayToDataTable(
                [['+'],
                <c:forEach items="${eventword}" var="eventstring">
                    ${eventstring}
                </c:forEach>
                ]
            );

            var options = {
                maxFontSize: 16,
                wordtree: {
                    format: 'implicit',
                    word: '+'
                }
            };

            var chart = new google.visualization.WordTree(document.getElementById('wordtree_basic'));
            chart.draw(data, options);
        }
    </script>
</head>
<body>


<div class="container top-buffer-20">
    <!-- Место для бокового меню статистик: -->
    <%@include file='statMenu.jsp'%>

        <div class="main-content">
            <div class="swipe-area"></div>
            <a href="#" data-toggle=".container" id="sidebar-toggle">
                <span class="bar"></span>
                <span class="bar"></span>
                <span class="bar"></span>
            </a>
            <div class="content">

                <!-- Информация о пользователе -->
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-6 col-lg-3">
                        <div class="card">
                            <h4 class="card-title text-center">${user.name} ${user.surname} ${user.middleName}</h4>
                            <div class="card-title text-center">
                                <small class=" text-muted"><span
                                        class="glyphicon glyphicon-user"></span> ${user.login} </small>
                            </div>
                            <div class="profile-userpic">
                                <img  id="profilePic" src="${user.picture}"
                                      onerror="this.src = 'ftp://netcracker.ddns.net/upload/default/avatar.png'" class="img-responsive"  alt='Изображение' >
                            </div>
                            <ul class="list-group list-group-my">
                                <li class="list-group-item" id="userAge">Дата рождения: ${user.ageDate}</li>
                                <li class="list-group-item">Город: ${user.city}</li>
                                <li class="list-group-item">Пол: ${user.sex}</li>
                                <li class="list-group-item">О себе: ${user.additional_field}</li>
                            </ul>
                        </div>
                    </div>
                    <!--
                    <div class="col-md-4">
                        <div class="card-title text-center">
                            <h4>Дерево событий:</h4>
                        </div>
                        <div class="card-title text-center">
                            <div class="prokrutka" id="wordtree_basic"></div>
                        </div>
                    </div>
                    -->
                    <!-- Список шаблонов задач -->
                    <div class="col-xs-12 col-sm-12 col-md-6 col-lg-3 col-lg-offset-6">
                        <div class="card">
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
                            <button type="button" class="btn btn-success btn-block" id="templateAddButton">Добавить</button>
                        </div>
                    </div>
                </div>
                <!-- Timeline и кнопки -->
                <div class="row top-buffer-5">
                    <div class="col-md-12">
                        <h4>Расписание</h4>
                        <div id ="timelineContainer">
                            <div class="btn-group btn-group-justified" role="group" aria-label="...">
                                <div class="btn-group" role="group">
                                    <button type="button" class="btn btn-default timeline-menu-button" id="showTodayButton">Сегодня</button>
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
                        <form id="eventForm" name="creation" data-toggle="validator">
                            <div class="modal-content">
                                <!-- Заголовок модального окна -->
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    <h4 class="modal-title text-center">Создание новой задачи</h4>
                                </div>
                                <!-- Основное содержимое модального окна -->
                                <div class="modal-body">
                                    <div class='row'>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="display: inline;">
                                                <label for="taskName" class="control-label">Название:</label>
                                                <input type="text" class="form-control" name="name" id="taskName" placeholder="Введите название задачи">
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="width: 1%;display: table-cell;">
                                                <div type="text" class="hidden" name="eventId" id="taskID" value = "eventId"></div>
                                                <label for="taskPriority" class="control-label">Приоритет:</label>
                                                <select type="text" id="taskPriority" name="priority" class="selectpicker form-control" title="Выберите приоритет" data-style="myButton">
                                                    <option style="background: #e74c3c; color: #fff;" value="Style1">Высокий</option>
                                                    <option style="background: #f39c12; color: #fff;" value="Style2">Средний</option>
                                                    <option style="background: #3498db; color: #fff;" value="Style3" selected>Низкий</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- DateTime Pickers -->
                                    <div class='row top-buffer-2'>
                                        <div class='col-md-6'>
                                            <label for="taskStartTime" class="control-label">Начало:</label>
                                            <div class='input-group date' id='datetimepicker1'>
                                                <input type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}" name="date_begin" class="form-control" id="taskStartTime" required/>
                                                <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-calendar"></span>
                                    </span>
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <label for="taskEndTime" class="control-label">Окончание:</label>
                                            <div class='input-group date' id='datetimepicker2'>
                                                <input type='text' name="date_end" class="form-control" id="taskEndTime" required/>
                                                <span class="input-group-addon">
                                        <span class="glyphicon glyphicon-calendar"></span>
                                    </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row top-buffer-2">
                                        <div class="col-md-12">
                                            <div class="form-group">
                                                <label for="taskAddInfo" class="control-label">Дополнительная информация:</label>
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
                                    <button type="button" class="btn btn-primary" id="modalAddButton">Добавить</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>



    <div id="log"></div>
</div>
<br>
<br>



<script type="text/javascript">
    // Поле дополнительная информация eventID : info
    var addInfoArray = {
    <c:forEach items="${allEvents}" var="event">${event.id}: '${event.info}',</c:forEach>
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
        $('#profilePic').on('click', function() {
            $('.imagepreview').attr('src', $(this).attr('src'));
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
        editable: true,
        selectable: true,
        stack: false,
        multiselect: true,
        dataAttributes: 'all',
        itemsAlwaysDraggable: true,
        zoomMin: 60000, // 1 минута
        zoomMax: 157700000000, //5 лет
        snap: null, // Плавно перемещать элементы
        start: new Date().setHours(0,0,0,0),

        // Добавление задачи
        onAdd: function (item, callback) {
            $('#taskName').val("Новая задача");
            $("#modalAddButton").html('Добавить');
            $("#taskAddInfo").val("");
            document.getElementById('taskStartTime').value = toLocaleDateTimeString(item.start);
            document.getElementById('taskEndTime').value = toLocaleDateTimeString(item.start);
            $('#taskmodal').modal('show');
            document.getElementById('modalAddButton').onclick = function() {
                $('#taskmodal').modal('hide');
                item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                item.start = getDateFromString(document.getElementById('taskStartTime').value);
                item.end = getDateFromString(document.getElementById('taskEndTime').value);
                item.content = document.getElementById('taskName').value;
                $('#taskmodal').modal('hide');
                // Изменение элемента на таймлайне, наверное уже не нужно, т.к. сервер сам перегружает данные, но на всякий пусть останется
                $.ajax({
                    url : '/userAddEventAJAX',
                    type: 'POST',
                    dataType: 'json',
                    data : {
                        name: item.content,
                        priority: item.className,
                        date_begin: toLocaleDateTimeString(item.start),
                        date_end: toLocaleDateTimeString(item.end),
                        info: $("#taskAddInfo").val()
                    },
                    success: function (data) {
                        item.id = data.text;
                        callback(item);
                        createTooltip();
                        addInfoArray[data.text] = $("#taskAddInfo").val();
                    }
                });
            };
            callback(null);
        },

        // Удаление задачи
        onRemove: function (item, callback) {
            //$('#eventForm').attr('action', '/userRemoveEvent/'+item.id);
            //$( "#eventForm" ).submit();
            $.ajax({
                url : '/userRemoveEventAJAX/' + item.id,
                type: 'POST',
                dataType: 'json',
                success: function (data) {
                    addInfoArray[item.id] = "";
                    callback(item);
                }
            });
        },

        // Обновление задачи
        onUpdate: function (item, callback) {
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
                 item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                 item.start = getDateFromString(document.getElementById('taskStartTime').value);
                 item.end = getDateFromString(document.getElementById('taskEndTime').value);
                 item.content = document.getElementById('taskName').value;
                 $('#taskmodal').modal('hide');
                $.ajax({
                    url : '/userChangeEventAJAX/' + item.id,
                    type: 'POST',
                    dataType: 'json',
                    data : {
                        name: item.content,
                        priority: item.className,
                        date_begin: toLocaleDateTimeString(item.start),
                        date_end: toLocaleDateTimeString(item.end),
                        info: $("#taskAddInfo").val()
                    },
                    success: function (data) {
                        addInfoArray[item.id] = $("#taskAddInfo").val();
                        callback(item);
                        createTooltip();
                    }
                });
                 callback(item);
                 createTooltip();
            };
            callback(null);
        },

        // Перемещение задачи
        onMove: function (item, callback) {
            $.ajax({
                url : '/userChangeEventAJAX/' + item.id,
                type: 'POST',
                dataType: 'json',
                data : {
                    name: item.content,
                    priority: item.className,
                    date_begin: toLocaleDateTimeString(item.start),
                    date_end: toLocaleDateTimeString(item.end),
                    info: addInfoArray[item.id]
                },
                success: function (data) {
                    callback(item);
                    createTooltip();
                }
            });
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
    createTooltip();
    // Установка возраста
    $("#userAge").html('Возраст: ' + declOfNum(getAge(getDateFromString('${user.ageDate}' + ' 00:00')), ['год', 'года', 'лет']));
</script>
</body>
<div style="margin-bottom: 4rem;"/>
<%@include file='footer.jsp'%>
</html>
