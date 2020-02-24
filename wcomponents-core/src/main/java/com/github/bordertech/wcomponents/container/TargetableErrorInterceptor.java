package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.Escape;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This interceptor catches exceptions and sets them as an error code.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TargetableErrorInterceptor extends InterceptorComponent {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TargetableErrorInterceptor.class);

	@Override
	public void serviceRequest(final Request request) {
		try {
			super.serviceRequest(request);
		} catch (Escape escape) {
			throw escape;
		} catch (SessionTokenException e) {
			// Log session token exception as warn to reduce noise in error logs
			LOG.warn(e.getMessage());
			handleRequestError(getSessionErrorMessage(), e);
		} catch (TargetableIdException e) {
			// Log targetable ID exception as warn to reduce noise in error logs
			LOG.warn(e.getMessage());
			handleRequestError(getDefaultMessage(), e);
		} catch (Exception e) {
			LOG.error("Error processing content request in action phase. " + e.getMessage(), e);
			handleError(getDefaultMessage(), e);
		}
	}

	@Override
	public void preparePaint(final Request request) {
		try {
			super.preparePaint(request);
		} catch (Escape escape) {
			throw escape;
		} catch (Exception e) {
			LOG.error("Error processing content request in prepare paint. " + e.getMessage(), e);
			handleError(getDefaultMessage(), e);
		}
	}

	/**
	 * Throw the default error code.
	 *
	 * @param msg the error message
	 * @param original the original exception
	 */
	private void handleError(final String msg, final Throwable original) {
		throw new ErrorCodeEscape(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg, original);
	}

	/**
	 * Throw the request error code.
	 *
	 * @param msg the error message
	 * @param original the original exception
	 */
	private void handleRequestError(final String msg, final Throwable original) {
		throw new ErrorCodeEscape(HttpServletResponse.SC_BAD_REQUEST, msg, original);
	}

	/**
	 * @return the default content error message
	 */
	private String getDefaultMessage() {
		String msg = I18nUtilities.format(UIContextHolder.getCurrent().getLocale(),
				InternalMessages.DEFAULT_CONTENT_ERROR);
		return msg;
	}

	/**
	 * @return the session token error message
	 */
	private String getSessionErrorMessage() {
		String msg = I18nUtilities.format(UIContextHolder.getCurrent().getLocale(),
				InternalMessages.DEFAULT_SESSION_TOKEN_ERROR);
		return msg;
	}
}
