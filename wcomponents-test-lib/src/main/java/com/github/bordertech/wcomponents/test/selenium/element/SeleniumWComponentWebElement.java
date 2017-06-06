package com.github.bordertech.wcomponents.test.selenium.element;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 *
 * A wrapper for WebElement to provide specific WComponents behavior.
 *
 * @author Joshua Barclay
 * @author Mark Reeves
 * @since 1.2.0
 */
public class SeleniumWComponentWebElement implements WebElement {

	/**
	 * The backing WebElement.
	 */
	private final WebElement element;

	/**
	 * The driver.
	 */
	private final WebDriver driver;

	/**
	 * Creates a WebElementWrapper.
	 *
	 * @param element the backing element.
	 * @param driver the SeleniumWComponentsWebDriver.
	 */
	public SeleniumWComponentWebElement(final WebElement element, final WebDriver driver) {
		if (element == null) {
			throw new IllegalArgumentException("WComponentWebElement cannot wrap a null element.");
		}
		if (driver == null) {
			throw new IllegalArgumentException("driver must not be null.");
		}
		this.element = element;
		this.driver = driver;
	}

	/**
	 * @return the driver.
	 */
	protected WebDriver getDriver() {
		return driver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		element.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void click() {
		// Wrapped element might already be a WComponent WebElement
		if (element instanceof SeleniumWComponentWebElement) {
			element.click();
		} else {
			element.click();
			waitForPageReady();
		}
	}

	/**
	 * <p>
	 * Perform a click action without waiting for the WComponent ready status</p>
	 * <p>
	 * Used when the click will result in a non-WComponents page.</p>
	 */
	public void clickNoWait() {
		// Wrapped element might already be a WComponent WebElement
		if (element instanceof SeleniumWComponentWebElement) {
			((SeleniumWComponentWebElement) element).clickNoWait();
		} else {
			element.click();
		}
	}

	/**
	 * Find immediate with no polling.
	 *
	 * @param by the by condition
	 * @return the matching element
	 */
	public SeleniumWComponentWebElement findElementImmediate(final By by) {
		if (by instanceof ByWComponent) {
			((ByWComponent) by).setContext(getUserContextForSession());
		}
		try {
			SeleniumWComponentsUtil.configureImmediateImplicitWait(driver);
			return wrapElement(element.findElement(by));
		} finally {
			SeleniumWComponentsUtil.configureImplicitWait(driver);
		}
	}

	/**
	 * Find immediate with no polling.
	 *
	 * @param by the by condition
	 * @return the matching element
	 */
	public List<WebElement> findElementsImmediate(final By by) {
		if (by instanceof ByWComponent) {
			((ByWComponent) by).setContext(getUserContextForSession());
		}
		try {
			SeleniumWComponentsUtil.configureImmediateImplicitWait(driver);
			List<WebElement> webElements = element.findElements(by);
			List<WebElement> wrappedList = new ArrayList<>();
			for (WebElement webElement : webElements) {
				wrappedList.add(wrapElement(webElement));
			}
			return wrappedList;
		} finally {
			SeleniumWComponentsUtil.configureImplicitWait(driver);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SeleniumWComponentWebElement findElement(final By by) {
		return wrapElement(element.findElement(by));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WebElement> findElements(final By by) {
		List<WebElement> elements = new ArrayList<>();
		for (WebElement e : element.findElements(by)) {
			elements.add(wrapElement(e));
		}
		return elements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAttribute(final String name) {
		return element.getAttribute(name);
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
	 * @return {@code true} if the element is hidden in the current UI. This is not exactly the same as the reverse of
	 * isDisplayed as it tests only for the element being hidden using the meachanism internal to WComponents and not
	 * any other (CSS-based) mechanism which may result in isDisplayed() returning {@code false}.
	 */
	public boolean isHidden() {
		return element.getAttribute("hidden") != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendKeys(final CharSequence... keys) {
		element.sendKeys(keys);
	}

	/**
	 * Send a key sequence and optionally wait for page ready afterwards.
	 * @param wait if {@code true} then wait for page ready after sending the keys
	 * @param keys The keys to send
	 */
	public void sendKeys(final boolean wait, final CharSequence... keys) {
		element.sendKeys(keys);
		if (wait) {
			waitForPageReady();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit() {
		element.submit();
		waitForPageReady();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle getRect() {
		return element.getRect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <X> X getScreenshotAs(final OutputType<X> ot) throws WebDriverException {
		return element.getScreenshotAs(ot);
	}

	/**
	 * Get the id of the default "active" part of the component. This may not be the WComponent id.
	 *
	 * @return the active component's id, by default this is the WComponent id.
	 */
	public String getActiveId() {
		return getAttribute("id");
	}

	/**
	 * @return the backing web element
	 */
	public WebElement getElement() {
		return element;
	}

	/**
	 *
	 * @return the user context for this session
	 */
	public UIContext getUserContextForSession() {
		if (driver instanceof SeleniumWComponentsWebDriver) {
			return ((SeleniumWComponentsWebDriver) driver).getUserContextForSession();
		}
		return null;
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript. Uses default values for timeout and polling
	 * interval.
	 *
	 */
	public void waitForPageReady() {
		SeleniumWComponentsUtil.waitForPageReady(driver);
	}

	/**
	 * @param element the element to click with no wait
	 */
	protected void clickElementNoWait(final WebElement element) {
		if (element instanceof SeleniumWComponentWebElement) {
			((SeleniumWComponentWebElement) element).clickNoWait();
		} else {
			element.click();
		}
	}

	/**
	 *
	 * @param element the element to wrap
	 * @return the element wrapped as {@link SeleniumWComponentWebElement}
	 */
	protected SeleniumWComponentWebElement wrapElement(final WebElement element) {
		if (element == null) {
			return null;
		}
		if (element instanceof SeleniumWComponentWebElement) {
			return (SeleniumWComponentWebElement) element;
		}
		return new SeleniumWComponentWebElement(element, getDriver());
	}

}
