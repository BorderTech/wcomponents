package com.github.openborders.wcomponents.container;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.Environment;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.UIContext;
import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.util.I18nUtilities;
import com.github.openborders.wcomponents.util.InternalMessages;
import com.github.openborders.wcomponents.util.SystemException;
import com.github.openborders.wcomponents.util.Util;

/**
 * This session token interceptor makes sure the request being processed is for the correct session.
 * <p>
 * As the token is a UUID, it will be much harder for CSRF attacks. No request processing will occur without the correct
 * UUID.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(SessionTokenInterceptor.class);

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

        // Get the session token from the request
        String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

        // Check tokens match (Both null if new session)
        // or processing a GET and no token
        if (Util.equals(expected, got) || (got == null && "GET".equals(request.getMethod())))
        {
            // Process request
            getBackingComponent().serviceRequest(request);
        }
        // Invalid token
        else
        {
            log.error("Wrong session token detected for servlet request. Expected token [" + expected
                      + "] but got token [" + got + "].");
            String message = I18nUtilities.format(uic.getLocale(), InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
            throw new SystemException(message);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void preparePaint(final Request request)
    {
        // Set session token
        UIContext uic = UIContextHolder.getCurrent();
        if (uic.getEnvironment().getSessionToken() == null)
        {
            uic.getEnvironment().setSessionToken(UUID.randomUUID().toString());
        }
        super.preparePaint(request);
    }

}
