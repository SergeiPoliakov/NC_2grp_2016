
<%--
  Created by IntelliJ IDEA.
  User: Костя
  Date: 07.02.2017
  Time: 21:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Ваши встречи</title>
    <%@include file='header.jsp'%>

    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap-datetimepicker.min.css">

    <link rel="stylesheet" type="text/css" href="resources\css\tlmain.css">

    <script type="text/javascript" src="resources\js\jquery-1.9.1.min.js"> </script>
    <script type="text/javascript" src="resources\js\moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>
    <script type="text/javascript" src="resources\js\bootstrap-datetimepicker.min.js"></script>

</head>
<body>

<div class="container top-buffer-20">
    <div class="row">
        <button class="btn btn-success" id="addMeetingButton">Добавить</button>
    </div>
    <div class="row top-buffer-10">
        <c:forEach items="${meetings}" var="meeting">
        <div class="col-md-3 col-sm-4" id="meeting">
            <div class="card_meetings_list style_prevu_kit_static">
                <h3 class="card-title text-center">${meeting.title}</h3>
                <div class="profile-userbuttons">
                    <a href="/meeting${meeting.id}"><button type="button" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-user" aria-hidden="true"> Просмотр</span> </button></a>
                    <a href="/leaveMeeting${meeting.id}"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Покинуть</span></button></a>
                </div>
                <ul class="list-group list-group-my list-group-flush">
                    <li class="list-group-item">Организатор: <a href='/viewProfile/${meeting.organizer.id}'>${meeting.organizer.name} ${meeting.organizer.middleName} ${meeting.organizer.surname}</a></li>
                    <li class="list-group-item">Начало: ${meeting.date_start}</li>
                    <li class="list-group-item">Окончание: ${meeting.date_end}</li>
                    <li class="list-group-item">Описание: ${meeting.info}</li>
                    <li class="list-group-item">Теги: ${meeting.tag}</li>
                </ul>
            </div>
        </div>
        </c:forEach>
    </div>
    <!-- Форма для создания новой встречи -->
    <div id="meetingmodal" class="modal fade">
        <div class="modal-dialog">
            <form id="eventForm" name="creation" action="/addMeeting" method="post">
                <div class="modal-content">
                    <!-- Заголовок модального окна -->
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title text-center">Создание новой встречи</h4>
                    </div>
                    <!-- Основное содержимое модального окна -->
                    <div class="modal-body">
                        <div class='row '>
                            <div class='col-md-6'>
                                <div class="input-group">
                                    <span class="input-group-addon">Название</span>
                                    <input type="text" class="form-control" name="title" id="meetingTitle" placeholder="Введите название задачи">
                                </div>
                            </div>
                            <div class='col-md-6'>
                                <div class="input-group">
                                    <span class="input-group-addon">Теги</span>
                                    <input type="text" class="form-control" name="tag" id="meetingTag" placeholder="Введите теги">
                                </div>
                            </div>
                        </div>
                        <!-- DateTime Pickers -->
                        <div class='row top-buffer-2'>
                            <div class='col-md-6'>
                                <div class='input-group date' id='datetimepicker1'>
                                    <span class="input-group-addon">Начало</span>
                                    <input type='text' name="date_start" class="form-control" id="taskStartTime" />
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
</div>

<%@include file='footer.jsp'%>

<script type="text/javascript">

    // datetimepickers настройка
    $(function () {
        $('#datetimepicker1').datetimepicker({
            locale: 'ru',
            viewMode: 'months'
            //format: "DD/MM/YYYY hh:mm"
        });
        $('#datetimepicker2').datetimepicker({
            locale: 'ru',
            viewMode: 'months',
            useCurrent: false
        });
        $("#datetimepicker1").on("dp.change", function (e) {
            $('#datetimepicker2').data("DateTimePicker").minDate(e.date);
        });
        $("#datetimepicker2").on("dp.change", function (e) {
            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
        });
    });

    document.getElementById('addMeetingButton').onclick = function() {
        $('#meetingmodal').modal('show');
        $('#meetingmodal').on('shown.bs.modal', function () {
            $('#meetingTitle').val("Новая встреча");
            $('#meetingTitle').focus();
            $('#meetingTitle').select();
        });
    };

</script>

<script type="text/javascript">

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
</script>
</body>
</html>
