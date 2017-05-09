package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A Selenium WebElement which models the output of WMessages.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
public class SeleniumWMessageBoxWebElement extends SeleniumWComponentWebElement {

	/**
	 * The HTML element which defines the WMessageBox.
	 */
	public static final String TAG_NAME = "section";

	/**
	 * the className which defines a WMessages wrapper.
	 */
	public static final String CLASS_NAME = "wc-messagebox";

	/**
	 * HTML class of messages in a WMessageBox.
	 */
	public static final String MESSAGE_CLASS_NAME = "wc-message";

	/**
	 * HTML class of errors in a WValidationErrors message box.
	 */
	public static final String ERROR_CLASS_NAME = "wc-error";

	/**
	 * HTML class of a WValidationErrors message box.
	 */
	public static final String VALIDATION_ERRORS_CLASSNAME = "wc-validationerrors";

	/**
	 * HTML class which denotes an error box.
	 */
	public static final String TYPE_ERROR_CLASS_NAME = "wc-messagebox-type-error";

	/**
	 * HTML class which denotes a warning box.
	 */
	public static final String TYPE_WARNING_CLASS_NAME = "wc-messagebox-type-warn";

	/**
	 * HTML class which denotes an information box.
	 */
	public static final String TYPE_INFO_CLASS_NAME = "wc-messagebox-type-info";

	/**
	 * HTML class which denotes a success box.
	 */
	public static final String TYPE_SUCCESS_CLASS_NAME = "wc-messagebox-type-success";

	/**
	 * Create a SeleniumWMessagesWebElement from a generic WebElement.
	 *
	 * @param element a generic WebElement
	 * @param driver the current WebDriver instance
	 */
	public SeleniumWMessageBoxWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		if (!TAG_NAME.equalsIgnoreCase(getTagName())) {
			throw new SystemException("Incorrect element selected for SeleniumWMessageBoxWebElement. Expected tagname to be `"
					+ TAG_NAME + "`but  found: " + getTagName());
		}
		String className = element.getAttribute("class");
		if (!(className.contains(CLASS_NAME) || className.contains(VALIDATION_ERRORS_CLASSNAME))) {
			throw new SystemException("Incorrect element selected for SeleniumWMessageBoxWebElement. Expected className to include `"
					+ CLASS_NAME + "` or `" + VALIDATION_ERRORS_CLASSNAME + "` found: " + className);
		}
		if (getType() == null && !isWValidationErrors()) {
			throw new SystemException("unexpected type for WValidationErrors message box.");
		}
	}

	/**
	 * @return the messages in a message box
	 */
	public List<WebElement> getMessages() {
		if (isWValidationErrors()) {
			return findElementsImmediate(By.className(ERROR_CLASS_NAME));
		}
		return findElementsImmediate(By.className(MESSAGE_CLASS_NAME));
	}

	/**
	 * Get a given message.
	 *
	 * @param idx the index of the message to get
	 * @return the message at the given index
	 */
	public WebElement getMessage(final int idx) {
		return getMessages().get(idx);
	}

	/**
	 * @return the first message in a message box
	 */
	public WebElement getMessage() {
		return getMessage(0);
	}

	/**
	 * Is a WMessageBox actually a WValidationErrors? They look mighty similar.
	 *
	 * @return {@code true} if the element represents a WValidationErrors.
	 */
	private boolean isWValidationErrors() {
		return getElement().getAttribute("class").contains(VALIDATION_ERRORS_CLASSNAME);
	}

	/**
	 * @return the type of the message box or null if a WValidationErrors.
	 */
	private WMessageBox.Type getType() {
		String className = getElement().getAttribute("class");
		if (className.contains(TYPE_ERROR_CLASS_NAME)) {
			if (isWValidationErrors()) {
				return null;
			}
			return WMessageBox.ERROR;
		}
		if (className.contains(TYPE_WARNING_CLASS_NAME)) {
			return WMessageBox.WARN;
		}
		if (className.contains(TYPE_INFO_CLASS_NAME)) {
			return WMessageBox.INFO;
		}
		if (className.contains(TYPE_SUCCESS_CLASS_NAME)) {
			return WMessageBox.SUCCESS;
		}
		throw new SystemException("Unable to determine message box type.");
	}

	/**
	 * @return the type of the message box or null if a WValidationErrors.
	 */
	public WMessageBox.Type getMessageBoxType() {
		return getType();
	}
}
