/**
 * Provides functionality to undertake client validation of WTextArea.
 *
 * @module wc/ui/validation/textArea
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires external:lib/sprintf
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires module:wc/ui/textArea
 */
define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"lib/sprintf",
		"wc/ui/validation/required",
		"wc/ui/validation/validationManager",
		"wc/ui/getFirstLabelForElement",
		"wc/ui/textArea"],
	/** @param attribute wc/dom/attribute @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param sprintf lib/sprintf @param required wc/ui/validation/required @param validationManager wc/ui/validation/validationManager @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param textArea wc/ui/textArea @ignore */
	function(attribute, event, initialise, Widget, i18n, sprintf, required, validationManager, getFirstLabelForElement, textArea) {
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
				function _getAttachmentPoint(element) {
					return textArea.getCounter(element) || element;
				}

				var obj = {container: container,
							widget: textArea.getWidget(),
							attachTo: _getAttachmentPoint};
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
					label,
					flag,
					message;
				if (value && !validationManager.isExempt(element)) {
					if ((mask = textArea.getMaxlength(element)) && value.length > mask) {
						result = true;
						flag = i18n.get("${validation.maxlength.i18n.error}", "%s", mask, value.length);
					}
					else if ((mask = element.getAttribute("${wc.common.attrib.min}")) && value.length < mask) {
						result = true;
						flag = i18n.get("${validation.textField.i18n.minLength}", "%s", mask);
					}

					if (result) {
						label = getFirstLabelForElement(element, true) || element.title || i18n.get("${validation.core.i18n.unlabelledQualifier}");
						message = sprintf.sprintf(flag, label);
						validationManager.flagError({element: element, message: message, attachTo: (textArea.getCounter(element) || element)});
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
				if (TEXTAREA.isOneOfMe(element)) {
					validationManager.revalidationHelper(element, validate);
				}
			}

			/**
			 * Use first focus to attach a change listener in browsers which cannot capture.
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
				}
			}

			/**
			 * Initialise callback to set up event listeners.
			 * @function module:wc/ui/validation/textArea.initialise
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
			 * Late initialisation to attach validation manager subscriber.
			 *
			 * @function module:wc/ui/validation/textArea.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:wc/ui/validation/textArea */ instance = new ValidationTextArea();
		instance.constructor = ValidationTextArea;
		initialise.register(instance);
		return instance;
	});
