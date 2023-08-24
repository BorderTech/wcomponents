import initialise from "wc/dom/initialise";

const wrapperSelector = ".wc-input-wrapper";
const roWrapperSelector = ".wc-ro-input";
const inputSelector = "input";
const selectSelector = "select";
const textareaSelector = "textarea";
const wrappedSelectors = [inputSelector, selectSelector, textareaSelector].join();
const selectors = [wrapperSelector, roWrapperSelector];
const idSuffix = "_input";
const wrappedInput = {};

// Removing these public methods as they appear unused
// wrappedInput.getWidget = () => wrapperSelector;
// wrappedInput.getWidgets = ()  => selectors;
// wrappedInput.getWrappedWidgets = () => wrappedSelectors;
// wrappedInput.getROWidget = () => roWrapperSelector;

/**
 * Am I a wrapped input?
 * @param {HTMLElement} element
 * @param {boolean} [inclReadOnly]
 * @return {boolean}
 */
wrappedInput.isOneOfMe = function(element, inclReadOnly) {
	const selector = inclReadOnly ? selectors.join() : wrapperSelector;
	return element.matches(selector);
};

/**
 *
 * @param {HTMLElement} element
 * @return {boolean}
 */
wrappedInput.isReadOnly = function(element) {
	return element.matches(roWrapperSelector);
};

/**
 *
 * @param {HTMLElement} element
 * @return {HTMLElement|null}
 */
wrappedInput.getInput = function(element) {
	if (!(element && element.matches(wrapperSelector))) {
		return null;
	}
	return element.querySelector(wrappedSelectors);
};

/**
 * Determine if this is a wrappedInput.
 * @param {HTMLElement} element
 * @return {boolean} true if it is a wrapped input.
 */
wrappedInput.isWrappedInput = function(element) {
	return element.parentElement.matches(wrapperSelector);
};

/**
 * Gets the wrapper for a wrapped input.
 * @param {HTMLElement} element
 * @return {HTMLElement|null}
 */
wrappedInput.getWrapper = function(element) {
	const { parentElement } = element;
	return parentElement.matches(wrapperSelector) ? parentElement : null;
};

/**
 * Get all the wrapped inputs in this container.
 * @param {HTMLElement} container
 * @param {boolean} [inclReadOnly]
 * @return {HTMLElement[]}
 */
wrappedInput.get = function (container, inclReadOnly) {
	const widgetsSelector = selectors.join();
	const selector = inclReadOnly ? widgetsSelector : wrapperSelector;
	const isInput = container.matches(selector);
	if (isInput) {
		return [container];
	}
	const result = container.querySelectorAll(selector);
	return /** @type {HTMLElement[]} */ Array.from(result);
};

/**
 *
 * @param {HTMLElement} element
 * @return {string|null}
 */
wrappedInput.getWrappedId = function(element) {
	const { id } = element;
	if (this.isReadOnly(element) || element.matches(wrapperSelector)) {
		return id.concat(idSuffix);
	}
	const wrapper = this.getWrapper(element);
	if (wrapper) {
		if (element.matches(wrappedSelectors)) {
			return element.id;
		}
		return (wrapper.id).concat(idSuffix);
	}
	return null;
};

export default initialise.register(wrappedInput);
