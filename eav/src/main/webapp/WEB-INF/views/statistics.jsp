<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 31.03.2017
  Time: 10:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<html lang="en">
<head>
    <title>Статистика</title>
    <%@include file='header.jsp'%>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\tlmain.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/jquery.mCustomScrollbar.min.css">

    <!-- <script type="text/javascript" src="resources\js\jquery-1.9.1.min.js"> </script> -->
    <script type="text/javascript" src="resources\js\moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.mCustomScrollbar.concat.min.js"> </script>
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>


    <!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script> -->
    <!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.touchSwipe.min.js"></script> -->

    <!-- Подгружаем скрипты для загрузки статистик: -->
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/stat_load.js"></script>


    <style>
        .card_statistic{
            min-height: 30rem;
            max-height: 30rem;
            overflow: hidden;
        }
    </style>
</head>
<body>

<div id = "insert_place_col-lg-6" class="container top-buffer-20">

</div>

<!-- Место для вставки диаграммы, чтобы не забыть-->
<!-- <div id="location_1"></div> -->
<!-- Место для вставки графика -->
<!-- <div id="location_2"></div> -->

</body>
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>
</html>
