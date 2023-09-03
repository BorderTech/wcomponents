/**
 * Provides functionality to test if a component is "complete". What constitutes complete is dependent upon the
 * WComponent, but for most of them we have a simple test using an extension of {@link module:wc/dom/isSuccessfulElement}.
 */

import Observer from "wc/Observer";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import isSuccessfulElement from "wc/dom/isSuccessfulElement";
import validationManager from "wc/ui/validation/validationManager";

let observer;
const controlsSelector = "[name]",
	analogsSelector = "[data-wc-name][data-wc-value]",
	allSelector = `${controlsSelector}, ${analogsSelector}`,
	OBSERVER_GROUP = "completeness",
	NULL_OPTION_ATTRIBUTE = "data-wc-null";

const instance = {
	/**
	 * Allow components to subscribe to this module so that they can look after their own notions of
	 * completeness.
	 *
	 * @see {@link module:wc/Observer#subscribe}
	 * @function module:wc/ui/validation/isComplete.subscribe
	 * @public
	 * @param {Function} subscriber The function that will be notified by {@link analytics/validationManager}.
	 *    This function <strong>MUST</strong> be present at "publish" time, but need not be preset at
	 *    "subscribe" time (i.e. when this function is called).
	 * @returns {Function} subscriber A subscriber to ValidationManager, actually the subscriber argument
	 *     function.
	 */
	subscribe: function(subscriber) {
		/**
		 * @param {Function} _subscriber
		 * @return {Function}
		 */
		function _subscribe(_subscriber) {
			return observer.subscribe(_subscriber, {group: OBSERVER_GROUP});
		}

		if (!observer) {
			observer = new Observer();
			instance.subscribe = _subscribe;  // memoize
		}
		return _subscribe(subscriber);
	},

	/**
	 * A function to determine if a particular complex component is complete.  Most WAI-ARIA based and compound
	 * controls can use this helper to determine their completeness just by passing in the Widget which
	 * describes their top level component and a filter/completeness test function.
	 *
	 * @function module:wc/ui/validation/isComplete.isCompleteHelper
	 * @param {HTMLElement} container A DOM node, usually one containing components but could be the component.
	 * @param {string} widget A Widget describing the component calling this function (or a query selector string).
	 * @param {function} filter A function which returns true if an instance of the component is complete.
	 * @param {object} [theOtherThis] A reference to a "this" to pass to `Array.some` if the filter func needs this.
	 * @returns {boolean} true if complete. Note: we assume false because a component cannot be complete if the
	 *    container does not contain any of them.
	 */
	isCompleteHelper: function(container, widget, filter, theOtherThis) {
		if (!(container && widget)) {
			return false;
		}

		if (typeof filter !== "function") {  // why did you get this far?
			throw new ReferenceError("Call to isCompleteHelper without a filter function");
		}
		let widgetSelector;  // at time of writing the transition off "Widget" is incomplete
		if (Array.isArray(widget)) {
			widgetSelector = widget.map(next => next.toString()).join();
		} else {
			widgetSelector = widget.toString();
		}

		if (container.matches(widgetSelector)) {
			return isNotExempt(container) && filter(container);
		} else {
			let candidates = Array.from(container.querySelectorAll(widgetSelector));
			if (candidates.length) {
				// filter candidates to remove exempt
				candidates = candidates.filter(isNotExempt);
				// @ts-ignore
				return candidates.some(filter, theOtherThis);
			}
		}
		return false;
	},

	/**
	 * Tests is a container is complete. A container is complete if ANY of the components it contains is
	 * complete, <strong>not</strong> if all the  components it contains is complete. Determining if an
	 * element is  complete is done in two parts:
	 * <ol>
	 * <li>we do `observer.notify` for any subscribers so that WAI-ARIA role based widgets can do their thing;</li>
	 * <li>if result is still false after 1 we do a DOM based test of likely candidates.</li></ol>
	 *
	 * @function module:wc/ui/validation/isComplete.isContainerComplete
	 * @param {HTMLElement} container That which we are testing.
	 * @returns {boolean} true if the container is "complete".
	 */
	isContainerComplete: function(container) {
		let result;  // start by assuming that nothing is complete but undefined is needed too

		if (container.getAttribute("data-wc-name") && container.hasAttribute("data-wc-value")) {
			// a control may have a name analog but no value analog and still not be incomplete, weird eh? (see selectToggle)
			result = !!container.getAttribute("data-wc-value");
		} else {
			if (observer) {
				observer.setFilter(OBSERVER_GROUP);
				observer.setCallback(function(decision) {
					result = result || decision;  // we are complete if any observer is complete
				});
				observer.notify(container);
			}

			/* It is very likely that we will not get a result from the notify since few components are complex
			 * enough to need this. A few complex components and all ARIA based components have a subscriber but
			 * most components are actually rather simple.
			 */
			if (!result) {
				let candidates = getComponents(container);
				if (candidates === null) {  // nothing of interest in the container
					result = true;  // nothing in the container, must be complete
				} else if (!candidates.length) {  // empty array, so we had candidates, but they are all exempt.
					if (result === undefined) {  // no subscribers, so we have only exempt candidates
						result = true;
					}
					/* else result was explicitly false from all interested subscribers, so we can assume notComplete
					 * otherwise there would have been at least one true amongst them. */
				} else {
					candidates = Array.from(candidates);
					result = candidates.some(next => this.isComplete(next));
				}
			}
		}
		return result;
	},

	/**
	 * Determines if an element  is 'complete' i.e. it sends something other than an empty string to the server.
	 * This public method is a fit for most components. Only unusual or complex components would need to have
	 * specialised methods for determining completeness.
	 * @function module:wc/ui/validation/isComplete.isComplete
	 * @param {HTMLElement} element the element to test.
	 * @returns {boolean} true if the element is complete.
	 */
	isComplete: function(element) {
		return element.hasAttribute("name") ? isNativeComplete(element) : this.isContainerComplete(element);
	}
};

/**
 * Array filter function to include components which are not exempt from testing. Completeness testing
 * follows the same rules as validation testing, so we use the same function; an element is not exempt
 * from completeness testing if it is not exempt from validation testing but an input element of type hidden
 * must be included in completeness testing otherwise a container could be complete because it has a hidden
 * input even if none of the actual user controls are complete.
 * @function
 * @private
 * @param {HTMLElement} candidate The element we are testing for exemption from completeness testing.
 * @returns {boolean} true if the element is not exempt from the completeness test.
 */
function isNotExempt(candidate) {
	let result;
	/* input of type hidden is exempt from validation but is not allowed to determine that a container is complete. */
	if (candidate.matches("input[type='hidden']")) {
		result = true;
	} else {
		// remove any elements which are exempt from validation as these are also exempt from completeness tests
		result = !validationManager.isExempt(candidate);
	}
	return result;
}

/**
 * Gets potential candidates for a completeness test after running a test of all completeness subscribers.
 * Anything which extends ariaAnalog is a completeness subscriber, so what we are left with really are
 * serializable form controls.
 *
 * @function
 * @private
 * @param {HTMLElement} container The place to look for candidates.
 * @returns {HTMLElement[]} If not null an array of elements (<strong>not</strong> a node list).
 */
function getComponents(container) {
	let result;

	/*
	 * NOTE:
	 * NEVER pass in an analogs in the direct test because you WILL end up in an infinite loop. It is also
	 * unnecessary since if we are testing a specific component it will have been through the analog tests
	 * already.
	 */
	if (container.matches(controlsSelector)) {
		result = [container];
	} else {
		result = container.querySelectorAll(allSelector);
	}

	if (result && result.length) {
		result = Array.prototype.filter.call(result, isNotExempt);
	} else {
		result = null;
	}
	return result;
}

/**
 * Tests if an element with support for the HTML required attribute is'complete' the determination of which
 * depends on the element being tested.
 *
 * @function
 * @private
 * @param {HTMLElement} element A component with native support for the "required" attribute.
 * @returns {boolean} true if complete.
 */
function isNativeComplete(element) {
	let result;

	if (element.matches("input[type='radio']")) {
		// isSuccessfulElement is insufficient for radio buttons which could be all over the place
		const allInGroup = getFilteredGroup(element);
		result = Array.isArray(allInGroup) && allInGroup.length === 1;
		if (result && allInGroup[0].getAttribute(NULL_OPTION_ATTRIBUTE)) {
			result = false;
		}
		return result;
	}
	result = isSuccessfulElement(element);
	if (result) {
		/* if isSuccessfulElement returns false we know the control is not complete */
		if (element instanceof HTMLSelectElement && element.matches("select:not([multiple])")) {
			const option = element.options[element.selectedIndex];
			if (option && option.getAttribute(NULL_OPTION_ATTRIBUTE)) {
				result = false;
			}
			return result;
		}
		if (element.matches("input[type='hidden']")) {
			return false;
		}
		if ((element.matches("input:not([type='checkbox'])")) || element.matches("textarea")) {
			result = !!(element["value"]);
		}
	}
	return result;
}

export default instance;
