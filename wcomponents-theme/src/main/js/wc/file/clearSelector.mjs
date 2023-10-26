/**
 * File selectors are heavily locked down for security reasons.
 * Clearing the value of a file selector (input of type "file") is not always as easy as setting its value to nothing.
 */

/**
 * Sets a file selector to an empty value.
 * Note that there is a chance the file selector will have to be removed from the DOM and replaced with a clone;
 * As usual this apparently simple task is made complex due to Internet Explorer.
 * @param {HTMLInputElement} element A file input.
 * @param {Function} callback Will be called with the cloned element and a boolean which will be true if the element was cloned.
 */
function clearInput(element, callback) {
	let myClone;
	element.value = "";
	if (element.value !== "") {
		if (element.parentNode) {  // IE10 somehow gets in here with an element with no parent
			myClone = element.cloneNode(false);
			element.parentNode.replaceChild(myClone, element);
		}
	}
	if (callback) {
		callback(myClone || element, !!myClone);
	}
}

export default clearInput;
