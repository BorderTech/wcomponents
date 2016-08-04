package com.github.bordertech.wcomponents.test.selenium.server;

import com.github.bordertech.wcomponents.lde.LdeLauncher;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * <p>
 * Static utility testing class to keep a Web Server open between tests. This is
 * to prevent the expensive server creation/deploy occurring multiple times per
 * test suite.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class ServerCache {

	/**
	 * The servlet launcher for testing.
	 */
	private static final LdeLauncher LAUNCHER = Factory.newInstance(LdeLauncher.class);

	/**
	 * Static class - no constructor.
	 */
	private ServerCache() {
	}

	/**
	 * <p>
	 * Get the shared instance of the launcher.</p>
	 * <p>
	 * <b>Warning: </b> ensure concurrency is considered with any commands run
	 * on the TestLauncher. Lock on the returned instance to ensure safe
	 * concurrent behaviour with other threads using this class.</p>
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
}
