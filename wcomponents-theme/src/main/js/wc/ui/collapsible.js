/**
 * Module to control collapsible sections. These use a DETAILS element which has some functionality in some modern
 * browsers. Eventually this module should become redundant.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/has
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/shed
 * @requires module:wc/timers
 * @requires module:wc/dom/isEventInLabel
 * @requires module:wc/dom/isAcceptableTarget
 * @requires module:wc/dom/role
 */
define(["wc/dom/event",
		"wc/dom/attribute",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/has",
		"wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/shed",
		"wc/timers",
		"wc/dom/isEventInLabel",
		"wc/dom/isAcceptableTarget",
		"wc/dom/role"],
	/** @param event wc/dom/event @param attribute wc/dom/attribute @param focus wc/dom/focus @param formUpdateManager wc/dom/formUpdateManager @param has wc/has @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param timers wc/timers @param isEventInLabel wc/dom/isEventInLabel @param isAcceptableEventTarget wc/dom/isAcceptableTarget @param $role wc/dom/role @ignore */
	function(event, attribute, focus, formUpdateManager, has, initialise, Widget, shed, timers, isEventInLabel, isAcceptableEventTarget, $role) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/collapsible~Collapsible
		 * @private
		 */
		function Collapsible() {
			var COLLAPSIBLE_CONTAINER = new Widget("${wc.dom.html5.element.details}"),
				COLLAPSIBLE_HEADER = new Widget("${wc.dom.html5.element.summary}"),
				BOOTSTRAPPED = "wc.ui.collapsible.bootStrapped",
				TRUE = "true",
				FALSE = "false";

			COLLAPSIBLE_HEADER.descendFrom(COLLAPSIBLE_CONTAINER, true);

			/**
			 * Toggles the expanded/collapsed state of a collapsible. If native then publishes shed.actions.EXPAND or
			 * shed.actions.COLLAPSE as required to ensure AJAX is invoked.
			 *
			 * @todo Use a state change to publish rather than having to wire up click etc here.
			 *
			 * @function
			 * @private
			 * @param {Element} element A collapsible trigger element.
			 */
			function toggle(element) {
				var container = COLLAPSIBLE_CONTAINER.findAncestor(element),
					isOpen;
				/*
				 * Do this in a timeout to allow us to use the details element and cater for browsers with no native
				 * support and browsers with native support which is not completely accessible (eg mouse only or no
				 * inbuilt arrow key triggering).
				 */
				function checkDoToggle() {
					var isStillOpen = shed.isExpanded(container);

					if (isOpen) {
						if (isStillOpen) {
							shed.collapse(container);
						}
						else {
							shed.publish(container, shed.actions.COLLAPSE);
						}
					}
					else if (!isStillOpen) {
						shed.expand(container);
					}
					else {
						shed.publish(container, shed.actions.EXPAND);
					}

					if (repainter) {
						repainter.checkRepaint(element);
					}
				}
				if (container) {
					isOpen = shed.isExpanded(container);
					timers.setTimeout(checkDoToggle, 0);
				}
			}

			/**
			 * Helper function for key and click initiated collapse toggling. Used to determine if the event is
			 * expected to change the state of the WCollapsible or some other nested interactive control. If we are able
			 * to act on element, or find a different focusable element before we get to element then return that
			 * element so we can use it to work out if we have to prevent default on SPACEBAR
			 * @function
			 * @private
			 * @param {Event} $event The event which initiated the toggle.
			 * @param {Element} element A collapsible header. Element must already have been determined to be a
			 *    COLLAPSIBLE_HEADER and since we have already extracted this from $event we may as well pass it in as
			 *    an arg rather than re-testing.
			 * @returns {?Element} The first interactive ancestor element of the event target if any. This may or may
			 *    not be the collapsible header.
			 */
			function toggleEventHelper($event, element) {
				var target = $event.target,
					focusableAncestor,
					result;
				if (!isEventInLabel(target)) {
					// do not toggle collapsible if the event is supposed to be for a label
					if (isAcceptableEventTarget(element, target)) {
						toggle(element);
						result = element;
					}
					else if ((focusableAncestor = focus.getFocusableAncestor(target))) {
						if (focusableAncestor !== target) {
							$event.preventDefault();
							timers.setTimeout(event.fire, 0, focusableAncestor, event.TYPE.click);
						}
						else {
							result = focusableAncestor;
						}
					}
				}
				return result;
			}

			/**
			 * Focus listener: if a header/trigger element is focused then wire it up. If the browser does not have
			 * native DETAILS support then attach ARIA role and state to expose a11y helpers. NOTE: it is very important
			 * that these DO NOT affect the function of the control or get attached in browsers which do have native
			 * support as to do so will adversely affect a11y: DETAILS/SUMMARY should have native language semantics.
			 *
			 * @function
			 * @private
			 * @param {Event} $event A focus/focusin event.
			 */
			function focusEvent($event) {
				var element = $event.target,
					details;
				if (!$event.defaultPrevented && (COLLAPSIBLE_HEADER.isOneOfMe(element)) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.keydown, keydownEvent);
					if (!has("element-details") && (details = element.parentNode)) {
						element.setAttribute("role", "button");
						element.setAttribute("aria-controls", details.id);
						element.setAttribute("aria-expanded", shed.isExpanded(details) ? TRUE : FALSE);
					}
				}
			}

			/**
			 * Click event handler to toggle the state of a collapsible if the SUMMARY is clicked. NOTE: this is
			 * required when native support is available as our toggle method is also responsible for triggering AJAX on
			 * open.
			 *
			 * @function
			 * @private
			 * @param {Event} $event A click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = COLLAPSIBLE_HEADER.findAncestor($event.target))) {
					toggleEventHelper($event, element);
				}
			}

			/**
			 * Operate collapsibles via the keyboard.
			 * @function
			 * @private
			 * @param {Event} $event A keydown event.
			 */
			function keydownEvent($event) {
				var element = $event.target,
					keyCode = $event.keyCode,
					trigger,
					foundFocusableElement;
				if ($event.defaultPrevented || $event.altKey || $event.ctrlKey) {
					return;
				}

				if ((keyCode === KeyEvent["DOM_VK_SPACE"] || keyCode === KeyEvent["DOM_VK_RETURN"]) && (trigger = COLLAPSIBLE_HEADER.findAncestor(element)) && !shed.isDisabled(trigger)) {
					foundFocusableElement = toggleEventHelper($event, trigger);
					if (element === trigger || !foundFocusableElement) {
						$event.preventDefault();
					}
				}
			}

			/**
			 * Listen for shed.actions. When DISABLE  re-enable the summary element (the content will get the disable call
			 * from its ancestor if a WCollapsible is "disabled" by subordinate). The summary is not "disableable"
			 * (WCollapsible does not implement disableable) but the SUMMARY element is exposed as a button for
			 * non-HTML5 aware AT so get disabled due to its role of "button". We do not republish the enable action
			 * though as we do not need to apply the same rule to nested disableable components.
			 *
			 * When the action is "wxpand" or "collapse" and the SUMMARY has a role then update the aria-expanded
			 * attribute.
			 *
			 * @function
			 * @private
			 * @param {Element} element the element being selected.
			 * @param {String} action The shed action being pne of "disable", "expand" or "collapse".
			 */
			function shedSubscriber(element, action) {
				var header;
				if (element && COLLAPSIBLE_CONTAINER.isOneOfMe(element) && (header = COLLAPSIBLE_HEADER.findDescendant(element))) {
					if (action === shed.actions.DISABLE) {
						if (shed.isDisabled(header)) {
							shed.enable(header, true);
						}
					}
					else if ($role.get(header) === "button") {
						header.setAttribute("aria-expanded", action === shed.actions.EXPAND ? TRUE : FALSE);
					}
				}
			}

			/**
			 * Write the state of collapsible sections.
			 * @function
			 * @private
			 * @param {Element} container The container of the components whose state we are writing.
			 * @param {Element} stateContainer Where to put the state fields.
			 */
			function writeState(container, stateContainer) {
				function writeStateCollapsible($element) {
					var val = shed.isExpanded($element) ? "open" : "closed";
					formUpdateManager.writeStateField(stateContainer, $element.id, val);
				}
				Array.prototype.forEach.call(COLLAPSIBLE_CONTAINER.findDescendants(container), writeStateCollapsible);
				if (COLLAPSIBLE_CONTAINER.isOneOfMe(container)) {
					writeStateCollapsible(container);
				}
			}

			/**
			 * Indicates if a given element is a collapsible.
			 *
			 * @function module:wc/ui/collapsible.isOneOfMe
			 * @param {Element} element The element to test.
			 * @param {Boolean} [onlyContainer] If true then we only want to know if the element is a collapsible
			 *    container element; if explicitly false if it is the header/trigger element and if undefined whether
			 *    it is either of these.
			 * @returns {Boolean} true if element matches the required type.
			 */
			this.isOneOfMe = function(element, onlyContainer) {
				var result;
				if (onlyContainer) {
					result = COLLAPSIBLE_CONTAINER.isOneOfMe(element);
				}
				else if (onlyContainer === false) {
					result = COLLAPSIBLE_HEADER.isOneOfMe(element);
				}
				else {
					result = Widget.isOneOfMe(element, [COLLAPSIBLE_HEADER, COLLAPSIBLE_CONTAINER]);
				}
				return result;
			};

			/**
			 * Get the trigger element from a container element.
			 *
			 * @function module:wc/ui/collapsible.getActionElement
			 * @param {Element} element The start element.
			 * @returns {?Element} If the start element is a collapsible container return its header/trigger element.
			 */
			this.getActionElement = function(element) {
				var result;
				if (COLLAPSIBLE_CONTAINER.isOneOfMe(element)) {
					result = COLLAPSIBLE_HEADER.findDescendant(element);
				}
				return result;
			};

			/**
			 * Initialisation: wire up focus and click listeners on the BODY.
			 *
			 * @function module:wc/ui/collapsible.initialise
			 * @param {Element} element document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, null, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
				event.add(element, event.TYPE.click, clickEvent);
			};

			/**
			 * Late initialiser callback to set up the shed subscribers. This is required because we have to give the
			 * SUMMARY element a role of button to expose its behaviour to non-HTML5 aware AT but this role makes the
			 * collapsible disable-able which is not a facet of a collapsible we want to encourage.
			 *
			 * @function module:wc/ui/collapsible.postInit
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.DISABLE, shedSubscriber);
				shed.subscribe(shed.actions.EXPAND, shedSubscriber);
				shed.subscribe(shed.actions.COLLAPSE, shedSubscriber);
				formUpdateManager.subscribe(writeState);
			};

			/**
			 * Public for testing.
			 * @ignore
			 */
			this._keydownEvent = keydownEvent;
		}

		var repainter = null,
			/** @alias module:wc/ui/collapsible */ instance = new Collapsible();

		if (has("ie") === 8) {
			require(["wc/fix/inlineBlock_ie8"], function(inlineBlock) {
				repainter = inlineBlock;
			});
		}
		initialise.register(instance);
		return instance;
	});
