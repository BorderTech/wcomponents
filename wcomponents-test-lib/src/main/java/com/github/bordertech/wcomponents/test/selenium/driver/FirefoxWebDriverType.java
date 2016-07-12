package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for Firefox.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class FirefoxWebDriverType extends WebDriverType<FirefoxDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "firefox";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public FirefoxDriver getDriverImplementation() {
		return new FirefoxDriver(getFirefoxBinary(), getFirefoxProfile(), getCapabilities());
	}

	/**
	 * Get the FirefoxBinary to use. Exists to allow subclasses to override.
	 *
	 * @return the default Firefox binary.
	 */
	public FirefoxBinary getFirefoxBinary() {
		return new FirefoxBinary();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.firefox();
	}

	/**
	 * Get the Firefox Profile to use. Exists to allow subclasses to override.
	 *
	 * @return the default Firefox profile (null).
	 */
	public FirefoxProfile getFirefoxProfile() {
		return null;
	}

}
