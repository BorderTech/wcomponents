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
					return i18n.translate("requiredPlaceholder").then(function(placeHolderText) {
						var ph;
						if (action === shed.actions.MANDATORY) {
							if (!input.getAttribute(PLACEHOLDER)) {
								input.setAttribute(PLACEHOLDER, placeHolderText);
							}
						} else if ((ph = input.getAttribute(PLACEHOLDER)) && ph.indexOf(placeHolderText) >= 0) {
							if (ph === placeHolderText) {
								input.removeAttribute(PLACEHOLDER);
							} else {
								ph = ph.replace(placeHolderText, "").trim();
								input.setAttribute(PLACEHOLDER, ph);
							}
						}
					});
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
