package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.test.WComponentTestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * A WComponent test case which tests the UI using Selenium to drive a web
 * browser.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class WComponentSeleniumTestCase extends WComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WComponentSeleniumTestCase.class);

	/**
	 * How long to implicitly wait for elements to appear, in milliseconds. If
	 * an element does not appear after this delay, Selenium will throw a
	 * NoSuchElementException.
	 */
	private static final int IMPLICIT_WAIT = 1000;

	/**
	 * The driver to use in testing.
	 */
	private WebDriver driver;

	/**
	 * The browser to use to run the test.
	 */
	private String browser;

	/**
	 * A sequence used to generate the {@link #testId} field.
	 */
	private static int testSequenceNumber = 0;

	/**
	 * The unique test identifier.
	 */
	private final String testId;

	/**
	 * Creates a WComponentSeleniumTestCase.
	 *
	 * @param ui the UI to test.
	 */
	public WComponentSeleniumTestCase(final WComponent ui) {
		super(ui);

		synchronized (WComponentSeleniumTestCase.class) {
			testId = getClass().getSimpleName() + '.' + testSequenceNumber++;
		}
	}

	/**
	 * Called before each test method to refresh the user session and launch a
	 * new browser.
	 */
	@Before
	public void setUp() {
		UIContext uic = getUIContext();
		SeleniumTestServlet.setUiContext(testId, uic);
		UIContextHolder.reset();
		UIContextHolder.pushContext(uic);

		// Ensure the browser has hit the test servlet,
		// otherwise the UIContext will not have been properly initialised.
		int oldCount = SeleniumTestServlet.getServiceCount();
		driver = launchBrowser();

		long end = System.currentTimeMillis() + IMPLICIT_WAIT;

		while (System.currentTimeMillis() < end && oldCount == SeleniumTestServlet.getServiceCount()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignored) {
				// Something's interrupted the test, so there's no point waiting any more.
				break;
			}
		}
	}

	/**
	 * Called after each test method to clear the user session and close the
	 * browser window.
	 */
	@After
	public void tearDown() {
		resetUIContext();
		SeleniumTestServlet.removeUiContext(testId);
		UIContextHolder.reset();
		driver.quit();
		driver = null;
	}

	/**
	 * Called once before any test methods, to start the Selenium test LDE if it
	 * is not already running.
	 */
	@BeforeClass
	public static void startLde() {
		SeleniumTestSetup.startLde();
	}

	/**
	 * Called once after all test methods have completed, to stop the Selenium
	 * test LDE if it is the last test being run.
	 */
	@AfterClass
	public static void stopLde() {
		SeleniumTestSetup.stopLde();
	}

	/**
	 * Sets the browser to use to run the test.
	 *
	 * @param browser the browser to use.
	 */
	public void setBrowser(final String browser) {
		this.browser = browser;
	}

	/**
	 * Retrieves the current driver instance. Subclasses should use this to
	 * obtain a driver instance for their tests. Note that the driver is
	 * disposed of between tests, so it should not be cached in the test
	 * classes.
	 *
	 * @return the driver to use during testing.
	 */
	protected final WebDriver getDriver() {
		return driver;
	}

	/**
	 * Opens the Web Browser to the WComponent being tested.
	 *
	 * @return the driver to use during testing.
	 */
	private WebDriver launchBrowser() {
		driver = getDriver();
		driver.get(SeleniumTestServlet.getServletUrl() + "/" + WebUtilities.escapeForUrl(testId)
				+ "?selenium-ts=" + System.currentTimeMillis()); // cache buster

		return driver;
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent
	 * path.
	 *
	 * @param path the WComponent path, see {@link ByWComponentPath} for syntax.
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponentPath(final String path) {
		return new ByWComponentPath(getWrappedUi(), getUIContext(), path);
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent.
	 *
	 * @param component the WComponent to search for.
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponent(final WComponent component) {
		return new ByWComponent(component, getUIContext());
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent
	 * path.
	 *
	 * @param path the WComponent path, see {@link ByWComponentPath} for syntax.
	 * @param value the optional value to narrow the search by (for e.g.
	 * drop-down lists).
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponentPath(final String path, final Object value) {
		return new ByWComponentPath(getWrappedUi(), getUIContext(), path, value);
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent.
	 *
	 * @param component the WComponent to search for.
	 * @param value the optional value to narrow the search by (for e.g.
	 * drop-down lists).
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponent(final WComponent component, final Object value) {
		return new ByWComponent(component, getUIContext(), value);
	}
}
