package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class WrongStepContentInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WrongStepContentInterceptor.class);

	/**
	 * Override to check whether the step variable in the incoming request matches what we expect.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Get expected step count
		UIContext uic = UIContextHolder.getCurrent();
		int expected = uic.getEnvironment().getStep();

		// Step should already be set on the session
		if (expected == 0) {
			throw new SystemException(
					"Step count should already be set on the session before content request.");
		}

		// Get step count on the request
		int got = StepCountUtil.getRequestStep(request);

		// Check tokens match (both must be provided)
		if (expected == got) {
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else if (!StepCountUtil.isStepOnRequest(request) && StepCountUtil.isCachedContentRequest(request)) {  // Check cached content (no step on request)
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else {  // Invalid token
			// Set an error code
			LOG.warn(
					"Wrong step detected for content request. Expected step [" + expected + "] but got step [" + got
					+ "].");
			handleError();
		}

	}

	/**
	 * Throw the default error code.
	 */
	private void handleError() {
		String msg = I18nUtilities
				.format(UIContextHolder.getCurrent().getLocale(),
						InternalMessages.DEFAULT_STEP_ERROR);
		throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
	}
}
