package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWRadioButtonSelectWebElement extends SeleniumCheckableGroupInputWebElement {

	/**
	 * The value of the read-only indicator for WRadioButtonSelect.
	 */
	private static final String RO_COMPONENT = "radiobuttonselect";

	/**
	 * Create a SeleniumWComponentWRadioButtonSelectWebElement.
	 *
	 * @param element the backing WebElement
	 * @param driver the backing Selenium Driver
	 */
	public SeleniumWRadioButtonSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * Get the radio button at a given option index. Only applies to WRadioButtonSelect in an interactive state.
	 *
	 * @param idx the option's index
	 * @return a HTML input element in the radio button state.
	 */
	public WebElement getRadioButton(final int idx) {
		if (isReadOnly()) {
			throw new SystemException("WRadioButtonSelect in a read-only state has no radio buttons.");
		}
		WebElement option = getOption(idx);
		By by = By.tagName(getOptionTag());
		return SeleniumWComponentsUtil.findElementImmediateForElement(option, by);
	}

	@Override
	final String getROComponentName() {
		return RO_COMPONENT;
	}
}
