package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.lde.PlainLauncher;
import com.github.bordertech.wcomponents.servlet.WServlet;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class extends PlainLauncher to manage sessions and UIContext for Selenium.</p>
 * <p>
 * This launcher (or subclass) must be used to search using ByWComponent or ByWComponentPath.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class SeleniumLauncher extends PlainLauncher {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SeleniumLauncher.class);

	/**
	 * List of all known session+context pairings.
	 */
	private static final Map<String, UIContext> CONTEXTS = new HashMap<>();

	/**
	 * Get the UIContext for the given session ID.
	 *
	 * @param sessionId the session.
	 * @return the session's UIContext.
	 */
	public static UIContext getContextForSession(final String sessionId) {
		if (StringUtils.isBlank(sessionId)) {
			throw new IllegalArgumentException("sessionId must not be blank.");
		}

		if (CONTEXTS.isEmpty()) {
			LOG.warn("No sessions exist in SeleniumLauncher. "
					+ "This function requires that the SeleniumLauncher (or subclass) is launched as the LDE launcher.");
		}

		return CONTEXTS.get(sessionId);
	}

	/**
	 * Remove any cached context for the given session. This method should be used when switching sessions.
	 *
	 * @param sessionId the sessionId to destroy the context.
	 */
	public static void destroyContextForSession(final String sessionId) {
		CONTEXTS.remove(sessionId);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected WServletHelper createServletHelper(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		return new SeleniumServletHelper(this, httpServletRequest, httpServletResponse);
	}

	/**
	 * Selenium specific ServletHelper to ensure the UIContext is available in the JVM at all times.
	 */
	private static final class SeleniumServletHelper extends WServletHelper {

		/**
		 * Construct a new helper.
		 *
		 * @param servlet the servlet.
		 * @param httpServletRequest the request.
		 * @param httpServletResponse the response.
		 */
		private SeleniumServletHelper(final WServlet servlet, final HttpServletRequest httpServletRequest,
				final HttpServletResponse httpServletResponse) {
			super(servlet, httpServletRequest, httpServletResponse);
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		protected void setUIContext(final UIContext uiContext) {
			super.setUIContext(uiContext);

			String sessionId = getBackingRequest().getSession().getId();
			CONTEXTS.put(sessionId, uiContext);
		}

	}
}
