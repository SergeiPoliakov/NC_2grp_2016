<%--
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

    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/skywalk-docs.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-notifications.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tlmain.css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/docs.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/header.js"></script>

</head>

<body>


<nav class="navbar navbar-default navbar-inverse" role="navigation">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Netcracker</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="dropdown dropdown-notifications" id="notificationDrop">
                    <a class="dropdown-toggle" id="notificationDropa">
                        <i data-count="4" class="glyphicon glyphicon-bell notification-icon" id ="notificationCount"></i>
                    </a>
                    <div class="dropdown-container" style="margin-top: 1rem;">
                        <div class="dropdown-toolbar">
                            <div class="dropdown-toolbar-actions">
                                <a href="#">Пометить всё как просмотренное</a>
                            </div>
                            <h3 class="dropdown-toolbar-title" id ="notificationSecondCounter">Уведомления (4)</h3>
                        </div><!-- /dropdown-toolbar -->
                        <ul class="dropdown-menu">
                            <li class="notification active">
                                <div class="media">
                                    <div class="media-left">
                                        <div class="media-object">
                                            <img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name" />
                                        </div>
                                    </div>
                                    <div class="media-body">
                                        <strong class="notification-title"><a href="#">Вася Пупкин</a> пригласил Вас на встречу <a href="#">Новая встреча</a></strong>

                                        <div class="notification-meta">
                                            <small class="timestamp">27. 11. 2017, 15:00</small>
                                        </div>
                                        <div class="notification-userbuttons">
                                            <a href="#acceptInvite"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Принять</span> </button></a>
                                            <a href="#declineInvite"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отказаться</span></button></a>
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="notification active">
                                <div class="media">
                                    <div class="media-left">
                                        <div class="media-object">
                                            <img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name" />
                                        </div>
                                    </div>
                                    <div class="media-body">
                                        <strong class="notification-title"><a href="#">Василий Степанов</a> принял вашу заявку в друзья</strong>
                                        <div class="notification-meta">
                                            <small class="timestamp">27. 10. 2015, 08:00</small>
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="notification active">
                                <div class="media">
                                    <div class="media-left">
                                        <div class="media-object">
                                            <img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name" />
                                        </div>
                                    </div>
                                    <div class="media-body">
                                        <strong class="notification-title"><a href="#">Иванов Иван</a> отправил Вам заявку в друзья</strong>
                                        <div class="notification-meta">
                                            <small class="timestamp">01. 09. 2015, 08:00</small>
                                        </div>
                                        <div class="notification-userbuttons">
                                            <a href="#acceptFriend"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Добавить</span> </button></a>
                                            <a href="#declineFriend"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a>
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="notification active">
                                <div class="media">
                                    <div class="media-left">
                                        <div class="media-object">
                                            <img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name" />
                                        </div>
                                    </div>
                                    <div class="media-body">
                                        <strong class="notification-title"><a href="#Ыуыу">Семен Станиславович</a> хочет принять участие в встрече <a href="#">Новая встреча 2</a></strong>
                                        <div class="notification-meta">
                                            <small class="timestamp">27. 11. 2017, 15:00</small>
                                        </div>
                                        <div class="notification-userbuttons">
                                            <a href="#inviteAtMeeting"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Пригласить</span> </button></a>
                                            <a href="#declineRequest"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a>
                                        </div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                        <div class="dropdown-footer text-center">
                            <a href="#">Просмотреть все</a>
                        </div><!-- /dropdown-footer -->
                    </div><!-- /dropdown-container -->
                </li><!-- /dropdown -->

                <li><a href="/main-login">Расписание</a></li>
                <li><a href="/meetings">Встречи</a></li>
                <li><a href="/allEvent">События</a></li>
                <li><a href="/allFriends">Друзья</a></li>
                <li><a href="/allUser">Пользователи</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Меню <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li ><a href="/addEvent">Добавить событие</a></li>
                        <li ><a href="/allEvent">Список событий</a></li>
                        <li><a href="/allFriends">Список друзей</a></li>
                        <li ><a href="/allUser">Все пользователи</a></li>
                    </ul>
                </li>
            </ul>
            <form action="/searchUser" class="navbar-form navbar-left" role="search" method="post">
                <div class="form-group">
                    <input type="text" class="form-control" name="name" placeholder="Search">
                </div>
                <button type="submit" class="btn btn-default">Поиск</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/allUnconfirmedFriends" id="result_text_friend"></a></li> <!-- AJAX "Друзья: 5" -->
                <li><a href="/allUnreadMessages" id="result_text_message"></a></li> <!-- AJAX "Сообщения: 10" -->
                <li>
                    <p class="navbar-text">Привет, </p>
                </li>

                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b> <sec:authentication property="principal.username" /></b> <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="/profile">Профиль</a></li>
                        <li><a href="/logout">Выход</a></li>
                    </ul>
                </li>
            </ul>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>
 <!-- Notification -->
<script type="text/javascript">
    //Скрыть/показать уведомления
    $(document).click(function(event) {
        if(!$(event.target).closest('#notificationDrop').length) {
            if ($( "#notificationDrop" ).hasClass("open"))
                $( "#notificationDrop" ).toggleClass( "open" );
        }
        else
        if (!$( "#notificationDrop" ).hasClass("open"))
            $( "#notificationDrop" ).toggleClass( "open" );
    })
    $( "#notificationDropa" ).click(function(event) {
        $( "#notificationDrop" ).toggleClass( "open" );
        event.stopPropagation();
    });

    // Изменение состояния уведомления при наведении на него
    $( ".notification" ).hover(
        function() { // mouse enter
            if ( $( this ).hasClass( "active" ) ){
                $( this ).removeClass( "active" );
                var notificationCount = $( "#notificationCount" ).attr( "data-count") - 1;
                $( "#notificationCount" ).attr( "data-count", notificationCount);
                //$( "#notificationSecondCounter" ).html("Уведомления (" + notificationCount + ")");
            }
        },
        function() {} // mouse leave
    );
</script>
</body>
</html>
