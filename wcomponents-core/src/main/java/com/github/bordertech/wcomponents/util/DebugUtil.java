package com.github.bordertech.wcomponents.util;

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
	 * When this class is loaded by the application, register a property change listener.
	 */
	static {
		// register the change listener with the configuration.
		Config.addPropertyChangeListener(evt -> retrieveDebugParameters());
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
		debugFeaturesEnabled = ConfigurationProperties.getDeveloperDebugEnabled();
		validateXMLEnabled = ConfigurationProperties.getDeveloperValidateXml();
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
	 * @deprecated 1.4.0 use {@link #isDebugFeaturesEnabled()}
	 */
	@Deprecated
	public static boolean isDebugStructureEnabled() {
		return isDebugFeaturesEnabled();
	}

}
