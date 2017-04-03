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

<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script> -->
<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.touchSwipe.min.js"></script> -->

<!-- <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script> -->

<!-- Подгружаем стили и скрипты для загрузки статистик и для свайпинга бокового меню: -->
<link href="<%=request.getContextPath()%>/resources/css/stat_menu.css" media="all" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/stat_load.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/stat_menu_swipe.js"></script>


<div id="sidebar">
    <ul>
        <li><a href="#">Статистика 1</a><div id="location_1"></div></li>
        <li><a href="#">Статистика 2</a><div id="location_2"></div></li>
        <li><a href="#">Статистика 3</a></li>
        <li><a href="#">Статистика 4</a></li>
    </ul>
</div>
