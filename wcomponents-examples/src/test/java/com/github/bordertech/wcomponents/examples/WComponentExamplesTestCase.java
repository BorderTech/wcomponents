package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponentPath;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.WebDriverType;
import com.github.bordertech.wcomponents.test.selenium.server.ServerCache;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;

/**
 * <p>
 * Abstract Test class for WComponents examples tests. Provides convenience methods for testing when the server is in
 * the same JVM as the Selenium test.</p>
 * <p>
 * This class demonstrates the 'quick and nasty' way of writing Selenium tests. These tests rely on the server running
 * in the same JVM and therefore cannot be used against other environments.
 * </p>
 *
 * @author Joshua Barclay
 * @author Jonathan Austin
 * @since 1.2.0
 */
public abstract class WComponentExamplesTestCase extends WComponentSeleniumTestCase {

	/**
	 * Flag if parallel methods are being used.
	 */
	private static boolean USE_PARALLEL_METHODS = ConfigurationProperties.getTestSeleniumParallelMethods();

	/**
	 * The Running instance of UI.
	 */
	private final WComponent ui;

	/**
	 * Constructor to set the UI component.
	 * <p>
	 * The example will be wrapped in a {@link WApplication}.
	 * </p>
	 *
	 * @param testUI the UI being tested.
	 */
	public WComponentExamplesTestCase(final WComponent testUI) {
		this.ui = ServerCache.setUI(this.getClass().getName(), testUI);
		super.setUrl(ServerCache.getUrl());
	}

	/**
	 * Find by WComponent path using the test's UI.
	 *
	 * @param path the path to find.
	 * @return the Selenium By implementation.
	 */
	public ByWComponentPath byWComponentPath(final String path) {
		return new ByWComponentPath(ui, path);
	}

	/**
	 * Find by WComponent path using the test's UI.
	 *
	 * @param path the path to find.
	 * @param visibleOnly visible components only
	 * @return the Selenium By implementation.
	 */
	public ByWComponentPath byWComponentPath(final String path, final boolean visibleOnly) {
		return new ByWComponentPath(ui, path, visibleOnly);
	}

	/**
	 * Find by WComponent path and value using the test's UI.
	 *
	 * @param path the path to find.
	 * @param value the value of the field to match.
	 * @return the Selenium By implementation.
	 */
	public ByWComponentPath byWComponentPath(final String path, final Object value) {
		return new ByWComponentPath(ui, null, path, value);
	}

	/**
	 * Find by WComponent path and value using the test's UI.
	 *
	 * @param path the path to find.
	 * @param value the value of the field to match.
	 * @param visibleOnly visible components only
	 * @return the Selenium By implementation.
	 */
	public ByWComponentPath byWComponentPath(final String path, final Object value, final boolean visibleOnly) {
		return new ByWComponentPath(ui, null, path, value, visibleOnly);
	}

	/**
	 * Find the client side element for the given component.
	 *
	 * @param component the component to find.
	 * @return the Selenium By implementation.
	 */
	public ByWComponent byWComponent(final WComponent component) {
		return new ByWComponent(component);
	}

	/**
	 * Find the client side element for the given component with the given value.
	 *
	 * @param component the component to find.
	 * @param value the value of the component.
	 * @return the Selenium By implementation.
	 */
	public ByWComponent byWComponent(final WComponent component, final Object value) {
		return new ByWComponent(component, null, value);
	}

	/**
	 * @return the ui for this test.
	 */
	public WComponent getUi() {
		return ui;
	}

	/**
	 * Setup the driver and device id (ie session) before each test method.
	 */
	@Before
	public void setupDriver() {
		// For parallel methods, each method has a different session, set the unique driver id (ie session id)
		if (USE_PARALLEL_METHODS) {
			WebDriverType type = getDriverType();
			String driverId = UUID.randomUUID().toString();
			setDriver(type, driverId);
		}
	}

	/**
	 * Clear the session and release the driver.
	 */
	@After
	public void tearDownDriver() {
		// For parallel methods, release the session after each method
		if (USE_PARALLEL_METHODS) {
			releaseDriver();
		}
	}

}
