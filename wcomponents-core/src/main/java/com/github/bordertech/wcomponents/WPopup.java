package com.github.bordertech.wcomponents;

/**
 * This component enables a pop up browser window to an arbitrary location. Pop-ups are initially invisible, to display
 * a pop-up, call {@link #setVisible(boolean)}. This will set the pop-up visible for the render cycle only.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WPopup extends AbstractWComponent {

	/**
	 * Creates a new Window with no URL.
	 */
	public WPopup() {
		// Change default visibility to false.
		getComponentModel().setFlags(ComponentModel.FLAGS_DEFAULT & ~ComponentModel.VISIBLE_FLAG);
	}

	/**
	 * Creates a new popup containing the specified content.
	 *
	 * @param url the popup url.
	 */
	public WPopup(final String url) {
		this();
		getComponentModel().url = url;
	}

	/**
	 * @return The height of the popup window. Default is 600px.
	 */
	public int getHeight() {
		return getComponentModel().height;
	}

	/**
	 * Sets the window height.
	 *
	 * @param height The height of the popup window.
	 */
	public void setHeight(final int height) {
		getOrCreateComponentModel().height = height;
	}

	/**
	 * @return The width of the popup window. Default is 800px.
	 */
	public int getWidth() {
		return getComponentModel().width;
	}

	/**
	 * Sets the popup window width.
	 *
	 * @param width The width of the popup window.
	 */
	public void setWidth(final int width) {
		getOrCreateComponentModel().width = width;
	}

	/**
	 * @return true if the popup window is resizable.
	 */
	public boolean isResizable() {
		return getComponentModel().resizable;
	}

	/**
	 * Sets whether the popup window is resizable.
	 *
	 * @param resizable true if the popup window should be resizable, false if not.
	 */
	public void setResizable(final boolean resizable) {
		getOrCreateComponentModel().resizable = resizable;
	}

	/**
	 * @return true if the popup window is scrollable.
	 */
	public boolean isScrollable() {
		return getComponentModel().scrollbars;
	}

	/**
	 * Sets whether the popup window should have a scroll bar.
	 *
	 * @param scrollable true if the popup window should have a scroll bar, false if not.
	 */
	public void setScrollable(final boolean scrollable) {
		getOrCreateComponentModel().scrollbars = scrollable;
	}

	/**
	 * @return the URL.
	 */
	public String getUrl() {
		return getComponentModel().url;
	}

	/**
	 * Sets the URL.
	 *
	 * @param url the URL to set.
	 */
	public void setUrl(final String url) {
		getOrCreateComponentModel().url = url;
	}

	/**
	 * Retrieves the target window name.
	 *
	 * @return the target window name.
	 */
	public String getTargetWindow() {
		return getComponentModel().targetWindow;
	}

	/**
	 * Sets the target window name.
	 *
	 * @param targetWindow the target window name.
	 */
	public void setTargetWindow(final String targetWindow) {
		getOrCreateComponentModel().targetWindow = targetWindow;
	}

	/**
	 * Make the popup not visible after painting.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);
		setVisible(false);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getUrl();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text);
	}

	// ---------------------------------------------------------------------------
	// Extrinsic state management
	// ---------------------------------------------------------------------------
	/**
	 * Stores the WPopup state information.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class PopupModel extends ComponentModel {

		/**
		 * The pop-up window width.
		 */
		private int width = -1;

		/**
		 * The pop-up window width.
		 */
		private int height = -1;

		/**
		 * Indicates whether the popup windows should allow resizing.
		 */
		private boolean resizable = true;

		/**
		 * Indicates whether the popup windows should allow scrolling.
		 */
		private boolean scrollbars = false;

		/**
		 * The pop-up window URL.
		 */
		private String url;

		/**
		 * The name of the target window to open.
		 */
		private String targetWindow;

	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new PopupModel.
	 */
	@Override
	protected PopupModel newComponentModel() {
		return new PopupModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PopupModel getComponentModel() {
		return (PopupModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PopupModel getOrCreateComponentModel() {
		return (PopupModel) super.getOrCreateComponentModel();
	}
}
