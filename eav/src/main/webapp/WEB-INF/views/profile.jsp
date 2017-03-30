<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 29.01.2017
  Time: 21:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Настройки</title>
    <%@include file='header.jsp' %>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/bootstrap-select.min.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/tlmain.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/moment-with-locales.min.js"> </script>
    <script src="<%=request.getContextPath()%>/resources/js/bootstrap-select.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/validator.min.js"></script>




</head>
<body>
<div class="container">
    <div class="container">
        <h2>Настройки</h2>
        <ul class="nav nav-tabs nav-justified">
            <li class="active"><a data-toggle="tab" href="#general">Основные</a></li>
            <li><a data-toggle="tab" href="#privacy">Уведомления и приватность</a></li>
            <li><a data-toggle="tab" href="#security">Безопасность</a></li>
        </ul>

        <div class="tab-content">
            <div id="general" class="tab-pane fade in active">
                <h3>Основные</h3>
                <div class="well bs-component">
                    <div class="row">
                    <div class="col col-lg-5 col-md-6">
                        <form action="/changeProfile/${user.id}" method="post" data-toggle="validator">
                            <fieldset>
                                <div class="form-group has-feedback">
                                    <label class="control-label" for="InputSurname1">Фамилия</label>
                                    <input type="text" class="form-control" name="surname" id="InputSurname1"
                                           value=${user.surname} data-toggle="tooltip"
                                           pattern="[A-Za-zА-яа-яЁё]{3,}" title="Только русские и английские буквы. Не менее 3 символов" required>
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>

                                <div class="form-group  has-feedback">
                                    <label for="InputName1">Имя</label>
                                    <input type="text" class="form-control " name="name" id="InputName1" value=${user.name}
                                            data-toggle="tooltip" placeholder="Имя" pattern="[A-Za-zА-яа-яЁё]{3,}"
                                           title="Только русские и английские буквы. Не менее 3 символов" required>
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>


                                <div class="form-group has-feedback">
                                    <label for="InputMiddleName1">Отчество</label>
                                    <input type="text" class="form-control" name="middle_name" id="InputMiddleName1" value=${user.middleName}
                                            data-toggle="tooltip" placeholder="Имя" pattern="[A-Za-zА-яа-яЁё]{3,}"
                                           title="Только русские и английские буквы. Не менее 3 символов">
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>


                                    <!--  Тут нужно сделать так же, как и на странице регистрации или вернуть как было   -->
                                <div class="form-group has-feedback">
                                    <label for="InputAge1">Дата рождения</label>
                                    <input type="date" class="form-control" name="ageDate" id="InputAge1" value=${user.ageDate}
                                            data-toggle="tooltip" pattern="\d{2}.\d{2}.\d{4} \d{2}:\d{2}" required>
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>

                                <div class="form-group">
                                    <label>Пол</label>
                                    <div class="radio">
                                        <label>
                                            <input type="radio" name="sex" id="Gender1" value="мужской"  <c:if test="${user.sex eq 'мужской' or user.sex eq null}">checked</c:if> >
                                            Мужской
                                        </label>
                                    </div>
                                    <div class="radio">
                                        <label>
                                            <input type="radio" name="sex" id="Gender2" value="женский" <c:if test="${user.sex eq 'женский'}">checked</c:if> >
                                            Женский
                                        </label>
                                    </div>
                                </div>

                                <div class="form-group has-feedback">
                                    <label for="InputCity1">Город</label>
                                    <input type="text" class="form-control" name="city" id="InputCity1" value=${user.city}>
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>

                                <div class="form-group has-feedback">
                                    <label for="InputPhone1">Номер телефона</label>
                                    <input type="text" class="form-control" name="phone" id="InputPhone1" value=${user.phone}
                                            data-toggle="tooltip" pattern="[1-9]{11}" title="Введите корректный номер телефона">
                                    <span class="glyphicon form-control-feedback" aria-hidden="true"></span>
                                </div>

                                <div class="form-group">
                                    <label for="TextArea1">Расскажите немного о себе</label>
                                    <textarea rows="3" class="form-control noresize" name="info"
                                              id="TextArea1">${user.additional_field}</textarea>
                                </div>

                                <button type="submit" class="btn btn-info col-lg-5 col-lg-offset-4">Сохранить</button>
                        </fieldset>
                        </form>
                    </div>
                    <div class="col col-lg-5 col-lg-offset-2 col-md-6">
                        <%--
                          <img src="http://nc2.hop.ru/upload/${user.id}/avatar/avatar_${user.id}.png"
                             onerror="this.src = 'http://nc2.hop.ru/upload/default/avatar.png'" class="img-polaroid"
                             width="200">
                        --%>
                        <img src="${user.picture}"
                             onerror="this.src = 'ftp://netcracker.ddns.net/upload/default/avatar.png'" class="img-polaroid"
                             width="200">

                        <div class="form-group ">
                            <%--Загрузка картинки-аватара--%>
                            <label for="InputImg">Загрузка изображения</label>
                            <form method="POST" action="/uploadAvatar" enctype="multipart/form-data">
                                <input type="hidden" name="MAX_FILE_SIZE" value="20971520"><%--Ограничение на максимальный размер файла = 20 Мб со стороны клиента--%>
                                Файл: <input name="file" type="file" id="InputImg"
                                             accept="image/jpeg, image/png, image/gif"> <%--Ограничение на тип файла со стороны клиента--%>
                                <input type="submit" value="Загрузить"> Загрузить
                            </form>
                        </div>
                        <img src="https://lifehacker.ru/wp-content/uploads/2014/11/01_Comp-2.png" class="img-polaroid"
                             width="200">
                        <div class="form-group ">
                            <%--Кнопка подключения календаря--%>
                            <label for="InputImg">Подключите Google-календарь</label>
                            <div class="form-group ">
                                <a href="/addCalendar">
                                    <button type="button" class="btn btn-info"><span class="glyphicon glyphicon-calendar"
                                                                                     aria-hidden="true"> Подключить</span>
                                    </button>
                                </a>
                                <a href="/synchronizeCalendar">
                                    <button type="button" class="btn btn-info"><span class="glyphicon glyphicon-calendar"
                                                                                     aria-hidden="true"> Синхронизировать</span>
                                    </button>
                                </a>
                            </div>
                        </div>





                        <div class="form-group ">
                            <a href="/advancedSettings"> Расширенные настройки </a>
                        </div>
                    </div>
                </div>
                </div>
            </div>
            <div id="privacy" class="tab-pane fade">
                <h3>Уведомления и приватность</h3>
                <div class="well bs-component">
                    <div class="row">
                        <div class="col col-xs-12 col-sm-10 col-sm-offset-1 col-md-8 col-md-offset-2 col-lg-6 col-lg-offset-3">
                            <form action="/updateSettings/${settings.id}" method="post">
                                <fieldset>
                                    <div class="row">
                                        <div class="col col-xs-12 col-sm-8 col-sm-offset-2 col-md-8 col-md-offset-2 col-lg-8 col-lg-offset-2">

                                            <div class="form-group">
                                                <div class="text-center">
                                                    <label class="control-label" for="privateProfile">Кто может просматривать мой профиль</label>
                                                    <select id="privateProfile" name="privateProfile" class="selectpicker show-menu-arrow" data-style="btn-info">
                                                        <option <c:if test="${settings.privateProfile eq 'any'}">selected</c:if>  value="any">Все</option>
                                                        <option <c:if test="${settings.privateProfile eq 'onlyFriend'}">selected</c:if> value="onlyFriend">Только друзья</option>
                                                        <option <c:if test="${settings.privateProfile eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <div class="text-center">
                                                    <label class="control-label" for="privateMessage">Кто может писать мне личные сообщения</label>
                                                    <select id="privateMessage" name="privateMessage" class="selectpicker show-menu-arrow" data-style="btn-info">
                                                        <option <c:if test="${settings.privateMessage eq 'any'}">selected</c:if>  value="any">Все</option>
                                                        <option <c:if test="${settings.privateMessage  eq 'onlyFriend'}">selected</c:if> value="onlyFriend">Только друзья</option>
                                                        <option <c:if test="${settings.privateMessage  eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <div class="text-center">
                                                    <label class="control-label" for="privateAddFriend">Кто может добавлять меня в друзья</label>
                                                    <select id="privateAddFriend" name="privateAddFriend" class="selectpicker show-menu-arrow" data-style="btn-info">
                                                        <option <c:if test="${settings.privateAddFriend eq 'any'}">selected</c:if>  value="any">Все</option>
                                                        <option <c:if test="${settings.privateAddFriend eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <div class="text-center">
                                                    <label class="control-label" for="privateLookFriend">Кто может cмотреть мой список друзей</label>
                                                    <select id="privateLookFriend" name="privateLookFriend" class="selectpicker show-menu-arrow" data-style="btn-info">
                                                        <option <c:if test="${settings.privateLookFriend eq 'any'}">selected</c:if>  value="any">Все</option>
                                                        <option <c:if test="${settings.privateLookFriend  eq 'onlyFriend'}">selected</c:if> value="onlyFriend">Только друзья</option>
                                                        <option <c:if test="${settings.privateLookFriend  eq 'nobody'}">selected</c:if> value="nobody">Никто</option>
                                                    </select>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <ul class="list-group">
                                            <li class="list-group-item">
                                                Отправлять уведомления о новых сообщениях на почту
                                                <div class="material-switch pull-right">
                                                    <input id="i1" type="checkbox" name="emailNewMessage"  <c:if test="${settings.emailNewMessage eq true}">checked=checked</c:if> >
                                                    <label for="i1" class="label-success"></label>
                                                </div>
                                            </li>
                                            <li class="list-group-item">
                                                Отправлять уведомления о новых заявках в друзья на почту
                                                <div class="material-switch pull-right">
                                                    <input id="i2" type="checkbox" name="emailNewFriend"  <c:if test="${settings.emailNewFriend eq true}">checked=checked</c:if> >
                                                    <label for="i2" class="label-success"></label>
                                                </div>
                                            </li>
                                            <li class="list-group-item">
                                                Отправлять уведомления о приглашениях на встречу на почту
                                                <div class="material-switch pull-right">
                                                    <input id="i3" type="checkbox" name="emailMeetingInvite" <c:if test="${settings.emailMeetingInvite eq true}">checked=checked</c:if> >
                                                    <label for="i3" class="label-success"></label>
                                                </div>
                                            </li>
                                            <li class="list-group-item">
                                                Отправлять уведомления о новых сообщениях на телефон
                                                <div class="material-switch pull-right">
                                                    <input id="i4" type="checkbox" name="phoneNewMessage"  <c:if test="${settings.phoneNewMessage eq true}">checked=checked</c:if> >
                                                    <label for="i4" class="label-success"></label>
                                                </div>
                                            </li>
                                            <li class="list-group-item">
                                                Отправлять уведомления о новых заявках в друзья на телефон
                                                <div class="material-switch pull-right">
                                                    <input id="i5" type="checkbox" name="phoneNewFriend"  <c:if test="${settings.phoneNewFriend eq true}">checked=checked</c:if> >
                                                    <label for="i5" class="label-success"></label>
                                                </div>
                                            </li>
                                            <li class="list-group-item">
                                                Отправлять уведомления о приглашениях на встречу на телефон
                                                <div class="material-switch pull-right">
                                                    <input id="i6" type="checkbox"  name="phoneMeetingInvite"  <c:if test="${settings.phoneMeetingInvite eq true}">checked=checked</c:if> >
                                                    <label for="i6" class="label-success"></label>
                                                </div>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="text-center">
                                        <button type="submit" class="btn btn-success">Сохранить</button>
                                    </div>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <div id="security" class="tab-pane fade">
                <h3>Безопасность</h3>
                <div class="well bs-component">
                    <p>Смена пароля, подтверждение телефона и т.д.</p>
                    <div class="form-group ">
                        <a href="#myModalPassword" data-toggle="modal"> Изменить пароль </a>
                    </div>
                    <div class="form-group ">
                        <a href="#myModalPhone" data-toggle="modal"> Подтвердить номер телефона </a>
                    </div>


                    <div class="modal fade" id="myModalPassword">
                        <div class="modal-dialog">
                            <!--  <div class="modal-content"> -->
                            <div class=".col-xs-6 .col-md-4">
                                <div class="panel panel-default">
                                    <div class="panel-body">
                                        <div class="text-center">
                                            <h3><i class="fa fa-lock fa-4x"></i></h3>
                                            <h2 class="text-center">Хотите изменить пароль?</h2>
                                            <p>Вы можете изменить ваш пароль здесь.</p>
                                            <div class="panel-body">
                                                    <form method="post" id="passwordForm" action="/changePassword">
                                                        <input type="password" class="input-lg form-control" name="password1" id="password1" placeholder="New Password" autocomplete="off">
                                                        <div class="row" style="margin-top:1rem;">
                                                            <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-center">
                                                                <label>
                                                                    <span id="8char" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> Не менее 8 символов
                                                                </label>
                                                                <label>
                                                                    <span id="ucase" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> Одна заглавная буква
                                                                </label>
                                                            </div>
                                                            <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left">
                                                                <label>
                                                                    <span id="lcase" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> Одна строчная буква<br>
                                                                </label>
                                                                <label>
                                                                    <span id="num" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> Одна цифра
                                                                </label>
                                                            </div>
                                                        </div>
                                                        <input type="password" class="input-lg form-control" name="password2" id="password2" placeholder="Повторите пароль" autocomplete="off">
                                                        <div class="row" style="margin-top:1rem;">
                                                            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 text-center">
                                                                <span id="pwmatch" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> Пароли совпадают
                                                            </div>
                                                        </div>
                                                        <div style="margin-top:1rem;">
                                                            <input type="submit" class="col-xs-12 btn btn-primary btn-load btn-lg" data-loading-text="Изменение пароля..." value="Изменять пароль">
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
										</form>
									</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal fade" id="myModalPhone">
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
                                                                <span class="input-group-addon"><i class="glyphicon glyphicon-phone"></i></span>

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

                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    // Modal datetimepickers для создания новой задачи
    $(function () {
        $('#InputAge1').datetimepicker({
            locale: 'ru'
        });
    });
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/profile.js"></script>
</body>
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>
</html>

