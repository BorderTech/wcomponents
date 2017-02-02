define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/wrappedInput",
	"wc/dom/Widget",
	"wc/i18n/i18n"],
	function(initialise, shed, wrappedInput, Widget, i18n) {
		"use strict";

		function WrappedInputUI () {
			var WRAPPED = wrappedInput.getWrappedWidgets();

			function mandate(element, action) {
				var input,
					PLACEHOLDER = "placeholder";
				if (!(element && wrappedInput.isOneOfMe(element))) {
					return;
				}
				if ((input = Widget.findDescendant(element, WRAPPED))) {
					shed[action](input);
					if (action === shed.actions.MANDATORY) {
						input.setAttribute(PLACEHOLDER, i18n.get("requiredPlaceholder"));
					}
					else {
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
