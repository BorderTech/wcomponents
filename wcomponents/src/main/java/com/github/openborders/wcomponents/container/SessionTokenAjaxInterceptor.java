package com.github.openborders.wcomponents.container;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.Environment;
import com.github.openborders.wcomponents.ErrorCodeEscape;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.UIContext;
import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.util.I18nUtilities;
import com.github.openborders.wcomponents.util.InternalMessages;
import com.github.openborders.wcomponents.util.SystemException;
import com.github.openborders.wcomponents.util.Util;

/**
 * This session token interceptor makes sure the ajax request being processed is for the correct session.
 * <p>
 * Similar to {@link SessionTokenInterceptor} but sets an error code when a token error is detected.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenAjaxInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(SessionTokenAjaxInterceptor.class);

    /**
     * Override to check whether the session token variable in the incoming request matches what we expect.
     * 
     * @param request the request being serviced.
     */
    @Override
    public void serviceRequest(final Request request)
    {
        // Get the expected session token
        UIContext uic = UIContextHolder.getCurrent();
        String expected = uic.getEnvironment().getSessionToken();

        // Session token should already be set
        if (expected == null)
        {
            throw new SystemException("Session token should already be set on the session before AJAX request");
        }

        // Get the session token from the request
        String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

        // Check tokens match (both must be provided)
        if (Util.equals(expected, got))
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }

        // Invalid token
        else
        {
            log.error("Wrong session token detected for AJAX request. Expected token [" + expected
                      + "] but got token [" + got + "].");
            handleError();
        }
    }

    /**
     * @param code the error code to set
     */
    private void handleError()
    {
        String msg = I18nUtilities.format(UIContextHolder.getCurrent().getLocale(),
                                          InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
        throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
    }

}
