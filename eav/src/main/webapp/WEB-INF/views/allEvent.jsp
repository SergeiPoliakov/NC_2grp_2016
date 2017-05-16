<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 31.01.2017
  Time: 10:07
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
    <title>Список событий</title>
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
                    <span class="glyphicon glyphicon-calendar"></span> Список событий:
                </div>
                <div class="panel-collapse in" id="collapseOne">
                    <div class="panel-body">
                        <ul class="chat">

                            <c:forEach items="${allObject}" var="object">
                                <li class="right clearfix"><span class="chat-img pull-right">
                                <a class="btn btn-primary btn-xs" href="/editEvent/${object.id}"><span class="glyphicon glyphicon-wrench"></span>Изменить </a>
                                <a class="btn btn-danger btn-xs" href="/deleteEvent/${object.id}"><span class="glyphicon glyphicon-remove"></span>Удалить</a>
                                </span>
                                    <div class="chat-body clearfix">
                                        <div class="header">
                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-bell"></span> ${object.name} </small>
                                            <br>

                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-time"></span> ${object.date_begin} - ${object.date_end} </small>


                                        </div>
                                            <%--<p>${object.info}</p>--%>
                                    </div>
                                </li>
                            </c:forEach>


                            <li class="right clearfix"><span class="chat-img pull-right">
                                    <a  class="btn btn-primary btn-xs" href="/addEvent"><span class="glyphicon glyphicon-plus"></span>Добавить</a>
                            </span>
                            </li>


                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file='footer.jsp'%>

</body>
</html>
