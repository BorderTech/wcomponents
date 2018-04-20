package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public class SeleniumWButtonWebElement extends SeleniumWComponentWebElement {

	private static final String TAG_NAME = "button";
	private static final String PRIMARY_CLASSNAME = "wc-button";

	/**
	 * Create an instance of the WebElement definition of a WButton.
	 * @param element the base WebElement
	 * @param driver the current Selenium driver
	 */
	public SeleniumWButtonWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		String tagName = element.getTagName();

		if (!tagName.equals(TAG_NAME)) {
			throw new IllegalArgumentException("Element is not the expected element. tag=[" + tagName + "].");
		}
		String className = element.getAttribute("class");
		if (!className.contains(PRIMARY_CLASSNAME)) {
			throw new IllegalArgumentException("Element does not contain the expected class value. class=[" + className + "].");
		}
	}

	/**
	 * @return a default CSS selector to get a WButton using By.cssSelector
	 */
	public static String getCssSelector() {
		return TAG_NAME + "." + PRIMARY_CLASSNAME;
	}

}
