package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
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
public class SeleniumWMessagesWebElement extends SeleniumWComponentWebElement {

	/**
	 * the className which defines a WMessages wrapper. This is just a WPanel. There is no guarantee that the WPanel is
	 * in fact a WMessages wrapper.
	 */
	public static final String CLASS_NAME = "wc-panel";

	/**
	 * Create a SeleniumWMessagesWebElement from a generic WebElement.
	 *
	 * @param element a generic WebElement
	 * @param driver the current WebDriver instance
	 */
	public SeleniumWMessagesWebElement(final WebElement element, final WebDriver driver) {
		super(element, driver);
		String className = element.getAttribute("class");
		if (!className.contains(CLASS_NAME)) {
			throw new SystemException("Incorrect element selected for SeleniumWMessagesWebElement. Expected className to include `"
					+ CLASS_NAME + "` found: " + className);
		}
	}

	/**
	 * @return a List of the WMessageBox children of the WMessages
	 */
	public List<SeleniumWMessageBoxWebElement> getMessageBoxes() {
		WebDriver driver = getDriver();
		By by = By.cssSelector(SeleniumWMessageBoxWebElement.TAG_NAME);
		List<WebElement> boxes = SeleniumWComponentsUtil.findElementsImmediateForDriver(driver, by);
		List<SeleniumWMessageBoxWebElement> result = new ArrayList<>();
		for (WebElement w : boxes) {
			result.add(new SeleniumWMessageBoxWebElement(w, driver));
		}
		return result;
	}

	/**
	 * Get the WMessageBox child at a given index.
	 *
	 * @param index the child index
	 * @return a WebElement corresponding to the WMessageBox child.
	 */
	public SeleniumWMessageBoxWebElement getMessageBox(final int index) {
		List<SeleniumWMessageBoxWebElement> boxes = getMessageBoxes();
		return boxes.get(index);
	}

	/**
	 * @return the first WMessageBox child of the wrapper.
	 */
	public SeleniumWMessageBoxWebElement getMessageBox() {
		return getMessageBox(0);
	}

	/**
	 * @return an error box descendant of the WMessages - may be a WValidationErrors
	 */
	public WebElement getErrorBox() {
		By by = By.className(SeleniumWMessageBoxWebElement.TYPE_ERROR_CLASS_NAME);
		return findViaDriver(by);
	}

	/**
	 * @return a warning box descendant of the WMessages
	 */
	public WebElement getWarningBox() {
		By by = By.className(SeleniumWMessageBoxWebElement.TYPE_WARNING_CLASS_NAME);
		return findViaDriver(by);
	}

	/**
	 * @return an info box descendant of the WMessages
	 */
	public WebElement getInfoBox() {
		By by = By.className(SeleniumWMessageBoxWebElement.TYPE_INFO_CLASS_NAME);
		return findViaDriver(by);
	}

	/**
	 * @return a success box descendant of the WMessages
	 */
	public WebElement getSuccessBox() {
		By by = By.className(SeleniumWMessageBoxWebElement.TYPE_SUCCESS_CLASS_NAME);
		return findViaDriver(by);
	}

	/**
	 * @param by the match condition
	 * @return the web element
	 */
	private WebElement findViaDriver(final By by) {
		return SeleniumWComponentsUtil.findElementImmediateForDriver(getDriver(), by);
	}

}
