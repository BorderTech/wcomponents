/**
 * Provides a mechanism to clear any selection. Useful when SHIFT + CLICKing (for example) to select multiple
 * check box analog components or dragging to move stuff...
 *
 * @example require(["wc/dom/clearSelection"], function(clearSelection){
 *    clearSelection();  // pretty easy eh?
 * });
 *
 * @module
 */

/**
 * Clears the current selection.
 * @todo investigate other browser means to achieve this, and if there is a w3c standard.
 * @alias module:wc/dom/clearSelection
 */
export default function() {
	let selection = window.getSelection();
	if (selection && selection.removeAllRanges) {
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
