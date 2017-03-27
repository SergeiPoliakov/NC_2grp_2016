<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 07.02.2017
  Time: 12:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
<head>
    <title>Ваши друзья</title>
    <%@include file='header.jsp'%>
    <meta charset="UTF-8">
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>
</head>
<body>
<div class="container top-buffer-20">
    <div class="row">
        <c:forEach items="${allObject}" var="object">
            <!-- Карточка пользователя -->
            <div class="col-md-3 col-sm-4">
                <div class="card style_prevu_kit_static_blue">
                    <h4 class="card-title text-center">${object.surname} ${object.name} ${object.middleName}</h4>
                    <div class="card-title text-center">
                        <small class=" text-muted"><span
                                class="glyphicon glyphicon-user"></span> ${object.login} </small>

                    </div>
                    <div class="profile-userpic">
                            <%-- <img src="${object.picture}" onerror="this.src = 'http://nc2.hop.ru/upload/default/avatar.png'" class="img-responsive"  alt='Изображение' > --%>
                        <img src="${object.picture}" onerror="this.src = 'ftp://netcracker.ddns.net/upload/default/avatar.png'" class="img-responsive"  alt='Изображение' >
                    </div>
                    <div class="profile-userbuttons">
                        <a href="/viewProfile/${object.id}"><button type="button" class="btn btn-primary btn-xs"><span   class="glyphicon glyphicon-cog" aria-hidden="true"> Профиль</span></button></a>
                        <a href="/sendMessage/${object.id}"><button type="button" class="btn btn-info btn-xs"><span class="glyphicon glyphicon-envelope" aria-hidden="true"> К  чату</span></button></a>
                        <a href="/deleteFriend/${object.id}"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-trash" aria-hidden="true"> Удалить</span></button></a>
                    </div>
                    <ul class="list-group list-group-my list-group-flush">
                        <li class="list-group-item">Дата рождения: ${object.ageDate}</li>
                        <li class="list-group-item">Город: ${object.city}</li>
                        <li class="list-group-item">Пол: ${object.sex}</li>
                        <li class="list-group-item">О себе: ${object.additional_field}</li>
                    </ul>
                </div>
            </div>
            <!-- Карточка пользователя -->
        </c:forEach>
    </div>
</div>
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
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>
</html>
