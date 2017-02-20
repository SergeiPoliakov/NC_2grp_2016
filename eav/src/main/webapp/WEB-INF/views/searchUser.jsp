<%@ page import="org.hibernate.Session" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %><%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 01.02.2017
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags"  prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
<head>
    <title>DB Test</title>

    <script type="text/javascript" src="/resources/js/jquery-1.9.1.min.js"> </script>

</head>
<body>


<%@include file='header.jsp'%>



<h2 id="faq">Результаты поиска:</h2>
<c:forEach items="${allUsers}" var="user">
    <div class="thumbnail">
        <h4>${user.surname} ${user.name} ${user.middleName}, ник "${user.login}"</h4>
        <ul class="nav nav-pills">
                <%--<li class="active pull-left"><a href="/delete/${object.id}">Удалить</a></li> Пока не будем тут удалять--%>
            <li class="active pull-left"><a href="/viewProfile/${user.id}">Смотреть профиль</a></li>
            <li class="active pull-left"><a href="/sendMessage/${user.id}">Отправить сообщение</a></li>
            <li class="active pull-left"><a href="/addFriend/${user.id}">Добавить в друзья</a></li>
        </ul>
    </div>
</c:forEach>

<%@include file='footer.jsp'%>

</body>
</html>


