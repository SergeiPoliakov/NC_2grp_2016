/**
 * Created by Lawrence on 02.03.2017.
 */

function doAjax() {

    var inputText = "0";

    $.ajax({
        url : 'http://localhost:8081/getNewMessage',
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

            $("#result_text").text(result);
        }
    });
}
setInterval(doAjax, 10000);