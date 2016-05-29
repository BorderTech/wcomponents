package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;

/**
 *
 * <p>
 * WebDriverType implementation for Edge.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class EdgeWebDriverType implements WebDriverType<EdgeDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public EdgeDriver getDriverImplementation() {
		return new EdgeDriver(getEdgeDriverService(), getOptions());
	}

	/**
	 * Get the EdgeDriverService to use. Exists to allow subclasses
	 * to override.
	 *
	 * @return the default Edge service.
	 */
	public EdgeDriverService getEdgeDriverService() {
		return EdgeDriverService.createDefaultService();
	}

	/**
	 * Get the Options to use. Exists to allow subclasses to override.
	 *
	 * @return the default Edge options.
	 */
	public EdgeOptions getOptions() {
		return new EdgeOptions();

	}

}
