$(function() {

	window.progressBar = new ProgressBar('#progressBar');
	window.session = false;

	// setup initial card setup
	if (typeof CARDS !== "undefined" && CARDS.length > 0) {
		$(CARDS).each(function() {
			addCard(this);
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
		var newCard = {
			"thinkingSession" : SESSION_ID,
			"hat" : HAT,
			"content" : $("#content").val(),
			"username" : USER_NAME,
			"userId" : USER_ID
		};
		window.session.call(CALL_URI + "#addCard", newCard);
		// store 
		CARDS.push(newCard);
	});

	$('#indicate-ready').click(function() {
		window.session.call(CALL_URI + "#moveHat", {
			"thinkingSession" : SESSION_ID,
			"hat" : HAT
		});
	});	


	$(document).on('click', '.addbucket', function() {
		window.session.call(CALL_URI + "#addBucket");
	});
	$(document).on('blur', '.bucketname', function() {
		window.session.call(CALL_URI + "#renameBucket", {
			name: $(this).val(),
			bucketId: $(this).data('bucketId')
		});
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
		//console.log("Subscribed to session number " + SESSION_ID);
		// publish join session for all participants
		var newUser = {
			"thinkingSession" : SESSION_ID,
			"hat" : HAT,
			"username" : USER_NAME,
			"userId" : USER_ID
		}
		var userJoinedEvent = {
			"eventType" : "userJoined",
			"eventData" : newUser
		}
		var message = JSON.stringify(userJoinedEvent);
		session.call(CALL_URI + "#userJoined", message);
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
    $('#session-info').html('Session time: ' + timeSince(new Date(CREATION_TIME)));
    var date = $.datepicker.formatDate('dd/mm/yy', new Date(CREATION_TIME));
    $('#feed-creation-time').html('Session was created on <strong>' + date + '</strong> by <strong>' + CREATOR_NAME + '</strong>');
}

function timeSince(date) {

    var seconds = Math.floor((new Date() - date) / 1000);

    var interval = Math.floor(seconds / 31536000);

    interval = Math.floor(seconds / 3600);
    if (interval > 1) {
        return interval + " hours";
    }

    interval = Math.floor(seconds / 60);
    if (interval > 1) {
        return interval + " minutes";
    } else {
    	return "less than a minute"
    }
}

// debugging handler for websocket events coming in

function onEvent(topic, event) {
    if (event.eventType === "addCard") {
        addCard(event.eventData, true);
    } else if (event.eventType === "moveHat") {
        moveTo(event.eventData.hat);
    } else if (event.eventType === "addBucket") {
    	addBucket(event.eventData);
    } else if (event.eventType === "renameBucket") {
    	renameBucket(event.eventData);
    } else if (event.eventType === "userJoin") {
    	console.log(event);
    }
    // window.progressBar.add(event.eventData);
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
	location.hash = HAT;

	if (HAT === "blue") {
		prepareBlueHat();
	}

	window.progressBar.render()

	setSessionData();

}

// bucket should be {id, name}

function addBucket() {
	// get bucket info from server
	console.log("send this through the websocket");
	window.session.call(CALL_URI +"#addBucket");

}

function injectBucket(bucket) {
	var template = Handlebars.compile($('#bucket-template').html());
	var compiled = template(bucket).toString(); // workaround   
	// workaround
	$('#buckets-list').append(compiled).find('div.bucket').droppable(options.droppable);
}


function renameBucket(elem) {
	// post bucketname to server
	var name = $(elem).val();
	// ajax to bucket name change
	var bucketId = $(elem).data("bucketid");
	jsRoutes.controllers.Cards.renameBucket(bucketId).ajax({
		data : {
			"name" : name
		},
		// have to use complete since no data is returned
		complete : function(e) {
			$(elem).parent().find("h4").removeClass("hide").text(name);
			$(elem).remove();
		}
	});
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

function addCard(card, effect) {

	if (card.content.trim() === "")
		return;
	card.content = linkify(card.content);

	var template = Handlebars.compile($('#card-template').html());
	var compiled = template(card);

	$('#cards-list').append(compiled);

	//if (effect) markup.effect('highlight', {}, 1000);

	// reset card content field
	$('#content').val("");
	$('#nocardsyet').remove();

	window.progressBar.add(card);

	if (HAT === "blue") {
		// do this again for all cards (easier than grabbing just added card)
		enableDragDrop();
	}

}
