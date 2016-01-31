/**
 * Attempts to focus a given element based on an ID passed in from XSLT.
 *
 * <p>NOTE there is a separate issue also being handled in this module:wc/</p><ul>
 * <li>IE will "remember" focus when you refresh a page.  That means it is possible for a page to load and for a field
 * to have focus but never have fired a focus event.  In this scenario there will probably be a whole lot of
 * bootstrapping that should have been fired but wasn't.  Have not observed the same behaviour in FF3.6 or Chrome 6.</li>
 * <li>We used to solve this by refocusing the activeElement if there is one, however this stopped working in IE8, I
 * guess MS worked out that setting focus to an element that already has focus is a noop.</li>
 * <li>This solution was not ideal as it effectively adds all the bootstrapping overhead to the page load.</li>
 * <li>Now what we do is shift the focus to the BODY if any type of interactive element has focus on page load (to which
 * we did not set focus). This should make IE behave more like other browsers. Yes there is still some bootstrapping
 * overhead but only: in IE, when page refreshed, when interactive control focused AND nothing will actually want to
 * bootstrap the body itself, so should be fast.</li></ul>
 *
 * @todo Integrate this with autofocus attribute (note: autofocus does not fire focus events yet).
 * @todo document private members, check source order.
 *
 * @typedef {object} module:wc/ui/onloadFocusControl.config() Optional module configuration
 * @property {boolean} rescroll If the document must scroll to bring the focussed element into the viewport this
 * property determines whether the focussed element is scrolled to teh top (or closest to) of teh viewport (true) or
 * uses the user agent default - usually to scroll only far enough to bring the element into the viewport.
 * @default false
 *
 * @module
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/initialise
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/timers
 *
 * @todo Document private members, check source order.
 */
define(["wc/dom/focus", "wc/dom/initialise", "wc/ui/ajax/processResponse", "wc/timers", "module"],
	/** @param focus wc/dom/focus @param initialise wc/dom/initialise @param processResponse wc/ui/ajax/processResponse @param timers wc/timers @param module @ignore */
	function(focus, initialise, processResponse, timers, module) {
		"use strict";
		/** @alias module:wc/ui/onloadFocusControl */
		function OnloadFocusControl() {
			var focusId,
				conf = module.config(),
				SCROLL_TO_TOP = (conf ? conf.rescroll : false),  // true to turn on scroll to top of viewport on load focus, false will apply user agent default (usually scroll to just in view)
				FOCUS_DELAY = null;  // if set to a non-negstive integer this will delay focus requests to allow native autofocus to work. Native autofocus is currently problematic since it does not fire a focus event.


			function processNow() {
				if (focusId) {
					instance.requestFocus(focusId);
					focusId = null;
				}
			}

			/**
			 * After focusing the element (or its first focusable child) scroll the element to
			 * the top of the viewport if required
			 * @function
			 * @private
			 * @param {Element} focusElement The element being focused.
			 */
			function focusCallback(focusElement) {
				if (SCROLL_TO_TOP) {
					focusElement.scrollIntoView();
				}
			}


			/**
			 * Makes the attempt to focus an element
			 * @function
			 * @private
			 * @param {String} targetId The id of the element to focus (or focus in).
			 */
			function doRequestFocus(targetId) {
				var element;
				if ((element = document.getElementById(targetId)) && canPolitelyChangeFocus()) {
					if (focus.canFocus(element)) {
						focus.setFocusRequest(element, focusCallback);
					}
					else if (focus.canFocusInside(element)) { // try focusing inside the target
						focus.focusFirstTabstop(element, focusCallback);
					}
					// as a last resort try focusing the nearest focusable ancestor of element if element has no dimensions
					else if (element.clientHeight === 0 && element.clientWidth === 0 && (element = focus.getFocusableAncestor(element))) {
						focus.setFocusRequest(element, focusCallback);
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
			 */
			function canPolitelyChangeFocus() {
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
				return result;
			}

			/*
			 * If there is a focus request in an ajax response try to honour it.
			 */
			function ajaxSubscriber(element, action, triggerId) {
				if (focusId) {
					instance.requestFocus(focusId);
				}
				else if (triggerId) {
					instance.requestFocus(triggerId, FOCUS_DELAY);
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
			 */
			this.requestFocus = function(targetId, timeout) {
				focusId = null;
				if (timeout || timeout === 0) {
					timers.setTimeout(doRequestFocus, timeout, targetId);
				}
				else {
					doRequestFocus(targetId);
				}
			};

			/**
			 * Change the scroll to top behaviour.
			 * Probably ONLY for testing but I see no reason why it should not work...
			 * @function module:wc/ui/onloadFocusControl.setScrollToTop
			 * @public
			 * @param {Boolean} val True for scroll to top, otherwise false.
			 */
			this.setScrollToTop = function(val) {
				SCROLL_TO_TOP = val;
			};

			/**
			 * Set the element id to be focussed once the page has finished doing its business.
			 * @function module:wc/ui/onloadFocusControl.register
			 * @public
			 * @param {String} id THe id of the component to focus.
			 */
			this.register = function(id) {
				if (!focusId && id) {
					focusId = id;
					initialise.addCallback(processNow);
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
		var /** @alias module:wc/ui/onloadFocusControl */ instance = new OnloadFocusControl();
		initialise.register(instance);
		return instance;
	});
