<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 05.02.2017
  Time: 14:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<html lang="en">
<head>
    <title>Title</title>



    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="/resources/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <script type="text/javascript" src="/resources/js/bootstrap.min.js"></script>

    <style>
        #login-dp{
            min-width: 250px;
            padding: 14px 14px 0;
            overflow:hidden;
            background-color:rgba(255,255,255,.8);
        }
        #login-dp .help-block{
            font-size:12px
        }
        #login-dp .bottom{
            background-color:rgba(255,255,255,.8);
            border-top:1px solid #ddd;
            clear:both;
            padding:14px;
        }
        #login-dp .social-buttons{
            margin:12px 0
        }
        #login-dp .social-buttons a{
            width: 49%;
        }
        #login-dp .form-group {
            margin-bottom: 10px;
        }
        .btn-fb{
            color: #fff;
            background-color:#3b5998;
        }
        .btn-fb:hover{
            color: #fff;
            background-color:#496ebc
        }
        .btn-tw{
            color: #fff;
            background-color:#55acee;
        }
        .btn-tw:hover{
            color: #fff;
            background-color:#59b5fa;
        }
        @media(max-width:768px){
            #login-dp{
                background-color: inherit;
                color: #fff;
            }
            #login-dp .bottom{
                background-color: inherit;
                border-top:0 none;
            }
        }
    </style>


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
                <!-- <li class="active"> -->

                <li><a href="/main-login">Расписание</a></li>
                <li><a href="/meeting">Встречи</a></li>
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
</body>
</html>
