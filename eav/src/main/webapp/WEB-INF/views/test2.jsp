<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 08.04.2017
  Time: 10:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>

    <script src="<%=request.getContextPath()%>/resources/js/sockjs-0.3.4.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/js/stomp.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/resources/js/app.js" type="text/javascript"></script>


    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/notify222');
            stompClient = Stomp.over(socket);
            stompClient.connect('guest', function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/notifications222', function(greeting){
                    getMessage(JSON.parse(greeting.body).type);
                });
            });
        }

        function disconnect() {
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }

        function sendMessage() {
            var type = document.getElementById('name').value;
            var senderID = 1123;
            var JSONMessage = JSON.stringify({ 'type': type, 'senderID': senderID, 'recieverID': 10124});
            stompClient.send("/app/notify111", {}, JSONMessage);
        }

        function getMessage(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
    </script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div id="conversationDiv">
        <label>What is your name?</label><input type="text" id="name" />
        <button id="sendMessage" onclick="sendMessage();">Send</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>
