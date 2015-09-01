package com.github.dibp.wcomponents.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * Allows the velocity engine to use the commons logging used by WComponents.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class VelocityLogger implements LogSystem
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(VelocityLogger.class);

    /**
     * @param rsvc the velocity runtime services
     * @throws Exception if problem configuring logger
     */
    public void init(final RuntimeServices rsvc) throws Exception
    {
        // Do no extra configuration
    }

    /**
     * Log velocity engine messages.
     * 
     * @param level severity level
     * @param message complete error message
     */
    public void logVelocityMessage(final int level, final String message)
    {
        switch (level)
        {
            case LogSystem.WARN_ID:
                log.warn(message);
                break;
            case LogSystem.INFO_ID:
                log.info(message);
                break;
            case LogSystem.DEBUG_ID:
                log.debug(message);
                break;
            case LogSystem.ERROR_ID:
                log.error(message);
                break;
            default:
                log.debug(message);
                break;
        }
    }

}
