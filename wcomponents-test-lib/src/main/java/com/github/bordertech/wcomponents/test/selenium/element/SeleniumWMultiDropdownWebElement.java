package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.support.ui.Select;

/**
 * WebElement to facilitate tests of WMultiDropdown.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWMultiDropdownWebElement extends SeleniumGroupInputWebElement {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SeleniumWMultiDropdownWebElement.class);

	/**
	 * The value of the read-only indicator for WMultiSelectPair.
	 */
	private static final String RO_COMPONENT = "multidropdown";

	/**
	 * Create a SeleniumWMultiSelectPairWebElement.
	 *
	 * @param element the backing WebElement
	 * @param driver the current Selenium web driver
	 */
	public SeleniumWMultiDropdownWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	@Override
	public boolean isEnabled() {
		if (isReadOnly()) {
			return false;
		}
		return getFirstDropdown().isEnabled();
	}

	@Override
	final String getROComponentName() {
		return RO_COMPONENT;
	}

	/**
	 * @return the WebElement representing the 'available' options dropdown.
	 */
	public WebElement getFirstDropdown() {
		if (isReadOnly()) {
			throw new SystemException("Cannot get active list from a read-only WMultiDropdown");
		}
		return findElementImmediate(By.tagName("select"));
	}

	/**
	 * @return the  button to add an option to the WMultiDropdown
	 */
	public WebElement getAddButton() {
		if (isReadOnly()) {
			throw new SystemException("Cannot get add button from a read-only WMultiDropdown");
		}
		return findElementImmediate(By.tagName("button"));
	}

	/**
	 * @return the selected options for this control if it is in a read-only state
	 */
	private List<WebElement> getReadOnlyOptions() {
		return findElementsImmediate(By.cssSelector(".wc-option"));
	}

	/**
	 * @return the available options for this control
	 */
	public List<WebElement> getOptions() {
		if (isReadOnly()) {
			return getReadOnlyOptions();
		}
		WebElement dropdown = getFirstDropdown();
		Select se = new Select(dropdown);
		return se.getOptions();
	}

	/**
	 * @return the available options for this control
	 */
	public List<WebElement> getSelected() {
		if (isReadOnly()) {
			return getReadOnlyOptions();
		}
		if (!isEnabled()) { // disabled always has no selection, even when it appears to have selected options
			return new ArrayList<>(0);
		}
		List<WebElement> dropdowns = getDropdowns();
		if (dropdowns.isEmpty()) { // theoretically this should never happen...
			return new ArrayList<>(0);
		}
		List<WebElement> selected = new ArrayList(dropdowns.size());
		Select se;
		for (WebElement dropdown : dropdowns) {
			se = new Select(dropdown);
			selected.add(se.getFirstSelectedOption());
		}
		return selected;
	}

	/**
	 *
	 * @param idx the index of the option list to obtain
	 * @return the WebElement representing the dropdown
	 */
	public WebElement getDropdown(final int idx) {
		if (isReadOnly()) {
			throw new SystemException("Cannot get option lists from a read-only WMultiDropdown");
		}
		if (idx < 0) {
			throw new IllegalArgumentException("idx must be >= 0");
		}
		List<WebElement> dropdowns = getDropdowns();
		if (idx >= dropdowns.size()) {
			throw new IllegalArgumentException("Cannot get option lists for the given index");
		}
		return dropdowns.get(idx);
	}

	/**
	 *
	 * @param optionText the text of the selected option
	 * @return a representation of the select list with selection equal to the selected option text
	 */
	public WebElement getDropdown(final String optionText) {
		if (isReadOnly()) {
			throw new SystemException("Cannot get option lists from a read-only WMultiDropdown");
		}
		if (optionText == null) {
			throw new IllegalArgumentException("Cannot get a reference to the selected option list without the option text");
		}
		List<WebElement> dropdowns = getDropdowns();
		if (dropdowns.isEmpty()) {
			// theoretically this is impossible
			throw new SystemException("Cannot find any selected lists");
		}
		Select se;
		WebElement option;
		for (WebElement dropdown: dropdowns) {
			se = new Select(dropdown);
			option = se.getFirstSelectedOption();
			if (optionText.equalsIgnoreCase(option.getText())) {
				return dropdown;
			}
		}
		throw new SystemException("Cannot get a list for that option text");
	}

	/**
	 * @param dropdown the WebElement representing the dropdown which will be removed if the remove button is invoked
	 * @return the remove button for the dropdown
	 */
	public WebElement getRemoveButton(final WebElement dropdown) {
		if (isReadOnly()) {
			throw new SystemException("Cannot get remove button from a read-only WMultiDropdown");
		}
		if (dropdown == null) {
			throw new IllegalArgumentException("Cannot get remove button without a reference dropdown");
		}

		String id = dropdown.getAttribute("id");
		WebElement firstOption = getFirstDropdown();
		if (id.equals(firstOption.getAttribute("id"))) {
			throw new SystemException("Cannot get remove button for the first option");
		}
		String cssSelector = "button[aria-controls='" + id + "']";
		return findElementImmediate(By.cssSelector(cssSelector));
	}

	/**
	 * Gets the dropdowns in the WMultiDropdown. There will be one per selected option.
	 * @return the selection dropdowns in the WMultiDropdown.
	 */
	public List<WebElement> getDropdowns() {
		if (isReadOnly()) {
			throw new SystemException("Cannot get selection lists from a read-only WMultiDropdown");
		}
		return findElementsImmediate(By.tagName("select"));
	}

	/**
	 * Is an option with given text selected?
	 *
	 * @param labelText the text visible in the option to be tested
	 * @return {@code true} if an option with that text is in the selected dropdown.
	 */
	public boolean isSelected(final String labelText) {
		if (isReadOnly()) {
			for (WebElement o : getReadOnlyOptions()) {
				if (labelText.equalsIgnoreCase(o.getText())) {
					return true;
				}
			}
			return false;
		}
		Select se;
		for (WebElement dropdown : getDropdowns()) {
			se = new Select((dropdown));
			if (labelText.equalsIgnoreCase(se.getFirstSelectedOption().getText())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Deselect the option with given text.
	 *
	 * @param optionText the text of the option to deselect
	 */
	public void deselect(final String optionText) {
		if (isReadOnly()) {
			throw new SystemException("Cannot deselect option from a read-only WMultiDropdown");
		}
		if (optionText == null) {
			throw new IllegalArgumentException("Cannot deselect option without the option text");
		}
		WebElement dropdown = getDropdown(optionText);
		WebElement removeButton = getRemoveButton(dropdown);
		clickButton(removeButton, false);
	}

	/**
	 * Select an option using its visible text.
	 * @param optionText the text of the option to select
	 */
	public void select(final String optionText) {
		if (isReadOnly()) {
			throw new SystemException("Cannot select from a read-only WMultiDropdown");
		}
		clickButton(getAddButton(), true);
		Select se = new Select(getFirstDropdown());
		se.selectByVisibleText(optionText);
	}

	/**
	 * Set a new selection in the first dropdown in a WMultiDropdown using visible option text. Convenient for setting a single selection.
	 * @param toText the visible text of the option to select
	 */
	public void switchFirstOption(final String toText) {
		if (isReadOnly()) {
			throw new SystemException("Cannot switch selection from a read-only WMultiDropdown");
		}
		WebElement dropdown = getFirstDropdown();
		Select se = new Select(dropdown);
		se.selectByVisibleText(toText);
	}

	/**
	 * Change a selected option from one value to another.
	 * @param fromText the current selected text
	 * @param toText the text to select
	 */
	public void switchOption(final String fromText, final String toText) {
		if (isReadOnly()) {
			throw new SystemException("Cannot switch selection from a read-only WMultiDropdown");
		}
		WebElement dropdown = getDropdown(fromText);
		Select se = new Select(dropdown);
		se.selectByVisibleText(toText);
	}

	/**
	 * Deselect "all" options. A slight misnomer as the first dropdown will end up with its the first option selected.
	 */
	public void deselectAll() {
		if (isReadOnly()) {
			throw new SystemException("Cannot deselect all from a read-only WMultiDropdown");
		}
		List<WebElement> dropdowns = getDropdowns();
		if (dropdowns.isEmpty()) {
			// really should not be here but tehre is nothing to do
			LOG.warn("Found an editable WMultiDropdown with no select elements");
			return;
		}
		WebElement dropdown = getFirstDropdown();
		Select se = new Select(dropdown);
		se.selectByIndex(0);
		if (dropdowns.size() == 1) {
			// finished
			return;
		}
		// to remove all the other options shift-click any delete button
		WebElement removeButton = getRemoveButton(dropdowns.get(1));
		WebDriver driver = getDriver();
		if (driver instanceof HasInputDevices) {
			Actions shiftClick = new Actions(driver);
			shiftClick.keyDown(Keys.SHIFT).click(removeButton).keyUp(Keys.SHIFT).perform();
		} else {
			removeButton.sendKeys(Keys.chord(Keys.SHIFT, Keys.SPACE));
		}

		SeleniumWComponentsUtil.waitForPageReady(getDriver());
	}

	/**
	 * Helper to click the add and remove buttons.
	 * @param button the button to click
	 * @param wait if {@code true} then wait for the page to be ready after the click
	 */
	private void clickButton(final WebElement button, final boolean wait) {
		if (button instanceof SeleniumWComponentWebElement) {
			if (wait) {
				((SeleniumWComponentWebElement) button).click();
			} else {
				((SeleniumWComponentWebElement) button).clickNoWait();
			}
			return;
		}
		button.click();
		if (wait) {
			SeleniumWComponentsUtil.waitForPageReady(getDriver());
		}
	}
}
