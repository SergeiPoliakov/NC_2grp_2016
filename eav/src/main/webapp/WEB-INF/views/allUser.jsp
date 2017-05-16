<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 06.02.2017
  Time: 12:38
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
    <title>Список пользователей</title>

    <link href="<%=request.getContextPath()%>/resources/css/resultList.css" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>


</head>
<body>

<%@include file='header.jsp' %>


<div class="container">
    <div class="row">
        <div class="col-md-5">
            <div class="panel panel-primary">
                <div class="panel-heading" id="accordion">
                    <span class="glyphicon glyphicon-list"></span> Список пользователей:
                </div>
                <div class="panel-collapse in" id="collapseOne">
                    <div class="panel-body">
                        <ul class="chat">

                            <c:forEach items="${allUsers}" var="object">
                                <li class="right clearfix"><span class="chat-img pull-right">
                                <a class="btn btn-primary btn-xs" href="/user${object.id}"><span class="glyphicon glyphicon-cog">  </span>Профиль </a>
                              <!--  <a class="btn btn-info btn-xs" href="/sendMessage/${object.id}"><span class="glyphicon glyphicon-envelope"></span>Написать</a>  -->
                                <a class="btn btn-success btn-xs" href="/addFriend/${object.id}/addFriend" onclick="sendMessage('friendRequest', ${object.id}, null, null)">
                                    <span class="glyphicon glyphicon-plus">   </span>В друзья
                                </a>
                                </span>
                                    <div class="chat-body clearfix">
                                        <div class="header">
                                            <small class=" text-muted"><span
                                                    class="glyphicon glyphicon-user"></span> ${object.login} </small>

                                        </div>
                                        <div class="text-left">
                                                ${object.surname} ${object.name} ${object.middleName}
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>


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
