package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing a WSelect.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWSelectWebElement extends SeleniumWComponentWebElement {

	/**
	 * The table itself is a 'table' entity, but the element containing all the controls is the wrapper div.
	 */
	public static final String TAG_NAME = "select";

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
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String elementTag = element.getTagName();

		if (!elementTag.equals(TAG_NAME)) {

			throw new SystemException("Incorrect element selected for SeleniumWSelectWebElement. Expected select but found: " + elementTag);
		}
	}

	/**
	 * @return the selected option for this select.
	 */
	public SeleniumWComponentWebElement getSelectedOption() {
		return findElement(By.cssSelector(SELECTOR_SELECTED_OPTION));
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
}
