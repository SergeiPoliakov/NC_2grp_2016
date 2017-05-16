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
    <%@include file='header.jsp' %>

    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" type="text/css"
          href="<%=request.getContextPath()%>/resources/css/jquery.mCustomScrollbar.min.css">

    <link rel="stylesheet" type="text/css" href="resources\css\tlmain.css">

    <script type="text/javascript" src="resources\js\jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="resources\js\moment-with-locales.min.js"></script>
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>
    <script type="text/javascript" src="resources\js\bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/resources/js/jquery.mCustomScrollbar.concat.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/validator.min.js"></script>


    <style>
        .disabled {
            pointer-events: none;
            cursor: default;
        }
    </style>

</head>
<body>

<div class="container top-buffer-20">

    <div class="container">
        <h2>Встречи</h2>
        <ul class="nav nav-tabs nav-justified" style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);">
            <li class="active"><a data-toggle="tab" href="#active">Активные</a></li>
            <li><a data-toggle="tab" href="#closed">Закрытые</a></li>
        </ul>

        <div class="tab-content">
            <div id="active" class="tab-pane fade in active">
                <div class="well bs-component"
                     style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);border-top-left-radius: 0px;border-top-right-radius: 0px;">

                    <div class="row top-buffer-10">
                        <div class="col-sm-12 col-md-6 col-lg-3" id="createMeeting">
                            <div class="card_meetings_list" style="border-style: dashed;border-color: #18bc9c;">
                                <h3 class="card-title text-center">Создать встречу</h3>
                                <div class="hor-align">
                                    <i class="hovicon effect-1 sub-a" id="addMeetingButton"><b
                                            class="fa fa-plus"></b></i>
                                </div>
                            </div>
                        </div>

                        <c:forEach items="${meetings}" var="meeting">
                            <c:if test="${meeting.status eq 'active'}">
                                <div class="col-sm-12 col-md-6 col-lg-3" id="meeting">
                                    <div class="card_meetings_list style_prevu_kit_static mCustomScrollbar"
                                         data-mcs-theme="minimal-dark" style="border-radius: 0.5rem;">

                                        <h3 class="card-title text-center">${meeting.title}</h3>
                                        <div class="profile-userbuttons">

                                            <c:if test="${status eq 'user' or meeting.organizer eq user or meeting.memberUsers.contains(user)}">
                                            <a href="/meeting${meeting.id}">
                                                <button type="button" class="btn btn-info btn-sm"><span
                                                        class="glyphicon glyphicon-user"
                                                        aria-hidden="true"> Просмотр</span></button>
                                            </a>
                                             </c:if>

                                            <a href="/leaveMeeting${meeting.id}" <c:if
                                                    test="${meeting.memberUsers.size() eq 1}"> class="disabled" </c:if> >
                                            <c:if test="${status eq 'user' or meeting.memberUsers.contains(user)}">
                                                <button type="button" class="btn btn-danger btn-xs" <c:if
                                                        test="${meeting.memberUsers.size() eq 1}"> disabled </c:if>  >
                                                    <span class="glyphicon glyphicon-trash"
                                                          aria-hidden="true"> Покинуть</span>
                                                </button>
                                            </c:if>
                                            </a>

                                            <a href="" onclick="sendMessage('meetingRequest', ${meeting.organizer.id}, '${meeting.id}', '${meeting.title}')">
                                            <c:if test="${!(meeting.memberUsers.contains(user))}">
                                                <button type="button" class="btn btn-danger btn-xs">
                                                    <span class="glyphicon glyphicon-trash"
                                                          aria-hidden="true"> Попроситься</span>
                                                </button>
                                            </c:if>
                                            </a>

                                            <!--       <c:if test="${meeting.organizer eq user}">   <a href="/deleteMeeting${meeting.id}"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Удалить</span></button></a>  </c:if>  -->
                                            <c:if test="${meeting.organizer eq user}"> <a
                                                    href="/closeMeeting${meeting.id}">
                                                <button type="button" class="btn btn-danger btn-sm"><span
                                                        class="glyphicon glyphicon-trash"
                                                        aria-hidden="true"> Закрыть</span>
                                                </button>
                                            </a> </c:if>
                                        </div>
                                        <ul class="list-group list-group-my list-group-flush">
                                            <li class="list-group-item">Организатор: <a
                                                    href='/user${meeting.organizer.id}'>${meeting.organizer.name} ${meeting.organizer.middleName} ${meeting.organizer.surname}</a>
                                            </li>
                                            <li class="list-group-item">Начало: ${meeting.date_start}</li>
                                            <li class="list-group-item">Окончание: ${meeting.date_end}</li>
                                            <li class="list-group-item">Описание: ${meeting.info}</li>
                                            <li class="list-group-item" name="tags">Теги: ${meeting.tag}</li>
                                        </ul>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>


            <div id="closed" class="tab-pane fade">
                <div class="well bs-component"
                     style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);border-top-left-radius: 0px;border-top-right-radius: 0px;">
                    <div class="row top-buffer-10">
                        <c:forEach items="${meetings}" var="meeting">
                            <c:if test="${meeting.status eq 'closed'}">
                                <div class="col-sm-12 col-md-6 col-lg-3" id="meeting">
                                    <div class="card_meetings_list style_prevu_kit_static mCustomScrollbar"
                                         data-mcs-theme="minimal-dark">
                                        <h3 class="card-title text-center">${meeting.title}</h3>
                                        <div class="profile-userbuttons">
                                            <a href="/meeting${meeting.id}">
                                                <button type="button" class="btn btn-info btn-sm"><span
                                                        class="glyphicon glyphicon-user"
                                                        aria-hidden="true"> Просмотр</span></button>
                                            </a>
                                            <a href="/deleteMeeting${meeting.id}">
                                                <button type="button" class="btn btn-danger btn-sm"><span
                                                        class="glyphicon glyphicon-trash"
                                                        aria-hidden="true"> Удалить</span></button>
                                            </a>
                                        </div>
                                        <ul class="list-group list-group-my list-group-flush">
                                            <li class="list-group-item">Организатор: <a
                                                    href='/user${meeting.organizer.id}'>${meeting.organizer.name} ${meeting.organizer.middleName} ${meeting.organizer.surname}</a>
                                            </li>
                                            <li class="list-group-item">Начало: ${meeting.date_start}</li>
                                            <li class="list-group-item">Окончание: ${meeting.date_end}</li>
                                            <li class="list-group-item">Описание: ${meeting.info}</li>
                                            <li class="list-group-item" name="tags">Теги: ${meeting.tag}</li>
                                        </ul>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <!-- Форма для создания новой встречи -->
    <div id="meetingmodal" class="modal fade">
        <div class="modal-dialog">

            <ul class="nav nav-tabs nav-justified" style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);">
                <li class="active"><a data-toggle="tab" href="#fixedMeeting">Фиксированная</a></li>
                <li><a data-toggle="tab" href="#floatingMeeting">Плавающая</a></li>
            </ul>

            <div class="tab-content">
                <div id="fixedMeeting" class="tab-pane fade in active">
                    <div class="well bs-component"
                         style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);border-top-left-radius: 0px;border-top-right-radius: 0px;">

                        <form id="eventForm" name="creation" action="/addNewMeeting" method="post" data-toggle="validator">
                            <div class="modal-content">
                                <!-- Заголовок модального окна -->
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-hidden="true">&times;</button>
                                    <h4 class="modal-title text-center">Создание новой встречи</h4>
                                </div>
                                <!-- Основное содержимое модального окна -->
                                <div class="modal-body">
                                    <div class='row '>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="display: inline;">
                                                <label for="meetingTitle" class="control-label">Название:</label>
                                                <input type="text" class="form-control" name="title" id="meetingTitle"
                                                       placeholder="Введите название встречи">
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="display: inline;">
                                                <label for="meetingTag" class="control-label">Теги:</label>
                                                <input type="text" class="form-control" name="tag" id="meetingTag"
                                                       placeholder="Введите теги">
                                            </div>
                                        </div>
                                    </div>
                                    <!-- DateTime Pickers -->
                                    <div class='row top-buffer-2'>
                                        <div class='col-md-6'>
                                            <label for="taskStartTime" class="control-label">Начало:</label>
                                            <div class='input-group date' id='datetimepicker1'>
                                                <input required data-toggle="tooltip" type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}"
                                                       name="date_start" class="form-control" id="taskStartTime"/>
                                                <span class="input-group-addon">
                                                        <span class="glyphicon glyphicon-calendar"></span>
                                                    </span>
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <label for="taskEndTime" class="control-label">Окончание:</label>
                                            <div class='input-group date' id='datetimepicker2'>
                                                <input required data-toggle="tooltip" type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}"
                                                       name="date_end" class="form-control" id="taskEndTime"/>
                                                <span class="input-group-addon">
                                                        <span class="glyphicon glyphicon-calendar"></span>
                                                    </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row top-buffer-2">
                                        <div class="col-md-12">
                                            <div class="form-group">
                                                <label for="taskAddInfo" class="control-label">Дополнительная
                                                    информация:</label>
                                                <textarea type='text' name="info"
                                                          class="form-control noresize textarea-for-modal" rows="5"
                                                          id="taskAddInfo"></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!-- Футер модального окна -->
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                                    <button type="submit" class="btn btn-primary" id="modalAddButton">Добавить
                                    </button>
                                </div>
                            </div>
                        </form>

                    </div>
                </div>


                <div id="floatingMeeting" class="tab-pane fade">
                    <div class="well bs-component"
                         style="box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.176);border-top-left-radius: 0px;border-top-right-radius: 0px;">

                        <form id="formFloatingMeeting" name="creation" action="/addNewFloatingMeeting" method="post" data-toggle="validator">
                            <div class="modal-content">
                                <!-- Заголовок модального окна -->
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-hidden="true">&times;</button>
                                    <h4 class="modal-title text-center">Создание новой встречи</h4>
                                </div>
                                <!-- Основное содержимое модального окна -->
                                <div class="modal-body">
                                    <div class='row '>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="display: inline;">
                                                <label for="meetingTitle2" class="control-label">Название:</label>
                                                <input type="text" class="form-control" name="title" id="meetingTitle2"
                                                       placeholder="Введите название встречи">
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <div class="input-group" style="display: inline;">
                                                <label for="meetingTag2" class="control-label">Теги:</label>
                                                <input type="text" class="form-control" name="tag" id="meetingTag2"
                                                       placeholder="Введите теги">
                                            </div>
                                        </div>
                                    </div>
                                    <!-- DateTime Pickers -->
                                    <div class='row top-buffer-2'>
                                        <div class='col-md-6'>
                                            <label for="taskStartTime" class="control-label">Начало:</label>
                                            <div class='input-group date' id='datetimepicker3'>
                                                <input required data-toggle="tooltip" type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}"
                                                       name="date_start" class="form-control" id="taskStartTime2"/>
                                                <span class="input-group-addon">
                                                            <span class="glyphicon glyphicon-calendar"></span>
                                                        </span>
                                            </div>
                                        </div>
                                        <div class='col-md-6'>
                                            <label for="taskEndTime" class="control-label">Окончание:</label>
                                            <div class='input-group date' id='datetimepicker4'>
                                                <input required data-toggle="tooltip" type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}"
                                                       name="date_end" class="form-control" id="taskEndTime2"/>
                                                <span class="input-group-addon">
                                                            <span class="glyphicon glyphicon-calendar"></span>
                                                        </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class='row top-buffer-2'>
                                        <div class='col-md-6'>
                                            <label for="taskDuration" class="control-label">Продолжительность в
                                                минутах:</label>
                                            <input required data-toggle="tooltip" type='text' name="duration" class="form-control" id="taskDuration"/>
                                        </div>
                                        <div class='col-md-6'>
                                            <label for="taskDecisionTime" class="control-label">Согласовать до:</label>
                                            <div class='input-group date' id='datetimepicker5'>
                                                <input required data-toggle="tooltip" type='text' pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}"
                                                       name="date_edit" class="form-control" id="taskDecisionTime"/>
                                                <span class="input-group-addon">
                                                            <span class="glyphicon glyphicon-calendar"></span>
                                                        </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row top-buffer-2">
                                        <div class="col-md-12">
                                            <div class="form-group">
                                                <label for="taskAddInfo2" class="control-label">Дополнительная
                                                    информация:</label>
                                                <textarea type='text' name="info"
                                                          class="form-control noresize textarea-for-modal" rows="5"
                                                          id="taskAddInfo2"></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!-- Футер модального окна -->
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                                    <button type="submit" class="btn btn-primary" id="modalAddButton2">Добавить
                                    </button>
                                </div>
                            </div>
                        </form>

                    </div>
                </div>
            </div>

        </div>
    </div>

</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/tags.js"></script>

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
        $('#datetimepicker3').datetimepicker({
            locale: 'ru',
            viewMode: 'months',
            useCurrent: false
        });
        $('#datetimepicker4').datetimepicker({
            locale: 'ru',
            viewMode: 'months',
            useCurrent: false
        });
        $('#datetimepicker5').datetimepicker({
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
        $("#datetimepicker3").on("dp.change", function (e) {
            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
        });
        $("#datetimepicker4").on("dp.change", function (e) {
            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
        });
        $("#datetimepicker5").on("dp.change", function (e) {
            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
        });
    });

    document.getElementById('addMeetingButton').onclick = function () {
        $('#meetingmodal').modal('show');
        $('#meetingmodal').on('shown.bs.modal', function () {
            $('#meetingTitle').val("Новая встреча");
            $('#meetingTitle2').val("Новая встреча");
            $('#meetingTitle').focus();
            $('#meetingTitle2').focus();
            $('#meetingTitle').select();
            $('#meetingTitle2').select();

        });
    };

    document.getElementById('taskDuration').onkeyup = function () {
        $.ajax({
            url : '/checkMeetingAJAX',
            type: 'POST',
            dataType: 'json',
            data : {
                date_start: $("#taskStartTime2").val(),
                date_end: $("#taskEndTime2").val(),
                duration: $("#taskDuration").val()
            },
            success: function (data) {
                var check = JSON.parse(data.text);
                if (check === false) {
                    $("#modalAddButton2").attr("disabled", true);
                } else $("#modalAddButton2").attr("disabled", false);
            }
        });
    }

</script>

<script type="text/javascript">

    // Преобразовать дату в строку формата DD.MM.YYYY hh:mm
    function toLocaleDateTimeString(dateString) {
        var eventTime = dateString.toLocaleTimeString();
        var eventTimeAfter = eventTime.substring(0, eventTime.length - 3);
        if (eventTimeAfter.length < 5)
            eventTimeAfter = '0' + eventTimeAfter;
        var startDate = dateString.toLocaleDateString() + ' ' + eventTimeAfter;
        return startDate;
    }
    // Получить дату из строки вида DD.MM.YYYY hh:mm
    function getDateFromString(dateString) {
        var reggie = /(\d{2}).(\d{2}).(\d{4}) (\d{2}):(\d{2})/;
        var dateArray = reggie.exec(dateString);
        var dateObject = new Date(
            (+dateArray[3]),
            (+dateArray[2]) - 1, // Careful, month starts at 0!
            (+dateArray[1]),
            (+dateArray[4]),
            (+dateArray[5])
        );
        return dateObject;
    }
</script>

</body>
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp' %>
</html>
