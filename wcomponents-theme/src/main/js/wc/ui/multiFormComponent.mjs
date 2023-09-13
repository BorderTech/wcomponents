import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import focus from "wc/dom/focus.mjs";
import shed from "wc/dom/shed.mjs";
import uid from "wc/dom/uid.mjs";
import i18n from "wc/i18n/i18n.mjs";
import selectLoader from "wc/ui/selectLoader.mjs";
import debounce from "wc/debounce.mjs";
import prompt from "wc/ui/prompt.mjs";
import ajaxRegion from "wc/ui/ajaxRegion.mjs";
import icon from "wc/ui/icon.mjs";

const containerSelector = "fieldset.wc_mfc";
const fieldSelector = `${containerSelector} li`;
const buttonSelector = `${fieldSelector} button`;
const selectSelector = `${fieldSelector} select`;
const inputSelector = `${fieldSelector} input`;
const controlsSelectors = [selectSelector, inputSelector];
const BUTTON_TYPE = { add: 0, remove: 1 };
const MAX = "data-wc-max";
const queueFocus = debounce(/** @param {Element} container */container => {
	focus.focusFirstTabstop(container);
}, 100);
let REMOVE_BUTTON_TITLE;

const instance = {
	/**
	 * Get the button 'type' (add or remove) for a particular button in a multiFormControl. Only one of them is
	 * an add, the others are all remove.
	 *
	 * @param {Element} element An add or remove button.
	 * @returns {number} either BUTTON_TYPE.add (0) or BUTTON_TYPE.remove (1).
	 */
	getButtonType: function (element) {
		const container = getContainer(element);
		let result = -1;
		if (container) {
			result = (element === container.querySelector(buttonSelector)) ? BUTTON_TYPE.add : BUTTON_TYPE.remove;
		}
		return result;
	},

	/**
	 * Register a multiFormControl on load.
	 *
	 * @param {String[]} idArr An array of ids of WMultiDropdowns and WMultiTextFields.
	 */
	register: function(idArr) {
		if (idArr?.length) {
			initialise.addCallback(() => processNow(idArr));
		}
	},

	/**
	 * Get the description of a multiFormControl container.
	 * @returns {string}
	 */
	getSelector: () => containerSelector,

	/**
	 * Get the description of an input witin a multiFormControl.
	 * @returns {string} The selector which describes the individual dropdowns (in a WMultiDropdown) or
	 *    text inputs (in a WMultiTextField).
	 */
	getInputSelector: () => controlsSelectors.join(),
};

/**
 * Load data list for cacheable WMultiDropdown.
 *
 * @see {@link module:wc/ui/selectLoader.load}
 * @function
 * @private
 * @param {String} id The id of a multiDropdown.
 */
function load(id) {
	const element = document.getElementById(id);
	if (element) {
		const selects = Array.from(element.querySelectorAll(selectSelector));
		selects.forEach(next => {
			const nextId = next.id;
			selectLoader.load(nextId);
		});
	}
}

function processNow(idArr) {
	let id;
	while ((id = idArr.shift())) {
		load(id);
	}
}

/**
 * on click:
 * 1. Am I a button?
 * 2. Am I a button that belongs to multiFormComponent?
 * 3. Am I an add button?
 * 	- Yes: add a new field
 *  - No: remove field
 *  @param {MouseEvent & { target: Element }} $event
 */
function clickEvent({ target, defaultPrevented, shiftKey }) {
	/** @type {HTMLButtonElement} */
	let element = defaultPrevented ? null :target.closest(buttonSelector);
	if (!element && !shed.isDisabled(element)) {
		doClick(element, shiftKey || event["shiftKey"]);  // event.shiftKey - see wc/fixes/shiftKey_ff
	}
}

/**
 * click event heavy lifter
 * @param {HTMLButtonElement} button an add/remove button
 * @param {boolean} SHIFT true if shift key held down during click event
 */
function doClick(button, SHIFT) {
	const type = instance.getButtonType(button);
	let tryAjax;
	if (type === BUTTON_TYPE.add) {
		addNewField(button);
		tryAjax = true;
	} else if (type === BUTTON_TYPE.remove) {
		removeField(button, SHIFT);
		if (button.type === "submit") {
			shed.hide(button);
			button.form.appendChild(button);
		}
		tryAjax = true;
	}
	const container = tryAjax ? getContainer(button) : null;
	if (container && ajaxRegion.getTrigger(container, true)) {
		ajaxRegion.requestLoad(container, null, true);
	}
}

/**
 * The container is the top level element which contains all the sub-elements
 * ie it contains all the fields.
 *
 * @function
 * @private
 * @param {Element} element Any child of the container.
 * @returns {Element} The container if element is a multi form control or one of its descendent elements.
 */
function getContainer(element) {
	return element?.closest(containerSelector);
}

/**
 * Get the field(s) containing each of the interactive controls in a multiFormControl.
 *
 * @param {Element} container A multiFormControl.
 * @param {Boolean} [firstOnly] If true only the first field will be returned.
 * @returns {NodeListOf<Element>|Element} A collection of fields OR a single field if firstOnly is true.
 */
function getFields(container, firstOnly) {
	return firstOnly ? container.querySelector(fieldSelector) : container.querySelectorAll(fieldSelector);
}

/**
 * @param {Element} element An add/remove button
 * @returns {boolean}  true if max has not been reached yet
 */
function checkMaxInputs(element) {
	let result = true;
	const container = getContainer(element),
		max = Number(container.getAttribute(MAX));

	if (max) {
		const fields = /** @type {NodeList} */(getFields(container));
		result = max > fields.length;
	}
	return result;
}

/**
 * Changes ALL ids inside the field
 * @param {Element} field Any DOM element node
 */
function resetField(field) {
	const idSelector = "[id]",
		candidates = Array.from(field.querySelectorAll(idSelector));
	for (const next of candidates) {
		next["elid"] = "";
		const labelSelector = `label[for='${next.id}']`;
		/** @type {HTMLLabelElement} */
		const nextLabel = field.querySelector(labelSelector);
		const nextButtonSelector = `${buttonSelector}[aria-controls]`;
		/** @type {HTMLButtonElement} */
		const nextButton = field.querySelector(nextButtonSelector);
		let nextId = uid();
		if (nextLabel) {
			nextLabel.htmlFor = nextId;
		}
		if (nextButton) {
			nextButton.setAttribute("aria-controls", nextId);
			nextButton.title = REMOVE_BUTTON_TITLE;
			icon.change(nextButton,"fa-minus-square", "fa-plus-square");
		}
		next.id = nextId;
	}
}

/**
 * Addressing two annoying issues here:
 *
 * 1. If the selected attribute is included in the original markup
 * then it will be honored when you clone the select even if you have set
 * selectedIndex to something else.  Obviously can resolve this with a 'removeAttribute'
 * but that wouldn't help with issue 2...
 *
 * 2. Whenever you clone a select the selectedIndex is ignored - if no selected
 * attribute is set then the selected index will be 0.
 *
 * Note that the selectedIndex is again reset in some browsers when you insert the
 * select into the DOM so call this function AFTER insertion.
 * @param {Element} newField
 * @param {Element} prototypeField
 */
function setSelectValues(newField, prototypeField) {
	/** @type {HTMLSelectElement[]} */
	const newSelects = Array.from(newField.querySelectorAll(selectSelector)),
		/** @type {HTMLSelectElement[]} */
		selects = Array.from(prototypeField.querySelectorAll(selectSelector));
	for (let i = 0; i < selects.length; i++) {
		newSelects[i].selectedIndex = selects[i].selectedIndex;
	}
}

/**
 * Prototype field has passed on its data (values) to the new field,
 * therefore we should clear the values from the prototype field.
 * @param {Element} field
 */
function resetPrototypeField(field) {
	Array.from(field.querySelectorAll(inputSelector)).forEach(processCandidateField);
	Array.from(field.querySelectorAll(selectSelector)).forEach(processCandidateField);
	// Array.prototype.forEach.call(TEXTAREA_WD.findDescendants(field), processCandidateField);

	/**
	 * @param {HTMLSelectElement} $element
	 */
	function processCandidateField($element) {
		if ($element.matches(selectSelector)) {
			$element.selectedIndex = 0;
		} else {
			$element.value = "";
		}
	}
}

/**
 * @param {HTMLButtonElement} element The "add button" that initiated this action
 */
function addNewField(element) {
	const container = getContainer(element),
		prototypeField = /** @type {Element} */(getFields(container, true));
	if (prototypeField) {
		if (checkMaxInputs(element)) {
			const newField = /** @type {Element} */(prototypeField.cloneNode(true));
			if (prototypeField.nextElementSibling) {
				prototypeField.parentElement.insertBefore(newField, prototypeField.nextElementSibling);
			} else {
				prototypeField.parentElement.appendChild(newField);
			}

			resetField(newField);
			setSelectValues(newField, prototypeField);
			resetPrototypeField(prototypeField);
		} else {
			prompt.alert(i18n.get("mfc_max"));
		}

	}
}

/**
 * Removes a field (or all but the first field)
 * @param {Element} element The "remove button" that initiated this action
 * @param {boolean} [removeAll] if true all additional fields are removed
 */
function removeField(element, removeAll) {
	let container;
	if (removeAll) {
		container = getContainer(element);
		let fields = /** @type {NodeListOf<Element>} */(getFields(container));
		let i = fields.length;
		while (--i > 0) {
			removeField(fields[i]);
		}
		return;
	}
	const field = element.closest(fieldSelector);
	if (field) {
		container = field.parentElement;
		container.removeChild(field);
		queueFocus(container);
	}
}

/**
 * Provides client side multiple controls:
 *
 * * WMultiDropdown provides a control which has single SELECT elements which can be used to create a multiple selection
 *   tool;
 * * WMultiTextField provides a control which has single SELECT elements which can be used to create a set of single
 *   line text input controls.
 *
 * @module
 */

initialise.register({
	/**
	 * initialisation: set up internationalised strings and event handlers.
	 * @param {Element} element a DOM element: in practice BODY
	 */
	initialise: element => {
		event.add(element, "click", clickEvent);
		return i18n.translate("mfc_remove").then(s => REMOVE_BUTTON_TITLE = s);
	}
});

export default instance;
