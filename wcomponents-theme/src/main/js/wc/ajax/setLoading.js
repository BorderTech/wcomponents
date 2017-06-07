define([], function() {
	"use strict";
	/**
	 * Provides a mechanism to set and unset an ajax aria-live region as busy. Moved out of {@link module:wc/ajax/Trigger}
	 * to improve encapsulation.
	 *
	 * Call when a trigger has been fired to set the busy state of the elements it targets. Call when a response is
	 * received to clear the busy state of the element it targets.
	 *
	 * WARNING: The busy state will not be cleared on parts of the UI while other AJAX requests are queued
	 * which will update the same parts of the UI.
	 *
	 * NOTE: The region of the form that will be updated is deliberately disabled because it should not be
	 * serialized as part of the form either in AJAX requests or form submissions. This region of the form
	 * is in an unknown state until the last AJAX response which updates it is processed. The server may get
	 * confused but it must deal with the realities of asynchronous interactions.
	 *
	 * @function
	 * @alias module:wc/ajax/setLoading
	 * @param {module:wc/ajax/Trigger~Request} request The request which is being queued (set loading) or serviced
	 *    (clear loading).
	 * @param {Boolean} [unset] If true we are clearing the loading state, otherwise we are setting it.
	 */
	function setLoading(request, unset) {
		var trigger = request.trigger,
			ids = trigger.loads,
			len = ids.length,
			conflicts,
			element,
			next,
			i;
		for (i = 0; i < len; i++) {
			next = ids[i];
			if (next) {
				try {
					element = document.getElementById(next);
					if (unset) {
						conflicts = trigger.getTriggersFor(next, trigger.getRequestBuffer(), true);
						/*
						 * hasAttribute is tested to prevent us from enabling a region of the
						 * form that should remain disabled. In other words if the AJAX response
						 * returned a disabled element we should be sure not to accidentally enable it.
						 * The "hasAttribute" test is really a test to find out whether the action was
						 * to replace the element (in which case we should leave it alone) or to replace
						 * its content (in which case we should enable it).
						 */
						if (element && element.hasAttribute("aria-busy")) {  // die ie7, die!
							if (conflicts.length < 1) {
								element.removeAttribute("aria-busy");
							}
						} else if (conflicts.length > 0) {
							/*
							* We are being asked to clear the loading state by a trigger which has just received
							* its response HOWEVER there is a chance it is WRONG and we may even need to reapply
							* the loading state if the AJAX response just replaced a part of the UI which will be
							* updated by another trigger in the request buffer.
							*/
							setLoading(conflicts[0]);
						}
					} else if (element) {
						element.setAttribute("aria-busy", true);
					}
				} catch (ex) {
					console.warn(ex);  // probably can't find element
				}
			}
		}
	}
	return setLoading;
});
