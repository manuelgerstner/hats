// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
	return this.each(function() {
		$('html, body').animate({
			scrollTop: $(this).offset().top - 10
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
		dataType: "json",
		type: "post",
		//beforeSubmit: showRequest,
		success: function(card) {
			if (card.error === true) {
				alert(card.message);
				return;
			}
			addCard(card); // post-submit callback
			progressBar.add(card);
		}
	});

	$('#indicate-ready').click(function(){
		jsRoutes.controllers.ThinkingSessions.restChangeHat(SESSION_ID).ajax({
			dataType: "json",
			type: "post",
			success: function(data) {
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

function makeDraggable() {
	$('#cards-list div.card').draggable({
		containment: "#cards-list",
		cursor: "move",
		stack: '.draggable',
		start: function() {
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
	// change hat class of div
	$('#hat').removeClass().addClass(hat.toLowerCase());
	$('body').removeClass().addClass(hat.toLowerCase());
	$('#form-hat').val(hat.toLowerCase());
	
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