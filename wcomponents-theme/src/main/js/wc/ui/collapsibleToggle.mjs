/*
 * IMPLICIT dependencies:
 *     wc/ui/radioAnalog
 * NOTE:
 * You may be tempted to look at table rowExpansion expand/collapse all
 * and think "this is the same as collapsibleToggle", and you would be
 * right. But it isn't, and you aren't.
 */
import toArray from "wc/array/toArray";
import event from "wc/dom/event";
import focus from "wc/dom/focus";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import timers from "wc/timers";
import tabset from "wc/ui/tabset";
import processResponse from "wc/ui/ajax/processResponse";
// import radioAnalog from "wc/ui/radioAnalog";  // hmmmm

/**
 * Provides functionality to toggle the expanded state of a group of WCollapsibles.
 * @constructor
 * @private
 */
function CollapsibleToggle() {
	const containerSelector = ".wc-collapsibletoggle",
		expandCollapseAllSelector = "button.wc_collapsibletoggle",
		collapsibleSelector = "details",
		tabsetSelector = ".wc-tabset-type-accordion",
		triggerSelector = `${collapsibleSelector} > summary`,
		EXPAND = "expand",
		CONTROLS = "aria-controls";

	function getControlled(trigger) {
		const actualTrigger = trigger.matches(containerSelector) ? trigger.querySelector(expandCollapseAllSelector) : trigger,
			idList = actualTrigger.getAttribute(CONTROLS);
		if (!idList) {
			return toArray(document.body.querySelectorAll(collapsibleSelector));
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
	 * @param {HTMLElement} controller The WCollapsibleToggle control.
	 * @param {Boolean} [expanded] truethy if we are checking if all expanded, otherwise falsey
	 */
	function areAllInExpandedState(controller, expanded) {
		let result = false;
		const candidates = getControlled(controller),
			test = !!expanded;

		if (candidates && candidates.length) {
			result = true;
			for (const element of candidates) {
				let next = element;
				if (!next) {
					continue;
				}
				if (next.matches(tabsetSelector)) {
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
	 * @param {HTMLElement} collapsible A collapsible.
	 * @param {boolean} [open] true means open the collapsible/expand the row.
	 */
	function toggleThisCollapsible(collapsible, open) {

		if (collapsible.matches(collapsibleSelector)) {
			let collapser;
			if (open !== collapsible.hasAttribute("open") && (collapser = collapsible.querySelector(triggerSelector))) {
				event.fire(collapser, "click");
			}
		} else if (collapsible.matches(tabsetSelector) && !shed.isDisabled(collapsible)) {
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
	 * @param {HTMLElement} element The toggler.
	 */
	function toggleGroup(element) {
		const open = element.getAttribute("data-wc-value") === EXPAND;
		const collapsibles = getControlled(element);

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
	 * @param {HTMLElement} element the element being controlled
	 * @returns { Element[]} An array containing all of the controllers for the collapsible.
	 */
	function getControllers(element) {
		const el = (element.matches(collapsibleSelector) ? element : element.closest(tabsetSelector));

		if (!(el && el.id)) {
			return null;
		}

		const controllerSelector = `${expandCollapseAllSelector}[aria-controls='${el.id}']`;
		const candidates = document.body.querySelectorAll(controllerSelector);

		if (!(candidates && candidates.length)) {
			return null;
		}

		return (toArray(candidates));
	}

	function setControllerState(controller) {
		const testVal = controller.getAttribute("data-wc-value");

		if (areAllInExpandedState(controller, testVal === "expand")) {
			shed.select(controller, true);  // no need to publish
		} else {
			shed.deselect(controller, true);  // no need to publish
		}
	}

	/**
	 * Listen for expand/collapse and act on any controller.
	 *
	 * @function
	 * @private
	 * @param {HTMLElement} element The element being expanded/collapsed.
	 */
	function collapsibleObserver(element) {
		let controllers;

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
	 * @param {HTMLElement} element a collapsible toggle wrapper
	 * @returns {undefined}
	 */
	function setControlList(element) {
		const groupName = element.getAttribute("data-wc-group"),
			idArray = [];
		const groupSelector = `[data-wc-group='${groupName}']`;
		const targetSelectors = [`${collapsibleSelector}${groupSelector}`, `${tabsetSelector}${groupSelector}`];
		let targets = document.body.querySelectorAll(targetSelectors.join());

		if (!(targets && targets.length)) {
			targets = document.body.querySelectorAll(collapsibleSelector);
		}

		Array.prototype.forEach.call(targets, function (next) {
			idArray.push(next.id);
		});

		if (idArray.length) {
			const buttons = element.querySelectorAll(expandCollapseAllSelector);
			const ids = idArray.join(" ");
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
		Array.prototype.forEach.call(document.body.querySelectorAll(containerSelector), setControlList);
	}


	/**
	 * Listen for select and act on any controller.
	 *
	 * @function
	 * @private
	 * @param {HTMLElement} element The element being selected.
	 */
	function shedObserver(element) {
		if (element && element.matches(expandCollapseAllSelector)) {
			toggleGroup(element);
			// just in case we were not able to toggle any controlled components.
			const wrapper = element.closest(containerSelector);
			Array.prototype.forEach.call(wrapper.querySelectorAll(expandCollapseAllSelector), setControllerState);
		}
	}

	/**
	 * Must be called during the postInit phase to ensure bootstrapping is performed.
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


export default initialise.register(new CollapsibleToggle());
