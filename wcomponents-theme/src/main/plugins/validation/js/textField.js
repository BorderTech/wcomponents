/**
 * Provides functionality to undertake client validation for text inputs including WTextField, WEmailField,
 * WPhoneNumberField and WPasswordField.
 *
 * @typedef {Object} module:w${validation.core.path.name}/textField.config() Optional module configuration.
 * @property {String} rx The email regular expression as a string.
 * @default "^(?:\\".+\\"|[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+)@[a-zA-Z0-9-]+(?:\\\\.[a-zA-Z0-9-]+)+$"
 *
 * @module ${validation.core.path.name}/textField
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires external:lib/sprintf
 * @requires module:wc/ui/dateField
 * @requires module:${validation.core.path.name}/required
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:wc/ui/textField
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/dom/attribute",
		"wc/dom/event",
		"wc/ui/getFirstLabelForElement",
		"lib/sprintf",
		"wc/ui/dateField",
		"${validation.core.path.name}/required",
		"${validation.core.path.name}/validationManager",
		"wc/ui/textField",
		"module"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param attribute wc/dom/attribute @param event wc/dom/event @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param sprintf lib/sprintf @param dateField wc/ui/dateField @param required ${validation.core.path.name}/required @param validationManager ${validation.core.path.name}/validationManager @param textField wc/ui/textField @param module @ignore */
	function(initialise, Widget, i18n, attribute, event, getFirstLabelForElement, sprintf, dateField, required, validationManager, textField, module) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/textField~ValidationTextInput
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
				conf = module.config(),
				RX_STRING = ((conf && conf.rx) ? conf.rx : "${wc.ui.emailField.regex}");

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
				var label = getFirstLabelForElement(element, true) || element.title || i18n.get("${validation.core.i18n.unlabelledQualifier}"),
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
					concatenator = i18n.get("${validation.core.i18n.multiErrorConcatenator}");

				if (value && !validationManager.isExempt(element)) {
					// min length
					if ((mask = element.getAttribute("${wc.ui.textField.attrib.minLength}")) && value.length < parseInt(mask, 10)) {
						result = true;
						flag = i18n.get("${validation.textField.i18n.minLength}", "%s", mask);
					}
					// pattern (first email)
					if (EMAIL.isOneOfMe(element)) {
						regexp = new RegExp(RX_STRING);
						patternFlag = i18n.get("${validation.email.i18n.error}");
					}
					else if ((mask = element.getAttribute("pattern"))) {
						try {
							regexp = new RegExp("^(?:" + mask + ")$");
							patternFlag = i18n.get("${validation.core.i18n.patternMismatch}");
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
					_required = true,
					_requiredTextFields = true,
					validConstrained = true;

				// do the required tests
				_required = required.doItAllForMe(container, INPUT_WIDGETS);

				/* type="text" excluding dateField
				 * this does required validation for all textFields apart from date fields. The impracticality of using
				 * type="date" is such that we cannot just let required.doItAllForMe.*/
				_requiredTextFields = required.complexValidationHelper({container: container,
																		widget: TEXT,
																		filter: function(next) {
																			return !(dateField.isOneOfMe(next) || next.value);
																		}});

				// do the constraint tests
				WITH_PATTERN = WITH_PATTERN || INPUT.extend("", {"pattern": null});
				WITH_MIN = WITH_MIN || INPUT.extend("", {"${wc.ui.textField.attrib.minLength}": null});
				PATTERNS = PATTERNS || [WITH_PATTERN, EMAIL, WITH_MIN];

				candidates = Widget.isOneOfMe(container, PATTERNS) ? [container] : Widget.findDescendants(container, PATTERNS);
				if (candidates && candidates.length) {
					validConstrained = ((Array.prototype.filter.call(candidates, isInvalid)).length === 0);
				}
				return _required && _requiredTextFields && validConstrained;
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
			 * @function module:${validation.core.path.name}/textField.initialise
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
			 * @function module:${validation.core.path.name}/textField.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:${validation.core.path.name}/textField */ instance = new ValidationTextInput();
		initialise.register(instance);
		return instance;
	});
