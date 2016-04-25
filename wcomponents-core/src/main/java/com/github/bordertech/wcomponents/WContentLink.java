package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WContent.DisplayMode;
import com.github.bordertech.wcomponents.WLink.WindowAttributes;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * <p>
 * WContentLink is a convenience class to configure a {@link WLink} to display content, such as a pdf, that is rendered
 * by {@link WContent}. By default, the content is displayed in a new window.</p>
 *
 * <p>
 * Be warned that this link does not post the entire form, as the client will opens the content directly via a "get"
 * request. For situations where it is important to post the form, use a combination of {@link WButton} and
 * {@link WContent}.
 * </p>
 * <p>
 * WContentLink provides a number of defaults to minimise configuration:-
 * </p>
 * <dl>
 * <dt>Window Height</dt>
 * <dd>600px</dd>
 * <dt>Window Width</dt>
 * <dd>800px</dd>
 * <dt>Resizable</dt>
 * <dd>true</dd>
 * </dl>
 * <p>
 * Below is an example of the code required to use WContentLink:-
 * </p>
 * <blockquote>
 *
 * <pre>
 * private final WContentLink contentLink = new WContentLink(&quot;link to content&quot;);
 *
 * public SampleConstructor()
 * {
 *     ....
 *     add(contentLink);
 *     ....
 * }
 *
 * protected void preparePaintComponent(Request request)
 * {
 *     ....
 *     contentLink.setContentAccess(examplePdfContent);
 *     ....
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * WContentLink provides basic configuration options, but if more advanced configuration is required, then a
 * {@link WLink} component and {@link WContent} component should be used instead. Below is an example of the code
 * required to use a WLink in combination with WContent:-
 * </p>
 * <blockquote>
 *
 * <pre>
 * private final WLink link = new WLink();
 * private final WContent content = new WContent();
 *
 * public SampleConstructor()
 * {
 *     ....
 *     link.setText(&quot;link to content&quot;);
 *     link.setOpenNewWindow(true);
 *     link.setTargetWindowName(&quot;content&quot;);
 *     add(link);
 *     add(content);
 *     ....
 * }
 *
 * protected void preparePaintComponent(Request request)
 * {
 *     ....
 *     link.setUrl(content.getUrl());
 *     content.setContentAccess(examplePdfContent);
 *     ....
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WContentLink extends AbstractContainer implements Disableable {

	/**
	 * Default height for the content window.
	 */
	private static final int DEFAULT_HEIGHT = 600;

	/**
	 * Default width for the content window.
	 */
	private static final int DEFAULT_WIDTH = 800;

	/**
	 * Default name for the content window.
	 */
	private static final String DEFAULT_NAME = "window";

	/**
	 * The content to be displayed in the window.
	 */
	private final WContent content = new WContent();

	/**
	 * The link used to open the window containing the content.
	 */
	private final WLink link = new WLink() {
		@Override
		public String getUrl() {
			return content.getUrl();
		}
	};

	/**
	 * Construct the WContentLink.
	 */
	public WContentLink() {
		WindowAttributes attr = new WindowAttributes();
		attr.setHeight(DEFAULT_HEIGHT);
		attr.setWidth(DEFAULT_WIDTH);
		attr.setWindowName(DEFAULT_NAME);
		attr.setResizable(true);
		link.setWindowAttrs(attr);
		link.setOpenNewWindow(true);
		add(link);
		add(content);
	}

	/**
	 * Creates a WContentLink with the given text.
	 *
	 * @param aText the link text.
	 */
	public WContentLink(final String aText) {
		this();
		link.setText(aText);
	}

	/**
	 * Creates a WContentLink with the given text and access key.
	 *
	 * @param aText the link text.
	 * @param accessKey the link access key.
	 */
	public WContentLink(final String aText, final char accessKey) {
		this(aText);
		link.setAccessKey(accessKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidden() {
		return link.isHidden();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHidden(final boolean flag) {
		link.setHidden(flag);
	}

	/**
	 * Indicates whether this link is disabled in the given context.
	 *
	 * @return true if this link is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return link.isDisabled();
	}

	/**
	 * Sets whether this link is disabled.
	 *
	 * @param disabled if true, this link is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		link.setDisabled(disabled);
	}

	/**
	 * @return the text to be displayed on the link.
	 */
	public String getText() {
		return link.getText();
	}

	/**
	 * Sets the text displayed on the link.
	 *
	 * @param text the link text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setText(final String text, final Serializable... args) {
		link.setText(text, args);
	}

	/**
	 * Indicates whether this link should render as a button.
	 *
	 * @return true if this link should render as a button, false for a hyperlink.
	 */
	public boolean isRenderAsButton() {
		return link.isRenderAsButton();
	}

	/**
	 * Sets whether this link should render as a button.
	 *
	 * @param renderAsButton true if this link should render as a button, false for a hyperlink.
	 */
	public void setRenderAsButton(final boolean renderAsButton) {
		link.setRenderAsButton(renderAsButton);
	}

	/**
	 * Retrieves the attributes for new windows which are opened.
	 * <p>
	 * To change attributes for individual users, set a new {@link WindowAttributes} object for each user.
	 * </p>
	 *
	 * @return the attributes for new windows.
	 */
	public WindowAttributes getWindowAttrs() {
		return link.getWindowAttrs();
	}

	/**
	 * @param windowAttrs the attributes for new windows which are opened.
	 */
	public void setWindowAttrs(final WindowAttributes windowAttrs) {
		link.setWindowAttrs(windowAttrs);
	}

	/**
	 * @return The height of the window containing the content.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public int getHeight() {
		return getWindowAttrs().getHeight();
	}

	/**
	 * @param height The height of the window containing the content.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public void setHeight(final int height) {
		getWindowAttrs().setHeight(height);
	}

	/**
	 * @return Returns true if the window is resizable.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public boolean isResizable() {
		return getWindowAttrs().isResizable();
	}

	/**
	 * @param resizable Should the window be resizable.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public void setResizable(final boolean resizable) {
		getWindowAttrs().setResizable(resizable);
	}

	/**
	 * @return The width of the window containing the content.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public int getWidth() {
		return getWindowAttrs().getWidth();
	}

	/**
	 * @param width The width of the window containing the document content.
	 * @deprecated use {@link #getWindowAttrs()} to access window attributes.
	 */
	@Deprecated
	public void setWidth(final int width) {
		getWindowAttrs().setWidth(width);
	}

	/**
	 * Supply this component with access to the document content to be displayed.
	 *
	 * @param contentAccess the ContentAccess which will supply the content.
	 */
	public void setContentAccess(final ContentAccess contentAccess) {
		content.setContentAccess(contentAccess);
	}

	/**
	 * @return the ContentAccess which will supply the content.
	 */
	public ContentAccess getContentAccess() {
		return content.getContentAccess();
	}

	/**
	 * @return the cacheKey
	 */
	public String getCacheKey() {
		return content.getCacheKey();
	}

	/**
	 * @param cacheKey the cacheKey to set.
	 */
	public void setCacheKey(final String cacheKey) {
		content.setCacheKey(cacheKey);
	}

	/**
	 * Sets the content display mode. Note that the window attributes will be ignored if the mode is changed to
	 * something other than {@link WContent.DisplayMode#OPEN_NEW_WINDOW}.
	 *
	 * @param displayMode the content display mode to set.
	 */
	public void setDisplayMode(final DisplayMode displayMode) {
		content.setDisplayMode(displayMode);
		link.setOpenNewWindow(DisplayMode.OPEN_NEW_WINDOW.equals(content.getDisplayMode()));
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text, -1, -1);
	}
}
