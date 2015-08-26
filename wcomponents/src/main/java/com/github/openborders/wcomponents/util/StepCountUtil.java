package com.github.openborders.wcomponents.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.Environment;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.UIContext;

/**
 * Static utility methods related to working with the step count.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class StepCountUtil
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(TableUtil.class);

    /**
     * The parameter key for the URL users are redirected to when a step error occurs.
     */
    public static final String STEP_ERROR_URL_PARAMETER_KEY = "wcomponent.wrongStep.redirect.url";

    /** Prevent instantiation of utility class. */
    private StepCountUtil()
    {
    }

    /**
     * @return the url users are redirected to when a step error occurs
     */
    public static String getErrorUrl()
    {
        String url = Config.getInstance().getString(STEP_ERROR_URL_PARAMETER_KEY);
        return url;
    }

    /**
     * @return true if users are to be redirected when a step error occurs
     */
    public static boolean isErrorRedirect()
    {
        return !Util.empty(getErrorUrl());
    }

    /**
     * Increments the step that is recorded in session.
     * 
     * @param uic the current user's session
     */
    public static void incrementSessionStep(final UIContext uic)
    {
        int step = uic.getEnvironment().getStep();
        uic.getEnvironment().setStep(step + 1);
    }

    /**
     * Retrieves the value of the step variable from the given request.
     * 
     * @param request the request being responded to.
     * @return the request step present in the request, or -1 on error.
     */
    public static int getRequestStep(final Request request)
    {
        String val = request.getParameter(Environment.STEP_VARIABLE);

        if (val == null)
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException ex)
        {
            return -1;
        }
    }

    /**
     * Checks if the step count is on the request.
     * 
     * @param request the request being responded to.
     * @return true if the step count is on the request
     */
    public static boolean isStepOnRequest(final Request request)
    {
        String val = request.getParameter(Environment.STEP_VARIABLE);
        return val != null;
    }

}
