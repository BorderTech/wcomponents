package com.github.bordertech.wcomponents.util;

/**
 * Utility class for tracking config.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class TrackingUtil {

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private TrackingUtil() {
	}

	/**
	 * @return true if tracking is enabled for the application.
	 */
	public static boolean isTrackingEnabled() {
		return !Util.empty(getClientId()) && !Util.empty(getApplicationName());
	}

	/**
	 * @return the client id.
	 */
	public static String getClientId() {
		return ConfigurationProperties.getTrackingClientId();
	}

	/**
	 * @return the cookie domain.
	 */
	public static String getCookieDomain() {
		return ConfigurationProperties.getTrackingCookieDomain();
	}

	/**
	 * @return the data collection domain.
	 */
	public static String getDataCollectionDomain() {
		return ConfigurationProperties.getTrackingDataCollectionDomain();
	}

	/**
	 * @return the application name used when tracking
	 */
	public static String getApplicationName() {
		return ConfigurationProperties.getTrackingApplicationName();
	}

}
