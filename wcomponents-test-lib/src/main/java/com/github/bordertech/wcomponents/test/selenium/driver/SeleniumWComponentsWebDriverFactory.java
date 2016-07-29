package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.test.selenium.SeleniumWComponentsUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

/**
 * Convenience class to provide factory methods for getting new instances of
 * WComponentsWebDriver.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class SeleniumWComponentsWebDriverFactory {

	/**
	 * Private constructor - utility class.
	 */
	private SeleniumWComponentsWebDriverFactory() {
		//No-impl
	}

	/**
	 * Create a SeleniumWComponentsWebDriver backed by the given WebDriver.
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriver the WebDriver implementation.
	 * @return a SeleniumWComponentsWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> SeleniumWComponentsWebDriver<T> createDriver(final T backingDriver) {
		SeleniumWComponentsUtil.configureDriver(backingDriver);
		return new SeleniumWComponentsWebDriver<>(backingDriver);
	}

	/**
	 * Create a SeleniumWComponentsWebDriver backed by the driver implementation of the
	 * given WebDriverType.
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriverType the WebDriverType that references the backing
	 * WebDriver to wrap.
	 * @return a SeleniumWComponentsWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> SeleniumWComponentsWebDriver<T> createDriver(final WebDriverType<T> backingDriverType) {
		return createDriver(backingDriverType.getDriverImplementation());
	}

	/**
	 * <p>
	 * Create a SeleniumWComponentsWebDriver backed by the an instance of the given
	 * backingDriverClass class name.</p>
	 * <p>
	 * <b>WARNING: </b> As reflection is used to instantiate the WebDriver, the
	 * type T may be unreliable if not used correctly.</p>
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriverClass the full class name of the WebDriver
	 * implementation.
	 * @return a SeleniumWComponentsWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> SeleniumWComponentsWebDriver<T> createDriver(final String backingDriverClass) {

		T backingDriver = (T) createBackingDriver(backingDriverClass);

		return (SeleniumWComponentsWebDriver<T>) createDriver(backingDriver);
	}

	/**
	 * Create a WebDriver implementation from a String class name.
	 *
	 * @param backingDriverClass the WebDriver implementation class.
	 * @return the WebDriver implementation.
	 */
	public static WebDriver createBackingDriver(final String backingDriverClass) {
		if (StringUtils.isBlank(backingDriverClass)) {
			throw new IllegalArgumentException("backingDriverClass must not be blank");
		}

		try {
			Class clazz = Class.forName(backingDriverClass);
			if (!WebDriver.class.isAssignableFrom(clazz)) {
				throw new SystemException("backingDriverClass does not implement WebDriver inteface. backingDriverClass=["
						+ backingDriverClass + "]");
			}
			Class<WebDriver> driverClass = (Class<WebDriver>) clazz;
			WebDriver backingDriver = (WebDriver) driverClass.newInstance();

			return backingDriver;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new SystemException("Unable to create backingDriverClass by classname String. backingDriverClass=[" + backingDriverClass + "]", ex);
		}
	}
}
