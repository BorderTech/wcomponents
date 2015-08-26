package com.github.openborders.wcomponents.container;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.ComponentWithContext;
import com.github.openborders.wcomponents.Environment;
import com.github.openborders.wcomponents.ErrorCodeEscape;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.UIContext;
import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.WAudio;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WContent;
import com.github.openborders.wcomponents.WImage;
import com.github.openborders.wcomponents.WVideo;
import com.github.openborders.wcomponents.WebUtilities;
import com.github.openborders.wcomponents.util.I18nUtilities;
import com.github.openborders.wcomponents.util.InternalMessages;
import com.github.openborders.wcomponents.util.SystemException;
import com.github.openborders.wcomponents.util.Util;

/**
 * This session token interceptor makes sure the content request being processed is for the correct session.
 * <p>
 * Similar to {@link SessionTokenInterceptor} but caters for setting error codes for content requests such as
 * {@link WImage} when a token error is detected.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenContentInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(SessionTokenContentInterceptor.class);

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
            throw new SystemException("Session token should already be set on the session before content request.");
        }
        
        // Get the session token from the request
        String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

        // Check tokens match (both must be provided)
        if (Util.equals(expected, got))
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }

        // Check cached content (no session token on request)
        else if (got == null && checkCachedContent(request))
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }

        // Invalid token
        else
        {
            // Set an error code
            log.warn("Wrong session token detected for content request. Expected token [" + expected
                     + "] but got token [" + got + "].");
            handleError();
        }

    }

    /**
     * Check for cached content.
     * 
     * @param target the target
     * @return true if content is cached, otherwise false
     */
    private boolean checkCachedContent(final Request request)
    {
        // Get target id on request
        String targetId = request.getParameter(Environment.TARGET_ID);
        if (targetId == null)
        {
            return false;
        }

        // Get target
        ComponentWithContext targetWithContext = WebUtilities.getComponentById(targetId, true);
        if (targetWithContext == null)
        {
            return false;
        }

        // Check for caching key
        WComponent target = targetWithContext.getComponent();

        // TODO Look at implementing CacheableTarget interface
        String key = null;
        if (target instanceof WContent)
        {
            key = ((WContent) target).getCacheKey();
        }
        else if (target instanceof WImage)
        {
            key = ((WImage) target).getCacheKey();
        }
        else if (target instanceof WVideo)
        {
            key = ((WVideo) target).getCacheKey();
        }
        else if (target instanceof WAudio)
        {
            key = ((WAudio) target).getCacheKey();
        }
        return !Util.empty(key);
    }
    
    /**
     * Throw the default error code.
     */
    private void handleError()
    {
        String msg = I18nUtilities.format(UIContextHolder.getCurrent().getLocale(),
                                          InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
        throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
    }
    

}
