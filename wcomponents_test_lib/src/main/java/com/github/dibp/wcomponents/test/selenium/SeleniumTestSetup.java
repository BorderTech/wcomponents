package com.github.dibp.wcomponents.test.selenium; 

import com.github.dibp.wcomponents.util.SystemException;

/**
 * An extension of TestSetup which will start/stop the {@link SeleniumTestServlet} server. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public final class SeleniumTestSetup 
{
    /**
     * In order to support tests running directly as well as suites, keep count
     * of the setup/teardown invocations, so that we don't keep restarting the LDE
     * unnecessarily.
     */
    private static int startCount = 0;
    
    /** Hide utility class constructor. */
    private SeleniumTestSetup()
    {
    }
    
    /**
     * Starts the LDE (if not already started).
     */
    public synchronized static void startLde()
    {
        if (startCount++ == 0)
        {
            try
            {
                SeleniumTestServlet.startServlet();
            }
            catch (Exception e)
            {
                throw new SystemException("Failed to start selenium test servlet", e);
            }
        }
    }
    
    /**
     * Stops the LDE (when the invocation counter reaches zero).
     */
    public synchronized static void stopLde()
    {
        if (--startCount == 0)
        {
            try
            {
                SeleniumTestServlet.stopServlet();
            }
            catch (Exception e)
            {
                throw new SystemException("Failed to start selenium test servlet", e);
            }
        }
    }
}
