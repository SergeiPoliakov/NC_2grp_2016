/**
 * Created by Lawrence on 02.03.2017.
 */

function doAjaxNewMessages() {

    var inputText = "new_message";

    $.ajax({
        url : '/getNewNotification',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data : ({
            text: inputText
        }),
        success: function (data) {
            console.log(data);

            var result = 'Новых сообщений нет';
            if (data.count > 0){
                // result = data.text+': '+data.count; // 2017-05-21 А вывод справа временно отключим, зачем дублировать
                $( "#messageCount" ).attr( "data-count", data.count); // 2017-05-21 А это для вывода количества новыйх непрочитаннных сообщений слева, где уведомления в хедере
            }


            // $("#result_text_message").text(result); // 2017-05-21 А вывод справа временно отключим, зачем дублировать
        }
    });
}
setInterval(doAjaxNewMessages, 2000); // 10000

function doAjaxNotifications() {

    $.ajax({
        url : '/getNotification',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        success: function (data) {
            console.log(data);
            try {
                var notifications = JSON.parse(data.text);
                var arrayLength = notifications.length;
                for (var i = 0; i < notifications.length; i++) {
                    addNotification(notifications[i]);
                }
            } catch (error){
                console.log("Ошибка " + e.name + ":" + e.message + "\n" + e.stack);
            }
            doAjaxNotifications();
        }
    });
}
