/*
 * Provides functionality to undertake client validation of WCheckBoxSelect. Extends {@link module:wc/validation/ariaAnalog}.
 */
import getFilteredGroup from "wc/dom/getFilteredGroup";
import group from "wc/dom/group";
import shed from "wc/dom/shed";
import validationManager from "wc/ui/validation/validationManager";
import required from "wc/ui/validation/required";
import minMax from "wc/ui/validation/minMax";
import checkBoxSelect from "wc/ui/checkBoxSelect";
import isComplete from "wc/ui/validation/isComplete";

/**
 * Determines whether a container is valid.
 * @function
 * @private
 * @param {HTMLElement} container The container being validated, may be a WCheckBoxSelect root container or an
 *    element which contains WCheckBoxSelects such as a form.
 * @returns {Boolean} true if valid.
 */
function validate (container) {
	const obj = {
			container: container,
			widget: checkBoxSelect.CONTAINER?.toString(),
			constraint: required.CONSTRAINTS.CLASSNAME,
			position: "beforeEnd"
		},
		_required = required.complexValidationHelper(obj);

	// add a selectedFunc to be able to do min/max validation
	obj.selectedFunc = fs => getFilteredGroup(fs, { itemWd: checkBoxSelect.ITEM }) || [];

	return _required && minMax(obj);
}

/**
 * Re-validate a previously invalid WCheckBoxSelect when the component's selection is changed.
 *
 * @function
 * @private
 * @param {HTMLElement} element A WCheckBoxSelect
 */
const revalidate = element => validationManager.revalidationHelper(element, validate);

function shedSubscriber(element) {
	const container = group.getContainer(element, checkBoxSelect.CONTAINER);

	if (!container) {
		return;
	}

	if (validationManager.isValidateOnChange()) {
		if (validationManager.isInvalid(element)) {
			revalidate(container);
			return;
		}
		validate(container);
		return;
	}
	revalidate(container);
}

function isCompleteSubscriber (container) {
	return isComplete.isCompleteHelper(container, checkBoxSelect.ITEM, el => shed.isSelected(el));
}

validationManager.subscribe(validate);
isComplete.subscribe(isCompleteSubscriber);
shed.subscribe(shed.actions.SELECT, shedSubscriber);
shed.subscribe(shed.actions.DESELECT, shedSubscriber);
