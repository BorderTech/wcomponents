package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.lde.PlainLauncher;
import com.github.bordertech.wcomponents.util.Config;
import org.junit.After;
import org.junit.Before;

/**
 * Base class to run a lde/jetty server for selenium based tests.
 */
public class SeleniumJettyTestCase extends WComponentSeleniumTestCase {

	/**
	 * The Jetty server to run.
	 */
	private PlainLauncher lde;

	/**
	 * Constructor...
	 *
	 * @param wComponent the base wcomponent to use.
	 */
	public SeleniumJettyTestCase(final WComponent wComponent) {
		Config.getInstance().setProperty("bordertech.wcomponents.lde.component.to.launch",
			wComponent.getClass().getCanonicalName());
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

}
