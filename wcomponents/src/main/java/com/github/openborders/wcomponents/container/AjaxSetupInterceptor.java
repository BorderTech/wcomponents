package com.github.openborders.wcomponents.container;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.wcomponents.AjaxHelper;
import com.github.openborders.wcomponents.AjaxOperation;
import com.github.openborders.wcomponents.ComponentWithContext;
import com.github.openborders.wcomponents.RenderContext;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WebUtilities;
import com.github.openborders.wcomponents.servlet.WServlet;
import com.github.openborders.wcomponents.util.SystemException;

/**
 * This interceptor setups the AJAX operation details.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AjaxSetupInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(AjaxSetupInterceptor.class);

    /**
     * Setup the AJAX operation details.
     * 
     * @param request the request being serviced.
     */
    @Override
    public void serviceRequest(final Request request)
    {
        // Get trigger id
        String triggerId = request.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME);
        if (triggerId == null)
        {
            throw new SystemException("No AJAX trigger id to on request");
        }

        // Get AJAX operations
        Map<String, AjaxOperation> operations = (Map<String, AjaxOperation>) request
            .getSessionAttribute(AjaxHelper.AJAX_OPERATIONS_SESSION_KEY);
        if (operations == null)
        {
            throw new SystemException("No AJAX operations have been registered. Trigger " + triggerId + ".");
        }

        // Get Operation for this trigger
        AjaxOperation ajaxOperation = operations.get(triggerId);
        if (ajaxOperation == null)
        {
            throw new SystemException("No AJAX operation has been registered for trigger " + triggerId + ".");
        }

        // Find the Component for this trigger
        ComponentWithContext trigger = WebUtilities.getComponentById(ajaxOperation.getTriggerId(), true);
        if (trigger == null)
        {
            throw new SystemException("No component found for AJAX trigger " + triggerId + ".");
        }

        // Set current operation
        AjaxHelper.setCurrentOperationDetails(ajaxOperation, trigger);

        // Process Service Request
        super.serviceRequest(request);
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final RenderContext renderContext)
    {
        try
        {
            super.paint(renderContext);
        }
        finally
        {
            AjaxHelper.clearCurrentOperationDetails();
        }
    }
}
