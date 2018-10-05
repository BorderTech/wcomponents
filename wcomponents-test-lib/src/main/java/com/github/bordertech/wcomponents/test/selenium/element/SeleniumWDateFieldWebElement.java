package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The Selenium WDateField web element.
 */
public class SeleniumWDateFieldWebElement extends SeleniumWComponentInputWebElement {

	/**
	 * HTML attributes applied to the input element.
	 */
	private static final List<String> INPUT_ATTRIBUTES =
		Arrays.asList("disabled", "required", "aria-describedby");

	/**
	 * The starting component for the date field.
	 */
	public static final String TOP_STANDARD_LEVEL_TAG = "div";

	/**
	 * The starting component for the date field.
	 */
	public static final String TOP_READONLY_LEVEL_TAG = "time";

	/**
	 * Construct a SeleniumWTextFieldWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWDateFieldWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		String elementClass = element.getAttribute("class");
		if (!tagName.equals(TOP_STANDARD_LEVEL_TAG) && !tagName.equals(TOP_READONLY_LEVEL_TAG)) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		} else if (!elementClass.contains("wc-datefield")) {
			throw new IllegalArgumentException("Element is not the expected ctyle class. class=[" + elementClass + "].");
		}
	}

	/**
	 * Some attributes are applied to the wrapper, some to the input. This
	 * override sorts out which is which.
	 *
	 * @param name the name of the attribute to find
	 * @return the value of the attribute
	 */
	@Override
	public String getAttribute(final String name) {
		if (INPUT_ATTRIBUTES.contains(name)) {
			if (isReadOnly()) {
				return null;
			}
			return getInputField().getAttribute(name);
		}
		return super.getAttribute(name);
	}
}
