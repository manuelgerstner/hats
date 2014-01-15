/**
 * WebSocket class
 * @author dbusser
 *
 */
 
function WebSocket(url) {
	var _session = null;
	
	this.connect = function() {
		ab.connect(url,
		// onconnect handler
		function(session) {
			_session = session;
			console.log("Connected to " + url);
		// hangup handler
		}, function() {
			console.log("connection to ws lost");
		},
		// additional options
		{
			skipSubprotocolCheck : true,
			skipSubprotocolAnnounce : true
		}
		
	};
	
	this.subscribe = function(topic, event) {
		//_session.subscribe(pattern);
		// call js function with same name as event
		// todo
	};
	this.publish = function(thinkingSessionId, topic) {
		// _session.publish(pattern)
		// todo
	};	
	var topics = {
			"addCard": function() { return addCard; },
			"removeCard": function() { return removeCard; }
	};
} 