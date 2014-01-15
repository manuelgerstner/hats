// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
	return this.each(function() {
		$('html, body').animate({
			scrollTop : $(this).offset().top - 10
		}, 1000);
	});
};

$(function() {

	// only show first hat
	if (typeof HAT !== "undefined") {
		$('circle:not(.' + HAT + ')').hide();
	}

	var progressBar = new ProgressBar('#progressBar');

	// initialize tooltips
	$('.tooltipster').tooltipster();

	// setup initial card setup
	if (typeof CARDS !== "undefined" && CARDS.length > 0) {
		$(CARDS).each(function() {
			addCard(this);
			progressBar.add(this);
		});

		// initial drag
		makeDraggable();
	}

	$('#card-form').ajaxForm({
		dataType : "json",
		type : "post",
		//beforeSubmit: showRequest,
		success : function(card) {
			if (card.error === true) {
				alert(card.message);
				return;
			}
			addCard(card); // post-submit callback
			progressBar.add(card);
		}
	});

	$('#indicate-ready').click(function() {
		jsRoutes.controllers.ThinkingSessions.restChangeHat(SESSION_ID).ajax({
			dataType : "json",
			type : "post",
			success : function(data) {
				if (data.error === true) {
					alert(data.message);
					return;
				}
				$('circle.' + data.hat).show();
				moveTo(data.hat);
			}
		});
	});

	$('.tooltipster').tooltipster();
	
	$('#tokenfield').tokenfield();

	$('#moveToConfig-button').click(function() {
		$("#config-panel").scrollView();
	});
	$('#moveToInvite-button').click(function() {
		$("#invite-panel").scrollView();
	});

	$('#start-button').click(function() {
		$('#control-panel').removeClass('hidden');
		$("#control-panel").scrollView();
	});
	$('#help-button').click(function() {
		$('body').chardinJs('start');
	});

});

// get websocket up and running
function instantiateSocket() {
	// connect to WAMPlay server
	console.log("Connecting to WAMPlay server...");
	// successful setup
	ab.connect(WSURI, function(session) {

		// click handler for add card
		$("#btnAddCard").click(function() {
			var newCard = {
				"hat" : $("#form-hat").val(),
				"content" : $("#content").val()
			};
			var message = JSON.stringify(newCard);
			// here the topic should be addCard
			session.publish(SESSION_TOPIC, message);
		});

		console.log("Connected to " + WSURI);
		// subscribe to add cards here, give a callback
		session.subscribe(SESSION_TOPIC, onEvent);
		console.log("Subscribed to " + SESSION_TOPIC);
	},

	// WAMP session is gone
	function(code, reason) {
		console.log("Connection lost (" + reason + ")", true);
		// should probably reconnect here
	},
	// additional options
	{
		skipSubprotocolCheck : true,
		skipSubprotocolAnnounce : true
	}); // Important! Play rejects all subprotocols for some reason...
}

// debugging handler for websocket events coming in
function onEvent(topic, event) {

	// add switch case for topic here:

	console.log("Message from topic: " + topic + ":");
	// event holds the actual message that is being sent
	console.log(event);
	event.username = "FooUser";
	event.id = 1e4;
	//if (userid != incoming user)
	addCard(JSON.parse(event), true);
}

function makeDraggable() {
	$('#cards-list div.card').draggable({
		containment : "#cards-list",
		cursor : "move",
		stack : '.draggable',
		start : function() {
			$(this).siblings().css("z-index", 50);
			$(this).css("z-index", 100);
		}
	});

	if (HAT !== "blue") {
		$('#cards-list div.card').draggable('disable');
	} else {
		$('#cards-list div.card').draggable('enable');
	}
}

function moveTo(hat) {
	// CSS changes for mood
	$('#hat').removeClass().addClass(hat.toLowerCase());
	$('body').removeClass().addClass(hat.toLowerCase());
	$('#form-hat').val(hat.toLowerCase());
	
	// change tooltip text for input
	var popover = $('#content'), content = TOOLTIPS[hat.toLowerCase()];
	popover.attr('data-content', content);
	// for some reason, data-title does not work, so we use this workaround
	popover.attr('data-original-title', "<strong>The " + hat.substr(0,1).toUpperCase() + hat.substr(1) + " Hat:</strong>");
	
	// now let's show the popover.
	$('#content').popover('show');
	
	// overwrite global HAT var
	HAT = hat.toLowerCase();
	makeDraggable();
}

function addCard(card, effect) {

	/**
	 * card json: {"id":5, "hat": "Green", "content": "card content",
	 * "username":"username"}
	 */

	var template = Handlebars.compile($('#card-template').html());
	var compiled = template(card);

	$('#cards-list').append(compiled);

	//if (effect) markup.effect('highlight', {}, 1000);

	// reset card content field
	$('#content').val("");
	$('#nocardsyet').remove();
}

function validateForm() {
	var isValidMail = function(mail) {
		if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail)) {
			return true;
		}
		return false;
	}
	var field = $("#tokenfield");
	var mails = field.tokenfield('getTokens');
	var mailString = '';
	for (var i = mails.length - 1; i >= 0; i--) {
		var mail = mails[i].value.trim()
		if (!isValidMail(mail)) {
			alert(mails[i].value.trim()
					+ ' seems to be invalid mail address... =(');
			return false;
		}
		mailString += mail + ',';
	}
	;
	field.val(mailString); // mails for form form binding on server side
	return true;
}