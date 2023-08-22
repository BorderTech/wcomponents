/*
 * UC Browser has trouble with width, min-width and max-width calculations. This fix
 * sets a pixel width on the FORM element (WApplication) which alleviates some of these
 * problems.
 */

import initialise from "wc/dom/initialise";
import getViewportSize from "wc/dom/getViewportSize";
import event from "wc/dom/event";
import debounce from "wc/debounce";
import getForm from "wc/ui/getForm";

function setWidth() {
	const form = getForm();
	const vp = form ? getViewportSize() : null;
	if (vp) {
		form.style.width = `${vp.width}px`;
	}
}

function Width() {
	const resizeEvent = debounce(setWidth, 100);

	this.preInit = function() {
		setWidth();
		event.add(window, "resize", resizeEvent, 1);
	};
}
export default initialise.register(new Width());
