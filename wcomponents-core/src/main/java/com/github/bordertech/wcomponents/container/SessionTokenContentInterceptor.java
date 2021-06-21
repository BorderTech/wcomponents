package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import java.util.Objects;

/**
 * This session token interceptor makes sure the content request being processed is for the correct session.
 * <p>
 * Similar to {@link SessionTokenInterceptor} but caters for setting error codes for content requests such as
 * {@link WImage} when a token error is detected.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenContentInterceptor extends InterceptorComponent {

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

		// Session token should already be set for a content request
		if (expected == null) {
			throw new SessionTokenException("Session token should already be set on the session before content request."
					+ " Can be due to the session timing out.");
		}

		// Get the session token from the content request
		String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

		// Check tokens match (both must be provided) or check if cached content (no session token on request)
		if (Objects.equals(expected, got) || (got == null && StepCountUtil.isCachedContentRequest(request))) {
			// Process content request
			getBackingComponent().serviceRequest(request);
		} else {
			// Invalid token on content request
			throw new SessionTokenException("Wrong session token detected for content request. Expected token ["
					+ expected + "] but got token [" + got + "].");
		}

	}

}
