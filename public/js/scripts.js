// change this variable depending on which hat is worn
var HAT = 'white';

// Contains progress outline data
var PROGRESS_DATA = [];

// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
	return this.each(function() {
		$('html, body').animate({
			scrollTop: $(this).offset().top - 10
		}, 1000);
	});
};

// Adds a bubble to the global bubble string array
function addBubbleToProgressBar(color, size, tooltip, timestamp) {
	PROGRESS_DATA.push({ "color" : color, "size" : size });
}

// Renders the progressbar given an array containing bubble data
function renderProgressOutline() {
	// Sample array
	var sampleProgress = [ { "color" : "white", "size" : 16 },
	                       { "color" : "red", "size" : 16 },
	                       { "color" : "yellow", "size" : 16 },
	                       { "color" : "black", "size" : 16 },
	                       { "color" : "green", "size" : 16 },
	                       { "color" : "blue", "size" : 16 }];

	var colorCode;

	// Maximum number of bubbles to show
	var maxBubbles = 16;
	// Width of the progress bar in pixels
	var barWidth = 800;
	// Space available to each bubble in pixels
	var bubbleSpace = barWidth / maxBubbles;

	// Current position on the progress bar
	var currentPosition = 25;

	for (var i=0;i<PROGRESS_DATA.length;i++){
        var bubble = PROGRESS_DATA[i];
        // Get color code for color, default green is too dark
        switch (bubble.color) {
		  case "white":
		    colorCode = "#ffffff";
		    break;
		  case "red":
		    colorCode = "#ff0000";
		    break;
		  case "green":
		    colorCode = "#00cc00";
		    break;
		  case "yellow":
		    colorCode = "#ffff00";
		    break;
		  case "blue":
		  	colorCode = "#0000ff";
		  	break;
		  default:
		    colorCode = "#000000";
		    break;
		}
        var circle = $('<circle/>');
        circle.addClass('process_bubble', bubble.color, 'tooltipster');
        circle.attr('cx', currentPosition);
        circle.attr('cy', 50);
        circle.attr('r', bubble.size);
        circle.css('fill', colorCode);
        $(circle).appendTo('#progressOutline');
        
        
		//$('#progressOutline').append('<circle class="process_bubble ' + bubble.color + ' tooltipster" cx="' + currentPosition + '" cy="50" r="' + bubble.size + '" style="fill:' + colorCode + ' ;"/>');
        currentPosition += bubbleSpace;
        // "Hack" for svg to reload after using jquerys append method
        $("#svg").html($("#svg").html());
    }
}

$(function() {

	// initialize tooltips
	$('.tooltipster').tooltipster();

	// setup initial card setup
	if (typeof CARDS !== "undefined" && CARDS.length > 0) {
		$(CARDS).each(function() {
			addCard(this);
		});
	}

	$('#card-form').ajaxForm({
		dataType: "json",
		type: "post",
		//beforeSubmit: showRequest,
		success: function(data) {
			if (data.error === true) {
				alert(data.message);
				return;
			}
			addCard(data); // post-submit callback
		}
	});


	$('.tooltipster').tooltipster();
	$('#tokenfield').tokenfield();
	$('#start-button').click(function() {
		$('#control-panel').removeClass('hidden');
		$("#control-panel").scrollView();
	});
	$('#help-button').click(function() {
		$('body').chardinJs('start');
	});
	
});

function moveTo(hat) {
	// change hat class of div
	$('#hat').removeClass(HAT).addClass(hat.toLowerCase());
	// overwrite var
	HAT = hat;
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
	$('#content').val('');
	$('#nocardsyet').remove();
}