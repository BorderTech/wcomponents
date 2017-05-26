define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/wrappedInput",
	"wc/dom/Widget",
	"wc/i18n/i18n"],
	function(initialise, shed, wrappedInput, Widget, i18n) {
		"use strict";

		function WrappedInputUI () {
			var WRAPPED = wrappedInput.getWrappedWidgets(),
				PLACEHOLDER_TEXT;

			function mandate(element, action) {
				var input,
					PLACEHOLDER = "placeholder";
				if (!(element && wrappedInput.isOneOfMe(element))) {
					return;
				}
				if ((input = Widget.findDescendant(element, WRAPPED))) {
					shed[action](input);
					if (action === shed.actions.MANDATORY) {
						PLACEHOLDER_TEXT = PLACEHOLDER_TEXT || i18n.get("requiredPlaceholder");
						input.setAttribute(PLACEHOLDER, PLACEHOLDER_TEXT);
					}					else {
						input.removeAttribute(PLACEHOLDER);
					}
				}
			}

			this.postInit = function() {
				shed.subscribe(shed.actions.MANDATORY, mandate);
				shed.subscribe(shed.actions.OPTIONAL, mandate);
			};
		}

		var instance = new WrappedInputUI();
		initialise.register(instance);
		return instance;
	});
