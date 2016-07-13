package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.test.selenium.DynamicLauncher;
import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponentPath;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.test.selenium.server.ServerCache;
import com.github.bordertech.wcomponents.lde.LdeLauncher;

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
 * @since 1.2.0
 */
public abstract class WComponentExamplesTestCase extends WComponentSeleniumTestCase {

	/**
	 * The UI being tested.
	 */
	private final WComponent ui;

	/**
	 * Constructor to set the UI component.
	 *
	 * @param ui the UI being tested.
	 */
	public WComponentExamplesTestCase(final WComponent ui) {

		// Retrieve the launcher from the server 
		LdeLauncher launcher = ServerCache.getLauncher();
		// If a DynamicLauncher is being used, set the UI to match this component.
		if (launcher instanceof DynamicLauncher) {
			this.ui = ((DynamicLauncher) launcher).setComponentToLaunch(this.getClass().getName(), ui);
		} else {
			this.ui = ui;
		}
		// Start the server (if not started).
		ServerCache.startServer();
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

}
