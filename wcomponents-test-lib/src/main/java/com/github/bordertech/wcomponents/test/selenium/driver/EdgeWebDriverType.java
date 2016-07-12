package com.github.bordertech.wcomponents.test.selenium.driver;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

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
public class EdgeWebDriverType extends WebDriverType<EdgeDriver> {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return "edge";
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public EdgeDriver getDriverImplementation() {
		return new EdgeDriver(getEdgeDriverService(), getCapabilities());
	}

	/**
	 * Get the EdgeDriverService to use. Exists to allow subclasses to override.
	 *
	 * @return the default Edge service.
	 */
	public EdgeDriverService getEdgeDriverService() {
		return EdgeDriverService.createDefaultService();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		return DesiredCapabilities.edge();
	}

}
