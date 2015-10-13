/**
 * functions for generic text type inputs:
 * makeMandatory subscriber for placing/removing placeholder text.
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 *
 * @todo Document private members.
 */
define(["wc/dom/initialise", "wc/dom/shed", "wc/dom/Widget", "wc/i18n/i18n"],
	/** @param initialise wc/dom/initialise @param shed wc/dom/shed @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @ignore */
	function(initialise, shed, Widget, i18n) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/textField~TextInput
		 * @private
		 */
		function TextInput() {
			var PLACEHOLDER = "placeholder",
				PLACEHOLDER_WIDGETS,
				INPUT = new Widget("input"),
				TEXT = INPUT.extend("", {"type": "text"}),
				EMAIL = INPUT.extend("", {"type": "email"}),
				INPUT_WIDGETS,
				TEXTAREA;

			function setUpWidgets() {
				var types = ["password", "tel", "file"];  // input types which are not needed for validation other than mandatory-ness.
				INPUT_WIDGETS = INPUT_WIDGETS || (types.map(function(next) {
					return INPUT.extend("", {"type": next});
				})).concat(EMAIL);  // we do not include type text here, we have to do special processing with it
			}

			/*
			 * listen for mandatory/optional and act set the inputs placeholder
			 * @param element the element being selected/deselected
			 * @param action shed.actions.MANDATORY or shed.actions.OPTIONAL
			 */
			function shedSubscriber(element, action) {
				TEXTAREA = TEXTAREA || new Widget("textarea");
				if (!INPUT_WIDGETS) {
					setUpWidgets();
				}
				PLACEHOLDER_WIDGETS = PLACEHOLDER_WIDGETS || INPUT_WIDGETS.concat([TEXT, TEXTAREA]);
				if (element && Widget.isOneOfMe(element, PLACEHOLDER_WIDGETS)) {
					if (action === shed.actions.MANDATORY) {
						element.setAttribute(PLACEHOLDER, i18n.get("${wc.common.i18n.requiredPlaceholder}"));
					}
					else {
						element.removeAttribute(PLACEHOLDER);
					}
				}
			}

			/**
			 * Wire up subscribers in late initialisation.
			 * @function module:wc/ui/textField~TextInput.postInit
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.MANDATORY, shedSubscriber);
				shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
			};

			/**
			 * Get the widgets which descibe input components.
			 * @function module:wc/ui/textField~TextInput.getWidget
			 * @returns {module:wc/dom/Widget[]}
			 */
			this.getWidget = function() {
				if (!INPUT_WIDGETS) {
					setUpWidgets();
				}
				return INPUT_WIDGETS;
			};
		}
		var /** @alias module:wc/ui/textField */ instance = new TextInput();
		initialise.register(instance);
		return instance;
	});
