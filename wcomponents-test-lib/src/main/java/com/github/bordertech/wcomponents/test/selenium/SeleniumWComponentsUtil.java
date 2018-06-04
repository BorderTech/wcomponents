package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWCheckBoxWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentInputWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWDialogWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWEmailFieldWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWRadioButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWSelectWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextAreaWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextFieldWebElement;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * <p>
 * Utility class containing convenience methods for testing WComponents with Selenium.</p>
 * <p>
 * Logic has been extracted into this utility class for any consumers who cannot extend WComponentSeleniumTestCase due
 * to a different test class hierarchy.</p>
 *
 * @author Joshua Barclay
 * @author Mark Reeves
 * @author Jonathan Austin
 * @since 1.2.0
 */
public final class SeleniumWComponentsUtil {

	/**
	 * The body tag indicating the page is ready.
	 */
	private static final String DATA_READY_TAG = ConfigurationProperties.getTestSeleniumDataReadyTag();

	/**
	 * The default page-ready timeout duration.
	 */
	private static final int PAGE_READY_WAIT_TIMEOUT = ConfigurationProperties.getTestSeleniumPageReadyTimeout();

	/**
	 * The default page-ready poll interval (milliseconds).
	 */
	private static final long PAGE_READY_POLL_INTERVAL = ConfigurationProperties.getTestSeleniumPageReadyPollInterval();

	/**
	 * The number of seconds to wait for an element to be available.
	 */
	private static final long IMPLICIT_WAIT_SECONDS = ConfigurationProperties.getTestSeleniumImplicitWait();

	/**
	 * The screen width in pixels.
	 */
	private static final int SCREEN_WIDTH = ConfigurationProperties.getTestSeleniumScreenWidth();

	/**
	 * The screen height in pixels.
	 */
	private static final int SCREEN_HEIGHT = ConfigurationProperties.getTestSeleniumScreenHeight();

	/**
	 * The expected condition for a page being ready.
	 */
	private static final ExpectedCondition<Boolean> PAGE_WAIT_CONDITION = new ExpectedCondition<Boolean>() {
		/**
		 * Wait for the WComponents page to be ready.
		 *
		 * @param driver - the web driver
		 * @return true when the page is ready, false otherwise.
		 */
		@Override
		public Boolean apply(final WebDriver driver) {

			if (driver == null) {
				throw new IllegalArgumentException("a driver must be provided.");
			}

			boolean domReady;
			try {
				WebElement body;
				if (driver instanceof SeleniumWComponentsWebDriver) {
					SeleniumWComponentsWebDriver wcDriver = (SeleniumWComponentsWebDriver) driver;
					// Dont wait "again" for element
					body = wcDriver.findElement(By.tagName("body"), false);
				} else {
					body = driver.findElement(By.tagName("body"));
				}
				String domReadyAttr = body.getAttribute(DATA_READY_TAG);
				// If value is 'true' or the tag does not exist, the dom is ready.
				// The tag will only not exist if there has been an error, and the page is not actual WComponents.
				domReady = BooleanUtils.isNotFalse(BooleanUtils.toBooleanObject(domReadyAttr));
			} catch (final StaleElementReferenceException e) {

				// It's possible the test got in too quick between loads.
				// The element is stale if the page has been reloaded
				// Run the test again with the new page.
				domReady = false;
			}

			return domReady;

		}
	};

	/**
	 * Configure the WebDriver with the standard WComponents configuration.
	 *
	 * @param driver the WebDriver to configure.
	 */
	public static void configureDriver(final WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		configureImplicitWait(driver);
		driver.manage().window().setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
	}

	/**
	 * Configure the WebDriver implicit wait.
	 *
	 * @param driver the WebDriver to configure.
	 */
	public static void configureImplicitWait(final WebDriver driver) {
		configureImplicitWait(driver, IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
	}

	/**
	 * Configure the WebDriver implicit wait for immediate find.
	 *
	 * @param driver the WebDriver to configure.
	 */
	public static void configureImmediateImplicitWait(final WebDriver driver) {
		// Only change the wait if it has a value
		if (IMPLICIT_WAIT_SECONDS != 0) {
			configureImplicitWait(driver, 0, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Configure the WebDriver implicit wait.
	 *
	 * @param driver the WebDriver to configure.
	 * @param time the amount of time to wait.
	 * @param unit the unit of measure for {@code time}.
	 */
	public static void configureImplicitWait(final WebDriver driver, final long time, final TimeUnit unit) {
		driver.manage().timeouts().implicitlyWait(time, unit);
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript. Uses default values for timeout and polling
	 * interval.
	 *
	 * @param driver the WebDriver.
	 */
	public static void waitForPageReady(final WebDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		waitForPageReady(driver, PAGE_READY_WAIT_TIMEOUT, PAGE_READY_POLL_INTERVAL);
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript.
	 *
	 * @param driver the WebDriver.
	 * @param timeoutSeconds - the number of seconds after which the 'wait' will time out.
	 * @param pollingMilliseconds - the number of milliseconds to wait between each poll attempt.
	 */
	public static void waitForPageReady(final WebDriver driver, final int timeoutSeconds, final long pollingMilliseconds) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}
		WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds, pollingMilliseconds);
		wait.until(getPageReadyCondition());
	}

	/**
	 * Is there an open dialog on the screen?
	 *
	 * @param driver the WebDriver.
	 * @return true if an open dialog exists, else false.
	 */
	public static boolean isOpenDialog(final WebDriver driver) {
		try {
			By by = By.cssSelector(SeleniumWDialogWebElement.getOpenDialogCssSelector());
			WebElement element = findElementImmediateForDriver(driver, by);
			return element != null;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Get the screen's dialog, whether it is open or not.
	 *
	 * @param driver the WebDriver.
	 *
	 * @return a WDialogWebElement for the dialog.
	 */
	public static SeleniumWDialogWebElement getDialog(final WebDriver driver) {
		By by = By.cssSelector(SeleniumWDialogWebElement.getOpenDialogCssSelector());
		WebElement dialog = findElementImmediateForDriver(driver, by);
		return dialog == null ? null : new SeleniumWDialogWebElement(dialog, driver);
	}

	/**
	 * Get the ExpectedCondition for waiting for the WComponents page to be ready.
	 *
	 * @return the WaitCondition for page ready.
	 */
	public static ExpectedCondition<Boolean> getPageReadyCondition() {
		return PAGE_WAIT_CONDITION;
	}

	/**
	 * Analyze the input element and attempt to wrap it in the appropriate component-specific subclass. If not component
	 * specific subclass can be identified then the element will be wrapped in a SeleniumWComponentWebElement.
	 *
	 * @param driver the WebDriver.
	 * @param element the default Selenium WebElement.
	 * @return a subtype of SeleniumWComponentWebElement specific to the element type.
	 */
	public static SeleniumWComponentInputWebElement wrapInputElementWithTypedWebElement(final WebDriver driver, final WebElement element) {

		String tag = element.getTagName();

		if (tag.equals(SeleniumWComponentInputWebElement.EDITABLE_TAG)) {
			String type = element.getAttribute("type");
			WebElement el = element.findElement(By.xpath(".."));
			switch (type) {
				case SeleniumWCheckBoxWebElement.TYPE:
					return new SeleniumWCheckBoxWebElement(el, driver);
				case SeleniumWTextFieldWebElement.TYPE:
					//Text fields have a wrapping Span, we want to wrap that
					return new SeleniumWTextFieldWebElement(el, driver);
				case SeleniumWEmailFieldWebElement.TYPE:
					return new SeleniumWEmailFieldWebElement(el, driver);
				case SeleniumWRadioButtonWebElement.TYPE:
					return new SeleniumWRadioButtonWebElement(el, driver);
				default:
					return new SeleniumWComponentInputWebElement(el, driver);
			}
		} else if (tag.equals(SeleniumWTextAreaWebElement.TEXTAREA_TAG)) {
			return new SeleniumWTextAreaWebElement(element.findElement(By.xpath("..")), driver);
		} else if (tag.equals(SeleniumWSelectWebElement.SELECT_TAG)) {
			return new SeleniumWSelectWebElement(element.findElement(By.xpath("..")), driver);
		}

		return new SeleniumWComponentInputWebElement(element, driver);
	}

	/**
	 * Find immediately the first element via the driver using the given method.
	 *
	 * @param driver the web driver
	 * @param by the by condition
	 * @return the web element
	 */
	public static WebElement findElementImmediateForDriver(final WebDriver driver, final By by) {
		if (driver instanceof SeleniumWComponentsWebDriver) {
			return ((SeleniumWComponentsWebDriver) driver).findElementImmediate(by);
		} else {
			return driver.findElement(by);
		}
	}

	/**
	 * Find immediately the elements via the driver using the given method.
	 *
	 * @param driver the web driver
	 * @param by the by condition
	 * @return the web element
	 */
	public static List<WebElement> findElementsImmediateForDriver(final WebDriver driver, final By by) {
		if (driver instanceof SeleniumWComponentsWebDriver) {
			return ((SeleniumWComponentsWebDriver) driver).findElementsImmediate(by);
		} else {
			return driver.findElements(by);
		}
	}

	/**
	 * Find immediately the first element via the passed in element and given method.
	 *
	 * @param element the web driver
	 * @param by the by condition
	 * @return the web element
	 */
	public static WebElement findElementImmediateForElement(final WebElement element, final By by) {
		if (element instanceof SeleniumWComponentWebElement) {
			return ((SeleniumWComponentWebElement) element).findElementImmediate(by);
		} else {
			return element.findElement(by);
		}
	}

	/**
	 * Find immediately the elements via the passed in element and given method.
	 *
	 * @param element the web driver
	 * @param by the by condition
	 * @return the web element
	 */
	public static List<WebElement> findElementsImmediateForElement(final WebElement element, final By by) {
		if (element instanceof SeleniumWComponentWebElement) {
			return ((SeleniumWComponentWebElement) element).findElementsImmediate(by);
		} else {
			return element.findElements(by);
		}
	}

	/**
	 * Default constructor is hidden. Static utility class.
	 */
	private SeleniumWComponentsUtil() {
		//No impl
	}

}
