<%@ page import="entities.User" %>
<%@ page import="service.converter.Converter" %>
<%@ page import="service.LoadingServiceImp" %>
<%@ page import="service.UserServiceImp" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="service.notifications.NotificationService" %>
<%@ page import="service.notifications.UsersNotifications" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="WebSocket.SocketMessage" %>
<%@ page import="com.google.gson.Gson" %><%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 05.02.2017
  Time: 14:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<!DOCTYPE html>
<html>
<head>

    <!--WEB SOCKET -->
    <%
        User user = new User();
        try {
            user = new Converter().ToUser(new LoadingServiceImp().getDataObjectByIdAlternative(new UserServiceImp().getObjID(new UserServiceImp().getCurrentUsername())));
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("Ошибка: " + e.toString());
        }
    %>



    <script src="<%=request.getContextPath()%>/resources/js/sockjs-0.3.4.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/js/stomp.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/js/app.js" type="text/javascript"></script>
    <script type="text/javascript">
        var gSenderID = '<%=user.getId()%>';
        var gSenderName = '<%=user.getName()%>' + " " + '<%=user.getSurname()%>';
        var gSenderPic = '<%=user.getPicture()%>';
        // Преобразовать дату в строку формата DD.MM.YYYY hh:mm
        function toLocaleDateTimeString(dateString){
            var eventTime = dateString.toLocaleTimeString();
            var eventTimeAfter = eventTime.substring(0, eventTime.length-3);
            if (eventTimeAfter.length < 5)
                eventTimeAfter = '0' + eventTimeAfter;
            var startDate = dateString.toLocaleDateString() + ' ' + eventTimeAfter;
            return startDate;
        }

        var stompClient = null;

        // Подключение
        var socket = new SockJS('/notify'+gSenderID); // Подписка на канал (ну нет, но чёт типа того, тут короче свой юзер id)
        stompClient = Stomp.over(socket);
        stompClient.connect('guest', function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/notifications'+gSenderID, function(greeting){ // Подписка на канал
                addNotification(JSON.parse(greeting.body));
            });
        });

        function disconnect() {
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }

        <!--type = friendrequest | meetingInvite -->
        function sendMessage(type, recieverID, meetingID, meetingName) {
            var JSONMessage = JSON.stringify({
                'type': type,
                'senderID': gSenderID,
                'recieverID': recieverID,
                'senderName': gSenderName,
                'additionalID': meetingID,
                'meetingName': meetingName,
                'senderPic': gSenderPic,
                'isSeen' : "active",
                'date': toLocaleDateTimeString(new Date())
            });
            if (type === 'friendRequest') {
                $.ajax({
                    url: '/checkPrivacyFriendForNotification',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        senderID: gSenderID,
                        recieverID: recieverID,
                    },
                    success: function (data) {
                        var check = JSON.parse(data.text);
                        if (check === true) {
                            stompClient.send("/app/notify" + recieverID, {}, JSONMessage); // Тут айди юзера, которому отправляется уведомление
                        }
                    }
                });
            }
            if (type === "meetingInvite") {
                $.ajax({
                    url: '/checkPrivacyMeetingForNotification',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        senderID: gSenderID,
                        recieverID: recieverID,
                        additionalID: meetingID,
                    },
                    success: function (data) {
                        var check = JSON.parse(data.text);
                        if (check === true) {
                            stompClient.send("/app/notify" + recieverID, {}, JSONMessage); // Тут айди юзера, которому отправляется уведомление
                        }
                    }
                });
            }
            if (type === "meetingRequest") {
                $.ajax({
                    url: '/addBeggingUser',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        senderID: gSenderID,
                        additionalID: meetingID,
                    },
                    success: function (data) {
                        var check = JSON.parse(data.text);
                        if (check === true) {
                            stompClient.send("/app/notify" + recieverID, {}, JSONMessage); // Тут айди юзера, которому отправляется уведомление
                        }
                    }
                });
            }
            if (type === "infoFriendAccept") {
                stompClient.send("/app/notify" + recieverID, {}, JSONMessage); // Тут айди юзера, которому отправляется уведомление
            }

        }
    </script>

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/skywalk-docs.min.css">
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-notifications.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/footer.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/select2.min.css">

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tlmain.css">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/search.css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/docs.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/header.js"></script>




    <!-- <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->


    <!-- <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->
    <link href="<%=request.getContextPath()%>/resources/load/css/fileinput.css" media="all" rel="stylesheet" type="text/css"/>
    <link href="<%=request.getContextPath()%>/resources/load/themes/explorer/theme.css" media="all" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="<%=request.getContextPath()%>/resources/load/js/plugins/sortable.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/load/js/fileinput.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/load/js/locales/ru.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/load/themes/explorer/theme.js" type="text/javascript"></script>
    <!-- <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" type="text/javascript"></script> -->

    <!--  <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script> -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/select2.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/ru.js"></script>

    <!--  Для статистик и бокового меню со статистиками: -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.touchSwipe.min.js"></script>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/search.js"></script>


</head>

<body>
<nav class="navbar navbar-default" role="navigation" style="border-radius: 0px 0px 0px 0px;">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/main-login">Netcracker</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="dropdown dropdown-notifications" id="notificationDrop">
                    <a class="dropdown-toggle" id="notificationDropa">
                        <i data-count="0" class="glyphicon glyphicon-bell notification-icon" id ="notificationCount"></i>
                    </a>
                    <div class="dropdown-container" style="top: 5.8rem !important;">
                        <div class="dropdown-toolbar">
                            <div class="dropdown-toolbar-actions" id ="markAllAsRead">
                                <a style="cursor: pointer;">Пометить всё как просмотренное</a>
                            </div>
                            <h3 class="dropdown-toolbar-title" id ="notificationSecondCounter">Уведомления (0)</h3>
                        </div><!-- /dropdown-toolbar -->
                        <ul class="dropdown-menu" id="notificationHolder">
                            <li id ="notificationEmpty" class="notification" id="14" recieverid="10003">
                                <p class="notification-title" style="text-align: center">Нет уведомлений</p>
                            </li>
                        </ul>
                        <div class="dropdown-footer text-center">
                            <a style="cursor: pointer;">  </a>
                        </div><!-- /dropdown-footer -->
                    </div><!-- /dropdown-container -->
                </li><!-- /dropdown -->

                <li><a href="/main-login">Расписание</a></li>
                <li><a href="/meetings">Встречи</a></li>
                <li><a href="/allFriends">Друзья</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Меню <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li ><a href="/allEvent">Список событий</a></li>
                        <li ><a href="/userOptimizerProblem">Требующие внимания</a></li>
                        <li ><a href="/allUser">Все пользователи</a></li>
                        <li ><a href="/statistics">Статистика</a></li>
                    </ul>
                </li>
            </ul>



            <div class="form-group navbar-form navbar-left" style="padding-top: 0.6rem;">
                <div class="form-group">

                    <select multiple type="text" class="form-control searchBox" name="name" id="searchInput" style="width: 30rem!important; overflow-y: auto !important;">
                    </select>

                    <button class="btn btn-success btn-sm" onclick="getFind(searchText)">
                        <span class="glyphicon glyphicon-search"></span> Поиск
                    </button>
                </div>



                <div class="form-group" style="width: 28.7rem;">
                <div class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b>Настройки поиска</b> <span class="caret"></span></a>
                    <ul id="search-dp" class="dropdown-menu">
                        <div>
                            <div class="form-group" style="padding-left: 1rem;padding-right: 1rem;padding-top: 1rem;">
                                <label for="object">Что ищем?</label>
                                <div id="object" class="funkyradio">

                                    <div class="funkyradio-success">
                                        <input type="radio" name="checkObject" id="checkName" value="name" checked="checked"  >
                                        <label class="radio-inline" for="checkUser" style="margin-top: 0.5rem;">Пользователя по имени</label>
                                    </div>

                                    <div class="funkyradio-success">
                                        <input type="radio" name="checkObject" id="checkUser" value="user"  >
                                        <label class="radio-inline" for="checkUser" style="margin-top: 0.5rem;">Пользователя по интересам</label>
                                    </div>

                                    <div class="funkyradio-success">
                                        <input type="radio" name="checkObject" id="checkMeeting" value="meeting"  />
                                        <label class="radio-inline" for="checkMeeting" style="margin-top: 0.5rem;">Встречу по тегам</label>
                                    </div>

                                </div>
                            </div>
                            <div class="form-group" style="padding-left: 1rem;padding-right: 1rem;padding-bottom: 1rem;padding-top: 1rem;">
                                <label for="logic">Критерий поиска</label>
                                <div id="logic" class="funkyradio">
                                    <div class="funkyradio-success">
                                        <div class="form-group" style="padding-right: 3rem;">
                                            <input type="radio" name="checkLogic" id="checkOR" value="or" checked="checked"  >
                                            <label class="radio-inline" for="checkOR" style="margin-top: 0.5rem;padding-right: 2rem;">OR</label>
                                        </div>

                                        <div class="form-group">
                                            <input type="radio" name="checkLogic" id="checkAND" value="and"  />
                                            <label class="radio-inline" for="checkAND" style="margin-top: 0.5rem;padding-right: 1rem;">AND</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </ul>
                </div>
                </div>
            </div>





            <ul class="nav navbar-nav navbar-right">
                <li><a href="/allUnconfirmedFriends" id="result_text_friend"></a></li> <!-- AJAX "Друзья: 5" -->
                <li><a href="/allUnreadMessages" id="result_text_message"></a></li> <!-- AJAX "Сообщения: 10" -->
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b><span class="glyphicon glyphicon-user"></span> <sec:authentication property="principal.username"/></b> <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="/profile">Профиль</a></li>
                        <li class="divider"></li>
                        <li><a href="#myModal" data-toggle="modal">Написать нам</a></li>
                        <li class="divider"></li>
                        <li><a href="/logout">Выход</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <!--  <div class="modal-content"> -->
        <div class=".col-xs-6 .col-md-4">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="text-center">
                        <h3><i class="fa fa-user-circle fa-4x"></i></h3>
                        <h2 class="text-center">Проблемы?</h2>
                        <p>Вы можете написать нам.</p>
                        <div class="panel-body">

                            <form class="form" method="post" action="/sendUsMessage">
                                <fieldset>


                                    <div class="form-group">
                                        <div class="input-group">
                                            <span class="input-group-addon"><i class="glyphicon glyphicon-user color-blue"></i></span>
                                            <input name="nameUser" id="nameUser" placeholder="Ваше имя" class="form-control" type="text" >
                                        </div>
                                    </div>


                                    <div class="form-group">
                                        <div class="input-group">
                                            <span class="input-group-addon"><i class="glyphicon glyphicon-envelope color-blue"></i></span>
                                            <input id="emailInput" name="email" placeholder="email адрес" class="form-control" type="email" oninvalid="setCustomValidity('Пожалуйста введите корректный email!')" onchange="try{setCustomValidity('')}catch(e){}" required="">
                                        </div>
                                    </div>

                                    <div class="form-group">

                                            <label for="TextArea" class="control-label">Ваше сообщение</label>
                                            <textarea  rows="3" class="form-control" name="text" id="TextArea"></textarea>
                                        
                                    </div>


                                    <div class="form-group">
                                        <input class="btn btn-lg btn-primary btn-block" id="sendMessage" value="Написать" type="submit">
                                    </div>
                                </fieldset>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--  </div> -->
    </div>
</div>


<script type="text/javascript">
    var searchText = "";
    $(".searchBox").select2({
        ajax: {
            url: "/getTags",
            type: 'POST',
            dataType: 'json',
            contentType: "application/json",
            mimeType: 'application/json',
            cache: true,
            data: function (params) {
                var type = $('input[name="checkObject"]:checked').val();
                var operation = $('input[name="checkLogic"]:checked').val();
                var retval = JSON.stringify({
                    type: type,       // name | user | meeting
                    operation: operation,   // and | or
                    text: params.term
                });
                searchText = params.term;
                return retval;
            },
            processResults: function (data) {
                for (var i = 0; i < data.length; i++) {
                    data[i].id = data[i].text;
                    data[i].text += " ";
                }
                return {
                    results: data
                };
            }
        },
        createTag: function (params) {
            var term = $.trim(params.term);
            if (term === '') {
                return null;
            }
            term += " ";
            return {
                id: term,
                text: term,
            }
        },
        tags: true,
        tokenSeparators: [',', ' '],
        minimumInputLength: 1,
        language: "ru",
        placeholder: ' Поиск...'
    });

</script>

<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/notifications.js"></script>
<%
    try {
        int currentUserID = user.getId();
        UsersNotifications usersNotifications = UsersNotifications.getInstance();
        ArrayList<SocketMessage> notifications =  usersNotifications.getNotifications(currentUserID);

        if (notifications != null) {
            out.println("<script type=\"text/javascript\">");
            for (SocketMessage notification : notifications) {
                out.println("addNotification(JSON.parse('" + new Gson().toJson(notification) + "'));");
            }
            out.println("</script>");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
%>
</body>

</html>
