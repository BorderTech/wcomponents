define([
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"lib/sprintf",
	"wc/ui/feedback",
	"wc/config",
	"mailcheck"],
function(event, initialise, Widget, sprintf, feedback, wcconfig, mailcheck) {
	"use strict";

	var instance;

	function EmailField() {

		var inputWidget = new Widget("input");

		this.emailWidget = inputWidget.extend("", {type: "email"});

		/**
		 * Checks an email input and detects common typos.
		 * @param element A form element.
		 */
		function emailCheck(element) {
			var message = "Did you mean '%s'?",
				options;

			if (instance.emailWidget.isOneOfMe(element)) {
				options = wcconfig.get("wc/ui/email", {});
				options.email = element.value;
				options.suggested = function(suggestion) {
					var newEmail = suggestion.full;
					if (newEmail) {
						/*
						 * Removing any success messages from any other validation actions.
						 * I think these should auto-disappear anyway but currently they don't.
						 */
						feedback.remove(element, null, feedback.LEVEL.SUCCESS);
						feedback.flagInfo({
							element: element,
							message: sprintf.sprintf(message, newEmail)
						});
					}
				};
				options.empty = function() {
					feedback.remove(element, null, feedback.LEVEL.INFO);
				};

				mailcheck.run(options);
			}
		}

		/**
		 * Change event listener to revalidate.
		 * @function
		 * @private
		 * @param {module:wc/dom/event} $event A wrapped change event.
		 */
		function changeEvent($event) {
			emailCheck($event.target);
		}

		this.initialise = function(element) {
			event.add(element, { type: "change", listener: changeEvent, capture: true });
		};
	}
	instance = new EmailField();
	return initialise.register(instance);
});
