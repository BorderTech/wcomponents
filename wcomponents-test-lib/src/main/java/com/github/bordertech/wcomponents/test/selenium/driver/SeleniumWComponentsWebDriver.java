package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.SeleniumLauncher;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWDialogWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWEmailFieldWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWPhoneNumberFieldWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTableWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextAreaWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextFieldWebElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * <p>
 * WComponent utility class to wrap a Selenium WebDriver.</p>
 * <p>
 * This class will automatically wait for the WComponents UI to finish loading (including JavaScript and AJAX) where
 * appropriate.</p>
 *
 * @author Joshua Barclay
 * @param <T> - the type of backing WebDriver class.
 * @since 1.2.0
 */
public class SeleniumWComponentsWebDriver<T extends WebDriver> implements WebDriver, TakesScreenshot {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SeleniumWComponentsWebDriver.class);
	/**
	 * Session cookie String name.
	 */
	private static final String SESSION_ID_COOKIE = "JSESSIONID";

	/**
	 * The backing driver.
	 */
	private final T driver;

	/**
	 * Whether there is a session on this driver.
	 */
	private boolean hasSession = false;

	/**
	 * Start a new session, using the current URL. For WComponents application this should reload the first page.
	 */
	public void newSession() {
		newSession(driver.getCurrentUrl());
	}

	/**
	 * @return true if an active session exists, else false.
	 */
	public boolean hasSession() {
		return hasSession;
	}

	/**
	 * Start a new session with the given URL.
	 *
	 * @param url the URL of the page to load.
	 */
	public void newSession(final String url) {

		driver.manage().deleteAllCookies();
		get(url);
	}

	/**
	 * @return the session ID for the driver, or null if no session.
	 */
	public String getSessionId() {

		Cookie cookie = driver.manage().getCookieNamed(SESSION_ID_COOKIE);
		if (cookie == null) {
			return null;
		}

		return cookie.getValue();
	}

	/**
	 * <p>
	 * No-arg constructor to support creation via other frameworks.</p>
	 * <p>
	 * This implementation will be backed by the driver implementation configured in
	 * {@link com.github.bordertech.wcomponents.test.selenium.driver.ParameterizedWebDriverType}</p>
	 */
	public SeleniumWComponentsWebDriver() {
		this((T) new ParameterizedWebDriverType().getDriverImplementation());
	}

	/**
	 * Default constructor.
	 *
	 * @param driver the backing web driver.
	 */
	public SeleniumWComponentsWebDriver(final T driver) {
		this.driver = driver;
	}

	/**
	 * Directly expose the driver for any special APIs.
	 *
	 * @return the actual backing driver implementation.
	 */
	public T getDriver() {
		return driver;
	}

	/**
	 * Wait until the page is fully loaded (including AJAX and timers).
	 */
	public void waitForPageReady() {
		SeleniumWComponentsUtil.waitForPageReady(driver);
	}

	/**
	 * Is there an open dialog on the screen?
	 *
	 * @return true if an open dialog exists, else false.
	 */
	public boolean isOpenDialog() {
		return SeleniumWComponentsUtil.isOpenDialog(this);
	}

	/**
	 * Get the dialog.
	 *
	 * @return the dialog component.
	 */
	public SeleniumWDialogWebElement getDialog() {
		return SeleniumWComponentsUtil.getDialog(this);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void get(final String url) {
		driver.get(url);
		hasSession = true;
		waitForPageReady();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getCurrentUrl() {
		waitForPageReady();
		return driver.getCurrentUrl();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getTitle() {
		waitForPageReady();
		return driver.getTitle();
	}

	/**
	 * Find a WTextArea by the given criteria.
	 *
	 * @param by the By selector.
	 * @return the SeleniumWTextAreaWebElement or null if not found.
	 */
	public SeleniumWTextAreaWebElement findWTextArea(final By by) {
		return new SeleniumWTextAreaWebElement(findElement(by), this);
	}

	/**
	 * Find a WTextField by the given criteria.
	 *
	 * @param by the By selector.
	 * @return the SeleniumWTextFieldWebElement or null if not found.
	 */
	public SeleniumWTextFieldWebElement findWTextField(final By by) {
		return new SeleniumWTextFieldWebElement(findElement(by), this);
	}

	/**
	 * Find a WEmailField by the given criteria.
	 *
	 * @param by the By selector.
	 * @return the SeleniumWEmailFieldWebElement or null if not found.
	 */
	public SeleniumWEmailFieldWebElement findWEmailField(final By by) {
		return new SeleniumWEmailFieldWebElement(findElement(by), this);
	}

	/**
	 * Find a WEmailField by the given criteria.
	 *
	 * @param by the By selector.
	 * @return the SeleniumWPhoneNumberFieldWebElement or null if not found.
	 */
	public SeleniumWPhoneNumberFieldWebElement findWPhoneNumberField(final By by) {
		return new SeleniumWPhoneNumberFieldWebElement(findElement(by), this);
	}

	/**
	 * Find a WTable by the given criteria.
	 *
	 * @param by the By selector.
	 * @return the SeleniumWTableWebElement or null if not found.
	 */
	public SeleniumWTableWebElement findWTable(final By by) {
		return new SeleniumWTableWebElement(findElement(by), this);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public List<WebElement> findElements(final By by) {

		/* Overloading doesn't work properly when the overloaded parameter is a subclass
		of the original parameter (By -> ByWComponent). This logic will mean consumers
		do not have to cast both the parameter and this class to invoke the ByWComponent specific method. */
		if (by instanceof ByWComponent) {
			return findElements((ByWComponent) by);
		} else {
			return findElementsInt(by);
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public SeleniumWComponentWebElement findElement(final By by) {

		/* Overloading doesn't work properly when the overloaded parameter is a subclass
		of the original parameter (By -> ByWComponent). This logic will mean consumers
		do not have to cast both the parameter and this class to invoke the ByWComponent specific method. */
		if (by instanceof ByWComponent) {
			return findElement((ByWComponent) by);
		} else {
			return findElementInt(by);
		}
	}

	/**
	 * <p>
	 * Find WComponents that were created in the same JVM as the servlet.</p>
	 * <p>
	 * This method requires that SeleniumLauncher (or subclass) was used to launch the server.</p>
	 *
	 * @param by the ByWcomponent to find.
	 * @return the matching WebElement.
	 */
	public List<WebElement> findElements(final ByWComponent by) {
		by.setContext(SeleniumLauncher.getContextForSession(getSessionId()));

		return findElementsInt(by);
	}

	/**
	 * <p>
	 * Find a WComponent that was created in the same JVM as the servlet.</p>
	 * <p>
	 * This method requires that SeleniumLauncher (or subclass) was used to launch the server.</p>
	 *
	 * @param by the ByWcomponent to find.
	 * @return the matching WebElement.
	 */
	public SeleniumWComponentWebElement findElement(final ByWComponent by) {
		by.setContext(SeleniumLauncher.getContextForSession(getSessionId()));

		return findElementInt(by);
	}

	/**
	 * Internal implementation to send the findElements command to the driver.
	 *
	 * @param by the By to search.
	 * @return the found WebElement(s).
	 */
	private List<WebElement> findElementsInt(final By by) {
		waitForPageReady();
		List<WebElement> webElements = driver.findElements(by);
		List<WebElement> wrappedList = new ArrayList<>();
		for (WebElement webElement : webElements) {
			wrappedList.add(new SeleniumWComponentWebElement(webElement, this));
		}

		return wrappedList;
	}

	/**
	 * Internal implementation to send the findElement command to the driver.
	 *
	 * @param by the By to search.
	 * @return the found WebElement.
	 */
	private SeleniumWComponentWebElement findElementInt(final By by) {
		waitForPageReady();
		return new SeleniumWComponentWebElement(driver.findElement(by), this);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getPageSource() {
		waitForPageReady();
		return driver.getPageSource();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void close() {
		driver.close();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void quit() {
		driver.quit();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Set<String> getWindowHandles() {
		waitForPageReady();
		return driver.getWindowHandles();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getWindowHandle() {
		waitForPageReady();
		return driver.getWindowHandle();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public TargetLocator switchTo() {
		waitForPageReady();
		return driver.switchTo();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Navigation navigate() {
		waitForPageReady();
		return driver.navigate();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Options manage() {
		return driver.manage();
	}

	/**
	 * <p>
	 * Capture a screenshot if the backing driver supports it.</p>
	 * <p>
	 * <b>IMPORTANT: </b>This function will log warnings and return null if a screenshot cannot be captured.</p>
	 *
	 * @param <X> - the output type.
	 * @param target - the target output type
	 * @return the screenshot from the driver if captured, or null if an error occurs.
	 */
	@Override
	public <X> X getScreenshotAs(final OutputType<X> target) {
		if (!(driver instanceof TakesScreenshot)) {
			//Do not fail here - we might be taking a screenshot in an error state.
			LOG.warn("Attempted to take screenshot but backing driver does not support it.");
			return null;
		}

		try {
			waitForPageReady();
		} catch (WebDriverException wde) {
			//Do not fail here - we might be taking a screenshot in an error state.
			LOG.warn("Failed to wait for page ready prior to capturing screenshot", wde);
			return null;
		}

		return ((TakesScreenshot) driver).getScreenshotAs(target);

	}

}
