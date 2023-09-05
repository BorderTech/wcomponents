import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import Observer from "wc/Observer";
import i18n from "wc/i18n/i18n";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement";
import feedback from "wc/ui/feedback";
import wcconfig from "wc/config";

/**
 * Generic client side validation manager. This is the publisher for client side validation. Any component which
 * requires custom validation subscribes to this using validationManager.subscribe.
 */

/**
 * At its heart validationManager is just an observer surrogate and this is the instance of
 * {@link module:wc/Observer} used to subscribe and publish.
 *
 * @var
 * @type {module:wc/Observer}
 * @private
 */
let observer,
	allowValidateOnChange = null,
	allowValidateOnBlur = null;

const REVALIDATE_OBSERVER_GROUP = "reval",
	invalidSelector = "[aria-invalid='true']";

/**
 * This module know when to mark things and valid/invalid but not "how".
 * Good rule of thumb, if you import "i18n" or "classList" or build DOM snippets in here you should rethink it.
 */
const validationManager = {
	/**
	 * An element is exempt from participating in client side validation if:
	 * <ol><li>the element in INPUT type hidden;</li>
	 * <li>the element is disabled; or</li>
	 * <li>the element is not 'visible' (do shed test first - it is quicker).</li>
	 * </ol>
	 *
	 * @function module:wc/ui/validation/validationManager.isExempt
	 * @param {HTMLElement} element The component to test.
	 * @returns {Boolean} true if the component is exempt from client side validation.
	 */
	isExempt: function(element) {
		let result = false;
		if (element.matches("input[type='hidden']") || shed.isDisabled(element) || shed.isHidden(element)) {
			result = true;
		}
		return result;
	},

	/**
	 * Is an element currently in an invalid state? This is used to indicate that revalidation may be needed
	 * (commonly for a change event listener). NOTE: this does not test the validity of the element, merely
	 * returns whether anything has put the element into an invalid state previously.
	 *
	 * @function module:wc/ui/validation/validationManager.isInvalid
	 * @param {HTMLElement} element The component to test for validity.
	 * @returns {Boolean} true if the element is invalid.
	 */
	isInvalid: element => element.matches(invalidSelector),

	/**
	 * Most validating components have a pretty similar mechanism to revalidate when their input changes so
	 * this helper exists to take care of it.
	 *
	 * @function module:wc/ui/validation/validationManager.revalidationHelper
	 * @param {HTMLElement} element The component being re-validated.
	 * @param {Function} _validateFunc The component's validation function.
	 * @return {Promise} When revalidation is complete.
	 */
	revalidationHelper: function(element, _validateFunc) {
		const initiallyInvalid = this.isInvalid(element);
		let isNowInvalid = initiallyInvalid;

		if (initiallyInvalid) {
			if ((_validateFunc(element))) {
				this.setOK(element);
				isNowInvalid = false;
			} else {
				isNowInvalid = true;
			}
		} else if (isMarkedOK(element)) {
			isNowInvalid = !_validateFunc(element);
		}

		if (observer && isNowInvalid !== initiallyInvalid) {  // if the current component's validity has changed
			observer.setFilter(REVALIDATE_OBSERVER_GROUP);
			return observer.notify(element);
		}
		return Promise.resolve();
	},

	/**
	 * Tests the validity of form bound elements within a specified container.
	 *
	 * @function module:wc/ui/validation/validationManager.isValid
	 * @param {HTMLElement} [container] A DOM node (preferably containing form controls). If the container is not specified finds the form
	 *   containing the activeElement (this is for use with controls with submitOnchange).
	 * @returns {Boolean} true if the container is in a valid state (all components in the container which support validation are valid).
	 */
	isValid: function (container) {
		let result = true;

		/**
		 * Observer callback function to keep track of validity from all subscribers. A container is only valid
		 * if all of its subscribers return true.
		 * @function
		 * @private
		 * @param {Boolean} decision true if valid.
		 */
		function _callback(decision) {
			result &&= decision;  // we are only valid if all observers are valid
		}

		if (!container && document.activeElement) {
			container = document.activeElement.closest("form");
		}
		if (container && observer) {
			observer.setCallback(_callback);
			observer.notify(container);
		}

		result = !!result;  // convert the potentially bitwise result to a Boolean

		return result;
	},

	/**
	 * Late initialisation callback to subscribe to shed to listen for state changes which impact any existing
	 * validation error messages.
	 * @function module:wc/ui/validation/validationManager.postInit
	 */
	postInit: function() {
		shed.subscribe(shed.actions.DISABLE, shedSubscriber);
		shed.subscribe(shed.actions.HIDE, shedSubscriber);
		shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
	},

	/**
	 * Allows a component to subscribe to client side validation.
	 * @function module:wc/ui/validation/validationManager.subscribe
	 * @see {@link module:wc/Observer#subscribe}
	 *
	 * @param {Function} subscriber The function that will be notified by validationManager. This function MUST be present at "publish" time,
	 *   but need not be present at "subscribe" time.
	 * @param {boolean} [revalidate] if truthy subscribe to revalidation rather than validation.
	 * @returns {Function} A reference to the subscriber.
	 */
	subscribe: function(subscriber, revalidate) {
		const group = revalidate ? { group: REVALIDATE_OBSERVER_GROUP } : null;
		observer = observer || new Observer();
		return observer.subscribe(subscriber, group);
	},

	/**
	 *
	 * @param {HTMLElement} element
	 * @param fallbackToken
	 * @return {string}
	 */
	getLabelText: function(element, fallbackToken) {
		const token = fallbackToken || "validation_common_unlabelledfield";
		return /** @type {string} */(getFirstLabelForElement(element, true)) ||
			element.getAttribute("aria-label") ||
			element.title ||
			i18n.get(token);
	},

	/**
	 * Updates an error box to a success box and its error box once an error is corrected.
	 *
	 * @function
	 * @public
	 * @param {HTMLElement} element the HTML element which was in an error state.
	 */
	setOK: function(element) {
		return feedback.flagSuccess({
			element: element,
			message: i18n.get("validation_ok")
		});
	},

	/**
	 * @function
	 * @public
	 * @returns {boolean} `true` if we want controls to validate when their value changes, false to validate only when submitting.
	 */
	isValidateOnChange: function() {
		if (allowValidateOnChange !== null) {
			return allowValidateOnChange;
		}
		setValidateRules();
		return allowValidateOnChange;
	},

	/**
	 * @function
	 * @public
	 * @returns {boolean} `true` if we want mandatory controls to validate when the user exits them, even if there has not been a change.
	 */
	isValidateOnBlur: function() {
		if (allowValidateOnBlur !== null) {
			return allowValidateOnBlur;
		}
		setValidateRules();
		return allowValidateOnBlur;
	}
};

/**
 * Listen for DISABLE, HIDE or OPTIONAL actions and clear any error message for the component.
 * @function
 * @private
 * @param {HTMLElement} element The element being acted upon.
 */
function shedSubscriber(element) {
	if (element && element.matches(invalidSelector)) {
		feedback.remove(element);
	}
}

/**
 * Set up the validation configuration.
 * @function
 * @private
 */
function setValidateRules() {
	const conf = wcconfig.get("validationManager", {
		"doOnChange": true,
		"doOnBlur": false
	});
	allowValidateOnChange = conf.doOnChange;
	allowValidateOnBlur = conf.doOnBlur;
}

/**
 * Indicates whether a component is associated with a message indicating that an error has been resolved.
 *
 * @function
 * @private
 * @param {HTMLElement} element The HTML element to test.
 * @returns {boolean} `true` if the element is associated with a success message.
 */
function isMarkedOK(element) {
	return !!feedback.getBox(element, feedback.LEVEL.SUCCESS);
}

export default initialise.register(validationManager);
