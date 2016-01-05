/**
 * Provides a mechanism to set and unset an ajax aria-live region as busy. Moved out of {@link module:wc/ajax/Trigger}
 * to improve encapsulation.
 *
 * @module
 * @requires module:wc/dom/getStyle
 */
define(["wc/dom/getStyle"],
	function(getStyle) {
		"use strict";
		var /**
			 * @constant {String} OLD_HEIGHT The name of the attribute used to hold the pre-ajax height of a target
			 * container if it was specified in a style attribute. Used to reset the height of the container to its
			 * initial (fixed) height after it stops being busy.
			 * @private
			 */
			OLD_HEIGHT = "data-wc-height",
			/**
			 * @constant {String} OLD_WIDTH The name of the attribute used to hold the pre-ajax width of a target
			 * container if it was specified in a style attribute. Used to reset the width of the container to its
			 * initial (fixed) width after it stops being busy.
			 * @private
			 */
			OLD_WIDTH = "data-wc-width",
			/**
			 * @constant {String} UPDATE_SIZE The attribute name used to indicate that the busy region has had its
			 * pre-update size calculated and set so that a region which has its contents removed does not collapse.
			 * @private
			 */
			UPDATE_SIZE = "data-wc-size";

		/**
		 * Removes the custom size set when making an ajax region busy.
		 *
		 * @function
		 * @private
		 * @param {Element} element The element being made no longer busy.
		 */
		function clearCustomSize(element) {
			var size;
			if (element.getAttribute(UPDATE_SIZE)) {
				element.removeAttribute(UPDATE_SIZE);
				if ((size = element.getAttribute(OLD_WIDTH))) {
					element.style.width = size;
				}
				else if (element.style.width) {
					element.style.width = "";
				}
				if ((size = element.getAttribute(OLD_HEIGHT))) {
					element.style.height = size;
				}
				else if (element.style.height) {
					element.style.height = "";
				}
			}
			element.removeAttribute("aria-busy");
		}

		/**
		 * Removes the content of a busy element. Before doing this the element will have a fixed size set.
		 *
		 * @function removeContent
		 * @private
		 * @param {Element} element The element being made busy.
		 */
		function removeContent(element) {
			var child;
			if (setLoading.fixSize(element)) {
				while ((child = element.firstChild)) {
					element.removeChild(child);
				}
			}
		}

		setLoading.clearSize = clearCustomSize;

		setLoading.fixSize = function(element) {
			var result = false, width, height, oldWidth, oldHeight;

			if (!element.getAttribute(UPDATE_SIZE)) {  // already targeted (ie: a conflict) therefore nothing to do
				width = getStyle(element, "width", true, true);
				height = getStyle(element, "height", true, true);
				if (width && height) {  // no point playing with custom sizes if the target has no size
					result = true;
					element.setAttribute(UPDATE_SIZE, "x");
					oldWidth = element.style.width;

					if (oldWidth) {
						element.setAttribute(OLD_WIDTH, oldWidth);
					}
					else {
						element.style.width = width;
					}
					oldHeight = element.style.height;
					if (oldHeight) {
						element.setAttribute(OLD_HEIGHT, oldHeight);
					}
					else {
						element.style.height = height;
					}
				}
			}
			return result;
		};

		/**
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
									clearCustomSize(element);
								}
							}
							else if (conflicts.length > 0) {
								/*
								* We are being asked to clear the loading state by a trigger which has just received
								* its response HOWEVER there is a chance it is WRONG and we may even need to reapply
								* the loading state if the AJAX response just replaced a part of the UI which will be
								* updated by another trigger in the request buffer.
								*/
								setLoading(conflicts[0]);
							}
						}
						else if (element) {
							element.setAttribute("aria-busy", true);
							removeContent(element);
						}
					}
					catch (ex) {
						console.warn(ex);  // probably can't find element
					}
				}
			}
		}
		return setLoading;
	});
