$(document).ready(function(){
	var animationShown = false; // Flag to track whether animation has been shown

	$('#action_menu_btn').click(function(){
		$('.action_menu').toggle();
	});
	$('.scrollable').scrollTop($('.scrollable')[0].scrollHeight);

	$('#questionForm').submit(function(event) {
		event.preventDefault(); // Prevent default form submission
		animateAndSubmit();
	});

	// Add event listener for pressing Enter key
	$('#question').keypress(function(event) {
		if (event.which === 13) { // Check if Enter key is pressed
			event.preventDefault(); // Prevent default behavior (form submission)
			animateAndSubmit();
		}
	});
});

function animateAndSubmit() {
	var questionInput = $('#question').val().trim();
	if (questionInput === '') {
		return; // Don't animate or submit if the input is empty
	}

	$('.first_message').remove();

	// Show the typed question as a message with animation
	var messageContainer = $('<div></div>').addClass('d-flex justify-content-end mb-4 animated');
	var msgCotainerSend = $('<div></div>').addClass('msg_cotainer_send').text(questionInput);
	var imgContMsg = $('<div></div>').addClass('img_cont_msg');

	// Append message components to the message container
	messageContainer.append(msgCotainerSend, imgContMsg);

	$('.msg_card_body').append(messageContainer);
	$('.scrollable').scrollTop($('.scrollable')[0].scrollHeight);

	// Append the message container to the message body
	setTimeout(function() {$('.msg_card_body').append(messageContainer);
	$('.scrollable').scrollTop($('.scrollable')[0].scrollHeight);

	var replyContainer = $('<div></div>').addClass('d-flex justify-content-start mb-4');
	var replySend = $('<div class="msg_cotainer"><div class="dot-pulse"></div></div>')

	// Append message components to the message container
	replyContainer.append(replySend, replyContainer);

	// Append the message container to the message body
	$('.msg_card_body').append(replyContainer);
	$('.scrollable').scrollTop($('.scrollable')[0].scrollHeight);}, 1000);

	// Submit the form after animation
	setTimeout(function() {
		$('.input-group').unbind('submit').submit();
		$('#question').val('');
	}, 1000); // Adjust the timeout duration as needed
}
function checkPasswordStrength() {
	var password = $("#password").val();
	var strength = 0;
	var upperCase = 0;
	var number = 0;
	var specialCharacter= 0;
	var length=0;

	// Check for both uppercase characters
	if (password.match(/[A-Z]/)) {
		strength += 1;
	}
	if (!password.match(/[A-Z]/)) {
		upperCase=1;
	}

	// Check for at least one number
	if (password.match(/\d+/)) {
		strength += 1;
	}
	if (!password.match(/\d+/)) {
		number = 1;
	}
	if (password.match(/\d+/)) {
		strength += 1;
	}

	// Check for special characters
	if (password.match(/.[!,@@,#,$,%,^,&,*,?,_,~,-,(,)]/)) {
		strength += 1;
	}

	if (!password.match(/.[!,@@,#,$,%,^,&,*,?,_,~,-,(,)]/)) {
		specialCharacter = 1;
	}


	// Check length
	if (password.length >= 8) {
		strength += 1;
	} else {
		length=1;
		strength = 0;
	}

	// Display the strength
	displayStrength(strength, upperCase, number, specialCharacter, length);
}
function displayStrength(strength, upperCase, number, specialCharacter, length) {
	var strengthIndicator = $("#passwordStrength");

	if (strength === 0) {
		strengthIndicator.html("Password Strength: <span style='color: red;'>Weak</span>");
		document.getElementById('submitButton').disabled = true;
	} else if (strength <= 2) {
		strengthIndicator.html("Password Strength: <span style='color: sandybrown;'>Moderate</span>");
		document.getElementById('submitButton').disabled = false;
	} else if (strength <= 3) {
		strengthIndicator.html("Password Strength: <span style='color: seagreen;'>Strong</span>");
		document.getElementById('submitButton').disabled = false;
	} else {
		strengthIndicator.html("Password Strength: <span style='color: green;'>Very Strong</span>");
		document.getElementById('submitButton').disabled = false;
	}

	if(upperCase===1){
		strengthIndicator.append("<p style='color: red'>You'll need at least 1 uppercase letter!</p>")
	}
	if(number===1){
		strengthIndicator.append("<p style='color: red'>You'll need at least 1 number!</p>")
	}
	if(specialCharacter===1){
		strengthIndicator.append("<p style='color: red'>You'll need at least 1 special character!</p>")
	}
	if(length===1){
		strengthIndicator.append("<p style='color: red'>Your password must be at least 8 characters long!</p>")
	}
}