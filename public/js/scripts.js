$(function() {
	// initialize tooltips
	$('.tooltipster').tooltipster();
	
	// enable ajax submit of form-add
	$('#card-add').ajaxForm({
		"beforeSubmit": function(data, form) {
			console.log(data);
			return false; // for now
		}
		,"dataType": "json"
		 // work directly with json here 
		,"success": null
	})
}); 