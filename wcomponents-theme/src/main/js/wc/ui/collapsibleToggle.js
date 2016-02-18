/**
* Provides functionality to toggle the expanded state of a group of WCollapsibles.
*
* @module
* @requires module:wc/array/toArray
* @requires module:wc/dom/event
* @requires module:wc/dom/focus
* @requires module:wc/dom/initialise
* @requires module:wc/dom/Widget
* @requires module:wc/dom/shed
* @requires module:wc/timers
* @requires module:wc/ui/radioAnalog
*/
define(["wc/array/toArray", "wc/dom/event", "wc/dom/focus", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed", "wc/timers", "wc/ui/radioAnalog"],
	/** @param toArray wc/array/toArray @param event wc/dom/event @param focus wc/dom/focus @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param timers wc/timers @ignore */
	function(toArray, event, focus, initialise, Widget, shed, timers) {
		"use strict";
		/*
		 * IMPLICIT dependencies:
		 *     wc/ui/radioAnalog
		 * NOTE:
		 * You may be tempted to look at table rowExpansion expand/collapse all
		 * and think "this is the same as collapsibleToggle", and you would be
		 * right. But it isn't, and you aren't.
		 */

		/**
		 * @constructor
		 * @alias module:wc/ui/collapsibleToggle~CollapsibleToggle
		 * @private
		 */
		function CollapsibleToggle() {
			var EXPAND_COLLAPSE_ALL = new Widget("button", "collpasibletoggle"),
				COLLAPSIBLE = new Widget("details"),
				COLLAPSIBLE_TRIGGER,
				EXPAND = "expand";

			/**
			 * Are all collapsibles in a group in a particular expanded or collapsed state?
			 *
			 * @function
			 * @private
			 * @param {Element} controller The WCollapsibleToggle control.
			 * @param {String} action A {@link module:wc/dom/shed} action expand or collapse.
			 */
			function areAllInState(controller, action) {
				var result = false,
					controlled = controller.getAttribute("aria-controls"),
					test = (action === shed.actions.EXPAND),
					candidates, i;

				if (controlled) {
					controlled = controlled.split(" ");
					candidates = controlled.map(function(next) {
						return document.getElementById(next);
					});
				}
				else {
					candidates = COLLAPSIBLE.findDescendants(document.body);
				}

				if (candidates && candidates.length) {
					result = true;
					for (i = 0; i < candidates.length; ++i) {
						if (shed.isExpanded(candidates[i]) !== test) {
							result = false;
							break;
						}
					}
				}
				return result;
			}

			/**
			 * Helper to toggle the state of a collapsible.
			 *
			 * @function
			 * @private
			 * @param {Element} collapsible A collapsible.
			 * @param {boolean} [open] true means open the collapsible/expand the row.
			 */
			function toggleThisCollapsible(collapsible, open) {
				var collapser, OPEN = "open";

				if (COLLAPSIBLE.isOneOfMe(collapsible)) {
					if (!COLLAPSIBLE_TRIGGER) {
						COLLAPSIBLE_TRIGGER = new Widget("${wc.dom.html5.element.summary}");
						COLLAPSIBLE_TRIGGER.descendFrom(COLLAPSIBLE, true);
					}
					if (open !== collapsible.hasAttribute(OPEN) && (collapser = COLLAPSIBLE_TRIGGER.findDescendant(collapsible))) {
						event.fire(collapser, event.TYPE.click);
					}
				}
			}

			/**
			 * Toggle a group of collapsible sections.
			 *
			 * @function
			 * @private
			 * @param {Element} element The toggler.
			 */
			function toggleGroup(element) {
				var open = (element.getAttribute("data-wc-value") === EXPAND) ? true : false,
					target = element.getAttribute("aria-controls"),
					collapsibles,
					next, i, len,
					idArray, SPACE = /\s/;

				if (!target) {
					// WCollapsibleToggle with no CollapsibleGroup: toggle EVERY WCollapsible on the screen!!
					if ((collapsibles = COLLAPSIBLE.findDescendants(document.body)) && collapsibles.length) {
						if (!open) {
							collapsibles = toArray(collapsibles);
							collapsibles.reverse();  // this just ensures nested collapsibles are closed before their ancestor
						}
						for (i = 0, len = collapsibles.length; i < len; ++i) {
							next = collapsibles[i];
							toggleThisCollapsible(next, open);
						}
					}
				}
				else {
					idArray = target.split(SPACE);
					if (!open) {
						idArray.reverse();  // this just ensures nested collapsibles are closed before their ancestor
					}
					idArray.forEach(function(nextId) {
						var coll = document.getElementById(nextId);
						if (coll) {
							toggleThisCollapsible(coll, open);
						}
					});
				}
				// webkit focus fix may remove focus to the collapsible, put it back
				timers.setTimeout(focus.setFocusRequest, 0, element);
			}

			/**
			 * Listen for select and act on any controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being selected.
			 */
			function shedObserver(element) {
				if (element && EXPAND_COLLAPSE_ALL.isOneOfMe(element)) {
					toggleGroup(element);
				}
			}

			/**
			 * Get all WCollapsibleToggles which control a particular WCollapsible.
			 *
			 * @function
			 * @private
			 * @param {String} id The id of a WCollapsible.
			 * @returns {Element[]} An array containing all of the controllers for the state of the particular WCollapsible.
			 */
			function getControllers(id) {
				return (toArray(EXPAND_COLLAPSE_ALL.findDescendants(document.body))).filter(function (nxt) {
					var ctrl = nxt.getAttribute("aria-controls");
					return !ctrl || ctrl.indexOf(id) > -1;
				});
			}

			/**
			 * Listen for expand/collapse and act on any controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being expanded/collapsed.
			 * @param {String} action The {@link module:wc/dom/shed} action.
			 */
			function collapsibleObserver(element, action) {
				if (element && COLLAPSIBLE.isOneOfMe(element)) {
					getControllers(element.id).forEach(function(next) {
						var testVal = action === shed.actions.EXPAND ? "collapse" : EXPAND;
						if (next.getAttribute("data-wc-value") === testVal) {
							shed.deselect(next, true);  // no need to publish
						}
						else if (areAllInState(next, action)) {
							shed.select(next, true);  // no need to publish
						}
					});
				}
			}

			/**
			 * Must be called during the postInit phase to ensure bootstrapping is performed.
			 * @function module:wc/ui/collapsibleToggle.postInit
			 * @public
			 */
			this.postInit = function() {
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.EXPAND, collapsibleObserver);
				shed.subscribe(shed.actions.COLLAPSE, collapsibleObserver);
			};
		}

		var /** @alias module:wc/ui/collapsibleToggle */ instance = new CollapsibleToggle();
		initialise.register(instance);
		return instance;
	});
