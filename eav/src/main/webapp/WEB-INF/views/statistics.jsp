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
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(drawChart);
        function drawChart() {
            // Определяем тип диаграммы:
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Element');
            data.addColumn('number', 'Percentage');
            data.addRows([
                ['Общие встречи', 0.68],
                ['Принятые встречи', 0.21],
                ['Отказы', 0.11]
            ]);

            // Создаем и рисуем диаграмму:
            var chart = new google.visualization.PieChart(document.getElementById('myPieChart'));
            chart.draw(data, null);
        }

    </script>

</head>
<body>


<!-- Место для вставки диаграммы -->
<div id="myPieChart"/>


</body>
</html>
