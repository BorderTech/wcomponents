package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WEmailField.
 *
 * @author Mark Reeves
 * @since 1.2.3
 */
public class SeleniumWEmailFieldWebElement extends SeleniumWTextFieldWebElement {

	/**
	 * HTML attributes applied to the input element.
	 */
	private static final List<String> INPUT_ATTRIBUTES = Arrays.asList("disabled", "required", "maxlength", "minlength");
	/**
	 * The type of this input component.
	 */
	public static final String TYPE = "email";

	/**
	 * Construct a SeleniumWEmailFieldWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWEmailFieldWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!tagName.equals(TOP_LEVEL_TAG)) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
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
