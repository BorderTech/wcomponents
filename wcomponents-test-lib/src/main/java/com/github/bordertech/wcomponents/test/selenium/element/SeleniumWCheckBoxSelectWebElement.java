package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWCheckBoxSelectWebElement extends SeleniumCheckableGroupInputWebElement {
	/**
	 * The value of the read-only indicator for WCheckBoxSelect.
	 */
	private static final String RO_COMPONENT = "checkboxselect";

	/**
	 * Create a SeleniumWComponentWCheckBoxSelectWebElement.
	 * @param element the backing WebElement
	 * @param driver the backing Selenium Driver
	 */
	public SeleniumWCheckBoxSelectWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * Toggle the selected state of an option identified by its label.
	 * @param option the option to toggle
	 */
	public void toggle(final WebElement option) {
		clickNoWait(option);
	}
	/**
	 * Toggle the selected state of an option by its index.
	 * @param idx the option index to toggle
	 */
	public void toggle(final int idx) {
		toggle(getOption(idx));
	}

	/**
	 * Toggle the selected state of an option identified by its label.
	 * @param labelText the option's label text
	 */
	public void toggle(final String labelText) {
		toggle(getOption(labelText));
	}

	/**
	 * Deselect an option.
	 * @param option the option to deselect
	 */
	public void deselect(final WebElement option) {
		if (isSelected(option)) {
			clickNoWait(option);
		}
	}

	/**
	 * Deselect an option by its index.
	 * @param idx the option index to deselect
	 */
	public void deselect(final int idx) {
		deselect(getOption(idx));
	}

	/**
	 * Deselect an option identified by its label text.
	 * @param labelText the option index to deselect
	 */
	public void deselect(final String labelText) {
		deselect(getOption(labelText));
	}

	@Override
	final String getROComponentName() {
		return RO_COMPONENT;
	}
}
