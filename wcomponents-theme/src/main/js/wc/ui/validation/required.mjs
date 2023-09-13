import isComplete from "wc/ui/validation/isComplete.mjs";
import i18n from "wc/i18n/i18n.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import feedback from "wc/ui/feedback.mjs";

const instance = {
	/**
	 * @constant {object}  module:wc/ui/validation/required.CONSTRAINTS Indicates how mandatory-ness
	 * is determined for a particular component one of the aria-required attribute, a className or the required
	 * attribute.
	 * @property {string} ARIA Use aria-required.
	 * @property {string} CLASSNAME Use a className as a required indicator.
	 * @property {string} ATTRIB Use the required attribute.
	 */
	CONSTRAINTS: {
		ARIA: "aria",
		CLASSNAME: "classname",
		ATTRIB: "attrib"
	},

	/**
	 * the majority of components required validation is all the same: a component is required or aria-required,
	 * it is incomplete, it gets a standard message and the flag is applied to the element "afterEnd".
	 *
	 * @function module:wc/ui/validation/required.doItAllForMe
	 * @param {Element} container the container being validated.
	 * @param {string} widget the descriptor of the component being tested.
	 * @param {Boolean} [useAria] set true to use aria-required as the indicator of mandatory-ness, otherwise
	 *    use required attribute.
	 * @returns {Boolean} true if the container is valid.
	 */
	doItAllForMe: function(container, widget, useAria) {
		let elements = this.getRequired(container, widget, useAria),
			result = true;
		// just get the failures for flagging
		elements = elements.filter(isNotComplete);
		if (elements?.length) {
			result = false;
			flagAllThese(elements);
		}
		return result;
	},

	/**
	 * A helper for doing all the required validation but allowing individual components to set a lot of
	 * optional parameters.
	 *
	 * @function module:wc/ui/validation/required.complexValidationHelper
	 * @param {module:wc/ui/validation/required~config} obj Configuration parameters.
	 * @returns {Boolean} true if `obj.container` is valid.
	 */
	complexValidationHelper: function(obj) {
		let result = true;
		const widget = obj.widget,
			container = obj.container,
			constraint = obj.constraint,
			filterFunc = obj.filter || isNotComplete,
			flagFunc = obj.flag || flagAllThese;

		if (widget && container) {
			const elements = this.getRequired(container, widget, constraint).filter(filterFunc);
			if (elements?.length) {
				result = false;
				flagFunc(elements, obj);
			}
		}
		return result;
	},

	/**
	 * Helper for re-validating a single control which is already in an invalid state. This is used for
	 * changeEvent based revalidation. This function assumes that the calling function has called
	 * <code>validationManager.isInvalid</code> for element but is not dependent on that. It is just better
	 * practice to do so before doing any further revalidation, but you could turn on in-context validation for
	 * all change events by not doing that test.
	 *
	 * @function module:wc/ui/validation/required.revalidate
	 * @param {Element} element The element to re-validate.
	 * @param {module:wc/ui/validation/required~config} config Configuration parameters.
	 */
	revalidate: function (element, config) {
		const result = isComplete.isComplete(element);
		if (!result) {
			flagAllThese([element], config);
		}
		return result;
	},

	/**
	 * Gets all required instances of a given Widget in a container.
	 *
	 * @function module:wc/ui/validation/required.getRequired
	 * @param {Element} container Where to look (we look inside, container doesn't count).
	 * @param {string} widget A Widget describing the type of component for which we are looking (or a query selector).
	 * @param {module:wc/ui/validation/required.CONSTRAINTS} [requiredConstraint] Sets the required constraint if not using the required attribute.
	 * @returns {HTMLElement[]} Will return an empty array if there are no required components in the container(including the container itself).
	 */
	getRequired: function(container, widget, requiredConstraint) {
		let selector,
			result,
			exObj;

		/**
		 * Array map function to extend the original widget to add the necessary required constraints.
		 *
		 * @function
		 * @private
		 * @param {string} nextWidget The widget we are extending.
		 * @return {string}
		 */
		function _mapFn(nextWidget) {
			let extendedWidget = nextWidget.toString();  // if no extension just return itself
			if (exObj) {
				extendedWidget += exObj;
			}
			return extendedWidget;
		}

		switch (requiredConstraint) {
			case this.CONSTRAINTS.ARIA:
				exObj = "[aria-required='true']";
				break;
			case this.CONSTRAINTS.CLASSNAME:
				exObj = ".wc_req";
				break;
			default:
				exObj = "[required]";
				break;
		}

		if (Array.isArray(widget)) {
			selector = widget.map(_mapFn).join();  // selector is now a comma separated list of extended selectors
		} else {
			selector = _mapFn(widget);  // selector is a single selector
		}

		if (selector) {
			if (container.matches(selector)) {
				result = [container];
			} else {
				result = container.querySelectorAll(selector);
			}
		}
		if (result) {
			result = Array.prototype.filter.call(result, (next) => !validationManager.isExempt(next));
		}
		return result || [];
	}
};

/**
 * Get the required field message for flagging a required field in an error state.
 *
 * @function
 * @private
 * @param {Element} element The element (component) with the error.
 * @returns {String} A formatted error message.
 */
function getRequiredMessage(element) {
	return i18n.get("validation_common_incomplete", validationManager.getLabelText(element));
}

/**
 * Common helper to add an error message indicator to each of an array of components.
 * @function
 * @private
 * @param {Element[]} elements An array of elements in an invalid state.
 * @param {module:wc/ui/validation/required~config} [config] Configuration object.
 */
function flagAllThese(elements, config) {
	const messageFunc = (config && config.messageFunc) ? config.messageFunc : getRequiredMessage;

	Array.prototype.forEach.call(elements, next => {
		feedback.flagError({ element: next, message: messageFunc(next)});
	});
}

/**
 * Determines if a given element is not 'complete' and therefore fails a mandatory test.
 *
 * @function
 * @private
 * @param {Element} element A form control or aria surrogate.
 * @returns {Boolean} true if not complete.
 */
function isNotComplete(element) {
	return !isComplete.isComplete(element);
}

/**
 * This is a set of helpers for required field validation. The actual validation should be handed off to individual
 * components. There are a lot of similarities though, so I have included a few public functions which will suffice for
 * all required testing for most components.
 *
 */
export default instance;

/**
 * Configuration object for several functions.
 * @typedef {Object} module:wc/ui/validation/required~config
 * @property {HTMLElement} container The container being validated.
 * @property {string} widget The description of the component we are currently testing.
 * @property {Function} [filter] A function to call to test for completeness, defaults to
 *    {@link module:wc/ui/validation/required~isNotComplete}.
 * @property {Function} [flag] A function to set the error message box. Defaults to
 *    {@link module:wc/ui/validation/required~flagAllThese}
 * @property {Function} [messageFunc] A function to get the error message. Defaults to
 *    {@link module:wc/ui/validation/required~getRequiredMessage}.
 * @property {module:wc/ui/validation/required.CONSTRAINTS} constraint
 */
