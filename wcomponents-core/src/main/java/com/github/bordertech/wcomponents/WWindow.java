package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * This component enables a pop up browser window with interactive wcomponent content. Be warned that pop up windows can
 * cause workflow headaches as you can't control where the user goes. Eg. They may not close a pop up window before
 * going back to the parent window. Later on they may choose to continue working in the pop up window, breaking the
 * intended flow of the application.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WWindow extends AbstractWComponent implements Container {

	/**
	 * Request parameter key for a wwindow.
	 */
	public static final String WWINDOW_REQUEST_PARAM_KEY = "wc_wwindow";

	/**
	 * This is the "normal" state for the Window component when it's being processed as part of the parent window.
	 */
	public static final int INACTIVE_STATE = 0;

	/**
	 * The component is in this state when a request has been made to display the window. The necessary mark-up to open
	 * the window will be rendered in this mode.
	 */
	public static final int DISPLAY_STATE = 1;

	/**
	 * This state is when the window is open and the initial render of the content is complete.
	 */
	public static final int ACTIVE_STATE = 2;

	/**
	 * Holder naming context.
	 */
	private final WNamingContext holderNamingContext = new WNamingContext("w") {
		@Override
		public String getNamingContextId() {
			// Build Id
			StringBuffer id = new StringBuffer();
			id.append(WWindow.this.getId());
			id.append(ID_CONTEXT_SEPERATOR);
			id.append(getIdName());
			return id.toString();
		}
	};

	/**
	 * The content holder exists to keep the content hidden from normal requests, yet still have the content attached to
	 * the wcomponent tree. Being part of the tree enables embedded targetables and other components to be found.
	 */
	private final WInvisibleContainer holder = new WInvisibleContainer();

	/**
	 * Creates a new Window.
	 */
	public WWindow() {
		add(holderNamingContext);
		holderNamingContext.add(holder);
	}

	/**
	 * Creates a new Window containing the specified content.
	 *
	 * @param content the window content.
	 */
	public WWindow(final WComponent content) {
		this();
		setContent(content);
	}

	/**
	 * Set the WComponent that will handle the content for this pop up window.
	 *
	 * @param content the window content.
	 */
	public void setContent(final WComponent content) {
		WindowModel model = getOrCreateComponentModel();

		// If the previous content had been wrapped, then remove it from the wrapping WApplication.
		if (model.wrappedContent != null && model.wrappedContent != model.content) {
			model.wrappedContent.removeAll();
		}

		model.content = content;

		// Wrap content in a WApplication
		if (content instanceof WApplication) {
			model.wrappedContent = (WApplication) content;
		} else {
			model.wrappedContent = new WApplication();
			model.wrappedContent.add(content);
		}

		// There should only be one content.
		holder.removeAll();
		holder.add(model.wrappedContent);
	}

	/**
	 * @return the component which is the content for this window.
	 */
	public WComponent getContent() {
		return getComponentModel().content;
	}

	/**
	 * @return the window title.
	 */
	public String getTitle() {
		return getComponentModel().title;
	}

	/**
	 * Sets the window title.
	 *
	 * @param title The title to set.
	 */
	public void setTitle(final String title) {
		getOrCreateComponentModel().title = title;
	}

	/**
	 * Signals that the pop up window should be opened in the given context.
	 */
	public void display() {
		setState(DISPLAY_STATE);
	}

	/**
	 * @return The height of the window. Default is 600px.
	 */
	public int getHeight() {
		return getComponentModel().height;
	}

	/**
	 * Sets the window height.
	 *
	 * @param height The height of the window.
	 */
	public void setHeight(final int height) {
		getOrCreateComponentModel().height = height;
	}

	/**
	 * @return The width of the window. Default is 800px.
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * Sets the window width.
	 *
	 * @param width The width of the window.
	 */
	public void setWidth(final int width) {
		getOrCreateComponentModel().width = width;
	}

	/**
	 * @return the y-coordinate of the window.
	 */
	public int getTop() {
		return getComponentModel().top;
	}

	/**
	 * Sets the y-coordinate of the window.
	 *
	 * @param top The y-coordinate of the window, or -1 for default.
	 */
	public void setTop(final int top) {
		getOrCreateComponentModel().top = top;
	}

	/**
	 * @return the x-coordinate of the window.
	 */
	public int getLeft() {
		return getComponentModel().left;
	}

	/**
	 * Sets the x-coordinate of the window.
	 *
	 * @param left The x-coordinate of the window, or -1 for default.
	 */
	public void setLeft(final int left) {
		getOrCreateComponentModel().left = left;
	}

	/**
	 * @return true the browser menubar should be shown.
	 */
	public boolean isShowMenuBar() {
		return getComponentModel().showMenuBar;
	}

	/**
	 * Sets whether the browser menubar should be shown. It is hidden by default.
	 *
	 * @param showMenuBar The showMenuBar to set.
	 */
	public void setShowMenuBar(final boolean showMenuBar) {
		getOrCreateComponentModel().showMenuBar = showMenuBar;
	}

	/**
	 * @return true if the browser toolbar should be shown.
	 */
	public boolean isShowToolbar() {
		return getComponentModel().showToolbar;
	}

	/**
	 * Sets whether the browser toolbar should be shown. It is hidden by default.
	 *
	 * @param showToolbar The showToolbar to set.
	 */
	public void setShowToolbar(final boolean showToolbar) {
		getOrCreateComponentModel().showToolbar = showToolbar;
	}

	/**
	 * @return true if the browser location bar should be shown.
	 */
	public boolean isShowLocation() {
		return getComponentModel().showLocation;
	}

	/**
	 * Sets whether the browser location bar should be shown. It is hidden by default.
	 *
	 * @param showLocation The showLocation to set.
	 */
	public void setShowLocation(final boolean showLocation) {
		getOrCreateComponentModel().showLocation = showLocation;
	}

	/**
	 * @return true if the browser status bar should be shown.
	 */
	public boolean isShowStatus() {
		return getComponentModel().showStatus;
	}

	/**
	 * Sets whether the browser status bar should be shown. It is hidden by default.
	 *
	 * @param showStatus The showStatus to set.
	 */
	public void setShowStatus(final boolean showStatus) {
		getOrCreateComponentModel().showStatus = showStatus;
	}

	/**
	 * @return true if the window is resizable.
	 */
	public boolean isResizable() {
		return getComponentModel().resizable;
	}

	/**
	 * Sets whether the window is resizable.
	 *
	 * @param resizable true if the window should be resizable, false if not.
	 */
	public void setResizable(final boolean resizable) {
		getOrCreateComponentModel().resizable = resizable;
	}

	/**
	 * @return true if the window is scrollable.
	 */
	public boolean isScrollable() {
		return getComponentModel().scrollbars;
	}

	/**
	 * Sets whether the window should have a scroll bar.
	 *
	 * @param scrollable true if the window should have a scroll bar, false if not.
	 */
	public void setScrollable(final boolean scrollable) {
		getOrCreateComponentModel().scrollbars = scrollable;
	}

	/**
	 * Retrieves the current state of the window.
	 *
	 * @return the current state of this window.
	 */
	public int getState() {
		return getComponentModel().state;
	}

	/**
	 * Sets the current state of this component.
	 *
	 * @param state the window state to set.
	 */
	protected void setState(final int state) {
		getOrCreateComponentModel().state = state;
	}

	/**
	 * Returns a dynamic URL that this wwindow component can be accessed from.
	 *
	 * @return the URL to access this wwindow component.
	 */
	public String getUrl() {
		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(WWINDOW_REQUEST_PARAM_KEY, getId());
		// Override the step count with WWindow step
		parameters.put(Environment.STEP_VARIABLE, String.valueOf(getStep()));

		String url = env.getWServletPath();

		return WebUtilities.getPath(url, parameters, true);
	}

	// -------------------------------------------------------------
	// Action and Event Handling
	// -------------------------------------------------------------
	/**
	 * Override handleRequest in order to perform processing specific to this component.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		super.handleRequest(request);

		// Check if window in request
		boolean targeted = isPresent(request);
		setTargeted(targeted);

		if (getState() == ACTIVE_STATE && isTargeted()) {
			getComponentModel().wrappedContent.serviceRequest(request);
		}
	}

	/**
	 * @param request the request being processed
	 * @return true if window in request
	 */
	private boolean isPresent(final Request request) {
		String target = request.getParameter(WWINDOW_REQUEST_PARAM_KEY);
		boolean targeted = target != null && target.equals(getId());
		return targeted;
	}

	/**
	 * When the window is targetted, we need to run the "laters". If we don't do this, they will not run because a
	 * targetted request bypasses the root component that would normally have run them.
	 */
	@Override
	protected void invokeLaters() {
		if (getState() == ACTIVE_STATE && isTargeted()) {
			UIContextHolder.getCurrent().doInvokeLaters();
		} else {
			super.invokeLaters();
		}
	}

	/**
	 * Override preparePaintComponent to clear the scratch map before the window content is being painted.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		// Check if window in request (might not have gone through handle request, eg Step error)
		boolean targeted = isPresent(request);
		setTargeted(targeted);

		if (getState() == ACTIVE_STATE && isTargeted()) {
			getComponentModel().wrappedContent.preparePaint(request);
		}
	}

	/**
	 * Override paintComponent in order to paint the window or its content, depending on the window state.
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	@Override
	protected void paintComponent(final RenderContext renderContext) {
		if (getState() == DISPLAY_STATE) {
			setState(ACTIVE_STATE);
			showWindow(renderContext);
		} else if (getState() == ACTIVE_STATE && isTargeted()) {
			getComponentModel().wrappedContent.paint(renderContext);
		}
	}

	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);
		setTargeted(false);
	}

	/**
	 * Emits the mark-up which pops up the window.
	 *
	 * @param renderContext the RenderContext to send the output to.
	 */
	protected void showWindow(final RenderContext renderContext) {
		// Get current step
		int current = UIContextHolder.getCurrent().getEnvironment().getStep();
		// Get window current step (may have already been launched)
		int window = getStep();
		// Combine step counts to make previous window (if still open) invalid
		setStep(window + current);

		// TODO: This should be in a renderer, not included in this class
		if (renderContext instanceof WebXmlRenderContext) {
			PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();

			writer.print("\n<ui:popup");
			writer.print(" url=\"" + WebUtilities.encode(getUrl()) + '"');
			writer.print(" width=\"" + getWidth() + '"');
			writer.print(" height=\"" + getHeight() + '"');

			if (isResizable()) {
				writer.print(" resizable=\"true\"");
			}

			if (isScrollable()) {
				writer.print(" showScrollbars=\"true\"");
			}

			if (isShowMenuBar()) {
				writer.print(" showMenubar=\"true\"");
			}

			if (isShowToolbar()) {
				writer.print(" showToolbar=\"true\"");
			}

			if (isShowLocation()) {
				writer.print(" showLocation=\"true\"");
			}

			if (isShowStatus()) {
				writer.print(" showStatus=\"true\"");
			}

			if (getTop() >= 0) {
				writer.print(" top=\"" + getTop() + '\'');
			}

			if (getLeft() >= 0) {
				writer.print(" left=\"" + getLeft() + '\'');
			}

			writer.println("/>");
		}
	}

	/**
	 * @return the current step counter.
	 */
	protected boolean isTargeted() {
		return getComponentModel().targeted;
	}

	/**
	 * @param targeted true if targeted
	 */
	protected void setTargeted(final boolean targeted) {
		getOrCreateComponentModel().targeted = targeted;
	}

	/**
	 * Retrieves the current step counter. This method should only ever be used by internal code.
	 *
	 * @return the current step counter.
	 */
	public int getStep() {
		return getComponentModel().step;
	}

	/**
	 * Sets the current step counter. This method should only ever be used by internal code.
	 *
	 * @param step the step to set.
	 */
	public void setStep(final int step) {
		getOrCreateComponentModel().step = step;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getTitle();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, 1);
	}

	// ---------------------------------------------------------------------------
	// Extrinsic state management
	// ---------------------------------------------------------------------------
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new WindowModel.
	 */
	@Override
	protected WindowModel newComponentModel() {
		return new WindowModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected WindowModel getComponentModel() {
		return (WindowModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected WindowModel getOrCreateComponentModel() {
		return (WindowModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a window.
	 */
	public static class WindowModel extends ComponentModel {

		/**
		 * The state of the window controls the behaviour during normal WComponent processing.
		 */
		private int state = INACTIVE_STATE;

		// Attributes of window.
		/**
		 * The window title.
		 */
		private String title;
		/**
		 * The window width, in HTML units.
		 */
		private int width = 800;
		/**
		 * The window height, in HTML units.
		 */
		private int height = 600;
		/**
		 * The y co-ordinate of the window, in pixels.
		 */
		private int top = -1;
		/**
		 * The x co-ordinate of the window, in pixels.
		 */
		private int left = -1;
		/**
		 * Indicates whether the window should be resizable.
		 */
		private boolean resizable = true;
		/**
		 * Indicates whether the window should have scrollbars.
		 */
		private boolean scrollbars = false;
		/**
		 * Indicates whether the window should display the menu bar.
		 */
		private boolean showMenuBar = false;
		/**
		 * Indicates whether the window should display the tool bar.
		 */
		private boolean showToolbar = false;
		/**
		 * Indicates whether the window should display the location bar.
		 */
		private boolean showLocation = false;
		/**
		 * Indicates whether the window should display the status bar.
		 */
		private boolean showStatus = false;

		/**
		 * The content to be displayed in the window.
		 */
		private WComponent content;

		/**
		 * The wrapped content.
		 */
		private WApplication wrappedContent;

		/**
		 * The expected step counter. This is kept separate from the Environment step counter to allow separate
		 * processing to occur in a WWindow.
		 */
		private int step;

		/**
		 * Flag if targeted.
		 */
		private boolean targeted;
	}
}
