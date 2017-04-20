package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Escape;
import com.github.bordertech.wcomponents.FatalErrorPage;
import com.github.bordertech.wcomponents.FatalErrorPageFactory;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextDelegate;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WebComponent;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.Factory;
import com.github.bordertech.wcomponents.util.SerializationUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class exists to enable sharing of features that are common between Servlet and Portlet handling of WComponents.
 * </p>
 * <p>
 * Instances of this support class are intended to be short lived. An instance of this class is intended to be created
 * to help service a single request and then be thrown away.
 * </p>
 *
 * @author Martin Shevchenko
 */
public abstract class AbstractContainerHelper {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractContainerHelper.class);

	/**
	 * The default session attribute key for where the UIContext is stored in the underlying session.
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	public static final String UICONTEXT_PORTLET_SESSION_KEY = "UIContext";

	/**
	 * The session attribute key for where propogated errors are stored in the underlying session.
	 */
	private static final String ACTION_ERROR_KEY = AbstractContainerHelper.class.getName() + ".action.error";

	/**
	 * Indicates whether the helper has been disposed. A disposed helper does not take part in action or render
	 * processing.
	 */
	private boolean disposed = false;

	/**
	 * This flag indicates if we are starting a new session. Starting a new session normally occurs because a new user
	 * is hitting the application for the first time. It can also be an existing user, but the request sent has
	 * indicated the desire to start a new session.
	 */
	private Boolean newConversation = null;

	private InterceptorComponent interceptor;

	private Request request = null;

	/**
	 * @param webComponent the web component
	 */
	public void setWebComponent(final WebComponent webComponent) {
		if (webComponent instanceof WComponent) {
			// No interceptor supplied but we need one to make things simple.
			// Dummy up a pass-through/do-nothing interceptor.
			this.interceptor = new InterceptorComponent();
			this.interceptor.setBackingComponent(webComponent);
		} else if (webComponent instanceof InterceptorComponent) {
			this.interceptor = (InterceptorComponent) webComponent;
		} else {
			throw new IllegalArgumentException("Unexpected extension of WebComponent supplied.");
		}
	}

	/**
	 * @return the interceptor
	 */
	public InterceptorComponent getInterceptor() {
		return interceptor;
	}

	/**
	 * @return the WComponent UI which will handle the request.
	 */
	public WComponent getUI() {
		WComponent ui = getInterceptor().getUI();
		return ui;
	}

	/**
	 * Prepare the user context for this request.
	 * <p>
	 * This must be called before processAction and render.
	 * </p>
	 *
	 * @return the user context
	 */
	public UIContext prepareUserContext() {
		UIContext uic = getUIContext();
		if (requestImpliesNew() || uic == null) {
			LOG.debug("Preparing a new session");
			newConversation = Boolean.TRUE;
			uic = createUIContext();
			setUIContext(uic);
		} else {
			newConversation = Boolean.FALSE;
			// In Development mode, simulate running in a cluster
			cycleUIContext();
		}
		return uic;
	}

	/**
	 * Support standard processing of the action phase of a request.
	 *
	 * @throws IOException if there is an IO error on writing a response.
	 */
	public void processAction() throws IOException {
		if (isDisposed()) {
			LOG.error("Skipping action phase. Attempt to reuse disposed ContainerHelper instance");
			return;
		}

		try {
			// Check user context has been prepared
			if (newConversation == null) {
				throw new IllegalStateException(
						"User context has not been prepared before the action phase");
			}

			prepareAction();

			UIContext uic = getUIContext();
			if (uic == null) {
				throw new IllegalStateException("No user context set for the action phase.");
			}

			UIContextHolder.pushContext(uic);

			// Make sure maps are cleared up
			uic.clearScratchMap();
			uic.clearRequestScratchMap();

			prepareRequest();

			clearPropogatedError();

			Request req = getRequest();
			getInterceptor().attachResponse(getResponse());
			getInterceptor().serviceRequest(req);

			if (req.isLogout()) {
				handleLogout();
				dispose();
			}
		} catch (ActionEscape esc) {
			LOG.debug("ActionEscape performed.");
			// Action escapes must be handled in the action phase and then
			// do nothing if they reach the render phase (which they will in
			// the servlet implementation)
			handleEscape(esc);
			dispose();
		} catch (Escape esc) {
			LOG.debug("Escape performed during action phase.");
			// We can't handle the escape until the render phase.
		} catch (Throwable t) {
			// We try not to let any exception propagate to container.
			String message = "Caught exception during action phase.";
			LOG.error(message, t);

			// We can't handle the error until the render phase.
			propogateError(t);
		} finally {
			UIContextHolder.reset();
		}
	}

	/**
	 * Support standard processing of the render phase of a request.
	 *
	 * @throws IOException IO Exception
	 */
	public void render() throws IOException {
		if (isDisposed()) {
			LOG.debug("Skipping render phase.");
			return;
		}

		try {
			// Check user context has been prepared
			if (newConversation == null) {
				throw new IllegalStateException(
						"User context has not been prepared before the render phase");
			}

			prepareRender();

			UIContext uic = getUIContext();
			if (uic == null) {
				throw new IllegalStateException("No user context set for the render phase.");
			}

			UIContextHolder.pushContext(uic);
			prepareRequest();

			// Handle errors from the action phase now.
			if (havePropogatedError()) {
				handleError(getPropogatedError());
				return;
			}

			WComponent uiComponent = getUI();

			if (uiComponent == null) {
				throw new SystemException("No UI Component exists.");
			}

			Environment environment = uiComponent.getEnvironment();

			if (environment == null) {
				throw new SystemException("No WEnvironment exists.");
			}

			getInterceptor().attachResponse(getResponse());
			getInterceptor().preparePaint(getRequest());

			String contentType = getUI().getHeaders().getContentType();
			Response response = getResponse();
			response.setContentType(contentType);

			addGenericHeaders(uic, getUI());

			PrintWriter writer = getPrintWriter();
			getInterceptor().paint(new WebXmlRenderContext(writer, uic.getLocale()));

			// The following only matters for a Portal context
			String title = uiComponent instanceof WApplication ? ((WApplication) uiComponent).
					getTitle() : null;

			if (title != null) {
				setTitle(title);
			}
		} catch (Escape esc) {
			LOG.debug("Escape performed during render phase.");
			handleEscape(esc);
		} catch (Throwable t) {
			// We try not to let any exception propagate to container.
			String message = "Caught exception during render phase.";
			LOG.error(message, t);
			handleError(t);
		} finally {
			UIContextHolder.reset();
			dispose();
		}
	}

	/**
	 * @return true if the current request signals a restart of the application. Subclasses may override.
	 */
	protected boolean requestImpliesNew() {
		return false;
	}

	/**
	 * Creates and initialises a new UIContext.
	 *
	 * @return a new UIContext.
	 */
	protected UIContext createUIContext() {
		// Create UIC
		UIContext uic = new UIContextImpl();
		uic.setUI(getUI());
		return uic;
	}

	/**
	 * Retrieves the UIContext from session. If the session does not yet exist or the UIContext is not yet created and
	 * saved, then this method returns null.
	 *
	 * @return the UIContext in the session, or null if it does not exist.
	 */
	protected abstract UIContext getUIContext();

	/**
	 * Saves the UIContext in session.
	 *
	 * @param uiContext the ui context to save.
	 */
	protected abstract void setUIContext(UIContext uiContext);

	/**
	 * Call this method to simulate what would happen if the UIContext was serialized due to clustering of servers.
	 */
	protected void cycleUIContext() {
		boolean cycleIt = ConfigurationProperties.getDeveloperClusterEmulation();

		if (cycleIt) {
			UIContext uic = getUIContext();

			if (uic instanceof UIContextWrap) {
				LOG.info("Cycling the UIContext to simulate clustering");
				((UIContextWrap) uic).cycle();
			}
		}
	}

	/**
	 * This class exists because we'd like to simulate clustering while still being able to use the UIContext as the
	 * synchronization lock when servicing a request.
	 */
	public static class UIContextWrap extends UIContextDelegate {

		/**
		 * Creates a UIContextWrap.
		 */
		public UIContextWrap() {
			super(new UIContextImpl());
		}

		/**
		 * Simulates moving between servers in a cluster by copying the UIContext backing using Serialization.
		 */
		public void cycle() {
			setBacking((UIContext) SerializationUtil.pipe(getBacking()));
		}
	}

	/**
	 * Prepare the session for the current request.
	 */
	protected void prepareRequest() {
		LOG.debug("Preparing for request by adding headers and environment to top wcomponent");

		// Configure the UIContext to handle this request.
		UIContext uiContext = getUIContext();

		// Add WEnvironment if not already done.
		// If the component is new, then it will not have a WEnvironment yet.
		Environment env;

		if (uiContext.isDummyEnvironment()) {
			env = createEnvironment();
			uiContext.setEnvironment(env);
		} else {
			env = uiContext.getEnvironment();
		}

		// Update the environment for the current phase of the request
		// processing.
		updateEnvironment(env);

		// Prepare an implementation of a wcomponent Request suitable to the
		// type of
		// container we are running in.
		if (request == null) {
			request = createRequest();
		}

		// Update the wcomponent Request for the current phase of the request
		// processing.
		updateRequest(request);
	}

	/**
	 * Marks the helper as disposed. A disposed helper does not take part in action or render processing.
	 */
	protected void dispose() {
		LOG.debug("Disposing ContainerHelper instance");

		if (request != null) {
			request = null;
		}

		disposed = true;
	}

	/**
	 * Indicates whether the helper has been disposed. A disposed helper does not take part in action or render
	 * processing.
	 *
	 * @return true if the helper is disposed, false otherwise.
	 */
	protected boolean isDisposed() {
		return disposed;
	}

	/**
	 * Creates an implementation of WEnvironment suitable for the type of container we are running in.
	 *
	 * @return a new WEnvironment instance.
	 */
	protected abstract Environment createEnvironment();

	/**
	 * Updates the environment for the current phase of the request processing.
	 *
	 * @param env the environment to update.
	 */
	protected abstract void updateEnvironment(Environment env);

	/**
	 * @return the request for this helper.
	 */
	protected Request getRequest() {
		return request;
	}

	/**
	 * Creates an implementation of WComponent Request suitable to the type of container we are running in.
	 *
	 * @return a new Request.
	 */
	protected abstract Request createRequest();

	/**
	 * Updates the wcomponent Request for the current phase of the outer containers request processing.
	 *
	 * @param request the request to update.
	 */
	protected abstract void updateRequest(Request request);

	/**
	 * Set the title for the application.
	 *
	 * @param title the title to set.
	 */
	protected abstract void setTitle(String title);

	/**
	 * Indicates whether this is a new conversation (session).
	 *
	 * @return true if a new conversation, false if continuing.
	 */
	protected boolean isNewConversation() {
		if (newConversation == null) {
			throw new IllegalStateException("Don't yet know the status of the conversation.");
		}

		return newConversation.booleanValue();
	}

	/**
	 * Indicates whether this is a continuing conversation (session).
	 *
	 * @return true if a continuing conversation, false if new.
	 */
	protected boolean isContinuingConversation() {
		return !isNewConversation();
	}

	/**
	 * The application can ask the Container to "log out" by setting the logout attribute on the Request. This method is
	 * called in response to that.
	 * <p>
	 * It invalidates the session and redirects the browser to the root of the web application.
	 * </p>
	 */
	protected void handleLogout() {
		invalidateSession();
		redirectForLogout();
	}

	/**
	 * @return the response for this helper.
	 */
	protected abstract Response getResponse();

	/**
	 * Invalidates the underlying session.
	 */
	protected abstract void invalidateSession();

	/**
	 * Redirects the client to a URL after the log-out process is complete. The URL is specified by the configuration
	 * parameter <code>bordertech.wcomponents.logout.url</code>. Note that this method is called after
	 * {@link #invalidateSession()}, so there may not be any session information available.
	 */
	protected abstract void redirectForLogout();

	/**
	 * Subclasses can override this method to add extra headers. They can do this by obtaining the top ui component's
	 * WHeaders object and adding headers to it. Adding generic headers makes sense for servlets but maybe not portlets.
	 *
	 * @param uic the current user context
	 * @param ui the WComponent UI which is handling the request.
	 */
	protected void addGenericHeaders(final UIContext uic, final WComponent ui) {
	}

	/**
	 * Retrieves the PrintWriter which is used to send data back to the client.
	 *
	 * @return the PrintWriter for this helper.
	 * @throws IOException if there is an error obtaining the PrintWriter.
	 */
	protected abstract PrintWriter getPrintWriter() throws IOException;

	// === Escape handling ===
	/**
	 * @param esc the escape to process
	 * @throws IOException IO Exception
	 */
	protected void handleEscape(final Escape esc) throws IOException {
		LOG.debug("Start handleEscape...");

		esc.setRequest(getRequest());
		esc.setResponse(getResponse());

		esc.escape();

		LOG.debug("End handleEscape");
	}

	// === Error handling ===
	/**
	 * Propogates an error from the action phase to the render phase.
	 *
	 * @param error the error to propogate.
	 */
	private void propogateError(final Throwable error) {
		// Unhandled runtime exceptions from action phase
		// must be remembered for subsequent renders
		if (getRequest() == null) {
			LOG.error("Unable to remember error from action phase beyond this request");
		} else {
			LOG.debug("Remembering error from action phase");
			getRequest().setAppSessionAttribute(ACTION_ERROR_KEY, error);
		}
	}

	/**
	 * Indicates whether there is an error which has been propogated from the action to the render phase.
	 *
	 * @return true if there is a propogated escape, false otherwise.
	 */
	private boolean havePropogatedError() {
		Request req = getRequest();
		return req != null && req.getAppSessionAttribute(ACTION_ERROR_KEY) != null;
	}

	/**
	 * Retrieves an error which has been propogated from the action to the render phase.
	 *
	 * @return the propogated escape, or null if there isn't one.
	 */
	private Throwable getPropogatedError() {
		Request req = getRequest();

		if (req != null) {
			return (Throwable) req.getAppSessionAttribute(ACTION_ERROR_KEY);
		}

		return null;
	}

	/**
	 * Clear the errors.
	 */
	private void clearPropogatedError() {
		Request req = getRequest();

		if (req.getAppSessionAttribute(ACTION_ERROR_KEY) != null) {
			LOG.debug("Clearing error remembered from previous action phase");
			req.setAppSessionAttribute(ACTION_ERROR_KEY, null);
		}
	}

	/**
	 * Last resort error handling.
	 *
	 * @param error the error to handle.
	 * @throws IOException if there is an error writing the error HTML.
	 */
	public void handleError(final Throwable error) throws IOException {

		LOG.debug("Start handleError...");

		// Should the session be removed upon error?
		boolean terminate = ConfigurationProperties.getTerminateSessionOnError();
		// If we are unfriendly, terminate the session
		if (terminate) {
			invalidateSession();
		}

		// Are we in developer friendly error mode?
		boolean friendly = ConfigurationProperties.getDeveloperErrorHandling();

		FatalErrorPageFactory factory = Factory.newInstance(FatalErrorPageFactory.class);
		WComponent errorPage = factory.createErrorPage(friendly, error);

		String html = renderErrorPageToHTML(errorPage);

		// Setup the response
		Response response = getResponse();
		response.setContentType(WebUtilities.CONTENT_TYPE_HTML);

		// Make sure not cached
		getResponse().setHeader("Cache-Control", CacheType.NO_CACHE.getSettings());
		getResponse().setHeader("Pragma", "no-cache");
		getResponse().setHeader("Expires", "-1");

		getPrintWriter().println(html);

		LOG.debug("End handleError");
	}

	/**
	 * Render the error page component to HTML.
	 *
	 * @param errorPage the error page component
	 * @return the error page as HTML
	 */
	protected String renderErrorPageToHTML(final WComponent errorPage) {

		// Check if using the default error page
		boolean defaultErrorPage = errorPage instanceof FatalErrorPage;

		String html = null;

		// If not default implementation of error page, Transform error page to HTML
		if (!defaultErrorPage) {
			// Set UIC and Environment (Needed for Theme Paths)
			UIContext uic = new UIContextImpl();
			uic.setEnvironment(createEnvironment());
			UIContextHolder.pushContext(uic);
			try {
				html = WebUtilities.renderWithTransformToHTML(errorPage);
			} catch (Exception e) {
				LOG.warn("Could not transform error page.", e);
			} finally {
				UIContextHolder.popContext();
			}
		}

		// Not transformed. So just render.
		if (html == null) {
			UIContextHolder.pushContext(new UIContextImpl());
			try {
				html = WebUtilities.render(errorPage);
			} catch (Exception e) {
				LOG.warn("Could not render error page.", e);
				html = "System error occurred but could not render error page.";
			} finally {
				UIContextHolder.popContext();
			}
		}

		return html;
	}

	/**
	 * Perform extra steps before processing action.
	 */
	protected void prepareAction() {
		// Do Nothing
	}

	/**
	 * Perform extra steps before rendering.
	 */
	protected void prepareRender() {
		// Do Nothing
	}
}
