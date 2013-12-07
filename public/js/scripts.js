$(function() {
	// initialize tooltips
	$('.tooltipster').tooltipster();
	
	// enable ajax submit of form-add
	$('#card-add').ajaxForm({
		,"dataType": "json"
		 // work directly with json here 
		,"success": function(data) {
			
			if (data.error === true) {
				console.log(data.message);
				return false;
			}
			// data should look like this:
			/**
			 * {"id":5, "hat": "Green", "content": "card content"}
			 */
			var template = Handlebars.compile($('#card-template'));
			var compiled = template(data);
			
			$('#cards-list').append(compiled);
		}
	})
});