import shed from "wc/dom/shed";

const makeInputType = type => `input[type='${type}']`;
// removed =file since this has changed and file is always successful just like text
const submitInputs = ["submit", "image", "reset"].map(makeInputType);

const checkable = [makeInputType("checkbox"), makeInputType("radio")].join();
const submitters = ["button[type='submit']", "button:not([type])"].concat(submitInputs).join();
const buttons = submitInputs.concat(makeInputType("button"), "button[type='button']").join();

/**
 * Tests if a form control is "successful" is the W3C sense of the word:
 * http://www.w3.org/TR/html401/interact/forms.html#h-17.13.2
 *
 * @function module:wc/dom/isSuccessfulElement
 * @param {HTMLElement|HTMLInputElement|HTMLButtonElement|HTMLSelectElement|HTMLTextAreaElement|HTMLObjectElement} element A form control element.
 * @param {boolean} [buttonsAlwaysSucceed] If true and element is a button that meets other criteria for being
 *    successful then the button will be successful.
 * @returns {boolean} true if the element is successful
 */
function isSuccessfulElement(element, buttonsAlwaysSucceed) {
	if (!element["name"] || shed.isDisabled(element) || element.matches(submitters)) {
		return false;
	}
	if (element.matches(checkable)) {
		return /** @type {HTMLInputElement} */ (element).checked;
	}
	if (element.matches(buttons)) {
		return !!buttonsAlwaysSucceed;
	}
	if (element.matches("select")) {
		return /** @type {HTMLSelectElement} */ (element).selectedIndex >= 0;
	}
	return !element.matches("object[declare]");
}

/**
 * Gets all "successful" elements in this DOM subtree (including "element" itself).
 * @param {HTMLElement} element A DOM element.
 * @param {boolean} [buttonsAlwaysSucceed] If true and element is a button that meets other criteria for being
 *    successful then the button will be successful.
 * @returns {HTMLElement[]} An array of successful elements found in this DOM subtree.
 */
isSuccessfulElement.getAll = function(element, buttonsAlwaysSucceed) {
	let result = [];
	if (element?.nodeType === Node.ELEMENT_NODE) {
		if (!element.matches("form") && isSuccessfulElement(element, buttonsAlwaysSucceed)) {
			result.push(element);
		} else if (element.children) {
			for (let i = 0; i < element.children.length; i++) {
				let next = element.children[i];
				let nextResult = isSuccessfulElement.getAll(/** @type {HTMLElement} */ (next), buttonsAlwaysSucceed);
				if (nextResult.length) {
					result = result.concat(nextResult);
				}
			}
		}
	}
	return result;
};

export default isSuccessfulElement;
