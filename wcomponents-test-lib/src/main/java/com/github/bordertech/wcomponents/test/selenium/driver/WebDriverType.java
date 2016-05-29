package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.WebDriver;

/**
 * Interface representing a possible WebDriver implementation to use for
 * WComponents Selenium testing.
 *
 * @author Joshua Barclay
 * @param <T> - the type of WebDriver returned.
 * @since 1.2.0
 */
public interface WebDriverType<T extends WebDriver> {

	/**
	 * <p>
	 * Return a new instance of the WebDriver for this type.</p>
	 * <p>
	 * Must return a new instance each time - must not cache.</p>
	 *
	 * @return a new instance of the WebDriver.
	 */
	T getDriverImplementation();

}
