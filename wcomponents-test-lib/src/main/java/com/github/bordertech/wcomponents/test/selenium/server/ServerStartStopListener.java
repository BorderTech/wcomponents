package com.github.bordertech.wcomponents.test.selenium.server;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

/**
 * Listener to start and stop the web server before the unit tests.
 *
 * @author Jonathan Austin
 */
public class ServerStartStopListener extends RunListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunStarted(final Description description) throws Exception {
		ServerCache.setInSuite(true);
		ServerCache.startServer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunFinished(final Result result) throws Exception {
		ServerCache.setInSuite(false);
		ServerCache.stopServer();
	}

}
