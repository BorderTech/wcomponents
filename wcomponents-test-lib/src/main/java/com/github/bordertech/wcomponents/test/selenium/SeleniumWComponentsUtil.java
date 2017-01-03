package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWCheckBoxWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWComponentInputWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWDialogWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWEmailFieldWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWRadioButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextAreaWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextFieldWebElement;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.BooleanUtils;
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
 * Utility class containing convenience methods for testing WComponents with
 * Selenium.</p>
 * <p>
 * Logic has been extracted into this utility class for any consumers who cannot
 * extend WComponentSeleniumTestCase due to a different test class
 * hierarchy.</p>
 *
 * @author Joshua Barclay
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
				WebElement body = driver.findElement(By.tagName("body"));
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

		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
//		driver.manage().window().fullscreen();
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript.
	 * Uses default values for timeout and polling interval.
	 *
	 * @param driver the WebDriver.
	 */
	public static void waitForPageReady(final WebDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		SeleniumWComponentsUtil.waitForPageReady(driver, PAGE_READY_WAIT_TIMEOUT, PAGE_READY_POLL_INTERVAL);
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript.
	 *
	 * @param driver the WebDriver.
	 * @param timeoutSeconds - the number of seconds after which the 'wait'
	 * will time out.
	 * @param pollingMilliseconds - the number of milliseconds to wait
	 * between each poll attempt.
	 */
	public static void waitForPageReady(final WebDriver driver, final int timeoutSeconds, final long pollingMilliseconds) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
		wait.pollingEvery(pollingMilliseconds, TimeUnit.MILLISECONDS);
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
			driver.findElement(By.cssSelector(SeleniumWDialogWebElement.getOpenDialogCssSelector()));
			return true;
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
		WebElement dialog = driver.findElement(By.cssSelector(SeleniumWDialogWebElement.getDialogCssSelector()));
		return new SeleniumWDialogWebElement(dialog, driver);
	}

	/**
	 * Get the ExpectedCondition for waiting for the WComponents page to be
	 * ready.
	 *
	 * @return the WaitCondition for page ready.
	 */
	public static ExpectedCondition<Boolean> getPageReadyCondition() {
		return PAGE_WAIT_CONDITION;
	}

	/**
	 * Analyze the input element and attempt to wrap it in the appropriate
	 * component-specific subclass. If not component specific subclass can
	 * be identified then the element will be wrapped in a
	 * SeleniumWComponentWebElement.
	 *
	 * @param driver the WebDriver.
	 * @param element the default Selenium WebElement.
	 * @return a subtype of SeleniumWComponentWebElement specific to the
	 * element type.
	 */
	public static SeleniumWComponentInputWebElement wrapInputElementWithTypedWebElement(final WebDriver driver, final WebElement element) {

		String tag = element.getTagName();

		if (tag.equals(SeleniumWComponentInputWebElement.EDITABLE_TAG)) {
			String type = element.getAttribute("type");
			switch (type) {
				case SeleniumWCheckBoxWebElement.TYPE:
					return new SeleniumWCheckBoxWebElement(element, driver);
				case SeleniumWTextFieldWebElement.TYPE:
					//Text fields have a wrapping Span, we want to wrap that
					return new SeleniumWTextFieldWebElement(element.findElement(By.xpath("..")), driver);
				case SeleniumWEmailFieldWebElement.TYPE:
					return new SeleniumWEmailFieldWebElement(element, driver);
				case SeleniumWRadioButtonWebElement.TYPE:
					return new SeleniumWRadioButtonWebElement(element, driver);
				default:
					return new SeleniumWComponentInputWebElement(element, driver);
			}
		} else if (tag.equals(SeleniumWTextAreaWebElement.EDITABLE_TAG)) {
			return new SeleniumWTextAreaWebElement(element, driver);
		}

		return new SeleniumWComponentInputWebElement(element, driver);
	}

	/**
	 * Default constructor is hidden. Static utility class.
	 */
	private SeleniumWComponentsUtil() {
		//No impl
	}

}
