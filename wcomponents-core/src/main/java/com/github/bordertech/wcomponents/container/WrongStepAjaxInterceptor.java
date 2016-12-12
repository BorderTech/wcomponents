package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.XMLUtil;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class WrongStepAjaxInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WrongStepAjaxInterceptor.class);

	/**
	 * The AJAX trigger ID.
	 */
	private String triggerId;

	/**
	 * The redirect url if a step error occurred.
	 */
	private String redirectUrl;

	/**
	 * Override to check whether the step variable in the incoming request matches what we expect.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Get trigger id
		triggerId = request.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME);
		if (triggerId == null) {
			throw new SystemException("No AJAX trigger id to check step count");
		}

		// Get trigger and its context
		ComponentWithContext trigger = AjaxHelper.getCurrentTriggerAndContext();
		if (trigger == null) {
			throw new IllegalStateException(
					"No component/context available for AJAX trigger " + triggerId + ".");
		}

		// Get expected step count
		UIContext uic = UIContextHolder.getCurrent();
		int expected = uic.getEnvironment().getStep();

		// Step should already be set on the session
		if (expected == 0) {
			throw new SystemException(
					"Step count should already be set on the session before AJAX request.");
		}

		// Get step count on the request
		int got = StepCountUtil.getRequestStep(request);

		// Check we are on the current step
		if (expected == got) {
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else { // Invalid step
			LOG.warn("AJAX: Wrong step detected. Expected step " + expected + " but got step " + got);
			// "GET" Ajax requests are just ignored and return an error code
			if ("GET".equals(request.getMethod())) {
				LOG.warn("Error code will be sent in the response for AJAX GET Request.");
				handleErrorCode();
				// Make sure the render phase is not processed
				throw new ActionEscape();
			} else if (StepCountUtil.isErrorRedirect()) { // Redirect to error page
				LOG.warn("User will be redirected to an error page.");
				redirectUrl = StepCountUtil.getErrorUrl();
			} else {  // Warp to the future by refreshing the page
				LOG.warn("Warp the user back to the future by refreshing the page.");
				handleWarpToTheFuture(uic);
				redirectUrl = buildApplicationUrl(uic);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparePaint(final Request request) {
		if (redirectUrl != null) {
			return;
		}
		super.preparePaint(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		if (redirectUrl != null) {
			if (renderContext instanceof WebXmlRenderContext) {
				handleRenderRedirect(((WebXmlRenderContext) renderContext).getWriter());
			}
			return;
		}
		super.paint(renderContext);
	}

	/**
	 * Warp the user to the future by replacing the entire page.
	 *
	 * @param uic the current user context
	 */
	private void handleWarpToTheFuture(final UIContext uic) {

		// Increment the step counter
		StepCountUtil.incrementSessionStep(uic);

		// Get component at end of chain
		WComponent application = getUI();

		// Call handle step error on WApplication
		if (application instanceof WApplication) {
			LOG.warn("The handleStepError method will be called on WApplication.");
			((WApplication) application).handleStepError();
		}
	}

	/**
	 * Redirect the user via the AJAX response.
	 *
	 * @param writer the print writer for the response
	 */
	private void handleRenderRedirect(final PrintWriter writer) {
		UIContext uic = UIContextHolder.getCurrent();
		// Redirect user to error page
		LOG.warn("User will be redirected to " + redirectUrl);

		// Setup response with redirect
		getResponse().setContentType(WebUtilities.CONTENT_TYPE_XML);
		writer.write(XMLUtil.getXMLDeclarationWithThemeXslt(uic));

		writer.print("<ui:ajaxresponse ");
		writer.print(XMLUtil.UI_NAMESPACE);
		writer.print(">");
		writer.print("<ui:ajaxtarget id=\"" + triggerId + "\" action=\"replace\">");

		// Redirect URL
		writer.print("<ui:redirect url=\"" + redirectUrl + "\" />");

		writer.print("</ui:ajaxtarget>");
		writer.print("</ui:ajaxresponse>");
	}

	/**
	 * Build the url to refresh the application.
	 *
	 * @param uic the current user's context
	 * @return the application url
	 */
	private String buildApplicationUrl(final UIContext uic) {
		Environment env = uic.getEnvironment();
		return env.getPostPath();
	}

	/**
	 * Throw the default error code.
	 */
	private void handleErrorCode() {
		String msg = I18nUtilities
				.format(UIContextHolder.getCurrent().getLocale(),
						InternalMessages.DEFAULT_STEP_ERROR);
		throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
	}

}
