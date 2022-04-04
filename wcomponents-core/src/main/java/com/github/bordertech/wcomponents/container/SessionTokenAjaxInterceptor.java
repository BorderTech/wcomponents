package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import java.util.Objects;

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

		// Check tokens match (both must be provided)
		if (Objects.equals(expected, got)) {
			// Process AJAX request
			getBackingComponent().serviceRequest(request);
		} else {
			// Invalid token on AJAX request
			throw new SessionTokenException("Wrong session token detected for AJAX request. Expected token ["
					+ expected + "] but got token [" + got + "].");
		}
	}

}
