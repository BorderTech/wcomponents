package com.github.openborders.util;

/**
 * Utility class for tracking config.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
final public class TrackingUtil
{
    /** Hide the constructor as there are no instance methods. */
    private TrackingUtil()
    {
    }

    /**
     * @return true if tracking is enabled for the application.
     */
    public static boolean isTrackingEnabled()
    {
        return !Util.empty(getClientId()) && !Util.empty(getApplicationName());
    }

    /**
     * @return the client id.
     */
    public static String getClientId()
    {
        return Config.getInstance().getString("wcomponent.tracking.clientid");
    }

    /**
     * @return the cookie domain.
     */
    public static String getCookieDomain()
    {
        return Config.getInstance().getString("wcomponent.tracking.cookiedomain");
    }

    /**
     * @return the data collection domain.
     */
    public static String getDataCollectionDomain()
    {
        return Config.getInstance()
            .getString("wcomponent.tracking.datacollectiondomain");
    }

    /**
     * @return the application name used when tracking
     */
    public static String getApplicationName()
    {
        return Config.getInstance()
            .getString("wcomponent.tracking.applicationname");
    }

}
