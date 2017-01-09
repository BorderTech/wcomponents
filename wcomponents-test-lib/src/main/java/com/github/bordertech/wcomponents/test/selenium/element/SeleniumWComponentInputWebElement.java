package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
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
	 * @return the value of the input component.
	 */
	public String getValue() {
		return super.getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString());
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
}
