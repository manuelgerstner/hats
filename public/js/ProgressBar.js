/**
 *  ProgressBar class
 *  @author dbusser
 */

/**
 * Constructor
 * 
 * @param container - a selector which holds the progress bar (should be type svg)
 * @bubbles (optional) - initial bubbles to be set up with custom counts
 */
function ProgressBar(container, bubbles) {
	this.container = container;
	this.bubbles = bubbles || {
		"blue": 5,
		"white": 5,
		"red": 5,
		"yellow": 5,
		"green": 5,
		"black": 5
	};
	hats = [ 
		"white",
		"red",
		"yellow",
		"black",
		"green",
		"blue"
	];
	this.render();
}
/**
 * adds a card to the progress bar inside the corresponding hat
 * 
 * @param card - a Card object that should be added to the progress bar
 */
ProgressBar.prototype.add = function(card) {
	//console.log("adding card: ", card);
	this.bubbles[card.hat]++;
	this.render();
	this.setBubbleSizes(this.bubbles);
};

/**
 * refreshes the circles in the progress bar to hold corresponding sizes
 * 
 */
ProgressBar.prototype.render = function() {
	var self = this;

	for(var i = 0; i < hats.indexOf(HAT) + 1; i++) {
			var cirque = $(self.container).find('circle.' + hats[i]);
			var position = 84 - hats.indexOf(HAT) * 14 + i * 14;
			var percentage = position + "%";
			cirque.attr("cx", percentage);
	}

	/**
     * Calculate bubble size using logistic distribution, see https://de.wikipedia.org/wiki/Logistische_Verteilung
     * to play with the parameters see plots at
     * http://fooplot.com/#W3sidHlwZSI6MCwiZXEiOiIzKzIwLygxK2V4cCgteC81KzIpKSIsImNvbG9yIjoiIzAwMDAwMCJ9LHsidHlwZSI6MTAwMCwid2luZG93IjpbIi05LjEzNzI3NTE1OTk5OTk3IiwiMzQuNDgzNDg2NDM5OTk5OTYiLCItMC42ODgzODQ2NTM4ODI4MTAyIiwiMjYuMTU1MTYwOTQ2MTE3MTY3Il19XQ--
     **/
    var getSize = function(count) {
        /**
         * stretch changes how many contributions are needed for full size
         * stretch = 1 -> ~ 10 cards yield full size
         * stretch = 2 -> ~ 20 cards yield full size
         * etc
         **/
        var stretch = 2;
        var fullSize = 20; // initial size is about 5
        // initial size for 0 cards
        return Math.ceil((3 + (fullSize - 3) / (1 + Math.exp((-count / stretch) + 2))));
    }

	/*for (var hat in self.bubbles) {
		var count = self.bubbles[hat];		

		if (count > 0) {			
			var count = getSize(count);
			self.bubbles[hat] = count;
		}		
	}*/	

};

ProgressBar.prototype.setBubbleSizes = function(bubbles) {
	/** Where David's magic happens
	
	**/
	for(var hat in bubbles) {
		var cirque = $(this.container).find('circle.' + hat);
		cirque.attr("r", bubbles[hat]);
	}
}