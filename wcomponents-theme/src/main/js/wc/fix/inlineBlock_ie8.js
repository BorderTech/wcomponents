define(["wc/has", "wc/dom/classList", "wc/timers"], function(has, classList, timers) {
	"use strict";
	var redrawTimer,
		TRIGGER_CLASS = "noop",
		REDRAW_DELAY = 100;  // delay before redrawing (performance optimisation to prevent rapid fire redraws)

	/**
	 * Force repaint by changing the className of document.body
	 * The CSS class we use has no actual style rules associated, simply adding or removing
	 * a class will trigger repaint.
	 * @function
	 * @private
	 * @ignore
	 */
	function repaint() {
		redrawTimer = null;
		classList.toggle(document.body, TRIGGER_CLASS);
		console.log("Forcing IE8 to repaint...");
	}

	/**
	 * Undertake a forced screen repaint if required.
	 * @function
	 * @private
	 * @ignore
	 * @param {Element} element The element which is being manipulated in a way which may require a screen repaint.
	 */
	function checkRepaint(element) {
		try {
			if (document.body && document === element.ownerDocument && document.body.contains(element)) {
				if (redrawTimer) {
					timers.clearTimeout(redrawTimer);
				}
				redrawTimer = timers.setTimeout(repaint, REDRAW_DELAY);
			}
		}
		catch (ex) {
			// don't let this fix break other stuff
		}
	}

	if (has("ie") === 8) {
		require(["wc/dom/shed"], function(shed) {
			shed.subscribe(shed.actions.SHOW, checkRepaint);
			shed.subscribe(shed.actions.HIDE, checkRepaint);
			shed.subscribe(shed.actions.SELECT, checkRepaint);
			shed.subscribe(shed.actions.DESELECT, checkRepaint);
			shed.subscribe(shed.actions.EXPAND, checkRepaint);
			shed.subscribe(shed.actions.COLLAPSE, checkRepaint);
		});
	}
	/**
	 * IE 8 has a flawed implementation of display:inline-block which results in the screen failing to repaint
	 * when certain components are shown/expanded. Some combinations of attribute selectors are also flawed which
	 * results in failed CSS repaints when WAI-ARIA widgets have a state change. This module is used to force a
	 * screen repaint in cases where these repaint failures occur. We take care of common scenarios but there are
	 * some cases where individual components have to manually call a repaint in IE8. Screen repaints are time
	 * consuming and expensive so you should only use this facility when necessary.
	 *
	 * @module
	 * @private
	 * @requires module:wc/has
	 * @requires module:wc/dom/classList
	 * @requires module:wc/timers
	 */
	return { checkRepaint: checkRepaint };
});
