define(["wc/i18n/i18n",
	"wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/validation/isComplete",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/required",
	"wc/ui/fieldset",
	"wc/ui/feedback"],
	function(i18n, attribute, event, initialise, shed, isComplete, validationManager, required, fieldset, feedback) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/fieldset~ValidationFieldset
		 * @private
		 */
		function ValidationFieldset() {
			var FIELDSET = fieldset.getWidget().clone().extend("wc-fieldset"),
				INVALID,
				INITED_KEY = "validation.fieldset.init";

			/**
			 * This is an Array.filter filter function which should return true only if the fieldset is NOT in a
			 * successful state. A fieldset is successful if at least one interactive control within the fieldset is
			 * complete. Therefore it is not successful only if EVERY interactive control is not complete.
			 *
			 * @function
			 * @private
			 * @param {Element} element A FIELDSET element
			 * @returns {boolean} true if the fieldset is not complete.
			 */
			function filterFieldsets(element) {
				return !isComplete.isContainerComplete(element);
			}

			/**
			 * Fieldset required state validation a fieldset is successful if at least one interactive control within
			 * the fieldset is complete.
			 *
			 * @function
			 * @private
			 * @param {Element} container The DOM element being validated.
			 * @returns {boolean} true if container is valid.
			 */
			function validate(container) {
				var result = true,
					elements = required.getRequired(container, FIELDSET, required.CONSTRAINTS.CLASSNAME);
				// are any not complete?
				if (elements && (elements = elements.filter(filterFieldsets)) && elements.length) {
					result = false;
					elements.forEach(function (next) {
						var message = i18n.get("validation_common_incompletegroup", validationManager.getLabelText(next));
						feedback.flagError({
							element: next,
							message: message
						});
					});
				}
				return result;
			}

			/**
			 * This function determines if a fieldset needs to be revalidated and if it does then it resets the
			 * validation. *NOTE:* WFieldSet only needs validation if "required".
			 *
			 * * If something is shown or enabled inside an invalid fieldset it may be populated, making the fieldset valid;
			 * * if something is hidden or disabled inside an invalid fieldset it may make the fieldset 'empty' thereby making the fieldset valid.
			 *
			 * In both cases we need to revalidate to make sure.
			 *
			 * If something changes inside an invalid fieldset we also need to revalidate the fieldset. This is done by having this module subscribe
			 * to validationManager.
			 *
			 * @function
			 * @private
			 * @param {Element} element a control which may be inside an invalid fieldset.
			 */
			function revalidate(element) {
				var container, result = true, initiallyInvalid;

				INVALID = INVALID || FIELDSET.extend("wc_req", {"aria-invalid": "true"});
				container = INVALID.isOneOfMe(element) ? element : INVALID.findAncestor(element);

				while (container) {
					initiallyInvalid = validationManager.isInvalid(container);
					result = validate(container);

					if (result) {
						if (initiallyInvalid) {
							validationManager.setOK(container);
						}
						container = INVALID.findAncestor(container.parentNode);
					} else {
						break;  // if the innermost invalid fieldset is still invalid there is no point traversing
					}
				}
			}

			/**
			 * Subscriber for {@link module:wc/dom/shed} functions which affect the validity of fieldsets.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element acted on by shed.
			 */
			function validationShedSubscriber(element) {
				var targetFieldset;
				if (!(element && (targetFieldset = FIELDSET.findAncestor(element)))) {
					return;
				}
				if (validationManager.isValidateOnChange()) {
					if (validationManager.isInvalid(targetFieldset)) {
						revalidate(targetFieldset);
					} else {
						validate(targetFieldset);
					}
				} else {
					revalidate(targetFieldset);
				}
			}

			function changeEvent($event) {
				/* var element = $event.target,
					targetFieldset;
				if (element && validationManager.isValidateOnChange() && (targetFieldset = FIELDSET.findAncestor(element))) {
					if (validationManager.isInvalid(targetFieldset)) {
						revalidate(targetFieldset);
					} else {
						validate(targetFieldset);
					}
				} */
				var element = $event.currentTarget;
				if (!(element && validationManager.isValidateOnChange())) {
					return;
				}
				if (validationManager.isInvalid(element)) {
					revalidate(element);
				} else {
					validate(element);
				}
			}

			function focusEvent($event) {
				var element = $event.target,
					targetFieldset;
				if (element && validationManager.isValidateOnChange() && (targetFieldset = FIELDSET.findAncestor(element)) &&  !attribute.get(targetFieldset, INITED_KEY)) {
					attribute.set(targetFieldset, INITED_KEY, true);
					event.add(targetFieldset, event.TYPE.change, changeEvent, 1);
				}
			}

			/**
			 * Initialise callback to set up event listeners.
			 * @function module:wc/ui/validation/textArea.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				// if (!validationManager.isValidateOnChange()) {
				// 	return;
				// }
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, 1, null, true);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Initialise callback.
			 *
			 * @function module:wc/ui/validation/fieldset.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
				validationManager.subscribe(revalidate, true);
				shed.subscribe(shed.actions.SELECT, validationShedSubscriber);
				shed.subscribe(shed.actions.DESELECT, validationShedSubscriber);
				shed.subscribe(shed.actions.ENABLE, validationShedSubscriber);
				shed.subscribe(shed.actions.DISABLE, validationShedSubscriber);
				shed.subscribe(shed.actions.SHOW, validationShedSubscriber);
				shed.subscribe(shed.actions.HIDE, validationShedSubscriber);
			};
		}
		/**
		 * Provides functionality to undertake client validation for WFieldSet.
		 *
		 * @module
		 * @requires wc/i18n/i18n
		 * @requires wc/dom/attribute
		 * @requires wc/dom/event
		 * @requires wc/dom/initialise
		 * @requires wc/dom/shed
		 * @requires wc/ui/validation/isComplete
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/fieldset
		 * @requires wc/ui/feedback
		 */
		var instance = new ValidationFieldset();
		initialise.register(instance);
		return instance;
	});
