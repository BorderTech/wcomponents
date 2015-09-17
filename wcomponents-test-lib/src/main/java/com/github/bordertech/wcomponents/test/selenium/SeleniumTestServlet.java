package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.lde.TestServlet;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * An extension of TestServlet to use when running tests in Selenium.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SeleniumTestServlet extends TestServlet {

	/**
	 * A map of UIContext by test case instance identifier. These are used instead of the UIContext in the HTTP session
	 * so that the jUnit test case has visibility of the UIContext being used.
	 */
	private static final Map<String, UIContext> CONTEXT_MAP = new HashMap<>();

	/**
	 * The singleton instance of the test servlet.
	 */
	private static final SeleniumTestServlet SERVLET = new SeleniumTestServlet();

	/**
	 * A count of the number of complete calls to the service method.
	 */
	private static int serviceCount = 0;

	/**
	 * Used for synchronization for the service count.
	 */
	private static final Object LOCK = new Object();

	/**
	 * This parameter is sent on each http request to identify the selenium test in use.
	 */
	protected static final String REQUEST_TEST_CASE_ID_PARAM = "SeleniumTestServlet.seleniumTestId";

	/**
	 * Subclasses may override this to register additional servlets with the server.
	 *
	 * @param webapp the webapp to register the servlets with.
	 */
	@Override
	protected void registerServlets(final WebAppContext webapp) {
		webapp.addServlet(getClass().getName(), "/app/*");
	}

	/**
	 * @param httpServletRequest the request being processed
	 * @return an interceptor chain to use when servicing requests.
	 */
	@Override
	public InterceptorComponent createInterceptorChain(final Object httpServletRequest) {
		InterceptorComponent backing = super.createInterceptorChain(httpServletRequest);
		HttpServletRequest request = (HttpServletRequest) httpServletRequest;

		// Only inject the script into normal round-trip requests.
		if (request.getParameter(DATA_LIST_PARAM_NAME) == null
				&& request.getParameter(AJAX_TRIGGER_PARAM_NAME) == null
				&& request.getParameter(TARGET_ID_PARAM_NAME) == null) {
			InterceptorComponent seleniumInterceptor = new ScriptInterceptor();
			seleniumInterceptor.setBackingComponent(backing);

			return seleniumInterceptor;
		}

		return backing;
	}

	/**
	 * Override in order to notify listeners when a request has been processed.
	 *
	 * @param req the {@link HttpServletRequest} object that contains the request the client made of the servlet
	 *
	 * @param res the {@link HttpServletResponse} object that contains the response the servlet returns to the client
	 *
	 * @throws ServletException a servlet exception
	 * @throws IOException an IO exception
	 */
	@Override
	public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
		synchronized (LOCK) {
			super.service(req, res);
			incrementServiceCount();
		}
	}

	/**
	 * Increments the service count.
	 */
	private static void incrementServiceCount() {
		synchronized (LOCK) {
			serviceCount++;
		}
	}

	/**
	 * Returns the number of times that the service method has been successfully invoked.
	 *
	 * @return the service count.
	 */
	public static int getServiceCount() {
		synchronized (LOCK) {
			return serviceCount;
		}
	}

	/**
	 * Sets the UIContext.
	 *
	 * @param identifier the context identifier
	 * @param uic the UIContext to set.
	 */
	public static synchronized void setUiContext(final String identifier, final UIContext uic) {
		CONTEXT_MAP.put(identifier, uic);
	}

	/**
	 * Removes the UIContext with the given key.
	 *
	 * @param identifier the identifier of the context to remove.
	 *
	 * @see #setUiContext(String, UIContext)
	 */
	public static synchronized void removeUiContext(final String identifier) {
		CONTEXT_MAP.remove(identifier);
	}

	/**
	 * This method has been overridden to return the configured ui.
	 *
	 * @param httpServletRequest the httpServletRequest being handled.
	 * @return the ui to use.
	 */
	@Override
	public WComponent getUI(final Object httpServletRequest) {
		HttpServletRequest request = (HttpServletRequest) httpServletRequest;
		String pathInfo = request.getPathInfo();

		if (pathInfo != null) {
			String testId = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
			UIContext uic = CONTEXT_MAP.get(testId);

			if (uic == null) {
				throw new SystemException("Failed to get UIContext from map");
			}

			return uic.getUI();
		}

		return super.getUI(httpServletRequest);
	}

	/**
	 * Starts the Servlet.
	 *
	 * @throws Exception if there is an error starting the servlet.
	 */
	public static void startServlet() throws Exception {
		SERVLET.run();
	}

	/**
	 * Stops the Servlet.
	 *
	 * @throws InterruptedException if there is an error stopping the servlet.
	 */
	public static void stopServlet() throws InterruptedException {
		SERVLET.stop();
	}

	/**
	 * @return the servlet URL.
	 */
	public static String getServletUrl() {
		return SERVLET.getUrl();
	}

	/**
	 * Starts up Jetty for use in the unit tests.
	 *
	 * @throws Exception if there is an error starting the server.
	 */
	@Override
	public void run() throws Exception {
		// Run on a randomly available port.
		Config.getInstance().setProperty("bordertech.wcomponents.lde.server.port", "0");
		super.run();
	}

	/**
	 * Overridden to return a SeleniumServletHelper.
	 *
	 * @param httpServletRequest the current servlet request.
	 * @param httpServletResponse the servlet response for the current request.
	 * @return a servlet helper for the given request/response.
	 */
	@Override
	protected WServletHelper createServletHelper(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) {
		return new SeleniumServletHelper(this, httpServletRequest, httpServletResponse);
	}

	/**
	 * <p>
	 * This servlet helper passes the request test case ID parameter around between requests, and also makes it
	 * available to the SeleniumTestServlet as a request attribute.</p>
	 *
	 * <p>
	 * The helper also ensures that we are using the test case's UIContext.</p>
	 */
	private static final class SeleniumServletHelper extends WServletHelper {

		/**
		 * We need the WComponent request object created immediately so that we can place the attribute in the request.
		 * NOTE: The creation of this request object performs a destructive read on the servlet input stream when a file
		 * has been uploaded.
		 */
		private final Request request = createRequest();
		private final String testId;

		/**
		 * Creates a SeleniumServletHelper.
		 *
		 * @param servlet the SeleniumTestServlet instance.
		 * @param httpServletRequest the servlet request being responded to.
		 * @param httpServletResponse the servlet response.
		 */
		private SeleniumServletHelper(final WServlet servlet, final HttpServletRequest httpServletRequest,
				final HttpServletResponse httpServletResponse) {
			super(servlet, httpServletRequest, httpServletResponse);

			String pathInfo = httpServletRequest.getPathInfo();

			if (pathInfo == null) {
				testId = null;
			} else {
				testId = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
			}
		}

		/**
		 * We want to use the UIContext from the test case rather than creating and storing it in the HttpSession.
		 *
		 * @return the correct UIContext instance for the current test case.
		 */
		@Override
		protected UIContext getUIContext() {
			UIContext uic = super.getUIContext();

			if (uic == null) {
				uic = CONTEXT_MAP.get(testId);
				getBackingRequest().getSession().setAttribute(getUiContextSessionKey(), uic);
			}

			return uic;
		}

		@Override
		protected Request getRequest() {
			return request;
		}
	}

	/**
	 * This interceptor is used to serve up the additional javascript required for Selenium tests.
	 */
	private static final class ScriptInterceptor extends InterceptorComponent {
		// A new interceptor chain is created for each request, so it is safe to store state here.

		/**
		 * The requested script.
		 */
		private String scriptRequested = null;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void serviceRequest(final Request request) {
			scriptRequested = request.getParameter("seleniumTestScript");

			// Same basic sanity checking
			if (Util.empty(scriptRequested) || scriptRequested.indexOf('/') != -1 || !scriptRequested.endsWith(".js")) {
				scriptRequested = null;
			}

			if (scriptRequested == null) {
				super.serviceRequest(request);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void preparePaint(final Request request) {
			UIContext uic = UIContextHolder.getCurrent();

			if (scriptRequested == null) {
				super.preparePaint(request);

				StringBuffer path = new StringBuffer(uic.getEnvironment().getPostPath());
				path.append("?seleniumTestScript=seleniumBefore.js");

				for (Map.Entry<String, String> entry : uic.getEnvironment().getHiddenParameters().entrySet()) {
					path.append('&');
					path.append(WebUtilities.escapeForUrl(entry.getKey()));
					path.append('=');
					path.append(WebUtilities.escapeForUrl(entry.getValue()));
				}

				String pathStr = WebUtilities.encode(path.toString());
				uic.getHeaders().addHeadLine("<script type=\"text/javascript\" src=\"" + pathStr + "\"/>");
			} else {
				uic.getHeaders().setContentType("application/x-javascript");
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paint(final RenderContext renderContext) {
			if (scriptRequested == null) {
				super.paint(renderContext);
			} else if (renderContext instanceof WebXmlRenderContext) {
				WebXmlRenderContext webContext = (WebXmlRenderContext) renderContext;

				try {
					InputStream stream = getClass().getResourceAsStream(
							"/com/github/bordertech/wcomponents/test/selenium/" + scriptRequested);
					webContext.getWriter().write(new String(StreamUtil.getBytes(stream), "UTF-8"));
				} catch (Exception e) {
					LogFactory.getLog(getClass()).error("Failed to write selenium script", e);
				}
			} else {
				throw new SystemException("Unable to render to " + renderContext);
			}
		}
	}
}
