import wrappedInput from "wc/dom/wrappedInput";

const labelable = ["input", "select", "textarea", "progress"];
const fieldsetSelector = "fieldset",
	legendSelector = "legend",
	labelSelector = "label";

/**
 * @param {string} id
 * @return {string} `label[for='${string}']`
 */
const labelForSelector = id => `${labelSelector}[for='${id}']`;
/**
 * @param {string} id
 * @return {string} `[data-wc-for='${string}']`
 */
const labelDataForSelector = id => `[data-wc-for='${id}']`;
/**
 * @param {string} id
 * @return {string} `[data-wc-rofor='${string}']`
 */
const labelDataRoForSelector = id => `[data-wc-rofor='${id}']`;

/**
 * Gets the selectors that will find labels for this id.
 * Example, if you pass "myId" and include readonly you will get:
 *    [ "label[for='myId']", "[data-wc-for='myId']" , "[data-wc-rofor='myId']" ]
 * @param {string} id
 * @param {boolean} [inclReadOnly]
 * @return {string[]} An array of selectors.
 */
function getLabelForSelectors(id, inclReadOnly) {
	if (id) {
		const result = [labelForSelector(id), labelDataForSelector(id)];
		if (inclReadOnly) {
			result.push(labelDataRoForSelector(id));
		}
		return result;
	}
	return [];
}

/**
 * Get labels and/or stand-ins using querySelector.
 *
 * @function
 * @private
 * @param {Element} element The labelled element.
 * @param {HTMLElement[]} [labelArr] Labels we have already found.
 * @param {Boolean} [readOnly] If true also get labels for element in its read-only state.
 * @returns {HTMLElement[]} If element has no 'labels' then an empty array is returned.
 */
function doLabelQuery(element, labelArr, readOnly) {
	let result = labelArr || [];
	const { id } = element;
	const query = getLabelForSelectors(id, readOnly);
	if (query.length) {
		if (readOnly && wrappedInput.isReadOnly(element)) {
			// we may be in an AJAX situation where we are trying to convert
			// labels to spans or vice-versa.
			const wrappedId = wrappedInput.getWrappedId(element);
			query.push(labelForSelector(wrappedId));
		}
		const labels = /** @type {NodeListOf<HTMLElement>} */(document.querySelectorAll(query.join()));
		result = result.concat(Array.from(labels));
	}
	return result;
}

function getLabelsForWrapper(element, includeReadOnly) {
	let result = [];
	if (includeReadOnly) {
		result = doLabelQuery(element, result, true);
	}
	const _input = wrappedInput.getInput(element);
	if (_input) {
		return doLabelQuery(_input, result);
	}
	return result;
}

function getAncestorLabel(element) {
	const label = element.closest(labelSelector);
	if (!label) {
		return [];
	}

	if (!label.hasAttribute("for") || label.matches(labelForSelector(element.id))) {
		return [label];
	}
	return [];
}

/**
 * Get element/s defined in aria-labelledby attribute
 * @param {Element} element The element with or without aria-labelledby attribute.
 * @returns {HTMLElement[]} An array of element/s. Element with 'id/s' listed in aria-labelledby attribute.
 * If either element with 'id' is not found, or aria-labelledby attribute is not found, then an empty array is returned.
 */
function getAriaLabelledElements(element) {
	const ariaLabels = [];
	const labelIds = element.getAttribute("aria-labelledby");
	if (labelIds) {
		labelIds.split(/\s+/).forEach(function(labelId) {
			if (labelId) {
				const lblElement = document.getElementById(labelId);
				if (lblElement) {
					ariaLabels.push(lblElement);
				}
			}
		});
	}
	return ariaLabels;
}

/**
 * Gets labelling element/s (label, legend or pseudo-label) for a control.
 *
 * @function module:wc/dom/getLabelsForElement
 * @param {Element} element The element for which we want to find labels.
 * @param {Boolean} [includeReadOnly] If true then also search for "labelling" elements for read only controls.
 * @returns {HTMLElement[]} An array of elements which 'label' element. If element has no 'labels' then an empty array
 *  is returned.
 */
function getLabels(element, includeReadOnly) {
	if (element) {
		const ariaLabelledElements = getAriaLabelledElements(element);
		let nativeLabeledElements = [];
		if (wrappedInput.isOneOfMe(element, includeReadOnly)) {
			nativeLabeledElements = getLabelsForWrapper(element, includeReadOnly);
		} else {
			if (element.matches(fieldsetSelector)) {
				const label = Array.from(element.children).find(next => next.matches(legendSelector));
				if (label) {
					nativeLabeledElements = [label];
				}
			}

			nativeLabeledElements = doLabelQuery(element, nativeLabeledElements, includeReadOnly);

			if (!nativeLabeledElements?.length) {
				// try getting an ancestor label element ONLY if element is input, textarea, select or progress.
				if (element.matches(labelable.join())) {
					nativeLabeledElements = getAncestorLabel(element);
				}
			}
		}
		// Append ariaLabel elements at the end of nativeLabel elements
		return nativeLabeledElements.concat(ariaLabelledElements);
	}
}

export default getLabels;
