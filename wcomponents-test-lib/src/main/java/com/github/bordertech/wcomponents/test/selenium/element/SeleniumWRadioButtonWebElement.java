package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import org.apache.commons.lang.BooleanUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a single Radio button element.
 *
 * @author Joshua Barclay
 * @since 1.3.0
 */
public class SeleniumWRadioButtonWebElement extends SeleniumWComponentInputWebElement {

	/**
	 * The type of this input when editable.
	 */
	public static final String TYPE = "radio";

	/**
	 * The tag name for a read-only select element.
	 */
	public static final String READ_ONLY_TAG = "span";

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWRadioButtonWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String elementTag = element.getTagName();

		if (!elementTag.equals(EDITABLE_TAG) && !elementTag.equals(READ_ONLY_TAG)) {

			throw new SystemException("Incorrect element selected for SeleniumWRadioButtonWebElement. Expected " + EDITABLE_TAG + " or " + READ_ONLY_TAG + " but found: " + elementTag);
		}
	}

	/**
	 * @return the value of the selected option, or null if no selected
	 * option.
	 */
	@Override
	public String getValue() {
		if (isReadOnly()) {
			return super.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_WRAPPED_VALUE.toString());
		}

		return BooleanUtils.toStringTrueFalse(isSelected());
	}

}
