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
	this.iterations = {};
	this.factor = 3;
	this.container = container;
	this.bubbles = bubbles || {
		// todo: use maps in the future to hold actual bubbles
		"blue": 0,
		"white": 0,
		"red": 0,
		"yellow": 0,
		"green": 0,
		"black": 0
	};
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
};

/**
 * refreshes the circles in the progress bar to hold corresponding sizes
 * 
 */
ProgressBar.prototype.render = function() {
	var self = this;
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
        return (3 + (fullSize - 3) / (1 + Math.exp((-count / stretch) + 2)));
    }
	for (var hat in self.bubbles) {
		var count = self.bubbles[hat];
		//console.log("hat: %s", hat);
		//console.log("count: %d", count);
		if (count > 0) {
			var circle = $(self.container).find('circle.' + hat);
			//console.log("current size %d", circle.attr("r"));
			//console.log(Math.ceil(getSize(count)));
			var count = Math.ceil(getSize(count));
			console.log("sizing %s hat to %dpx", hat, count);
			circle.attr("r", count);
		}
	}
};