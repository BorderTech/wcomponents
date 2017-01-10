package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a WSelect.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWSelectWebElement extends SeleniumWComponentInputWebElement {
	/**
	 * HTML attributes applied to the input element.
	 */
	private static final List<String> INPUT_ATTRIBUTES = Arrays.asList("disabled", "required");

	/**
	 * The CSS Selector for a selected option.
	 */
	public static final String SELECTOR_SELECTED_OPTION = "option[selected]";

	/**
	 * The CSS Selector for the first option.
	 */
	public static final String SELECTOR_FIRST_OPTION = "option:first-of-type";

	/**
	 * The CSS Selector for the last option.
	 */
	public static final String SELECTOR_LAST_OPTION = "option:last-of-type";

	/**
	 * The CSS Selector for the Nth option.
	 */
	public static final String SELECTOR_NTH_OPTION = "option:nth-child(%s)";

	/**
	 * HTML tagName for select elements.
	 */
	public static final String SELECT_TAG = "select";

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!tagName.equals(TOP_LEVEL_TAG)) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
	}

	/**
	 * @return the value of the selected option, or null if no selected
	 * option.
	 */
	@Override
	public String getValue() {
		if (isReadOnly()) {
			return getText();
		}

		SeleniumWComponentWebElement selected = getSelectedOption();
		return selected == null ? null : selected.getText();
	}

	/**
	 * @return the selected option for this select, or null if none
	 * selected.
	 */
	public SeleniumWComponentWebElement getSelectedOption() {
		try {
			return getInputField().findElement(By.cssSelector(SELECTOR_SELECTED_OPTION));
		} catch (final NoSuchElementException nsee) {
			//There is not a selected element - return null.
			return null;
		}
	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getFirstOption() {
		return getInputField().findElement(By.cssSelector(SELECTOR_FIRST_OPTION));
	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getLastOption() {
		return getInputField().findElement(By.cssSelector(SELECTOR_LAST_OPTION));
	}

	/**
	 * @return the Nth option for this select.
	 * @param optionNumber - the
	 */
	public SeleniumWComponentWebElement getNthOption(final int optionNumber) {
		return getInputField().findElement(By.cssSelector(String.format(SELECTOR_NTH_OPTION, optionNumber)));
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

	@Override
	public SeleniumWComponentWebElement getInputField() {
		if (isReadOnly()) {
			return null;
		}
		return findElement(By.tagName(SELECT_TAG));
	}
}
