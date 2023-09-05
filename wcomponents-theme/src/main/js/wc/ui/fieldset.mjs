/**
 * Provides functionality peculiar to FIELDSET elements around ensuring that the contained controls and legends
 * are correctly synchronised after AJAX transactions.
 */

import initialise from "wc/dom/initialise";
import processResponse from "wc/ui/ajax/processResponse";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement";
import onchangeSubmit from "wc/ui/onchangeSubmit";

const fieldsetSelector = "fieldset";

function makeLegend(el) {
	let accesskey,
		labelContent,
		label = el.firstElementChild,
		labelClass = "wc-moved-label ";
	const WLABEL_CLASS = "wc-label";

	// quickly jump out if we have already got a legend in this fieldset.
	if (label?.matches("legend")) {
		return;
	}
	label = getFirstLabelForElement(el);
	if (!label) {
		labelContent = el.getAttribute("aria-label");
		if (labelContent) {
			el.removeAttribute("aria-label");
		} else {
			labelContent = el.getAttribute("title");
			el.removeAttribute("title");
		}
		labelClass += "wc-off";
	} else {
		labelContent = label.innerHTML;
		labelClass += label.className;
		accesskey = label.getAttribute("data-wc-accesskey");
	}
	const html = `<legend class='${labelClass}'${accesskey ? ` accesskey='${accesskey}'` : ""}>${labelContent}</legend>`;
	el.insertAdjacentHTML("afterbegin", html);
	// label is now the first child of el.
	label = el.firstElementChild;
	label.classList.remove(WLABEL_CLASS);
	onchangeSubmit.warn(el, label);
}

function labelToLegend(element) {
	if (element && element.matches(fieldsetSelector)) {
		makeLegend(element);
	} else {
		Array.from(document.body?.querySelectorAll(fieldsetSelector)).forEach(makeLegend);
	}
}

initialise.register({
	/**
	 * Initialiser callback. For internal use only.
	 *
	 * @function module:wc/ui/fieldset.preInit
	 * @public
	 */
	preInit: () => {
		labelToLegend();
		processResponse.subscribe(labelToLegend, true);
	}
});

export default {
	/**
	 * Gets the selector for a fieldset element.
	 *
	 * @function module:wc/ui/fieldset.getWidget
	 * @public
	 * @returns {string} The description of a fieldset.
	 */
	getWidget: () => fieldsetSelector
};
