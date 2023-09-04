import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import clearSelection from "wc/dom/clearSelection";
import group from "wc/dom/group";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import fieldset from "wc/ui/fieldset";
import cbrShedPublisher from "wc/dom/cbrShedPublisher";

/**
 * Module to provide a grouped set of check boxes with some group-like behaviour which is not inherent in HTML check
 * boxes, though whether this should be implemented or not is another matter since according to
 * http://www.w3.org/TR/wai-aria-practices/#checkbox strictly speaking checkbox should not get arrow key
 * navigation nor SHIFT+CLICK range toggle support!
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 */
CheckBoxSelect.prototype = ariaAnalog;
let instance = new CheckBoxSelect();
instance.constructor = CheckBoxSelect;

/**
 * @constructor
 * @alias module:wc/ui/checkBoxSelect~CheckBoxSelect
 * @extends module:wc/dom/ariaAnalog~AriaAnalog
 * @private
 */
function CheckBoxSelect() {
	let inGroupMode;

	/**
	 * The description of a group item. This makes this class concrete.
	 * @var
	 * @type {string}
	 * @public
	 */
	this.ITEM = cbrShedPublisher.getWidget("cb").toString();

	/**
	 * The description of a group container since WCheckBoxSelects are grouped by descent.
	 * @var
	 * @type {string}
	 * @public
	 */
	this.CONTAINER = `${fieldset.getWidget().toString()}.wc-checkboxselect`;

	/**
	 * The description of a group item.
	 * @var
	 * @type {number}
	 * @protected
	 */
	this.exclusiveSelect = this.SELECT_MODE.MULTIPLE;

	/**
	 * Hold a record of the last activated item in any group with which we interact. Used for SHIFT + CLICK
	 * processing. The Object has properties keyed on the container id and value is the id of the last activated
	 * check box.
	 * @var
	 * @type {Object}
	 * @protected
	 */
	this.lastActivated = {};

	/**
	 * Extra setup in the initialisation phase needed to add an exception to SPACEBAR key event handling on
	 * check boxes, since that is what we are.
	 * @see {@link module:wc/dom/ariaAnalog}#actionable}
	 * @see {@link module:wc/dom/ariaAnalog}#keydownEvent}
	 * @function
	 * @protected
	 */
	this._extendedInitialisation = function(/* element */) {
		this.actionable.push(this.ITEM);
	};

	/**
	 * Activation action which occurs when a checkbox is selected/deselected.
	 * This includes group selection and revalidation of elements and containers.
	 * This over-ride is to remove the call to toggle the selection in aria-analog.
	 *
	 * @function
	 * @protected
	 * @param {Element} element The element being activated.
	 * @param {Boolean} [SHIFT] If defined event.shiftKey.
	 * @override
	 */
	this.activate = function(element, SHIFT) {
		const container = this.getGroupContainer(element);

		if (container && !inGroupMode) {
			if (SHIFT) {
				const lastActivated = document.getElementById(this.lastActivated[container.id]);
				if (lastActivated) {
					this.doGroupSelect(element, lastActivated, container);
				}
			}
			this.setLastActivated(element);
		}
	};

	/**
	 * Allow an external component to set selection of all checkboxes in a CheckBoxSelect using an array of
	 * values for those to be selected.
	 * @function
	 * @public
	 * @param {HTMLElement} element The checkBoxSelect.
	 * @param {String[]} selectedValArr An array of value(s) of the checkbox(es) to select.
	 */
	this.setSelectionByValue = function(element, selectedValArr) {
		const selectable = element.matches(this.CONTAINER.toString()) && !(shed.isHidden(element) || shed.isDisabled(element));
		const _group = selectable ? group.get(element) : [];
		let lastOption, silent = true;
		_group.forEach((option, idx) => {
			if (silent && idx === _group.length - 1) {
				silent = false;
			}
			if (selectedValArr.indexOf(option.value) > -1) {
				shed.select(option, silent);
			} else {
				shed.deselect(option, silent);
			}
			lastOption = option;
		});
		if (lastOption) {
			this.setLastActivated(lastOption);
		}
	};

	/**
	 * We want to [de]select options between two end points. The select/deselect is determined by the checked
	 * state of element. element is the element being activated, lastActivated is the element LAST activated.
	 * @function
	 * @protected
	 * @param {HTMLElement} element The element currently being de/selected.
	 * @param {HTMLElement} [lastActivated] The last element in the group which was activated.
	 * @param {HTMLElement} container The element which holds the checkboxes.
	 * @override
	 */
	this.doGroupSelect = function(element, lastActivated, container) {

		try {
			inGroupMode = true;

			if (element && lastActivated && container && !shed.isDisabled(element) && !(shed.isHidden(container)) || shed.isDisabled(container)) {
				const isSelected = shed.isSelected(element);
				const selectedFilter = isSelected ? getFilteredGroup.FILTERS.deselected : getFilteredGroup.FILTERS.selected;

				const _group = getFilteredGroup(element, {filter: (getFilteredGroup.FILTERS.enabled | selectedFilter), asObject: true});
				const filtered = _group.filtered;
				const unfiltered = _group.unfiltered;

				if (filtered && filtered.length) {
					let start = Math.min(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));

					while (unfiltered[start] && shed.isSelected(unfiltered[start]) === isSelected) {
						start++;
					}
					if (start < unfiltered.length) {
						let end = Math.max(unfiltered.indexOf(element), unfiltered.indexOf(lastActivated));

						while (end >= 0 && shed.isSelected(unfiltered[end]) === isSelected) {
							end--;
						}

						if (end >= 0) {
							start = filtered.indexOf(unfiltered[start]);
							if (start > -1) {
								end = filtered.indexOf(unfiltered[end]);
								const func = isSelected ? "select" : "deselect";

								for (let i = start; i <= end; ++i) {
									let next = filtered[i];
									shed[func](next, i !== end);
								}
							}
						}
					}
				}
			}
			clearSelection();
		} finally {
			inGroupMode = false;
		}
	};

	/**
	 * We do not need the inherited focus event.
	 * @function
	 * @protected
	 * @override
	 */
	this.focusEvent = null;  // do not reset tabIndex in grouped checkboxes
}

export default initialise.register(instance);
