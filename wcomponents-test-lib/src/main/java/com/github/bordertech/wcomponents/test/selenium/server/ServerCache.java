package com.github.bordertech.wcomponents.test.selenium.server;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.lde.LdeLauncher;
import com.github.bordertech.wcomponents.test.selenium.DynamicLauncher;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * <p>
 * Static utility testing class to keep a Web Server open between tests. This is to prevent the expensive server
 * creation/deploy occurring multiple times per test suite.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class ServerCache {

	/**
	 * The servlet launcher for testing.
	 */
	private static final LdeLauncher LAUNCHER = Factory.newInstance(LdeLauncher.class);

	private static boolean inSuite;

	/**
	 * Static class - no constructor.
	 */
	private ServerCache() {
	}

	/**
	 * <p>
	 * Get the shared instance of the launcher.</p>
	 * <p>
	 * <b>Warning: </b> ensure concurrency is considered with any commands run on the TestLauncher. Lock on the returned
	 * instance to ensure safe concurrent behaviour with other threads using this class.</p>
	 *
	 * @return the TestLauncher.
	 */
	public static LdeLauncher getLauncher() {
		return LAUNCHER;
	}

	/**
	 * @return the URL of the launcher.
	 */
	public static String getUrl() {
		synchronized (LAUNCHER) {
			return LAUNCHER.getUrl();
		}
	}

	/**
	 * Stop the server.
	 */
	public static void stopServer() {
		if (isInSuite()) {
			return;
		}
		synchronized (LAUNCHER) {
			try {
				LAUNCHER.stop();
			} catch (Exception ex) {
				throw new SystemException("Unable to stop server", ex);
			}
		}
	}

	/**
	 * Stop the server if it is running, then run it.
	 */
	public static void restartServer() {
		synchronized (LAUNCHER) {
			if (isRunning()) {
				stopServer();
			}

			startServer();
		}
	}

	/**
	 * @return true if the server is running.
	 */
	public static boolean isRunning() {
		synchronized (LAUNCHER) {

			return LAUNCHER.isRunning();
		}
	}

	/**
	 * Start the server.
	 */
	public static void startServer() {
		synchronized (LAUNCHER) {
			try {
				if (!isRunning()) {
					LAUNCHER.run();
				}
			} catch (Exception ex) {
				throw new SystemException("Unable to start server", ex);
			}
		}
	}

	/**
	 * Set true if running in a suite.
	 *
	 * @param flag true if running in a suite
	 */
	public static void setInSuite(final boolean flag) {
		synchronized (LAUNCHER) {
			inSuite = flag;
		}
	}

	/**
	 *
	 * @return true if running a test suite
	 */
	public static boolean isInSuite() {
		return inSuite;
	}

	/**
	 * Set the UI for the launcher.
	 *
	 * @param key the UI key
	 * @param ui the UI component
	 * @return the registered UI instance
	 */
	public static WComponent setUI(final String key, final WComponent ui) {

		if (!(LAUNCHER instanceof DynamicLauncher)) {
			return ui;
		}

		// If a DynamicLauncher is being used, set the UI to match this component.
		synchronized (LAUNCHER) {
			DynamicLauncher dynamic = (DynamicLauncher) LAUNCHER;
			// Set the current key
			dynamic.setCurrentKey(key);
			// Check if already registered
			WApplication egUI = dynamic.getRegisteredComponent(key);
			if (egUI != null) {
				// Already registered, check if wrapped
				return (ui instanceof WApplication) ? egUI : egUI.getChildAt(0);
			}
			// Register UI - Check if needs to be wrapped
			dynamic.registerComponent(key, wrapUI(ui));
			// This is the registered instance
			return ui;
		}
	}

	/**
	 *
	 * @param ui the UI component
	 * @return the UI component wrapped in WApplication
	 */
	private static WApplication wrapUI(final WComponent ui) {
		WApplication egUI;
		if (ui instanceof WApplication) {
			egUI = (WApplication) ui;
		} else {
			egUI = new WApplication();
			egUI.add(ui);
		}
		return egUI;
	}

}
