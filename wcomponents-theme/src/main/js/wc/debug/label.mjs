/**
* Highlight any labellable elements which are not adequately labelled. This means:
*
* 1. no label (or legend for a fieldset); and
* 2. no aria-label or aria-describedby attribute; and
* 3. no title
*
*/

import initialise from "wc/dom/initialise";
import processResponse from "wc/ui/ajax/processResponse";
import i18n from "wc/i18n/i18n";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement";
import timers from "wc/timers";
import utils from "wc/debug/debugUtils";

const TAGS = ["input", "textarea", "select", "fieldset"].join(),
	AFTER_END = "afterend";

function nonEmptyAttribute(element, attr) {
	const content = element.getAttribute(attr);
	return content && content.trim();
}

function insertLegend(fieldset) {
	fieldset.insertAdjacentHTML("afterbegin", "<legend class='wc-err'>" + i18n.get("requiredLabel") + "</legend>");
}


function insertLabel(input) {
	const id = input.id,
		endLabel = "</label>",
		isCheckRadio = input.type === "checkbox" || input.type ==="radio";
	let youHaveBeenNaughty = "<label class='wc-label wc-err'";
	if (id) {
		youHaveBeenNaughty += " for='" + input.id + "'";
	}

	youHaveBeenNaughty += ">" + i18n.get("requiredLabel");

	if (id || isCheckRadio) {
		youHaveBeenNaughty += endLabel;
	}

	input.insertAdjacentHTML(isCheckRadio ? AFTER_END : "beforebegin", youHaveBeenNaughty);

	if (!(id || isCheckRadio)) {
		input.insertAdjacentHTML(AFTER_END, endLabel);
	}
}

function isLabelMissing(input) {
	const label = getFirstLabelForElement(input);

	if (label) {
		if (utils.isContentEmpty(label)) {
			label.insertAdjacentHTML("beforeend", i18n.get("requiredLabel"));
			label.classList.add("wc-err");
		}
		return false;
	}
	return true;
}

function testLabel(element) {
	// hidden inputs do not need to be labelled.
	if (element.type === "hidden" || element.classList.contains("wc_nolabel")) {
		return;
	}
	// Any one (or more) of aria-label, title or aria-describedby is OK
	if (nonEmptyAttribute(element, "aria-label") || nonEmptyAttribute(element, "title") || nonEmptyAttribute(element, "aria-describedby")) {
		// We have something, it may not be great, but it is there.
		return;
	}

	// if the label is missing, create a VERY NASTY PLACEHOLDER LABEL!!
	if (isLabelMissing(element)) {
		if (element.matches("fieldset")) {
			insertLegend(element);
		} else {
			insertLabel(element);
		}
	}
}

/**
 * AJAX subscriber to test for missing labels after ajax has happened.
 *
 * @function
 * @private
 * @param {HTMLElement} element The reference element (element being replaced).
 */
function ajaxSubscriber(element) {
	if (element) {
		timers.setTimeout(() => utils.flagBad(TAGS, testLabel, element), 100);
	}
}

initialise.register({
	postInit: () => {
		processResponse.subscribe(ajaxSubscriber, true);
		timers.setTimeout(() => utils.flagBad(TAGS, testLabel), 500);
	}
});
