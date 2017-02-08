<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 07.02.2017
  Time: 23:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<html>
<head>
    <title>Title</title>

    <style>
        html, body {
            height: 100%;
            position: relative;
            overflow-x: hidden;
        }

        body {
            overflow: hidden;
            height: auto;
            min-height: 100%;
        }

        div {

            color: #337AB7;
        }
        #left { text-align: left; }
        #right { text-align: right; }
        #right2 { text-align: right; }
        #center { text-align: center; }

        .footer {
            height: 60px;
            width: 100%;
            position: absolute;
            bottom: 0;
            background-color: #222222;
        }
    </style>


</head>
<body>


<footer class="footer">
    <div class="container">
        <div class="row">
            <div id="left" class="col-sm-4">
                <h3>Netcracker</h3>
            </div>
            <div id="center" class="col-sm-4">
                <h5><br>© 2 группа УНЦ "Инфотех" 2017</h5>
            </div>
            <div class="col-sm-4">
                <div id="right" class="row">
                    <br>
                    <div class="col-md-12 hidden-lg hidden-md hidden-sm text-left"><a href="#"><i
                            class="fa fa-2x fa-fw fa-instagram text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-twitter text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-facebook text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-github text-inverse"></i></a></div>
                </div>
                <div id="right2" class="row">
                    <div class="col-md-12 hidden-xs text-right"><a href="#"><i
                            class="fa fa-2x fa-fw fa-instagram text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-twitter text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-facebook text-inverse"></i></a> <a href="#"><i
                            class="fa fa-2x fa-fw fa-github text-inverse"></i></a></div>
                </div>
            </div>
        </div>
    </div>
</footer>
</body>
</html>
