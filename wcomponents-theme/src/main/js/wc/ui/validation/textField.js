define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/dom/attribute",
		"wc/dom/event",
		"wc/ui/getFirstLabelForElement",
		"lib/sprintf",
		"wc/ui/dateField",
		"wc/ui/validation/required",
		"wc/ui/validation/validationManager",
		"wc/ui/textField",
		"wc/config"],
	function(initialise, Widget, i18n, attribute, event, getFirstLabelForElement, sprintf, dateField, required, validationManager, textField, wcconfig) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/textField~ValidationTextInput
		 * @private
		 */
		function ValidationTextInput() {
			var INPUT = new Widget("input"),
				TEXT = INPUT.extend("", {type: "text"}),
				EMAIL = INPUT.extend("", {type: "email"}),
				BOOTSTRAPPED = "validation.textInput.bs",
				INPUT_WIDGETS = textField.getWidget(),
				WITH_PATTERN,
				PATTERNS,
				WITH_MIN,
				DEFAULT_RX = /^(?:\".+\"|[a-zA-Z0-9\.!#\$%&'\*\+/=\?\^_`\{\|\}~]+)@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/,
				RX_STRING = "";

			/**
			 * Test for an input which we are interested in.
			 * @function
			 * @private
			 * @param {Element} element The component to test.
			 * @returns {Boolean} true if the element is an input which we need to test.
			 */
			function isValidatingInput(element) {
				return Widget.isOneOfMe(element, INPUT_WIDGETS) || (TEXT.isOneOfMe(element) && !dateField.isOneOfMe(element));
			}

			/**
			 * Adds an error message to a component.
			 * @function
			 * @private
			 * @param {Element} element The DOM element with the error.
			 * @param {String} flag The framework for the error message in sprintf format.
			 */
			function _flagError(element, flag) {
				var label = getFirstLabelForElement(element, true) || element.title || i18n.get("validation_common_unlabelledfield"),
					message = sprintf.sprintf(flag, label),
					attachTo = null;

				if (element.id && element.name && (element.id !== element.name)) {
					// not a stand alone text input so a WMultiTextField;
					attachTo = element.parentNode.lastChild;
				}
				validationManager.flagError({element: element,
											message: message,
											attachTo: attachTo});
			}

			/**
			 * Array filter function which tests an individual field to see if it meets contraints (min and pattern).
			 * @function
			 * @private
			 * @param {Element} element A constrained field.
			 * @returns {Boolean} true if the field is invalid.
			 */
			function isInvalid(element) {
				var result = false,
					mask,
					regexp,
					value = element.value,
					flag = "",
					patternFlag,
					concatenator = i18n.get("validation_concatenator"),
					conf;

				if (value && !validationManager.isExempt(element)) {
					// min length
					if ((mask = element.getAttribute("minlength")) && value.length < parseInt(mask, 10)) {
						result = true;
						flag = i18n.get("validation_text_belowmin", "%s", mask);
					}
					// pattern (first email)
					if (EMAIL.isOneOfMe(element)) {
						if (RX_STRING === "") {
							conf = wcconfig.get("wc/ui/validation/textField");
							RX_STRING = conf && conf.rx ? conf.rx : null;
						}

						regexp = RX_STRING ? new RegExp(RX_STRING) : DEFAULT_RX;
						patternFlag = i18n.get("validation_email_format");
					}
					else if ((mask = element.getAttribute("pattern"))) {
						try {
							regexp = new RegExp("^(?:" + mask + ")$");
							patternFlag = i18n.get("validation_common_pattern");
						}
						catch (e) {
							regexp = null;
							// console.log("cannot convert input mask to regular expression, assuming valid");
						}
					}
					if (regexp && !(regexp.test(value))) {
						if (flag) {
							patternFlag = patternFlag.replace("%s ", "");
							flag = sprintf.sprintf(concatenator, flag, patternFlag);
						}
						else {
							flag = patternFlag;
						}
						result = true;
					}
					if (result) {
						_flagError(element, flag);
					}
				}
				return result;
			}


			/**
			 * Validates all of the constrained fields we are interested in in a given container.
			 * @function
			 * @private
			 * @param {Element} container An element, preferably one containing form controls with input masks, otherwise
			 *    why are we here?
			 * @returns {Boolean} true if the container is valid.
			 */
			function validate(container) {
				var candidates,
					_requiredTextFields = true,
					validConstrained = true;

				function _getWrapper(element) {
					if (element.type === "password") {
						return element;
					}
					return element.parentNode;
				}

				/* This does required validation for all text-style inputs apart from date fields.*/
				_requiredTextFields = required.complexValidationHelper({container: container,
																		widget: INPUT_WIDGETS.concat(TEXT),
																		filter: function(next) {
																			return !(dateField.isOneOfMe(next) || next.value);
																		},
																		attachTo: _getWrapper});

				// do the constraint tests
				WITH_PATTERN = WITH_PATTERN || INPUT.extend("", {"pattern": null});
				WITH_MIN = WITH_MIN || INPUT.extend("", {"minlength": null});
				PATTERNS = PATTERNS || [WITH_PATTERN, EMAIL, WITH_MIN];

				candidates = Widget.isOneOfMe(container, PATTERNS) ? [container] : Widget.findDescendants(container, PATTERNS);
				if (candidates && candidates.length) {
					validConstrained = ((Array.prototype.filter.call(candidates, isInvalid)).length === 0);
				}
				return _requiredTextFields && validConstrained;
			}

			/**
			 * Change event listener to revalidate.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped change event.
			 */
			function changeEvent($event) {
				var element = $event.target;
				if (isValidatingInput(element)) {
					validationManager.revalidationHelper(element, validate);
				}
			}

			/**
			 * Focus event listener to attach change events on first focus in browsers which do not capture.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event A wrapped focus[in] event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && !attribute.get(element, BOOTSTRAPPED) && isValidatingInput(element)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
				}
			}

			/**
			 * Initialise callback to attach event listeners.
			 * @function module:wc/ui/validation/textField.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.change, changeEvent, 1, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Late initialisation callback to wire up validation manager subscriber.
			 * @function module:wc/ui/validation/textField.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		/**
		 * Provides functionality to undertake client validation for text inputs including WTextField, WEmailField,
		 * WPhoneNumberField and WPasswordField.
		 *
		 * @typedef {Object} module:wc/ui/validation/textField.config() Optional module configuration.
		 * @property {String} rx The email regular expression as a string.
		 *
		 * @module wc/ui/validation/textField
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/event
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires external:lib/sprintf
		 * @requires module:wc/ui/dateField
		 * @requires module:wc/ui/validation/required
		 * @requires module:wc/ui/validation/validationManager
		 * @requires module:wc/ui/textField
		 * @requires module:wc/config
		 */
		var instance = new ValidationTextInput();
		initialise.register(instance);
		return instance;
	});
