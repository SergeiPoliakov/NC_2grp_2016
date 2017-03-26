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

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/skywalk-docs.min.css">
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-notifications.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/footer.css">

    <!-- Если будут какие траблы - возможно из за tlmain..css -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tlmain.css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/docs.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/header.js"></script>

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
                    <div class="dropdown-container" style="margin-top: 1rem;">
                        <div class="dropdown-toolbar">
                            <div class="dropdown-toolbar-actions">
                                <a href="#">Пометить всё как просмотренное</a>
                            </div>
                            <h3 class="dropdown-toolbar-title" id ="notificationSecondCounter">Уведомления (0)</h3>
                        </div><!-- /dropdown-toolbar -->
                        <ul class="dropdown-menu" id="notificationHolder">
                        </ul>
                        <div class="dropdown-footer text-center">
                            <a href="#">Просмотреть все</a>
                        </div><!-- /dropdown-footer -->
                    </div><!-- /dropdown-container -->
                </li><!-- /dropdown -->

                <li><a href="/main-login">Расписание</a></li>
                <li><a href="/meetings">Встречи</a></li>
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
                    <input type="text" class="form-control" name="name" placeholder='Поиск'>
                </div>
                <button type="submit" class="btn btn-success">Поиск</button>
            </form>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="/allUnconfirmedFriends" id="result_text_friend"></a></li> <!-- AJAX "Друзья: 5" -->
                <li><a href="/allUnreadMessages" id="result_text_message"></a></li> <!-- AJAX "Сообщения: 10" -->
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b><span class="glyphicon glyphicon-user"></span> <sec:authentication property="principal.username"/></b> <span class="caret"></span></a>
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
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/notifications.js"></script>
</body>
</html>
