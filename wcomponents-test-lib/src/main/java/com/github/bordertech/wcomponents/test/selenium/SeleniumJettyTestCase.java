package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.lde.PlainLauncher;
import com.github.bordertech.wcomponents.util.Config;
import org.junit.After;
import org.junit.Before;

/**
 * Base class to run a lde/jetty server for selenium based tests.
 * @param <T> the wcomponent extension
 */
public class SeleniumJettyTestCase<T extends WComponent> extends WComponentSeleniumTestCase {

	/**
	 * The Jetty server to run.
	 */
	private PlainLauncher lde;
	private T wComponent;

	/**
	 * Constructor...
	 *
	 * @param wComponent the base wcomponent to use.
	 */
	public SeleniumJettyTestCase(final T wComponent) {
		this.wComponent = wComponent;
		Config.getInstance().setProperty("bordertech.wcomponents.lde.component.to.launch",
			wComponent.getClass().getCanonicalName());
		this.wComponent = wComponent;
	}

	@Before
	public void startup() throws Exception {
		lde = new PlainLauncher();
		lde.run();
	}


	@After
	public void tearDown() throws InterruptedException {
		lde.stop();
	}

	/**
	 * The wcomponent for test running.
	 *
	 * @return the main wcomponent
	 */
	public T getwComponent() {
		return wComponent;
	}
}
