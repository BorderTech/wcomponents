package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.test.WComponentTestCase;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * A WComponent test case which tests the UI using Selenium to drive a web browser.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WComponentSeleniumTestCase extends WComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WComponentSeleniumTestCase.class);

	/**
	 * How long to implicitly wait for elements to appear, in milliseconds. If an element does not appear after this
	 * delay, Selenium will throw a NoSuchElementException.
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
	 * Called before each test method to refresh the user session and launch a new browser.
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
	 * Called after each test method to clear the user session and close the browser window.
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
	 * Called once before any test methods, to start the Selenium test LDE if it is not already running.
	 */
	@BeforeClass
	public static void startLde() {
		SeleniumTestSetup.startLde();
	}

	/**
	 * Called once after all test methods have completed, to stop the Selenium test LDE if it is the last test being
	 * run.
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
	 * Retrieves the current driver instance. Subclasses should use this to obtain a driver instance for their tests.
	 * Note that the driver is disposed of between tests, so it should not be cached in the test classes.
	 *
	 * @return the driver to use during testing.
	 */
	protected final WebDriver getDriver() {
		return driver;
	}

	/**
	 * Creates a driver to use during testing. Subclasses may use this to change the browser that is used during
	 * testing.
	 *
	 * @return the driver to use during testing.
	 */
	protected WebDriver createDriver() {
		if ("iexplore".equals(browser)) {
			return new MyInternetExplorerDriver();
		} else if ("firefox".equals(browser)) {
			return new MyFirefoxDriver();
		} else if ("chrome".equals(browser)) {
			return new MyChromeDriver();
		} else {
			// Old behaviour - Default to IE.
			return new MyInternetExplorerDriver();
		}
	}

	/**
	 * Opens the Web Browser to the WComponent being tested.
	 *
	 * @return the driver to use during testing.
	 */
	private WebDriver launchBrowser() {
		driver = createDriver();
		driver.get(SeleniumTestServlet.getServletUrl() + "/" + WebUtilities.escapeForUrl(testId)
				+ "?selenium-ts=" + System.currentTimeMillis()); // cache buster

		return driver;
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent path.
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
	 * Convenience method to create a By search criteria using a WComponent path.
	 *
	 * @param path the WComponent path, see {@link ByWComponentPath} for syntax.
	 * @param value the optional value to narrow the search by (for e.g. drop-down lists).
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponentPath(final String path, final Object value) {
		return new ByWComponentPath(getWrappedUi(), getUIContext(), path, value);
	}

	/**
	 * Convenience method to create a By search criteria using a WComponent.
	 *
	 * @param component the WComponent to search for.
	 * @param value the optional value to narrow the search by (for e.g. drop-down lists).
	 * @return the By search criteria for the given path.
	 */
	protected By byWComponent(final WComponent component, final Object value) {
		return new ByWComponent(component, getUIContext(), value);
	}

	/**
	 * Waits for the page to finish loading and initialising.
	 *
	 * @param driver the Javascript driver.
	 */
	private static void waitForPageReady(final JavascriptExecutor driver) {
		long end = System.currentTimeMillis() + IMPLICIT_WAIT;

		while (System.currentTimeMillis() < end) {
			try {
				Object readyFlag = driver.executeScript("return window.isPageReady && window.isPageReady()");

				if (Boolean.TRUE.equals(readyFlag)) {
					LOG.debug("READY");
					break;
				} else if (LOG.isDebugEnabled()) {
					LOG.debug("STATUS: " + driver.executeScript(
							"return window.isPageReady? window.isPageReady.status() : ''"));
					LOG.debug("PAGE NOT READY. WAITING...");
				}

				Thread.sleep(20);
			} catch (InterruptedException e) {
				// Something's interrupted the test, so there's no point waiting any more.
				break;
			} catch (WebDriverException ignored) {
				// The javascript has thrown an exception.
				// The likely cause is that the page hasn't loaded properly, so ignore the error.
			}
		}
	}

	/**
	 * Extension of InternetExplorerDriver to work around Selenium not waiting for the theme and skin javascript to
	 * complete.
	 */
	private static final class MyInternetExplorerDriver extends InternetExplorerDriver {

		/**
		 * Creates a new MyInternetExplorerDriver.
		 */
		private MyInternetExplorerDriver() {
			manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism to use
		 * @return A list of all {@link WebElement}s, or an empty list if nothing matches
		 */
		@Override
		public List<WebElement> findElements(final By by) {
			waitForPageReady(this);

			List<WebElement> elements = super.findElements(by);

			for (int i = 0; i < elements.size(); i++) {
				elements.set(i, new WebElementWrapper(this, elements.get(i)));
			}

			return elements;
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism
		 * @return The first matching element on the current page
		 */
		@Override
		public WebElement findElement(final By by) {
			waitForPageReady(this);
			WebElement element = super.findElement(by);

			if (by instanceof ByWComponent) {
				Class<? extends WComponent> targetClass = ((ByWComponent) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else if (by instanceof ByWComponentPath) {
				Class<? extends WComponent> targetClass = ((ByWComponentPath) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else {
				return new WebElementWrapper(this, element);
			}
		}
	}

	/**
	 * Extension of FirefoxDriver to work around Selenium not waiting for the theme and skin javascript to complete.
	 */
	private static final class MyFirefoxDriver extends FirefoxDriver {

		/**
		 * Creates a new MyFirefoxDriver.
		 */
		private MyFirefoxDriver() {
			manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism to use
		 * @return A list of all {@link WebElement}s, or an empty list if nothing matches
		 */
		@Override
		public List<WebElement> findElements(final By by) {
			waitForPageReady(this);

			List<WebElement> elements = super.findElements(by);

			for (int i = 0; i < elements.size(); i++) {
				elements.set(i, new WebElementWrapper(this, elements.get(i)));
			}

			return elements;
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism
		 * @return The first matching element on the current page
		 */
		@Override
		public WebElement findElement(final By by) {
			waitForPageReady(this);
			WebElement element = super.findElement(by);

			if (by instanceof ByWComponent) {
				Class<? extends WComponent> targetClass = ((ByWComponent) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else if (by instanceof ByWComponentPath) {
				Class<? extends WComponent> targetClass = ((ByWComponentPath) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else {
				return new WebElementWrapper(this, element);
			}
		}
	}

	/**
	 * Extension of ChromeDriver to work around Selenium not waiting for the theme and skin javascript to complete.
	 */
	private static final class MyChromeDriver extends ChromeDriver {

		/**
		 * Creates a new MyChromeDriver.
		 */
		private MyChromeDriver() {
			super(capabilities());
			manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
		}

		/**
		 * @return the desired capabilities for the driver.
		 */
		private static Capabilities capabilities() {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized", "--disable-popup-blocking"));
			return capabilities;
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism to use
		 * @return A list of all {@link WebElement}s, or an empty list if nothing matches
		 */
		@Override
		public List<WebElement> findElements(final By by) {
			waitForPageReady(this);

			List<WebElement> elements = super.findElements(by);

			for (int i = 0; i < elements.size(); i++) {
				elements.set(i, new WebElementWrapper(this, elements.get(i)));
			}

			return elements;
		}

		/**
		 * Fix for Selenium webdriver issue #26 - Should block on AJAX calls.
		 *
		 * @param by The locating mechanism
		 * @return The first matching element on the current page
		 */
		@Override
		public WebElement findElement(final By by) {
			waitForPageReady(this);
			WebElement element = super.findElement(by);

			if (by instanceof ByWComponent) {
				Class<? extends WComponent> targetClass = ((ByWComponent) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else if (by instanceof ByWComponentPath) {
				Class<? extends WComponent> targetClass = ((ByWComponentPath) by).getTargetWComponentClass();
				return new WebElementWrapper(this, element, targetClass);
			} else {
				return new WebElementWrapper(this, element);
			}
		}
	}

	/**
	 * A Wrapper for a web-element to provide developers some further insulation from theme and skin changes.
	 *
	 * This also contains a work-around for Selenium not waiting for javascript form submission. A delay is inserted
	 * after each form control is used.
	 */
	private static final class WebElementWrapper implements WebElement {

		/**
		 * Sleep delay after using a control, in milliseconds. This only needs to sleep for as long as themes take to
		 * submit the form, trigger AJAX, subordinates etc.
		 */
		private static final int SLEEP_DELAY = 100;

		/**
		 * The backing WebElement.
		 */
		private final WebElement element;

		/**
		 * The WComponent corresponding to the WebElement.
		 */
		private final Class<? extends WComponent> componentClass;

		/**
		 * The driver instance which created this wrapper.
		 */
		private final WebDriver driver;

		/**
		 * Creates a WebElementWrapper.
		 *
		 * @param driver the driver instance which created this wrapper.
		 * @param element the backing element.
		 */
		private WebElementWrapper(final WebDriver driver, final WebElement element) {
			this(driver, element, null);
		}

		/**
		 * Creates a WebElementWrapper.
		 *
		 * @param driver the driver instance which created this wrapper.
		 * @param element the backing element.
		 * @param componentClass the class for the component corresponding to the element.
		 */
		private WebElementWrapper(final WebDriver driver, final WebElement element,
				final Class<? extends WComponent> componentClass) {
			this.driver = driver;
			this.element = element;
			this.componentClass = componentClass;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			element.clear();
			sleep();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void click() {
			/*|| WTab.class.equals(componentClass)
|| WMenu.class.equals(componentClass)
|| WMenuItem.class.equals(componentClass)*/

			if (WCollapsible.class.equals(componentClass)) {
				// These elements are not clickable directly - we have to find the actual control to click on
				element.findElement(By.xpath(".//button")).click();
			} else {
				element.click();
			}

			sleep();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public WebElement findElement(final By by) {
			return element.findElement(by);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<WebElement> findElements(final By by) {
			return element.findElements(by);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAttribute(final String name) {
			if ((driver instanceof MyInternetExplorerDriver) && "value".equals(name) && "textarea".equals(getTagName())) {
				return element.getAttribute(name).replaceAll("\r\n", "\n");
			} else {
				return element.getAttribute(name);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTagName() {
			return element.getTagName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			return element.getText();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEnabled() {
			return element.isEnabled();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSelected() {
			return element.isSelected();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void sendKeys(final CharSequence... keys) {
			element.sendKeys(keys);
			sleep();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void submit() {
			element.submit();
			sleep();
		}

		/**
		 * Pauses the current thread for a specified interval.
		 *
		 * @see #SLEEP_DELAY
		 */
		private void sleep() {
			try {
				synchronized (this) {
					wait(SLEEP_DELAY);
					waitForPageReady((JavascriptExecutor) driver);
				}
			} catch (InterruptedException e) {
				LOG.warn("Interrupted", e);
				throw new SystemException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDisplayed() {
			return element.isDisplayed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point getLocation() {
			return element.getLocation();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Dimension getSize() {
			return element.getSize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getCssValue(final String propertyName) {
			return element.getCssValue(propertyName);
		}
	}
}
