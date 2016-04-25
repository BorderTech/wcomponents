package com.github.bordertech.wcomponents.examples.lde;

import com.github.bordertech.wcomponents.lde.StandaloneLauncher;

/**
 * Acts as a proxy to the StandaloneLauncherProxy, intended use is the executable jar built by this module.
 *
 * @author Rick Brown
 */
public class StandaloneLauncherProxy {

	/**
	 * The entry point to the launcher.
	 *
	 * @param args command-line arguments, ignored.
	 * @throws Exception on error
	 */
	public static void main(final String args[]) throws Exception {
		StandaloneLauncher.main(args);
	}
}
