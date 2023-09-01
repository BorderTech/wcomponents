/**
 * Provides functionality to select/deselect all checkboxes in a group. Generally applies to {@link module:wc/ui/checkBoxSelect} but can work
 * with any check boxes in any container.
 *
 * The following modules are imported as dependencies as their functionality us used by WSelectToggle, but they are not required by the
 * code in this module.
 *
 * * {@link module:wc/ui/checkboxAnalog}
 * * {@link module:wc/ui/radioAnalog}
 *
 * @alias module:wc/ui/selectToggle~SelectToggle
 * @private
 */

import shed from "wc/dom/shed";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import formUpdateManager from "wc/dom/formUpdateManager";
import initialise from "wc/dom/initialise";
import uid from "wc/dom/uid";
import table from "wc/ui/table/common";
import rowAnalog from "wc/ui/rowAnalog";
import processResponse from "wc/ui/ajax/processResponse";
import i18n from "wc/i18n/i18n";
import icon from "wc/ui/icon";
import checkBox from "wc/ui/checkBox";
import getLabelsForElement from "wc/dom/getLabelsForElement";
import "wc/ui/checkboxAnalog";
import "wc/ui/radioAnalog";

const CLASS_TOGGLE = "wc_seltog";

const controllerSelector = `.${CLASS_TOGGLE}`;
const controllerAbstractSelector = `button.${CLASS_TOGGLE}`;
const controllerCheckboxSelector = `${controllerAbstractSelector}[role='checkbox']`;
const controllerListSelector = `span.${CLASS_TOGGLE}`;
const controllerMenuSelector = `${controllerSelector}.wc_submenucontent`;
const radioSubControllerSelector = `${controllerAbstractSelector}[role='radio']`;
const menuSubControllerSelector = `${controllerAbstractSelector}[role='menuitemradio']`;
const subControllerSelector = [radioSubControllerSelector, menuSubControllerSelector];
const activeControllerSelector = subControllerSelector.map(next => `${next}[aria-checked='true']`);
const checkboxSelector = checkBox.getWidget().toString();
const ariaCbSelector = "[role='checkbox']";
const tableSelector = `${table.TABLE.toString()}[aria-multiselectable='true']`;
const tbodySelector = `${tableSelector} > ${table.TBODY.toString()}`;
const rowSelector = `${table.TBODY.toString()} > ${rowAnalog.ITEM.toString()}`;
const allCbSelector = [checkboxSelector, ariaCbSelector, rowSelector];

const registry = {},
	WSELECTTOGGLE_CLASS = "wc-selecttoggle",
	ARIA_CONTROLS = "aria-controls",
	TARGET_ATTRIB = "data-wc-target",
	STATE = {ALL: "all",
		NONE: "none",
		MIXED: "some",
		UNKOWN: "unknown"};

let STAND_IN_LABEL,
	STAND_IN_TEXT_EQUIV;

function isWSelectToggleContainer(element) {
	return element?.classList.contains(WSELECTTOGGLE_CLASS);
}

/**
 *
 * @param {HTMLElement} element
 * @return {boolean}
 */
function isWSelectToggle(element) {
	if (isWSelectToggleContainer(element)) {
		return true;
	}
	if (element.matches(radioSubControllerSelector)) {
		const el = element.closest(controllerListSelector);
		return isWSelectToggleContainer(el);
	}
	return false;
}

/**
* Write the state of the select toggles when a form submission takes place.
*
* @function
* @private
* @param {HTMLElement} form The form or sub-form the state of which is being written.
* @param {HTMLElement} stateContainer The element to which to append the state inputs.
*/
function writeState(form, stateContainer) {

	// CHECKBOX type controllers
	Array.from(form.querySelectorAll(controllerCheckboxSelector)).forEach(next => {
		let state = STATE.UNKNOWN;
		if (!shed.isDisabled(next) && isWSelectToggle(next)) {
			if (shed.isSelected(next) === shed.state.MIXED) {
				state = STATE.MIXED;
			} else if (shed.isSelected(next) === shed.state.SELECTED) {
				state = STATE.ALL;
			} else if (shed.isSelected(next) === shed.state.DESELECTED) {
				state = STATE.NONE;
			}
			if (state !== STATE.UNKNOWN) {
				formUpdateManager.writeStateField(stateContainer, next.getAttribute("data-wc-name"), state);
			}
		}
	});

	/*
	 * Write the state of selectToggles of type text when no options are selected
	 * NOTE: if either radio analog is selected then the state is written by radioAnalog
	 * @param next the containing element of a selectToggle of type text
	 */
	Array.from(form.querySelectorAll(controllerListSelector)).forEach(next => {
		if (!shed.isDisabled(next) && isWSelectToggle(next)) {
			if (!next.querySelector(activeControllerSelector.join())) {
				const reportValue = "some";
				const reportName = next.querySelector(radioSubControllerSelector).getAttribute("data-wc-name"); // note: all buttons in the selectToggle group have the same name
				formUpdateManager.writeStateField(stateContainer, reportName, reportValue);
			}
		}
	});
}

/**
 *
 * @param {HTMLElement} trigger
 * @return {null|HTMLElement[]}
 */
function getControlledElements(trigger) {
	let actualTrigger = trigger;

	if (trigger.matches(controllerListSelector)) {
		actualTrigger = trigger.querySelector(radioSubControllerSelector);
	} else if (trigger.matches(controllerMenuSelector)) {
		actualTrigger = trigger.querySelector(menuSubControllerSelector);
	}

	const idList = actualTrigger.getAttribute(ARIA_CONTROLS);
	if (idList) {
		return idList.split(" ").map(next => document.getElementById(next));
	}
	return null;
}

/**
 *
 * @param {string} groupName
 * @return {HTMLElement[]}
 */
function getNamedGroup(groupName) {
	const namedGroupWd = [
		`${checkboxSelector}[data-wc-group='${groupName}']`,
		`${ariaCbSelector}[data-wc-group='${groupName}']`
	];
	return /** @type HTMLElement[] */ Array.from(document.querySelectorAll(namedGroupWd.join()));
}

/**
 *
 * @param {HTMLElement} element
 * @return {HTMLElement[]}
 */
function getAllControllers(element) {
	if (!element?.id) {
		return [];
	}
	const controllingWidget = `${controllerAbstractSelector}[aria-controls='${element.id}']`;
	return /** @type HTMLElement[] */ document.body.querySelectorAll(controllingWidget);
}

/**
 * Get all the components which are controlled by a selectToggle.
 *
 * @function
 * @private
 * @param {HTMLElement} controller The selectToggle.
 * @returns {HTMLElement[]} The elements in the group as an Array not as a nodeList or null if no group found.
 */
function getGroup(controller) {

	if (!controller) {
		return null;
	}

	const targetId = controller.getAttribute(TARGET_ATTRIB);
	if (!targetId) {
		// NOTE: the aria-controls list of a WTable row selection sub controller is set in the renderer as
		// all the information to render this is available.
		return null;
	}
	let candidates;
	const targetElement = document.getElementById(targetId);
	if (targetElement) {
		if (targetElement.matches(checkboxSelector)) {
			const groupName = targetElement.getAttribute("data-wc-group");
			if (!groupName) {
				return [targetElement];
			}
			return getNamedGroup(groupName);
		}
		// hurray, the easy one! Get every checkbox or multi-selectable table row inside the target.
		// NOTE: the sub-row selector in WTable does not have the data-wc-target attribute and therefore
		// will never be here
		if (isWSelectToggle(controller)) {
			// get all checkboxes and surrogates inside the targetElement
			candidates = Array.from(targetElement.querySelectorAll(allCbSelector.join()));
			// remove any which are themselves a controller
			return candidates.map(next => next.matches(controllerCheckboxSelector) ? null : next);
		}

		// WTable select/deselect all
		candidates = Array.from(targetElement.querySelectorAll(rowSelector));
		// we only want those rows in the current table, not in nested tables.
		return candidates.map(next => next.closest(tbodySelector) === targetElement ? next : null);
	}
	// No target element means a WSelectToggle with a named group
	return getNamedGroup(targetId);
}

/**
 * Undertake the "click" of selectable components controlled by a select toggle. A helper for
 * {@link module:wc/ui/selectToggle~shedSubscriber}.
 *
 * @function
 * @private
 * @param {HTMLElement} trigger The select toggle trigger element.
 * @returns {?number} The number of items affected by this activation. This is needed to prevent a double
 * activation of a SUB_CONTROLLER from erroneously setting the sub-controllers state.
 */
function activateTrigger(trigger) {
	let _group = getControlledElements(trigger);
	if (_group?.length) {
		let state;
		if (trigger.matches(controllerCheckboxSelector) || !(state = trigger.getAttribute("data-wc-value"))) {
			state = shed.isSelected(trigger) === shed.state.DESELECTED ? STATE.NONE : STATE.ALL;
		}

		/*
		 * Why the filter variation?
		 *
		 * We normally do not allow users to interact with controls they are not able to perceive. This
		 * complies with normal usability guidelines and keeps "client mode" controls in sync with ajax
		 * modes of the same controls
		 *
		 * Example with row selection
		 *
		 * * When table also has row expansion:
		 *   * in client mode all sub rows are present so could be "selectable" by a select toggle;
		 *   * in lazy or dynamic mode only the descendants of opened rows are available.
		 * * When "select all" is invoked if we allowed hidden rows to be selected
		 *   * in client rows the newly visible rows would be selected;
		 *   * in lazy/dynamic mode the newly visible rows would not be selected.
		 *   This leads to an inconsistent user experience so we do not allow interaction with controls
		 *   which are not visible.
		 *
		 * HOWEVER
		 * * If a row is expanded, then "select all" is invoked the sub row(s) will be selected.
		 * * If the row is then collapsed and "deselect all" is invoked the sub row(s) will not be
		 *   deselected as we do not allow interaction with hidden controls.
		 *
		 * SO:
		 * * If the expand mode is ajax and some other control then refreshes the view (or part thereof
		 *   containing the table)
		 *   * the closed row does not have children, if it is then expanded again
		 *   * the table state does not include "selected" for the child rows as they are not present
		 *   * therefore the newly visible rows will not be selected
		 *
		 * * If the expand mode is client the rows are always present so when the table is refreshed
		 *   * the closed row still has its children, if it is expanded again
		 *   * the table does not send its state to the server and the child rows are not changed
		 *   * therefore the newly visible rows will remain selected which is inconsistent with the above.
		 *
		 * THEREFORE we must allow a selectToggle to **deselect** hidden controls.
		 *
		 * That is why we have a filter variation.
		 */
		let groupFilter = getFilteredGroup.FILTERS[(state === STATE.ALL) ? "deselected" : "selected"] | getFilteredGroup.FILTERS.enabled;
		if (state === STATE.ALL) { // we have to allow "hidden" controls to be deslected but not selected.
			groupFilter = groupFilter | getFilteredGroup.FILTERS.visible;
		}
		_group = getFilteredGroup(_group, {filter: groupFilter});

		_group = _group.filter(function (next) {
			return !(next.matches(controllerSelector) || next.getAttribute("aria-readonly") === "true");
		});

		_group.forEach(next => shed[(state === STATE.ALL) ? "select" : "deselect"](next));

		return _group.length;
	}
	return null; // do not return 0 as this means we got a group which after filtering was zero length.
}



/**
 * Set the controller based on status. A helper for {@link module:wc/ui/selectToggle~shedSubscriber}.
 *
 * @function
 * @private
 * @param {HTMLElement} controller A WSelectToggle.
 * @param {String} status The status to set "all", "some" or "none".
 */
function setControllerStatus(controller, status) {
	const ICON_ALL = "fa-check-square-o",
		ICON_SOME = "fa-square",
		ICON_NONE = "fa-square-o";
	if (!controller) {
		return;
	}

	if (controller.matches(controllerCheckboxSelector)) {
		let to, from = [];
		// By this stage it is too late to calculate the old icon from the
		// selected state as the controller may have had its state changed by
		// a click event on itself, not by a change in the state of one of its
		// controlled elements.
		const initialState = shed.isSelected(controller);
		if (status === STATE.ALL && initialState !== shed.state.SELECTED) {
			shed.select(controller, true);
			to = ICON_ALL;
			from = [ICON_SOME, ICON_NONE];
		} else if (status === STATE.MIXED && initialState !== shed.state.MIXED) {
			shed.mix(controller, true);
			to = ICON_SOME;
			from = [ICON_ALL, ICON_NONE];
		} else if (status === STATE.NONE && initialState !== shed.state.DESELECTED) {
			shed.deselect(controller, true);
			to = ICON_NONE;
			from = [ICON_SOME, ICON_ALL];
		}
		if (to) {
			icon.change(controller, to, from[0]);
			icon.remove(controller, from[1]);
		}
		return;
	}

	if (status === STATE.MIXED || controller.getAttribute("data-wc-value") !== status) {
		shed.deselect(controller, true);
		return;
	}
	shed.select(controller, true);
}

/**
 * Listen for select/deselect and act on any controller.
 *
 * @function
 * @private
 * @param {HTMLElement} element The element being selected/deselected.
 * @param {String} action shed.SELECT or shed.DESELECT.
 */
function shedObserver(element, action) {
	if (!element) {
		return;
	}

	// Change the selected stae of a WSelectToggle button:
	if ((action === shed.actions.SELECT && element.matches(subControllerSelector.join())) ||
		((action === shed.actions.SELECT || action === shed.actions.DESELECT) &&
			(element.matches(controllerCheckboxSelector)))) {
		/* If activateTrigger returns exactly 0 we did not change the state of any controls so we won't
		 * have set the state of the controller and it may be in the incorrect state. This _will_ be the
		 * case if, for example, someone clicks a "select all" which controls only selected and hidden
		 * components. Nothing will be selected in activateTigger so the state of the controller will not
		 * have been updated by this shed observer and it will remain in an erroneous selected state (the
		 * controller should be mixed). */
		if (activateTrigger(element) === 0) {
			controlStatusHelper(element);
		}
		return;
	}
	const allControllers = element.matches(allCbSelector.join()) ? Array.from(getAllControllers(element)) : [];
	allControllers.forEach(controlStatusHelper);

}

function controlStatusHelper(controller) {
	let groupState = STATE.MIXED;
	const controlledElements = shed.isDisabled(controller) ? null : getControlledElements(controller);
	if (!controlledElements) {
		// no grouped items means no controller to set state on.
		return;
	}
	/** @type {HTMLElement[]} */
	let selected;
	if (controlledElements.length === 0) {
		groupState = STATE.NONE;
	} else if ((selected = getFilteredGroup(controlledElements))) {
		if (selected.length === 0) {
			groupState = STATE.NONE;
		} else if (controlledElements.length === selected.length) {
			groupState = STATE.ALL;
		}
	}

	setControllerStatus(controller, groupState);
}

/**
 * Set the aria-controls attribute on the buttons of a selectToggle.
 *
 * @function
 * @private
 * @param {HTMLElement} element a collapsible toggle wrapper
 */
function setControlList(element) {
	const candidates = getGroup(element),
		idArray = [];

	if (candidates) {
		candidates.forEach(next => {
			if (!next.id) {
				next.id = uid();
			}
			idArray.push(next.id);
		});

		if (idArray.length) {
			const ids = idArray.join(" ");
			if (element.matches(controllerCheckboxSelector)) {
				element.setAttribute(ARIA_CONTROLS, ids);
			} else {
				Array.prototype.forEach.call(element.querySelectorAll(radioSubControllerSelector), function (next) {
					next.setAttribute(ARIA_CONTROLS, ids);
				});
			}
		}
	}
}

/**
 * Set aria-controls for each collapsible toggle
 *
 * @function
 * @private
 */
function setControls() {
	Array.from(document.body.querySelectorAll(controllerSelector)).forEach(setControlList);
}

function setAriaLabelAttrib(element) {
	const labels = getLabelsForElement(element);
	let label = labels.length ? labels[0] : null;
	const elId = element.id;

	if (!label) {
		STAND_IN_LABEL = STAND_IN_LABEL || i18n.get("toggle_label");
		const labelStr = `<span data-wc-for='${elId}' id='${elId}_l'>${STAND_IN_LABEL}</span>`;
		element.insertAdjacentHTML("afterbegin", labelStr);
		label = element.firstChild;
	}
	const id = label ? label.id : "";
	if (id) {
		Array.from(element.querySelectorAll(radioSubControllerSelector)).forEach(next => {
			next.setAttribute("aria-labelledby", id);
		});
	}
}

function setTextEquivalent(element) {
	const labels = getLabelsForElement(element);
	let label = labels.length ? labels[0] : null;
	if (label) {
		element.setAttribute("aria-labelledby", label.id);
		return;
	}
	STAND_IN_TEXT_EQUIV = STAND_IN_TEXT_EQUIV || i18n.get("toggle_all_label");
	if (isWSelectToggle(element)) {
		element.setAttribute("title", STAND_IN_TEXT_EQUIV);
	} else {
		element.insertAdjacentHTML("beforeend", `<span>${STAND_IN_TEXT_EQUIV}</span>`);
	}
}

function setLabelledBy(element) {
	const el = element || document.body;
	if (el.matches(controllerListSelector)) {
		setAriaLabelAttrib(el);
	} else if (el.matches(controllerCheckboxSelector)) {
		setTextEquivalent(el);
	} else {
		Array.from(el.querySelectorAll(controllerListSelector)).forEach(setAriaLabelAttrib);
		Array.from(el.querySelectorAll(controllerCheckboxSelector)).forEach(setTextEquivalent);
	}
}

const instance = {
	/**
	 * Late initialisation to add {@link module:wc/dom/shed} and {@link module:wc/dom/formUpdateManager}
	 * subscribers.
	 *
	 * @function module:wc/ui/selectToggle.postInit
	 * @public
	 */
	postInit: function() {
		setControls();
		setLabelledBy();
		shed.subscribe(shed.actions.SELECT, shedObserver);
		shed.subscribe(shed.actions.DESELECT, shedObserver);
		shed.subscribe(shed.actions.MIX, shedObserver);
		formUpdateManager.subscribe(writeState);
		processResponse.subscribe(setControls, true);
		processResponse.subscribe(setLabelledBy, true);
	},

	/**
	 * Set up a registry of all select togglers and their group keyed on id.
	 *
	 * @function
	 * @public
	 * @param {Object[]} objArr an array of selectToggle dtos.
	 */
	register: function(objArr) {
		objArr.forEach(next => registry[next.identifier] = next);
	}
};

export default initialise.register(instance);
