package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Parent class for all elements that represent user input. An element might be an input even if it represent read-only
 * state, e.g. a &lt;span&gt; element for a read-only text input.
 *
 * @author Joshua Barclay
 * @author Mark Reeves
 * @since 1.3.0
 */
public class SeleniumWComponentInputWebElement extends SeleniumWComponentWebElement {

	/**
	 * The tag name of the wrapping element for text-like inputs WTextField, WEmailField, WPhoneNumberField,
	 * WPasswordField.
	 */
	public static final String TOP_LEVEL_TAG = "span";

	/**
	 * The tag name of the editable CheckBox element.
	 */
	public static final String EDITABLE_TAG = "input";

	/**
	 * Construct an input element.
	 *
	 * @param element the web element.
	 * @param driver the driver.
	 */
	public SeleniumWComponentInputWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
	}

	/**
	 * @return the editable input field of a WTextField.
	 */
	public SeleniumWComponentWebElement getInputField() {
		if (isReadOnly()) {
			return null;
		}

		return findElementImmediate(By.tagName(EDITABLE_TAG));
	}

	/**
	 * @return the value of the input component.
	 */
	public String getValue() {
		if (isReadOnly()) {
			return getText();
		}
		return getInputField().getAttribute(SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString());
	}

	/**
	 * Retrieve the given attribute for the element, but if the attribute requested is the value instead return the
	 * result of getValue().
	 *
	 * @param name the attribute to find
	 * @return the value of the attribute
	 */
	@Override
	public String getAttribute(final String name) {
		if (SeleniumWComponentWebProperties.ATTRIBUTE_HTML_VALUE.toString().equals(name)) {
			return getValue();
		}
		return super.getAttribute(name);
	}

	/**
	 * @return true if the field is in a read-only state.
	 */
	public boolean isReadOnly() {
		return !Util.empty(getAttribute("data-wc-component"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		if (isReadOnly()) {
			return false;
		}
		return getInputField().isEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendKeys(final CharSequence... keys) {
		getInputField().sendKeys(keys);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendKeys(final boolean wait, final CharSequence... keys) {
		getInputField().sendKeys(wait, keys);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		getInputField().clear();
	}

	@Override
	public String getActiveId() {
		if (isReadOnly()) {
			return super.getActiveId();
		}
		return getInputField().getAttribute("id");
	}

	@Override
	public void click() {
		if (isReadOnly()) {
			super.click();
		} else {
			getInputField().click();
		}
	}

	@Override
	public void clickNoWait() {
		if (isReadOnly()) {
			super.clickNoWait();
		} else {
			getInputField().clickNoWait();
		}
	}

	/**
	 * @return {@code true} if the input is a combo box
	 */
	public boolean isCombo() {
		if (isReadOnly()) {
			return false;
		}
		return null != getAttribute("data-wc-suggest");
	}

	/**
	 * @return {@code true} if the input is mandatory
	 */
	public boolean isMandatory() {
		if (isReadOnly()) {
			return false;
		}
		return null != getInputField().getAttribute("required");
	}

	/**
	 * @return the element representing a combo boxes list of suggestions if the element is a combo otherwise
	 * {@code null}
	 */
	public WebElement getSuggestionList() {
		if (!isCombo()) {
			return null;
		}
		String listId = getAttribute("data-wc-suggest");
		if (Util.empty(listId)) {
			return null;
		}
		return SeleniumWComponentsUtil.findElementImmediateForDriver(getDriver(), By.xpath("//*[@id='" + listId + "']"));
	}
}
