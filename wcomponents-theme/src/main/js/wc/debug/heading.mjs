/**
 * Highlight any labellable elements which are not adequately labelled. This means:
 *
 * 1. no label (or legend for a fieldset); and
 * 2. no aria-label or aria-describedby attribute; and
 * 3. no title
 *
 */

import initialise from "wc/dom/initialise.mjs";
import processResponse from "wc/ui/ajax/processResponse.mjs";
import i18n from "wc/i18n/i18n.mjs";
import utils from "wc/debug/debugUtils.mjs";
import timers from "wc/timers.mjs";

let MISSING_HEADING;
const TAGS = ["h1", "h2", "h3", "h4", "h5", "h6"].join();

function testHeading(element) {
	if (utils.isContentEmpty(element)) {
		element.insertAdjacentHTML("beforeend", MISSING_HEADING);
		element.classList.add("wc-err");
	}
}

/**
 * AJAX subscriber to test for empty headings after ajax has happened.
 *
 * @function
 * @private
 * @param {Element} element The reference element (element being replaced).
 */
function ajaxSubscriber(element) {
	if (element) {
		timers.setTimeout(() => utils.flagBad(TAGS, testHeading, element), 100);
	}
}

initialise.register({
	postInit: function () {
		return i18n.translate("missingHeading").then(function(missingHeading) {
			MISSING_HEADING = missingHeading;
			processResponse.subscribe(ajaxSubscriber, true);
			timers.setTimeout(() => utils.flagBad(TAGS, testHeading), 500);
		});
	}
});
