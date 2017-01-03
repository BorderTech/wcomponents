package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WTextField.
 *
 * @author Mark Reeves
 * @author Joshua Barclay
 * @since 1.2.3
 */
public class SeleniumWTextFieldWebElement extends SeleniumWComponentInputWebElement {

	private static final List<String> INPUT_ATTRIBUTES = Arrays.asList("value", "disabled", "required", "pattern", "maxlength");

	/**
	 * The tag name of the wrapping element for WTextField.
	 */
	public static final String TOP_LEVEL_TAG = "span";

	/**
	 * The type for the input field.
	 */
	public static final String TYPE = "text";

	/**
	 * Construct a SeleniumWTextFieldWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWTextFieldWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		final String tagName = element.getTagName();
		if (!tagName.equals(TOP_LEVEL_TAG)) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
	}

	/**
	 * @return the editable input field of a WTextField.
	 */
	public SeleniumWComponentWebElement getInputField() {
		if (isReadOnly()) {
			return null;
		}

		return findElement(By.tagName(EDITABLE_TAG));
	}

	/**
	 * @return the value of a WTextField.
	 */
	@Override
	public String getValue() {
		if (isReadOnly()) {
			return getText();
		}
		return getInputField().getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		if (isReadOnly()) {
			return false;
		}

		return getInputField().isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendKeys(final CharSequence... keys) {
		getInputField().sendKeys(keys);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		getInputField().clear();
	}

}
