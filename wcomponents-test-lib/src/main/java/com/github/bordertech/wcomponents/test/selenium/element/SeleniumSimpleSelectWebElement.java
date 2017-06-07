package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Selenium WebElement class representing a WSelect.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumSimpleSelectWebElement extends SeleniumWComponentWebElement {

	/**
	 * HTML tagName for the select element.
	 */
	public static final String TOP_LEVEL_TAG = "select";

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
	public SeleniumSimpleSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!(TOP_LEVEL_TAG.equals(tagName))) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
	}

	/**
	 * @return the value of the selected option, or null if no selected option.
	 */
	public String getValue() {
		WebElement selected = getSelectedOption();
		return selected == null ? "" : selected.getText();
	}

	/**
	 * @return the selected option for this select, or null if none selected.
	 */
	public WebElement getSelectedOption() {
		Select se = new Select(this);
		return se.getFirstSelectedOption();
	}

	/**
	 * @return the first option for this select.
	 */
	public WebElement getFirstOption() {
		List<WebElement> options = getOptions();
		return options.get(0);
	}

	/**
	 * @return the last option for this select.
	 */
	public WebElement getLastOption() {
		List<WebElement> options = getOptions();
		return options.get(options.size() - 1);
	}

	/**
	 * @return the Nth option for this select.
	 * @param optionNumber - the
	 */
	public SeleniumWComponentWebElement getNthOption(final int optionNumber) {
		return findElementImmediate(By.cssSelector(String.format(SELECTOR_NTH_OPTION, optionNumber)));
	}

	/**
	 * Is the component a multi-select list?
	 *
	 * @return {@code true} if the component is a multi-select
	 */
	public boolean isMultiSelect() {
		Select se = new Select(this);
		return se.isMultiple();
	}

	/**
	 *
	 * @return all options
	 */
	public List<WebElement> getOptions() {
		Select se = new Select(this);
		return se.getOptions();
	}

	/**
	 *
	 * @return all selected options
	 */
	public List<WebElement> getSelectedOptions() {
		Select se = new Select(this);
		return se.getAllSelectedOptions();
	}

	/**
	 * Get an option based on its visible text.
	 *
	 * @param optionText the text of the option we are trying to find
	 * @return the option
	 */
	public WebElement getOption(final String optionText) {
		if (optionText == null) {
			throw new SystemException("option text must not be null");
		}

		for (WebElement we : getOptions()) {
			if (optionText.equals(we.getText())) {
				return we;
			}
		}
		throw new NoSuchElementException("No option with text provided");
	}
}
