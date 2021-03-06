﻿//Скрыть/показать уведомления
	$(document).click(function(event) { 	
		if(!$(event.target).closest('#notificationDrop').length) {
			if ($( "#notificationDrop" ).hasClass("open"))
				$( "#notificationDrop" ).toggleClass( "open" );
		}
		else
			if (!$( "#notificationDrop" ).hasClass("open"))
				$( "#notificationDrop" ).toggleClass( "open" );
	})	
	$( "#notificationDropa" ).click(function(event) {
		$( "#notificationDrop" ).toggleClass( "open" );
		event.stopPropagation();
	});

	// Изменение состояния уведомления при наведении на него	
	$(document).on("mouseenter", ".notification", function(e) {
		if ( $( this ).hasClass( "active" ) ){				
			$( this ).removeClass( "active" );
			var notificationCount = $( "#notificationCount" ).attr( "data-count") - 1;
			$( "#notificationCount" ).attr( "data-count", notificationCount); 
			//$( "#notificationSecondCounter" ).html("Уведомления (" + notificationCount + ")");
		}
	});
	
	// Add notifications	
	function notificationCountIncrease(){
		var notificationCount = Number($( "#notificationCount" ).attr( "data-count"))+ 1;
		$( "#notificationCount" ).attr( "data-count", notificationCount);
		
		var thenum = Number($( "#notificationSecondCounter" ).html().replace ( /[^\d.]/g, '' )) + 1; 
		$( "#notificationSecondCounter" ).html("Уведомления (" + thenum + ")");		
	}
	
	function addNotification(data){
		switch(data.type) {
			case 'friendRequest':  
				$("#notificationHolder").prepend('<li class="notification active"><div class="media"><div class="media-left"><div class="media-object"><img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name"/></div></div><div class="media-body"><strong class="notification-title"><a href="#'+data.senderID+'">Иванов Иван</a> отправил Вам заявку в друзья</strong><div class="notification-meta"><small class="timestamp">01. 09. 2015, 08:00</small></div><div class="notification-userbuttons"><a href="#acceptFriend"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Добавить</span> </button></a><a href="#declineFriend"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a></div></div></div></li>');
				break;
			case 'meetingInvite':  
				$("#notificationHolder").prepend('<li class="notification active"><div class="media"><div class="media-left"><div class="media-object"><img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name"/></div></div><div class="media-body"><strong class="notification-title"><a href="#'+data.senderID+'">Вася Пупкин</a> пригласил Вас на встречу <a href="#'+data.meetingID+'">Новая встреча</a></strong><div class="notification-meta"><small class="timestamp">27. 11. 2017, 15:00</small></div><div class="notification-userbuttons"><a href="#acceptInvite"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Принять</span> </button></a><a href="#declineInvite"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отказаться</span></button></a></div></div></div></li>');
				break;				
			case 'meetingRequest':  
				$("#notificationHolder").prepend('<li class="notification active"><div class="media"><div class="media-left"><div class="media-object"><img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name"/></div></div><div class="media-body"><strong class="notification-title"><a href="#'+data.senderID+'">Семен Станиславович</a> хочет принять участие в встрече <a href="#'+data.meetingID+'">Новая встреча 2</a></strong><div class="notification-meta"><small class="timestamp">27. 11. 2017, 15:00</small></div><div class="notification-userbuttons"><a href="#inviteAtMeeting"><button type="button" class="btn btn-success btn-sm"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Пригласить</span> </button></a><a href="#declineRequest"><button type="button" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a></div></div></div></li>');
				break;			
			case 'infoFriendAccept':  
				$("#notificationHolder").prepend('<li class="notification active"><div class="media"><div class="media-left"><div class="media-object"><img src="https://s-media-cache-ak0.pinimg.com/736x/dd/45/96/dd4596b601062eb491ea9bb8e3a78062.jpg" class="img-circle" alt="Name"/></div></div><div class="media-body"><strong class="notification-title"><a href="#'+data.senderID+'">Василий Степанов</a> принял вашу заявку в друзья</strong><div class="notification-meta"><small class="timestamp">27. 10. 2015, 08:00</small></div></div></div></li>');
				break;
			default:
				return;
		}
		notificationCountIncrease();
	}
	
	// TEST BLOCK
		var data1 = {senderID: 221, meetingID: 225, type: "friendRequest"};
		var data2 = {senderID: 222, meetingID: 226, type: "meetingInvite"};
		var data3 = {senderID: 223, meetingID: 227, type: "meetingRequest"};
		var data4 = {senderID: 224, meetingID: 228, type: "infoFriendAccept"};
		
		addNotification(data1);
		addNotification(data2);
		addNotification(data3);
		addNotification(data4);
		addNotification(data1);
	// END TEST BLOCK