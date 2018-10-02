package com.github.bordertech.wcomponents.test.selenium;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByXPath;

import java.util.List;

/**
 * Finds a button by the value on the button.
 */
public class ByButtonValue extends By {

	/**
	 * Button value by exact root.
	 */
	public static final String XPATH_BUTTON_VALUE_EXACT_ROOT = "//button[text()='%1$s']";

	/**
	 * Button value contains within root.
	 */
	public static final String XPATH_BUTTON_VALUE_CONTAINS_ROOT = "//button[contains(text(),'%1$s')]";

	/**
	 * Button value by exact relative.
	 */
	public static final String XPATH_BUTTON_VALUE_EXACT_RELATIVE = ".//button[text()='%1$s']";

	/**
	 * Button value contains within relative.
	 */
	public static final String XPATH_BUTTON_VALUE_CONTAINS_RELATIVE = ".//button[contains(text(),'%1$s')]";


	private final String buttonId;
	private final String buttonValue;
	private final boolean partialMatch;
	private final boolean relative;


	/**
	 * ByButtonValue using the buttons's ID.
	 *
	 * @param buttonId the ID of the label.
	 */
	public ByButtonValue(final String buttonId) {

		this.buttonId = buttonId;
		this.buttonValue = null;
		this.partialMatch = false;
		this.relative = false;
	}

	/**
	 * ByButtonValue using the button's value.
	 *
	 * @param buttonValue the button value
	 * @param partialMatch whether it can be a partial text match
	 * @param relative whether to use a relative xpath lookup
	 */
	public ByButtonValue(final String buttonValue, final boolean partialMatch, final boolean relative) {
		this.buttonValue = buttonValue;
		this.buttonId = null;
		this.partialMatch = partialMatch;
		this.relative = relative;
	}

	@Override
	public List<WebElement> findElements(final SearchContext context) {
		List<WebElement> buttonValues;
		if (buttonId != null) {
			buttonValues = ((FindsById) context).findElementsById(buttonId);
		} else {
			String xpath;
			if (partialMatch) {
				xpath = String.format((relative ? XPATH_BUTTON_VALUE_CONTAINS_RELATIVE : XPATH_BUTTON_VALUE_CONTAINS_ROOT), buttonValue);
			} else {
				xpath = String.format((relative ? XPATH_BUTTON_VALUE_EXACT_RELATIVE : XPATH_BUTTON_VALUE_EXACT_ROOT), buttonValue);
			}
			buttonValues = ((FindsByXPath) context).findElementsByXPath(xpath);
		}

		if (CollectionUtils.isEmpty(buttonValues)) {
			return buttonValues;
		}

		return buttonValues;
	}

}
