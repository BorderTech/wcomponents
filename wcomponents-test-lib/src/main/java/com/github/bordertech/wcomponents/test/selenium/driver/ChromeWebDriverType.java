package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * <p>
 * WebDriverType implementation for Chrome.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class ChromeWebDriverType implements WebDriverType<ChromeDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public ChromeDriver getDriverImplementation() {
		return new ChromeDriver(getChromeDriverService(), getChromeOptions());
	}

	/**
	 * Get the ChromeDriverService to use. Exists to allow subclasses to
	 * override.
	 *
	 * @return the default chrome service.
	 */
	public ChromeDriverService getChromeDriverService() {
		return ChromeDriverService.createDefaultService();
	}

	/**
	 * Get the ChromeOptions to use. Exists to allow subclasses to override.
	 *
	 * @return the default chrome options.
	 */
	public ChromeOptions getChromeOptions() {
		return new ChromeOptions();
	}

}
