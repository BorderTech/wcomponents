package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for Chrome.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class ChromeWebDriverType extends WebDriverType<ChromeDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "chrome";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public ChromeDriver getDriverImplementation() {
		return new ChromeDriver(getChromeDriverService(), getCapabilities());
	}

	/**
	 * Get the ChromeDriverService to use. Exists to allow subclasses to override.
	 *
	 * @return the default chrome service.
	 */
	public ChromeDriverService getChromeDriverService() {
		return ChromeDriverService.createDefaultService();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.chrome();
	}

//	@Override
//	public DesiredCapabilities getCapabilities() {
//		DesiredCapabilities cap = super.getCapabilities();
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
//		options.addArguments("--no-sandbox");
//		options.addArguments("--disable-gpu");
//		options.addArguments("--dom-automation");
//		options.addArguments("--disable-images");
//		cap.setCapability("chromeOptions", options);
//		return cap;
//	}
}
