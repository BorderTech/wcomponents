/**
 * Provides a mechanism to clear any selection. Useful when SHIFT + CLICKing (for example) to select multiple
 * check box analog components or dragging to move stuff...
 * @example require(["wc/dom/clearSelection"], function(clearSelection){
 *    clearSelection();  // pretty easy eh?
 * });
 * @module
 */

const { console, window } = globalThis;

/**
 * Clears the current selection.
 * TO-DO investigate other browser means to achieve this, and if there is a w3c standard.
 * @alias module:wc/dom/clearSelection
 */
export default function clearSelection () {
	let selection = window.getSelection();
	if (selection?.removeAllRanges) {
		selection.removeAllRanges();
	}
	try {
		if (!selection.isCollapsed) {
			selection.collapseToEnd();
		}
	} catch (ex) {
		// consume the exception, we don't want failures here causing the UI to totally stop working
		console.warn(ex.message);
	}
}
