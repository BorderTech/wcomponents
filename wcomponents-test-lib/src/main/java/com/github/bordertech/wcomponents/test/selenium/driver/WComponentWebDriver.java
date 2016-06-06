package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.test.selenium.element.WComponentWebElement;
import com.github.bordertech.wcomponents.test.selenium.WComponentSelenium;
import com.github.bordertech.wcomponents.test.selenium.element.WDialogWebElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * <p>
 * WComponent utility class to wrap a Selenium WebDriver.</p>
 * <p>
 * This class will automatically wait for the WComponents UI to finish loading
 * (including JavaScript and AJAX) where appropriate.</p>
 *
 * @author Joshua Barclay
 * @param <T> - the type of backing WebDriver class.
 * @since 1.2.0
 */
public class WComponentWebDriver<T extends WebDriver> implements WebDriver, TakesScreenshot {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WComponentWebDriver.class);

	/**
	 * The backing driver.
	 */
	private final T driver;

	/**
	 * Default constructor.
	 *
	 * @param driver the backing web driver.
	 */
	public WComponentWebDriver(final T driver) {
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
	 * Is there an open dialog on the screen?
	 *
	 * @return true if an open dialog exists, else false.
	 */
	public boolean isOpenDialog() {
		return WComponentSelenium.isOpenDialog(this);
	}

	/**
	 * Get the dialog.
	 *
	 * @return the dialog component.
	 */
	public WDialogWebElement getDialog() {
		return WComponentSelenium.getDialog(this);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void get(final String url) {
		driver.get(url);
		WComponentSelenium.waitForPageReady(driver);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getCurrentUrl() {
		WComponentSelenium.waitForPageReady(driver);
		return driver.getCurrentUrl();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getTitle() {
		WComponentSelenium.waitForPageReady(driver);
		return driver.getTitle();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public List<WebElement> findElements(final By by) {
		WComponentSelenium.waitForPageReady(driver);
		List<WebElement> webElements = driver.findElements(by);
		List<WebElement> wrappedList = new ArrayList<>();
		for (WebElement webElement : webElements) {
			wrappedList.add(new WComponentWebElement(webElement));
		}

		return wrappedList;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public WComponentWebElement findElement(final By by) {
		WComponentSelenium.waitForPageReady(driver);
		return new WComponentWebElement(driver.findElement(by));
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getPageSource() {
		WComponentSelenium.waitForPageReady(driver);
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
		WComponentSelenium.waitForPageReady(driver);
		return driver.getWindowHandles();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getWindowHandle() {
		WComponentSelenium.waitForPageReady(driver);
		return driver.getWindowHandle();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public TargetLocator switchTo() {
		WComponentSelenium.waitForPageReady(driver);
		return driver.switchTo();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public Navigation navigate() {
		WComponentSelenium.waitForPageReady(driver);
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
	 * <b>IMPORTANT: </b>This function will log warnings and return null if a
	 * screenshot cannot be captured.</p>
	 *
	 * @param <X> - the output type.
	 * @param target - the target output type
	 * @return the screenshot from the driver if captured, or null if an error
	 * occurs.
	 */
	@Override
	public <X> X getScreenshotAs(final OutputType<X> target) {
		if (!(driver instanceof TakesScreenshot)) {
			//Do not fail here - we might be taking a screenshot in an error state.
			LOG.warn("Attempted to take screenshot but backing driver does not support it.");
			return null;
		}

		try {
			WComponentSelenium.waitForPageReady(driver);
		} catch (WebDriverException wde) {
			//Do not fail here - we might be taking a screenshot in an error state.
			LOG.warn("Failed to wait for page ready prior to capturing screenshot", wde);
			return null;
		}

		return ((TakesScreenshot) driver).getScreenshotAs(target);

	}

}
