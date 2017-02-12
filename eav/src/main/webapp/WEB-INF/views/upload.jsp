<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 11.02.2017
  Time: 10:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page session="false" %>
<html lang="en">
<html>
<head>
    <title>Тестовая страница загрузки файла</title>
</head>
<body>
<form method="POST" action="/uploadFile" enctype="multipart/form-data">
    Файл: <input type="file" name="file">

    Имя: <input type="text" name="name">


    <input type="submit" value="Upload"> Загрузить
</form>
</body>
</html>