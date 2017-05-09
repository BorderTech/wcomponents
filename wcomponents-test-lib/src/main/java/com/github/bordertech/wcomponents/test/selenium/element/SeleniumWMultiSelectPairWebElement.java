package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * WebElement to facilitate tests of WMultiSelectPair.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWMultiSelectPairWebElement extends SeleniumGroupInputWebElement {

	/**
	 * The value of the read-only indicator for WMultiSelectPair.
	 */
	private static final String RO_COMPONENT = "multiselectpair";

	/**
	 * Create a SeleniumWMultiSelectPairWebElement.
	 *
	 * @param element the backing WebElement
	 * @param driver the current Selenium web driver
	 */
	public SeleniumWMultiSelectPairWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	@Override
	public boolean isEnabled() {
		if (isReadOnly()) {
			return false;
		}
		return getAvailableList().isEnabled();
	}

	@Override
	final String getROComponentName() {
		return RO_COMPONENT;
	}

	/**
	 * @return the WebElement representing the 'available' options list.
	 */
	public WebElement getAvailableList() {
		if (isReadOnly()) {
			throw new SystemException("Cannot get available list from a read-only WMultiSelectPair");
		}
		return findElementImmediate(By.className("wc_msp_av"));
	}

	/**
	 * @return the WebElement representing the 'selected' options list.
	 */
	public WebElement getSelectedList() {
		if (isReadOnly()) {
			throw new SystemException("Cannot get selected list from a read-only WMultiSelectPair");
		}
		return findElementImmediate(By.className("wc_msp_chos"));
	}

	/**
	 * @return the selected options for this control if it is in a read-only state
	 */
	private List<WebElement> getReadOnlyOptions() {
		return findElementsImmediate(By.cssSelector(".wc-option"));
	}

	/**
	 * @param list the selection list for which we are getting options
	 * @return the options in a given list
	 */
	private List<WebElement> getOptions(final WebElement list) {
		By by = By.tagName("option");
		return SeleniumWComponentsUtil.findElementsImmediateForElement(list, by);
	}

	/**
	 * @return the available options for this control
	 */
	public List<WebElement> getOptions() {
		if (isReadOnly()) {
			return getReadOnlyOptions();
		}
		List<WebElement> options = new ArrayList<>();
		options.addAll(getOptions(getAvailableList()));
		options.addAll(getOptions(getSelectedList()));
		return options;
	}

	/**
	 * @return the available options for this control
	 */
	public List<WebElement> getSelected() {
		if (isReadOnly()) {
			return getReadOnlyOptions();
		}
		if (!isEnabled()) { // disabled always has no selection, even when it appears to have selected options
			return new ArrayList<>();
		}
		return getOptions(getSelectedList());
	}

	/**
	 * @param labelText the visible text of the option
	 * @return the option
	 */
	public WebElement getOption(final String labelText) {
		for (WebElement option : getOptions()) {
			if (labelText.equalsIgnoreCase(option.getText())) {
				return option;
			}
		}
		throw new IllegalArgumentException("Cannot find option with text" + labelText);
	}

	/**
	 * Is an option with given label text selected?
	 *
	 * @param labelText the text visible in the option to be tested
	 * @return {@code true} if an option with that text is in the selected list.
	 */
	public boolean isSelected(final String labelText) {
		for (WebElement o : getSelected()) {
			if (labelText.equalsIgnoreCase(o.getText())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is an option selected?
	 *
	 * @param option the option to be tested
	 * @return {@code true} if an option is in the selected list.
	 */
	public boolean isSelected(final WebElement option) {
		return isSelected(option.getText());
	}

	/**
	 * FInd a button based on a CSS selector.
	 *
	 * @param selector the CSS selector to use
	 * @return a WebElement representing the button.
	 */
	private WebElement getButton(final String selector) {
		if (isReadOnly()) {
			throw new SystemException("Cannot get buttons from a read-only WMultiSelectPair");
		}
		return findElementImmediate(By.cssSelector("button" + selector));
	}

	/**
	 * @return the button used to add option(s)
	 */
	public WebElement getSelectButton() {
		return getButton("[value='add']");
	}

	/**
	 * @return the button used to add all options
	 */
	public WebElement getSelectAllButton() {
		return getButton("[value='aall']");
	}

	/**
	 * @return the button used to add option(s)
	 */
	public WebElement getDeselectButton() {
		return getButton("[value='rem']");
	}

	/**
	 * @return the button used to add all options
	 */
	public WebElement getDeselectAllButton() {
		return getButton("[value='rall']");
	}

	/**
	 * Select a given option.
	 *
	 * @param option the option to select
	 */
	public void select(final WebElement option) {
		if (!isReadOnly() && isEnabled() && !isSelected(option)) {
			if (!option.isSelected()) {
				clickElementNoWait(option);
			}
			clickElementNoWait(getSelectButton());
		}
	}

	/**
	 * Select the option with given text.
	 *
	 * @param labelText the text of the option to select
	 */
	public void select(final String labelText) {
		select(getOption(labelText));
	}

	/**
	 * Deselect a given option.
	 *
	 * @param option the option to deselect
	 */
	public void deselect(final WebElement option) {
		if (!isReadOnly() && isEnabled() && isSelected(option)) {
			if (!option.isSelected()) {
				clickElementNoWait(option);
			}
			clickElementNoWait(getDeselectButton());
		}
	}

	/**
	 * Deselect the option with given text.
	 *
	 * @param labelText the text of the option to deselect
	 */
	public void deselect(final String labelText) {
		deselect(getOption(labelText));
	}

	/**
	 * Select all of the options.
	 */
	public void selectAll() {
		if (!isReadOnly() && isEnabled()) {
			clickElementNoWait(getSelectAllButton());
		}
	}

	/**
	 * Deselect all of the options.
	 */
	public void deselectAll() {
		if (!isReadOnly() && isEnabled()) {
			clickElementNoWait(getDeselectAllButton());
		}
	}

	// TODO: shuffle
}
