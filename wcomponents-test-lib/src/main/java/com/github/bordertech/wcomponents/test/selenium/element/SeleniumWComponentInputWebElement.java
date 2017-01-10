package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Parent class for all elements that represent user input. An element might be
 * an input even if it represent read-only state, e.g. a &lt;span&gt; element
 * for a read-only text input.
 *
 * @author Joshua Barclay
 * @since 1.3.0
 */
public class SeleniumWComponentInputWebElement extends SeleniumWComponentWebElement {
	/**
	 * The tag name of the wrapping element for WTextField.
	 */
	public static final String TOP_LEVEL_TAG = "span";

	/**
	 * The tag name of the editable CheckBox element.
	 */
	public static final String EDITABLE_TAG = "input";

	/**
	 * Construct an input element.
	 *
	 * @param element the web element.
	 * @param driver the driver.
	 */
	public SeleniumWComponentInputWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
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
	 * @return the value of the input component.
	 */
	public String getValue() {
		if (isReadOnly()) {
			return getText();
		}
		return getInputField().getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString());
	}

	/**
	 * Retrieve the given attribute for the element, but if the attribute
	 * requested is the value instead return the result of getValue().
	 *
	 * @param name the attribute to find
	 * @return the value of the attribute
	 */
	@Override
	public String getAttribute(final String name) {
		if (SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString().equals(name)) {
			return getValue();
		}
		return super.getAttribute(name);
	}

	/**
	 * @return true if the field is in a read-only state.
	 */
	public boolean isReadOnly() {
		String className = getAttribute("class");
		if (null == className) {
			return false;
		}
		List<String> classAsList = Arrays.asList(className.split("\\s"));
		return classAsList.contains(SeleniumWComponentWebProperties.CLASS_READ_ONLY.toString());
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

	@Override
	public String getActiveId() {
		if (isReadOnly()) {
			return super.getActiveId();
		}
		return getInputField().getAttribute("id");
	}


}
