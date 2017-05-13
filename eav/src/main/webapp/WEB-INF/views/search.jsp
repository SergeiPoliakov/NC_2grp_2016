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
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/search.js"></script>

</head>
<body>
<div class="search_area">

    <input type="text" name="query" id="search_box" onkeyup="check()" value="" autocomplete="off">
    <button class="btn btn-warning btn-sm" id="btn-find"
            onclick="getFind()" disabled="disabled" >Поиск</button>

    <div id="search_advice_wrapper"></div>
</div>




    <div class="form-group">
        <label for="object">Что ищем?</label>
        <div id="object" class="funkyradio">

            <div class="funkyradio-success">
                <input type="radio" name="checkObject" id="checkName" value="name" checked="checked"  >
                <label class="radio-inline" for="checkUser" style="margin-top: 0.5rem;">Пользователя по имени</label>
            </div>

            <div class="funkyradio-success">
                <input type="radio" name="checkObject" id="checkUser" value="user"  >
                <label class="radio-inline" for="checkUser" style="margin-top: 0.5rem;">Пользователя по интересам (тегам)</label>
            </div>

            <div class="funkyradio-success">
                <input type="radio" name="checkObject" id="checkMeeting" value="meeting"  />
                <label class="radio-inline" for="checkMeeting" style="margin-top: 0.5rem;">Встречу по тегам</label>
            </div>

        </div>
    </div>


<div class="form-group">
    <label for="logic">Критерий поиска</label>
    <div id="logic" class="funkyradio">
        <div class="funkyradio-success">
            <input type="radio" name="checkLogic" id="checkOR" value="or" checked="checked"  >
            <label class="radio-inline" for="checkOR" style="margin-top: 0.5rem;">OR</label>
        </div>
        <div class="funkyradio-success">
            <input type="radio" name="checkLogic" id="checkAND" value="and"  />
            <label class="radio-inline" for="checkAND" style="margin-top: 0.5rem;">AND</label>
        </div>
    </div>
</div>





</body>
</html>
