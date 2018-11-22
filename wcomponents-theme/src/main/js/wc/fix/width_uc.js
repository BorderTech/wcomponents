/*
 * UC Browser has trouble with width, min-width and max-width calculations. This fix
 * sets a pixel width on the FORM element (WApplication) which alleviates some of these
 * problems.
 */
define([
	"wc/dom/initialise",
	"wc/dom/getViewportSize",
	"wc/dom/event",
	"wc/debounce",
	"wc/ui/getForm"],
	function(initialise, getViewportSize, event, debounce, getForm) {
		"use strict";

		function setWidth() {
			var form = getForm(),
				vp;
			if (form && (vp = getViewportSize())) {
				form.style.width = vp.width  + "px";
			}
		}

		function Width() {
			var resizeEvent = debounce(setWidth, 100);

			this.preInit = function() {
				setWidth();
				event.add(window, event.TYPE.resize, resizeEvent, 1);
			};
		}
		return initialise.register(new Width());
	}
);
