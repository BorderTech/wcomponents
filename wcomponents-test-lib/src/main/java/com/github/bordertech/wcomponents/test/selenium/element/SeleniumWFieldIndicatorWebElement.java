package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The selenium web element for a WFieldIndicator.
 */
public class SeleniumWFieldIndicatorWebElement extends SeleniumWComponentWebElement {

	/**
	 * Top level html element.
	 */
	public static final String TOP_LEVEL_TAG = "span";

	private AbstractWFieldIndicator.FieldIndicatorType indicatorType;

	/**
	 * Creates a WebElementWrapper.
	 *
	 * @param element the backing element.
	 * @param driver  the SeleniumWComponentsWebDriver.
	 */
	public SeleniumWFieldIndicatorWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		String tagName = element.getTagName();
		String aClass = element.getAttribute("class");
		if (!(TOP_LEVEL_TAG.equalsIgnoreCase(tagName))) {
			throw new SystemException("Incorrect element selected for SeleniumWFieldIndicatorWebElement. Found: " + tagName);
		} else if (!StringUtils.contains(aClass, FieldIndicatorClasses.CLASS_PREFIX)) {
			throw new SystemException("Incorrect element selected for SeleniumWFieldIndicatorWebElement. Found: " + aClass);

		}
		loadIndicator(element);
	}

	/**
	 * Determines the current field indicator value.
	 * @param element to load indicator type
	 */
	private void loadIndicator(final WebElement element) {
		String aClass = element.getAttribute("class");
		if (StringUtils.contains(aClass, FieldIndicatorClasses.ERROR)) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.ERROR;
		} else if (StringUtils.contains(aClass, FieldIndicatorClasses.WARN)) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.WARN;
		} else if (StringUtils.contains(aClass, FieldIndicatorClasses.INFO)) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.INFO;
		} else if (StringUtils.contains(aClass, FieldIndicatorClasses.SUCCESS)) {
			indicatorType = AbstractWFieldIndicator.FieldIndicatorType.SUCCESS;
		}
	}


	/**
	 * Returns the current field indicator type.
	 *
	 * @return the indicator type.
	 */
	public AbstractWFieldIndicator.FieldIndicatorType getIndicatorType() {
		return indicatorType;
	}

	/**
	 * All the field indicator CSS classes.
	 */
	private class FieldIndicatorClasses {
		public static final String CLASS_PREFIX = "wc-fieldindicator";
		/**
		 * The error css class.
		 */
		private static final String ERROR = CLASS_PREFIX + "-type-error";
		/**
		 * The warning css class.
		 */
		private static final String WARN = CLASS_PREFIX + "-type-warn";
		/**
		 * The information css class.
		 */
		private static final String INFO = CLASS_PREFIX + "-type-info";
		/**
		 * The success css class.
		 */
		private static final String SUCCESS = CLASS_PREFIX + "-type-success";
	}
}
