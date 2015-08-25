package com.github.openborders.container;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.ComponentWithContext;
import com.github.openborders.Environment;
import com.github.openborders.ErrorCodeEscape;
import com.github.openborders.Request;
import com.github.openborders.UIContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.WAudio;
import com.github.openborders.WComponent;
import com.github.openborders.WContent;
import com.github.openborders.WImage;
import com.github.openborders.WVideo;
import com.github.openborders.WebUtilities;
import com.github.openborders.util.I18nUtilities;
import com.github.openborders.util.InternalMessages;
import com.github.openborders.util.StepCountUtil;
import com.github.openborders.util.SystemException;
import com.github.openborders.util.Util;

/**
 * <p>
 * This wrong step interceptor makes sure that content requests are only processed from the most recently rendered view.
 * </p>
 * <p>
 * It assumes the correct context has already been set via the {@link WWindowInterceptor}.
 * </p>
 * <p>
 * If a step error occurs, then the user, depending on the redirect flag, is either (1) redirected to an error page or
 * (2) warped to the future so the application is rendered in its current state. When the user is warped to the future,
 * the handleStepError method is called on WApplication, which allows applications to take the appropriate action for
 * when a step error has occurred. For content like WImage, an error code is set, rather than trying to do a redirect.
 * </p>
 * 
 * @author Jonathan Austin
 */
public class WrongStepContentInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(WrongStepContentInterceptor.class);

    /**
     * Override to check whether the step variable in the incoming request matches what we expect.
     * 
     * @param request the request being serviced.
     */
    @Override
    public void serviceRequest(final Request request)
    {
        // Get expected step count
        UIContext uic = UIContextHolder.getCurrent();
        int expected = uic.getEnvironment().getStep();

        // Step should already be set on the session
        if (expected == 0)
        {
            throw new SystemException("Step count should already be set on the session before content request.");
        }

        // Get step count on the request
        int got = StepCountUtil.getRequestStep(request);

        // Check tokens match (both must be provided)
        if (expected == got)
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }

        // Check cached content (no step on request)
        else if (!StepCountUtil.isStepOnRequest(request) && checkCachedContent(request))
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }

        // Invalid token
        else
        {
            // Set an error code
            log.warn("Wrong step detected for content request. Expected step [" + expected + "] but got step [" + got
                     + "].");
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
     * @param code the error code to set
     */
    private void handleError()
    {
        String msg = I18nUtilities
            .format(UIContextHolder.getCurrent().getLocale(), InternalMessages.DEFAULT_STEP_ERROR);
        throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
    }
}
