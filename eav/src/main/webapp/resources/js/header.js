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
            if (data.count > 0)
                result = data.text+': '+data.count;

            $("#result_text_message").text(result);
        }
    });
}
setInterval(doAjaxNewMessages, 10000);

function doAjaxNewFriends() {

    var inputText = "new_friend";

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

            var result = 'Заявок в друзья нет';
            if (data.count > 0)
                result = data.text+': '+data.count;

            $("#result_text_friend").text(result);
        }
    });
}
setInterval(doAjaxNewFriends, 10000);

function doAjaxNotifications() {

    $.ajax({
        url : '/getNotification',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        success: function (data) {
            console.log(data);

            var notifications = JSON.parse(data.text);
            $.each(notifications, function(index, notification) {    // Iterate over the JSON array.
                   //alert(index + " " + notification.senderID);
                addNotification(notification);
            });

            //$( "#notificationCount" ).attr( "data-count", data.count);
        }
    });
}
setInterval(doAjaxNotifications, 10000);