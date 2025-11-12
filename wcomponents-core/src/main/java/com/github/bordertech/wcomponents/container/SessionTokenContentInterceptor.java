package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;

/**
 * This session token interceptor makes sure the session token on content requests are handled correctly for CSRF.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenContentInterceptor extends InterceptorComponent {

	/**
	 * Override to check whether the session token is handled correctly for CSRF.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {

		// Get the current session token
		UIContext uic = UIContextHolder.getCurrent();
		String expected = uic.getEnvironment().getSessionToken();

		// Session token should already be set for a content request
		if (expected == null) {
			throw new SessionTokenException("Session token should already be set on the session before content request."
					+ " Can be due to the session timing out.");
		}

		// Content requests should only be a GET (CSRF Rules)
		if (!"GET".equals(request.getMethod())) {
			throw new IllegalStateException("Content request should only be a GET");
		}

		// Check no session token on the content request (CSRF Rules)
		String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);
		if (got != null) {
			throw new IllegalStateException("A session token should not be provided on a GET");
		}

		getBackingComponent().serviceRequest(request);

	}

}
