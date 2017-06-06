package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WTextArea.
 *
 * @author Joshua Barclay
 * @author Mark Reeves
 * @since 1.2.0
 */
public class SeleniumWTextAreaWebElement extends SeleniumWComponentInputWebElement {

	/**
	 * The HTML attribute used as for maxlength in WTextArea.
	 */
	private static final String ATTR_MAX_LENGTH = "data-wc-maxlength";

	/**
	 * HTML attributes applied to the input element.
	 */
	private static final List<String> INPUT_ATTRIBUTES = Arrays.asList("disabled", "required", "minlength", ATTR_MAX_LENGTH);

	/**
	 * Read-only element wrapper tag.
	 */
	public static final String READ_ONLY_TAG = "pre";
	/**
	 * RTF read-only element wrapper tag.
	 */
	public static final String RTF_READ_ONLY_TAG = "div";
	/**
	 * Editable element tag.
	 */
	public static final String TEXTAREA_TAG = "textarea";

	/**
	 * Construct a WTextAreaWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWTextAreaWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!(tagName.equals(TOP_LEVEL_TAG) || tagName.equals(READ_ONLY_TAG) || tagName.equals(RTF_READ_ONLY_TAG))) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
	}

	/**
	 * WTextArea's line breaks will be in Java format "\n whereas HTML textarea's value has line breaks in the HTML
	 * specified format "\r\n".
	 *
	 * @return the value of the attribute
	 */
	@Override
	public String getValue() {
		return super.getValue().replaceAll("\r\n", "\n");
	}

	/**
	 * Some attributes are applied to the wrapper, some to the input. This override sorts out which is which.
	 *
	 * @param name the name of the attribute to find
	 * @return the value of the attribute
	 */
	@Override
	public String getAttribute(final String name) {
		if (SeleniumWComponentWebProperties.ATTRIBUTE_MAX_LENGTH.toString().equals(name)) {
			return getInputField().getAttribute(ATTR_MAX_LENGTH);
		}
		if (INPUT_ATTRIBUTES.contains(name)) {
			if (isReadOnly()) {
				return null;
			}
			return getInputField().getAttribute(name);
		}
		return super.getAttribute(name);
	}

	/**
	 * @return the value of the max length attribute or 0 if not set
	 */
	public int getMaxLength() {
		String maxLength = getAttribute(ATTR_MAX_LENGTH);
		if (Util.empty(maxLength)) {
			return 0;
		}
		try {
			return Integer.parseInt(maxLength);
		} catch (NumberFormatException ex) {
			throw new SystemException("Unexpected non-integer value of maxLength.", ex);
		}
	}

	/**
	 * @return the editable input field of a WTextField.
	 */
	@Override
	public SeleniumWComponentWebElement getInputField() {
		if (isReadOnly()) {
			return null;
		}

		return findElementImmediate(By.tagName(TEXTAREA_TAG));
	}

	/**
	 * Clear the content of a WTextArea.
	 */
	public void clearContent() {
		WebElement input = getInputField();
		input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		input.sendKeys(Keys.chord(Keys.COMMAND, "a"));
		input.sendKeys(Keys.DELETE);
		input.sendKeys(Keys.BACK_SPACE);
	}
}
