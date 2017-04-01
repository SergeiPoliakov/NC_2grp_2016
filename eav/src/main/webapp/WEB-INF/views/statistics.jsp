<%--
  Created by IntelliJ IDEA.
  User: Hroniko
  Date: 31.03.2017
  Time: 10:50
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
    <title>Статистики :: тестовая версия</title>

    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.min.js"></script>

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

</head>
<body>


<!-- Место для вставки диаграммы -->
<div id="location_1"></div>
<!-- Место для вставки графика -->
<div id="location_2"></div>


</body>
</html>
