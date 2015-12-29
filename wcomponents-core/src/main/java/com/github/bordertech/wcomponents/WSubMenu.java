package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WMenu.SelectMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This component is used to provide either a dropmenu (when added directly to a {@link WMenu} or nestable sub menus
 * (when added to another <code>WSubMenu</code> instance).</p>
 *
 * <p>
 * Sub Menus may contain the following components:
 * </p>
 * <ul>
 * <li>Other sub menus.</li>
 * <li>Menu items ({@link WMenuItem})</li>
 * <li>Menu separators (see {@link #addSeparator()})</li>
 * </ul>
 *
 * <p>
 * Actions on sub-menus are only supported for {@link WMenu.MenuType#COLUMN} menus.</p>
 *
 * @author Adam Millard
 * @author Yiannis Paschalidis - re-written to not extend WButton.
 */
public class WSubMenu extends AbstractNamingContextContainer implements Disableable, MenuSelectContainer, MenuItemSelectable {

	/**
	 * The available types of operation.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum MenuMode {
		/**
		 * Indicates that a round-trip should be made whenever the menu is opened.
		 *
		 * @deprecated Use MenuMode DYNAMIC instead as a like-for-like replacement or any other mode if it is more
		 * appropriate to the individual use case.
		 */
		SERVER,
		/**
		 * Indicates that an ajax request should be made the first time the menu is opened.
		 */
		LAZY,
		/**
		 * Indicates that the sub-menu content is always sent to the client.
		 */
		CLIENT,
		/**
		 * Indicates that an ajax request should be made whenever the menu is opened.
		 */
		DYNAMIC,
		/**
		 * Indicates that an ajax request should be made immediately after the page is loaded.
		 */
		EAGER
	};

	/**
	 * The submenu's text label.
	 */
	private final WDecoratedLabel label;

	/**
	 * Hold the submenu's items.
	 */
	private final WContainer content = new WContainer();

	/**
	 * Creates a WSubMenu with the given text.
	 *
	 * @param text the sub menu text.
	 */
	public WSubMenu(final String text) {
		this(new WDecoratedLabel(text));
	}

	/**
	 * Creates a WSubMenu with the given text.
	 *
	 * @param label the sub menu label.
	 */
	public WSubMenu(final WDecoratedLabel label) {
		this.label = label;
		add(label);
		add(content);
	}

	/**
	 * Creates a WSubMenu with the given text and access key.
	 *
	 * @param text the sub menu text.
	 * @param accessKey the access key.
	 */
	public WSubMenu(final String text, final char accessKey) {
		this(text);
		setAccessKey(accessKey);
	}

	/**
	 * Sets the menu mode.
	 *
	 * @param mode the menu mode.
	 */
	public void setMode(final MenuMode mode) {
		getOrCreateComponentModel().mode = mode;
	}

	/**
	 * @return the menu mode.
	 */
	public MenuMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Indicates whether this is a top-level menu (ie. attached to a menu bar).
	 *
	 * @return true if this is a top-level menu.
	 */
	public boolean isTopLevelMenu() {
		MenuContainer container = (MenuContainer) WebUtilities.getAncestorOfClass(MenuContainer.class, this);
		return container instanceof WMenu;
	}

	/**
	 * Adds a separator to the sub-menu.
	 */
	public void addSeparator() {
		addMenuItem(new WSeparator());
	}

	/**
	 * @param item add a {@link WSeparator}
	 */
	public void add(final WSeparator item) {
		addMenuItem(item);
	}

	/**
	 * @param item add a {@link WMenuItem}
	 */
	public void add(final WMenuItem item) {
		addMenuItem(item);
	}

	/**
	 * Adds a menu item group to this sub menu.
	 *
	 * @param menuItemGroup the menu item group to add.
	 * @deprecated menu groups are not compatible with WCAG 2.0.
	 */
	public void addMenuItemGroup(final WMenuItemGroup menuItemGroup) {
		addMenuItem(menuItemGroup);
	}

	/**
	 * @param item add a {@link WMenuItemGroup}
	 */
	public void add(final WMenuItemGroup item) {
		addMenuItem(item);
	}

	/**
	 * @param item add a {@link WSubMenu}
	 */
	public void add(final WSubMenu item) {
		addMenuItem(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMenuItem(final MenuItem item) {
		getContent().add(item);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link #removeMenuItem(com.github.bordertech.wcomponents.MenuItem)} instead.
	 */
	@Deprecated
	@Override
	public void remove(final WComponent item) {
		if (item instanceof MenuItem) {
			removeMenuItem((MenuItem) item);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMenuItem(final MenuItem item) {
		getContent().remove(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllMenuItems() {
		getContent().removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MenuItem> getMenuItems() {
		List<MenuItem> items = new ArrayList(getContent().getChildren());
		return Collections.unmodifiableList(items);
	}

	/**
	 * Indicates whether this sub menu is disabled in the given context.
	 *
	 * @return true if this sub menu is disabled.
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
	 * Sets whether this sub menu is disabled.
	 *
	 * @param disabled true to disable this sub menu, false to enable.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * <p>
	 * Set the accesskey (shortcut key) that will activate the sub-menu.
	 * </p>
	 *
	 * @param accesskey The key (in combination with the Alt key) that activates this element.
	 */
	public void setAccessKey(final char accesskey) {
		getOrCreateComponentModel().accesskey = accesskey;
	}

	/**
	 * The accesskey is a shortcut key that will focus the input element when used in combination with the Alt key.
	 *
	 * @return The key that in combination with Alt will focus this input.
	 */
	public char getAccessKey() {
		return getComponentModel().accesskey;
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
	 * @return the sub-menu text.
	 */
	public String getText() {
		return label.getText();
	}

	/**
	 * Sets the sub-menu text.
	 *
	 * @param text the sub-menu text.
	 */
	public void setText(final String text) {
		label.setText(text);
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
	 *
	 * @deprecated submenus should not be selectable.
	 */
	@Override
	public Boolean getSelectability() {
		return getComponentModel().selectability;
	}

	/**
	 * @param selectability true if this item is selectable, false if not, or null to default to the container.
	 * @deprecated submenus should not be selectable.
	 */
	@Override
	public void setSelectability(final Boolean selectability) {
		getOrCreateComponentModel().selectability = selectability;
	}

	/**
	 * @return Returns the multipleSelection.
	 * @deprecated Use {{@link com.github.bordertech.wcomponents.MenuSelectContainer#getSelectionMode()}.
	 */
	@Deprecated
	public boolean isMultipleSelection() {
		return SelectionMode.MULTIPLE.equals(getSelectionMode());
	}

	/**
	 * @param multipleSelection The multipleSelection to set.
	 * @deprecated Use {{@link com.github.bordertech.wcomponents.MenuSelectContainer#setSelectionMode(com.github.bordertech.wcomponents.MenuSelectContainer.SelectionMode)}.
	 */
	@Deprecated
	public void setMultipleSelection(final boolean multipleSelection) {
		setSelectionMode(multipleSelection ? SelectionMode.MULTIPLE : SelectionMode.NONE);
	}

	/**
	 * @return the selection mode of the container
	 * @deprecated Use {@link com.github.bordertech.wcomponents.MenuSelectContainer#getSelectionMode()} instead.
	 */
	public SelectMode getSelectMode() {
		switch (getSelectionMode()) {
			case MULTIPLE:
				return SelectMode.MULTIPLE;
			case SINGLE:
				return SelectMode.SINGLE;
			default:
				return SelectMode.NONE;
		}
	}

	/**
	 * @param selectMode the selection mode for the items in this menu container.
	 *
	 * @deprecated Use {@link com.github.bordertech.wcomponents.MenuSelectContainer#setSelectionMode(com.github.bordertech.wcomponents.MenuSelectContainer.SelectionMode)} instead.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		switch (selectMode) {
			case MULTIPLE:
				setSelectionMode(SelectionMode.MULTIPLE);
				break;
			case SINGLE:
				setSelectionMode(SelectionMode.SINGLE);
				break;
			default:
				setSelectionMode(SelectionMode.NONE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectionMode getSelectionMode() {
		return getComponentModel().selectionMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectionMode(final SelectionMode selectionMode) {
		getOrCreateComponentModel().selectionMode = selectionMode;
	}

	/**
	 * @return the sub-menu's action, or null if there is no action specified.
	 */
	public Action getAction() {
		return getComponentModel().action;
	}

	/**
	 * Sets the action to execute when the sub-menu is selected. Note that actions are currently only supported for
	 * {@link WMenu.MenuType#COLUMN} menus.
	 *
	 * @param action the menu item's action.
	 */
	public void setAction(final Action action) {
		getOrCreateComponentModel().action = action;
	}

	/**
	 * Retrieves this sub-menu's "default" action command.
	 *
	 * @return the actionCommand.
	 */
	public String getActionCommand() {
		return getComponentModel().actionCommand;
	}

	/**
	 * Sets this sub-menu's action command.
	 *
	 * @param actionCommand The actionCommand to set.
	 */
	public void setActionCommand(final String actionCommand) {
		getOrCreateComponentModel().actionCommand = actionCommand;
	}

	/**
	 * Retrieves this sub-menu's "default" action object.
	 *
	 * @return the actionObject.
	 */
	public Serializable getActionObject() {
		return getComponentModel().actionObject;
	}

	/**
	 * Sets this sub-menu's "default" action object.
	 *
	 * @param actionObject The actionObject to set.
	 */
	public void setActionObject(final Serializable actionObject) {
		getOrCreateComponentModel().actionObject = actionObject;
	}

	/**
	 * Sets whether this menu is open. Only has an effect for some menu types.
	 *
	 * @param open true if the menu should be open, false for closed.
	 */
	public void setOpen(final boolean open) {
		getOrCreateComponentModel().open = open;
	}

	/**
	 * Indicates whether this sub-menu is open in the given context.
	 *
	 * @return true if this menu is open in the given context, false otherwise.
	 */
	public boolean isOpen() {
		return getComponentModel().open;
	}

	/**
	 * @return the sub menu's decorated label.
	 */
	public WDecoratedLabel getDecoratedLabel() {
		return label;
	}

	/**
	 * Indicates whether this sub-menuis selected (for menu types which support sub-menu selection).
	 *
	 * @return true if this sub-menu is selected, false otherwise.
	 * @deprecated submenus should not be selectable
	 */
	@Deprecated
	@Override
	public boolean isSelected() {
		WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, this);

		if (menu != null) {
			return menu.getSelectedMenuItems().contains(this);
		}

		return false;
	}

	/**
	 * Override handleRequest in order to perform processing for this component. This implementation checks for submenu
	 * selection and executes the associated action if it has been set.
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
			// If current ajax trigger, process menu for current selections
			if (AjaxHelper.isCurrentAjaxTrigger(this)) {
				WMenu menu = WebUtilities.getAncestorOfClass(WMenu.class, this);
				menu.handleRequest(request);

				// Execute associated action, if set
				final Action action = getAction();

				if (action != null) {
					final ActionEvent event = new ActionEvent(this, this.getActionCommand(),
							this.getActionObject());

					Runnable later = new Runnable() {
						@Override
						public void run() {
							action.execute(event);
						}
					};

					invokeLater(later);
				}
			}

			boolean openState = "true".equals(request.getParameter(getId() + ".open"));
			setOpen(openState);
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
		return menu != null && menu.isPresent(request);
	}

	/**
	 * Override preparePaintComponent in order to correct the visibility of the sub-menu's children before they are
	 * rendered.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		String targetId = getContent().getId();
		String contentId = getId() + "-content";

		switch (getComponentModel().mode) {
			case LAZY: {
				getContent().setVisible(isOpen());
				if (!isOpen()) {
					AjaxHelper.registerContainer(getId(), contentId, targetId, request);
				}
				break;
			}
			case DYNAMIC: {
				AjaxHelper.registerContainer(getId(), contentId, targetId, request);
				getContent().setVisible(isOpen());
				break;
			}
			case EAGER: {
				AjaxHelper.registerContainer(getId(), contentId, targetId, request);
				// Will always be visible
				break;
			}
			case SERVER: {
				getContent().setVisible(isOpen());
				break;
			}
			case CLIENT: {
				// Will always be visible
				break;
			}
		}
	}

	/**
	 * Paint the menu items.
	 *
	 * @param renderContext the render context
	 */
	public void paintMenuItems(final RenderContext renderContext) {
		getContent().paint(renderContext);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');
		return getContent().toString(text, -1, -1);
	}

	/**
	 * @return the container holding the menu items
	 */
	private WContainer getContent() {
		return content;
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new SubMenuModel.
	 */
	@Override
	protected SubMenuModel newComponentModel() {
		return new SubMenuModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SubMenuModel getComponentModel() {
		return (SubMenuModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected SubMenuModel getOrCreateComponentModel() {
		return (SubMenuModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WSubMenu.
	 *
	 * @author Yiannis Paschalidis
	 * @since 1.0.0
	 */
	public static class SubMenuModel extends ComponentModel {

		/**
		 * Indicates whether the sub-menu is in an open-state.
		 */
		private boolean open;

		/**
		 * Indicates whether the sub-menu supports selection of multiple menu-items.
		 */
		private SelectionMode selectionMode = SelectionMode.NONE;

		/**
		 * Indicates whether the sub-menu itself can be selected (e.g. for column menus).
		 */
		private Boolean selectability;

		/**
		 * The action to execute when the menu item is selected.
		 */
		private Action action;

		/**
		 * The action command to pass to the action when it is executed.
		 */
		private String actionCommand;

		/**
		 * The action object to pass to the action when it is executed.
		 */
		private Serializable actionObject;

		/**
		 * The key shortcut that activates the menu.
		 */
		private char accesskey = '\0';

		/**
		 * The {@link MenuMode | ajax mode}.
		 */
		private MenuMode mode = MenuMode.CLIENT;
	}
}
