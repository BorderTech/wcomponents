/**
 * Module to provide an ARIA role of radio with useful functionality. That is: to make something which is not a radio
 * button behave like radio button based on its role: http://www.w3.org/TR/wai-aria-practices/#radiobutton.
 *
 * This is primarily concerned with selection and navigation almost all of which is in the super class.
 *
 *<ul><li>Up Arrow and Left Arrow move forward in the group</li>
 *<li>Down arrow and Right Arrow move backwards in the group. When the arrow moves focus, the button is selected.</li>
 *<li>Down Arrow at bottom should wrap to top.</li>
 *<li>Up Arrow at top should wrap to bottom.</li>
 *<li>Space selects the radio button with focus and de-selects other radio buttons in the group.</li>
 *<li>Ctrl/Meta+Arrow moves through the options without updating content or selecting the button.</li></ul>
 *
 */

import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";

RadioAnalog.prototype = ariaAnalog;
const instance = new RadioAnalog();
instance.constructor = RadioAnalog;

/**
 * @constructor
 * @alias module:wc/ui/radioAnalog~RadioAnalog
 * @extends module:wc/dom/ariaAnalog~AriaAnalog
 * @private
 */
function RadioAnalog() {
	/**
	 * The description of a group item. This makes this class concrete.
	 * @var
	 * @type {string}
	 * @public
	 * @override
	 */
	this.ITEM = "[role='radio']";


	/**
	 * Select items immediately on navigation.
	 * @function
	 * @protected
	 * @returns {Boolean} always true for this analog.
	 * @override
	 */
	this.selectOnNavigate = function() {
		return true;
	};

	/**
	 * The selection mode is single.
	 * @var
	 * @protected
	 * @type {number}
	 * @override
	 */
	this.exclusiveSelect = this.SELECT_MODE.SINGLE;
}

initialise.register(instance);
export default instance;
