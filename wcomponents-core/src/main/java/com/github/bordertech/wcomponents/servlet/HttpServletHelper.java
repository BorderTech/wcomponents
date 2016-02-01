package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.AbstractEnvironment;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UserAgentInfo;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.container.AbstractContainerHelper;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A servlet specific ContainerHelper.
 * <p>
 * Chiefly it handles the creation and storage of each users {@link UIContext} in the servlet session, and it maps
 * servlet requests and responses to wcomponent requests and responses.
 * </p>
 * <p>
 * An instance is created each request being processed.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class HttpServletHelper extends AbstractContainerHelper {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(HttpServletHelper.class);

	/**
	 * The name of the servlet initialisation parameter whose value is used to distinguish "new" sessions from "ongoing"
	 * sessions.
	 */
	public static final String ONGOING_URL_SUFFIX = "suffix";

	/**
	 * Flag that update already processed in the action phase.
	 */
	private boolean environmentUpdated = false;

	/**
	 * The servlet which is handling the request.
	 */
	private HttpServlet servlet;

	/**
	 * The request being responded to.
	 */
	private HttpServletRequest httpServletRequest;

	/**
	 * The response for the current request.
	 */
	private HttpServletResponse httpServletResponse;

	/**
	 * The name of a session parameter used to store the UIContext. We try to make the key unique between servlets by
	 * prefixing it with the class name of this servlet.
	 */
	private final String uiContextSessionKey;

	/**
	 * The ID of the targeted component for an AJAX or content request.
	 */
	private final String targetComponentId;

	/**
	 * Indicates whether the current request is for a data list.
	 */
	private final boolean dataRequest;

	/**
	 * @param servlet the servlet processing the request
	 * @param httpServletRequest the servlet request being processed
	 * @param httpServletResponse the servlet response
	 */
	public HttpServletHelper(final HttpServlet servlet, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) {
		// The name of a session parameter used to store the UIContext.
		// We try to make the key unique between servlets by prefixing it
		// with the name of the servlet.
		this.uiContextSessionKey = servlet.getClass().getName() + ".servlet.model";

		this.servlet = servlet;
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;

		dataRequest = httpServletRequest.getParameter(WServlet.DATA_LIST_PARAM_NAME) != null;

		String target = httpServletRequest.getParameter(WServlet.AJAX_TRIGGER_PARAM_NAME);
		if (target == null) {
			target = httpServletRequest.getParameter(WServlet.TARGET_ID_PARAM_NAME);
		}

		this.targetComponentId = target;
	}

	/**
	 * @return the servlet processing this request
	 */
	public HttpServlet getServlet() {
		return servlet;
	}

	/**
	 * @return the backing http request.
	 */
	public HttpServletRequest getBackingRequest() {
		return httpServletRequest;
	}

	/**
	 * @return the backing http response
	 */
	public HttpServletResponse getBackingResponse() {
		return httpServletResponse;
	}

	/**
	 * @return the ui context session key.
	 */
	public String getUiContextSessionKey() {
		return uiContextSessionKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispose() {
		servlet = null;
		httpServletRequest = null;
		httpServletResponse = null;

		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UIContext getUIContext() {
		HttpSession session = httpServletRequest.getSession(false);
		if (session == null) {
			return null;
		}
		UIContext uic = (UIContext) session.getAttribute(uiContextSessionKey);
		return uic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUIContext(final UIContext uiContext) {
		HttpSession session = httpServletRequest.getSession();
		session.setAttribute(uiContextSessionKey, uiContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UIContext createUIContext() {
		if (targetComponentId != null) {
			throw new SystemException("AJAX request for a session with no context set");
		}
		return super.createUIContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Environment createEnvironment() {
		HttpServletRequest request = getBackingRequest();
		String postPath = getResponseUrl(request);
		String baseUrl = getBaseUrl(request);
		String userAgent = request.getHeader("user-agent");

		/**
		 * Careful - this won't be serializable
		 */
		HttpServletEnvironment env = new HttpServletEnvironment(postPath, baseUrl, userAgent);
		if (request instanceof SubSessionHttpServletRequestWrapper) {
			env.setSubsessionId(((SubSessionHttpServletRequestWrapper) request).getSessionId());
		}

		return env;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateEnvironment(final Environment env) {
		// Check if update already applied (so not applied in render phase)
		if (!environmentUpdated) {
			environmentUpdated = true;
			// Post path may have changed (if mapped with multiple URLs)
			String postPath = getResponseUrl(httpServletRequest);
			env.setPostPath(postPath);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request createRequest() {
		Request request = new ServletRequest(httpServletRequest);
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateRequest(final Request request) {
		// Nothing required here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setTitle(final String title) {
		// nop
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PrintWriter getPrintWriter() throws IOException {
		PrintWriter writer = httpServletResponse.getWriter();
		return writer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Response getResponse() {
		return new ServletResponse(httpServletResponse);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void invalidateSession() {
		HttpSession session = httpServletRequest.getSession(true);
		session.invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void redirectForLogout() {
		String url = Config.getInstance().getString("bordertech.wcomponents.logout.url", null);

		if (Util.empty(url)) {
			LOG.warn("No logout URL specified");

			try {
				getResponse().getWriter().write("Logged out successfully");
			} catch (IOException e) {
				LOG.error("Failed to send logout message", e);
			}
		} else {
			try {
				getResponse().sendRedirect(url);
			} catch (IOException e) {
				LOG.error("Failed to redirect to logout url " + url, e);
			}
		}
	}

	/**
	 * Get the URL that responses should be directed to (ie, the URL in forms and hyperlinks). This is usually the same
	 * as the current request URL.
	 *
	 * @param request the incoming HttpServletRequest
	 * @return the URL that will be used in the form's action attribute and also in hyperlinks
	 */
	protected String getResponseUrl(final HttpServletRequest request) {
		String suffix = servlet.getServletConfig().getInitParameter(ONGOING_URL_SUFFIX);
		String uri = request.getRequestURI();

		if (suffix != null && uri.indexOf(suffix) == -1) {
			return uri + suffix;
		} else {
			return uri;
		}
	}

	/**
	 * Get the base url that corresponds to this request. For instance, if the web application is mounted in the web
	 * context "evisas" on the server "localhost", and the request is for the URL
	 * "http://localhost/evisas/some/thing/page", then the returned base url will be "http://localhost/evisas".
	 *
	 * @param request the incoming request
	 * @return the base url for the web application.
	 */
	protected String getBaseUrl(final HttpServletRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Extract base url from " + request);
		}

		StringBuffer results = new StringBuffer();

		results.append(request.getScheme());
		results.append("://");
		results.append(request.getServerName());

		if ((!("http".equals(request.getScheme()) && (80 == request.getServerPort())))
				&& (!("https".equals(request.getScheme()) && (443 == request.getServerPort())))) {
			results.append(':');
			results.append(request.getServerPort());
		}

		results.append(request.getContextPath());
		// Make sure to strip any trailing slash
		if (results.charAt(results.length() - 1) == '/') {
			return results.substring(0, results.length() - 1);
		}

		return results.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addGenericHeaders(final UIContext uic, final WComponent ui) {
		// Note: This effectively prevents caching of anything served up from a WServlet.
		// We are ok for WContent and thrown ContentEscapes, as addGenericHeaders will not be called
		if (httpServletRequest instanceof SubSessionHttpServletRequestWrapper) {
			httpServletResponse.setHeader("Cache-Control", CacheType.NO_CACHE.getSettings());
			httpServletResponse.setHeader("Pragma", "no-cache");
			httpServletResponse.setHeader("Expires", "-1");
		}
		// This is to prevent clickjacking. It can also be set to "DENY" to prevent embedding in a frames at all or
		// "ALLOW-FROM uri" to allow embedding in a frame within a particular site.
		// The default will allow WComponents applications in a frame on the same origin.
		httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleError(final Throwable error) throws IOException {
		if (targetComponentId != null || dataRequest) {
			getResponse().sendError(500, "Internal Error");
		} else {
			super.handleError(error);
		}
	}

	/**
	 * The HttpServlet implementation of the WEnvironment interface.
	 */
	public static class HttpServletEnvironment extends AbstractEnvironment {

		/**
		 * The subsession id for this environment.
		 */
		private int subsessionId = -1;

		/**
		 * @param postPath the post path
		 * @param baseUrl the base url
		 * @param userAgent the user agent string
		 */
		public HttpServletEnvironment(final String postPath, final String baseUrl,
				final String userAgent) {
			this(postPath, baseUrl, new UserAgentInfo(userAgent));
			LOG.debug("user-agent: " + userAgent);
		}

		/**
		 * @param postPath the post path
		 * @param baseUrl the base url
		 * @param userAgentInfo the user agent info
		 */
		public HttpServletEnvironment(final String postPath, final String baseUrl,
				final UserAgentInfo userAgentInfo) {
			setPostPath(postPath);
			setBaseUrl(baseUrl);
			setUserAgentInfo(userAgentInfo);
			setAppId("app");

			try {
				URL url = new URL(baseUrl);
				setHostFreeBaseUrl(url.getPath());
			} catch (MalformedURLException ex) {
				// This should not happen
				LOG.warn("Invalid base url: " + baseUrl);
				setHostFreeBaseUrl(baseUrl);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAppHostPath() {
			return getHostFreeBaseUrl();
		}

		/**
		 * Sets the subsession id.
		 *
		 * @param subsessionId The sessionId to set.
		 */
		public void setSubsessionId(final int subsessionId) {
			this.subsessionId = subsessionId;
		}

		/**
		 * Override getHiddenParameters in order to add the subsession id.
		 *
		 * @return the hidden parameter map.
		 */
		@Override
		public Map<String, String> getHiddenParameters() {
			Map<String, String> params = super.getHiddenParameters();

			if (subsessionId >= 0) {
				params.put("ssid", String.valueOf(subsessionId));
			}

			return params;
		}
	}
}
