package com.github.bordertech.wcomponents.lde;

/**
 * Interface for classes that can launch an LDE, e.g. Jetty.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public interface LdeLauncher {

	/**
	 * Runs the server.
	 *
	 * @throws Exception if the LDE fails to start.
	 */
	void run() throws Exception;

	/**
	 * Stops the server.
	 *
	 * @throws java.lang.InterruptedException an interrupted exception
	 */
	void stop() throws InterruptedException;

	/**
	 * @return the URL of the server.
	 */
	String getUrl();

	/**
	 * @return true if the server is running, else false.
	 */
	boolean isRunning();

}
