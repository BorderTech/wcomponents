/**
 * Finds the form for this element.
 * @param {HTMLElement|HTMLInputElement} [el] Will find the form that contains this element.
 *    If null, the first form in the DOM.
 * @param {boolean} [forceAncestor] Weird arg - if true, el is mandatory.
 * @return {HTMLFormElement|null}
 */
export default function(el, forceAncestor) {
	if (!el) {
		if (!forceAncestor) {
			return document.querySelector("form");
		}
		return null;
	}
	if (el.form) {
		return el.form;
	}
	return el.closest("form");
}
