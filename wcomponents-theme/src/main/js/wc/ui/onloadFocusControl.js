define(["wc/dom/focus", "wc/dom/initialise", "wc/ui/ajax/processResponse", "wc/timers", "wc/dom/messageBox"],
	function(focus, initialise, processResponse, timers, messageBox) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/onloadFocusControl~OnloadFocusControl
		 * @private
		 */
		function OnloadFocusControl() {
			var focusId;

			/**
			 * Makes the attempt to focus an element
			 * @function
			 * @private
			 * @param {String} targetId The id of the element to focus (or focus in).
			 * @param {boolean} [ignoreMessages] if `true` then allow focus request even if there are message boxes in the view
			 */
			function doRequestFocus(targetId, ignoreMessages) {
				var element;
				if ((element = document.getElementById(targetId)) && canPolitelyChangeFocus(ignoreMessages)) {
					if (focus.canFocus(element)) {
						focus.setFocusRequest(element);
					} else if (focus.canFocusInside(element)) { // try focusing inside the target
						focus.focusFirstTabstop(element);
					} else if (element.clientHeight === 0 && element.clientWidth === 0 && (element = focus.getFocusableAncestor(element))) {
						// as a last resort try focusing the nearest focusable ancestor of element if element has no dimensions
						focus.setFocusRequest(element);
					}
				}
			}

			/**
			 * Determine if we can change the focus without upsetting what the user is doing.
			 * This is useful when an ajax response is asking us to change focus. We will only do so if this does not
			 * interfere with the user.
			 * @function
			 * @private
			 * @returns {Boolean} true if it is ok to change focus from whereever it happens to be at the moment.
			 * @param {boolean} [ignoreMessages] if `true` then allow focus request even if there are message boxes in the view
			 */
			function canPolitelyChangeFocus(ignoreMessages) {
				var element = document.activeElement,
					result = !element || !element.tagName || element === document.body || element === document.documentElement;
				if (!result) {
					// ok, something has focus, but let's REALLY make sure it's something sensible because some browsers allow invisible elements to retain focus
					/*
					 * If it is hidden directly or via an ancestor it will be fine to steal the focus.
					 * Some browsers allow hidden elements to remain focused, some do not.
					 * We can not rely on the browser or the script that did the hiding to clear the focus.
					 */
					result = (element.clientHeight === 0 && element.clientWidth === 0);
				}
				if (result && !ignoreMessages) {
					// we still can't focus if there are message boxes in the view
					if (messageBox.getErrorBoxes()) {
						result = false;
					}
				}
				return result;
			}

			/*
			 * If there is a focus request in an ajax response try to honour it.
			 */
			function ajaxSubscriber(element, action, triggerId) {
				var targetId = focusId || triggerId;
				if (targetId) {
					instance.requestFocus(targetId, null, true);
				}
			}

			/**
			 * Allows a focus request to be made from any other class or component. This focus request will be
			 * honoured iff the current active element is null or document.body.
			 * @function module:wc/ui/onloadFocusControl.requestFocus
			 * @public
			 * @param {String} targetId the id of the element to focus
			 * @param {int} [timeout] A timeout for the focus call. Explicit 0 is acceptable. If not set (falsey other
			 *    than explicit 0) then {@link module:wc/ui/onloadFocusControl~doRequestFocus} is called immediately
			 *    which may have implications so think carefuly.
			 * @param {boolean} [ignoreMessages] if `true` then allow focus request even if there are message boxes in the view
			 */
			this.requestFocus = function(targetId, timeout, ignoreMessages) {
				focusId = null;
				if (timeout || timeout === 0) {
					timers.setTimeout(doRequestFocus, timeout, targetId, ignoreMessages);
				} else {
					doRequestFocus(targetId, ignoreMessages);
				}
			};

			/**
			 * Set the element id to be focussed once the page has finished doing its business.
			 * @function module:wc/ui/onloadFocusControl.register
			 * @public
			 * @param {String} id THe id of the component to focus.
			 */
			this.register = function(id) {
				if (id && !focusId) {
					focusId = id;
					initialise.addCallback(function () {
						timers.setTimeout(function() {
							try {
								if (!focusId) {
									return;
								}
								instance.requestFocus(focusId);
							} finally {
								focusId = null;
							}
						}, 0);
					});
				}
			};

			/**
			 * Late initialisation to wire up the ajax subscriber to set focus on ajax response.
			 * @function module:wc/ui/onloadFocusControl.postInit
			 * @public
			 */
			this.postInit = function() {
				processResponse.subscribe(ajaxSubscriber, true);
			};
		}

			/**
		 * Attempts to focus a given element based on an ID passed in from XSLT.
		 *
		 * NOTE there is a separate issue also being handled in this module:
		 *
		 * * IE will "remember" focus when you refresh a page.  That means it is possible for a page to load and for a field to have focus but never
		 *   have fired a focus event.  In this scenario there will probably be a whole lot of bootstrapping that should have been fired but wasn't.
		 *   Have not observed the same behaviour in FF3.6 or Chrome 6.
		 * * We used to solve this by refocusing the activeElement if there is one, however this stopped working in IE8, I guess MS worked out that
		 *   setting focus to an element that already has focus is a noop.
		 * * This solution was not ideal as it effectively adds all the bootstrapping overhead to the page load.
		 * * Now what we do is shift the focus to the BODY if any type of interactive element has focus on page load (to which we did not set focus).
		 *   This should make IE behave more like other browsers. Yes there is still some bootstrapping overhead but only: in IE, when page refreshed,
		 *    when interactive control focused AND nothing will actually want to bootstrap the body itself, so should be fast.
		 *
		 * @todo Integrate this with autofocus attribute (note: autofocus does not fire focus events yet - at least not in FF).
		 *
		 *
		 * @module
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/timers
		 *
		 */
		var instance = new OnloadFocusControl();
		initialise.register(instance);
		return instance;
	});
