package ${package}.lde;

import com.github.bordertech.wcomponents.lde.PlainLauncher;

/**
 * Provides this project with a main class that can facilitate running from an IDE. This allows people to run the
 * generated archetype "out of the box" with zero configuration.
 */
public class PlainLauncherProxy {
	/**
	 * The entry point to the launcher.
	 *
	 * @param args command-line arguments, ignored.
	 * @throws Exception on error
	 */
	public static void main(final String[] args) throws Exception {
		PlainLauncher.main(args);
	}
}
