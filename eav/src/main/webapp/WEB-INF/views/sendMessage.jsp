<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 01.02.2017
  Time: 14:58
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
    <title>Отправка сообщений</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>
<body>

<%@include file='header.jsp'%>

<%--<div class="thumbnail">
    <h4>${to_id} </h4>
</div> --%>


<h2 id="faq">История сообщений:</h2>
<c:forEach items="${allObject}" var="object">
    <div class="thumbnail">
        <h4>${object.date_send}, ${object.from_name}: </h4>
        <h4>${object.text} </h4>
        <ul class="nav nav-pills">
            <li class="active pull-left"><a href="/deleteMessage/${to_id}/${object.id}">Удалить</a></li>
        </ul>
    </div>
</c:forEach>


<div class="container col-lg-5">
    <form action="/sendMessage1/${to_id}" method="post">
        <div class="form-group ">
            <label for="TextArea1"> Введите сообщение:</label>
            <textarea rows="3" class="form-control" name="text" id="TextArea1"></textarea>
        </div>
        <button type="submit" class="btn-lg btn-success col-lg-5 col-lg-offset-4">Отправить</button>
    </form>
</div>


</body>
</html>
