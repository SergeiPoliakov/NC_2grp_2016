/**
 * Created by Hroniko on 08.04.2017.
 */
function doAjaxFreeSlots() {

    var sendText = 'test';

    $.ajax({
        url: '/getFreeSlots',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        mimeType: 'application/json',
        data: JSON.stringify({
            user: str,
            meeting: m_id,
            start: "05.04.2017 00:00",
            end: "12.04.2017 00:00"
        }),
        success: function (data) {


            var result = '';

            var index;
            for (index = 0; index < data.length; ++index) {
                console.log(data[index]);

                result += '' +
                    '<li class="right clearfix"><span class="chat-img pull-right">';
                result += '</span>' +
                    '<div class="chat-body clearfix">' +
                    '<div class="header">' +
                    '<small class=" text-muted"><span class="glyphicon ' +
                    'glyphicon-time"></span>' + (data[index]).string_start + ' - ' + (data[index]).string_end + '</small>' +
                    '<strong class="pull-right primary-font">' + 'Свободный слот для встречи' +
                    '</strong>' +
                    '</div>' +
                    '<p>' + (data[index]).string_start + ' - ' + (data[index]).string_end +
                    '</p>' +
                    '</div>' +
                    '</li>';


            }
            $("#result_array").html(result);

        }
    });
}
setInterval(doAjaxFreeSlots, 1000);

