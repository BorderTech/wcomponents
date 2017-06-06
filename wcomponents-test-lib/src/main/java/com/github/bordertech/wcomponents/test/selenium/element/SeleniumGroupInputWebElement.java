package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.Util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Group inputs include WCheckBoxSelect, WMultiTextField, WMultiDropdown, WMultiSelectPair and WRadioButtonSelect.
 * @author Mark Reeves
 */
public abstract class SeleniumGroupInputWebElement extends SeleniumWComponentWebElement {

	/**
	 * All group inputs are wrapped in a HTML fieldset element when in an interactive state.
	 */
	public static final String WRAPPER_ELEMENT = "fieldset";

	/**
	 * Create a SeleniumWComponentGroupInputWebElement instance.
	 * @param element the base WebElement
	 * @param driver the current Selenium driver
	 */
	public SeleniumGroupInputWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		String tagName = element.getTagName();
		if (!WRAPPER_ELEMENT.equalsIgnoreCase(tagName) && !isReadOnly()) {
			throw new IllegalArgumentException("Did not find expected tagName, expected " + WRAPPER_ELEMENT + " but found " + tagName);
		}
		if (!isCorrectReadOnly()) {
			throw new IllegalArgumentException("Did not find expected read-only component.");
		}
	}

	/**
	 * A constructor helper to determine if an instance is in a readOnly state and if so to ensure that it is the correct component type.
	 * @return {@code true} unless the component is in a read only state and does not have the expected component name attribute.
	 */
	private boolean isCorrectReadOnly() {
		if (isReadOnly()) {
			String ro = getRoComponent();
			if (!getROComponentName().equals(ro)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return {@code true} if the component is in a read only state.
	 */
	public final boolean isReadOnly() {
		return !Util.empty(getAttribute("data-wc-component"));
	}

	/**
	 * @return {@code true} if the input is mandatory
	 */
	public boolean isMandatory() {
		if (!isEnabled()) {
			return false;
		}
		String className = getAttribute("class");
		if (className == null) {
			return false;
		}
		return className.contains(SeleniumWComponentWebProperties.CLASS_REQUIRED.toString());
	}

	/**
	 * @return the value of the read-only signifier. Used only for sub classes to make sure they have the correct element.
	 */
	private String getRoComponent() {
		if (!isReadOnly()) {
			return null;
		}
		return getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_WRAPPED_INPUT_TYPE.toString());
	}

	/**
	 * @return the HTML tagname of the active element in an option. One of input, select or textarea.
	 */
	String getOptionTag() {
		return "input";
	}

	/**
	 * @return the value of the read-only indicator attribute for a given sub-class
	 */
	abstract String getROComponentName();

}
