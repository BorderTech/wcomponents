package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.Map;

/**
 * <p>
 * This wcomponent enables the display of arbitrary document content from a WComponent application. This component does
 * not by default display anything until the {@link #display()} method is called. It then causes the browser to access
 * the content through the WContentHelperServlet.</p>
 *
 * <p>
 * By default, the browser will display the content in a new window, but the {@link #setDisplayMode(DisplayMode)} method
 * can be used to display the content inline or prompt the user to save the content to a file.</p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WContent extends AbstractWComponent implements Targetable {

	/**
	 * An enumeration of how the content should be provided to the user.
	 */
	public enum DisplayMode {
		/**
		 * Indicates that the content should be displayed in another browser window.
		 */
		OPEN_NEW_WINDOW,
		/**
		 * Indicates that the content should replace the current page.
		 */
		DISPLAY_INLINE,
		/**
		 * Indicates that the browser should prompt the user to save the content to a file. Note that for this to work,
		 * the ContenetAccess MUST have its description set to the file name which the content should be saved as.
		 */
		PROMPT_TO_SAVE
	}

	/**
	 * This magic parameter is a work-around to the loading indicator becoming "stuck" in certain browsers. It is also
	 * used by the static resource handler to set the correct headers
	 *
	 */
	public static final String URL_CONTENT_MODE_PARAMETER_KEY = "wc_content";

	/**
	 * Supply this component with access to the document content to be displayed.
	 *
	 * @param contentAccess the ContentAccess which will supply the content.
	 */
	public void setContentAccess(final ContentAccess contentAccess) {
		getOrCreateComponentModel().contentAccess = contentAccess;
	}

	/**
	 * @return the ContentAccess which will supply the content.
	 */
	public ContentAccess getContentAccess() {
		return getComponentModel().contentAccess;
	}

	/**
	 * Next time this component is painted, it will output the appropriate script to open a new browser window to
	 * display the content.
	 */
	public void display() {
		getOrCreateComponentModel().displayRequested = true;
	}

	/**
	 * @return The height of the window containing the document content. Default is 600px.
	 */
	public String getHeight() {
		return getComponentModel().height;
	}

	/**
	 * @param height The height of the window containing the document content.
	 */
	public void setHeight(final String height) {
		getOrCreateComponentModel().height = height;
	}

	/**
	 * @return Returns True if the window is resizable.
	 */
	public boolean isResizable() {
		return getComponentModel().resizable;
	}

	/**
	 * @param resizable Should the window be resizable.
	 */
	public void setResizable(final boolean resizable) {
		getOrCreateComponentModel().resizable = resizable;
	}

	/**
	 * @return The width of the window containing the document content. Default is 800px.
	 */
	public String getWidth() {
		return getComponentModel().width;
	}

	/**
	 * @param width The width of the window containing the document content.
	 */
	public void setWidth(final String width) {
		getOrCreateComponentModel().width = width;
	}

	/**
	 * @return Returns the displayMode.
	 */
	public DisplayMode getDisplayMode() {
		return getComponentModel().displayMode;
	}

	/**
	 * Sets the content display mode. The default displayMode is {@link DisplayMode#OPEN_NEW_WINDOW}.
	 *
	 * @param displayMode The displayMode to set.
	 */
	public void setDisplayMode(final DisplayMode displayMode) {
		getOrCreateComponentModel().displayMode = displayMode == null ? DisplayMode.OPEN_NEW_WINDOW : displayMode;
	}

	/**
	 * @return the cacheKey
	 */
	public String getCacheKey() {
		return getComponentModel().cacheKey;
	}

	/**
	 * @param cacheKey the cacheKey to set.
	 */
	public void setCacheKey(final String cacheKey) {
		getOrCreateComponentModel().cacheKey = cacheKey;
	}

	/**
	 * Indicates whether the content should be displayed in the given context.
	 *
	 * @return true if the content should be displayed, otherwise false.
	 */
	public boolean isDisplayRequested() {
		return getComponentModel().displayRequested;
	}

	/**
	 * Resets the flag used to indicate that the content should be displayed.
	 */
	private void resetDisplayRequested() {
		getOrCreateComponentModel().displayRequested = false;
	}

	/**
	 * Implementation of the Targetable interface.
	 *
	 * @return the target id for this targetable.
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * Retrieves a dynamic URL which this targetable component can be accessed from.
	 *
	 * @return the URL to access this targetable component.
	 */
	public String getUrl() {
		ContentAccess content = getContentAccess();

		String mode = DisplayMode.PROMPT_TO_SAVE.equals(getDisplayMode()) ? "attach" : "inline";

		// Check for a "static" resource
		if (content instanceof InternalResource) {
			String url = ((InternalResource) content).getTargetUrl();
			// This magic parameter is a work-around to the loading indicator becoming
			// "stuck" in certain browsers.
			// It is also used by the static resource handler to set the correct headers
			url = url + "&" + URL_CONTENT_MODE_PARAMETER_KEY + "=" + mode;
			return url;
		}

		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		if (Util.empty(getCacheKey())) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, getCacheKey());
		}

		// This magic parameter is a work-around to the loading indicator becoming
		// "stuck" in certain browsers. It is only read by the theme.
		parameters.put(URL_CONTENT_MODE_PARAMETER_KEY, mode);

		// The targetable path needs to be configured for the portal environment.
		String url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 * <p>
	 * Override handleRequest in order to perform processing specific to this component.</p>
	 *
	 * <p>
	 * When the new browser window for the document content is opened, it will make another request to fetch the
	 * content. It is that situation we are trying to detect and handle here.</p>
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		resetDisplayRequested();

		// Has this component been targeted to return the document content?
		// Look in the request for the target parameter and see if it's for us.
		// The target parameter is encoded into the url in the javascript that
		// was rendered by this component to open the new browser window.
		String targ = request.getParameter(Environment.TARGET_ID);
		boolean contentReqested = targ != null && targ.equals(getTargetId());

		if (contentReqested) {
			ContentEscape escape = new ContentEscape(getContentAccess());
			escape.setCacheable(!Util.empty(getCacheKey()));
			escape.setDisplayInline(getDisplayMode() != DisplayMode.PROMPT_TO_SAVE);
			throw escape;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);
		// Clear display flag
		resetDisplayRequested();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		ContentAccess content = getContentAccess();
		return toString(content == null ? "null" : content.getClass().getSimpleName());
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new ComponentModel appropriate for this component.
	 *
	 * @return a new ContentModel.
	 */
	@Override
	protected ContentModel newComponentModel() {
		return new ContentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ContentModel getComponentModel() {
		return (ContentModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ContentModel getOrCreateComponentModel() {
		return (ContentModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WContent.
	 */
	public static class ContentModel extends ComponentModel {

		/**
		 * Content to display.
		 */
		private ContentAccess contentAccess;
		/**
		 * Display requested flag.
		 */
		private boolean displayRequested = false;
		/**
		 * Cache key for the content.
		 */
		private String cacheKey;

		/**
		 * The initial window width.
		 */
		private String width = "800px";

		/**
		 * The initial window hegiht.
		 */
		private String height = "600px";

		/**
		 * Whether the window should allow resizing by the user.
		 */
		private boolean resizable = true;

		/**
		 * Indicates how the content should be provided to the user.
		 */
		private DisplayMode displayMode = DisplayMode.OPEN_NEW_WINDOW;
	}
}
