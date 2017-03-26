<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 07.02.2017
  Time: 23:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>

<footer class="footer">
    <div class="container">
        <div class="row">
            <div id="left" class="col-sm-4">
                <h3>Netcracker</h3>
            </div>
            <div id="center" class="col-sm-4">
                <h5><br>© 2 группа УНЦ "Инфотех" <%= new java.text.SimpleDateFormat("dd.MM.yyyy").format( new java.util.Date()) %></h5>
            </div>
            <div class="col-sm-4">
                <div class="row">
                    <br>
                </div>
                <div id="right" class="row">
                    <div class="col-md-12 hidden-xs text-right">
                        <a href="https://github.com/SergeiPoliakov/NC_2grp_2016/" target="_blank"><i class="fa fa-2x fa-fw fa-github text-inverse"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>
