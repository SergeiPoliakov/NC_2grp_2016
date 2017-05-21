/**
* Created by Hroniko on 12.05.2017.
*/

// Все функции для работы с чатом встреч (подгрузка всех, части, отпрака)

// var v_message_id = 0; // не недо, есть на странице jsp
// var v_meeting_id = '${meeting.id}'; // не недо, есть на странице jsp

// 1 Функция для загрузки всех сообщений чата из сейвера (однократно при загрузке страницы)
function getAllMessagesChat() {
	document.getElementById("messageInput").value = '';

	$.ajax({
		url: '/getAllMessagesChat',
		type: 'POST',
		dataType: 'json',
		contentType: "application/json",
		mimeType: 'application/json',
		async: true,
		data: JSON.stringify({
			meeting_id: v_meeting_id,
			message_id: null,
			text: null
		}),


		success: function (data) {

			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);

                var result = '<li class="list-group-item" style=";background-color: rgb(244, 244, 244);"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+(data[i]).avatar+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><div class="notification-meta" style="text-align: right;"><small class="timestamp">'+(data[i]).date_send+'</small></div><p class="notification-title"><a href="user'+(data[i]).from_id+'">'+(data[i]).from_name+ '</a>: '+(data[i]).text+'</p></div></div></li>';

				$("#insert_place_messages").append(result); // в элемент с id="insert_place_messages"
			}

			if (data.length > 0){
                $("#cardsholderItems").mCustomScrollbar("scrollTo", "bottom");
				v_message_id = (data[data.length - 1]).id; // И помещаем айди последнего (в списке) сообщения в переменную, чтобы потом знать, с какого номера запрашивать
			}

		}
	});
}
setTimeout(getAllMessagesChat, 500); // Однократный вызов функции загрузки всех имеющихся в системе (сейвере) сообщений


// 2 Функция для загрузки некоторых (начиная с определенного айди) сообщений данной встречи (периодически с интервалом 1 сек)
function getMessagesChatAfterId() {

	$.ajax({
		url: '/getMessagesChatAfterId',
		type: 'POST',
		dataType: 'json',
		contentType: "application/json",
		mimeType: 'application/json',
		async: true,
		data: JSON.stringify({
			meeting_id: v_meeting_id,
			message_id: v_message_id,
			text: null
		}),


		success: function (data) {

			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);

                var result = '<li class="list-group-item" style=";background-color: rgb(244, 244, 244);"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+(data[i]).avatar+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><div class="notification-meta" style="text-align: right;"><small class="timestamp">'+(data[i]).date_send+'</small></div><p class="notification-title"><a href="user'+(data[i]).from_id+'">'+(data[i]).from_name+ '</a>: '+(data[i]).text+'</p></div></div></li>';

                $("#insert_place_messages").append(result); // в элемент с id="insert_place_messages"
			}
			if (data.length > 0){
                $("#cardsholderItems").mCustomScrollbar("scrollTo", "bottom");
				v_message_id = (data[data.length - 1]).id; // И помещаем айди последнего (в списке) сообщения в переменную, чтобы потом знать, с какого номера запрашивать
			}
		}
	});
}
setInterval(getMessagesChatAfterId, 1000); //Многократный вызов функции загрузки некоторых (начиная с определенного айди) сообщений данной встречи

// 3 Функция для отправки сообщения в сейвер и последующей загрузки некоторых (начиная с определенного айди) сообщений данной встречи (по нажатию кнопки)
function sendMessageChat() {
	var messageInput = $("#messageInput").val();
	document.getElementById("messageInput").value = '';

	$.ajax({
		url: '/sendMessageChat',
		type: 'POST',
		dataType: 'json',
		contentType: "application/json",
		mimeType: 'application/json',
		async: true,
		data: JSON.stringify({
			meeting_id: v_meeting_id,
			message_id: v_message_id,
			text: messageInput
		}),


		success: function (data) {

			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);

                var result = '<li class="list-group-item" style=";background-color: rgb(244, 244, 244);"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+(data[i]).avatar+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><div class="notification-meta" style="text-align: right;"><small class="timestamp">'+(data[i]).date_send+'</small></div><p class="notification-title"><a href="user'+(data[i]).from_id+'">'+(data[i]).from_name+ '</a>: '+(data[i]).text+'</p></div></div></li>'

                $("#insert_place_messages").append(result); // в элемент с id="insert_place_messages"
			}
			if (data.length > 0){
                $("#cardsholderItems").mCustomScrollbar("scrollTo", "bottom");
				v_message_id = (data[data.length - 1]).id; // И помещаем айди последнего (в списке) сообщения в переменную, чтобы потом знать, с какого номера запрашивать
			}

			var result = '';
			$("#messageInput").text(result);
			var text_max = 70;
			$('#textarea_feedback').html('Осталось символов: ' + text_max);
		}
	});

}

