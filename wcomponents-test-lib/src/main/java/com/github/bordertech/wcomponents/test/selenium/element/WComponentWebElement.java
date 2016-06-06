package com.github.bordertech.wcomponents.test.selenium.element;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 *
 * A wrapper for WebElement to provide specific WComponents behavior.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class WComponentWebElement implements WebElement {

	/**
	 * The backing WebElement.
	 */
	private final WebElement element;

	/**
	 * Creates a WebElementWrapper.
	 *
	 * @param element the backing element.
	 */
	public WComponentWebElement(final WebElement element) {
		if (element == null) {
			throw new IllegalArgumentException("WComponetWebElement cannot wrap a null element.");
		}
		this.element = element;
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
		element.click();
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

		if ("value".equals(name) && "textarea".equals(getTagName())) {
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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit() {
		element.submit();

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

}
