// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
	return this.each(function() {
		$('html, body').animate({
			scrollTop: $(this).offset().top - 10
		}, 1000);
	});
};

$(function() {

	filepicker.setKey("ALJ5oSFlR428EQekrItRgz");
	$('#addFile').click(function() {
		filepicker.pick({
			mimetypes: ['image/*', 'text/plain'],
			services: ['COMPUTER', 'FACEBOOK', 'GMAIL'],
		}, function(inkBlob) {
			var imgUrl = inkBlob.url,
				imgMine = inkBlob.mime;
			// inject into hidden fields
			$('#imgUrl').val(imgUrl);
			$('#imgMime').val(imgMime);
		});
	});

	$("table").tablesorter({
		debug: true
	});

	$('#modal-button').click(function() {
        /* new user? */
        var test = $('#form-user').val();
        var isNewUser = ($('#form-user').val() === "New User");

        console.log("Is new user? = " + isNewUser);

        /* force new user to enter name */
        if (isNewUser) {
            var name = $('#modal-username');

            if (name.val() === "") {
                name.parent().addClass('has-error');
            } else {
                var nameString = name.val()
                var dataString = 'new-name=' + nameString;
                $.ajax({
                    type: "POST",
                    dataType: "text/plain",
                    url: "/user/saveName",
                    data: dataString
                });
                USERNAME = nameString;
                name.parent().removeClass('has-error');
                $('#form-user').val(nameString);
                $('#first-time').remove();
                $('#hatchange-modal').modal('hide');
            }
        } else {
            $('#hatchange-modal').modal('hide');
        }

    });

	// only show first hat in progress bar
	if (typeof HAT !== "undefined") {
		//$('circle:not(.' + HAT + ')').hide();
	}

	window.progressBar = new ProgressBar('#progressBar');

	// initialize tooltips
	$('.tooltipster').tooltipster();

	// setup initial card setup
	if (typeof CARDS !== "undefined" && CARDS.length > 0) {
		$(CARDS).each(function() {
			addCard(this);
		});
	}
	$('.tooltipster').tooltipster();

	$('.tokenfield').tokenfield();
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
					"thinkingSession": SESSION_ID,
					"hat": $("#form-hat").val(),
					"content": $("#content").val(),
					"username": $("#form-user").val(),
					"userId": USER_ID
				};
				var addCardEvent = {
					"eventType": "addCard",
					"eventData": newCard
				}
				var message = JSON.stringify(addCardEvent);
				session.call(CALL_URI + "#addCard", message)
			});

			$('#indicate-ready').click(
				function() {
					var hatInfo = {
						"thinkingSession": SESSION_ID,
						"hat": $("#form-hat").val()
					};
					var moveHatEvent = {
						"eventType": "moveHat",
						"eventData": hatInfo
					}
					var message = JSON.stringify(moveHatEvent);
					session.call(CALL_URI + "#moveHat", message)
				});
			console.log("Connected to " + WSURI);
			// subscribe to add cards here, give a callback
			// ID needs to be string
			session.subscribe(SESSION_ID.toString(), onEvent);
			console.log("Subscribed to session number " + SESSION_ID);
		},

		// WAMP session is gone

		function(code, reason) {
			console.log("Connection lost (" + reason + ")", true);
			// should probably reconnect here
		},
		// additional options
		{
			skipSubprotocolCheck: true,
			skipSubprotocolAnnounce: true
		}); // Important! Play rejects all subprotocols for some reason...
}

// debugging handler for websocket events coming in

function onEvent(topic, event) {

	// add switch case for topic here:
	console.log("Message from topic: " + topic + ":");
	// event holds the actual message that is being sent
	console.log(event);
	//if (userid != incoming user) OR use skip paramters in session.send
	if (event.eventType === "addCard") {
		addCard(event.eventData, true);
	} else if (event.eventType === "moveHat") {
		moveTo(event.eventData.hat);
	}
	window.progressBar.add(event.eventData);

}

function makeDraggable() {
	$('#cards-list div.card').draggable({
		containment: "#cards-list",
		cursor: "move",
		stack: '.draggable'
	});
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

	if (HAT !== "blue") {
		$('#cards-list div.card').draggable('disable');
	} else {
		$('#cards-list div.card').draggable('enable');
		$('#indicate-finish').show();
		$('#indicate-ready').hide();
	}

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
	$('#content, imgUrl, imgMime').val("");
	$('#nocardsyet').remove();

	window.progressBar.add(card);
	makeDraggable();
}