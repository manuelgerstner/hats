$(function() {

	window.progressBar = new ProgressBar('#progressBar');
	window.session = false;
	// default socket eventData json, will be reused but never overwritten
	window.eventData = {
		thinkingSessionId: SESSION_ID,
		userId: USER_ID,
		hat: HAT
	};


	// setup initial card setup
	if (typeof CARDS !== "undefined" && CARDS.length > 0) {
		$(CARDS).each(function() {
			injectCard(this);
		});
	}

	$('#modal-button').click(function() {
		/* new user? */
		var isNewUser = (USER_NAME === "New User");
		/* force new user to enter name */
		if (isNewUser) {
			var name = $('#modal-username');
			if (name.val() === "") {
				name.parent().addClass('has-error');
			} else {
				jsRoutes.controllers.Users.saveName().ajax({
					data : {
						"name" : name.val()
					},
					success : function() {
						USER_NAME = name.val();
						name.parent().removeClass('has-error');
						$('#first-time').remove();
						$('#hatchange-modal').modal('hide');
					}
				});
			}
		} else {
			$('#hatchange-modal').modal('hide');
		}
	});
	
	$("#btnAddCard").click(function() {
		var newCard = $.extend({}, eventData, {
			"content" : $("#content").val()
		});
		window.session.call(CALL_URI + "#addCard", newCard);
		// store 
		CARDS.push(newCard);
	});

	$('#indicate-ready').click(function() {
		window.session.call(CALL_URI + "#moveHat", eventData);
	});	


	$(document).on('click', '.addbucket', function() {
		window.session.call(CALL_URI + "#addBucket", eventData);
	});

	$(document).on('blur', '.bucketname', function() {
		window.session.call(CALL_URI + "#renameBucket", $.extend({}, eventData, {
			name: $(this).val(),
			bucketId: $(this).data('bucketid')
		}));
	});
	$(document).on('click', 'a.fancybox', function(e) {
		e.preventDefault();
		var options = {
			speed : "slow"
		}
		var href = $(this).attr("href");
		if (!!~href.indexOf("youtube.com/watch?v=")) {
			$(this).attr("href", href.replace("watch?v=", "v/"));
			options.type = "swf";
			//options.swf= {"wmode":'transparent','allowfullscreen':'true'}
		}
		$.fancybox.open(this, options);
	});

	$('#indicate-finish').click(function() {
		location.href = "/" + SESSION_ID + "/dashboard";
	});


});
// get websocket up and running

function instantiateSocket() {
	ab.connect(WSURI, function(session) {
		// store session
		window.session = session;
		session.subscribe(SESSION_ID.toString(), onEvent);
	},

	// WAMP session is gone
	function(code, reason) {
		if (confirm('an error occured, reload?'))
			location.reload();
	},
	// additional options
	{
		skipSubprotocolCheck : true,
		skipSubprotocolAnnounce : true
	}); // Important! Play rejects all subprotocols for some reason...
}

function setSessionData() {
    $('#session-info').html('Session time: ' + timeSince(CREATION_TIME));
    var date = $.datepicker.formatDate('dd/mm/yy', new Date(CREATION_TIME));
    $('#feed-creation-time').html('Session was created on <strong>' + date + '</strong> by <strong>' + CREATOR_NAME) + '</strong>';
}

// debugging handler for websocket events coming in

function onEvent(topic, event) {

	console.log("received event %s, data: ", event.eventType, event.eventData);

	// this is dirty.
    if (event.eventType === "addCard") {
        injectCard(event.eventData);
    } else if (event.eventType === "moveHat") {
        moveTo(event.eventData.hat);
    } else if (event.eventType === "addBucket") {
    	injectBucket(event.eventData);
    } else if (event.eventType === "renameBucket") {
    	renameBucket(event.eventData);
    } else if (event.eventType === "moveCardToBucket") {
    } else if (event.eventType === "userJoin") {
    	feedUserJoin(event.eventData);
    }
}

function moveTo(hat) {
	// CSS changes for mood
	$('#hat').removeClass().addClass(hat.toLowerCase());
	$('body').removeClass().addClass(hat.toLowerCase());
	$('#form-hat').val(hat.toLowerCase());

	// change tooltip text for input
	var modal = $('#hatchange-modal');
	$('.hat', modal).html(hat.toLowerCase());
	$('.message', modal).html(TOOLTIPS[hat.toLowerCase()]);

	modal.modal();

	// overwrite global HAT var
	HAT = hat.toLowerCase();
	window.eventData.hat = HAT;

	location.hash = HAT;

	if (HAT === "blue") {
		prepareBlueHat();
	}

	window.progressBar.render()

	setSessionData();

}

function injectBucket(bucket) {
	var template = Handlebars.compile($('#bucket-template').html());
	var compiled = template(bucket).toString(); // workaround   
	// workaround
	$('#buckets-list').append(compiled).find('div.bucket').droppable(options.droppable);
}


function renameBucket(eventData) {
	var bucketId = ''+eventData.bucketId,
		bucketName = eventData.bucketName;
	var bucket = $('div#bucket-' + bucketId)
	bucket.find("h4").removeClass("hide").text(bucketName);
	bucket.find("input").remove();
}

function enableDragDrop() {
	$('#cards-list div.card').draggable(options.draggable);
}

function prepareBlueHat() {
	enableDragDrop();
	// toggle buttons
	$('#indicate-finish').removeClass('hide');
	$('#indicate-ready').addClass('hide');

	// enable buckets here
	$('#buckets').removeClass("hide"); 
}

function injectCard(card) {

	if (card.content.trim() === "")
		return;
	card.content = linkify(card.content);

	var template = Handlebars.compile($('#card-template').html());
	var compiled = template(card);

	$('#cards-list').append(compiled);
	// reset card content field
	$('#content').val("");
	$('#nocardsyet').remove();

	window.progressBar.add(card);

	if (HAT === "blue") {
		// do this again for all cards (easier than grabbing just added card)
		enableDragDrop();
	}

}

function feedUserJoin(user) {
	$('#feed').append('<li>User <strong>' + user.username + '</strong> joined.</li>');
}