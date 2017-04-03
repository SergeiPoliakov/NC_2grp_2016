<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 03.04.2017
  Time: 15:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<html lang="en">
<head>

    <!-- <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script> -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.touchSwipe.min.js"></script>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>


    <script type="text/javascript">

        google.charts.load('current', {packages: ['corechart', 'line']});

        // Нужно подгрузить настройки сначала и по ним определить, что выводить в статистики, а что нет

        google.charts.setOnLoadCallback(getStatSettings);
        function getStatSettings() {

            // Получаем настройки через AJAX-запрос и формируем массив настроек
            $.ajax({
                url: '/getStatSettings',
                type: 'POST',
                dataType: 'json',
                contentType: "application/json",
                mimeType: 'application/json',
                async: true,
                data: JSON.stringify({
                    plotview: "settings",
                    datatype: null,
                    period: null
                }),
                success: function (dataSetting) {
                    // Пробегаем по всему массиву переданных параметров и вытаскиваем настройки
                    for (var j = 0; j < dataSetting.length; j++) {

                        var plotview = (dataSetting[j]).plotview;
                        var state = (dataSetting[j]).state;
                        var datatype = (dataSetting[j]).datatype;
                        var period = (dataSetting[j]).period;
                        var location_id = (dataSetting[j]).location_id;
                        var xlabel = (dataSetting[j]).xlabel;
                        var ylabel = (dataSetting[j]).ylabel;

                        if (state == "on"){ // Если значение ключа текущей настройки ON, то надо выводить статистику
                            if (plotview == "round"){
                                // 1
                                google.charts.setOnLoadCallback(drawChart());
                                function drawChart() {
                                    // Определяем тип диаграммы:
                                    var data_plot = new google.visualization.DataTable();
                                    data_plot.addColumn('string', 'Element');
                                    data_plot.addColumn('number', 'Percentage');
                                    // Получаем данные через AJAX-запрос и формируем массив для отрисовки графика
                                    $.ajax({
                                        url: '/getStat',
                                        type: 'POST',
                                        dataType: 'json',
                                        contentType: "application/json",
                                        mimeType: 'application/json',
                                        async: false,
                                        data: JSON.stringify({
                                            plotview: plotview,
                                            datatype: datatype,
                                            period: period
                                        }),
                                        success: function (data) {


                                            var myArray = [data.length];
                                            for (var i = 0; i < data.length; i++) {
                                                myArray[i] = [2];
                                                myArray[i][0] = (data[i]).skey;
                                                myArray[i][1] = parseFloat((data[i]).nvalue);

                                            }

                                            data_plot.addRows(myArray);
                                            console.log(myArray);

                                            // Создаем и рисуем диаграмму:
                                            var chart = new google.visualization.PieChart(document.getElementById(location_id));
                                            chart.draw(data_plot, null);
                                        }
                                    });

                                }

                            }

                            if (plotview == "plot"){
                                // 2
                                google.charts.setOnLoadCallback(drawBackgroundColor());

                                function drawBackgroundColor() {
                                    var data_plot = new google.visualization.DataTable();
                                    data_plot.addColumn('number', 'X');
                                    data_plot.addColumn('number', 'Уровень');

                                    // Получаем данные через AJAX-запрос и формируем массив для отрисовки графика
                                    $.ajax({
                                        url: '/getStat',
                                        type: 'POST',
                                        dataType: 'json',
                                        contentType: "application/json",
                                        mimeType: 'application/json',
                                        async: false,
                                        data: JSON.stringify({
                                            plotview: plotview,
                                            datatype: datatype,
                                            period: period
                                        }),
                                        success: function (data) {

                                            var myArray = [data.length];
                                            for (var i = 0; i < data.length; i++) {
                                                myArray[i] = [2];
                                                myArray[i][0] = parseInt((data[i]).nkey, 10);
                                                myArray[i][1] = parseInt((data[i]).nvalue, 10);

                                            }

                                            data_plot.addRows(myArray);
                                            console.log(myArray);

                                            var options = {
                                                hAxis: {
                                                    title: xlabel
                                                },
                                                vAxis: {
                                                    title: ylabel
                                                },
                                                backgroundColor: '#f1f8e9'
                                            };

                                            var chart = new google.visualization.LineChart(document.getElementById(location_id));
                                            chart.draw(data_plot, options);

                                        }
                                    });

                                }
                            }
                        }

                    }

                }
            });

        }

    </script>




    <style type="text/css">
        body, html {
            height: 100%;
            margin: 0;
            overflow:hidden;
            font-family: helvetica;
            font-weight: 100;
        }
        .container {
            position: relative;
            height: 100%;
            width: 100%;
            left: 0;
            -webkit-transition:  left 0.4s ease-in-out;
            -moz-transition:  left 0.4s ease-in-out;
            -ms-transition:  left 0.4s ease-in-out;
            -o-transition:  left 0.4s ease-in-out;
            transition:  left 0.4s ease-in-out;
        }
        .container.open-sidebar {
            left: 340px;
        }

        .swipe-area {
            position: absolute;
            width: 50px;
            left: 0;
            top: 0;
            height: 100%;
            background: #f3f3f3;
            z-index: 0;
        }
        #sidebar {
            background: #2c3e50;
            position: absolute;
            width: 340px;
            height: 100%;
            left: -340px;
            box-sizing: border-box;
            -moz-box-sizing: border-box;
        }
        #sidebar ul {
            margin: 0;
            padding: 0;
            list-style: none;
        }
        #sidebar ul li {
            margin: 0;
        }
        #sidebar ul li a {
            padding: 15px 20px;
            font-size: 16px;
            font-weight: 100;
            color: white;
            text-decoration: none;
            display: block;
            border-bottom: 1px solid #2c3e50;
            -webkit-transition:  background 0.3s ease-in-out;
            -moz-transition:  background 0.3s ease-in-out;
            -ms-transition:  background 0.3s ease-in-out;
            -o-transition:  background 0.3s ease-in-out;
            transition:  background 0.3s ease-in-out;
        }
        #sidebar ul li:hover a {
            background: #2c3e50;
        }
        .main-content {
            width: 100%;
            height: 100%;
            padding: 10px;
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            position: relative;
        }
        .main-content .content{
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            padding-left: 60px;
            width: 100%;
        }
        .main-content .content h1{
            font-weight: 100;
        }
        .main-content .content p{
            width: 100%;
            line-height: 160%;
        }
        .main-content #sidebar-toggle {
            background: #2c3e50;
            border-radius: 3px;
            display: block;
            position: relative;
            padding: 10px 7px;
            float: left;
        }
        .main-content #sidebar-toggle .bar{
            display: block;
            width: 18px;
            margin-bottom: 3px;
            height: 2px;
            background-color: #fff;
            border-radius: 1px;
        }
        .main-content #sidebar-toggle .bar:last-child{
            margin-bottom: 0;
        }
    </style>
    <script type="text/javascript">
        $(window).load(function(){
            $("[data-toggle]").click(function() {
                var toggle_el = $(this).data("toggle");
                $(toggle_el).toggleClass("open-sidebar");
            });
            $(".swipe-area").swipe({
                swipeStatus:function(event, phase, direction, distance, duration, fingers)
                {
                    if (phase=="move" && direction =="right") {
                        $(".container").addClass("open-sidebar");
                        return false;
                    }
                    if (phase=="move" && direction =="left") {
                        $(".container").removeClass("open-sidebar");
                        return false;
                    }
                }
            });
        });

    </script>
</head>
<body>
<div class="container">
    <div id="sidebar">
        <ul>
            <li><a href="#">Статистика 1</a><div id="location_1"></div></li>
            <li><a href="#">Статистика 2</a><div id="location_2"></div></li>
            <li><a href="#">Статистика 3</a></li>
            <li><a href="#">Статистика 4</a></li>
        </ul>
    </div>
    <div class="main-content">
        <div class="swipe-area"></div>
        <a href="#" data-toggle=".container" id="sidebar-toggle">
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
        </a>
        <div class="content">
            <h1>Страница статистик с боковым меню (тестовая)</h1>
            <p>Нажмите на копку слева в виде трех линий, раскроется меню со статистиками</p>
        </div>
    </div>
</div>

<!-- Место для вставки диаграммы -->
<!-- <div id="location_1"></div> -->
<!-- Место для вставки графика -->
<!-- <div id="location_2"></div> -->


</body>
</html>
