import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import event from "wc/dom/event.mjs";
import getLabelsForElement from "wc/dom/getLabelsForElement.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import $role from "wc/dom/role.mjs";
import textContent from "wc/dom/textContent.mjs";
import wrappedInput from "wc/dom/wrappedInput.mjs";
import checkBox from "wc/ui/checkBox.mjs";
import feedback from "wc/ui/feedback.mjs";

const CLASS_HINT = "wc-label-hint";
const checkboxWrapperSelector = checkBox.getWrapper().toString();
const moveSelectors = [checkboxWrapperSelector, ".wc-radiobutton", "button.wc-selecttoggle"];
const hintSelector = `.${CLASS_HINT}`;
const tags = ["input", "textarea", "select", "fieldset"];

// @ricksbrown this is a workaround for a flaw in our whole rendering system - we should fix the underlying flaw
const movedCbLabelReg = {};

const instance = {
	/**
	 * Get the hint from a given label.
	 *
	 * @function module:wc/ui/label.getHint
	 * @public
	 * @param {Element} label the label to test
	 * @returns {HTMLElement} the label's hint, if any
	 */
	getHint: function (label) {
		if (label) {
			return label.querySelector(hintSelector);
		}
		return null;
	},

	/**
	 * Set (add to or remove) a hint on a label.
	 *
	 * @function module:wc/ui/label.setHint
	 * @public
	 * @param {Element} label the label to which we are modifying hint content
	 * @param {String} [content] the hint content to add; if falsey then an existing hint (if any) is removed
	 */
	setHint: function(label, content) {
		const hint = this.getHint(label);
		const BEFORE_END = "beforeend";
		if (hint) {
			if (content) {
				if (textContent.get(hint)) {
					hint.insertAdjacentHTML(BEFORE_END, "<br>");
				}
				hint.insertAdjacentHTML(BEFORE_END, content);
			} else {
				hint.parentNode.removeChild(hint);
			}
		} else if (content) {
			let html = `<span class='${CLASS_HINT}'>${content}</span>`;
			label.insertAdjacentHTML(BEFORE_END, html);
		}
	},

	/**
	 * Public for testing.
	 * @ignore
	 */
	_convert: convertLabel,
	/**
	 * Public for testing.
	 * @ignore
	 */
	_ajax: ajaxSubscriber,

	/**
	 * Public for testing.
	 * @ignore
	 */
	_checkboxLabelPositionHelper: checkboxLabelPositionHelper,
	/**
	 * Move labels to their correct position.
	 * TODO: This _should_ be done in the Java Renderers.
	 * @function
	 * @private
	 * @param {Element} [element] a container element
	 */
	moveLabels: function(element) {
		const moveSelector = moveSelectors.join();
		const el = element || document.body;
		if (element?.matches(moveSelector)) {
			moveLabel(el);
		} else {
			const moveElements = Array.from(el.querySelectorAll(moveSelector));
			moveElements.forEach(moveLabel);
		}
	}
};

/**
 * Helper to do label manipulation.
 * @function
 * @private
 * @param {Element} label the label to manipulate
 * @param {string} func the function to apply to the label
 */
function mandateLabel(label, func) {
	if (!label.matches("legend")) {
		label.classList[func]("wc_req");
	}
}

/**
 * Helper to do label manipulation.
 * @function
 * @private
 * @param {Element} label the label to manipulate
 * @param {string} func the function to apply to the label
 */
function showHideLabel(label, func) {
	if (!label.matches("legend")) {
		shed[func](label);
	}
}

/**
 * Manipulate a label when a labelled element is made mandatory or optional.
 * @function
 * @private
 * @param {CustomEvent & { target: HTMLElement }} $event An optional/mandatory event.
 */
function shedMandatorySubscriber($event) {
	const { target, type: action} = $event;
	if (!target) {
		return;
	}
	const input = wrappedInput.isOneOfMe(target) ? wrappedInput.getInput(target) : target;
	if (input && input.type !== "radio" && (input.matches(tags.join()) || $role.has(input))) {
		const func = action === shed.events.OPTIONAL ? "remove" : "add";
		getLabelsForElement(target).forEach(function (next) {
			mandateLabel(next, func);
		});
	}
}

/**
 * Show/hide label[s] when a labelled element (even readOnly) is shown/hidden.
 * @function
 * @private
 * @param {CustomEvent & { target: HTMLElement }} $event A show/hide event.
 */
function shedHideSubscriber($event) {
	const { target: element, type: action} = $event;
	if (element) {
		const func = action === shed.events.SHOW ? "show" : "hide";
		// anything, even read-only, can be hidden/shown
		getLabelsForElement(element, true).forEach(next => showHideLabel(next, func));
	}
}

/**
 * This is the function which does the heavy lifting of converting a label into and out of its read-only
 * analogue state when a labelled element is converted. An element may have more than one label (though this
 * is not a good thing) and this function is a forEach iterator function manipulating a single specific
 * labelling element. This function is called only if the labelled element (element) has converted between
 * its active and read-only states.
 * @function
 * @private
 * @param {Element} element The DOM element which is being converted to/from its read-only state via AJAX.
 * @param {Element} label a label (or read-only analogue) for element
 * @param {boolean} [isRO] indicates the element is readOnly, already calculated in the caller so pass it thru.
 */
function convertLabel(element, label, isRO) {
	let newLabellingElement, input;
	const parent = label.parentElement;
	if (isRO) {
		newLabellingElement = element.ownerDocument.createElement("span");
		newLabellingElement.setAttribute("data-wc-rofor", element.id);
	} else {
		newLabellingElement = element.ownerDocument.createElement("label");
		input = wrappedInput.getInput(element);
		if (input) { // should always be found
			newLabellingElement.setAttribute("for", input.id);
		} else if (element.matches(tags.join())) {
			newLabellingElement.setAttribute("for", element.id);
		}
	}
	newLabellingElement.className = label.className;
	newLabellingElement.innerHTML = label.innerHTML;
	input = input || element;
	if (input && input.type !== "radio") {
		mandateLabel(newLabellingElement, (!isRO && shed.isMandatory(input) ? "add" : "remove"));
	}
	if (shed.isHidden(element, true)) {
		shed.hide(newLabellingElement, true); // nothing depends on the hidden state of a label and we are replicating a load-time state.
	}
	newLabellingElement.id = label.id;
	label.id = "";
	parent.insertBefore(newLabellingElement, label);
	parent.removeChild(label);
	// Add submitOnChange warnings.
	if (!isRO) {
		// this cannot be a module level dependency as it would cause a circular
		// dependency. It is also not really important how long this takes.
		// @ts-ignore
		import("wc/ui/onchangeSubmit.mjs").then(function (soc) {
			soc.default.warn(element, newLabellingElement);
		});
	}
}

/**
 * Helper to put checkbox labels in the right place. This is far more complex than it needs to be.
 * @function
 * @private
 * @param {HTMLInputElement} input the labelled WCheckBox
 * @param {Element|String} label the label or its HTML (single element root)
 */
function checkboxLabelPositionHelper(input, label) {
	let labelElement;
	if (!(input && label)) {
		throw new TypeError("Input and label must be defined.");
	}
	if (!(input && input.nodeType === Node.ELEMENT_NODE && label)) {
		throw new TypeError("Input must be an element.");
	}

	if (label.constructor === String) {
		labelElement = input.ownerDocument.createElement("span");
		labelElement.innerHTML = label.trim();
		labelElement = labelElement.firstElementChild;
	} else {
		labelElement = /** @type {HTMLElement} */(label);
	}

	if (labelElement?.nodeType !== Node.ELEMENT_NODE) {
		console.error("label arg must be an Element or HTML String representing a single element");
		// do not throw, this function is not that important
		return;
	}

	if (wrappedInput.isReadOnly(input)) {
		const parent = input.parentElement;
		const refElement = input.nextSibling;
		if (refElement) {
			parent.insertBefore(labelElement, refElement);
		} else {
			parent.appendChild(labelElement);
		}
		return;
	}

	const refElement = feedback.getBox(input, -1);
	if (refElement && refElement.parentElement === input) {
		input.insertBefore(labelElement, refElement);
	} else {
		input.appendChild(labelElement);
	}
}

/**
 * @param {Element} el
 * @return {boolean}
 */
function isActiveWCheckBox(el) {
	if (el?.nodeType !== Node.ELEMENT_NODE) {
		return false;
	}
	return el.matches(checkboxWrapperSelector) && !wrappedInput.isReadOnly(el);
}

/**
 * Move an individual element's label if required.
 * @function
 * @private
 * @param {Element} el a WRadioButton, WCheckBox or WSelectToggle-button
 */
function moveLabel(el) {
	const labels = getLabelsForElement(el, true);
	if (!labels?.length) {
		return;
	}

	const label = labels[0];
	// We **almost** always have to move interactive checkbox labels because otherwise error messages make the label go nuts.
	if (isActiveWCheckBox(el)) {
		// We want to put the label inside the wrapper but before any diagnostics.
		// but did we already put it there as part of the ajaxSubscriber?
		if (el.compareDocumentPosition(label) & el.ownerDocument.DOCUMENT_POSITION_CONTAINED_BY) {
			// label already inside the wraspper so do nothing
			return;
		}
		checkboxLabelPositionHelper(/** @type {HTMLInputElement} */(el), label);
		return;
	}

	// read-only, WRadioButton and WSelectToggle labels go after their labelled component
	if (label === el.nextElementSibling) {
		// already in the right place
		return;
	}
	const parent = el.parentElement;
	const refElement = el.nextSibling;
	if (refElement) {
		parent.insertBefore(label, refElement);
	} else {
		parent.appendChild(label);
	}
}

/**
 * Check if we need to restore a label from the moved label registry.
 * The label for a WCheckBox has to be inside the labelled component's input wrapper to allow for error messages.
 * @function
 * @private
 * @param {Element} element the potentially unlabelled labelled WCheckBox
 */
function checkRestoreLabel(element) {
	const refId = element.id,
		missingLabelContent = movedCbLabelReg[refId];
	try {
		if (missingLabelContent && element.matches(checkboxWrapperSelector)) {
			const notMissingLabels = getLabelsForElement(element, wrappedInput.isReadOnly(element));
			if (!notMissingLabels?.length) {
				// yep, we don't have a label for this check box anymore
				checkboxLabelPositionHelper(/** @type {HTMLInputElement} */(element), missingLabelContent);
			}
		}
	} finally {
		if (missingLabelContent) {
			delete movedCbLabelReg[refId];
		}
	}
}

/**
 * Store a nested label before we blow away a WCheckBox. Only needed if the WCheckBox is EXPLICITLY targeted via AJAX.
 * @param {Element} element
 */
function preInsertionAjaxSubscriber(element) {
	if (!(element && isActiveWCheckBox(element))) {
		return;
	}
	const labels = getLabelsForElement(element);
	if (!labels?.length) {
		return;
	}
	const label = labels[0];
	if (!(element.compareDocumentPosition(label) & element.ownerDocument.DOCUMENT_POSITION_CONTAINED_BY)) {
		// label not inside the wrapper so do nothing
		return;
	}
	movedCbLabelReg[element.id] = label.outerHTML;
}

/**
 * Post-insertion AJAX subscriber to convert labels from a HTML label element to its read-only analogue and vice-versa when
 * a labelled element is replaced via AJAX.
 *
 * @function
 * @private
 * @param {Element} element the new element.
 */
function ajaxSubscriber(element) {
	if (!element) {
		return;
	}

	checkRestoreLabel(element);
	instance.moveLabels(element);
	const wrappedInputs = Array.from(wrappedInput.get(element, true));
	wrappedInputs.forEach(function (next) {
		const isRO = wrappedInput.isReadOnly(next),
			labels = getLabelsForElement(next, true);
		labels.forEach(function (label) {
			const isLabel = label.matches("label");
			// if the new element is readOnly and the old one
			if (isRO && isLabel || !(isRO || isLabel)) {
				convertLabel(next, label, isRO);
				return;
			}
			// only have to do this if we are not converting the labels.
			const input = wrappedInput.getInput(next);
			if (input && input.type !== "radio") {
				mandateLabel(label, !isRO && shed.isMandatory(input) ? "add" : "remove");
			}
			if (shed.isHidden(next, true)) {
				shed.hide(label, true);
			} else {
				shed.show(next);
			}
		});
	});
}

export const initialiser = {
	preInit: instance.moveLabels,

	/**
	 * Initialise callback to subscribe to {@link module:wc/dom/shed} and
	 * {@link module:wc/ui/ajax/processResponse}.
	 * @param {HTMLBodyElement} element
	 * @function module:wc/ui/label.postInit
	 * @public
	 */
	postInit: function (element) {
		event.add(element, shed.events.MANDATORY, shedMandatorySubscriber);
		event.add(element, shed.events.OPTIONAL, shedMandatorySubscriber);
		event.add(element, shed.events.SHOW, shedHideSubscriber);
		event.add(element, shed.events.HIDE, shedHideSubscriber);
		processResponse.subscribe(preInsertionAjaxSubscriber);
		processResponse.subscribe(ajaxSubscriber, true);
	}
};

initialise.register(initialiser);

/**
 * A module which provides functionality peculiar to control labelling elements (labels and label-surrogates). This
 * module is mainly concerned with ensuring that as controls are replaced using AJAX that any labelling components for
 * those controls are always kept in the right state.
 */
export default instance;
