<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 05.02.2017
  Time: 13:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<html lang="en">
<head>
    <title>Title</title>

    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/font-awesome/css/font-awesome.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/header.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/tlmain.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/footer.css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>


    <script>

        // Нажатие кнопки "Отправить" в всплывающем окне
        document.getElementById('sendPassword').onclick = function() {
            $('#sendPasswordToEmail').modal('hide');
        };
    </script>


</head>

<body>

<nav class="navbar navbar-default" role="navigation" style="border-radius: 0px;">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/main">Netcracker</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li><a href="/addUser">Регистрация</a></li>
                <li><a href="https://www.youtube.com/watch?v=1Q5tfgkjws4">Быстрый старт</a></li>
            </ul>
            <!--<form action="/searchUser" class="navbar-form navbar-left" role="search" method="post">
                <div class="form-group">
                    <input type="text" class="form-control" name="name" placeholder="Поиск">
                </div>
                <button type="submit" class="btn btn-default">Поиск</button>
            </form> -->
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"><b>Войти</b> <span class="caret"></span></a>
                    <ul id="login-dp" class="dropdown-menu">
                        <li>
                            <div class="row">
                                <div class="col-md-12">
                                    <h4 class="text-center">Авторизация</h4>
                                   <!--Войти с помощью
                                    <div class="form-group">
                                        <button class="btn btn-vk btn-block">
                                            <i class="fa fa-vk fa-2x"></i>
                                        </button>
                                    </div>
                                    или -->
                                    <form class="form" role="form" method="post" action="<%=request.getContextPath()%>/j_spring_security_check" accept-charset="UTF-8" id="login-nav">
                                        <div class="form-group">
                                            <label class="sr-only" for="exampleInputUsername2">Логин</label>
                                            <input type="text" name="username" class="form-control" id="exampleInputUsername2" placeholder="Логин" required>
                                        </div>
                                        <div class="form-group">
                                            <label class="sr-only" for="exampleInputPassword2">Пароль</label>
                                            <input type="password" name="password" class="form-control" id="exampleInputPassword2" placeholder="Пароль" required>
                                            <div class="help-block text-right" id="rememberPassword"><a href="#myModal" data-toggle="modal">Забыли пароль?</a></div>
                                        </div>
                                        <div class="form-group">
                                            <button type="submit" class="btn btn-primary btn-block">Войти</button>
                                        </div>
                                        <div class="form-group">
                                            Запомнить меня
                                            <div class="material-switch pull-right">
                                                <input id="remember_me" type="checkbox" name="_spring_security_remember_me">
                                                <label for="remember_me" class="label-primary"></label>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                                <div class="bottom text-center">
                                    Впервые тут? <a href="/addUser"><b>Регистрация</b></a>
                                </div>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>


<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <!--  <div class="modal-content"> -->
        <div class=".col-xs-6 .col-md-4">
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="text-center">
                        <h3><i class="fa fa-lock fa-4x"></i></h3>
                        <h2 class="text-center">Забыли пароль?</h2>
                        <p>Вы можете сбросить ваш пароль здесь.</p>
                        <div class="panel-body">

                            <form class="form" method="post" action="/resetPassword">
                                <fieldset>
                                    <div class="form-group">
                                        <div class="input-group">
                                            <span class="input-group-addon"><i class="glyphicon glyphicon-envelope color-blue"></i></span>

                                            <input id="emailInput" name="email" placeholder="email адрес" class="form-control" type="email" oninvalid="setCustomValidity('Пожалуйста введите корректный email!')" onchange="try{setCustomValidity('')}catch(e){}" required="">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <input class="btn btn-lg btn-primary btn-block" id="sendPassword" value="Отправить мой пароль" type="submit">
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
</body>
</html>

