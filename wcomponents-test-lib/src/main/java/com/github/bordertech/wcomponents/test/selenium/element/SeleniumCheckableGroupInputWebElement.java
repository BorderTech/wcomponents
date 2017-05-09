package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Checkable group inputs are WCheckBoxSelect and WRadioButtonSelect.
 *
 * @author Mark Reeves
 */
public abstract class SeleniumCheckableGroupInputWebElement extends SeleniumGroupInputWebElement {

	/**
	 * Create a SeleniumGroupInputWebElement instance.
	 *
	 * @param element the base WebElement
	 * @param driver the current Selenium driver
	 */
	public SeleniumCheckableGroupInputWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * @return the available options for this control
	 */
	public List<WebElement> getOptions() {
		return findElementsImmediate(By.cssSelector(".wc-option"));
	}

	/**
	 * Get an option using an index.
	 *
	 * @param idx the index of the option to get
	 * @return the option as a WebElement
	 */
	public WebElement getOption(final int idx) {
		List<WebElement> options = getOptions();
		if (CollectionUtils.isEmpty(options)) {
			throw new SystemException("No options available");
		}
		return options.get(idx);
	}

	/**
	 * Find an option with a particular label.
	 *
	 * @param labelText the text content of the option's label
	 * @return the option
	 */
	public WebElement getOption(final String labelText) {
		List<WebElement> options = getOptions();
		if (CollectionUtils.isEmpty(options)) {
			throw new SystemException("No options available");
		}
		if (isReadOnly()) {
			for (WebElement o : options) {
				if (labelText.equalsIgnoreCase(o.getText())) {
					return o;
				}
			}
			throw new IllegalArgumentException("Could not find option identified by " + labelText);
		}
		SeleniumWComponentWebElement input = findElementImmediate(new ByLabel(labelText, false, true));
		return input.findElementImmediate(By.xpath(".."));
	}

	/**
	 * Get the active control from an option.
	 *
	 * @param idx the option index
	 * @return the active HTML element, if any
	 */
	public WebElement getInput(final int idx) {
		if (isReadOnly()) {
			throw new SystemException("Component in a read-only state has no interactive controls.");
		}
		SeleniumWComponentWebElement option = (SeleniumWComponentWebElement) getOption(idx);
		return option.findElementImmediate(By.tagName(getOptionTag()));
	}

	/**
	 * Get the interactive HTML control with a given label. Only applies to components in an interactive state.
	 *
	 * @param labelText the text of the label for the option we are after
	 * @return the active HTML element, if any
	 */
	public WebElement getInput(final String labelText) {
		if (isReadOnly()) {
			throw new SystemException("Component in a read-only state has no interactive controls.");
		}
		return findElementImmediate(new ByLabel(labelText, false, true));
	}

	/**
	 * Get the input control belonging to a particular option in the group.
	 *
	 * @param option the option we are trying to interact with
	 * @return the input control of that option
	 */
	public WebElement getInput(final WebElement option) {
		if (isReadOnly()) {
			throw new SystemException("Components in a read-only state have no inputs.");
		}
		return SeleniumWComponentsUtil.findElementImmediateForElement(option, By.tagName(getOptionTag()));
	}

	/**
	 * Get the selected options. Note that a WCheckBoxSelect in a read only state contains only the selected options and
	 * a disabled component has no selected options.
	 *
	 * @return a list of selected options
	 */
	public List<WebElement> getSelected() {
		List<WebElement> options = getOptions();
		if (isReadOnly()) {
			return options;
		}
		if (!isEnabled()) {
			return new ArrayList<>();
		}
		List<WebElement> selected = new ArrayList<>();
		for (WebElement o : options) {
			if (getInput(o).isSelected()) {
				selected.add(o);
			}
		}
		return selected;
	}

	/**
	 * @param option the WebElement representing an option in the component.
	 * @return {@code true} if the input in option is selected or if option exists and the component is read-only.
	 */
	public boolean isSelected(final WebElement option) {
		if (isReadOnly()) {
			List<WebElement> options = getOptions();
			String optionText = option.getText();

			for (WebElement o : options) {
				if (optionText.equals(o.getText())) {
					return true;
				}
			}
			return false;
		}
		if (!isEnabled(option)) {
			return false;
		}
		return getInput(option).isSelected();
	}

	/**
	 * @param idx the option index
	 * @return {@code true} if the option is selected or, if the component is in a read-only state, if option merely
	 * exists
	 */
	public boolean isSelected(final int idx) {
		if (isReadOnly()) {
			return getOptions().size() > idx;
		}
		return isSelected(getOption(idx));
	}

	/**
	 * @param labelText the label text for the option
	 * @return {@code true} if the option is selected or, if the component is in a read-only state, if the option exists
	 */
	public boolean isSelected(final String labelText) {
		if (isReadOnly()) {
			String path = ".//*[text()='" + labelText + "']";
			// if in a read only state the option is selected if it is in the UI as only selected options are output.
			try {
				findElementImmediate(By.xpath(path));
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return isSelected(getOption(labelText));
	}

	/**
	 * Select an option.
	 *
	 * @param option the option to select
	 */
	public void select(final WebElement option) {
		if (!isSelected(option)) {
			SeleniumCheckableGroupInputWebElement.this.clickNoWait(option);
		}
	}

	/**
	 * Select the option which is labelled by a given piece of text.
	 *
	 * @param text the option's label.
	 */
	public void select(final String text) {
		select(getOption(text));
	}

	/**
	 * Select the option which at a given index.
	 *
	 * @param idx the option index.
	 */
	public void select(final int idx) {
		select(getOption(idx));
	}

	/**
	 * Clicks an option's input. This may or may not have an impact.
	 *
	 * @param option the option to click
	 */
	public void click(final WebElement option) {
		if (isEnabled(option)) {
			getInput(option).click();
		}
	}

	/**
	 * Click an option labelled by a given string.
	 *
	 * @param labelText the label text of the option to click
	 */
	public void click(final String labelText) {
		WebElement option = getOption(labelText);
		click(option);
	}

	/**
	 * Click an option based on its index.
	 *
	 * @param idx the index of the option to click
	 */
	public void click(final int idx) {
		WebElement option = getOption(idx);
		click(option);
	}

	/**
	 * Clicks an option's input. This may or may not have an impact.
	 * <p>
	 * Clicks with no wait.
	 * </p>
	 *
	 * @param option the option to click
	 */
	public void clickNoWait(final WebElement option) {
		if (isEnabled(option)) {
			clickElementNoWait(getInput(option));
		}
	}

	/**
	 * Click an option labelled by a given string.
	 * <p>
	 * Clicks with no wait.
	 * </p>
	 *
	 * @param labelText the label text of the option to click
	 */
	public void clickNoWait(final String labelText) {
		WebElement option = getOption(labelText);
		clickNoWait(option);
	}

	/**
	 * Click an option based on its index.
	 * <p>
	 * Clicks with no wait.
	 * </p>
	 *
	 * @param idx the index of the option to click
	 */
	public void clickNoWait(final int idx) {
		WebElement option = getOption(idx);
		clickNoWait(option);
	}

	/**
	 * Is a given option enabled? For most purposes this is moot as either all options are enabled or non are. Usually
	 * you will just need to use {@link #isEnabled()}.
	 *
	 * @param option the option to test
	 * @return {@code true} if the input element in option is enabled
	 */
	private boolean isEnabled(final WebElement option) {
		if (isReadOnly()) {
			return false;
		}
		return getInput(option).isEnabled();
	}

	@Override
	public boolean isEnabled() {
		return isEnabled(getOption(0));
	}
}
