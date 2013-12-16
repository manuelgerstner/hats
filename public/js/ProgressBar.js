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
	for (var hat in self.bubbles) {
		var count = self.bubbles[hat];
		//console.log("hat: %s", hat);
		//console.log("count: %d", count);
		if (count > 0) {
			var circle = $(self.container).find('circle.' + hat);
			//console.log("current size %d", circle.attr("r"));
			circle.attr('r', (5 + count * self.factor));
		}
	}
};