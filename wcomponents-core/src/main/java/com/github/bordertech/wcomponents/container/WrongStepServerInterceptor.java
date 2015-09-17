package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This wrong step interceptor prevents an old request being processed that might have been submitted by a user using
 * multiple windows.
 * <p>
 * If a step error occurs, then the user, depending on the redirect flag, is either (1) redirected to an error page or
 * (2) warped to the future by the application being rendered in its current state. When the user is warped to the
 * future, the handleStepError method is called on WApplication, which allows applications to take the appropriate
 * action for when a step error has occurred.
 * </p>
 *
 * @author Jonathan Austin
 */
public class WrongStepServerInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WrongStepServerInterceptor.class);

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

		// Get step count from the request
		int got = StepCountUtil.getRequestStep(request);

		// Match (first time, both are zero)
		// or no Step count and processing a GET
		if (expected == got || (!StepCountUtil.isStepOnRequest(request) && "GET".equals(request.
				getMethod()))) {
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else { // Invalid step
			LOG.warn(
					"SERVER: Wrong step detected. Expected step " + expected + " but got step " + got);

			// Redirect to error page
			if (StepCountUtil.isErrorRedirect()) {
				String url = StepCountUtil.getErrorUrl();
				LOG.warn("User will be redirected to an error page. URL: " + url);
				try {
					getResponse().sendRedirect(url);
				} catch (IOException e) {
					LOG.warn("Error trying to redirect for wrong step indicator.");
				}

				// Make sure the render phase is not processed
				throw new ActionEscape();
			} else {  // Warp to the future
				// Call handle step error
				WComponent application = getUI();
				if (application instanceof WApplication) {
					LOG.warn("The handleStepError method will be called on WApplication.");
					((WApplication) application).handleStepError();
				}

				LOG.warn("The render phase will warp the user back to the future.");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparePaint(final Request request) {
		// Increment the step counter
		UIContext uic = UIContextHolder.getCurrent();
		StepCountUtil.incrementSessionStep(uic);
		super.preparePaint(request);
	}

}
