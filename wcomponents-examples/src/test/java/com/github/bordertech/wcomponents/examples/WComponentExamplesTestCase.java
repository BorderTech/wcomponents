package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponentPath;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.WebDriverType;
import com.github.bordertech.wcomponents.test.selenium.server.ServerCache;
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
	 * The UI being tested.
	 */
	private final WComponent ui;

	/**
	 * Constructor to set the UI component.
	 * <p>
	 * The example will be wrapped in a {@link WApplication}.
	 * </p>
	 *
	 * @param ui the UI being tested.
	 */
	public WComponentExamplesTestCase(final WComponent ui) {
		// Wrap the example in an WApplication
		WApplication egUI;
		boolean wrapped = false;
		if (ui instanceof WApplication) {
			egUI = (WApplication) ui;
		} else {
			egUI = new WApplication();
			egUI.add(ui);
			wrapped = true;
		}
		// Register the Example UI (if already registered, the original instance will be returned)
		egUI = ServerCache.setUI(this.getClass().getName(), egUI);
		// Hold onto the Example instance
		this.ui = wrapped ? egUI.getChildAt(0) : egUI;
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
		WebDriverType type = getDriverType();
		String driverId = UUID.randomUUID().toString();
		setDriver(type, driverId);
	}

	/**
	 * Clear the session and release the driver.
	 */
	@After
	public void tearDownDriver() {
		releaseDriver();
	}

}
