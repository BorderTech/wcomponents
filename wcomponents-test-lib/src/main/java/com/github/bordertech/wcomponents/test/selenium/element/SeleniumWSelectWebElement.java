package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Selenium WebElement class representing a WSelect.
 *
 * @author Joshua Barclay
 * @author Mark Reeves
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
	 * HTML tagName for multi-select components in a read-only state.
	 */
	public static final String RO_MULTI_SELECT_TAG = "ul";

	/**
	 * HTML tagName for the options in a multi-select component in a read-only state.
	 */
	public static final String RO_MULTI_OPTION_TAG = "li";

	/**
	 * Default constructor for this element.
	 *
	 * @param element the WebElement
	 * @param driver the driver.
	 */
	public SeleniumWSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);

		final String tagName = element.getTagName();
		if (!(TOP_LEVEL_TAG.equals(tagName) || RO_MULTI_SELECT_TAG.equals(tagName))) {
			throw new IllegalArgumentException("Element is not the expected wrapper. tag=[" + tagName + "].");
		}
	}

	/**
	 * @return the value of the selected option, or null if no selected option.
	 */
	@Override
	public String getValue() {
		WebElement selected = getSelectedOption();
		return selected == null ? "" : selected.getText();
	}

	/**
	 * @return the selected option for this select, or null if none selected.
	 */
	public WebElement getSelectedOption() {
		if (isReadOnly()) {
			if (isMultiSelect()) {
				try {
					return findElementImmediate(By.tagName(RO_MULTI_OPTION_TAG));
				} catch (final NoSuchElementException nsee) {
					//There is not a selected element - return null.
					return null;
				}
			}
			String text = getText();
			if (Util.empty(text)) {
				return null;
			}
			return this;
		}

		SeleniumWComponentWebElement input = getInputField();
		Select se = new Select(input);
		try {
			return se.getFirstSelectedOption();
			//return getInputField().findElement(By.cssSelector(SELECTOR_SELECTED_OPTION));
		} catch (final NoSuchElementException nsee) {
			//There is not a selected element - return null.
			return null;
		}
	}

	/**
	 * @return the Nth option for this select.
	 * @param optionNumber - the option - 0 indexed (unlike CSS which is 1 indexed)
	 */
	public SeleniumWComponentWebElement getNthOption(final int optionNumber) {
		return new SeleniumWComponentWebElement(getOptions().get(optionNumber), getDriver());
	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getFirstOption() {
		return getNthOption(0);
	}

	/**
	 * @return the last option for this select.
	 */
	public SeleniumWComponentWebElement getLastOption() {
		List<WebElement> options = getOptions();
		return new SeleniumWComponentWebElement(options.get(options.size() - 1), getDriver());
	}


	/**
	 * Some attributes are applied to the wrapper, some to the input. This override sorts out which is which.
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
		return findElementImmediate(By.tagName(SELECT_TAG));
	}

	/**
	 * Is the component a multi-select list?
	 *
	 * @return {@code true} if the component is a multi-select
	 */
	public boolean isMultiSelect() {
		if (isReadOnly()) {
			return RO_MULTI_SELECT_TAG.equals(getTagName());
		}
		return "multiple".equals(getInputField().getAttribute("multiple"));
	}

	/**
	 *
	 * @return all options
	 */
	public List<WebElement> getOptions() {
		if (isReadOnly()) {
			return findElementsImmediate(By.tagName(RO_MULTI_OPTION_TAG));
		}
		WebElement input = getInputField();
		Select se = new Select(input);
		return se.getOptions();
	}

	/**
	 *
	 * @return all selected options
	 */
	public List<WebElement> getSelectedOptions() {
		if (isReadOnly()) {
			return getOptions();
		}
		WebElement input = getInputField();
		Select se = new Select(input);
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

		if (isReadOnly()) {
			if (isMultiSelect()) {
				for (WebElement we : getOptions()) {
					if (optionText.equals(we.getText())) {
						return we;
					}
				}
				throw new NoSuchElementException("No option with text provided");
			}
			if (optionText.equals(getText())) {
				return this;
			}
			throw new NoSuchElementException("No option with text provided");
		}

		for (WebElement we : getOptions()) {
			if (optionText.equals(we.getText())) {
				return we;
			}
		}
		throw new NoSuchElementException("No option with text provided");
	}
}
