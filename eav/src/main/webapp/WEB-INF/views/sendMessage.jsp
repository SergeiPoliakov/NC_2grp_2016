<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 01.02.2017
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags"  prefix="sec" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
<head>
    <title>Отправка сообщений</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

    <style>
        .chat
        {
            list-style: none;
            margin: 0;
            padding: 0;
        }

        .chat li
        {
            margin-bottom: 10px;
            padding-bottom: 5px;
            border-bottom: 1px dotted #B3A9A9;
        }

        .chat li.left .chat-body
        {
            margin-left: 60px;
        }

        .chat li.right .chat-body
        {
            margin-right: 60px;
        }


        .chat li .chat-body p
        {
            margin: 0;
            color: #777777;
        }

        .panel .slidedown .glyphicon, .chat .glyphicon
        {
            margin-right: 5px;
        }

        .panel-body
        {
            overflow-y: scroll;
            height: 550px;
        }

        ::-webkit-scrollbar-track
        {
            -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
            background-color: #F5F5F5;
        }

        ::-webkit-scrollbar
        {
            width: 12px;
            background-color: #F5F5F5;
        }

        ::-webkit-scrollbar-thumb
        {
            -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
            background-color: #555;
        }
    </style>


</head>
<body>

<%@include file='header.jsp'%>


<%--<div class="thumbnail">
    <h4>${to_id} </h4>
</div> --%>


<div class="container">
    <div class="row">
        <div class="col-md-5">
            <div class="panel panel-primary">
                <div class="panel-heading" id="accordion">
                    <span class="glyphicon glyphicon-comment"></span> История сообщений
                    <div class="btn-group pull-right">
                        <a type="button" class="btn btn-default btn-xs" data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
                            <span class="glyphicon glyphicon-chevron-down"></span>
                        </a>
                    </div>
                </div>
                <div class="panel-collapse in" id="collapseOne">
                    <div class="panel-body">
                        <ul class="chat">

                            <c:forEach items="${allObject}" var="object">




                                <li class="right clearfix"><span class="chat-img pull-right">
                                    <a  class="btn btn-danger btn-xs" href="/deleteMessage/${to_id}/${object.id}">[X]</a>
                            </span>
                                    <div class="chat-body clearfix">
                                        <div class="header">

                                            <small class=" text-muted"><span class="glyphicon glyphicon-time"></span>${object.date_send}</small>
                                            <strong class="pull-right primary-font">${object.from_name} </strong>
                                        </div>
                                        <p>
                                                ${object.text}
                                        </p>
                                    </div>
                                </li>



                            </c:forEach>


                        </ul>
                    </div>
                    <div class="panel-footer">
                        <div class="input-group">
					<span class="input-group-addon ">
						<div class="text-right" id="textarea_feedback">
						Осталось
						</div>
					</span>
                        </div>
                        <form action="/sendMessage1/${to_id}" method="post">
                            <div class="form-group ">
                                <textarea rows="3" class="form-control custom-control" name="text" style="resize:none" placeholder="Введите Ваше сообщение..." maxlength="70" id="TextArea1"></textarea>
                            </div>
                            <span class="input-group-btn">
                                <div class="text-center">
						<button class="btn btn-warning btn-sm" id="btn-send">Отправить</button>
						</div>

                            </span>
                        </form>


                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file='footer.jsp'%>

<script type="text/javascript">
    // Лимит числа символов в сообщении
    $(function() {
        var text_max = 70;
        $('#textarea_feedback').html('Осталось символов: ' + text_max);

        $('#TextArea1').keydown(function() {
            var text_length = $('#TextArea1').val().length;
            var text_remaining = text_max - text_length;

            $('#textarea_feedback').html('Осталось символов: ' + text_remaining);
        });
    });
</script>




</body>

</html>
