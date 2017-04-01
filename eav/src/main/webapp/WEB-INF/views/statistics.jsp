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

        // 1
        //google.charts.load('current', {packages: ['corechart']});
        google.charts.load('current', {packages: ['corechart', 'line']});

        // Тут задаются параметры, какую статистику и как отрисовывать:
        //var plotview = "round";       // Вид диаграммы: plot - график | round - круговая диаграмма
        //var datatype = "meeting";   // Тип данных для диаграммы: activity - активность юзера за период | meeting - соотношение встреч | message - соотношение сообщений ...
        //var period = "day"; // Период выборки: hour - за последний час | day - за последний день | week - за последнюю неделю | month - за последний месяц | year - за последний год
        //var location_id = "myPieChart";

        google.charts.setOnLoadCallback(drawChart("round", "meeting", "day", "myPieChart"));
        google.charts.setOnLoadCallback(drawChart("plot", "meeting", "day", "chart_div"));

        function drawChart(plotview, datatype, period, location_id) { // Параметры - вид, тип данных, период выборки, позиция на странице

            // Получаем данные через AJAX-запрос и формируем массив для отрисовки графика
            $.ajax({
                url: '/getStat',
                type: 'POST',
                dataType: 'json',
                contentType: "application/json",
                mimeType: 'application/json',
                data: JSON.stringify({
                    plotview: plotview,
                    datatype: datatype,
                    period: period
                }),
                success: function (data) {

                    // Создаем массив под полученные данные:
                    var myArray = [data.length];
                    // Определяем тип диаграммы:
                    var data_plot = new google.visualization.DataTable();

                    // 1) Если хотим отрисовать курговоую диаграмму, такая логика:
                    if (plotview == "round"){
                        // Задаем поля:
                        data_plot.addColumn('string', 'Element');
                        data_plot.addColumn('number', 'Percentage');

                        // Переносим данные из JSON в массив:
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
                    // 2) Если же хотим отрисовать обычный график, такая логика:
                    if (plotview == "plot"){
                        // Задаем поля:
                        data_plot.addColumn('number', 'X');
                        data_plot.addColumn('number', 'Уровень');

                        // Переносим данные из JSON в массив:
                        for (var i = 0; i < data.length; i++) {
                            myArray[i] = [2];
                            myArray[i][0] = parseInt((data[i]).nkey, 10);
                            myArray[i][1] = parseInt((data[i]).nvalue, 10);
                        }

                        data_plot.addRows(myArray);
                        console.log(myArray);

                        // Задаем дополнительные опции - подписываем оси и легенду
                        var options = {
                            hAxis: {
                                title: 'Время, мин'
                            },
                            vAxis: {
                                title: 'Интенсивность работы'
                            },
                            backgroundColor: '#f1f8e9'
                        };

                        // Создаем и рисуем диаграмму:
                        var chart = new google.visualization.LineChart(document.getElementById(location_id));
                        chart.draw(data_plot, options);
                    }


                }
            });

        }

    </script>








</head>
<body>


<!-- Место для вставки диаграммы -->
<div id="myPieChart"></div>
<!-- Место для вставки графика -->
<div id="chart_div"></div>


</body>
</html>
