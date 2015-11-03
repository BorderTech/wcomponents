package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Note that WLink is different to WButton rendered as a link because WLink will not post the form, and does not support
 * Actions. By default it opens up a new browser window and shows the given url. If the new window attributes needs to
 * be specified then a builder can be used <code>
 * WLink wLink = new WLink.Builder("WLink using builder and with attrs", "http://bordertech.github.io/").
 * window("myWcomponentsWindow").width(200).height(200).scrollbars(true).build();
 * </code> Caution must be taken when specifying attributes so that window name is not same for two links on the same
 * page. This can occur when window name is not provided with attributes causing same default window name.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class WLink extends WBeanComponent implements Container, Disableable, AjaxTarget,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WLink.class);

	/**
	 * A holder for a link image, if set.
	 */
	private final LinkImage linkImage = new LinkImage(this);

	/**
	 * This is used to control the position of the image on the link.
	 */
	public enum ImagePosition {
		/**
		 * Image is in the North position.
		 */
		NORTH,
		/**
		 * Image is in the East position.
		 */
		EAST,
		/**
		 * Image is in the South position.
		 */
		SOUTH,
		/**
		 * Image is in the West position.
		 */
		WEST
	};

	/**
	 * Creates a WLink.
	 */
	public WLink() {
		add(linkImage);
	}

	/**
	 * Creates a WLink with the given text and url.
	 *
	 * @param text the link test to display.
	 * @param url the link url.
	 */
	public WLink(final String text, final String url) {
		this(text, url, null);
	}

	/**
	 * Creates a WLink with the given attributes.
	 *
	 * @param text the link test to display.
	 * @param url the link url.
	 * @param windowAttrs the window attributes.
	 */
	private WLink(final String text, final String url, final WindowAttributes windowAttrs) {
		this();
		setText(text);
		setUrl(url);
		setWindowAttrs(windowAttrs);
	}

	/**
	 * The name of the target popup window. Has no meaning when the link is not a popup.
	 *
	 * @return the target window name.
	 */
	public String getTargetWindowName() {
		return getComponentModel().targetWindowName;
	}

	/**
	 * The name of the target popup window. Has no meaning when the link is not a popup.
	 *
	 * @param targetWindowName The targetWindowName to set.
	 */
	public void setTargetWindowName(final String targetWindowName) {
		getOrCreateComponentModel().targetWindowName = targetWindowName;
	}

	/**
	 * Return the default text displayed on the link.
	 * <ul>
	 * <li>else user text if set</li>
	 * <li>else shared text if set</li>
	 * <li>user value if set</li>
	 * <li>bean value if present</li>
	 * <li>else shared value</li>
	 * </ul>
	 *
	 * @return the link text.
	 */
	public String getText() {
		Object text = getComponentModel().text;

		if (text == null) {
			Object value = getData();
			if (value != null) {
				text = value.toString();
			}
		}

		return I18nUtilities.format(null, text);
	}

	/**
	 * Sets the text displayed on the link.
	 *
	 * @param text the text to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setText(final String text, final Serializable... args) {
		getOrCreateComponentModel().text = I18nUtilities.asMessage(text, args);
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

	// ================================
	// Access key
	/**
	 * The accesskey is a shortcut key that will focus the input element when used in combination with the Alt key.
	 *
	 * @return The key that in combination with Alt will focus this input.
	 */
	public char getAccessKey() {
		return getComponentModel().accessKey;
	}

	/**
	 * Returns the accesskey character as a String. If the character is not a letter or digit then <code>null</code> is
	 * returned.
	 *
	 * @return The accesskey character as a String (may be <code>null</code>).
	 */
	public String getAccessKeyAsString() {
		char accessKey = getAccessKey();

		if (Character.isLetterOrDigit(accessKey)) {
			return String.valueOf(accessKey);
		}

		return null;
	}

	/**
	 * Set the accesskey (shortcut key) that will activate the link.
	 *
	 * @param accessKey The key (in combination with the Alt key) that activates this element.
	 */
	public void setAccessKey(final char accessKey) {
		getOrCreateComponentModel().accessKey = accessKey;
	}

	/**
	 * Indicates whether this link is disabled in the given context.
	 *
	 * @return true if this link is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this link is disabled by default.
	 *
	 * @param disabled true if this link is to disabled by default, false for enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * @return the relationship of the link's target to the current page.
	 */
	public String getRel() {
		return getComponentModel().rel;
	}

	/**
	 * Sets the relationship of the link's target to the current page.
	 *
	 * @param rel the relationship to set.
	 * @see
	 * <a href="http://www.w3.org/TR/html4/struct/links.html#adef-rel">Links in html docuemnts</a>
	 */
	public void setRel(final String rel) {
		getOrCreateComponentModel().rel = rel;
	}

	/**
	 * Indicates whether this link should open in a new window.
	 *
	 * @return true if the link should open in a new window, false if it should re-use the existing window.
	 */
	public boolean getOpenNewWindow() {
		return getComponentModel().openNewWindow;
	}

	/**
	 * Sets whether this link should open in a new window.
	 *
	 * @param openNewWindow true to open in a new window, false to re-use the existing window.
	 */
	public void setOpenNewWindow(final boolean openNewWindow) {
		getOrCreateComponentModel().openNewWindow = openNewWindow;
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
		return getComponentModel().windowAttrs;
	}

	/**
	 * @param windowAttrs the attributes for new windows which are opened.
	 */
	public void setWindowAttrs(final WindowAttributes windowAttrs) {
		LinkModel model = getOrCreateComponentModel();

		if (windowAttrs != null) {
			if (windowAttrs.link != null) {
				throw new IllegalArgumentException(
						"WindowAttributes is already being used by another WLink");
			}

			windowAttrs.link = this;
			model.targetWindowName = windowAttrs.getWindowName();
		}

		model.windowAttrs = windowAttrs;
	}

	/**
	 * Indicates whether this link should render as a button.
	 *
	 * @return true if this link should render as a button, false for a hyperlink.
	 */
	public boolean isRenderAsButton() {
		return getComponentModel().renderAsButton;
	}

	/**
	 * Sets whether this link should render as a button.
	 *
	 * @param renderAsButton true if this link should render as a button, false for a hyperlink.
	 */
	public void setRenderAsButton(final boolean renderAsButton) {
		getOrCreateComponentModel().renderAsButton = renderAsButton;
	}

	// ================================
	// Action handling
	/**
	 * @return the action to execute when the link is pressed.
	 */
	public Action getAction() {
		return getComponentModel().action;
	}

	/**
	 * Sets the action that will run if the link is pressed.
	 * <p>
	 * The intended use of this action is when the link opens a new window or launches another application such as
	 * "mailto".
	 * </p>
	 *
	 * @param action the action to execute when the link is pressed.
	 * @param actionTargets the targets to replace when the link is clicked.
	 */
	public void setAction(final Action action, final AjaxTarget... actionTargets) {
		LinkModel model = getOrCreateComponentModel();
		model.action = action;
		model.actionTargets = actionTargets;
	}

	/**
	 * @return the targets to replace when the link with an action is clicked.
	 */
	public AjaxTarget[] getActionTargets() {
		return getComponentModel().actionTargets;
	}

	/**
	 * Retrieves this menu item's action command.
	 *
	 * @return the actionCommand.
	 */
	public String getActionCommand() {
		return getComponentModel().actionCommand;
	}

	/**
	 * Sets this menu item's action command.
	 *
	 * @param actionCommand The actionCommand to set.
	 */
	public void setActionCommand(final String actionCommand) {
		getOrCreateComponentModel().actionCommand = actionCommand;
	}

	/**
	 * Returns the data object that has been associated with this button, else null. For convenience, this data object
	 * is passed to the execute() method of the button's associated Action, in the ActionEvent parameter.
	 *
	 * @return the action object.
	 */
	public Object getActionObject() {
		return getComponentModel().actionObject;
	}

	/**
	 * Associate this button with a data object that can be easily accessed in the execute() method of the button's
	 * associated Action.
	 *
	 * @param data the action object.
	 */
	public void setActionObject(final Serializable data) {
		getOrCreateComponentModel().actionObject = data;
	}

	/**
	 * Override handleRequest in order to perform processing for this component. This implementation checks whether the
	 * link has been pressed via the current ajax operation.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		// Check if this link was the AJAX Trigger
		AjaxOperation operation = AjaxHelper.getCurrentOperation();
		boolean pressed = (operation != null && getId().equals(operation.getTriggerId()));

		// Protect against client-side tampering of disabled/read-only fields.
		if (isDisabled() && pressed) {
			LOG.warn("A disabled link has been triggered. " + getText() + ". " + getId());
			return;
		}

		// If an action has been supplied then execute it, but only after
		// handle request has been performed on the entire component tree.
		final Action action = getAction();

		if (pressed && action != null) {
			final ActionEvent event = new ActionEvent(this, getActionCommand(), getActionObject());

			Runnable later = new Runnable() {
				@Override
				public void run() {
					action.execute(event);
				}
			};

			invokeLater(later);
		}
	}

	/**
	 * Override preparePaintComponent to register an AJAX operation if this link has an action.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		UIContext uic = UIContextHolder.getCurrent();

		// If the link has an action, register it for AJAX
		final Action action = getAction();
		final AjaxTarget[] actionTargets = getActionTargets();

		if (action != null && uic.getUI() != null) {
			if (actionTargets != null && actionTargets.length > 0) {
				List<String> targetIds = new ArrayList<>();
				for (AjaxTarget target : actionTargets) {
					targetIds.add(target.getId());
				}
				// Register the action targets
				AjaxHelper.registerComponents(targetIds, request, getId());
			} else {
				// If no action targets set, then register the link itself as the target
				AjaxHelper.registerComponentTargetItself(this.getId(), request);
			}
		}
	}

	/**
	 * Return the image to display on the link.
	 *
	 * @return the image.
	 */
	public Image getImage() {
		return getComponentModel().image;
	}

	/**
	 * Sets the image to display on the link.
	 *
	 * @param image the image, or null for no image.
	 */
	public void setImage(final Image image) {
		LinkModel model = getOrCreateComponentModel();
		model.image = image;
		model.imageUrl = null;
	}

	/**
	 * Sets the image to display on the link. The image will be read from the application's class path rather than from
	 * its web docs.
	 *
	 * @param image the relative path to the image resource, or null for no image.
	 */
	public void setImage(final String image) {
		setImage(new ImageResource(image));
	}

	/**
	 * Return the {@link WImage} used by this link to hold the {@link Image} resource.
	 * <p>
	 * If the link is not using an Image resource, it will return null.
	 * </p>
	 *
	 * @return the WImage holding the Image resource, or null if the link is not using an Image resource.
	 */
	public WImage getImageHolder() {
		return getImage() == null ? null : linkImage;
	}

	/**
	 * Return the URL of the image to display on the link.
	 *
	 * @return the image url.
	 */
	public String getImageUrl() {
		return getImage() == null ? getComponentModel().imageUrl : getImageHolder().getTargetUrl();
	}

	/**
	 * Sets the URL of the image to display on the link.
	 *
	 * @param imageUrl the image url, or null for no image.
	 */
	public void setImageUrl(final String imageUrl) {
		LinkModel model = getOrCreateComponentModel();
		model.imageUrl = imageUrl;
		model.image = null;
	}

	/**
	 * @return the position of the image
	 */
	public ImagePosition getImagePosition() {
		return getComponentModel().imagePosition;
	}

	/**
	 * The position of the image on the link.
	 *
	 * @param imagePosition the position of the image
	 */
	public void setImagePosition(final ImagePosition imagePosition) {
		getOrCreateComponentModel().imagePosition = imagePosition;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, 1);
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
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new model appropriate for this component.
	 *
	 * @return a new {@link LinkModel}.
	 */
	@Override
	protected LinkModel newComponentModel() {
		return new LinkModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected LinkModel getComponentModel() {
		return (LinkModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected LinkModel getOrCreateComponentModel() {
		return (LinkModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WLink.
	 */
	public static class LinkModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The text for the link.
		 */
		private Serializable text;

		/**
		 * The URL for the link.
		 */
		private String url;

		/**
		 * The window name for the link.
		 */
		private String targetWindowName = "somename";

		/**
		 * Indicates whether the link should open in a new window.
		 */
		private boolean openNewWindow = true;

		/**
		 * The key shortcut which activates the input element.
		 */
		private char accessKey = '\0';

		/**
		 * Relationship to the current page.
		 */
		private String rel;

		/**
		 * Render the link as a button.
		 */
		private boolean renderAsButton = false;

		/**
		 * The attributes of the window if the url will be opened in a new window.
		 */
		private WindowAttributes windowAttrs;

		/**
		 * The action to execute when the link is pressed.
		 */
		private Action action;

		/**
		 * The action command to pass to the link's action when it executes.
		 */
		private String actionCommand;

		/**
		 * The object to include in the action event when the action is triggered.
		 */
		private Object actionObject;

		/**
		 * The targets to replace when the link with an action is pressed.
		 */
		private AjaxTarget[] actionTargets;

		/**
		 * If not null, it will be taken as a URL to use as image.
		 */
		private String imageUrl;

		/**
		 * The position of the image.
		 */
		private ImagePosition imagePosition;

		/**
		 * The image to display on the link.
		 */
		private Image image;
	}

	/**
	 * This WImage implemention delegates to the link's image and is only used to serve up the image for the link.
	 */
	private static final class LinkImage extends WImage {

		/**
		 * The link containing the link image.
		 */
		private final WLink link;

		/**
		 * Creates a link image.
		 *
		 * @param link the owning link.
		 */
		private LinkImage(final WLink link) {
			this.link = link;
		}

		/**
		 * Override isVisible to only return true if the link has an image.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			return link.getImage() != null;
		}

		/**
		 * Override getImage to return the link's image.
		 *
		 * @return the link image.
		 */
		@Override
		public Image getImage() {
			return link.getImage();
		}
	};

	/**
	 * Encapsulates window attributes for new windows which are opened.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class WindowAttributes implements Serializable, Cloneable {

		/**
		 * The link which these window attributes belong to. Used to enforce locking.
		 */
		private WLink link;

		/**
		 * The name of the window to open.
		 */
		private String windowName;

		/**
		 * Indicates whether the new window should be resizable.
		 */
		private boolean resizable;

		/**
		 * Indicates whether the new window should have scrollbars.
		 */
		private boolean scrollbars;

		/**
		 * Indicates whether the new window should have toolbars.
		 */
		private boolean toolbars;

		/**
		 * Indicates whether the new window should have the location bar.
		 */
		private boolean location;

		/**
		 * Indicates whether the new window should show directory buttons.
		 */
		private boolean directories;

		/**
		 * Indicates whether the new window should show the status bar.
		 */
		private boolean status;

		/**
		 * Indicates whether the new window should show a menu bar.
		 */
		private boolean menubar;

		/**
		 * The x-coordinate of the new window.
		 */
		private int left = -1;

		/**
		 * The y-coordinate of the new window.
		 */
		private int top = -1;

		/**
		 * The width of the new window.
		 */
		private int width = -1;

		/**
		 * The height of the new window.
		 */
		private int height = -1;

		/**
		 * @return Returns the windowName.
		 */
		public String getWindowName() {
			return windowName;
		}

		/**
		 * @param windowName The windowName to set.
		 */
		public void setWindowName(final String windowName) {
			this.windowName = windowName;
		}

		/**
		 * @return Returns the resizable.
		 */
		public boolean isResizable() {
			return resizable;
		}

		/**
		 * @param resizable The resizable to set.
		 */
		public void setResizable(final boolean resizable) {
			this.resizable = resizable;
		}

		/**
		 * @return Returns the scrollbars.
		 */
		public boolean isScrollbars() {
			return scrollbars;
		}

		/**
		 * @param scrollbars The scrollbars to set.
		 */
		public void setScrollbars(final boolean scrollbars) {
			this.scrollbars = scrollbars;
		}

		/**
		 * @return Returns the toolbars.
		 */
		public boolean isToolbars() {
			return toolbars;
		}

		/**
		 * @param toolbars The toolbars to set.
		 */
		public void setToolbars(final boolean toolbars) {
			this.toolbars = toolbars;
		}

		/**
		 * @return Returns the location.
		 */
		public boolean isLocation() {
			return location;
		}

		/**
		 * @param location The location to set.
		 */
		public void setLocation(final boolean location) {
			this.location = location;
		}

		/**
		 * @return Returns the directories.
		 */
		public boolean isDirectories() {
			return directories;
		}

		/**
		 * @param directories The directories to set.
		 */
		public void setDirectories(final boolean directories) {
			this.directories = directories;
		}

		/**
		 * @return Returns the status.
		 */
		public boolean isStatus() {
			return status;
		}

		/**
		 * @param status The status to set.
		 */
		public void setStatus(final boolean status) {
			this.status = status;
		}

		/**
		 * @return Returns the menubar.
		 */
		public boolean isMenubar() {
			return menubar;
		}

		/**
		 * @param menubar The menubar to set.
		 */
		public void setMenubar(final boolean menubar) {
			this.menubar = menubar;
		}

		/**
		 * @return Returns the left.
		 */
		public int getLeft() {
			return left;
		}

		/**
		 * @param left The left to set.
		 */
		public void setLeft(final int left) {
			this.left = left;
		}

		/**
		 * @return Returns the top.
		 */
		public int getTop() {
			return top;
		}

		/**
		 * @param top The top to set.
		 */
		public void setTop(final int top) {
			this.top = top;
		}

		/**
		 * @return Returns the width.
		 */
		public int getWidth() {
			return width;
		}

		/**
		 * @param width The width to set.
		 */
		public void setWidth(final int width) {
			this.width = width;
		}

		/**
		 * @return Returns the height.
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * @param height The height to set.
		 */
		public void setHeight(final int height) {
			this.height = height;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public WindowAttributes clone() throws CloneNotSupportedException {
			return (WindowAttributes) super.clone();
		}
	}

	/**
	 * This class allows building of WLink in a fluent interface style. It will use a default window name if window name
	 * is not supplied along with other attributes.
	 */
	public static class Builder {

		/**
		 * Used to quote attributes in the windowAttributes String.
		 */
		public static final String QUOTE = "'";
		/**
		 * Used to separate attributes in the windowAttributes String.
		 */
		public static final String SEPARATOR = ",";

		/**
		 * The link text.
		 */
		private final String text;

		/**
		 * The link's URL.
		 */
		private final String url;

		/**
		 * The link's window attributes.
		 */
		private final WindowAttributes windowAttrs = new WindowAttributes();

		/**
		 * Creates a builder with no attributes set.
		 */
		public Builder() {
			this(null, null);
		}

		/**
		 * Creates a builder preconfigured with the given link text and URL.
		 *
		 * @param text the link text.
		 * @param url the link url.
		 */
		public Builder(final String text, final String url) {
			this.text = text;
			this.url = url;
		}

		/**
		 * Sets the target window name.
		 *
		 * @param windowName the target window name.
		 * @return this builder.
		 */
		public Builder windowName(final String windowName) {
			windowAttrs.setWindowName(windowName);
			return this;
		}

		/**
		 * Sets the target window's width.
		 *
		 * @param width the window width, in pixels.
		 * @return this builder.
		 */
		public Builder width(final int width) {
			windowAttrs.setWidth(width);
			return this;
		}

		/**
		 * Sets the target window's height.
		 *
		 * @param height the window height, in pixels.
		 * @return this builder.
		 */
		public Builder height(final int height) {
			windowAttrs.setHeight(height);
			return this;
		}

		/**
		 * Sets whether the target window should be able to be resized.
		 *
		 * @param val true if the window should be resizable, false if not.
		 * @return this builder.
		 */
		public Builder resizable(final boolean val) {
			windowAttrs.setResizable(val);
			return this;
		}

		/**
		 * Sets whether the target window should have scrollbars visible.
		 *
		 * @param val true if the window should have scrollbars visible, false if not.
		 * @return this builder.
		 */
		public Builder scrollbars(final boolean val) {
			windowAttrs.setScrollbars(val);
			return this;
		}

		/**
		 * Sets whether the target window should have the browser toolbar visible.
		 *
		 * @param val true if the window should have the toolbar visible, false if not.
		 * @return this builder.
		 */
		public Builder toolbar(final boolean val) {
			windowAttrs.setToolbars(val);
			return this;
		}

		/**
		 * Sets whether the target window should have the browser location input field visible.
		 *
		 * @param val true if the window should have the location visible, false if not.
		 * @return this builder.
		 */
		public Builder location(final boolean val) {
			windowAttrs.setLocation(val);
			return this;
		}

		/**
		 * Sets whether the target window should contain the standard browser directory buttons.
		 *
		 * @param val true the target window should contain the standard browser directory buttons, false if not.
		 * @return this builder.
		 */
		public Builder directories(final boolean val) {
			windowAttrs.setDirectories(val);
			return this;
		}

		/**
		 * Sets whether the target window should have the browser status bar visible.
		 *
		 * @param val true if the window should have the status visible, false if not.
		 * @return this builder.
		 */
		public Builder status(final boolean val) {
			windowAttrs.setStatus(val);
			return this;
		}

		/**
		 * Sets whether the target window should have the browser menu bar visible.
		 *
		 * @param val true if the window should have the menu bar visible, false if not.
		 * @return this builder.
		 */
		public Builder menubar(final boolean val) {
			windowAttrs.setMenubar(val);
			return this;
		}

		/**
		 * Sets the X-axis location of the browser window.
		 *
		 * @param val the location for the left side of the browser window, in pixels.
		 * @return this builder.
		 */
		public Builder left(final int val) {
			windowAttrs.setLeft(val);
			return this;
		}

		/**
		 * Sets the Y-axis location of the browser window.
		 *
		 * @param val the location for the top side of the browser window, in pixels.
		 * @return this builder.
		 */
		public Builder top(final int val) {
			windowAttrs.setTop(val);
			return this;
		}

		/**
		 * Build the attributes list for new window.
		 *
		 * @return a new WLink, configured with the attributes from this builder.
		 */
		public WLink build() {
			// add a default unique name for the window if not provided
			if (Util.empty(windowAttrs.getWindowName())) {
				windowName("myWindow");
			}

			try {
				return new WLink(text, url, windowAttrs.clone());
			} catch (CloneNotSupportedException e) {
				// Impossible, this class implements cloneable
				throw new SystemException("Failed to clone WLink.Builder", e);
			}
		}
	}

}
