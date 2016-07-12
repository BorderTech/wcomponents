package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

/**
 *
 * <p>
 * WebDriverType implementation for Safari.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SafariWebDriverType extends WebDriverType<SafariDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "safari";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public SafariDriver getDriverImplementation() {
		return new SafariDriver(getCapabilities());
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.safari();
	}

}
