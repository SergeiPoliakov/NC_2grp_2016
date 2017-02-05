<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 14.01.2017
  Time: 22:53
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
<html>
<head>
    <title>Список пользователей</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

</head>
<body>

<%@include file='header.jsp'%>

<h2 id="faq">Список пользователей:</h2>
<c:forEach items="${allObject}" var="object">
    <div class="thumbnail">
        <h4>${object.middleName} ${object.name} ${object.surname}, ник "${object.login}"</h4>
        <ul class="nav nav-pills">
    <%--<li class="active pull-left"><a href="/delete/${object.id}">Удалить</a></li> Пока не будем тут удалять--%>
        <li class="active pull-left"><a href="/viewProfile/${object.id}">Смотреть профиль</a></li>
        <li class="active pull-left"><a href="/sendMessage/${object.id}">Отправить сообщение</a></li>
        <li class="active pull-left"><a href="/addFriend/${object.id}">Добавить в друзья</a></li>
</ul>
</div>
</c:forEach>
</body>
</html>
