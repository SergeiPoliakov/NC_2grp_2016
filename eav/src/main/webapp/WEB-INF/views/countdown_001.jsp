<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 08.05.2017
  Time: 16:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<!-- 2017-05-08 Таймеры обратного отсчета, работают вместе с инклудами файлов countdown_001.jsp и countdown_002.jsp -->
<link rel="stylesheet" type="text/css"
      href="<%=request.getContextPath()%>/resources/countdown/countdown.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/countdown/countdown.js"></script>

<!-- Место первого таймера (обратный отсчет до окончания времени редактирования) -->
<div class="second-my timerhello timerhello_001">
    <div class="second-my-content">
        <link href="//fonts.googleapis.com/css?family=Russo+One&amp;subset=latin,cyrillic" rel="stylesheet"
              type="text/css">
        <p class="titloftimer">До конца редактирования:</p>
        <br>
        <p class="result">
            <span class="result-day-001 items">00</span>
            <span class="dot">дн.&nbsp;</span>
            <span class="result-hour-001 items">00</span>
            <span class="dot">:</span>
            <span class="result-minute-001 items">00</span>
            <span class="dot">:</span>
            <span class="result-second-001 items">00</span>
        </p>
        <div class="clearf"></div>
    </div>
</div>
<!-- Окончание первого таймера -->
