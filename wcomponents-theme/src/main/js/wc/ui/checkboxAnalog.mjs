import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import formUpdateManager from "wc/dom/formUpdateManager";

const BUTTON_VAL_ATTRIB = "value",
	BUTTON_NAME_ATTRIB = "name";

/**
 * Module to provide an ARIA role of checkbox with useful functionality. That is: to make something which is not a
 * checkbox behave like a checkbox based on its role: http://www.w3.org/TR/wai-aria-practices/#checkbox.
 * Strictly speaking checkbox should not get arrow key navigation.
 *
 * @constructor
 * @private
 */
function CheckboxAnalog() {

	/**
	 * The description of a group item. This makes this class concrete.
	 * @var
	 * @type {string}
	 * @public
	 * @override
	 */
	this.ITEM = "[role='checkbox']";

	/**
	 * Custom controls have to report their state.
	 * @function
	 * @public
	 * @override
	 * @param {Element} form The form or subform which state we are writing.
	 * @param {Element} container The container to which state is written.
	 */
	this.writeState = function(form, container) {
		const checkboxes = Array.from(form.querySelectorAll(this.ITEM.toString()));
		checkboxes.forEach(next => {
			if (!shed.isDisabled(next)) {
				const name = next.hasAttribute(this.VALUE_ATTRIB) ? "data-wc-name" : BUTTON_NAME_ATTRIB;
				const val = next.hasAttribute(this.VALUE_ATTRIB) ? this.VALUE_ATTRIB : BUTTON_VAL_ATTRIB;
				formUpdateManager.writeStateField(container, next.getAttribute(name), shed.isSelected(next) ? next.getAttribute(val) : "");
			}
		});
	};
}

CheckboxAnalog.prototype = ariaAnalog;

const instance = new CheckboxAnalog();
instance.constructor = CheckboxAnalog;

export default initialise.register(instance);
