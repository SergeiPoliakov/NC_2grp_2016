<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 27.02.2017
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Advanced Settings Page</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/bootstrap-select.min.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>


    <script src="<%=request.getContextPath()%>/resources/js/bootstrap-select.min.js"></script>

    <%@include file='header.jsp'%>

</head>
<body>
<div class="container">


        <div class="page-header">
            <h1>Netcracker <small>Расширенные настройки профиля ${user.email}</small></h1>
        </div>

        <div class="modal fade" id="myModal">
            <div class="modal-dialog">
                <!--  <div class="modal-content"> -->
                <div class=".col-xs-6 .col-md-4">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="text-center">

                                <h3><i class="fa fa-mobile fa-4x"></i></h3>
                                <h2 class="text-center">Хотите получать уведомления на телефон?</h2>
                                <p>Вы можете подтвердить ваш номер здесь.</p>

                                <div class="panel-body">

                                    <form action="/generatePhoneCode" method="get">
                                        <fieldset>
                                        <div class="form-group">
                                            <input class="btn btn-lg btn-primary btn-block" id="sendCode" value="Отправить код подтверждения" type="submit">
                                        </div>
                                            </fieldset>
                                    </form>


                                    <form class="form" method="post" action="/confirmedPhone">
                                        <fieldset>
                                            <div class="form-group">
                                                <div class="input-group">
                                                    <span class="input-group-addon"><i class="glyphicon-phone-alt glyphicon-envelope color-blue"></i></span>

                                                    <input id="emailInput" name="codeUser" id="InputCode" placeholder="Код подтверждения" class="form-control" type="text" >
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <input class="btn btn-lg btn-primary btn-block" id="confirm" value="Подтвердить" type="submit">
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







    <div class="form-group ">
        <a href="/profile"> К обычным настройкам </a>
    </div>

    <div class="form-group ">
        <a href="#myModal" data-toggle="modal"> Подтвердить номер телефона </a>
    </div>

    <div class="container">
        <form action="/updateSettings/${settings.id}" method="post">
            <!--  Тут будут настройки оповещения для email и телефона с кнопкой Сохранить. Нужен фронтенд. -->

            <div class="form-group col-lg-offset-4 col-lg-4">
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="emailNewMessage"  <c:if test="${settings.emailNewMessage eq true}">checked=checked</c:if> >
                        Отправлять уведомления о новых сообщениях на почту
                    </label>
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="emailNewFriend"  <c:if test="${settings.emailNewFriend eq true}">checked=checked</c:if> >
                        Отправлять уведомления о новых заявках в друзья на почту
                    </label>
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="emailMeetingInvite" <c:if test="${settings.emailMeetingInvite eq true}">checked=checked</c:if> >
                        Отправлять уведомления о приглашениях на встречу на почту
                    </label>
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="phoneNewMessage"  <c:if test="${settings.phoneNewMessage eq true}">checked=checked</c:if> >
                        Отправлять уведомления о новых сообщениях на телефон
                    </label>
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="phoneNewFriend"  <c:if test="${settings.phoneNewFriend eq true}">checked=checked</c:if> >
                        Отправлять уведомления о новых заявках в друзья на телефон
                    </label>
                </div>
                <div class="checkbox">
                    <label>
                        <input type="checkbox"  name="phoneMeetingInvite"  <c:if test="${settings.phoneMeetingInvite eq true}">checked=checked</c:if> >
                        Отправлять уведомления о приглашениях на встречу на телефон
                    </label>
                </div>

                <div>
                    <label>Кто может просматривать мой профиль</label>
                    <select name="privateProfile" class="selectpicker show-menu-arrow" data-style="btn-info">
                        <option <c:if test="${settings.privateProfile eq 'any'}">selected</c:if>  value="any">Все</option>
                        <option <c:if test="${settings.privateProfile eq 'onlyFriend'}">selected</c:if> value="onlyFriend">Только друзья</option>
                        <option <c:if test="${settings.privateProfile eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                    </select>
                </div>

                <div>
                    <label>Кто может писать мне личные сообщения</label>
                    <select name="privateMessage" class="selectpicker show-menu-arrow" data-style="btn-info">
                        <option <c:if test="${settings.privateMessage eq 'any'}">selected</c:if>  value="any">Все</option>
                        <option <c:if test="${settings.privateMessage  eq 'onlyFriend'}">selected</c:if> value="onlyFriend">Только друзья</option>
                        <option <c:if test="${settings.privateMessage  eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                    </select>
                </div>

            </div>
            <button type="submit" class="btn-lg btn-success col-lg-4 col-lg-offset-4">Сохранить</button>
        </form>
    </div>

</div>

<%@include file='footer.jsp'%>

</body>
</html>
