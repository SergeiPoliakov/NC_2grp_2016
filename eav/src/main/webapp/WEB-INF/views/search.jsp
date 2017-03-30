<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 30.03.2017
  Time: 0:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>

<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<html lang="en">
<head>
    <title>Поиск :: тестовая версия</title>

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/search.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/search_with_num.js"></script>

</head>
<body>
<div class="search_area">

        <input type="text" name="query" id="search_box" value="" autocomplete="off">
        <button class="btn btn-warning btn-sm" id="btn-find"
                onclick="getFind()">Поиск</button>

    <div id="search_advice_wrapper"></div>
</div>



</body>
</html>
