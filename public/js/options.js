var options = {
	// drag options for cards
	draggable : {
		containment : "#hat-cards",
		cursor : "move",
		stack : "div.card",
		// snap: true,
		revert : "invalid" // revert, if not dropped to droppable
	},

	droppable : {
		hoverClass : "dropit",
		drop : function(event, ui) {
			// grab bucket id
			var bucketId = $(event.target).data('bucketid');
			// kill placeholder
			$(this).find(".placeholder").remove();
			// bind card
			var card = ui.draggable, cardId = card.data('cardid');f			
			// css fix
			card.css("position", "").off(); // unbind all drag shit
			card.draggable("disable"); // disable further dragging
			// inject into container
			$(this).find(".cards").append(card);
			// finally, post
			window.session.call(CALL_URI + "#addCardToBucket", {
				cardId: cardId,
				bucketId: bucketId
			});
		}
	},
	fancybox : {
		speed : "slow"
	}
};