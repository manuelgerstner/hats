var clicked = false;
var sec = 0;
var elapsedTime = 0;
function startClock() {
	if (clicked === false) {
		clock = setInterval(stopWatch(), 1000);
		clicked = true;
	} else if (clicked === true) {
	}
}
function stopWatch() {
	sec++;
}
function stopClock() {
	window.clearInterval(clock);
	sec = 0;
	clicked = false;
	startClock();
}


// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
	return this.each(function() {
		$('html, body').animate({
			scrollTop : $(this).offset().top - 10
		}, 1000);
	});
};

$(function() {

	filepicker.setKey("ALJ5oSFlR428EQekrItRgz");
	$('#addFile').click(function() {
		filepicker.pick({
			mimetypes : [ 'image/*', 'text/plain' ],
			services : [ 'COMPUTER', 'FACEBOOK', 'GMAIL' ],
		}, function(inkBlob) {
			var imgUrl = inkBlob.url, imgMine = inkBlob.mime;
			// inject into hidden fields
			$('#imgUrl').val(imgUrl);
			$('#imgMime').val(imgMime);
		});
	});

	$("table").tablesorter({
		debug : true
	});

	$('#modal-button').click(function() {
		/* new user? */
		var isNewUser = ($('#form-user').val() === "New User");

		console.log("is new user? " + isNewUser);

		/* force new user to enter name */
		if (isNewUser) {
			var name = $('#modal-username');
			if (name.val() === "") {
				name.parent().addClass('has-error');
			} else {
				name.parent().removeClass('has-error');
				$('#form-user').val(name.val());
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

	/*$('#card-form').ajaxForm({
	        dataType : "json",
	        type : "post",
	        //beforeSubmit: showRequest,
	        success : function(card) {
	                return;
	                // if (card.error === true) {
	                //         alert(card.message);
	                //         return;
	                // }
	                // addCard(card); // post-submit callback
	                
	        }
	});*/

	$('.tooltipster').tooltipster();

	$('.tokenfield').tokenfield();

	// get dashboard for session id
	$('#indicate-finish').click(
			function() {
				window.location.href = '/' + SESSION_ID + '/dashboard';
				$('body').removeClass().addClass("dashboard");
				elapsedTime = sec;
				stopClock();//Stop clock
				jsRoutes.controllers.Dashboard.saveDuration(SESSION_ID, HAT,
						elapsedTime).ajax({
					dataType : "json",
					type : "post",
					success : function(data) {
						if (data.error === true) {
							alert(data.message);
						}
						return;
					}
				});
			});

});
// get websocket up and running
function instantiateSocket() {
	// connect to WAMPlay server
	console.log("Connecting to WAMPlay server...");
	startClock();
	// successful setup
	ab.connect(WSURI, function(session) {

		// click handler for add card
		$("#btnAddCard").click(function() {
			var newCard = {
				"thinkingSession" : SESSION_ID,
				"hat" : $("#form-hat").val(),
				"content" : $("#content").val(),
				"username" : $("#form-user").val(),
				"userId" : USER_ID
			};
			var addCardEvent = {
				"eventType" : "addCard",
				"eventData" : newCard
			}
			var message = JSON.stringify(addCardEvent);
			session.call(CALL_URI + "#addCard", message)
		});

		$('#indicate-ready').click(
				function() {
					var hatInfo = {
						"thinkingSession" : SESSION_ID,
						"hat" : $("#form-hat").val()
					};
					var moveHatEvent = {
						"eventType" : "moveHat",
						"eventData" : hatInfo
					}
					var message = JSON.stringify(moveHatEvent);
					session.call(CALL_URI + "#moveHat", message)
					// jsRoutes.controllers.ThinkingSessions.restChangeHat(SESSION_ID).ajax({
					//         dataType : "json",
					//         type : "post",
					//         success : function(data) {
					//                 if (data.error === true) {
					//                         alert(data.message);
					//                         return;
					//                 }
					//                 $('circle.' + data.hat).show();
					//                 moveTo(data.hat);
					//         }
					// });
					elapsedTime = sec;
					stopClock();//Stop clock
					jsRoutes.controllers.Dashboard.saveDuration(SESSION_ID,
							HAT, elapsedTime).ajax({
						dataType : "json",
						type : "post",
						success : function(data) {
							if (data.error === true) {
								alert(data.message);
							}
							return;
						}
					});
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
	// event.username = "FooUser";
	// event.id = 1e4;
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
		containment : "#cards-list",
		cursor : "move",
		stack : '.draggable'
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