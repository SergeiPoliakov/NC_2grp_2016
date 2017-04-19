<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 18.04.2017
  Time: 15:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <title>Список требующих Вашего внимания встреч</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <link href="<%=request.getContextPath()%>/resources/css/resultList.css" rel="stylesheet">


</head>
<body>

<%@include file='header.jsp' %>


<div class="container">
    <div class="row">
        <div class="col-md-5">
            <div class="panel panel-primary">
                <div class="panel-heading" id="accordion">
                    <span class="glyphicon glyphicon-calendar"></span> Список требующих Вашего внимания встреч:
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
                            <c:forEach items="${allObject}" var="object">

                                <li class="right clearfix"><span class="chat-img pull-right">
                                    <a class="btn btn-danger btn-xs" href="/removeMeetingByDuplicate/${object.key.id}"><span
                                            class="glyphicon glyphicon-remove"></span>Удалить</a>
                                    <a class="btn btn-info btn-xs" href="/getMeetingPage/${object.key.id}"><span
                                            class="glyphicon glyphicon-zoom-in"></span>Встреча</a></span>
                                    <a class="btn btn-warning btn-xs"
                                       href="/userOptimizerGetOptimizerPage/${object.key.id}"><span
                                            class="glyphicon glyphicon-flash"></span>Перейти к оптимизатору</a></span>

                                    <div class="chat-body clearfix">
                                        <div class="header">
                                            <br>
                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-bell"></span> ${object.key.name} </small>
                                            <br>
                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-time"></span> ${object.key.date_begin}
                                                - ${object.key.date_end} </small>
                                            <br>
                                            <br>

                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-exclamation-sign"></span>ПРОБЛЕМА:
                                                совпадение по
                                                времени с событиями:
                                            </small> </span>
                                            <br>
                                            <br>

                                            <c:forEach items="${object.value}" var="object2">

                                                <small class=" text-muted"><span
                                                        class="glyphicon glyphicon-thumbs-down"></span> ${object2.name}
                                                </small>
                                                <br>

                                                <small class=" text-muted"><span
                                                        class="glyphicon glyphicon-time"></span> ${object2.date_begin}
                                                    - ${object2.date_end} </small>
                                                <br>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </li>

                            </c:forEach>

                            <li class="right clearfix"><span class="chat-img pull-right">
                                    <a class="btn btn-primary btn-xs" href="/userOptimizerProblem"><span
                                            class="glyphicon glyphicon-refresh"></span>Обновить список</a>
                            </span>
                            </li>

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
