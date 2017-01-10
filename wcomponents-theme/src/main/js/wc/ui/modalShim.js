define(["wc/dom/attribute", "wc/dom/uid", "wc/dom/classList", "wc/dom/event", "wc/dom/focus", "wc/dom/Widget", "wc/dom/shed", "wc/timers",
	"wc/Observer"],
	function(attribute, uid, classList, event, focus, Widget, shed, timers, Observer) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/modalShim~ModalShim
		 * @private
		 */
		function ModalShim() {
			var MODAL_BACKGROUND_ID = "wc-shim",
				SHIFTKEY_ON = false,
				activeElement,
				UNIT = "px",
				ACCESS_KEY_WD = new Widget("", "", {accesskey: null}),
				AKEY = "accesskey",
				accessKeyMap = {},
				HAS_EVENTS = "wc/ui/modalShim.wired",
				observer,
				ALTERNATE_OBSERVER_GROUP = "shimshow";

			/**
			 * If the user is shift-tabbing their way back through the dialog we want to wrap focus around to the last
			 * tabstop, not the first. To do this we need to track the state of the shift key because focus event does
			 * not report shift key flag.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The keydown event.
			 */
			function keyEvent($event) {
				SHIFTKEY_ON = $event.shiftKey;
			}

			/**
			 * The activeElement (or something within it) has received focus. Cancel any outstanding calls to refocus it.
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 */
			function focusEvent($event) {
				if (!$event.defaultPrevented && activeElement) {
					if (!( $event.target.compareDocumentPosition(activeElement) & Node.DOCUMENT_POSITION_CONTAINS)) {
						timers.setTimeout(focus.focusFirstTabstop, 0, activeElement, null, SHIFTKEY_ON);
					}
				}
			}
			/**
			 * Use the shim to prevent touch events by cancelling touchstart.
			 * @function
			 * @private
			 * @param {Event} $event The touch start event.
			 */
			function touchstartEvent($event) {
				if (!$event.defaultPrevented && activeElement &&
					!($event.target.compareDocumentPosition(activeElement) & Node.DOCUMENT_POSITION_CONTAINS)) {
					$event.preventDefault();
				}
			}
			/**
			 * create a new modalShim if required.
			 * @function
			 * @private
			 * @returns {Element} The shim element.
			 */
			function create() {
				var d = document,
					b = d.body,
					result = d.createElement("div");
				result.id = MODAL_BACKGROUND_ID;
				shed.hide(result, true);
				if (b.firstChild) {
					b.insertBefore(result, b.firstChild);
				}
				else {
					b.appendChild(result);
				}
				return result;
			}

			/**
			 * Attaches or detaches the events required by the modal shim.
			 * @param {Boolean} [add] true to add the events, otherwise remove them.
			 */
			function addRemoveEvents(add) {
				var action = add ? "add" : "remove",
					element = document.body;
				if (!(add && attribute.get(element, HAS_EVENTS))) {
					if (add) {
						attribute.set(element, HAS_EVENTS, true);
					}
					else {
						attribute.remove(element, HAS_EVENTS);
					}
					event[action](element, event.TYPE.keydown, keyEvent, false);
					event[action](element, event.TYPE.keyup, keyEvent, false);
					if (event.canCapture) {
						if (add) {
							event[action](element, event.TYPE.focus, focusEvent, null, null, true);
							event[action](document.body, event.TYPE.touchstart, touchstartEvent, null, null, true);
						}
						else {
							event[action](element, event.TYPE.focus, focusEvent, true);
							event[action](document.body, event.TYPE.touchstart, touchstartEvent, true);
						}
					}
					else {
						event[action](element, event.TYPE.focusin, focusEvent);
					}
				}
			}

			/**
			 * Prevents interaction with the current browser window except for the active region.
			 * @function module:wc/ui/modalShim.setModal
			 * @param {Element} [activeRegion] The region the user is allowed to interact with. Defaults to the shim
			 *    (i.e. no interaction allowed).
			 * @param {String} [className] Additional class to add to the shim.
			 */
			this.setModal = function(activeRegion, className) {
				var shimElement = document.getElementById(MODAL_BACKGROUND_ID) || create();
				shimElement.style.height = document.documentElement.scrollHeight + UNIT;
				shed.show(shimElement, true);
				activeElement = activeRegion || shimElement;
				addRemoveEvents(true);
				if (className) {
					classList.add(shimElement, className);
				}

				// remove the accesskey attribute from controls with access keys which are not in the activeRegion
				Array.prototype.forEach.call(ACCESS_KEY_WD.findDescendants(document), function(next) {
					var nextId = next.id || (next.id = uid());
					if (activeRegion && !(next.compareDocumentPosition(activeRegion) & Node.DOCUMENT_POSITION_CONTAINS)) {
						accessKeyMap[nextId] = next.getAttribute(AKEY);
						next.removeAttribute(AKEY);
					}
				});

				if (observer) {
					observer.setFilter(ALTERNATE_OBSERVER_GROUP);
					observer.notify(activeElement);
				}
			};

			/**
			 * Removes the modal shim.
			 * @function module:wc/ui/modalShim.clearModal
			 */
			this.clearModal = function() {
				var shimElement, key, aKeyElement, notify;
				try {
					shimElement = document.getElementById(MODAL_BACKGROUND_ID);
					if (shimElement && !shed.isHidden(shimElement, true)) {
						addRemoveEvents();
						shimElement.className = "";
						for (key in accessKeyMap) {
							if ((aKeyElement = document.getElementById(key))) {
								aKeyElement.setAttribute(AKEY, accessKeyMap[key]);
							}
						}
						shed.hide(shimElement, true);
						notify = true;
					}
				}
				finally {
					activeElement = null;
					accessKeyMap = {};
					if (notify && observer) {
						observer.notify();
					}
				}
			};

			/**
			 * Allow external module to subscribe to this module's Observer instance to be informed when a modal shim is removed.
			 *
			 * If subscribing to `show` then the notification will include an arg of the activeElement. This is wither the active region passed in to
			 * `showModal` or the shim element if `showModal` is called without an `activeRegion` arg.
			 *
			 * If subscribing to `clear` (the default) then the notification will have no arguments.
			 *
			 * @function module:wc/ui/modalShim.subscribe
			 * @public
			 * @param {Function} subscriber the function to subscribe
			 * @param {boolean} onshow if true notify when the modalShim is shown, otherwise notify when the shim is removed
			 * @returns {?Function} the subscribed function
			 */
			this.subscribe = function(subscriber, onshow) {
				var group = null;
				if (!observer) {
					observer = new Observer();
				}
				if (onshow) {
					group = { group: ALTERNATE_OBSERVER_GROUP };
				}
				return observer.subscribe(subscriber, group);
			};

			/**
			 * Unsubscribe from this observer instance.
			 * @function module:wc/ui/modalShim.unsubscribe
			 * @public
			 * @param {Function} subscriber the function to unsubscribe
			 * @param {boolean} onshow if true unsubscribe from the group notified when the modalShim is shown. The unsubscribe will only succeed if
			 * the group is the same as when the subscriber was subscribed.
			 * @returns {?Function} the unsubscribed function
			 */
			this.unsubscribe = function(subscriber, onshow) {
				var group;
				if (observer) {
					group = onshow ? ALTERNATE_OBSERVER_GROUP : null;
					return observer.unsubscribe(subscriber, group);
				}
				return null;
			};
		}
		/**
		 * Used to overlay the page with a shim which blocks mouse access to the components on the page and steals focus back if it loses it (e.g.
		 * due to TAB key or accesskeys).
		 * @module
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/uid
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/shed
		 * @requires module:wc/timers
		 * @requires module:wc/Observer
		 */
		return new ModalShim();
	});
