/*
 * UC Browser has trouble with width, min-width and max-width calculations. This fix
 * sets a pixel width on the FORM element (WApplication) which alleviates some of these
 * problems.
 */

import initialise from "wc/dom/initialise.mjs";
import getViewportSize from "wc/dom/getViewportSize.mjs";
import event from "wc/dom/event.mjs";
import debounce from "wc/debounce.mjs";
import getForm from "wc/ui/getForm.mjs";

function setWidth() {
	const form = getForm();
	const vp = form ? getViewportSize() : null;
	if (vp) {
		form.style.width = `${vp.width}px`;
	}
}

initialise.register({
	preInit: function() {
		setWidth();
		event.add(window, "resize", debounce(setWidth, 100), 1);
	}
});
