define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/wrappedInput",
	"wc/dom/Widget",
	"wc/i18n/i18n"],
	function(initialise, shed, wrappedInput, Widget, i18n) {
		"use strict";

		function WrappedInputUI () {
			var WRAPPED = wrappedInput.getWrappedWidgets();

			/**
			 * This is a shed subscriber.
			 * Sets or clears the "mandatory" state of an input (whether it is a "required" field).
			 * @param element The element candidate for setting or clearing mandatory state.
			 * @param action either shed.actions.MANDATORY or shed.actions.OPTIONAL
			 * @returns {void|Promise}
			 */
			function mandate(element, action) {
				var input,
					PLACEHOLDER = "placeholder";
				if (!(element && wrappedInput.isOneOfMe(element))) {
					return;
				}
				if ((input = Widget.findDescendant(element, WRAPPED))) {
					shed[action](input);
					if (action === shed.actions.MANDATORY) {
						return i18n.translate("requiredPlaceholder").then(function(placeHolderText) {
							input.setAttribute(PLACEHOLDER, placeHolderText);
						});
                                        input.removeAttribute(PLACEHOLDER);
					

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
