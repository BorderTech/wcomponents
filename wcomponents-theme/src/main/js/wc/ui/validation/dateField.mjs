import interchange from "wc/date/interchange";
import getDifference from "wc/date/getDifference";
import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import i18n from "wc/i18n/i18n";
import dateField from "wc/ui/dateField";
import validationManager from "wc/ui/validation/validationManager";
import sprintf from "lib/sprintf";
import isComplete from "wc/ui/validation/isComplete";
import feedback from "wc/ui/feedback";


/**
 * A descriptor for a WDateField.
 * @see {@link module:wc/ui/dateField#getWidget}.
 * @private
 */
const DATE_FIELD = dateField.getWidget();

/**
 * Array filter function which checks if a date is within the future/past constraint (if any).
 * @function
 * @private
 * @param {Element} element A WDateField
 * @returns {boolean} true if constraint not met; false if constraints met, no constraints or date field is empty
 */
function isDateInvalid(element) {
	const LABEL_PLACEHOLDER = "%s";
	let flag, invalid = false,
		minAttrib = "data-wc-min",
		maxAttrib = "data-wc-max";
	const textbox = dateField.isReadOnly(element) ? null : dateField.getTextBox(element);
	if (!textbox || textbox.matches(dateField.getPartialDateWidget())) {
		return false;  // do not apply constraint validation to read-only or partial date fields, even if the date entered is a full date.
	}
	let value = dateField.getValue(element);
	if (value && !validationManager.isExempt(element)) {
		if (textbox.getAttribute("type") === "date") {
			minAttrib = "min";
			maxAttrib = "max";
		}
		let date = interchange.toDate(value);
		/*
		 * There are a set of very unusual circumstances which can cause the change and close calls to
		 * acceptFirstMatch to fail. These involve concurrent use of the keyboard and mouse along with
		 * strategic de-focusing of the document whilst not losing focus of the window. Odd but true. This
		 * double up just makes sure that if there is any input into the input element then we make every
		 * effort to convert it to a value we can understand.
		 */
		if (!date) {
			dateField.acceptFirstMatch(textbox);
			value = dateField.getValue(textbox);
			date = interchange.toDate(value);
		}

		if (date) {
			let dateString = textbox.getAttribute(minAttrib);
			if (dateString) {
				let comparisonDate = interchange.toDate(dateString);
				if (getDifference(date, comparisonDate) < 0) {
					invalid = true;
					dateString = comparisonDate.toLocaleDateString();
					flag = i18n.get("validation_date_undermin");
					// manipulate flag to replace the numbered string placeholders (so it ends up in the same format as the other flags)
					flag = sprintf.sprintf(flag, LABEL_PLACEHOLDER, dateString);
				}
			}
			dateString = textbox.getAttribute(maxAttrib);
			if (dateString) {
				let comparisonDate = interchange.toDate(dateString);
				if (getDifference(date, comparisonDate) > 0) {
					invalid = true;
					dateString = comparisonDate.toLocaleDateString();
					flag = i18n.get("validation_date_overmax");
					// manipulate flag to replace the numbered string placeholders (so it ends up in the same format as the other flags)
					flag = sprintf.sprintf(flag, LABEL_PLACEHOLDER, dateString);
				}
			}
		} else {
			// a full date field can only be valid if a full date is entered and getDateFromElement will return ""
			flag = i18n.get("validation_date_incomplete");
			invalid = true;
		}
	}
	if (invalid) {
		feedback.flagError({ element, message: sprintf.sprintf(flag, validationManager.getLabelText(element)) });
	}
	return invalid;
}

/**
 * Message formatting function. Used by the function validate.
 * @function
 * @private
 * @param {Element} element The element in an invalid state.
 * @returns {String} The formatted validation message.
 */
function messageFunction(element) {
	const textbox = dateField.getTextBox(element);
	return sprintf.sprintf(i18n.get("validation_common_incomplete"), validationManager.getLabelText(textbox));
}

/**
 * Determines if a date field is 'valid' for client side validation. A WDateField is valid
 * if it meets the following criteria:
 * <ol>
 *  <li>If the WDateField is mandatory then the WDateField has content</li>
 *  <li>If the WDateField has content then it is able to be parsed to a date</li>
 *  <li>If the WDateField has min and/or max constraints and has content then the content is within the
 *      expected range.</li></ol>
 * <p><strong>NOTE:</strong> will always return TRUE for a partial date field to prevent date validation problems.</p>
 * @function
 * @private
 * @param {Element} container The element being validated, a form, container or WDateField
 * @returns {boolean} true if the WDateField is valid
 */
function validate(container) {
	let valid = true,
		complete = true;
	const incomplete = [];
	let candidates;
	if (dateField.isOneOfMe(container, true)) {
		candidates = [container];
	} else {
		candidates = Array.from(container.querySelectorAll(DATE_FIELD));
	}
	candidates.forEach(next => {
		if (dateField.isReadOnly(next)) {
			return;
		}
		if (dateField.isLameDateField(next)) {
			if (!next.getAttribute("aria-required")) {
				return;
			}
			if (!dateField.getValue(next)) {
				incomplete.push(next);
			}
		} else {
			const textBox = dateField.getTextBox(next);
			if (!textBox.getAttribute("required")) {
				return;
			}
			if (!textBox.value) {
				incomplete.push(next);
			}
		}
	});

	if (incomplete.length) {
		complete = false;
		incomplete.forEach(next => {
			feedback.flagError({ element: next, message: messageFunction(next) });
		});
	}

	if (dateField.isOneOfMe(container, true)) {
		valid = dateField.isReadOnly(container) || !isDateInvalid(container);
	} else {
		let invalid = candidates.filter(isDateInvalid, this);
		if (invalid && invalid.length) {
			valid = false;
		}
	}
	return complete && valid;
}

/**
 * Change event handler. This is attached to body in browsers which capture and bubble change events and
 * directly to each WDateField's input element when the element is first focused otherwise.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event The change event
 */
function changeEvent($event) {
	const element = $event.target.closest(DATE_FIELD);
	if (element) {
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
}

/**
 * @param {UIEvent & { target: HTMLElement }} $event
 */
function blurEvent($event) {
	const element = $event.target.closest(DATE_FIELD);
	if (element && shed.isMandatory(element)) {
		validate(element);
	}
}

/**
 * Focus event handler used to lazily attach a change event listener to a WDateField when first focused.
 * @function
 * @private
 * @param {FocusEvent & { target: HTMLElement }} $event the wrapped focus/focusin event as published by the WComponent event manager.
 */
function focusEvent({ target, defaultPrevented }) {
	const BOOTSTRAPPED = "validation.dateField.bs";
	if (!defaultPrevented && !target[BOOTSTRAPPED] && dateField.isOneOfMe(target, false)) {
		target[BOOTSTRAPPED] = true;
		event.add(target, "change", changeEvent, 1);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

/**
 * Determines if a WDateField is 'complete'. A date input is complete is it has content without attempting
 * to determine if the content is a valid date.
 * @function
 * @private
 * @param {Element} element A WDateField.
 * @returns {boolean} true if element is complete.
 */
function isCompleteHelper(element) {
	const textbox = dateField.getTextBox(element);
	return !!textbox?.value;
}

/**
 * Subscriber function for {@link ./isComplete} to test the completeness of a container.
 * @function
 * @private
 * @param {Element} container The form, subform or date field we are testing for completeness.
 * @returns {boolean} true if container is complete.
 */
function isCompleteSubscriber(container) {
	return isComplete.isCompleteHelper(container, DATE_FIELD, isCompleteHelper);
}

/**
 * Provides functionality to undertake client validation of WDateField.
 *
 * @module
 */
initialise.register({
	/**
	 * @function module:wc/ui/validation/dateField.initialise
	 * @public
	 * @param {Element} element The element being initialised.
	 */
	initialise: element => {
		event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true });
	},
	/**
	 * Late initialisation function to set up dateField validation.
	 * TODO: move initialisation here, do we need the change listeners so early?
	 * @function module:wc/ui/validation/dateField.postInit
	 * @public
	 */
	postInit: () => {
		isComplete.subscribe(isCompleteSubscriber);
		validationManager.subscribe(validate);
	}
});
