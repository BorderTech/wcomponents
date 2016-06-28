package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
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
public class PhantomJSWebDriverType extends WebDriverType<PhantomJSDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "phantomjs";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public PhantomJSDriver getDriverImplementation() {
		return new PhantomJSDriver(getCapabilities());
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.phantomjs();
	}

}
