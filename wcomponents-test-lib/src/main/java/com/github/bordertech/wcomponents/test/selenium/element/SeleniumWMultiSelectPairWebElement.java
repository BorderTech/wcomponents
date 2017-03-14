package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * WebElement to facilitate tests of WMultiSelectPair.
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWMultiSelectPairWebElement extends SeleniumGroupInputWebElement {
	/**
	 * The value of the read-only indicator for WMultiSelectPair.
	 */
	private static final String RO_COMPONENT = "radiobuttonselect";

	/**
	 * Create a SeleniumWMultiSelectPairWebElement.
	 * @param element the backing WebElement
	 * @param driver the current Selenium web driver
	 */
	public SeleniumWMultiSelectPairWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	@Override
	final String getROComponentName() {
		return RO_COMPONENT;
	}

	

}
