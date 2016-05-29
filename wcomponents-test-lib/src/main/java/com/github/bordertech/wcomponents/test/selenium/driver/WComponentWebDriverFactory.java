package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.util.SystemException;
import org.openqa.selenium.WebDriver;

/**
 * Convenience class to provide factory methods for getting new instances of
 * WComponentsWebDriver.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class WComponentWebDriverFactory {

	/**
	 * Private constructor - utility class.
	 */
	private WComponentWebDriverFactory() {
		//No-impl
	}

	/**
	 * Create a WComponentWebDriver backed by the given WebDriver.
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriver the WebDriver implementation.
	 * @return a WComponentWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> WComponentWebDriver<T> createDriver(final T backingDriver) {
		return new WComponentWebDriver<>(backingDriver);
	}

	/**
	 * Create a WComponentWebDriver backed by the driver implementation of the
	 * given WebDriverType.
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriverType the WebDriverType that references the backing
	 * WebDriver to wrap.
	 * @return a WComponentWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> WComponentWebDriver<T> createDriver(final WebDriverType<T> backingDriverType) {
		return createDriver(backingDriverType.getDriverImplementation());
	}

	/**
	 * <p>
	 * Create a WComponentWebDriver backed by the an instance of the given
	 * backingDriverClass class name.</p>
	 * <p>
	 * <b>WARNING: </b> As reflection is used to instantiate the WebDriver, the
	 * type T may be unreliable if not used correctly.</p>
	 *
	 * @param <T> the type of the Selenium WebDriver.
	 * @param backingDriverClass the full class name of the WebDriver
	 * implementation.
	 * @return a WComponentWebDriver wrapping the given backing driver.
	 */
	public static <T extends WebDriver> WComponentWebDriver<T> createDriver(final String backingDriverClass) {
		try {
			Class clazz = Class.forName(backingDriverClass);
			if (!WebDriver.class.isAssignableFrom(clazz)) {
				throw new SystemException("backingDriverClass does not implement WebDriver inteface. backingDriverClass=["
						+ backingDriverClass + "]");
			}
			Class<T> driverClass = (Class<T>) clazz;
			T backingDriver = (T) driverClass.newInstance();

			return (WComponentWebDriver<T>) createDriver(backingDriver);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new SystemException("Unable to create backingDriverClass by classname String. backingDriverClass=[" + backingDriverClass + "]", ex);
		}
	}
}
