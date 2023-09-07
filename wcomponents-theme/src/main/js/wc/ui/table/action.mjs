import event from "wc/dom/event";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import common from "wc/ui/table/common";
import debounce from "wc/debounce";

const registry = new Conditions(enableDisableButton),
	ACTION_CONTAINER = ".wc-actions",
	ACTION_BUTTON = `${ACTION_CONTAINER} ${common.BUTTON}`,
	ACTION_TABLE = common.WRAPPER,
	ROW_CONTAINER = common.TBODY,
	ROW = `${ROW_CONTAINER} > ${common.TR}`;

const instance = {
	register: actionArray => {
		actionArray.forEach(registry.set, registry);
	}
};

/**
 * Determines if an action condition is met.
 *
 * @function
 * @private
 * @param {Element} button The table action invoking button.
 * @param {Object} condition The action condition.
 * @returns {Boolean} true if the condition is met, or the button is not a table action.
 */
function isConditionMet(button, condition) {
	let otherSelected = 0,
		currentSelected = 0,
		totalSelected = 0;
	const table = button.closest(ACTION_TABLE)
	if (table) {
		const { min, max } = condition;
		if (!(min || max)) {  // no condition worth testing.
			return true;
		}
		currentSelected = /** @type {HTMLElement[]} */(getFilteredGroup(table.querySelector(ROW_CONTAINER))).length;

		if (condition.otherSelected) {
			otherSelected = condition.otherSelected;
		}

		totalSelected = currentSelected + otherSelected;

		if ((min && totalSelected < min) || (max && totalSelected > max)) {
			return false;
		}
	}
	return true;
}


/**
 * Test if an action button can be enabled.
 * @function
 * @private
 * @param {Element} button
 * @returns {Boolean} true if the conditions of the button are met.
 */
function canEnableButton(button) {
	const conditions = registry.get(button);
	if (conditions) {
		return Array.prototype.every.call(conditions, condition => {
			if (condition.type === "error") {
				return isConditionMet(button, condition);
			}
			return true;
		});
	}
	// if no conditions we can always be enabled.
	return true;
}

/**
 * Helper to actually toggle the disabled state.
 * @function
 * @private
 * @param {Element} button the button which we are disabling/enabling.
 */
function enableDisableButton(button) {
	const func = canEnableButton(button) ? "enable" : "disable";
	shed[func](button);
}

function canSubmit (button) {
	if (!canEnableButton(button)) {
		return false;
	}
	const conditions = registry.get(button);
	if (conditions) {
		return Array.prototype.every.call(conditions, condition => {
			if (condition.type === "error") {
				return isConditionMet(button, condition);
			}
			if (!isConditionMet(button, condition)) {
				return confirm(condition.message);
			}
			return true;
		});
	}
	// if no conditions we can always submit.
	return true;
}

/**
 * Click listener for table actions. Invokes the test of conditions before allowing the submit button's
 * normal action.
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
 */
function clickEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	const target = $event.target.closest(ACTION_BUTTON);
	if (target && !shed.isDisabled(target) && !canSubmit(target)) {
		$event.preventDefault();
	}
}


/**
 * Set the initial state of action buttons when a page/ajax arrives.
 * @function
 * @private
 * @param {Element} container the page or ajax response.
 */
//		function setUp(container) {
//			const _container = container || document.body;
//			Array.from(_container.querySelectorAll(ACTION_BUTTON)).forEach(function(next) {
//				next.setAttribute("formnovalidate", "formnovalidate");
//				enableDisableButton(next);
//			});
//		}

/**
 * Subscriber to row select/deselect which is the trigger for changing the button's disabled state.
 * @function
 * @private
 * @param {Element} element the element being selected/deselected.
 */
function shedSubscriber(element) {
	const table = (element?.matches(ROW)) ? element.closest(ACTION_TABLE) : null;
	if (table) {
		Array.from(table.querySelectorAll(ACTION_BUTTON)).forEach(next => {
			if (next.closest(ACTION_TABLE) !== table) {
				return;  // not in the same table.
			}
			enableDisableButton(next);
		});
	}
}

/**
 * A special registry to handle button conditions.
 *
 * @param {Function} buttonChangeFunc The function to call when a button's conditions have been changed.
 * @constructor
 */
function Conditions(buttonChangeFunc) {
	const data = {},
		changed = {};

	/**
	 * Ask the registry to ensure that all buttons are "up to date" with their registered conditions.
	 * If there have been any condition changes since the last update then they will be applied.
	 * Note: buttons may not be updated synchronously.
	 */
	this.update = debounce(function() {
		const changedIds = Object.keys(changed);
		for (const id of changedIds) {
			let button = document.getElementById(id);
			if (button) {
				delete changed[id];
				button.setAttribute("formnovalidate", "formnovalidate");  // this really only needs to happen once but meh
				buttonChangeFunc(button);
			} else {
				/*
				 * This could theoretically happen if the condition is registered before both:
				 * - the DOM is ready AND
				 * - the debounce timer is done
				 * I never saw it happen though.
				 * It should be OK if update is called again because we won't remove the changed flag until it's actioned.
				 * I wouldn't call update from itself though for risk of infinite looping.
				 */
				console.warn("Could not find button", id);  // this may be resolved in postInit
			}
		}
	}, 200);

	/**
	 * Register a button condition.
	 * @param {Object} action The table action which holds the conditions.
	 */
	this.set = function(action) {
		const id = action ? action.trigger : null;
		if (id) {
			data[id] = action.conditions;
			changed[id] = true;
			this.update();
		}
	};

	/**
	 * Get registered conditions for a given table action button.
	 *
	 * @function
	 * @param {Element} button The table action invoking button.
	 * @returns {Object} The action conditions.
	 */
	this.get = function(button) {
		if (!button) {
			return null;
		}
		return data[button.id];
	};
}

initialise.register({
	/**
	 * Initial set up for table action.
	 *
	 * @function module:wc/ui/table/action.postInit
	 * @public
	 */
	postInit: () => {
		// setUp();
		// processResponse.subscribe(setUp, true);  // Re-evaluation will be triggered when the register method is called by AJAX scripts
		registry.update();  // Probably not strictly necessary but just in case...
		shed.subscribe(shed.actions.SELECT, shedSubscriber);
		shed.subscribe(shed.actions.DESELECT, shedSubscriber);
	},
	/**
	 * Initial set up for table action.
	 *
	 * @function module:wc/action.initialise
	 * @public
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: (element) => event.add(element, "click", clickEvent, -50)
});

export default instance;
