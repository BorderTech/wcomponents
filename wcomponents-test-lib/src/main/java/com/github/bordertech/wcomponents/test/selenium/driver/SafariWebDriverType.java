package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

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
public class SafariWebDriverType implements WebDriverType<SafariDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public SafariDriver getDriverImplementation() {
		return new SafariDriver(getOptions());
	}

	/**
	 * Get the Options to use. Exists to allow subclasses to override.
	 *
	 * @return the default Safari options.
	 */
	public SafariOptions getOptions() {
		return new SafariOptions();

	}

}
