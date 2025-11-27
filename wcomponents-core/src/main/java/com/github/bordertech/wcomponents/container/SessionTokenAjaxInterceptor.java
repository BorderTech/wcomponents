package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This session token interceptor makes sure the ajax request being processed is for the correct session.
 * <p>
 * Similar to {@link SessionTokenInterceptor} but sets an error code when a token error is detected.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenAjaxInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SessionTokenAjaxInterceptor.class);

	/**
	 * Override to check whether the session token variable in the incoming request matches what we expect.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {

		// Get the expected session token
		UIContext uic = UIContextHolder.getCurrent();
		String expected = uic.getEnvironment().getSessionToken();

		// Session token should already be set for an AJAX request
		if (expected == null) {
			throw new SessionTokenException("Session token should already be set on the session before AJAX request."
					+ " Can be due to the session timing out.");
		}

		// Get the session token from the AJAX request
		String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

		// Session token should not be provided on a GET URL (CSRF Rules)
		if (got != null && "GET".equals(request.getMethod())) {
			throw new IllegalStateException("A session token should not be provided on a GET");
		}

		// Check processing a GET or tokens must match
		if ("GET".equals(request.getMethod()) || (got != null && Util.equals(expected, got))) {
			// Process AJAX request
			getBackingComponent().serviceRequest(request);
		} else {
			// Invalid token on AJAX request
			LOG.debug("Wrong session token detected for AJAX request. Expected token [" + expected + "] but got token [" + got + "].");
			throw new SessionTokenException("Wrong session token detected for AJAX request.");
		}
	}

}
