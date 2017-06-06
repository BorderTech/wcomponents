package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a WCheckBox.
 *
 * @author Joshua Barclay
 * @author Mark Reeves
 * @since 1.2.0
 */
public class SeleniumWCheckBoxWebElement extends SeleniumWComponentInputWebElement {

	/**
	 * HTML attributes applied to the input element.
	 */
	private static final List<String> INPUT_ATTRIBUTES = Arrays.asList("disabled", "required", "checked");

	/**
	 * The input type for checkbox.
	 */
	public static final String TYPE = "checkbox";

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWCheckBoxWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!tagName.equals(TOP_LEVEL_TAG)) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
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
		return isSelected();
	}

	@Override
	public boolean isSelected() {
		if (isReadOnly()) {
			String className = getAttribute("class");
			if (null == className) {
				return false;
			}
			List<String> classAsList = Arrays.asList(className.split("\\s"));
			return classAsList.contains(SeleniumWComponentWebProperties.CLASS_READONLY_CHECKED.toString());
		}
		return getInputField().isSelected();
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
