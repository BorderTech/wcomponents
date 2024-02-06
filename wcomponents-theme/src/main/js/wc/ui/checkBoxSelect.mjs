import AriaAnalog from "wc/dom/ariaAnalog.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import clearSelection from "wc/dom/clearSelection.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import fieldset from "wc/ui/fieldset.mjs";
import cbrShedPublisher from "wc/dom/cbrShedPublisher.mjs";

let inGroupMode;

/**
 * Module to provide a grouped set of check boxes with some group-like behaviour which is not inherent in HTML check
 * boxes, though whether this should be implemented or not is another matter since according to
 * http://www.w3.org/TR/wai-aria-practices/#checkbox strictly speaking checkbox should not get arrow key
 * navigation nor SHIFT+CLICK range toggle support!
 *
 * @alias module:wc/ui/checkBoxSelect~CheckBoxSelect
 */
class CheckBoxSelect extends AriaAnalog {


	/**
	 * The description of a group item. This makes this class concrete.
	 * @var
	 * @type {string}
	 */
	ITEM = cbrShedPublisher.getWidget("cb").toString();

	/**
	 * The description of a group container since WCheckBoxSelects are grouped by descent.
	 * @var
	 * @type {string}
	 */
	CONTAINER = `${fieldset.getWidget().toString()}.wc-checkboxselect`;

	/**
	 * The description of a group item.
	 * @var
	 * @type {number}
	 */
	exclusiveSelect = super.SELECT_MODE.MULTIPLE;

	/**
	 * Hold a record of the last activated item in any group with which we interact. Used for SHIFT + CLICK
	 * processing. The Object has properties keyed on the container id and value is the id of the last activated
	 * check box.
	 * @var
	 * @type {Object}
	 */
	lastActivated = {};

	/**
	 * Extra setup in the initialisation phase needed to add an exception to SPACEBAR key event handling on
	 * check boxes, since that is what we are.
	 * @see {@link module:wc/dom/ariaAnalog}#actionable}
	 * @see {@link module:wc/dom/ariaAnalog}#keydownEvent}
	 * @function
	 */
	_extendedInitialisation(/* element */) {
		this.actionable.push(this.ITEM);
	}

	/**
	 * Activation action which occurs when a checkbox is selected/deselected.
	 * This includes group selection and revalidation of elements and containers.
	 * This over-ride is to remove the call to toggle the selection in aria-analog.
	 *
	 * @function
	 * @param {Element} element The element being activated.
	 * @param {Boolean} [SHIFT] If defined event.shiftKey.
	 * @override
	 */
	activate(element, SHIFT) {
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
	}

	/**
	 * We want to [de]select options between two end points. The select/deselect is determined by the checked
	 * state of element. element is the element being activated, lastActivated is the element LAST activated.
	 * @function
	 * @param {Element} element The element currently being de/selected.
	 * @param {Element} [lastActivated] The last element in the group which was activated.
	 * @param [container] The element which holds the checkboxes.
	 * @override
	 */
	doGroupSelect(element, lastActivated, container) {

		try {
			inGroupMode = true;

			if (element && lastActivated && container && !shed.isDisabled(element) && !(shed.isHidden(container)) || shed.isDisabled(container)) {
				const isSelected = shed.isSelected(element);
				const selectedFilter = isSelected ? getFilteredGroup.FILTERS.deselected : getFilteredGroup.FILTERS.selected;

				const _group = getFilteredGroup(element, { filter: (getFilteredGroup.FILTERS.enabled | selectedFilter), asObject: true });
				const filtered = _group["filtered"];
				const unfiltered = _group["unfiltered"];

				if (filtered?.length) {
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
	}

	/**
	 * We do not need the inherited focus event.
	 * @function
	 * @override
	 */
	focusEvent = null;  // do not reset tabIndex in grouped checkboxes
}

export default initialise.register(new CheckBoxSelect());
