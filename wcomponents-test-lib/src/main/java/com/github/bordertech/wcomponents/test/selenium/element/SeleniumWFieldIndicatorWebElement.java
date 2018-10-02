package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumWFieldIndicatorWebElement extends SeleniumWComponentWebElement {

	public static final String TOP_LEVEL_TAG = "span";

	private AbstractWFieldIndicator.FieldIndicatorType indicatorType;

	/**
	 * Creates a WebElementWrapper.
	 *
	 * @param element the backing element.
	 * @param driver  the SeleniumWComponentsWebDriver.
	 */
	public SeleniumWFieldIndicatorWebElement(WebElement element, WebDriver driver) {
		super(element, driver);
		String tagName = element.getTagName();
		if (!(TOP_LEVEL_TAG.equalsIgnoreCase(tagName))) {
			throw new SystemException("Incorrect element selected for SeleniumWFieldIndicatorWebElement. Found: " + tagName);
		}
		loadIndicator(element);
	}

	private void loadIndicator(WebElement element) {
		String aClass = element.getAttribute("class");
		if (StringUtils.contains(aClass, "error")) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.ERROR;
		} else if (StringUtils.contains(aClass, "warn")) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.WARN;
		} else if (StringUtils.contains(aClass, "info")) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.INFO;
		} else if (StringUtils.contains(aClass, "success")) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.SUCCESS;
		}
	}


	public AbstractWFieldIndicator.FieldIndicatorType getIndicatorType() {
		return indicatorType;
	}
}
