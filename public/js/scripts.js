// Scrolls the view down to the config pane on the frontpage
$.fn.scrollView = function() {
        return this.each(function() {
                $('html, body').animate({
                        scrollTop : $(this).offset().top - 10
                }, 1000);
        });
};

$(function() {

        filepicker.setKey("ALJ5oSFlR428EQekrItRgz");
        $('#addFile').click(function() {
                filepicker.pick({
                        mimetypes : [ 'image/*', 'text/plain' ],
                        services : [ 'COMPUTER', 'FACEBOOK', 'GMAIL' ],
                }, function(inkBlob) {
                        var imgUrl = inkBlob.url, imgMine = inkBlob.mime;
                        // inject into hidden fields
                        $('#imgUrl').val(imgUrl);
                        $('#imgMime').val(imgMime);
                });
        });

        $("table").tablesorter({
                debug : true
        });

        $('#modal-button').click(function() {
                /* new user? */
                var isNewUser = ($('#form-user').val() === "New User");

                console.log(isNewUser);

                /* force new user to enter name */
                if (isNewUser) {
                        var name = $('#modal-username');
                        if (name.val() === "") {
                                name.parent().addClass('has-error');
                        } else {
                                name.parent().removeClass('has-error');
                                $('#form-user').val(name.val());
                                $('#first-time').remove();
                                $('#hatchange-modal').modal('hide');
                        }
                } else {
                        $('#hatchange-modal').modal('hide');
                }

        });

        // only show first hat in progress bar
        if (typeof HAT !== "undefined") {
                //$('circle:not(.' + HAT + ')').hide();
        }

        window.progressBar = new ProgressBar('#progressBar');

        // initialize tooltips
        $('.tooltipster').tooltipster();

        // setup initial card setup
        if (typeof CARDS !== "undefined" && CARDS.length > 0) {
                $(CARDS).each(function() {
                        addCard(this);
                        window.progressBar.add(this);
                });

                // initial drag
                makeDraggable();
        }

        /*$('#card-form').ajaxForm({
                dataType : "json",
                type : "post",
                //beforeSubmit: showRequest,
                success : function(card) {
                        return;
                        // if (card.error === true) {
                        //         alert(card.message);
                        //         return;
                        // }
                        // addCard(card); // post-submit callback
                        
                }
        });*/

        $('.tooltipster').tooltipster();

        $('#tokenfield').tokenfield();

        $('#moveToConfig-button').click(function() {
                $("#config-panel").scrollView();
        });
        $('#moveToInvite-button').click(function() {
                $("#invite-panel").scrollView();
        });

        //$('#start-button').click(function() {
        //$('#control-panel').removeClass('hidden');
        //$("#control-panel").scrollView();
        //});
        $('#help-button').click(function() {
                $('body').chardinJs('start');
        });
        var $validator = $("#session-form").validate({
                rules : {
                        topicfield : {
                                required : true,
                                minlength : 3
                        }
                }
        });
        $('#rootwizard').bootstrapWizard({
                onTabShow : function(tab, navigation, index) {
                        var $total = navigation.find('li').length;
                        var $current = index + 1;
                        var $percent = ($current / $total) * 100;
                        $('#rootwizard').find('.bar').css({
                                width : $percent + '%'
                        });

                        // If it's the last tab then hide the last button and show the finish instead
                        if ($current >= $total) {
                                $('#rootwizard').find('.pager .next').hide();
                                $('#rootwizard').find('.pager .finish').show();
                                $('#rootwizard').find('.pager .finish').removeClass(
                                                'disabled');
                        } else {
                                $('#rootwizard').find('.pager .next').show();
                                $('#rootwizard').find('.pager .finish').hide();
                        }
                },
                onNext : function(tab, navigation, index) {
                        var $valid = $("#session-form").valid();
                        if (!$valid) {
                                $validator.focusInvalid();
                                return false;
                        }
                }
        });
 // get dashboard for session id
        $('#indicate-finish').click(function() {
        	window.location.href = '/' + SESSION_ID + '/dashboard';
        	$('body').removeClass().addClass("dashboard");
        });

});
// get websocket up and running
function instantiateSocket() {
        // connect to WAMPlay server
        console.log("Connecting to WAMPlay server...");
        // successful setup
        ab.connect(WSURI, function(session) {

                // click handler for add card
                $("#btnAddCard").click(function() {
                        var newCard = {
                                "thinkingSession" : SESSION_ID,
                                "hat" : $("#form-hat").val(),
                                "content" : $("#content").val(),
                                "username" : USERNAME,
                                "userId" : USER_ID
                        };
                        var addCardEvent = {
                                "eventType" : "addCard",
                                "eventData" : newCard
                        }
                        var message = JSON.stringify(addCardEvent);
                        session.call(CALL_URI + "#addCard", message)
                });

                $('#indicate-ready').click(function() {
                        var hatInfo = {
                                "thinkingSession" : SESSION_ID,
                                "hat" : $("#form-hat").val()
                        };
                        var moveHatEvent = {
                                "eventType" : "moveHat",
                                "eventData" : hatInfo
                        }
                        var message = JSON.stringify(moveHatEvent);
                        session.call(CALL_URI + "#moveHat", message)
                        // jsRoutes.controllers.ThinkingSessions.restChangeHat(SESSION_ID).ajax({
                        //         dataType : "json",
                        //         type : "post",
                        //         success : function(data) {
                        //                 if (data.error === true) {
                        //                         alert(data.message);
                        //                         return;
                        //                 }
                        //                 $('circle.' + data.hat).show();
                        //                 moveTo(data.hat);
                        //         }
                        // });
                });
                console.log("Connected to " + WSURI);
                // subscribe to add cards here, give a callback
                // ID needs to be string
                session.subscribe(SESSION_ID.toString(), onEvent);
                console.log("Subscribed to session number " + SESSION_ID);
        },

        // WAMP session is gone
        function(code, reason) {
                console.log("Connection lost (" + reason + ")", true);
                // should probably reconnect here
        },
        // additional options
        {
                skipSubprotocolCheck : true,
                skipSubprotocolAnnounce : true
        }); // Important! Play rejects all subprotocols for some reason...
}

// debugging handler for websocket events coming in
function onEvent(topic, event) {

        // add switch case for topic here:

        console.log("Message from topic: " + topic + ":");
        // event holds the actual message that is being sent
        console.log(event);
        // event.username = "FooUser";
        // event.id = 1e4;
        //if (userid != incoming user) OR use skip paramters in session.send
        if (event.eventType === "addCard") {
                addCard(event.eventData, true);
        } else if (event.eventType === "moveHat") {
                moveTo(event.eventData.hat);
        }
        window.progressBar.add(event);

}

function makeDraggable() {
        $('#cards-list div.card').draggable({
                containment : "#cards-list",
                cursor : "move",
                stack : '.draggable',
                start : function() {
                        $(this).siblings().css("z-index", 50);
                        $(this).css("z-index", 100);
                }
        });

        if (HAT !== "blue") {
                $('#cards-list div.card').draggable('disable');
        } else {
                $('#cards-list div.card').draggable('enable');
        }
}

function moveTo(hat) {
        // CSS changes for mood
        $('#hat').removeClass().addClass(hat.toLowerCase());
        $('body').removeClass().addClass(hat.toLowerCase());
        $('#form-hat').val(hat.toLowerCase());

        // change tooltip text for input
        var modal = $('#hatchange-modal');
        $('.hat', modal).html(hat.toLowerCase());
        $('.message', modal).html(TOOLTIPS[hat.toLowerCase()]);

        modal.modal();

        // overwrite global HAT var
        HAT = hat.toLowerCase();

        makeDraggable();
        
        if (HAT === "blue") {
                //window.location.reload(true); 
                $('#indicate-finish').show();
                $('#indicate-ready').hide();
        }
        
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
        $('#content, imgUrl, imgMime').val("");
        $('#nocardsyet').remove();
}

function validateForm() {
        var isValidMail = function(mail) {
                if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail)) {
                        return true;
                }
                return false;
        }

        var field = $("#tokenfield");
        var mails = field.tokenfield('getTokens');
        var mailString = '';
        for (var i = mails.length - 1; i >= 0; i--) {
                var mail = mails[i].value.trim()
                if (!isValidMail(mail)) {
                        alert(mails[i].value.trim()
                                        + ' seems to be invalid mail address... =(');
                        return false;
                }
                mailString += mail + ',';
        }
        field.val(mailString); // mails for form form binding on server side
        return true;
}