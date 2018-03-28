define(["wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"lib/sprintf",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/feedback",
	"wc/ui/textArea"],
	function(attribute, event, initialise, shed, Widget, i18n, sprintf, required, validationManager, feedback, textArea) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/textArea~ValidationTextArea
		 * @private
		 */
		function ValidationTextArea() {
			var TEXTAREA = textArea.getWidget(),
				INITED_KEY = "validation.textArea.init";

			/**
			* Undertake required validation for WTextArea.
			*
			* @function
			* @private
			* @param {Element} container The element being validated.
			* @returns {Boolean} true if all required WTextAreas in container are complete.
			*/
			function _validateRequired(container) {
				var obj = {container: container,
					widget: textArea.getWidget()};
				return required.complexValidationHelper(obj);
			}


			/**
			 * Tests an individual WTextArea to see if it meets constraints of minLength and maxLength.
			 * This is an array filter so returns false if the field is valid.
			 *
			 * @function
			 * @private
			 * @param {Element} element a WTextArea
			 * @returns {Boolean} true if the field is invalid.
			 */
			function doContraintValidityTest(element) {
				var result = false,
					mask,
					value = element.value,
					size,
					flag,
					message;
				if (value && !validationManager.isExempt(element)) {
					size = textArea.getLength(element);
					if ((mask = textArea.getMaxlength(element)) && size > mask) {
						result = true;
						flag = i18n.get("validation_textarea_overmax", "%s", mask, size);
					} else if ((mask = element.getAttribute("data-wc-min")) && size < mask) {
						result = true;
						flag = i18n.get("validation_text_belowmin", "%s", mask);
					}

					if (result) {
						message = sprintf.sprintf(flag, validationManager.getLabelText(element));
						feedback.flagError({element: element, message: message});
					}
				}
				return result;
			}


			/**
			 * Validate all WTextAreas in a given container.
			 *
			 * @function
			 * @private
			 * @param {Element} container A DOM node, preferably one containing constrained text areas.
			 * @returns {boolean} true if container is valid.
			 */
			function validate(container) {
				var result = true,
					_required = _validateRequired(container),
					_widget = textArea.getWidget(true),
					invalid,
					candidates = (Widget.isOneOfMe(container, _widget) ? [container] : Widget.findDescendants(container, _widget));

				if (candidates && candidates.length && (invalid = Array.prototype.filter.call(candidates, doContraintValidityTest))) {
					result = (invalid.length === 0);
				}
				return result && _required;
			}

			/**
			 * Regular (non-constrained) text areas get a change event listener to revalidate mandatory and ancestor
			 * fieldsets.
			 *
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
			 * Use first focus to attach other event listeners.
			 *
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped change event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (TEXTAREA.isOneOfMe(element) && !attribute.get(element, INITED_KEY)) {
					attribute.set(element, INITED_KEY, true);
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
			 * Initialise callback to set up event listeners.
			 * @function module:wc/ui/validation/textArea.initialise
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
			 * Late initialisation to attach validation manager subscriber.
			 *
			 * @function module:wc/ui/validation/textArea.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		/**
		 * Provides functionality to undertake client validation of WTextArea.
		 *
		 * @module
		 * @requires wc/dom/attribute
		 * @requires wc/dom/event
		 * @requires wc/dom/initialise
		 * @requires wc/dom/Widget
		 * @requires wc/i18n/i18n
		 * @requires external:lib/sprintf
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/feedback
		 * @requires wc/ui/textArea
		 */
		var instance = new ValidationTextArea();
		instance.constructor = ValidationTextArea;
		initialise.register(instance);
		return instance;
	});
