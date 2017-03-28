<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 08.02.2017
  Time: 23:02
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
    <title>Netcracker</title>

    <script type="text/javascript" src="/resources/js/jquery-1.9.1.min.js"> </script>

    <%@include file='headerGuest.jsp'%>

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/tether/tether.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/animate.css/animate.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/theme/css/style.css">

    <style>
        h2 {
            color: #aaadaf;
        }
    </style>
</head>
<body>
<section class="mbr-section mbr-parallax-background" id="msg-box4-e" style="padding-top: 4rem;">
    <div class="container">
        <div class="row">
            <div class="mbr-table-md-up">

                <div class="mbr-table-cell mbr-right-padding-md-up mbr-valign-top col-md-7 image-size" style="width: 50%;">
                    <div class="mbr-figure"><iframe class="mbr-embedded-video" src="https://www.youtube.com/embed/1Q5tfgkjws4?rel=0&amp;amp;showinfo=0&amp;autoplay=0&amp;loop=0" width="1280" height="720" frameborder="0" allowfullscreen></iframe></div>
                </div>
                <div class="mbr-table-cell col-md-5 text-xs-center text-md-left content-size">
                    <h2>БЫСТРЫЙ СТАРТ: ВИДЕО</h2>
                    <button class="btn btn-info btn-sm" id="btn-read">ПОДРОБНЕЕ</button>
                </div>
            </div>
        </div>
    </div>
</section>

<section class="engine"></section><section class="mbr-section" id="msg-box5-f" style="padding-top: 0px; padding-bottom: 0px;">
    <div class="container">
        <div class="row">
            <div class="mbr-table-md-up">
                <div class="mbr-table-cell col-md-5 text-xs-center text-md-right content-size">
                    <h2>ПРИСОЕДИНЯЙТЕСЬ УЖЕ СЕГОДНЯ!</h2>
                    <div class="lead">
                        <p></p>
                    </div>
                    <button class="btn btn-info btn-sm" href="/addUser" id="btn-add">РЕГИСТРАЦИЯ</button>
                </div>
                <div class="mbr-table-cell mbr-left-padding-md-up mbr-valign-top col-md-7 image-size" style="width: 50%;">
                    <div class="mbr-figure"><img src="/resources/assets/images/desktop.jpg"></div>
                </div>
            </div>
        </div>
    </div>
</section>

<script src="/resources/assets/tether/tether.min.js"></script>
<script src="/resources/assets/smooth-scroll/SmoothScroll.js"></script>
<script src="/resources/assets/jarallax/jarallax.js"></script>
<script src="/resources/assets/viewportChecker/jquery.viewportchecker.js"></script>
<script src="/resources/assets/theme/js/script.js"></script>


<input name="animation" type="hidden">

<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>

</body>
</html>
