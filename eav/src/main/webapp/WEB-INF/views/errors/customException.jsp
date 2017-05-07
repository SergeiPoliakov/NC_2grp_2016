<%@ page import="entities.User" %>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ошибка</title>
    <script type="text/javascript" src="/resources/js/jquery-1.9.1.min.js"> </script>

    <link href="<%=request.getContextPath()%>/resources/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/font-awesome/css/font-awesome.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/header.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/tlmain.css" rel="stylesheet">

    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/footer.css">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bootstrap.min.js"></script>


    <style>
        .error-template {padding: 40px 15px;text-align: center;}
        .error-actions {margin-top:15px;margin-bottom:15px;}
        .error-actions .btn { margin-right:10px; }
    </style>

</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>
                    Ой!</h1>
                <h2>
                    ${exception.message}</h2>

                <div class="error-actions">
                    <a href="/main-login" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-home"></span>
                        На главную </a>
                </div>
            </div>
        </div>
    </div>
</div>




</body>
<%@include file='/WEB-INF/views/footer.jsp'%>

</html>
