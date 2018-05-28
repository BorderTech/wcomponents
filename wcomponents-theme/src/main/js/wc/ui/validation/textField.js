define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/shed",
	"lib/sprintf",
	"wc/ui/dateField",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/feedback",
	"wc/config"],
	function(initialise, Widget, i18n, attribute, event, shed, sprintf, dateField, required, validationManager, feedback, wcconfig) {
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
				INPUT_WIDGETS,
				WITH_PATTERN,
				PATTERNS,
				WITH_MIN,
				DEFAULT_RX = /^(?:\".+\"|[a-zA-Z0-9\.!#\$%&'\*\+/=\?\^_`\{\|\}~]+)@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/,
				RX_STRING = "";

			function setUpWidgets() {
				var types = ["password", "tel", "file"];  // input types which are not needed for validation other than mandatory-ness.
				INPUT_WIDGETS = (types.map(function(next) {
					return INPUT.extend("", {"type": next});
				})).concat(EMAIL);  // we do not include type text here, we have to do special processing with it
			}

			/**
			 * Test for an input which we are interested in.
			 * @function
			 * @private
			 * @param {Element} element The component to test.
			 * @returns {Boolean} true if the element is an input which we need to test.
			 */
			function isValidatingInput(element) {
				if (!INPUT_WIDGETS) {
					setUpWidgets();
				}
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
				var message = sprintf.sprintf(flag, validationManager.getLabelText(element));

				feedback.flagError({element: element, message: message});
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
							conf = wcconfig.get("wc/ui/validation/textField", {
								rx: null
							});
							RX_STRING = conf.rx;
						}

						regexp = RX_STRING ? new RegExp(RX_STRING) : DEFAULT_RX;
						patternFlag = i18n.get("validation_email_format");
					} else if ((mask = element.getAttribute("pattern"))) {
						try {
							regexp = new RegExp("^(?:" + mask + ")$");
							patternFlag = i18n.get("validation_common_pattern");
						} catch (e) {
							regexp = null;
							// console.log("cannot convert input mask to regular expression, assuming valid");
						}
					}
					if (regexp && !(regexp.test(value))) {
						if (flag) {
							patternFlag = patternFlag.replace("%s ", "");
							flag = sprintf.sprintf(concatenator, flag, patternFlag);
						} else {
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
				if (!INPUT_WIDGETS) {
					setUpWidgets();
				}
				var candidates,
					_requiredTextFields = true,
					validConstrained = true,
					helperObj = {container: container,
						widget: INPUT_WIDGETS.concat(TEXT),
						filter: function(next) {
							return !(next.value || dateField.isOneOfMe(next));
						}
					};

				/* This does required validation for all text-style inputs apart from date fields.*/
				_requiredTextFields = required.complexValidationHelper(helperObj);

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
				if (validationManager.isValidateOnChange()) {
					if (validationManager.isInvalid(element)) {
						validationManager.revalidationHelper(element, validate);
						return;
					}
					validate(element);
					return;
				}
				validationManager.revalidationHelper(element, validate);
			}

			function blurEvent($event) {
				var element = $event.target;
				if (!element.value && shed.isMandatory(element)) {
					validate(element);
				}
			}

			/**
			 * Focus event listener to attach change events on first focus.
			 * @function
			 * @private
			 * @param {wc/dom/event} $event A wrapped focus[in] event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && !attribute.get(element, BOOTSTRAPPED) && isValidatingInput(element)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
					if (validationManager.isValidateOnBlur()) {
						if (event.canCapture) {
							event.add(element, event.TYPE.blur, blurEvent, 1, null, true);
						} else {
							event.add(element, event.TYPE.focusout, blurEvent);
						}
					}
				}
			}

			/**
			 * Initialise callback to attach event listeners.
			 * @function module:wc/ui/validation/textField.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, 1, null, true);
				} else {
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
		 * @module
		 *
		 * @requires wc/dom/initialise
		 * @requires wc/dom/Widget
		 * @requires wc/i18n/i18n
		 * @requires wc/dom/attribute
		 * @requires wc/dom/event
		 * @requires wc/dom/shed
		 * @requires external:lib/sprintf
		 * @requires wc/ui/dateField
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/feedback
		 * @requires wc/config
		 */
		var instance = new ValidationTextInput();
		initialise.register(instance);
		return instance;
		/**
		 * @typedef {Object} module:wc/ui/validation/textField.config Optional module configuration.
		 * @property {String} rx The email regular expression as a string.
		 */
	});
