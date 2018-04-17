define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/wrappedInput",
	"wc/dom/Widget",
	"wc/i18n/i18n"],
	function(initialise, shed, wrappedInput, i18n) {
		"use strict";

		function WrappedInputUI () {

			/**
			 * This is a shed subscriber.
			 * Sets or clears the "mandatory" state of an input (whether it is a "required" field).
			 * @param element The element candidate for setting or clearing mandatory state.
			 * @param action either shed.actions.MANDATORY or shed.actions.OPTIONAL
			 * @returns {void|Promise}
			 */
			function mandate(element, action) {
				var PLACEHOLDER = "placeholder";

				if (element && wrappedInput.isWrappedInput(element)) {
					return i18n.translate("bordertech.wcomponents.message.fieldRequired").then(function(placeHolderText) {
						var ph;
						if (action === shed.actions.MANDATORY) {
							if (!element.getAttribute(PLACEHOLDER)) {
								element.setAttribute(PLACEHOLDER, placeHolderText);
							}
						} else if ((ph = element.getAttribute(PLACEHOLDER)) && ph.indexOf(placeHolderText) >= 0) {
							if (ph === placeHolderText) {
								element.removeAttribute(PLACEHOLDER);
							} else {
								ph = ph.replace(placeHolderText, "").trim();
								element.setAttribute(PLACEHOLDER, ph);
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
