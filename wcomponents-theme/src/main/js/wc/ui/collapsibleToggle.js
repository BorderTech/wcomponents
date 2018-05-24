define(["wc/array/toArray",
	"wc/dom/event",
	"wc/dom/focus",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/shed",
	"wc/timers",
	"wc/ui/tabset",
	"wc/ui/ajax/processResponse",
	"wc/ui/radioAnalog"],
	function(toArray, event, focus, initialise, Widget, shed, timers, tabset, processResponse) {
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
			var CONTAINER = new Widget("", "wc-collapsibletoggle"),
				DETAILS = "details",
				EXPAND_COLLAPSE_ALL = new Widget("button", "wc_collapsibletoggle"),
				COLLAPSIBLE = new Widget(DETAILS),
				TABSET = new Widget("","wc-tabset-type-accordion"),
				COLLAPSIBLE_TRIGGER,
				EXPAND = "expand",
				CONTROLS = "aria-controls";

			function getControlled(trigger) {
				var actualTrigger = CONTAINER.isOneOfMe(trigger) ? EXPAND_COLLAPSE_ALL.findDescendant(trigger) : trigger,
					idList = actualTrigger.getAttribute(CONTROLS);
				if (!idList) {
					return toArray(COLLAPSIBLE.findDescendants(document.body));
				}
				return idList.split(/\s+/).map(function (next) {
					return document.getElementById(next);
				});
			}

			/**
			 * Are all collapsibles in a group in a particular expanded or collapsed state?
			 *
			 * @function
			 * @private
			 * @param {Element} controller The WCollapsibleToggle control.
			 * @param {Boolean} [expanded] truethy if we are checking if all expanded, otherwise falsey
			 */
			function areAllInExpandedState(controller, expanded) {
				var result = false,
					candidates = getControlled(controller),
					test = !!expanded,
					i,
					next;

				if (candidates && candidates.length) {
					result = true;
					for (i = 0; i < candidates.length; ++i) {
						next = candidates[i];
						if (!next) {
							continue;
						}
						if (TABSET.isOneOfMe(next)) {
							result = tabset.areAllInExpandedState(next, test);
							if (!result) {
								return false;
							}
						} else if (shed.isExpanded(next) !== test) {
							return false;
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
				var collapser;

				if (COLLAPSIBLE.isOneOfMe(collapsible)) {
					if (!COLLAPSIBLE_TRIGGER) {
						COLLAPSIBLE_TRIGGER = new Widget("summary");
						COLLAPSIBLE_TRIGGER.descendFrom(COLLAPSIBLE, true);
					}
					if (open !== collapsible.hasAttribute("open") && (collapser = COLLAPSIBLE_TRIGGER.findDescendant(collapsible))) {
						event.fire(collapser, event.TYPE.click);
					}
				} else if (TABSET.isOneOfMe(collapsible) && !shed.isDisabled(collapsible)) {
					if (open) {
						tabset.expandAll(collapsible);
					} else {
						tabset.collapseAll(collapsible);
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
					collapsibles;

				collapsibles = getControlled(element);

				if (collapsibles) {
					collapsibles.forEach(function(next) {
						if (next) {
							toggleThisCollapsible(next, open);
						}
					});
				}
				// webkit focus fix may remove focus to the collapsible, put it back
				timers.setTimeout(focus.setFocusRequest, 0, element);
			}

			/**
			 * Get all WCollapsibleToggles which control a particular collapsible.
			 *
			 * @function
			 * @private
			 * @param {Element} element the element being controlled
			 * @returns { Element[]} An array containing all of the controllers for the collapsible.
			 */
			function getControllers(element) {
				var el = (COLLAPSIBLE.isOneOfMe(element) ? element : TABSET.findAncestor(element)),
					controllerWidget,
					candidates;

				if (!(el && el.id)) {
					return null;
				}

				controllerWidget = EXPAND_COLLAPSE_ALL.extend("", {"aria-controls":  el.id});
				candidates = controllerWidget.findDescendants(document.body);

				if (!(candidates && candidates.length)) {
					return null;
				}

				return (toArray(candidates));
			}

			function setControllerState(controller) {
				var testVal = controller.getAttribute("data-wc-value");

				if (areAllInExpandedState(controller, testVal === "expand")) {
					shed.select(controller, true); // no need to publish
				} else {
					shed.deselect(controller, true); // no need to publish
				}
			}

			/**
			 * Listen for expand/collapse and act on any controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being expanded/collapsed.
			 */
			function collapsibleObserver(element) {
				var controllers;

				if (!element || (!((controllers = getControllers(element)) && controllers.length))) {
					return;
				}

				controllers.forEach(setControllerState);
			}

			/**
			 * Set the aria-controls attribute on the buttons of a collapsibleToggle.
			 *
			 * @function
			 * @private
			 * @param {Element} element a collapsible toggle wrapper
			 * @returns {undefined}
			 */
			function setControlList(element) {
				var groupName = element.getAttribute("data-wc-group"),
					targetWidgets,
					targets,
					idArray = [],
					buttons,
					ids;

				targetWidgets = [COLLAPSIBLE.extend("", {"data-wc-group": groupName}), TABSET.extend("", {"data-wc-group": groupName})];
				targets = Widget.findDescendants(document.body, targetWidgets);
				if (!(targets && targets.length)) {
					targets = COLLAPSIBLE.findDescendants(document.body);
				}

				Array.prototype.forEach.call(targets, function (next) {
					idArray.push(next.id);
				});

				if (idArray.length) {
					buttons = EXPAND_COLLAPSE_ALL.findDescendants(element);
					ids = idArray.join(" ");
					Array.prototype.forEach.call(buttons, function (next) {
						next.setAttribute(CONTROLS, ids);
					});
				}
			}

			/**
			 * Set aria-controls for each collapsible toggle.
			 *
			 * @function
			 * @private
			 */
			function setControls() {
				Array.prototype.forEach.call(CONTAINER.findDescendants(document.body), setControlList);
			}


			/**
			 * Listen for select and act on any controller.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being selected.
			 */
			function shedObserver(element) {
				var wrapper;
				if (element && EXPAND_COLLAPSE_ALL.isOneOfMe(element)) {
					toggleGroup(element);
					// just in case we were not able to toggle any controlled components.
					wrapper = CONTAINER.findAncestor(element);
					Array.prototype.forEach.call(EXPAND_COLLAPSE_ALL.findDescendants(wrapper), setControllerState);
				}
			}

			/**
			 * Must be called during the postInit phase to ensure bootstrapping is performed.
			 * @function module:wc/ui/collapsibleToggle.postInit
			 * @public
			 */
			this.postInit = function() {
				setControls();
				shed.subscribe(shed.actions.SELECT, shedObserver);
				shed.subscribe(shed.actions.EXPAND, collapsibleObserver);
				shed.subscribe(shed.actions.COLLAPSE, collapsibleObserver);
				processResponse.subscribe(setControls, true);
			};
		}

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
		 * @requires module:wc/ui/tabse
		 * @requires module:wc/ui/ajax/processResponse
		 */
		var instance = new CollapsibleToggle();
		initialise.register(instance);
		return instance;
	});
