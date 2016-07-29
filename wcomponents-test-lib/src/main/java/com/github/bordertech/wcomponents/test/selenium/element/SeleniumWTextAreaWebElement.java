package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WTextArea.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWTextAreaWebElement extends SeleniumWComponentWebElement {

	/**
	 * The tag name of the editable WTextArea element.
	 */
	public static final String EDITABLE_TAG = "textarea";

	/**
	 * The tag name for a read-only WTextArea element.
	 */
	public static final String READ_ONLY_TAG = "pre";

	/**
	 * The tag name of the field.
	 */
	private final String tagName;

	/**
	 * Construct a WTextAreaWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWTextAreaWebElement(final WebElement element, final WebDriver driver) {

		super(element, driver);

		this.tagName = element.getTagName();
		if (!tagName.equals(EDITABLE_TAG) && !tagName.equals(READ_ONLY_TAG)) {

			throw new IllegalArgumentException("element is not a WTextArea. tag=[" + tagName + "]");
		}
	}

	/**
	 * @return true if the component is editable.
	 */
	@Override
	public boolean isEnabled() {
		return tagName.equals(EDITABLE_TAG) && super.isEnabled();
	}

	/**
	 * @return true if the WTextArea is read-only.
	 */
	public boolean isReadOnly() {
		return tagName.equals(READ_ONLY_TAG);
	}

}
