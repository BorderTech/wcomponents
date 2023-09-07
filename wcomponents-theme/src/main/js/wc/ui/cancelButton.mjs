/**
 * Simple module to add cancelUpdate functionality to a form if a WCancelButton is added with an unsavedChanges
 * flag. This can be done in XSLT if the WCancelButton exists in the initial page load but not if it is inserted
 * via Ajax. We do not do it in XSLT because the JavaScript is so much faster. We only need to do this once for
 * any screen with such a WCancelButton.
 */

import initialise from "wc/dom/initialise";
import processResponse from "wc/ui/ajax/processResponse";
/* cancelUpdate is added as a requirement because any cancel button will need it implicitly */
import "wc/ui/cancelUpdate";

const cancelButtonSelector = "button.wc_btn_cancel";
const unsavedClass = "wc_unsaved";
const unsavedButtonSelector = `${cancelButtonSelector}.${unsavedClass}`;
const formSelector = "form";

/**
 * Mark the form as having unsaved changes if a flagged cancel button is inserted via ajax.
 * @function
 * @private
 * @param {Element} element The AJAX target element.
 */
function ajaxSubscriber(element) {
	if (element) {
		const matches = element.matches(unsavedButtonSelector) || element.querySelector(unsavedButtonSelector);
		const form = matches ? element.closest(formSelector) : null;
		form?.classList.add(unsavedClass);
	}
}


initialise.register({
	/**
	 * Late initialisation to process any flagged cancel buttons and set up ajax subscribers.
	 * @function module:wc/ui/cancelButton.postInit
	 * @public
	 */
	postInit: function() {
		const button = document.body.querySelector(unsavedButtonSelector);
		const form = button?.closest(formSelector);
		if (form) {
			form.classList.add(unsavedClass);
		} else {
			processResponse.subscribe(ajaxSubscriber, true);
		}
	},
	/**
	 * Unsubscribes event listeners etc.
	 */
	deinit: () => processResponse.unsubscribe(ajaxSubscriber, true)
});

export default {
	/**
	 * Get the description of a cancel button.
	 * @function module:wc/ui/cancelButton.getWidget
	 * @public
	 * @returns {string}
	 */
	getWidget: () => cancelButtonSelector
};
