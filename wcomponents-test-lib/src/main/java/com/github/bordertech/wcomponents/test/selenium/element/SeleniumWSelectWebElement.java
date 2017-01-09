package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
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
	 * The select element's tag name.
	 */
	public static final String TAG_NAME = "select";

	/**
	 * The tag name for a read-only select element.
	 */
	public static final String READ_ONLY_TAG = "span";

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
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String elementTag = element.getTagName();

		if (!elementTag.equals(TAG_NAME) && !elementTag.equals(READ_ONLY_TAG)) {

			throw new SystemException("Incorrect element selected for SeleniumWSelectWebElement. Expected " + TAG_NAME + " or " + READ_ONLY_TAG + " but found: " + elementTag);
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
			return findElement(By.cssSelector(SELECTOR_SELECTED_OPTION));
		} catch (final NoSuchElementException nsee) {
			//There is not a selected element - return null.
			return null;
		}

	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getFirstOption() {
		return findElement(By.cssSelector(SELECTOR_FIRST_OPTION));
	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getLastOption() {
		return findElement(By.cssSelector(SELECTOR_LAST_OPTION));
	}

	/**
	 * @return the Nth option for this select.
	 * @param optionNumber - the
	 */
	public SeleniumWComponentWebElement getNthOption(final int optionNumber) {
		return findElement(By.cssSelector(String.format(SELECTOR_NTH_OPTION, optionNumber)));
	}

}
