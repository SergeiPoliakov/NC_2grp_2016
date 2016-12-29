<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
 <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Main Page</title>
     <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
 </head>
 <body>


 <div class="container login">
     <div class="navbar ">
         <div class="navbar-inner ">
             <a class="navbar-brand" href="/">Netcracker</a>
             <ul class="nav nav-pills">
                 <li class="active"><a href="/mainLogin">Главная</a></li>
                 <li class="active pull-right"><a href="/logout">Выход</a></li>
                 <li class="active pull-right"><a href="/message">Отправить сообщение</a></li>
                 <li class="active pull-right"><a href="/event">Добавить событие</a></li>


             </ul>
         </div>
     </div>
 </div>


 </body>
</html>