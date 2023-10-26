/**
* Provides functionality to undertake client validation of WMultiSelectPair.
*/

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import multiSelectPair from "wc/ui/multiSelectPair.mjs";
import isComplete from "wc/ui/validation/isComplete.mjs";
import minMax from "wc/ui/validation/minMax.mjs";
import required from "wc/ui/validation/required.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement.mjs";
import i18n from "wc/i18n/i18n.mjs";

const containerSelector = multiSelectPair.getWidget(),
	selectSelector = multiSelectPair.getInputWidget();

/**
 * Gets the multiSelectPair wrapper if the passed in element is a WMultiSelectPair wrapper or any element
 * inside one.
 * @function
 * @private
 * @param {Element} element Any DOM element.
 * @returns {HTMLElement} A WMultiSelectPair wrapper element.
 */
function getContainer(element) {
	return element.closest(containerSelector);
}

/**
 * Re-validate WMultiSelectPair when the selection changes.
 * @function
 * @private
 * @param {Element} container a WMultiSelectPair component.
 */
function revalidate(container) {
	return validationManager.revalidationHelper(container, validate);
}

/**
 * Listen for click events and revalidate.
 * This is wired up on the multiSelectPair container.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement, currentTarget: HTMLElement }} $event A click or dblclick event.
 */
function clickEvent({ target, currentTarget, defaultPrevented }) {
	if (defaultPrevented || shed.isDisabled(target)) {
		return;
	}
	const container = currentTarget;
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
 * @returns {HTMLSelectElement} The select element which holds the selected options.
 */
function getSelectionList(element) {
	return multiSelectPair.getListByType(element, multiSelectPair.LIST_TYPE_CHOSEN);
}


/**
 * Listen for keydown events which can cause a change in the selection of the component and revalidate.
 * This is wired up on the multiSelectPair container.
 * @function
 * @private
 * @param {KeyboardEvent & { target: HTMLElement }} $event A  keydown event.
 */
function keydownEvent({ target, key, defaultPrevented }) {
	if (defaultPrevented) {
		return;
	}
	let expectedType;
	switch (key) {
		// This switch needs to be a cheap and quick bail out, it happens every keydown!
		case "Enter":
			break;
		case "ArrowRight":
			expectedType = multiSelectPair.LIST_TYPE_AVAILABLE;
			break;
		case "ArrowLeft":
			expectedType = multiSelectPair.LIST_TYPE_CHOSEN;
			break;
		default:
			return;  // bail
	}

	/** @type {HTMLSelectElement} */
	const selectList = target.closest(selectSelector);
	if (!selectList) {
		return;
	}
	const selectType = multiSelectPair.getListType(selectList);
	if (!(selectType || selectType === 0)) {
		return;
	}

	// Less than zero if key was Enter, otherwise it was an arrow key, check the list
	if (expectedType >= 0 && selectType === expectedType) {
		const container = this;
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
}

/**
 * Wire up some event listeners on first focus.
 * @function
 * @private
 * @param {FocusEvent & { target: HTMLElement }} $event A wrapped focus/focusin event.
 */
function focusEvent({ target, defaultPrevented }) {
	if (defaultPrevented) {
		return;
	}
	const container = getContainer(target);
	const CONTAINER_INITIALISED_KEY = "validation.multiSelectPair.inited";
	if (container && !container[CONTAINER_INITIALISED_KEY]) {
		container[CONTAINER_INITIALISED_KEY] = true;
		event.add(container, { type: "click", listener: clickEvent, pos: 1, passive: true });
		event.add(container, { type: "dblclick", listener: clickEvent, pos: 1, passive: true });
		event.add(container, { type: "keydown", listener: keydownEvent, pos: 1, passive: true });
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
	const list = getSelectionList(next);
	return !!list?.options.length;
}

/**
 * Test if a container is complete if it is, or contains, WMultiSelectPairs.
 * @function
 * @private
 * @param {Element} container A container element, mey be a WMultiSelectPair wrapper.
 * @returns {boolean} true if the container is complete.
 */
function _isComplete(container) {
	return isComplete.isCompleteHelper(container, containerSelector, amIComplete);
}

/**
 * A custom message function to apply a validation error to an incomplete mandatory WMultiSelectPair.
 * @function
 * @private
 * @param {HTMLElement} element A WMultiSelectPair.
 * @returns {String} a formatted error message used by the validation flag function.
 */
function _requiredMessageFunc(element) {
	const label = getFirstLabelForElement(element, true) || element.title,
		list = getSelectionList(element),
		listLabel = getFirstLabelForElement(list, true) || list.title;
	return i18n.get("validation_multiselectpair_incomplete", label, listLabel);
}

/**
 * A custom validation filter function for WMultiSelectPair. This is an array filter, so we want to keep
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
	const obj = {
			container: container,
			widget: containerSelector,
			constraint: required.CONSTRAINTS.CLASSNAME,
			filter: _filter,
			position: "beforeEnd",
			messageFunc: _requiredMessageFunc
		},
		_required = required.complexValidationHelper(obj);

	// reset obj for minMax checking
	obj.constraint = obj.filter = obj.messageFunc = null;
	obj.selectFunc = getSelectionList;
	obj.minText = "validation_multiselectpair_undermin";
	obj.maxText = "validation_multiselectpair_overmax";

	obj.selectedFunc = function(el) {
		return el.options || [];
	};

	const result = minMax(obj) && _required;
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}

initialise.register({
	/**
	 * Set up initial event listeners.
	 * @function module:wc/ui/validation/multiSelectPair.initialise
	 * @param {Element} element the element being intialised: usually `document.body`
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
	},

	/**
	 * Late set up to wire up subscribers after initialisation.
	 * @function module:wc/ui/validation/multiSelectPair.postInit
	 */
	postInit: function () {
		validationManager.subscribe(validate);
		isComplete.subscribe(_isComplete);
	}
});
