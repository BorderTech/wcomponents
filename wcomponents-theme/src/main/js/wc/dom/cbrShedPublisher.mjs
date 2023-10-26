import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";

const radioSelector = "input[type='radio']";
const checkboxSelector = "input[type='checkbox']";
const selectors = [radioSelector, checkboxSelector];

/**
 * Provides a mechanism to publish {@link module"wc/dom/shed"} events on native selectable controls (radio buttons and
 * check boxes) which are not intrinsically 'sheddy'. This allows us to use shed subscribers to do stuff on select/
 * deselect of these native controls as if they were ARIA widgets.
 *
 */
const instance = {
	/**
	 * Set up event listeners in initialisation.
	 *
	 * @function
	 * @alias module:wc/dom/cbrShedPublisher.initialise
	 * @param {Element} element The element being initialised: usually document.body.
	 */
	initialise: function(element) {
		event.add(element, "change", changeEvent);
	},

	/**
	 * Get the Widget(s) to describe a checkbox, radio button or both.
	 * @param {String} [whichOne] which widget to get:
	 *    "cb" will fetch the CHECKBOX widget;
	 *    "r" will fetch the RADIO widget;
	 *    anything else will fetch an array containing both.
	 * @returns {string|string[]}
	 */
	getWidget: function(whichOne) {
		switch (whichOne) {
			case "cb":
				return checkboxSelector;
			case "r":
				return radioSelector;
			default:
				return selectors;
		}
	}
};

/**
 * NOTE: this may not work in IE < 11 but has been tested in IE 11 and it is fine there:
 * changeEvents on checkboxes and radios fire reliably when the state changes, not when focus is lost.
 * @function
 * @private
 * @param {Event} $event a wrapped change event
 */
function changeEvent($event) {
	const target = $event.target;
	if (target && target.matches(selectors.join())) {
		const action = shed.isSelected(target) ? shed.actions.SELECT : shed.actions.DESELECT;
		shed.publish(target, action);
	}
}

export default initialise.register(instance);
