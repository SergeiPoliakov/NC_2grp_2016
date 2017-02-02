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
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap4.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\tipped.css">
    <link rel="stylesheet" type="text/css" href="resources\css\vis.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\tlmain.css">

    <script type="text/javascript" src="resources\js\jquery-1.9.1.min.js"> </script>
    <script type="text/javascript" src="resources\js\moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="resources\js\enscroll-0.6.2.min.js"> </script>
    <script type="text/javascript" src="resources\js\tipped.js"> </script>
    <script type="text/javascript" src="resources\js\vis.js"> </script>
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>
    <script type="text/javascript" src="resources\js\bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="resources\js\bootstrap-select.min.js"> </script>
</head>
<body>
<c:forEach items="${allEvents}" var="event">
    {id: ${event.id}, content: '${event.name}', start: '${event.date_begin}', end: '${event.date_end}', className: '${event.priority}'}

</c:forEach>

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
                    <li class="list-group-item">Возраст: ${user.ageDate}</li>
                    <li class="list-group-item">Город: ${user.country}</li>
                    <li class="list-group-item">Пол: ${user.sex.toLowerCase()}</li>
                    <li class="list-group-item">О себе: ${user.additional_field}</li>
                </ul>
                <button type="button" class="btn btn-success btn-block">Отслеживать</button>
            </div>
        </div>
        <!-- Список шаблонов задач -->
        <div class="col-md-6">
            <div class="card pull-right" style="width: 30rem;">
                <div class="card-title">
                    <h3 class="text-center" id="cardsholder">Ваши шаблоны</h3>
                </div>
                <ul class="list-group list-group-my list-group-flush text-center nav" id="cardsholderItems">
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
                                <span class="input-group-addon" id="basic-addon1">Название</span>
                                <input type="text" class="form-control" id="taskName" placeholder="Введите название задачи">
                            </div>
                        </div>
                        <div class='col-md-6'>
                            <div class="input-group">
                                <span class="input-group-addon">Приоритет</span>
                                <select id="taskPriority" class="selectpicker form-control" title="Выберите приоритет">
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
                                <span class="input-group-addon" id="basic-addon1">Начало</span>
                                <input type='text' class="form-control" id="taskStartTime" />
                                <span class="input-group-addon">
							<span class="glyphicon glyphicon-calendar"></span>
						</span>
                            </div>
                        </div>
                        <div class='col-md-6'>
                            <div class='input-group date' id='datetimepicker2'>
                                <span class="input-group-addon" id="basic-addon1">Окончание</span>
                                <input type='text' class="form-control" id="taskEndTime" />
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
                                <textarea class="form-control noresize textarea-for-modal" rows="5" id="taskAddInfo"></textarea>
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
        </div>
    </div>
    <div id="log"></div>
</div>

<script type="text/javascript">
    // Scrollbar для кардхолдера
    $('#cardsholderItems').enscroll({
        showOnHover: false,
        verticalTrackClass: 'track3',
        verticalHandleClass: 'handle3'
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
        {id: 1, content: 'Работа', start: new Date(2017,1,2), end: new Date(2017,1,2,8), className: 'Style1'},
        {id: 2, content: 'Сон', start: new Date(2017,1,2,8), end: new Date(2017,1,2,17,30), className: 'Style3'},
        {id: 3, content: 'Прогулка', start: new Date(2017,1,2,18), end: new Date(2017,1,2,21,50), className: 'Style2'},
        {id: 4, content: 'Еще что то', start: new Date(2017,1,2,21,50,1), end: new Date(2017,1,2,24), className: 'Style3'},
        <c:forEach items="${allEvents}" var="event">
            {id: ${event.id}, content: '${event.name}', start: new Date(getDateFromString('${event.date_begin}')), end: new Date(getDateFromString('${event.date_end}')), className: '${event.priority}'}
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

        // Добавление задачи
        onAdd: function (item, callback) {
            $('#taskName').val("Новая задача");
            $("#modalAddButton").html('Добавить');
            document.getElementById('taskStartTime').value = toLocaleDateTimeString(item.start);
            document.getElementById('taskEndTime').value = toLocaleDateTimeString(item.start);
            $('#taskmodal').modal('show');
            document.getElementById('modalAddButton').onclick = function() {
                item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                item.start = getDateFromString(document.getElementById('taskStartTime').value);
                item.end = getDateFromString(document.getElementById('taskEndTime').value);
                item.content = document.getElementById('taskName').value;
                $('#taskmodal').modal('hide');
                callback(item);
                createTooltip();
            };
            callback(null);
        },

        // Удаление задачи
        onRemove: function (item, callback) {
            callback(item);
        },

        // Обновление задачи
        onUpdate: function (item, callback) {
            $("#modalAddButton").html('Сохранить');
            $('#taskStartTime').val(toLocaleDateTimeString(item.start));
            $('#taskEndTime').val(toLocaleDateTimeString(item.end));
            $('#taskName').val(item.content);
            $('#taskPriority').val(item.className);
            $('#taskPriority').selectpicker('refresh');
            $('#taskmodal').modal('show');
            document.getElementById('modalAddButton').onclick = function() {
                item.className = $('#taskPriority').val() =='' ? 'Style3' : $('#taskPriority').val();
                item.start = getDateFromString(document.getElementById('taskStartTime').value);
                item.end = getDateFromString(document.getElementById('taskEndTime').value);
                item.content = document.getElementById('taskName').value;
                $('#taskmodal').modal('hide');
                callback(item);
                createTooltip();
            };
            callback(null);
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
</script>
</body>
</html>
