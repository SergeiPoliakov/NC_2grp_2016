<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 14.01.2017
  Time: 22:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>Title</title>
</head>
<body>

<h2 id="faq">Object</h2>
<c:forEach items="${allObject}" var="object">
    <h4>${object}</h4>
</c:forEach>
<a href="/delete/${10001}">Удалить Геннадия</a>

</body>
</html>
