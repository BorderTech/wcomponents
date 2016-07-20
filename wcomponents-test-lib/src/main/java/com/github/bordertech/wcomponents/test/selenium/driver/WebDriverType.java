package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.util.Map;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Superclass representing a possible WebDriver implementation to use for WComponents Selenium testing.
 *
 * @author Joshua Barclay
 * @param <T> - the type of WebDriver returned.
 * @since 1.2.0
 */
public abstract class WebDriverType<T extends WebDriver> {

	/**
	 * <p>
	 * Return a new instance of the WebDriver for this type.</p>
	 * <p>
	 * Must return a new instance each time - must not cache.</p>
	 *
	 * @return a new instance of the WebDriver.
	 */
	public abstract T getDriverImplementation();

	/**
	 * @return a unique name String for this driver type.
	 */
	public abstract String getDriverTypeName();

	/**
	 * @return the default DesiredCapabilities for this driver type.
	 */
	protected abstract DesiredCapabilities getDefaultDriverCapabilities();

	/**
	 * @return the DesiredCapabilities configured from parameters.
	 */
	public DesiredCapabilities getCapabilities() {

		DesiredCapabilities capabilities = getDefaultDriverCapabilities();

		Properties props = ConfigurationProperties.getTestSeleniumDriverCapabilities(getDriverTypeName());

		for (Map.Entry<Object, Object> property : props.entrySet()) {
			capabilities.setCapability((String) property.getKey(), property.getValue());
		}

		return capabilities;
	}

}
