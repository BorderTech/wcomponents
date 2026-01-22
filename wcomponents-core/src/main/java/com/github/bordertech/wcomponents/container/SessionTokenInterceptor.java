package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.util.Util;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This session token interceptor makes sure the request being processed is for the correct session.
 * <p>
 * As the token is a UUID, it will be much harder for CSRF attacks. No request processing will occur without the correct UUID.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SessionTokenInterceptor.class);

	/**
	 * Override to check whether the session token variable in the incoming request matches what we expect.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {

		// Get the expected session token (could be null for new session)
		UIContext uic = UIContextHolder.getCurrent();
		String expected = uic.getEnvironment().getSessionToken();

		// Get the session token from the request
		String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

		// Session token should not be provided on a GET URL (CSRF Rules)
		if (got != null && "GET".equals(request.getMethod())) {
			throw new IllegalStateException("A session token should not be provided on a GET");
		}

		// Check processing a GET or tokens must match
		if ("GET".equals(request.getMethod()) || (got != null && Util.equals(expected, got))) {
			// Process request
			getBackingComponent().serviceRequest(request);
		} else if (expected == null && got != null) {
			// Expired token
			LOG.debug("Session for token [" + got + "] is no longer valid or timed out.");
			throw new SessionTokenException("Session for token is no longer valid or timed out.");
		} else {
			// Wrong token
			LOG.debug("Wrong session token detected for servlet request. Expected token [" + expected + "] but got token [" + got + "].");
			throw new SessionTokenException("Wrong session token detected for servlet request.");
		}

	}

	@Override
	public void preparePaint(final Request request) {
		// Set session token
		UIContext uic = UIContextHolder.getCurrent();
		if (uic.getEnvironment().getSessionToken() == null) {
			uic.getEnvironment().setSessionToken(UUID.randomUUID().toString());
		}
		super.preparePaint(request);
	}

}
