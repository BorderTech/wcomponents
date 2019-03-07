package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for Internet Explorer.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class InternetExplorerWebDriverType extends WebDriverType<InternetExplorerDriver> {

	@Override
	public String getDriverTypeName() {
		return "ie";
	}

	@Override
	public InternetExplorerDriver getDriverImplementation() {
		return new InternetExplorerDriver(getInternetExplorerDriverService(), getCapabilities());
	}

	/**
	 * Get the InternetExplorerDriverService to use. Exists to allow subclasses
	 * to override.
	 *
	 * @return the default Internet Explorer service.
	 */
	public InternetExplorerDriverService getInternetExplorerDriverService() {
		return InternetExplorerDriverService.createDefaultService();
	}

	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.internetExplorer();
	}

}
