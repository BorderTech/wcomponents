/**
 * Provides a mechanism to clear any selection. Useful when SHIFT + CLICKing (for example) to select multiple
 * check box analog components or dragging to move stuff..
 *
 * @example require(["wc/dom/clearSelection"], function(clearSelection){
 *    clearSelection();  // pretty easy eh?
 * });
 *
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Clears the current selection.
	 * @todo investigate other browser means to achieve this, and if there is a w3c standard.
	 * @alias module:wc/dom/clearSelection
	 */
	return function () {
		if (document.selection) {
			/*
			 * NOTE: IE native code will throw an unknown name error on this call
			 * under certain circumstances
			 */
			try {
				document.selection.empty();
			}
			catch (err) {
				// continue on
				console.log(err.message);
			}
		}
		else if (window.getSelection) {  // Mozilla
			var selection = window.getSelection();
			if (selection && selection.removeAllRanges) {
				selection.removeAllRanges();
			}
			try {
				if (!selection.isCollapsed) {
					selection.collapseToEnd();
				}
			}
			catch (ex) {
				// consume the exception, we don't want failures here causing the UI to totally stop working
				console.warn(ex.message);
			}
		}
	};
});
