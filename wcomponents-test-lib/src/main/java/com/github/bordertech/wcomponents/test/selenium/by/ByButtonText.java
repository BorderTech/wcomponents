package com.github.bordertech.wcomponents.test.selenium.by;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * Finds a button by the text value on the button.
 */
public class ByButtonText extends By {


	/**
	 * Button's text value by exact root.
	 */
	public static final String XPATH_BUTTON_VALUE_EXACT_ROOT = "//button[text()='%1$s']";

	/**
	 * Button's text value contains within root.
	 */
	public static final String XPATH_BUTTON_VALUE_CONTAINS_ROOT = "//button[contains(text(),'%1$s')]";

	/**
	 * Button's text value by exact relative.
	 */
	public static final String XPATH_BUTTON_VALUE_EXACT_RELATIVE = ".//button[text()='%1$s']";

	/**
	 * Button's text value contains within relative.
	 */
	public static final String XPATH_BUTTON_VALUE_CONTAINS_RELATIVE = ".//button[contains(text(),'%1$s')]";

	/**
	 * The button element's text value.
	 */
	private final String buttonText;

	/**
	 * Whether to use a partial match on the value.
	 */
	private final boolean partialMatch;

	/**
	 * Whether to use a relative path to find the element.
	 */
	private final boolean relative;

	/**
	 * ByButtonText using the button's text value.
	 *
	 * @param buttonText the button value
	 */
	public ByButtonText(final String buttonText) {
		this.buttonText = buttonText;
		this.partialMatch = false;
		this.relative = false;
	}

	/**
	 * ByButtonText using the button's text value.
	 *
	 * @param buttonText the button value
	 * @param partialMatch whether it can be a partial text match
	 * @param relative whether to use a relative xpath lookup
	 */
	public ByButtonText(final String buttonText, final boolean partialMatch, final boolean relative) {
		this.buttonText = buttonText;
		this.partialMatch = partialMatch;
		this.relative = relative;
	}

	@Override
	public List<WebElement> findElements(final SearchContext context) {
		String xpath;
		if (partialMatch) {
			xpath = String.format((relative ? XPATH_BUTTON_VALUE_CONTAINS_RELATIVE : XPATH_BUTTON_VALUE_CONTAINS_ROOT), buttonText);
		} else {
			xpath = String.format((relative ? XPATH_BUTTON_VALUE_EXACT_RELATIVE : XPATH_BUTTON_VALUE_EXACT_ROOT), buttonText);
		}
		return ((FindsByXPath) context).findElementsByXPath(xpath);
	}

}
