/**
 * Created by Hroniko on 30.03.2017.
 */

var suggest_count = 0;
var input_initial_value = '';
var suggest_selected = 0;

$(window).load(function () {
    // читаем ввод с клавиатуры
    $("#search_box").keyup(function (I) {
        // определяем какие действия нужно делать при нажатии на клавиатуру
        switch (I.keyCode) {
            // игнорируем нажатия на эти клавишы
            case 13:  // enter
            case 27:  // escape
            case 38:  // стрелка вверх
            case 40:  // стрелка вниз
                break;

            default:
                // производим поиск только при вводе более 2х символов
                if ($(this).val().length < 3) {
                    // Прячем подсказку
                    $('#search_advice_wrapper').hide();
                }
                else {
                    input_initial_value = $(this).val();

                    $.ajax({

                        url: "/getTags",
                        type: 'POST',
                        dataType: 'json',
                        contentType: "application/json",
                        mimeType: 'application/json',
                        data: JSON.stringify({
                            type: "user",       // user | meeting
                            operation: "or",   // and | or
                            text: input_initial_value
                        }),

                        success: function (data) {
                            var index;
                            suggest_count = data.length;
                            if (suggest_count > 0) {
                                // перед показом слоя подсказки, его обнуляем
                                $("#search_advice_wrapper").html("").show();
                                for (index = 0; index < data.length; ++index) {
                                    console.log(data[index]);
                                    if ((data[index]).text != '') {
                                        // добавляем слою позиции
                                        $('#search_advice_wrapper').append('<div class="advice_variant">' + ' [' + (data[index]).id + '] ' + (data[index]).text + '</div>');
                                    }
                                }
                            }

                        },
                        error: function () { // если ничего не нашли
                            // Прячем подсказку
                            $('#search_advice_wrapper').hide();
                        }
                    });

                }
                break;
        }
    });

    //считываем нажатие клавишь, уже после вывода подсказки
    $("#search_box").keydown(function (I) {
        switch (I.keyCode) {
            // по нажатию клавишь прячем подсказку
            case 13: // enter
            case 27: // escape
                $('#search_advice_wrapper').hide();
                return false;
                break;
            // делаем переход по подсказке стрелочками клавиатуры
            case 38: // стрелка вверх
            case 40: // стрелка вниз
                I.preventDefault();
                if (suggest_count) {
                    //делаем выделение пунктов в слое, переход по стрелочкам
                    key_activate(I.keyCode - 39);
                }
                break;
        }
    });

    // делаем обработку клика по подсказке
    $('body').on('click', 'div.advice_variant', function () {

        // Разбираем строку на подстроки, чтобы исключить цифры - количество использования
        var substringArray = $(this).text().split(" ");
        // ставим текст в input поиска
        $('#search_box').val(substringArray[2]);

        // прячем слой подсказки
        $('#search_advice_wrapper').fadeOut(350).html('');
    });

    // если кликаем в любом месте сайта, нужно спрятать подсказку
    $('html').click(function () {
        $('#search_advice_wrapper').hide();
    });
    // если кликаем на поле input и есть пункты подсказки, то показываем скрытый слой
    $('#search_box').click(function (event) {
        //alert(suggest_count);
        if (suggest_count)
            $('#search_advice_wrapper').show();
        event.stopPropagation();
    });
})
;

function key_activate(n) {
    $('#search_advice_wrapper div').eq(suggest_selected - 1).removeClass('active');

    if (n == 1 && suggest_selected < suggest_count) {
        suggest_selected++;
    } else if (n == -1 && suggest_selected > 0) {
        suggest_selected--;
    }

    if (suggest_selected > 0) {
        $('#search_advice_wrapper div').eq(suggest_selected - 1).addClass('active');

        // Разбираем строку на подстроки, чтобы исключить цифры - количество использования
        var substringArray = $('#search_advice_wrapper div').eq(suggest_selected - 1).text().split(" ");
        // ставим текст в input поиска
        $('#search_box').val(substringArray[2]);
    } else {

        // Разбираем строку на подстроки, чтобы исключить цифры - количество использования
        var substringArray = $('#search_advice_wrapper div').eq(suggest_selected - 1).text().split(" ");
        // ставим текст в input поиска
        $('#search_box').val(substringArray[2]);
        $("#search_box").val(input_initial_value);
    }
}

function getFind() {
    input_initial_value = $("#search_box").val();
    document.getElementById("search_box").value = '';

    $.ajax({
        url: "/getFind",
        type: 'POST',
        dataType: 'json',
        contentType: "application/json",
        mimeType: 'application/json',
        data: JSON.stringify({
            type: "user",       // user | meeting
            operation: "or",   // and | or
            text: input_initial_value
        }),
        success: function (data) {

        }
    });

    var delay = 1000;
    // и с задержкой переходим на страницу с результатами
    setTimeout("document.location.href='/searchUser'", delay);
}

function check() {
    if ($('#search_box').val() != '')
        $('#btn-find').removeAttr('disabled');
    else
        $('#btn-find').attr('disabled','disable');
}
