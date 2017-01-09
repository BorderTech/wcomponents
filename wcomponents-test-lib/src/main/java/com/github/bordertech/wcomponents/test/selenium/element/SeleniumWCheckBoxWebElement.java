package com.github.bordertech.wcomponents.test.selenium.element;

import org.apache.commons.lang.BooleanUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a WCheckBox.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWCheckBoxWebElement extends SeleniumWComponentInputWebElement {

	/**
	 * The input type for checkbox.
	 */
	public static final String TYPE = "checkbox";

	/**
	 * The tag name for a read-only CheckBox element.
	 */
	public static final String READ_ONLY_TAG = "span";

	/**
	 * The tag name of the field.
	 */
	private final String tagName;

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWCheckBoxWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		this.tagName = element.getTagName();
		if (!tagName.equals(EDITABLE_TAG) && !tagName.equals(READ_ONLY_TAG)) {

			throw new IllegalArgumentException("element is not a WCheckBox. tag=[" + tagName + "]");
		}
	}

	/**
	 * @return true if the component is editable.
	 */
	@Override
	public boolean isEnabled() {
		return tagName.equals(EDITABLE_TAG) && super.isEnabled();
	}

	/**
	 * Gets the value of the checkbox, accounting for
	 * readOnly/editable/disabled.
	 *
	 * @return The value String, e.g. "true" or "false".
	 */
	@Override
	public String getValue() {
		if (isReadOnly()) {
			return super.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_WRAPPED_VALUE.toString());
		} else {
			return super.getValue();
		}
	}

	/**
	 * @return Whether the checkbox is checked.
	 */
	public boolean isChecked() {
		String value = getValue();
		return BooleanUtils.toBoolean(value);
	}
}
