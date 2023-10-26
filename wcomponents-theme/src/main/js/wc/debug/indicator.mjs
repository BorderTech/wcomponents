/**
 * Provides an indicator that debug mode is on.
 *
 * @module
 */
import initialise from "wc/dom/initialise.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";

/**
 * Count the number of Element nodes in the current document and set it as the value of an attribute on
 * document.body.
 * @function
 * @private
 */
function countElements() {
	document.body.setAttribute("data-wc-nodeCount", String(document.getElementsByTagName("*").length));
}

initialise.register({
	postInit: () => {
		countElements();
		processResponse.subscribe(countElements, true);
	}
});
