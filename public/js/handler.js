var samplejson = {
	"status": 1, // TODO: statuscodes
	"data": {
		"fn": "createCard",
		"args": [
			"1",
			"Title 1",
			"15"
		]
	}
};

var fns = {};
fns.createCard = function() {
	var args = arguments, id = args[0], name = args[1], creator = args[2];
};
function json(j) {
	this.json = j;
}
json.onresponse = function() {
	var response = JSON.parseJson(this.json).data,
		status = response.status,
		fn = response.data.fn,
		args = response.data.args;

	if (status !== 1) {
		throw new TypeError("status not ok");
	}
	if (fns.hasOwnProperty(fn)) {
		fns[fn].apply(null, args);
	}
}