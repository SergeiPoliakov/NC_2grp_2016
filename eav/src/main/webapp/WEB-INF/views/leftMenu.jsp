<%--
  Created by IntelliJ IDEA.
  User: Lawrence
  Date: 04.02.2017
  Time: 20:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!doctype html>
<!--[if IE 8]><html class="ie8 lt-ie10"><![endif]-->
<!--[if IE 9]><html class="ie9 lt-ie10"><![endif]-->
<!--[if gt IE 9]><!--><html lang="en"><!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="cleartype" content="on">
    <meta name="MobileOptimized" content="320">
    <meta name="HandheldFriendly" content="True">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/mocha/1.13.0/mocha.min.css">
    <link rel="stylesheet" href="/resources/dist/test.css">
    <link rel="stylesheet" href="/resources/dist/index.css">
    <title>Slideout tests</title>
    <!--%@include file='header.jsp'%>-->
</head>
<body>

<nav id="menu" class="menu">
    <a href="https://github.com/mango/slideout" target="_blank">
        <header class="menu-header">
            <span class="menu-header-title">Tests</span>
        </header>
    </a>

    <section class="menu-section">
        <h3 class="menu-section-title">Docs</h3>
        <ul class="menu-section-list">
            <li><a href="https://github.com/mango/slideout#installation" target="_blank">Installation</a></li>
            <li><a href="https://github.com/mango/slideout#usage" target="_blank">Usage</a></li>
            <li><a href="https://github.com/mango/slideout#api" target="_blank">API</a></li>
            <li><a href="https://github.com/mango/slideout#npm-scripts" target="_blank">npm-scripts</a></li>
        </ul>
    </section>

</nav>

<main id="panel" class="panel">
    <header class="panel-header">
        <button class="btn-hamburger js-slideout-toggle"></button>



    </header>




</main>


<script>
    mocha.setup('bdd');
    var exports = null;
    function assert(expr, msg) {
        if (!expr) throw new Error(msg || 'failed');
    }
</script>
<script src="/resources/dist/slideout.js"></script>
<script src="/resources/dist/test.js"></script>
<script>
    window.onload = function() {
        document.querySelector('.js-slideout-toggle').addEventListener('click', function() {
            slideout.toggle();
        });

        document.querySelector('.menu').addEventListener('click', function(eve) {
            if (eve.target.nodeName === 'A') { slideout.close(); }
        });

        var runner = mocha.run();
    };
</script>
</body>
</html>
