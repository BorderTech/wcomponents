package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for PhantomJS.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class PhantomJSWebDriverType implements WebDriverType<PhantomJSDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public PhantomJSDriver getDriverImplementation() {
		return new PhantomJSDriver(getPhantomJSDriverService(), getCapabilities());
	}

	/**
	 * Get the PhantomJSDriverService to use. Exists to allow subclasses to
	 * override.
	 *
	 * @return the default phantomJS service.
	 */
	public PhantomJSDriverService getPhantomJSDriverService() {
		return PhantomJSDriverService.createDefaultService();
	}

	/**
	 * Get the Capabilities to use. Exists to allow subclasses to override.
	 *
	 * @return the default phantomJS capabilities.
	 */
	public DesiredCapabilities getCapabilities() {
		return DesiredCapabilities.phantomjs();

	}

}
