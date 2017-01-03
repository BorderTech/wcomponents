package com.github.bordertech.wcomponents.test.selenium.element;

/**
 * An enum of commonly applied HTML attribute values for use in WComponents
 * Selenium testing.
 *
 * @author Mark Reeves
 * @since 1.2.3
 */
public enum SeleniumWComponentWebProperties {
	/**
	 * The HTML attribute which holds the minLength property of an Input
	 * WComponent.
	 */
	ATTRIBUTE_MIN_LENGTH("data-wc-minlength"),
	/**
	 * HTML attribute which holds the accessibleText property of a
	 * WComponent.
	 */
	ATTRIBUTE_ACCESSIBLE_TEXT("aria-label"),
	/**
	 * HTML attribute which holds the ID of the element that a label is
	 * 'for'.
	 */
	ATTRIBUTE_LABEL_FOR("for"),
	/**
	 * HTML attribute which holds the ID of the element that a label is
	 * 'for' when the label is for a read-only element.
	 */
	ATTRIBUTE_LABEL_FOR_READ_ONLY("data-wc-rofor"),
	/**
	 * HTML attribute which holds an Input WComponent's name if the
	 * component is not a native HTML form-bound element.
	 */
	ATTRIBUTE_WRAPPED_NAME("data-wc-name"),
	/**
	 * HTML attribute which holds an Input WComponent's value if the
	 * component is not a native HTML form-bound element.
	 */
	ATTRIBUTE_WRAPPED_VALUE("data-wc-value"),
	/**
	 * HTML attribute which holds the type of input if the component is not
	 * a native HTML form-bound element.
	 */
	ATTRIBUTE_WRAPPED_INPUT_TYPE("data-wc-component"),
	/**
	 * HTML attribute which holds the input's value when it is native HTML.
	 */
	ATTRIBUTE_HTML_VALUE("value"),
	/**
	 * Common HTML class attribute value applied to an Input WComponent in a
	 * read-only state.
	 */
	CLASS_READ_ONLY("wc_ro");

	/**
	 * The attribute itemValue for items.
	 */
	private final String itemValue;

	/**
	 * Instantiate an item in the enum.
	 *
	 * @param value the attribute itemValue to apply to the item
	 */
	SeleniumWComponentWebProperties(final String value) {
		this.itemValue = value;
	}

	/**
	 * @return the item value of the enum item.
	 */
	public String toString() {
		return itemValue;
	}
}
