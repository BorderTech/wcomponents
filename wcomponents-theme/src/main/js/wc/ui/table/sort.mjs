/**
 * Provides table sort controls. A column is sorted by an algorithm controlled by the server application. There is no
 * client side sorting.
 *
 * @module
 */

import initialise from "wc/dom/initialise.mjs";
import event from "wc/dom/event.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import group from "wc/dom/group.mjs";
import ajaxRegion from "wc/ui/ajaxRegion.mjs";
import isEventInLabel from "wc/dom/isEventInLabel.mjs";
import isAcceptableEventTarget from "wc/dom/isAcceptableTarget.mjs";
import shed from "wc/dom/shed.mjs";
import common from "wc/ui/table/common.mjs";

const TABLE_WRAPPER = common.WRAPPER.toString(),
	SORTABLE_TABLE = `${common.TABLE.toString()}[sortable]`,
	THEAD = `${SORTABLE_TABLE} > ${common.THEAD.toString()}`,
	SORT_CONTROL = `${THEAD} ${common.TH.toString()}[aria-sort]`,
	// ID_EXTENDER = "_thh",
	SORT_ATTRIB = "sorted",
	ARIA_SORT_ATTRIB = "aria-sort",
	SORTED_COL = `${SORT_CONTROL}[sorted]`;

/**
 *
 * @param {Element} element
 * @returns {HTMLElement}
 */
function getWrapper(element) {
	return element.closest(TABLE_WRAPPER);
}

/**
 * Helper function for key and click initiated collapse toggling. Used to determine if the event is
 * expected to change the state of the WCollapsible or some other nested interactive control. If we are able
 * to act on element, or find a different focusable element before we get to element then return that
 * element, so we can use it to work out if we have to prevent default on SPACEBAR
 * @function
 * @private
 * @param {Event & {target: HTMLElement}} $event The event which initiated the toggle.
 * @param {Element} element A sortable column header. Element must already have been determined to be a
 *    SORT_CONTROL and since we have already extracted this from $event we may as well pass it in as
 *    an arg rather than re-testing.
 */
function toggleEventHelper({ target }, element) {
	if ((element === target || (!isEventInLabel(target) && isAcceptableEventTarget(element, target))) && !shed.isDisabled(element)) {
		const sorted = element.getAttribute(SORT_ATTRIB);

		if (!sorted || sorted.indexOf("reversed") > -1) {
			if (!sorted) { // remove current sort col if any
				const controlGroup = group.getGroup(element, SORTED_COL, THEAD);  // there should be only one
				if (controlGroup?.length) {
					controlGroup.forEach(function(next) {
						next.removeAttribute(SORT_ATTRIB);
						next.setAttribute(ARIA_SORT_ATTRIB, "none");
					});
				}
			}
			element.setAttribute(SORT_ATTRIB, "1");
			element.setAttribute(ARIA_SORT_ATTRIB, "ascending");
		} else {
			element.setAttribute(SORT_ATTRIB, "1 reversed");
			element.setAttribute(ARIA_SORT_ATTRIB, "descending");
		}

		ajaxRegion.requestLoad(element, common.getAjaxDTO(element, true));
	}
}

/**
 *
 * @param {MouseEvent & {target: HTMLElement}} $event
 */
function clickEvent($event) {
	const element = $event.defaultPrevented ? null : $event.target.closest(SORT_CONTROL);
	if (element) {
		toggleEventHelper($event, element);
	}
}

/**
 *
 * @param {KeyboardEvent & {target: HTMLElement}} $event
 */
function keydownEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	if ($event.key === "Enter") {  // remember this event is only attached to an element which is a SORT_CONTROL.
		toggleEventHelper($event, $event.target);
	}
}

/**
 *
 * @param {FocusEvent & {target: HTMLElement}} $event
 */
function focusEvent({ defaultPrevented, target }) {
	const BOOTSTRAPPED = "wc.ui.table.sort.BS";
	if (!defaultPrevented && !target[BOOTSTRAPPED] && target.matches(SORT_CONTROL)) {
		target[BOOTSTRAPPED] = true;
		event.add(target, "keydown", keydownEvent);
	}
}

/**
 * @param {Element} container
 * @param {Element} stateContainer
 */
function writeState(container, stateContainer) {
	const sortableTables = container.querySelectorAll(SORTABLE_TABLE);
	Array.from(sortableTables).forEach(next => {
		const nextContainer = getWrapper(next),
			tableId = nextContainer.id,
			sortedColumn = next.querySelector(SORTED_COL);

		// we need to do the reverse look-up to allow for the possibility of nested tables.
		if (sortedColumn && next === sortedColumn.closest(SORTABLE_TABLE)) {
			formUpdateManager.writeStateField(stateContainer, tableId + ".sort", sortedColumn.getAttribute("data-wc-columnidx"));
			if (sortedColumn.getAttribute("sorted").indexOf("reversed") > -1) {
				formUpdateManager.writeStateField(stateContainer, tableId + ".sortDesc", "true");
			}
		}
	});
}

initialise.register({
	initialise: function(element) {
		if (event.canCapture) {
			event.add(element, { type: "focus", listener: focusEvent, capture: true });
		} else {
			event.add(element, "focusin", focusEvent);
		}
		event.add(element, "click", clickEvent);
	},

	postInit: function() {
		formUpdateManager.subscribe({ writeState });
	}
});
