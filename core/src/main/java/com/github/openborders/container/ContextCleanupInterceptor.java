package com.github.openborders.container;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.RenderContext;
import com.github.openborders.Request;
import com.github.openborders.UIContext;
import com.github.openborders.UIContextDebugWrapper;
import com.github.openborders.UIContextHolder;

/**
 * This interceptor removes component model objects that are no longer needed from 
 * the UIContext. This is not essential other than to keep web server memory usage down.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class ContextCleanupInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(ContextCleanupInterceptor.class);

    /**
     * Override serviceRequest to clear out the targetable index from the last request.
     * And clear out the scratch map after the request has been processed 
     * (and before painting occurs).
     * 
     * @param request the request being responded to.
     */
    @Override
    public void serviceRequest(final Request request)
    {
        log.debug("Before Service Request - Clearing targetable index.");
        UIContext uic = UIContextHolder.getCurrent();
        
        super.serviceRequest(request);
        log.debug("After Service Request - Clearing scratch map.");
        uic.clearScratchMap();
    }

    /**
     * Override paint to clear out the scratch map and component models
     * which are no longer necessary.
     * 
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        super.paint(renderContext);
        UIContext uic = UIContextHolder.getCurrent();

        if (log.isDebugEnabled())
        {
            UIContextDebugWrapper debugWrapper = new UIContextDebugWrapper(uic);
            log.debug("Session usage after paint:\n" + debugWrapper);
        }

        log.debug("Performing session tidy up of WComponents (but note that any WComponents that are disconnected from the active top component will not be tidied up.");
        getUI().tidyUpUIContextForTree();

        log.debug("After paint - Clearing scratch map.");
        uic.clearScratchMap();
    }
}
