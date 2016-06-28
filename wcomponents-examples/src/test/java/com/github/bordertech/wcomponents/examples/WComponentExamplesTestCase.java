package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.test.selenium.DynamicLauncher;
import com.github.bordertech.wcomponents.lde.TestLauncher;
import com.github.bordertech.wcomponents.test.selenium.ByWComponent;
import com.github.bordertech.wcomponents.test.selenium.ByWComponentPath;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.test.selenium.server.ServerCache;

/**
 * Abstract Test class for WComponents examples tests. Demonstrates convenience methods that applications may want to
 * implement in their abstract test.
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
		this.ui = ui;

		TestLauncher launcher = ServerCache.getLauncher();
		if (launcher instanceof DynamicLauncher) {
			((DynamicLauncher) launcher).setComponentToLaunch(ui);
		}
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
