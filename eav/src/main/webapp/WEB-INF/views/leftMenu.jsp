<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 08.02.2017
  Time: 15:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<html>
<head>
    <title>Меню</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

    <link rel="stylesheet" href="/resources/menu/css/normalize.css">
    <link rel="stylesheet" href="/resources/menu/css/demo.css">
    <link rel="stylesheet" href="/resources/menu/css/pushy.css">

</head>

<body>

<nav class="pushy pushy-left" data-focus="#first-link">
    <div class="pushy-content">
        <ul>
            <li class="pushy-submenu">
                <button id="first-link"><span class="glyphicon glyphicon-comment"></span>  События</button>
                <ul>
                    <li class="pushy-link"><a href="#">Расписание</a></li>
                    <li class="pushy-link"><a href="#">Добавить</a></li>
                    <li class="pushy-link"><a href="#">Поиск</a></li>
                </ul>
            </li>
            <li class="pushy-submenu">
                <button><span class="glyphicon glyphicon-user"></span>  Мои друзья</button>
                <ul>
                    <li class="pushy-link"><a href="#">Список друзей</a></li>
                    <li class="pushy-link"><a href="#">Запросы на добавление</a></li>
                    <li class="pushy-link"><a href="#">Черный список</a></li>
                </ul>
            </li>
            <li class="pushy-submenu">
                <button><span class="glyphicon glyphicon-envelope"></span>  Сообщения</button>
                <ul>
                    <li class="pushy-link"><a href="#">Входящие</a></li>
                    <li class="pushy-link"><a href="#">Исходящие</a></li>
                    <li class="pushy-link"><a href="#">Срочные</a></li>
                </ul>
            </li>
            <li class="pushy-submenu">
                <button><span class="glyphicon glyphicon-star"></span>  Встречи</button>
                <ul>
                    <li class="pushy-link"><a href="#">Расписание</a></li>
                    <li class="pushy-link"><a href="#">Приглашения</a></li>
                    <li class="pushy-link"><a href="#">Отказы</a></li>
                </ul>
            </li>
            <li class="pushy-link"><a href="#"><span class="glyphicon glyphicon-home"></span>  Профиль</a></li>
            <li class="pushy-link"><a href="#"><span class="glyphicon glyphicon-film"></span>  Обучение</a></li>
            <li class="pushy-link"><a href="#"><span class="glyphicon glyphicon-cog"></span>  Настройки</a></li>
            <li class="pushy-link"><a href="#"><span class="glyphicon glyphicon-off"></span>  Выход</a></li>
        </ul>
    </div>
</nav>

<div class="site-overlay"></div>

<div id="container">
    <!-- Кнопочка менюшки -->
    <button class="menu-btn">&#9776; Меню</button>

    <!-- Свои данные -->
</div>

<script src="/resources/menu/js/pushy.min.js"></script>

</body>
</html>