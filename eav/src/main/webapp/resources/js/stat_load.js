/**
 * Created by Hroniko on 03.04.2017.
 */
google.charts.load('current', {packages: ['corechart', 'line', 'gauge']});

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
            // Сначала создаем необходимое количество карточек на странице:
            console.log(dataSetting.length);
            var count_div = (dataSetting.length / 2  | 0); // Необходимое количество полноценных сдвоенных карточек
            var count_mod = dataSetting.length - count_div*2;  // Необходимое количество одиночных карточек
            var count_loc = 1;
            for (var c = 0; c < count_div; c++){ // for (var c = 0; c < count_div; c++) // Создаем обычные карточки

                var block_col_lg_6 = '<div class="row">';

                block_col_lg_6 += '<div class="col-lg-6">';
                block_col_lg_6 += '<div id="location_' + count_loc + '" class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">';
                block_col_lg_6 += '<h4 class="card-title text-center">Загрузка статистики... Пожалуйста, подождите </h4>';
                block_col_lg_6 += '</div>';
                block_col_lg_6 += '</div>';
                count_loc ++;

                block_col_lg_6 += '<div class="col-lg-6">';
                block_col_lg_6 += '<div id="location_' + count_loc + '" class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">';
                block_col_lg_6 += '<h4 class="card-title text-center">Загрузка статистики... Пожалуйста, подождите </h4>';
                block_col_lg_6 += '</div>';
                block_col_lg_6 += '</div>';
                block_col_lg_6 += '</div>';
                count_loc ++;
                $("#insert_place_col-lg-6").append(block_col_lg_6); // в элемент с id="insert_place_col-lg-6"
            }

            for (var d = 0; d < count_mod; d++){ // Создаем одинарные карточки

                var block_col_lg_6 = '<div class="row">';

                block_col_lg_6 += '<div class="col-lg-6">';
                block_col_lg_6 += '<div id="location_' + count_loc + '" class="card card_statistic mCustomScrollbar" data-mcs-theme="minimal-dark">';
                block_col_lg_6 += '<h4 class="card-title text-center">Загрузка статистики... Пожалуйста, подождите </h4>';
                block_col_lg_6 += '</div>';
                block_col_lg_6 += '</div>';

                block_col_lg_6 += '</div>';
                count_loc ++;
                $("#insert_place_col-lg-6").append(block_col_lg_6); // вставляем в конец элемента с id="insert_place_col-lg-6"
            }


            // Пробегаем по всему массиву переданных параметров и вытаскиваем настройки
            for (var j = 0; j < dataSetting.length; j++) {

                var plotview = (dataSetting[j]).plotview;
                var state = (dataSetting[j]).state;
                var datatype = (dataSetting[j]).datatype;
                var period = (dataSetting[j]).period;
                var location_id = (dataSetting[j]).location_id;
                var xlabel = (dataSetting[j]).xlabel;
                var ylabel = (dataSetting[j]).ylabel;
                var title = (dataSetting[j]).title;

                if (state == "on") { // Если значение ключа текущей настройки ON, то надо выводить статистику
                    if (plotview == "round") {
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

                                    var options = {
                                        title: title
                                    };

                                    // Создаем и рисуем диаграмму:
                                    var chart = new google.visualization.PieChart(document.getElementById(location_id));
                                    chart.draw(data_plot, options);
                                }
                            });

                        }

                    }

                    if (plotview == "plot") {
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
                                        myArray[i][0] = parseInt((data[i]).nkey - data.length, 10);
                                        myArray[i][1] = parseInt((data[i]).nvalue, 10);

                                    }

                                    data_plot.addRows(myArray);
                                    console.log(myArray);

                                    var options = {
                                        title: title,
                                        hAxis: {
                                            title: xlabel
                                        },
                                        vAxis: {
                                            title: ylabel
                                        },
                                        curveType: 'function',
                                        legend: { position: 'bottom' } //,
                                        //backgroundColor: '#f1f8e9'
                                    };

                                    var chart = new google.visualization.LineChart(document.getElementById(location_id));
                                    chart.draw(data_plot, options);

                                }
                            });

                        }
                    }

                    /*
                    // Искусственное ухищрение, потом сделаю как надо
                    plotview = "gauge";
                    if (plotview == "gauge") { // То работаем с индикаторами
                        // 3
                        google.charts.setOnLoadCallback(drawChart());

                        function drawChart() {

                                var data = google.visualization.arrayToDataTable([
                                    ['Label', 'Value'],
                                    ['Файлы', 80],
                                    ['Теги', 55],
                                    ['СМС', 68]
                                ]);

                                var options = {
                                    title: "Использование ресурсов по отношению к выделенному лимиту, %",
                                    redFrom: 90, redTo: 100,
                                    yellowFrom:75, yellowTo: 90,
                                    minorTicks: 5
                                };

                                var chart = new google.visualization.Gauge(document.getElementById('location_3'));

                                chart.draw(data, options);

                                setInterval(function() {
                                    data.setValue(0, 1, 40 + Math.round(60 * Math.random()));
                                    chart.draw(data, options);
                                }, 13000);
                                setInterval(function() {
                                    data.setValue(1, 1, 40 + Math.round(60 * Math.random()));
                                    chart.draw(data, options);
                                }, 5000);
                                setInterval(function() {
                                    data.setValue(2, 1, 60 + Math.round(20 * Math.random()));
                                    chart.draw(data, options);
                                }, 26000);


                        }
                    }

                    */
                }

            }

        }
    });

}