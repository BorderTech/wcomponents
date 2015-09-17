package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WAudio;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WVideo;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This session token interceptor makes sure the content request being processed is for the correct
 * session.
 * <p>
 * Similar to {@link SessionTokenInterceptor} but caters for setting error codes for content
 * requests such as {@link WImage} when a token error is detected.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SessionTokenContentInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SessionTokenContentInterceptor.class);

	/**
	 * Override to check whether the session token variable in the incoming request matches what we
	 * expect.
	 *
	 * @param request the request being serviced.
	 */
	@Override
	public void serviceRequest(final Request request) {
		// Get the expected session token
		UIContext uic = UIContextHolder.getCurrent();
		String expected = uic.getEnvironment().getSessionToken();

		// Session token should already be set
		if (expected == null) {
			throw new SystemException("Session token should already be set on the session before content request.");
		}

		// Get the session token from the request
		String got = request.getParameter(Environment.SESSION_TOKEN_VARIABLE);

		// Check tokens match (both must be provided)
		if (Util.equals(expected, got)) {
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else if (got == null && checkCachedContent(request)) {  // Check cached content (no session token on request)
			// Process Service Request
			getBackingComponent().serviceRequest(request);
		} else {  // Invalid token
			// Set an error code
			LOG.warn("Wrong session token detected for content request. Expected token [" + expected
					+ "] but got token [" + got + "].");
			handleError();
		}

	}

	/**
	 * Check for cached content.
	 *
	 * @param request the request being processed
	 * @return true if content is cached, otherwise false
	 */
	private boolean checkCachedContent(final Request request) {
		// Get target id on request
		String targetId = request.getParameter(Environment.TARGET_ID);
		if (targetId == null) {
			return false;
		}

		// Get target
		ComponentWithContext targetWithContext = WebUtilities.getComponentById(targetId, true);
		if (targetWithContext == null) {
			return false;
		}

		// Check for caching key
		WComponent target = targetWithContext.getComponent();

		// TODO Look at implementing CacheableTarget interface
		String key = null;
		if (target instanceof WContent) {
			key = ((WContent) target).getCacheKey();
		} else if (target instanceof WImage) {
			key = ((WImage) target).getCacheKey();
		} else if (target instanceof WVideo) {
			key = ((WVideo) target).getCacheKey();
		} else if (target instanceof WAudio) {
			key = ((WAudio) target).getCacheKey();
		}
		return !Util.empty(key);
	}

	/**
	 * Throw the default error code.
	 */
	private void handleError() {
		String msg = I18nUtilities.format(UIContextHolder.getCurrent().getLocale(),
				InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
		throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg);
	}

}
