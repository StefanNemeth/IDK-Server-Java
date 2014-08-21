/**
 * Plugin Information
 * name, description
**/
var plugin = {
	'name' 	      : 'Calculator Plugin',
	'description' : 'Contains a bot interactor for calculating.'
};

var botTexts = ['Warte.. ich glaube %d?', 'Hm, bei mir würde jetzt %d rauskommen.', '%d wäre da die wahrscheinlichste Lösung.', 'Wie wärs mit %d?', 'Die Lösung müsste %d sein.'];

var calculateInteractor = {
    /**
     * Event -> OnPlayerSays..
    **/
    onPlayerSays : function(player, bot, message) {
        // Get message text
        var messageText = message.getMessage().toLowerCase().replace("geteilt durch", "/").replace(" ", "").replace("x", "*").replace(",", ".").replace("plus", "+").replace("minus", "-").replace("mal", "*").replace("geteilt", "/");
        var calculateString = '';
        var calculationStarted = false;
		var calculationOperation = false;
        // Loop message text (chars))
        for (var i = 0; i < messageText.length(); i++) {
            // Get char
            var chr = String.fromCharCode(messageText.charAt(i));
            if (!isNaN(chr)) {
                calculationStarted = true;
            }
            if (!calculationStarted) {
                continue;
            }
            // Check if operation allowed
            if (chr != '*' && chr != '/' && chr != '%' && chr != '+' && chr != '(' && chr != ')' && chr != '-' && chr != '.' && isNaN(chr)) {
				break;
            } else if(isNaN(chr)) {
				calculationOperation = true;
			}
            // Add to calculation string
            calculateString += chr;
        }
        // Execute the calculation string and save the value to result
        var result = 0;
        try {
            result = eval(calculateString);
        } catch (e) {
            return;
        }
        if (result === undefined || !calculationOperation) {
            return;
        }
        // Bot writes the result
        bot.chat(botTexts[Math.floor(Math.random() * (botTexts.length))].replace("%d", result.toString().replace(".", ",")));
    }
};

function initializePlugin() {
    /**
     * integer INTERACTOR_ID
     * object  INTERACTOR
    **/
    IDK.addBotInteractor(1, 'calculateInteractor');
}