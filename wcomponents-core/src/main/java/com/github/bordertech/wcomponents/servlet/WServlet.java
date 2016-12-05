package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UserAgentInfo;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.servlet.HttpServletHelper.HttpServletEnvironment;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class enables hosting of wcomponents in a servlet container. To get a basic wcomponent application running in a
 * servlet container:
 * <ul>
 * <li>Build your own wcomponent application from other building block wcomponents.</li>
 * <li>Extend WServlet, overriding the getUI(Object) method to return your wcomponent application.</li>
 * <li>Add your WServlet to your web.xml file.</li>
 * </ul>
 *
 * @author James Gifford
 * @author Martin Shevchenko
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WServlet extends HttpServlet {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WServlet.class);

	/**
	 * The name of the parameter that will specify the id of the element that triggered the AJAX request.
	 */
	public static final String AJAX_TRIGGER_PARAM_NAME = "wc_ajax";

	/**
	 * The name of the parameter that will specify this is an internal component AJAX request.
	 */
	public static final String AJAX_TRIGGER_INTERNAL_PARAM_NAME = "wc_ajax_int";

	/**
	 * The name of the parameter that will specify the data list to fetch.
	 */
	public static final String DATA_LIST_PARAM_NAME = "wc_data";

	/**
	 * The name of the parameter that will specify the name of the static resource to fetch.
	 */
	public static final String STATIC_RESOURCE_PARAM_NAME = "wc_static";

	/**
	 * The URL/Post variable that will identify a specific wcomponent to process the request. A request that includes
	 * this variable is called a targeted request. Targeted requests are used to return document content such as image
	 * data and PDFs. It is also used to support AJAX regions. Targeted requests are handled by the
	 * WContentHelperServlet.
	 */
	public static final String TARGET_ID_PARAM_NAME = Environment.TARGET_ID;

	/**
	 * The component to serve up when no UI has been specified.
	 */
	private final WComponent noUI = new WLabel("You must override the getUI method in " + getClass());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		// To enable server-side generation of images
		System.setProperty("java.awt.headless", "true");
	}

	/**
	 * Service an HTTP request. The {@link #serviceInt(HttpServletRequest, HttpServletResponse)} method is called to do
	 * the actual servicing. If the serviceInt method throws an exception, it is caught and the user is shown a generic
	 * error page using the {@link #handleError(HttpServletRequest, HttpServletResponse, Throwable)} method.
	 *
	 * @param httpServletRequest the request being processed
	 * @param httpServletResponse the servelt response
	 *
	 * @throws IOException an IO exception
	 * @throws ServletException a servlet exception
	 *
	 * @see #serviceInt(HttpServletRequest, HttpServletResponse)
	 * @see #handleError(HttpServletRequest, HttpServletResponse, Throwable)
	 */
	@Override
	protected void service(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		String path = httpServletRequest.getRequestURI();

		try {
			LOG.info("Service called on " + this + "... from " + path);

			if (ServletUtil.isEnableSubSessions()) {
				SubSessionHttpServletRequestWrapper requestWrapper = new SubSessionHttpServletRequestWrapper(
						httpServletRequest);
				serviceInt(requestWrapper, httpServletResponse);
			} else {
				serviceInt(httpServletRequest, httpServletResponse);
			}
		} catch (Throwable t) {
			// We don't let any exception propagate to the servlet container.
			LOG.error("WServlet caught exception.", t);
			handleError(httpServletRequest, httpServletResponse, t);
		} finally {
			LOG.info("...service complete");
		}
	}

	/**
	 * Service internal. This method does the real work in servicing the http request. It integrates wcomponents into a
	 * servlet environment via a servlet specific helper class.
	 *
	 * @param request the http servlet request.
	 * @param response the http servlet response.
	 * @throws IOException an IO exception
	 * @throws ServletException a servlet exception
	 */
	protected void serviceInt(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// Check for resource request
		boolean continueProcess = ServletUtil.checkResourceRequest(request, response);
		if (!continueProcess) {
			return;
		}

		// Create a support class to coordinate the web request.
		WServletHelper helper = createServletHelper(request, response);

		// Get the interceptors that will be used to plug in features.
		InterceptorComponent interceptorChain = createInterceptorChain(request);

		// Get the WComponent that represents the application we are serving.
		WComponent ui = getUI(request);

		ServletUtil.processRequest(helper, ui, interceptorChain);
	}

	/**
	 * The interceptor component returned here is used to wrap the html fragment generated by the UI component.
	 *
	 * @param nativeRequest the native request being responded to
	 * @return the top-level interceptor in the chain.
	 */
	public InterceptorComponent createInterceptorChain(final Object nativeRequest) {
		return ServletUtil.createInterceptorChain((HttpServletRequest) nativeRequest);
	}

	/**
	 * Subclasses may override this method.
	 * <p>
	 * This method will be called internally once per request/response cycle.
	 * <p>
	 * It is up to the subclass to determine the life cycle of the returned component. Should the same instance be
	 * returned and shared by all sessions, or should there be a new instance per session.
	 * <p>
	 * A good way to always return the same instance is to use the UIRegistry. E.g.
	 *
	 * <pre>
	 * return UIRegistry.getInstance().getUI(KitchenSink.class.getName());
	 * </pre>
	 *
	 * @param httpServletRequest the servlet request being handled.
	 * @return the top-level WComponent for this servlet.
	 */
	public WComponent getUI(final Object httpServletRequest) {
		return noUI;
	}

	/**
	 * Create a support class to coordinate the web request.
	 *
	 * @param httpServletRequest the request being processed
	 * @param httpServletResponse the servlet response
	 *
	 * @return the servlet helper
	 */
	protected WServletHelper createServletHelper(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) {
		LOG.debug("Creating a new WServletHelper instance");
		WServletHelper helper = new WServletHelper(this, httpServletRequest, httpServletResponse);
		return helper;
	}

	/**
	 * Subclasses can override this method to add extra headers. They can do this by obtaining the top ui component's
	 * WHeaders object and adding headers to it.
	 *
	 * @param uic the current user's UIContext.
	 * @param ui the application UI.
	 */
	protected void addGenericHeaders(final UIContext uic, final WComponent ui) {
		// No headers are added by default
	}

	/**
	 * Called if a Throwable is caught by the top-level service method. By default we display an error and terminate the
	 * session.
	 *
	 * @param httpServletRequest the http servlet request.
	 * @param httpServletResponse the http servlet response.
	 * @param throwable the throwable
	 * @throws IOException an IO exception
	 * @throws ServletException a servlet exception
	 */
	protected void handleError(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse, final Throwable throwable)
			throws ServletException, IOException {
		HttpServletHelper helper = createServletHelper(httpServletRequest, httpServletResponse);
		ServletUtil.handleError(helper, throwable);
	}

	/**
	 * A servlet specific ContainerHelper.
	 * <p>
	 * Chiefly it handles the creation and storage of each users {@link UIContext} in the servlet session, and it maps
	 * servlet requests and responses to wcomponent requests and responses.
	 * </p>
	 * <p>
	 * An instance is created each request being processed.
	 * </p>
	 */
	public static class WServletHelper extends HttpServletHelper {

		/**
		 * @param servlet the servlet instance
		 * @param httpServletRequest the request being processed
		 * @param httpServletResponse the response being handled
		 */
		public WServletHelper(final WServlet servlet, final HttpServletRequest httpServletRequest,
				final HttpServletResponse httpServletResponse) {
			super(servlet, httpServletRequest, httpServletResponse);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void addGenericHeaders(final UIContext uic, final WComponent ui) {
			((WServlet) getServlet()).addGenericHeaders(uic, ui);
			super.addGenericHeaders(uic, ui);
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
			WServletEnvironment env = new WServletEnvironment(postPath, baseUrl, userAgent);
			if (request instanceof SubSessionHttpServletRequestWrapper) {
				env.setSubsessionId(((SubSessionHttpServletRequestWrapper) request).getSessionId());
			}

			return env;
		}

	}

	/**
	 * The WServlet implementation of the WEnvironment interface.
	 */
	public static class WServletEnvironment extends HttpServletEnvironment {

		/**
		 * @param postPath the post path
		 * @param baseUrl the base URL
		 * @param userAgent the user agent
		 */
		public WServletEnvironment(final String postPath, final String baseUrl,
				final String userAgent) {
			super(postPath, baseUrl, userAgent);
		}

		/**
		 * @param postPath the post path
		 * @param baseUrl the base URL
		 * @param userAgentInfo the user agent info
		 */
		public WServletEnvironment(final String postPath, final String baseUrl,
				final UserAgentInfo userAgentInfo) {
			super(postPath, baseUrl, userAgentInfo);
		}
	}

}
