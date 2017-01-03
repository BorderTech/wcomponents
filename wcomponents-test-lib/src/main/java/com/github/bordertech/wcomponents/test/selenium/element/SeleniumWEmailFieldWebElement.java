package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WEmailField.
 *
 * @author Mark Reeves
 * @since 1.2.3
 */
public class SeleniumWEmailFieldWebElement extends SeleniumWTextFieldWebElement {

	/**
	 * The type of this input component.
	 */
	public static final String TYPE = "email";

	/**
	 * Construct a SeleniumWEmailFieldWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWEmailFieldWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

}
