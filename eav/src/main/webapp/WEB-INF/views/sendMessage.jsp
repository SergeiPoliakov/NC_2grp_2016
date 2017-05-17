<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 01.02.2017
  Time: 14:58
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
    <title>Отправка сообщений</title>

    <link href="<%=request.getContextPath()%>/resources/css/resultList.css" rel="stylesheet">

    <script type="text/JavaScript"
            src="${pageContext.request.contextPath}/resources/js/jquery-1.9.1.min.js">
    </script>

    <script type="text/javascript">
        function doAjaxArray() {

            var inputText = '${to_id}';

            $.ajax({
                url: '/getArray',
                type: 'GET',
                dataType: 'json',
                contentType: 'application/json',
                mimeType: 'application/json',
                data: ({
                    text: inputText
                }),
                success: function (data) {


                    var result = '';

                    var index;
                    for (index = 0; index < data.length; ++index) {
                        console.log(data[index]);

                        result += '' +
                            '<li class="right clearfix"><span class="chat-img pull-right">';
                        if ((data[index]).to_id == ${to_id})
                            result += '<a  class="btn btn-danger btn-xs" ' +
                                'href="/deleteMessage/${to_id}/' + (data[index]).id + '">[X]</a>';
                        result += '</span>' +
                            '<div class="chat-body clearfix">' +
                            '<div class="header">' +
                            '<small class=" text-muted"><span class="glyphicon ' +
                            'glyphicon-time"></span>' + (data[index]).date_send + '</small>' +
                            '<strong class="pull-right primary-font">' + (data[index]).from_name +
                            '</strong>' +
                            '</div>' +
                            '<p>' + (data[index]).text +
                            '</p>' +
                            '</div>' +
                            '</li>';


                    }
                    $("#result_array").html(result);

                }
            });
        }
        setInterval(doAjaxArray, 1000);
    </script>

    <script type="text/javascript">
        function doAjaxSendMessage() {
            var inputText2 = $("#input_str2").val();
            document.getElementById("input_str2").value = '';

            $.ajax({
                url: '/sendMessage3',
                type: 'GET',
                dataType: 'json',
                contentType: 'application/json',
                mimeType: 'application/json',
                data: ({
                    text: '${to_id}' + '~' + inputText2
                }),
                success: function (data) {
                    var result = '';
                    $("#input_str2").text(result);
                    var text_max = 70;
                    $('#textarea_feedback').html('Осталось символов: ' + text_max);
                }
            });
        }
    </script>




</head>
<body>

<%@include file='header.jsp' %>


<div class="container">
    <div class="row">
        <div class="col-md-5 col-md-offset-3">
            <div class="panel panel-primary">
                <div class="panel-heading" id="accordion">
                    <span class="glyphicon glyphicon-comment"></span> История сообщений
                </div>
                <div class="panel-collapse in" id="collapseOne">
                    <div class="panel-body">
                        <ul>

                            <p id="result_array"></p>

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

                        <div class="form-group ">
                            <textarea id="input_str2" type="text" rows="3" class="form-control custom-control"
                                      name="text" style="resize:none" placeholder="Введите Ваше сообщение..."
                                      maxlength="70"></textarea>
                        </div>
                            <span class="input-group-btn">
                                <div class="text-center">
                            <button class="btn btn-warning btn-sm" id="btn-send"
                                    onclick="doAjaxSendMessage()">Отправить</button>
                                </div>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file='footer.jsp' %>

<script type="text/javascript">
    // Лимит числа символов в сообщении
    $(function () {
        var text_max = 70;
        $('#textarea_feedback').html('Осталось символов: ' + text_max);

        $('#input_str2').keydown(function () {
            var text_length = $('#input_str2').val().length;
            var text_remaining = text_max - text_length;

            $('#textarea_feedback').html('Осталось символов: ' + text_remaining);
        });
    });
</script>

</body>

</html>
