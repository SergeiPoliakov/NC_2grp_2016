<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 08.04.2017
  Time: 23:28
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
    <title>Тестовая страница свободных слотов</title>
    <link href="<%=request.getContextPath()%>/resources/css/resultList.css" rel="stylesheet">

    <script type="text/JavaScript"
            src="${pageContext.request.contextPath}/resources/js/jquery-1.9.1.min.js">
    </script>

    <meta charset="UTF-8">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/slots.js"></script>

</head>
<body>

<%@include file='header.jsp' %>

<div class="container">
    <div class="row">
        <div class="col-md-5">
            <div class="panel panel-primary">

                <div class="panel-heading" id="accordion">
                    <span class="glyphicon glyphicon-comment"></span> Список свободных слотов для встречи за текущую неделю
                    <div class="btn-group pull-right">
                        <a type="button" class="btn btn-default btn-xs" data-toggle="collapse" data-parent="#accordion"
                           href="#collapseOne">
                            <span class="glyphicon glyphicon-chevron-down"></span>
                        </a>
                    </div>
                </div>

                <div class="panel-collapse in" id="collapseOne">
                    <div class="panel-body">
                        <ul class="chat">

                            <p id="result_array"></p>

                        </ul>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<%@include file='footer.jsp' %>
</body>
</html>
