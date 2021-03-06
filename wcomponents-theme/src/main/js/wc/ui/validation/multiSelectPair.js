/**
 * Provides functionality to undertake client validation of WMultiSelectPair.
 *
 * @module wc/ui/validation/multiSelectPair
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/multiSelectPair
 * @requires module:wc/ui/validation/isComplete
 * @requires module:wc/ui/validation/minMax
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires module:wc/i18n/i18n
 */
define(["wc/dom/attribute",
	"wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/multiSelectPair",
	"wc/ui/validation/isComplete",
	"wc/ui/validation/minMax",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/getFirstLabelForElement",
	"wc/i18n/i18n"],
function(attribute, event, initialise, shed, multiSelectPair, isComplete, minMax, required, validationManager, getFirstLabelForElement, i18n) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/ui/validation/multiSelectPair~ValidationMultiSelectPair
	 * @private
	 */
	function ValidationMultiSelectPair() {
		var CONTAINER = multiSelectPair.getWidget(),
			SELECT = multiSelectPair.getInputWidget(),
			CONTAINER_INITIALISED_KEY = "validation.multiSelectPair.inited";

		/**
		 * Gets the multiSelectPair wrapper if the passed in element is a WMultiSelectPair wrapper or any element
		 * inside one.
		 * @function
		 * @private
		 * @param {Element} element Any DOM element.
		 * @returns {Element} A WMultiSelectPair wrapper element.
		 */
		function getContainer(element) {
			return CONTAINER.findAncestor(element);
		}

		/**
		 * Re-validate WMultiSelectPair when the selection changes.
		 * @function
		 * @private
		 * @param {Element} container a WMultiSelectPair component.
		 */
		function revalidate(container) {
			validationManager.revalidationHelper(container, validate);
		}

		/**
		 * Listen for click events and revalidate.
		 * @function
		 * @private
		 * @param {wc/dom/event} $event A Wrapped click or dblclick event.
		 */
		function clickEvent($event) {
			var element = $event.target, container;
			if ($event.defaultPrevented || ! element || shed.isDisabled(element)) {
				return;
			}
			container = getContainer(element);
			if (!container) {
				return;
			}
			if (validationManager.isValidateOnChange()) {
				if (validationManager.isInvalid(container)) {
					revalidate(container);
				} else {
					validate(container);
				}
				return;
			}
			revalidate(container);
		}

		/**
		 * Gets the "selected items" list from a WMultiSelectPair.
		 * @function
		 * @private
		 * @param {Element} element A WMultiSelectPair.
		 * @returns {Element} The select element which holds the selected options.
		 */
		function getSelectionList(element) {
			return multiSelectPair.getListByType(element, multiSelectPair.LIST_TYPE_CHOSEN);
		}


		/**
		 * Listen for keydown events which can cause a change in the selection of the component and revalidate.
		 * @function
		 * @private
		 * @param {module:wc/dom/event} $event A wrapped keydown event.
		 */
		function keydownEvent($event) {
			var selectList, keyCode = $event.keyCode, selectType, container, carryOn;
			// this is cheaper than any other test in this function
			if ($event.defaultPrevented || !(keyCode === KeyEvent.DOM_VK_RETURN || keyCode === KeyEvent.DOM_VK_RIGHT || keyCode === KeyEvent.DOM_VK_LEFT)) {
				return;
			}
			selectList = SELECT.findAncestor($event.target);
			if (!selectList) {
				return;
			}
			container = getContainer(selectList);
			if (!container) {
				return;
			}
			selectType = multiSelectPair.getListType(selectList);
			if (!(selectType || selectType === 0)) {
				return;
			}
			carryOn = keyCode === KeyEvent.DOM_VK_RETURN;
			if (!carryOn) {
				carryOn = keyCode === KeyEvent.DOM_VK_RIGHT && selectType === multiSelectPair.LIST_TYPE_AVAILABLE;
			}
			if (!carryOn) {
				carryOn = keyCode === KeyEvent.DOM_VK_LEFT && selectType === multiSelectPair.LIST_TYPE_CHOSEN;
			}
			if (!carryOn) {
				return;
			}
			if (validationManager.isValidateOnChange()) {
				if (validationManager.isInvalid(container)) {
					revalidate(container);
				} else {
					validate(container);
				}
				return;
			}

			revalidate(container);
		}


		/**
		 * Wire up some event listeners on first focus.
		 * @function
		 * @private
		 * @param {module:wc/dom/event} $event A wrapped focus/focusin event.
		 */
		function focusEvent($event) {
			var container;
			if (!$event.defaultPrevented && (container = getContainer($event.target)) && !attribute.get(container, CONTAINER_INITIALISED_KEY)) {
				attribute.set(container, CONTAINER_INITIALISED_KEY, true);
				event.add(container, "click", clickEvent, 1);
				event.add(container, "dblclick", clickEvent, 1);
				event.add(container, "keydown", keydownEvent, 1);
			}
		}

		/**
		 * An array filter function to determine if a particular component is "complete". Passed in to
		 * {@link module:wc/ui/validation/isComplete} as part of
		 * {@link module:wc/ui/validation/multiSelectPair~_isComplete}.
		 * @function
		 * @private
		 * @param {Element} next A WMultiSelectPair.
		 * @returns {boolean} true if the selected items list in the component has one or more options.
		 */
		function amIComplete(next) {
			var _result = false, list;
			if ((list = getSelectionList(next))) {
				_result = !!list.options.length;
			}
			return _result;
		}

		/**
		 * Test if a container is complete if it is, or contains, WMultiSelectPairs.
		 * @function
		 * @private
		 * @param {Element} container A container element, mey be a WMultiSelectPair wrapper.
		 * @returns {boolean} true if the container is complete.
		 */
		function _isComplete(container) {
			return isComplete.isCompleteHelper(container, CONTAINER, amIComplete);
		}

		/**
		 * A custom message function to apply a validation error to an incomplete mandatory WMultiSelectPair.
		 * @function
		 * @private
		 * @param {Element} element A WMultiSelectPair.
		 * @returns {String} a formatted error message used by the validation flag function.
		 */
		function _requiredMessageFunc(element) {
			var label = getFirstLabelForElement(element, true) || element.title,
				list = getSelectionList(element),
				listLabel = getFirstLabelForElement(list, true) || list.title;
			return i18n.get("validation_multiselectpair_incomplete", label, listLabel);
		}

		/**
		 * A custom validation filter function for WMultiSelectPair. This is an array filter so we want to keep
		 * components which are not complete.
		 * @function
		 * @private
		 * @param {Element} element a WMultiSelectPair.
		 * @returns {boolean} true if the component is not complete.
		 */
		function _filter(element) {
			return !amIComplete(element);
		}

		/**
		 * Validate WMultiSelectPair: A WMultiSelectPair which is required must have at least one option in the
		 * "selected" list. Constraint validation exists for minimum number of options and maximum number of options.
		 * @function
		 * @private
		 * @param {Element} container A WMultiSelectPair or a container which may contain WMultiSelectPairs. Often a form.
		 * @returns {boolean} true if the container is valid.
		 */
		function validate(container) {
			var obj = {container: container,
					widget: CONTAINER,
					constraint: required.CONSTRAINTS.CLASSNAME,
					filter: _filter,
					position: "beforeEnd",
					messageFunc: _requiredMessageFunc
				},
				_required = required.complexValidationHelper(obj),
				result = true;

			// reset obj for minMax checking
			obj.constraint = obj.filter = obj.messageFunc = null;
			obj.selectFunc = getSelectionList;
			obj.minText = "validation_multiselectpair_undermin";
			obj.maxText = "validation_multiselectpair_overmax";

			obj.selectedFunc = function(el) {
				return el.options || [];
			};

			result = minMax(obj);
			return result && _required;
		}


		/**
		 * Set up initial event listeners.
		 * @function module:wc/ui/validation/multiSelectPair.initialise
		 * @param {Element} element the element being intialised: usually document.body
		 */
		this.initialise = function(element) {
			if (event.canCapture) {
				event.add(element, { type: "focus", listener: focusEvent, capture: true });
			} else {
				event.add(element, "focusin", focusEvent);
			}
		};

		/**
		 * Late set up to wire up subscribers after initialisation.
		 * @function module:wc/ui/validation/multiSelectPair.postInit
		 */
		this.postInit = function () {
			validationManager.subscribe(validate);
			isComplete.subscribe(_isComplete);
		};
	}

	return /** @alias module:wc/ui/validation/multiSelectPair */ initialise.register(new ValidationMultiSelectPair());
});
