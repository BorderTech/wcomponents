package com.github.openborders.container;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.ActionEscape;
import com.github.openborders.AjaxHelper;
import com.github.openborders.ComponentWithContext;
import com.github.openborders.Environment;
import com.github.openborders.ErrorCodeEscape;
import com.github.openborders.Request;
import com.github.openborders.UIContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.WApplication;
import com.github.openborders.WComponent;
import com.github.openborders.WebUtilities;
import com.github.openborders.servlet.WServlet;
import com.github.openborders.util.I18nUtilities;
import com.github.openborders.util.InternalMessages;
import com.github.openborders.util.StepCountUtil;
import com.github.openborders.util.SystemException;
import com.github.openborders.util.XMLUtil;

/**
 * This wrong step interceptor makes sure that ajax requests are only processed for the most recently rendered view.
 * <p>
 * If a step error occurs, then the user, depending on the redirect flag, is either (1)redirected to an error page or
 * (2) warped to the future by being redirected to the current page so the application is refreshed to the current
 * state. When the user is warped to the future, the handleStepError method is called on WApplication, which allows
 * applications to take the appropriate action for when a step error has occurred.
 * </p>
 * 
 * @author Jonathan Austin
 */
public class WrongStepAjaxInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(WrongStepAjaxInterceptor.class);

    /**
     * Override to check whether the step variable in the incoming request matches what we expect.
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
            throw new SystemException("No AJAX trigger id to check step count");
        }

        // Get trigger and its context
        ComponentWithContext trigger = AjaxHelper.getCurrentTriggerAndContext();
        if (trigger == null)
        {
            throw new IllegalStateException("No component/context available for AJAX trigger " + triggerId + ".");
        }

        // Get expected step count
        UIContext uic = UIContextHolder.getCurrent();
        int expected = uic.getEnvironment().getStep();

        // Step should already be set on the session
        if (expected == 0)
        {
            throw new SystemException("Step count should already be set on the session before AJAX request.");
        }

        // Get step count on the request
        int got = StepCountUtil.getRequestStep(request);

        // Check we are on the current step
        if (expected == got)
        {
            // Process Service Request
            getBackingComponent().serviceRequest(request);
        }
        // Invalid step
        else
        {
            log.warn("AJAX: Wrong step detected. Expected step " + expected + " but got step " + got);

            // "GET" Ajax requests are just ignored and return an error code
            if ("GET".equals(request.getMethod()))
            {
                log.warn("Error code will be sent in the response for AJAX GET Request.");
                handleError();
            }
            // Redirect to error page
            else if (StepCountUtil.isErrorRedirect())
            {
                log.warn("User will be redirected to an error page.");
                handleRedirect(UIContextHolder.getCurrent(), StepCountUtil.getErrorUrl(), triggerId);
            }
            // Warp to the future by refreshing the page
            else
            {
                log.warn("Warp the user back to the future by refreshing the page.");
                handleWarpToTheFuture(trigger, triggerId);
            }
            // Make sure the render phase is not processed
            throw new ActionEscape();
        }
    }

    /**
     * Warp the user to the future by replacing the entire page.
     * 
     * @param window the window ancestor of the trigger, may be null
     * @param trigger the trigger for the ajax operation.
     * @param triggerId the triggerId
     */
    private void handleWarpToTheFuture(final ComponentWithContext trigger, final String triggerId)
    {
        // Get the trigger context
        UIContext renderUic = trigger.getContext();
        UIContextHolder.pushContext(renderUic);

        try
        {
            // Increment the step counter
            StepCountUtil.incrementSessionStep(renderUic);

            // Get component at end of chain
            WComponent application = getUI();
            String url = buildApplicationUrl(renderUic);

            // Call handle step error on WApplication
            if (application instanceof WApplication)
            {
                log.warn("The handleStepError method will be called on WApplication.");
                ((WApplication) application).handleStepError();
            }

            // Build redirect response
            handleRedirect(renderUic, url, triggerId);
        }
        finally
        {
            UIContextHolder.popContext();
        }
    }

    /**
     * Redirect the user via the ajax response.
     * 
     * @param uic the current user's UI Context.
     * @param url the url to redirect page to.
     * @param targetId the targetId to include in the response.
     */
    private void handleRedirect(final UIContext uic, final String url, final String targetId)
    {
        try
        {
            // Redirect user to error page
            log.warn("User will be redirected to " + url);

            // Setup response with redirect
            getResponse().setContentType(WebUtilities.CONTENT_TYPE_XML);
            PrintWriter writer = getResponse().getWriter();

            writer.write(XMLUtil.getXMLDeclarationWithThemeXslt(uic));

            writer.print("<ui:ajaxResponse ");
            writer.print(XMLUtil.UI_NAMESPACE);
            writer.print(">");
            writer.print("<ui:ajaxTarget id=\"" + targetId + "\" action=\"replace\">");

            // Redirect URL
            writer.print("<ui:redirect url=\"" + url + "\" />");

            writer.print("</ui:ajaxTarget>");
            writer.print("</ui:ajaxResponse>");
        }
        catch (IOException e)
        {
            throw new SystemException("Error writing redirect for ajax wrong step interceptor", e);
        }
    }

    /**
     * Build the url to refresh the application.
     * 
     * @param uic the current user's context
     * @return the application url
     */
    private String buildApplicationUrl(final UIContext uic)
    {
        Environment env = uic.getEnvironment();
        return env.getPostPath();
    }

    /**
     * Throw the default error code.
     */
    private void handleError()
    {
        String msg = I18nUtilities
            .format(UIContextHolder.getCurrent().getLocale(), InternalMessages.DEFAULT_STEP_ERROR);
        throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
    }

}
