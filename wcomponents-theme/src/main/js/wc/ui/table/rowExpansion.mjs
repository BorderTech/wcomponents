/**
 * Provides controller for expanding and collapsing table rows.
 */
import event from "wc/dom/event";
import focus from "wc/dom/focus";
import formUpdateManager from "wc/dom/formUpdateManager";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import ajaxRegion from "wc/ui/ajaxRegion";
import timers from "wc/timers";
import processResponse from "wc/ui/ajax/processResponse";
import rowAnalog from "wc/ui/rowAnalog";
import common from "wc/ui/table/common";
import triggerManager from "wc/ajax/triggerManager";
import icon from "wc/ui/icon";
import uid from "wc/dom/uid";
import "wc/ui/radioAnalog";

/**
 * Find the closest ancestor-or-self match but excludes any results at or above `stopAtSelector`.
 * @param {HTMLElement} element The reference element.
 * @param {string} selector The ancestor to find.
 * @param {string} stopAtSelector Exclude results at or above this selector.
 * @return {HTMLElement|null}
 */
const closest = (element, selector, stopAtSelector) => {
	const id = element.id || (element.id = uid());
	const result = element.closest(selector);
	if (stopAtSelector && result.querySelector(`:scope ${stopAtSelector} #${id}`)) {
		return null;
	}
	return result;
};

const exp_coll_all_container = ".wc-rowexpansion";
const table_wrapper = common.WRAPPER.toString();
const row_trigger = `${common.TD.toString()}[role='button']`;
const expand_collapse_all = "button.wc_rowexpansion";
const table = `${table_wrapper} > ${common.TABLE.toString()}.wc_tbl_expansion`;
const tbody = `${table} > ${common.TBODY.toString()}`;
const tbl_expandable_row = `${tbody} > ${common.TR.toString()}[aria-expanded]`;

const CONTROLS = "aria-controls",
	BOOTSTRAPPED = "wc.ui.table.rowExpansion.bootStrapped",
	NO_AJAX = "data-wc-tablenoajax",
	MODE = "data-wc-expmode",
	VALUE = "data-wc-value",
	EXPAND = "expand",
	LAZY = "lazy",
	CLIENT = "client";

const instance = {
	/**
	 * Is a given table a treegrid? We cannot currently use the treegrid role because it causes a11y failure in common screenreader/browser
	 * combos.
	 * @param {HTMLElement} element the element to test
	 * @returns {Boolean} {@code true} if the element is a table with row expansion.
	 */
	isTreeGrid: element => element.matches(table)

};

function getControlled(trigger) {
	const actualTrigger = trigger.matches(exp_coll_all_container) ? trigger.querySelector(expand_collapse_all) : trigger,
		idList = actualTrigger.getAttribute(CONTROLS);
	if (!idList) {
		return null;
	}
	return idList.split(/\s+/).map(function (next) {
		return document.getElementById(next);
	});
}

/**
 * Get all controllers for a given row.
 *
 * @function
 * @private
 * @param {HTMLElement} element the element being controlled
 * @returns {Element[]} An array containing all of the controllers for the row
 */
function getControllers(element) {
	if (!(element && element.id)) {
		return null;
	}
	const controllerWidget = `${expand_collapse_all}[aria-controls='${element.id}']`;
	return /** @type Element[] */ Array.from(document.body.querySelectorAll(controllerWidget));
}

/**
 * Are all rows in a particular state?
 *
 * @function
 * @private
 * @param {HTMLElement} controller The WCollapsibleToggle control.
 * @param {Boolean} expanded true if we are checking if all expanded, otherwise false
 */
function areAllInExpandedState(controller, expanded) {
	const candidates = getControlled(controller);
	if (candidates && candidates.length) {
		return candidates.every(function (next) {
			return next && shed.isExpanded(next) === expanded;
		});
	}
	return false;
}

function setControllerState(controller) {
	const testVal = controller.getAttribute(VALUE);

	if (areAllInExpandedState(controller, testVal === "expand")) {
		shed.select(controller, true);  // no need to publish
	} else {
		shed.deselect(controller, true);  // no need to publish
	}
}

function getWrapper(element) {
	return element.closest(table_wrapper);
}

function getMode(row) {
	const wrapper = getWrapper(row);
	if (wrapper) {
		return wrapper.getAttribute(MODE);
	}
	return null;
}

function isAjaxExpansion(row) {
	const mode = getMode(row);
	return mode === LAZY || mode === "dynamic";
}

/**
 * Get a DTO suitable for registering an AJAX trigger for an expandable row or expand/collapse all control.
 *
 * @function
 * @private
 * @param {HTMLElement} element The triggering element.
 * @returns {Object} An object suitable to create a {@link module:wc/ajax/Trigger}.
 */
function getTriggerDTO(element) {
	return common.getAjaxDTO(element, element.getAttribute(MODE) === LAZY);
}

/**
 * Write the state of collapsible rows. As usual this function expects state fields from previous calls to
 * be cleaned up elsewhere (most commonly in {@link module:wc/dom/formUpdateManager}).
 *
 * @function
 * @private
 * @param {HTMLElement} form The form or form segment the state of which is being written.
 * @param {HTMLElement} stateContainer The element into which the sate is written.
 */
function writeState(form, stateContainer) {
	Array.from(form.querySelectorAll(table)).forEach(function(next) {
		const id = next.parentElement.id,
			rows = Array.from(next.querySelectorAll(tbl_expandable_row)).filter(function(row) {
				return shed.isExpanded(row);
			});
		rows.forEach(function(row) {
			const rowIndex = row.getAttribute("data-wc-rowindex");
			formUpdateManager.writeStateField(stateContainer, id + ".expanded", rowIndex, false, true);
		});
	});
}

/**
 * Toggles the expanded/collapsed state of a single collapsible row.
 *
 * @function
 * @private
 * @param {HTMLElement} row A collapsible row.
 * @param {String} [forceDirection] use a particular direction "true" or "false" rather than inferring from
 *    the current button state. Required when recursively closing/hiding rows.
 * @param {Boolean} [ignoreRecursion] true if we want to ignore the recursion of controllers to toggle sub
 *    rows. This is used when invoked from collapsibleToggle.
 * @param {Boolean| [ignoreAjax] used when recursing to prevent multiple ajax calls for the same table.
 * @returns {Boolean} true if successfully expanded/collapsed.
 */
function toggleRow(row, ignoreAjax) {
	if (row) {
		const show = shed.isExpanded(row) ? "false" : "true";
		if (show === "true" && !shed.isDisabled(row)) {
			if (ignoreAjax && isAjaxExpansion(row)) {
				row.setAttribute(NO_AJAX, "true");
			}
			shed.expand(row);
		} else if (show === "false") { // We need to collapse disabled rows otherwise we have nesting vestige issues
			shed.collapse(row);
		}
	}
}

/**
 * Helper to show and hide rows controlled by an expandable row.
 *
 * @function
 * @private
 * @param {HTMLElement} triggerRow The expandable row.
 * @param {String} action A {@link module:wc/dom/shed} action: one of shed.actions.EXPAND or shed.actions.COLLAPSE.
 */
function showHideContent(triggerRow, action) {
	const shedFunc = action === shed.actions.EXPAND ? "show" : "hide",
		controlled = getControlled(triggerRow);

	if (controlled) {
		controlled.forEach(function(row) {
			if (row) {
				shed[shedFunc](row);
			}
		});

		getControllers(triggerRow).forEach(setControllerState);
	}
}

/**
 * Subscriber to {@link module:wc/dom/shed} to manage showing and hiding content when a row is expanded or
 * collapsed.
 *
 * @function
 * @private
 * @param {CustomEvent} $event
 */
function expCollapseObserver({ target: element, action }) {

	if (element && element.matches(tbl_expandable_row)) {
		const control = Array.from(element.children).find(el => el.matches(row_trigger));
		if (control) {
			const add = action === shed.actions.EXPAND ? "fa-caret-down" : "fa-caret-right";
			const remove = action === shed.actions.EXPAND ? "fa-caret-right" : "fa-caret-down";
			icon.change(control, add, remove);
		}
		if (action === shed.actions.EXPAND && isAjaxExpansion(element)) {
			if (element.getAttribute(NO_AJAX) === "true") {
				element.removeAttribute(NO_AJAX);
			} else if (element.getAttribute(MODE) !== CLIENT) {
				ajaxRegion.requestLoad(element, getTriggerDTO(element));
			}
			if (getMode(element) === LAZY) {
				element.setAttribute(MODE, CLIENT);
			}
		}
		showHideContent(element, action);
	}
}

/**
 * Subscriber to {@link module:wc/dom/shed} to manage collapsing an expandable row if it is hidden. This
 * allows us to manage multiply nested ex[andables and hiding rows using (for example) client pagination.
 *
 * @function
 * @private
 * @param {CustomEvent} $event
 */
function closeOnHide({ target }) {
	if (target && target.matches(tbl_expandable_row) && shed.isExpanded(target)) {
		toggleRow(target, true);
	}
}

/**
 * Reset focus to a row expander after dynamic/lazy expansion.
 *
 * @function
 * @private
 */
function ajaxSubscriber(/* element, action, triggerId */) {
	setControls();
}

/**
 * Keydown event listener to operate collapsibles via the keyboard.
 * @function
 * @private
 * @param {KeyboardEvent} $event The keydown event.
 */
function keydownEvent($event) {
	if (event.defaultPrevented || $event.altKey || $event.ctrlKey || $event.metaKey) {
		return;
	}
	const element = closest($event.target, row_trigger, "td");
	if (element) {
		let row;
		switch ($event.key) {
			case "Space":
			case " ": // The control is a td with a role - some browsers do not have a default click from SPACE.
			case "Enter":
				timers.setTimeout(event.fire, 0, element, "click");
				$event.preventDefault();
				break;
			case "ArrowLeft" :
				row = closest(element, rowAnalog.ITEM.toString(), "tr");
				if (row && !shed.isDisabled(row)) {
					rowAnalog.setFocusIndex(row);
					focus.setFocusRequest(row);
					$event.preventDefault();
				}
				break;
		}
	}
}

/**
 * Focus bootstrapper to wire up keydown event listener.
 *
 * @function
 * @private
 * @param {FocusEvent} $event The focus event.
 */
function focusEvent({ target, defaultPrevented }) {
	if (!defaultPrevented) {
		const element = closest(target, row_trigger, "td");
		if (element && !element[BOOTSTRAPPED]) {
			element[BOOTSTRAPPED] = true;
			event.add(element, "keydown", keydownEvent);
		}
	}
}

/**
 * Click on table row expander control or expand/collapse all control.
 *
 * @function
 * @private
 * @param {module:wc/dom/event} $event The wrapped click event.
 */
function clickEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	let element = closest($event.target, row_trigger, "td");
	if (element) {
		if (shed.isDisabled(element)) {
			return;
		}

		const row = closest(element, tbl_expandable_row, "tr");
		if (row) {
			if (shed.isDisabled(row)) {
				return;
			}
			if (toggleRow(row) && !shed.isExpanded(row)) {// if we have collapsed the row do nothing else. This stops dynamic ajax on collapse
				$event.preventDefault();
			}
		}
	} else if ((element = $event.target.closest(expand_collapse_all)) && !shed.isDisabled(element)) {
		triggerManager.removeTrigger(element.id);
	}
}

/**
 * Expand/collapse all available row controllers in a table (but not in any further nested tables).
 * @function
 * @private
 * @param {HTMLElement} element One of the expand-all/collapse-all buttons.
 * @returns {Boolean} {@code true} if there are any rows to toggle.
 */
function toggleAll(element) {
	const candidates = element ? getControlled(element) : null;

	if (element && candidates && candidates.length) {
		const open = element.getAttribute(VALUE) === EXPAND;

		const filtered = candidates.filter(function(next) {
			if (!next) {
				return false;
			}
			if (open) {
				return !(shed.isExpanded(next)|| shed.isHidden(next));
			}
			return shed.isExpanded(next);
		});

		if (!open) {
			filtered.reverse();
		}
		
		filtered.forEach(next => toggleRow(next, true));

		return true;
	}

	return false;
}

/**
 * Toggle rows when the select/deselect all options are triggered.
 * @param {CustomEvent} $event.
 */
function activateOnSelect({ target }) {
	if (target && target.matches(expand_collapse_all)) {
		const toggled = toggleAll(target);
		const wrapper = target.closest(exp_coll_all_container);
		Array.from(wrapper.querySelectorAll(expand_collapse_all)).forEach(setControllerState);
		if (toggled && target.getAttribute(VALUE) === EXPAND && isAjaxExpansion(target)) {
			ajaxRegion.requestLoad(target, getTriggerDTO(target));
		}
	}
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
	var wrapper,
		idArray = [],
		ids;

	wrapper = element.closest(table_wrapper);
	if (!wrapper) {
		return;
	}

	Array.from(wrapper.querySelectorAll(tbl_expandable_row)).forEach(function (next) {
		idArray.push(next.id);
	});

	if (idArray.length) {
		ids = idArray.join(" ");
		Array.from(element.querySelectorAll(expand_collapse_all)).forEach(function (next) {
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
	Array.from(document.body.querySelectorAll(exp_coll_all_container)).forEach(setControlList);
}


initialise.register({
	/**
	 * Set up the collapsible row controllers.
	 * @function module:wc/ui/table/rowExpansion.initialise
	 * @public
	 * @param {HTMLElement} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
		event.add(element, "click", clickEvent);
	},

	/**
	 * Late setup to wire up the shed observer.
	 * @function module:wc/ui/table/rowExpansion.postInit
	 * @public
	 */
	postInit: function() {
		setControls();
		processResponse.subscribe(ajaxSubscriber, true);
		event.add(document.body, shed.events.EXPAND, expCollapseObserver);
		event.add(document.body, shed.events.COLLAPSE, expCollapseObserver);
		event.add(document.body, shed.events.HIDE, closeOnHide);
		event.add(document.body, shed.events.SELECT, activateOnSelect);
		formUpdateManager.subscribe(writeState);
	}

});

export default instance;
