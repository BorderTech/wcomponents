package com.github.dibp.wcomponents.exampleslde;

import com.github.dibp.wcomponents.lde.StandaloneLauncher;

/**
 * Acts as a proxy to the StandaloneLauncherProxy, intended use is the executable jar built by this module.
 * @author Rick Brown
 */
public class StandaloneLauncherProxy
{
	public static void main(final String args[]) throws Exception
	{
		StandaloneLauncher.main(args);
	}
}
