//Скрыть/показать уведомления
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

            var JSONMessage = JSON.stringify({
                'messageID': $( this ).attr('id'),
				'recieverID': $( this ).attr('recieverID')
            });
            stompClient.send("/app/updateNotificationState", {}, JSONMessage);
		}
	});

	// Отметка всех уведомлений как прочитанных
	$( "#markAllAsRead" ).click(function() {
        var matches = [];
        var searchEles = document.getElementById("notificationHolder").children;
        var recieverID =  $( searchEles[0] ).attr('recieverID');

        for (var i = 0; i < searchEles.length; i++)
            $( searchEles[i] ).removeClass( "active" );
        $( "#notificationCount" ).attr( "data-count", 0);
        var JSONMessage = JSON.stringify({
            'type': 'all',
            'recieverID': recieverID
        });
        stompClient.send("/app/updateNotificationState", {}, JSONMessage);
	});
	
	// Увеличение счётчика уведомлений
	function notificationCountIncrease(){
		var notificationCount = Number($( "#notificationCount" ).attr( "data-count"))+ 1;
		$( "#notificationCount" ).attr( "data-count", notificationCount);
		
		var thenum = Number($( "#notificationSecondCounter" ).html().replace ( /[^\d.]/g, '' )) + 1; 
		$( "#notificationSecondCounter" ).html("Уведомления (" + thenum + ")");		
	}

	// Удаление уведомления
	function removeNotification(something){
        var JSONMessage = JSON.stringify({
		 	'messageID': $( something ).attr('id'),
		 	'recieverID': $( something ).attr('recieverID')
		 });
        stompClient.send("/app/removeNotification", {}, JSONMessage);
	}
	
	function addNotification(data){
		if (!$("#notificationEmpty").hasClass("hidden" ))
            $("#notificationEmpty").addClass("hidden" );
		switch(data.type) {
			case 'friendRequest':
				var type = 'infoFriendAccept';
                $("#notificationHolder").prepend('<li class="notification '+data.isSeen+'" id ="'+data.messageID+'" recieverID="'+data.recieverID+'"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+data.senderPic+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><p class="notification-title"><a href="user'+data.senderID+'">'+data.senderName+ '</a> отправил Вам заявку в друзья</p><div class="notification-meta"><small class="timestamp">'+data.date+'</small></div><div class="notification-userbuttons"><a href="/addFriend/'+data.senderID+'/acceptFriend" ><button type="button" class="btn btn-success btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'" onclick="removeNotification(this)"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Добавить</span> </button></a><a href="/declineFriend/'+data.senderID +'/" ><button type="button" class="btn btn-danger btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'" onclick="removeNotification(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a></div></div></div></li>');
				break;
			case 'meetingInvite':
				$("#notificationHolder").prepend('<li class="notification '+data.isSeen+'" id ="'+data.messageID+'" recieverID="'+data.recieverID+'"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+data.senderPic+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><p class="notification-title"><a href="user'+data.senderID+'">'+data.senderName+ '</a> пригласил Вас на встречу <a href="/viewMeeting/'+data.additionalID+'">'+data.meetingName+'</a></p><div class="notification-meta"><small class="timestamp">'+data.date+'</small></div><div class="notification-userbuttons"><a href="/inviteUserAtMeeting/'+data.additionalID+'/'+data.recieverID+'/"><button type="button" class="btn btn-success btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'"  onclick="removeNotification(this)"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Принять</span> </button></a><a href="/declineInviteMeeting/'+data.additionalID +'/"><button type="button" class="btn btn-danger btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'" onclick="removeNotification(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отказаться</span></button></a></div></div></div></li>');
				break;				
			case 'meetingRequest':  
				$("#notificationHolder").prepend('<li class="notification '+data.isSeen+'" id ="'+data.messageID+'" recieverID="'+data.recieverID+'"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+data.senderPic+'" class="img-circle" alt="Name"/></div></div><div class="media-body"><p class="notification-title"><a href="user'+data.senderID+'">'+data.senderName+ '</a> хочет принять участие в встрече <a href="meeting'+data.additionalID+'">'+data.meetingName+'</a></p><div class="notification-meta"><small class="timestamp">'+data.date+'</small></div><div class="notification-userbuttons"><a href="/inviteUserAtMeeting/'+data.additionalID+'/'+data.senderID+'/"><button type="button" class="btn btn-success btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'" onclick="removeNotification(this)"><span class="glyphicon glyphicon-ok" aria-hidden="true"> Пригласить</span> </button></a><a href="/declineRequestMeeting/'+data.additionalID +'/'+data.senderID+'/"><button type="button" class="btn btn-danger btn-sm" id ="'+data.messageID+'" recieverID="'+data.recieverID+'" onclick="removeNotification(this)"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Отклонить</span></button></a></div></div></div></li>');
				break;			
			case 'infoFriendAccept':  
				$("#notificationHolder").prepend('<li class="notification '+data.isSeen+'" id ="'+data.messageID+'" recieverID="'+data.recieverID+'"><div class="media"><div class="media-left"><div class="media-object"><img id="notificationImage" src="'+data.senderPic+'" class="img-circle" alt="img"/></div></div><div class="media-body"><p class="notification-title"><a href="/user'+data.senderID+'">'+data.senderName+ '</a> принял вашу заявку в друзья</p><div class="notification-meta"><small class="timestamp">'+data.date+'</small></div></div></div></li>');
				break;
			default:
				return;
		}
		if (data.isSeen != "")
			notificationCountIncrease();
	}