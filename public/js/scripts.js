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
}

// Adds a bubble to the global bubble string array
var addBubbleToProgressBar = function(color, size, tooltip, timestamp) {
	PROGRESS_DATA.push({ "color" : color, "size" : size });
}

// Renders the progressbar given an array containing bubble data
var renderProgressOutline = function() {
	// Sample array
	var sampleProgress = [ { "color" : "white", "size" : 16 }, { "color" : "red", "size" : 16 }, { "color" : "yellow", "size" : 16 }, { "color" : "black", "size" : 16 }, { "color" : "green", "size" : 16 }, { "color" : "blue", "size" : 16 }];

	var colorCode;

	// Maximum number of bubbles to show
	var maxBubbles = 16;
	// Width of the progress bar in pixels
	var barWidth = 800;
	// Space available to each bubble in pixels
	var bubbleSpace = barWidth / maxBubbles;

	// Current position on the progress bar
	var currentPosition = 25;

	for(var i=0;i<PROGRESS_DATA.length;i++){
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
		$('#progressOutline').append('<circle class="process_bubble ' + bubble.color + ' tooltipster" cx="' + currentPosition + '" cy="50" r="' + bubble.size + '" style="fill:' + colorCode + ' ;"/>');
        currentPosition = currentPosition+bubbleSpace;

        //"Hack" for svg to reload after using jquerys append method
        $("#svg").html($("#svg").html());
    }
}

$(document).ready(function() {

    // initialize tooltips
    $('.tooltipster').tooltipster();


    var options = {
        dataType: "json",
        type: "post",
        //target: '#cards-list', // target element(s) to be updated with server response
        success: addCard // post-submit callback
    };

    $('#card-add').ajaxForm(options);

    function moveTo(hat) {
        // change hat class of div
        $('#hat').removeClass(HAT).addClass(hat.toLowerCase());
        // overwrite var
        HAT = hat;
    }
});

function addCard(data, statusText, xhr, $form) {

    if (data.error === true) {
        console.log(data.message);
        return false;
    }
    // data should look like this:
    /**
     * {"id":5, "hat": "Green", "content": "card content", "user":"username"}
     */

    var template = Handlebars.compile($('#card-template').html());
    var compiled = template(data);

    $('#cards-list-ul').append(compiled);
}


// from jQuery FromPlugin documentation:
// pre-submit callback

function showRequest(formData, jqForm, options) {
    // formData is an array; here we use $.param to convert it to a string to display it
    // but the form plugin does this for you automatically when it submits the data
    var queryString = $.param(formData);

    // jqForm is a jQuery object encapsulating the form element.  To access the
    // DOM element for the form do this:
    // var formElement = jqForm[0];

    alert('About to submit: \n\n' + queryString);

    // here we could return false to prevent the form from being submitted;
    // returning anything other than false will allow the form submit to continue
    return true;
}

// post-submit callback

function showResponse(responseText, statusText, xhr, $form) {
    // for normal html responses, the first argument to the success callback
    // is the XMLHttpRequest object's responseText property

    // if the ajaxForm method was passed an Options Object with the dataType
    // property set to 'xml' then the first argument to the success callback
    // is the XMLHttpRequest object's responseXML property

    // if the ajaxForm method was passed an Options Object with the dataType
    // property set to 'json' then the first argument to the success callback
    // is the json data object returned by the server

    alert('status: ' + statusText + '\n\nresponseText: \n' + responseText +
        '\n\nThe output div should have already been updated with the responseText.');
}