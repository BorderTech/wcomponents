package com.github.bordertech.wcomponents.test.selenium.element;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Selenium WebElement class representing the functionality of WDialog.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumWDialogWebElement extends SeleniumWComponentWebElement {

	/**
	 * The tag name of the dialog element.
	 */
	public static final String DIALOG_TAG = "dialog";

	/**
	 * The CSS attribute indicating the dialog is open.
	 */
	public static final String DIALOG_OPEN_ATTRIBUTE = "open";

	/**
	 * The CSS class for the maximize/restore button.
	 */
	public static final String MAXIMIZE_RESTORE_CLASS = "wc_maxcont";

	/**
	 * The CSS class for the close button.
	 */
	public static final String CLOSE_CLASS = "wc_dialog_close";

	/**
	 * The CSS class of the content element.
	 */
	public static final String CONTENT_CLASS = "content";

	/**
	 * Get the CSS selector to select an open dialog.
	 *
	 * @return the open dialog CSS selector.
	 */
	public static String getDialogCssSelector() {
		return DIALOG_TAG;
	}

	/**
	 * Get the CSS Selector for an open dialog.
	 *
	 * @return the CSS selector for an open dialog.
	 */
	public static String getOpenDialogCssSelector() {
		return DIALOG_TAG + "[" + DIALOG_OPEN_ATTRIBUTE + "]";
	}

	/**
	 * Construct a dialog for the given component.
	 *
	 * @param element the dialog element.
	 * @param driver the SeleniumWComponentsWebDriver
	 */
	public SeleniumWDialogWebElement(final WebElement element, final WebDriver driver) {

		super(element, driver);

		if (!element.getTagName().equals(DIALOG_TAG)) {

			throw new IllegalArgumentException("element is not a WDialog.");
		}
	}

	/**
	 * Get the maximize/restore button.
	 *
	 * @return the button to maximize/restore the dialog.
	 */
	public SeleniumWComponentWebElement getMaximizeRestoreButton() {
		return findElementImmediate(By.cssSelector("button." + MAXIMIZE_RESTORE_CLASS));
	}

	/**
	 * Get the close button.
	 *
	 * @return the button to close the dialog.
	 */
	public SeleniumWComponentWebElement getCloseButton() {
		return findElementImmediate(By.cssSelector("button." + CLOSE_CLASS));
	}

	/**
	 * Close the dialog.
	 */
	public void close() {
		clickElementNoWait(getCloseButton());
//		getCloseButton().click();
	}

	/**
	 * Get the content container for the dialog.
	 *
	 * @return the element containing the content.
	 */
	public SeleniumWComponentWebElement getContent() {
		return findElementImmediate(By.cssSelector("." + CONTENT_CLASS));
	}

	/**
	 * Get the heading text of the dialog.
	 *
	 * @return the heading text.
	 */
	public String getHeadingText() {
		return getHeading().getText();
	}

	/**
	 * Get the dialog's heading element.
	 *
	 * @return the heading element.
	 */
	public SeleniumWComponentWebElement getHeading() {
		return findElementImmediate(By.cssSelector("header > h1"));
	}

	/**
	 * Is this dialog open (visible)?
	 *
	 * @return true if the dialog is open and visible, else false.
	 */
	public boolean isOpen() {
		try {
			String openAttribute = super.getAttribute(DIALOG_OPEN_ATTRIBUTE);
			return openAttribute != null;
		} catch (final ElementNotVisibleException e) {
			return false;
		}
	}

}
