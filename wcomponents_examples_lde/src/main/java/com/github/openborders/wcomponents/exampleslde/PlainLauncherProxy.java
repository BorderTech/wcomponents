package com.github.openborders.wcomponents.exampleslde;

import com.github.openborders.wcomponents.examples.picker.ExamplePicker;
import com.github.openborders.wcomponents.lde.PlainLauncher;
import com.github.openborders.wcomponents.util.Config;
import org.apache.commons.configuration.Configuration;

/**
 * Provides this project with a main class that can facilitate running from an IDE.
 * Calls out to a default launcher and sets any default properties needed to run at a bare minimum.
 * This allows people to run the examples "out of the box" with zero configuration.
 *
 * @author Rick Brown
 */
public class PlainLauncherProxy
{
	/**
	 * Ensures that the "class to launch" has been configured and if not sets it to a sensible default.
	 */
	static void configureClassToLaunch() {
		Configuration config = Config.getInstance();
		String configuredClassToLaunch = config.getString(PlainLauncher.COMPONENT_TO_LAUNCH_PARAM_KEY);
		if (configuredClassToLaunch == null || configuredClassToLaunch.isEmpty())
		{
			// Don't hardcode the fully qualified name of the ExamplePicker because then you lose compiletime safety.
			config.setProperty(PlainLauncher.COMPONENT_TO_LAUNCH_PARAM_KEY, ExamplePicker.class.getName());
		}
	}

	public static void main(final String[] args) throws Exception
	{
		configureClassToLaunch();
		PlainLauncher.main(args);
	}
}
