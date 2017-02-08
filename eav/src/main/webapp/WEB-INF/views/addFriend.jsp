<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 01.02.2017
  Time: 14:58
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
    <title>Добавление в друзья</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

</head>
<body>

<%@include file='header.jsp'%>
<%@include file='leftMenu.jsp'%>

<div class="container">
    <div class="page-header">
        <h1>Netcracker <small>Добавление в друзья</small></h1>
    </div>
    <h2>Пользователь успешно добавлен в список друзей</h2>
        <ul class="nav nav-pills">
            <li class="active pull-left"><a href="/allUser">Продолжить</a></li>
        </ul>
</div>


<%@include file='footer.jsp'%>

</body>
</html>
