package com.github.bordertech.wcomponents.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Utility class used by WComponents for accessing {@link Config configuration} parameters used in debug features.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class DebugUtil {

	/**
	 * The flag used to indicate if wComponents is running in debug mode.
	 */
	private static boolean debugFeaturesEnabled;
	/**
	 * The flag used to indicate if Validate XML is enabled.
	 */
	private static boolean validateXMLEnabled;
	/**
	 * The flag used to indicate if Debug Structure is enabled.
	 */
	private static boolean debugStructureEnabled;

	/**
	 * When this class is loaded by the application, register a property change listener.
	 */
	static {
		PropertyChangeListener paramChange = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				retrieveDebugParameters();
			}
		};

		// register the change listener with the configuration.
		Config.addPropertyChangeListener(paramChange);

		// set current values
		retrieveDebugParameters();
	}

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private DebugUtil() {
	}

	/**
	 * Retrieve the current parameter settings for the debug flags.
	 */
	private static void retrieveDebugParameters() {
		debugFeaturesEnabled = Config.getInstance().getBoolean(
				"bordertech.wcomponents.debug.enabled", false);
		validateXMLEnabled = Config.getInstance().getBoolean(
				"bordertech.wcomponents.debug.validateXML.enabled", false);
		debugStructureEnabled = Config.getInstance().getBoolean(
				"bordertech.wcomponents.debug.clientSide.enabled", false);
	}

	/**
	 * Determines if WComponents is in debug mode. The debug features will only be enabled if this is true.
	 *
	 * @return true or false
	 */
	public static boolean isDebugFeaturesEnabled() {
		return debugFeaturesEnabled;
	}

	/**
	 * Determines if Validate XML is enabled.
	 *
	 * @return true or false
	 */
	public static boolean isValidateXMLEnabled() {
		return (debugFeaturesEnabled && validateXMLEnabled);
	}

	/**
	 * Determines if Debug Structure is enabled.
	 *
	 * @return true or false
	 */
	public static boolean isDebugStructureEnabled() {
		return (debugFeaturesEnabled && debugStructureEnabled);
	}

}
