package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This component provides a menu item for use either directly in the top level of a {@link WMenu} or as an item within
 * a {@link WSubMenu} or {@link WMenuItemGroup}.
 *
 * @author Adam Millard
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMenuItem extends AbstractContainer implements Disableable, AjaxTrigger, MenuItemSelectable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WMenuItem.class);

	/**
	 * The decorated label which holds the menu item's text/icon etc.
	 */
	private final WDecoratedLabel label;

	/**
	 * Creates a WMenuItem with the given label.
	 *
	 * @param label the menu item label.
	 */
	public WMenuItem(final WDecoratedLabel label) {
		this.label = label;
		add(label);
	}

	/**
	 * Creates a WMenuItem with the given label and url.
	 *
	 * @param label the menu item label.
	 * @param url the URL to navigate to when the menu item is invoked.
	 */
	public WMenuItem(final WDecoratedLabel label, final String url) {
		this(label);
		getComponentModel().url = url;
	}

	/**
	 * Creates a WMenuItem with the given label and action.
	 *
	 * @param label the menu item label.
	 * @param action the action to execute when the menu item is invoked.
	 */
	public WMenuItem(final WDecoratedLabel label, final Action action) {
		this(label);
		getComponentModel().action = action;
	}

	/**
	 * Creates a WMenuItem with the given text and url.
	 *
	 * @param text the menu item text.
	 * @param url the URL to navigate to when the menu item is invoked.
	 */
	public WMenuItem(final String text, final String url) {
		this(text);
		getComponentModel().url = url;
	}

	/**
	 * Creates a WMenuItem with the given label and action.
	 *
	 * @param text the menu item text.
	 * @param action the action to execute when the menu item is invoked.
	 */
	public WMenuItem(final String text, final Action action) {
		this(text);
		getComponentModel().action = action;
	}

	/**
	 * Creates a new WMenuItem with the specified text.
	 *
	 * @param text the menu item's text.
	 */
	public WMenuItem(final String text) {
		this(new WDecoratedLabel());
		label.setBody(new WText(text));
	}

	/**
	 * Creates a new WMenuItem with the specified text and accessKey.
	 *
	 * @param text the menu item's text.
	 * @param accessKey the menu item's access key.
	 */
	public WMenuItem(final String text, final char accessKey) {
		this(text);
		setAccessKey(accessKey);
	}

	/**
	 * Creates a new WMenuItem with the specified text, accessKey and action.
	 *
	 * @param text the menu item's text.
	 * @param accessKey the menu item's access key.
	 * @param action the action to execute when the menu item is invoked.
	 */
	public WMenuItem(final String text, final char accessKey, final Action action) {
		this(text, accessKey);
		getComponentModel().action = action;
	}

	/**
	 * @return the decorated label which displays the menu item's text/icon etc.
	 */
	public WDecoratedLabel getDecoratedLabel() {
		return label;
	}

	/**
	 * @return the menu item's action, or null if there is no action specified.
	 */
	public Action getAction() {
		return getComponentModel().action;
	}

	/**
	 * Sets the action to execute when the menu item is invoked.
	 *
	 * @param action the menu item's action.
	 */
	public void setAction(final Action action) {
		MenuItemModel model = getOrCreateComponentModel();
		model.action = action;
		model.url = null;
	}

	/**
	 * Retrieves the menu item's URL.
	 *
	 * @return the menu item's url, or null if there is no url specified.
	 */
	public String getUrl() {
		return getComponentModel().url;
	}

	/**
	 * Sets the URL to navigate to when the menu item is invoked.
	 *
	 * @param url the url to set.
	 */
	public void setUrl(final String url) {
		MenuItemModel model = getOrCreateComponentModel();
		model.url = url;
		model.action = null;
	}

	/**
	 * <p>
	 * Indicates whether the form should be submitted when the menu item is selected. By default, the form will only be
	 * submitted if an action has been set on this item.
	 * </p>
	 * <p>
	 * Examples of where the form might should not be submitted include if the menu item is within a sub-menu which
	 * supports multiple selection, or if the menu item points at an external URL.
	 * </p>
	 *
	 * @return true if the form should be submitted when the menu item is selected.
	 */
	public boolean isSubmit() {
		return getAction() != null;
	}

	/**
	 * Retrieves the menu item text.
	 *
	 * @return the menu item text
	 */
	public String getText() {
		return getDecoratedLabel().getText();
	}

	/**
	 * Sets the text of the menu item.
	 *
	 * @param text the text to set.
	 */
	public void setText(final String text) {
		getDecoratedLabel().setText(text);
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
	 * Sets this menu item's target window name.
	 *
	 * @param targetWindow the target window name.
	 */
	public void setTargetWindow(final String targetWindow) {
		getOrCreateComponentModel().targetWindow = targetWindow;
	}

	/**
	 * @return true if this item is selectable, false if not, or null to default to the container.
	 * @deprecated Use {@link #getSelectability()} instead.
	 */
	@Deprecated
	public Boolean isSelectable() {
		return getSelectability();
	}

	/**
	 * @param selectable true if this item is selectable, false if not, or null to default to the container.
	 * @deprecated Use {@link #setSelectability(java.lang.Boolean)} instead.
	 */
	@Deprecated
	public void setSelectable(final Boolean selectable) {
		setSelectability(selectable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelected() {
		WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, this);

		if (menu != null) {
			return menu.getSelectedMenuItems().contains(this);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getSelectability() {
		return getComponentModel().selectability;
	}

	/**
	 * @param selectability true if this item is selectable, false if not, or null to default to the container.
	 */
	@Override
	public void setSelectability(final Boolean selectability) {
		getOrCreateComponentModel().selectability = selectability;
	}

	/**
	 * Indicates whether this menu item is disabled in the given context.
	 *
	 * @return true if this menu item is disabled, false if it is enabled.
	 */
	@Override
	public boolean isDisabled() {
		boolean disabled = false;

		MenuContainer container = (MenuContainer) WebUtilities.getAncestorOfClass(MenuContainer.class, this);
		if (container instanceof MenuItemGroup && container instanceof Disableable) {
			disabled = ((Disableable) container).isDisabled();
		}

		return disabled || isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this menu item is disabled.
	 *
	 * @param disabled true to set the item disabled, false for enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * @return the menu item's accesskey.
	 */
	public char getAccessKey() {
		return getComponentModel().accessKey;
	}

	/**
	 * Set the accesskey on the menu item button or link. For more information on access keys see
	 * {@link WButton#setAccessKey(char)}.
	 *
	 * @param accesskey The key that will form a keyboard shortcut to the menu item.
	 */
	public void setAccessKey(final char accesskey) {
		getOrCreateComponentModel().accessKey = accesskey;
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
	 * Retrieves this menu item's action object.
	 *
	 * @return the actionObject.
	 */
	public Serializable getActionObject() {
		return getComponentModel().actionObject;
	}

	/**
	 * Sets this menu item's action object.
	 *
	 * @param actionObject The actionObject to set.
	 */
	public void setActionObject(final Serializable actionObject) {
		getOrCreateComponentModel().actionObject = actionObject;
	}

	/**
	 * @return the confirmation message for the menu item.
	 */
	public String getMessage() {
		return I18nUtilities.format(null, getComponentModel().message);
	}

	/**
	 * Sets the confirmation message that is to be displayed to the user for this menu item.
	 *
	 * @param message the confirmation message to display, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 */
	public void setMessage(final String message, final Serializable... args) {
		getOrCreateComponentModel().message = I18nUtilities.asMessage(message, args);
	}

	/**
	 * @return true if menu item is a cancel control and will warn the user of unsaved changes, otherwise false
	 */
	public boolean isCancel() {
		return getComponentModel().cancel;
	}

	/**
	 * @param cancel true if menu item is a cancel control and will warn the user of unsaved changes, otherwise false
	 */
	public void setCancel(final boolean cancel) {
		getOrCreateComponentModel().cancel = cancel;
	}

	/**
	 * Override handleRequest in order to perform processing for this component. This implementation checks for
	 * selection of the menu item, and executes the associated action if it has been set.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (isDisabled()) {
			// Protect against client-side tampering of disabled/read-only fields.
			return;
		}

		if (isMenuPresent(request)) {
			String requestValue = request.getParameter(getId());

			if (requestValue != null) {
				// Only process on a POST
				if (!"POST".equals(request.getMethod())) {
					LOG.warn("Menu item on a request that is not a POST. Will be ignored.");
					return;
				}

				// Execute associated action, if set
				final Action action = getAction();

				if (action != null) {
					final ActionEvent event = new ActionEvent(this, this.getActionCommand(), this.
							getActionObject());

					Runnable later = new Runnable() {
						@Override
						public void run() {
							action.execute(event);
						}
					};

					invokeLater(later);
				}
			}
		}
	}

	/**
	 * Determine if this WMenuItem's parent WMenu is on the Request.
	 *
	 * @param request the request being responded to.
	 * @return true if this WMenuItem's WMenu is on the Request, otherwise return false.
	 */
	protected boolean isMenuPresent(final Request request) {
		WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, this);

		if (menu != null) {
			return menu.isPresent(request);
		}

		return false;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, -1, -1);
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new MenuItemModel.
	 */
	@Override
	protected MenuItemModel newComponentModel() {
		return new MenuItemModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MenuItemModel getComponentModel() {
		return (MenuItemModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected MenuItemModel getOrCreateComponentModel() {
		return (MenuItemModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WMenuItem.
	 */
	public static class MenuItemModel extends ComponentModel {

		/**
		 * An (external) url to open when the menu item is selected.
		 */
		private String url;

		/**
		 * Used together with the url parameter to launch the url in a new window.
		 */
		private String targetWindow;

		/**
		 * The menu item's access key.
		 */
		private char accessKey;

		/**
		 * The action to execute when the menu item is selected.
		 */
		private Action action;

		/**
		 * The action command to pass to the menu item's action when it executes.
		 */
		private String actionCommand;

		/**
		 * The action object to pass to the menu item's action when it executes.
		 */
		private Serializable actionObject;

		/**
		 * Indicates whether the sub-menu itself can be selected (e.g. for column menus).
		 */
		private Boolean selectability;

		/**
		 * The confirmation message to be shown.
		 */
		private Serializable message;

		/**
		 * Act as a cancel control and warn the user of unsaved changes.
		 */
		private boolean cancel;

	}
}
