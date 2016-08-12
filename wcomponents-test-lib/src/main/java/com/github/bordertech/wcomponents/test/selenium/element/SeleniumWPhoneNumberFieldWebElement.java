package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WPhoneNumberField.
 *
 * @author Mark Reeves
 * @since 1.2.3
 */
public class SeleniumWPhoneNumberFieldWebElement extends SeleniumWTextFieldWebElement {
	/**
	 * Construct a SeleniumWPhoneNumberFieldWebElement for the given WebElement.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWPhoneNumberFieldWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

}
