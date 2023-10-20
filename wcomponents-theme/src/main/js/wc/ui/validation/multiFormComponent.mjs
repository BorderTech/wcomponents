/**
 * Provides functionality to undertake client validation of WMultiDropdown and WMultiTextField.
 *
 * @module
 */

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import i18n from "wc/i18n/i18n.mjs";
import multiFormComponent from "wc/ui/multiFormComponent.mjs";
import unique from "wc/array/unique.mjs";
import sprintf from "wc/string/sprintf.mjs";
import required from "wc/ui/validation/required.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import isComplete from "wc/ui/validation/isComplete.mjs";
import feedback from "wc/ui/feedback.mjs";

const containerSelector = multiFormComponent.getSelector();

/**
 * Get the top level element which contains all the sub-elements ie that which contains all of the fields.
 * @function
 * @private
 * @param {Element} element A HTML element, hopefully one inside a multiFormComponent.
 * @returns {Element} The container element if element is a descendant of a multiFormComponent.
 */
function getContainer(element) {
	return element.closest(containerSelector);
}

/**
 * An array filter function which filters out "null" options from a select so that we can determine if a
 * single selection select element has a valid selection.
 * @function
 * @private
 * @param {HTMLSelectElement} select A HTML SELECT element.
 * @returns {boolean} true if the select has a selection and that selection is NOT a "null" option.
 */
function selectValidOptionFilter(select) {
	let result = select.selectedIndex > -1;
	if (result) {
		result = !select.options[select.selectedIndex].hasAttribute("data-wc-null");
	}
	return result;
}

/**
 * An array filter function to determine if a particular component is "complete". Passed in to
 * {@link module:wc/ui/validation/isComplete}
 * @function
 * @private
 * @param {Element} next A multi form component.
 * @returns {boolean} true if the selected items list in the component has one or more options.
 */
function amIComplete(next) {
	const inputs = Array.from(next.querySelectorAll("input"));
	if (inputs.length) {
		return inputs.some(n => isComplete.isComplete(n));
	}
	const selects = Array.from(next.querySelectorAll("select"));
	if (selects.length) {
		return selects.some(n => isComplete.isComplete(n));
	}
	return false;
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
 * Array filter function for selecting invalid multi form controls. The filter will also flag these invalid
 * components as it iterates: saves us another iteration.
 * @function
 * @private
 * @param {Element} next A multi form control.
 * @returns {boolean} true if 'next' is invalid.
 */
function filter(next) {
	// added parseInt because for a while these values were being compared to non-numeric objects
	let min = parseInt(next.getAttribute("data-wc-min")),
		max = parseInt(next.getAttribute("data-wc-max")),
		underFlag = "validation_common_undermin",
		overFlag = "validation_common_overmax",
		isInvalid = false,
		count, flag, limit;
	if (min || max) {
		count = Array.from(next.querySelectorAll("select"));
		if (count.length) {
			count = unique(count.filter(selectValidOptionFilter), (a, b) => a.selectedIndex - b.selectedIndex).length;
		} else {
			count = Array.from(next.querySelectorAll("input"));
			if (count.length) {
				// WMultiTextField may have empty inputs to fool the validator!
				count = unique(count.filter(input => input.value), (a, b) => {
					return (a.value === b.value ? 0 : 1);
				}).length;
				underFlag = "validation_multitext_undermin";
				overFlag = "validation_multitext_overmax";
			} else {
				// set it to zero because otherwise it will be a zero length nodelist and not equivalent of false
				count = 0;
			}
		}

		if (count) {  // count may be zero now (after filter/unique)
			if (min && count < min) {
				isInvalid = true;
				flag = i18n.get(underFlag);
				limit = min;
			} else if (max && count > max) {
				isInvalid = true;
				flag = i18n.get(overFlag);
				limit = max;
			}
		}
	}
	if (isInvalid) {
		_flag(next, flag, limit);
	}
	return isInvalid;
}

/**
 * Flag any errors.
 * @function
 * @private
 * @param {Element} element The multiFormComponent which failed validation.
 * @param {String} flag The framework text of the message in sprintf format with placeholders for the label
 *                 text and selection constraint limit.
 * @param {number} limit The max/min number of values/selections.
 */
function _flag(element, flag, limit) {
	const message = sprintf(flag, validationManager.getLabelText(element), limit);
	feedback.flagError({ element, message });
}

/**
 * Validation for multiFormComponent.
 * @function
 * @private
 *
 * @param {Element} container DOM element, the container being validated (usually form).
 * @returns {boolean} true if the container is valid.
 */
function validate(container) {
	let result = true;
	const obj = { container: container,
			widget: containerSelector,
			constraint: required.CONSTRAINTS.CLASSNAME,
			position: "beforeEnd" },
		_required = required.complexValidationHelper(obj);
	let controls = container.matches(containerSelector) ? [container] : Array.from(container.querySelectorAll(containerSelector));
	if (controls) {
		controls = controls.filter(filter);
		result = !(controls && controls.length);
	}
	result &&= _required;
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}


/**
 * Revalidate a multiFormControl after a change of input/selection or a change in the number of fields.
 * @function
 * @private
 * @param {Element} element The MultiFormComponent container element wrapping the component which invoked the event.
 */
function revalidate(element) {
	const container = getContainer(element);
	if (container) {
		validationManager.revalidationHelper(container, validate);
	}
}

/**
 * Revalidate on change.
 * @function
 * @param {UIEvent & { target: Element }} $event a change event as wrapped by the WComponent event module.
 */
function changeEvent($event) {
	const container = getContainer($event.target);
	if (container) {
		if (validationManager.isValidateOnChange()) {
			if (validationManager.isInvalid(container)) {
				revalidate(container);
				return;
			}
			validate(container);
			return;
		}
		revalidate(container);
	}
}

/**
 *
 * @param {UIEvent & { target: Element }} $event
 */
function blurEvent($event) {
	const element = $event.target,
		container = getContainer(element);
	if (container && !validationManager.isInvalid(container)) {
		validate(container);
	}
}

/**
 * Successful click on an add/remove field button will need to revalidate.
 * @function
 * @private
 * @param {MouseEvent  & { target: Element }} $event a click event as wrapped by the WComponent event module.
 */
function clickEvent($event) {
	let element;
	if (!$event.defaultPrevented && (element = $event.target.closest("button"))) {
		let container;
		if (multiFormComponent.getButtonType(element) && (container = getContainer(element))) {
			revalidate(container);
		}
	}
}

/**
 * Browsers which cannot capture change events have to attach the event to each component.
 * @function
 * @private
 * @param {FocusEvent & { target: Element }} $event a focus[in] event as wrapped by the WComponent event module.
 */
function focusEvent($event) {
	const element = $event.target,
		BOOTSTRAPPED = "validation.multiFormComponent.bs";
	if (element && !element[BOOTSTRAPPED] && element.matches(multiFormComponent.getInputSelector())) {
		element[BOOTSTRAPPED] = true;
		event.add(element, "change", changeEvent);
		let container = getContainer(element);
		if (container && !container[BOOTSTRAPPED] && validationManager.isValidateOnBlur()) {
			container[BOOTSTRAPPED] = true;
			event.add(container, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

initialise.register({
	/**
	 * Initialisation callback to set up event listeners.
	 * @function module:wc/ui/validation/multiFormComponent.initialise
	 * @param {Element} element A DOM element: in practice ths is usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "click", clickEvent, 1);  // we want the click handler late so that add/remove runs first.
	},

	/**
	 * Initialisation callback to attach vaidation subscriber.
	 * @function module:wc/ui/validation/multiFormComponent.postInit
	 */
	postInit: function() {
		validationManager.subscribe(validate);
		isComplete.subscribe(_isComplete);
	}
});

