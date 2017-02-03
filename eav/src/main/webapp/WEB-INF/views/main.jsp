<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
<head>
    <title>DB Test</title>
    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container login">
    <div class="navbar ">
        <div class="navbar-inner ">
            <a class="navbar-brand" href="/">Netcracker</a>
            <ul class="nav nav-pills">

                <li class="active pull-right"><a href="/searchUser">Найти пользователя</a></li>
                <li class="active pull-right"><a href="/addUser">Зарегистрироваться</a></li>
                <li class="active pull-right"><a href="/login">Войти</a></li>

            </ul>
        </div>
    </div>
</div>



</body>
</html>
