<%--
  Created by IntelliJ IDEA.
  User: Костя
  Date: 03.04.2017
  Time: 22:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title>Статистика</title>
    <%@include file='header.jsp'%>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="resources\css\bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="resources\css\tlmain.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/jquery.mCustomScrollbar.min.css">

    <script type="text/javascript" src="resources\js\jquery-1.9.1.min.js"> </script>
    <script type="text/javascript" src="resources\js\moment-with-locales.min.js"> </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.mCustomScrollbar.concat.min.js"> </script>
    <script type="text/javascript" src="resources\js\bootstrap.min.js"></script>

    <style>
        .card_statistic{
            min-height: 40rem;
            max-height: 40rem;
            overflow: hidden;
        }
    </style>
</head>
<body>

<div class="container top-buffer-20">
    <div class="row">
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 1 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 2 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 3 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 4 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 5 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 6 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 7 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">
                <h4 class="card-title text-center">Название Статистики 8 </h4>
                Тут все связанное с выводом статистики, можно тут внутри создать container и
                попробовать в него запихнуть графики, если они будут вылезать за границы и т.д.
            </div>
        </div>
    </div>
</div>
</body>
<div style="margin-bottom: 8rem;"/>
<%@include file='footer.jsp'%>
</html>
