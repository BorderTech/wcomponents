package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This component enables rendering of a menuing system for an application. A menu bar consists of a collection of
 * either "Menu Items" (see {@link WMenuItem} or "Sub Menus" (see {@link WSubMenu}).</p>
 *
 * @author Adam Millard
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMenu extends AbstractNamingContextContainer implements Disableable, AjaxTarget,
		Marginable, MenuSelectContainer {

	/**
	 * The available types of client-side menus.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum MenuType {
		/**
		 * The menu is displayed as a horizontal bar, similar to menu bars in desktop applications.
		 */
		BAR,
		/**
		 * The menu is displayed as a series of buttons. This is most commonly used to display a single submenu as a
		 * button.
		 */
		FLYOUT,
		/**
		 * The menu is displayed as a tree structure.
		 */
		TREE,
		/**
		 * The menu is displayed columns from left to right. This menu type supports multiple selections of menu items
		 * and sub-menus.
		 */
		COLUMN
	};

	/**
	 * The available types of selection mode for the items in a menu.
	 *
	 * @deprecated Use {@link MenuSelectContainer#getSelectionMode()} instead.
	 */
	@Deprecated
	public enum SelectMode {
		/**
		 * No items can be selected.
		 */
		NONE,
		/**
		 * A single item can be selected.
		 */
		SINGLE,
		/**
		 * Multiple items can be selected.
		 */
		MULTIPLE
	};

	/**
	 * The type of menu.
	 */
	private final MenuType type;

	/**
	 * Hold the menu's items.
	 */
	private final WContainer content = new WContainer();

	/**
	 * Creates a WMenu which is displayed as a menu bar.
	 */
	public WMenu() {
		this(MenuType.BAR);
	}

	/**
	 * Creates a WMenu of the given type.
	 *
	 * @param type the type of WMenu to create.
	 */
	public WMenu(final MenuType type) {
		this.type = type;
		add(content);
	}

	/**
	 * @return the menu type.
	 */
	public MenuType getType() {
		return type;
	}

	/**
	 * @return the number of rows to display for a column menu.
	 */
	public int getRows() {
		return getComponentModel().rows;
	}

	/**
	 * Sets the number of rows to display for a column menu.
	 *
	 * @param rows The rows to set.
	 */
	public void setRows(final int rows) {
		getOrCreateComponentModel().rows = rows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * @return the selection mode of the container
	 * @deprecated Use {@link #getSelectionMode()} instead.
	 */
	@Deprecated
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
	 * @deprecated Use {@link #setSelectionMode(com.github.bordertech.wcomponents.MenuSelectContainer.SelectionMode)} instead.
	 */
	@Deprecated
	public void setSelectMode(final SelectMode selectMode) {
		if (selectMode == null) {
			setSelectionMode(null);
		} else {
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
		getOrCreateComponentModel().selectionMode = selectionMode == null ? SelectionMode.NONE : selectionMode;
	}

	/**
	 * Indicates whether this menu is disabled.
	 *
	 * @return true if this menu is disabled.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this menu is disabled.
	 *
	 * @param disabled if true, the menu will be disabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * Adds a separator to the end of the menu.
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
	 * @param item add a {@link WMenuItemGroup}
	 * @deprecated menu groups are not compatible with WCAG 2.0.
	 */
	@Deprecated
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
	 * @param recurse true if recurse into child items that are menu containers (ie group items and submenus).
	 * @return the list of menu items
	 */
	public List<MenuItem> getMenuItems(final boolean recurse) {
		List<MenuItem> items = new ArrayList();
		getChildMenuItems(items, recurse, this);
		return Collections.unmodifiableList(items);
	}

	/**
	 * Returns the selected item (WMenUItem/WSubMenu, depending on the menu type) in the given context.
	 *
	 * @return the selected item, or null if no item has been selected.
	 * @deprecated Use {@link #getSelectedMenuItem()} instead.
	 */
	@Deprecated
	public WComponent getSelectedItem() {
		return getSelectedMenuItem();
	}

	/**
	 * Sets the selected item (WMenuItem and/or WSubMenu, depending on the menu type).
	 *
	 * @param selectedItem the selected item.
	 * @deprecated Use {@link #setSelectedMenuItem(com.github.bordertech.wcomponents.MenuItemSelectable)} instead.
	 */
	@Deprecated
	public void setSelectedItem(final WComponent selectedItem) {
		setSelectedMenuItem((MenuItemSelectable) selectedItem);
	}

	/**
	 * Returns the selected items (WMenUItems/WSubMenus, depending on the menu type) in the given context.
	 *
	 * @return the selected items, or an empty list if nothing is selected.
	 * @deprecated Use {@link #getSelectedMenuItems()} instead.
	 */
	@Deprecated
	public List<WComponent> getSelectedItems() {
		List<WComponent> items = new ArrayList(getSelectedMenuItems());
		return Collections.unmodifiableList(items);
	}

	/**
	 * Sets the selected items (WMenuItems or WSubMenus, depending on the menu type).
	 *
	 * @param selectedItems the selected items.
	 * @deprecated Use {@link #setSelectedMenuItems(java.util.List)} instead.
	 */
	@Deprecated
	public void setSelectedItems(final List<WComponent> selectedItems) {
		if (selectedItems == null || selectedItems.isEmpty()) {
			setSelectedMenuItems(null);
		} else {
			List<MenuItemSelectable> items = new ArrayList(selectedItems);
			setSelectedMenuItems(items);
		}
	}

	/**
	 * Clears an existing list of selected items.
	 *
	 * @deprecated Use {@link #clearSelectedMenuItems()} instead.
	 */
	@Deprecated
	public void clearSelectedItems() {
		clearSelectedMenuItems();
	}

	/**
	 * Returns the selected item (WMenUItem/WSubMenu, depending on the menu type) in the given context.
	 *
	 * @return the selected item, or null if no item has been selected.
	 */
	public MenuItemSelectable getSelectedMenuItem() {
		List<MenuItemSelectable> selectedItems = getSelectedMenuItems();
		if (selectedItems.isEmpty()) {
			return null;
		} else {
			return selectedItems.get(0);
		}
	}

	/**
	 * Sets the selected item (WMenuItem and/or WSubMenu, depending on the menu type).
	 *
	 * @param selectedItem the selected item.
	 */
	public void setSelectedMenuItem(final MenuItemSelectable selectedItem) {
		MenuModel model = getOrCreateComponentModel();
		if (selectedItem == null) {
			model.selectedMenuItems = null;
		} else {
			if (model.selectedMenuItems == null) {
				model.selectedMenuItems = new ArrayList<>();
			} else {
				model.selectedMenuItems.clear();
			}
			model.selectedMenuItems.add(selectedItem);
		}
	}

	/**
	 * Returns the selected items (WMenUItems/WSubMenus, depending on the menu type) in the given context.
	 *
	 * @return the selected items, or an empty list if nothing is selected.
	 */
	public List<MenuItemSelectable> getSelectedMenuItems() {
		List<MenuItemSelectable> selectedItems = getComponentModel().selectedMenuItems;
		if (selectedItems == null || selectedItems.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(selectedItems);
		}
	}

	/**
	 * Sets the selected items (WMenuItems or WSubMenus, depending on the menu type).
	 *
	 * @param selectedItems the selected items.
	 */
	public void setSelectedMenuItems(final List<? extends MenuItemSelectable> selectedItems) {
		MenuModel model = getOrCreateComponentModel();
		if (selectedItems == null || selectedItems.isEmpty()) {
			model.selectedMenuItems = null;
		} else {
			model.selectedMenuItems = new ArrayList<>(selectedItems);
		}
	}

	/**
	 * Clears an existing list of selected items.
	 */
	public void clearSelectedMenuItems() {
		MenuModel model = getOrCreateComponentModel();
		model.selectedMenuItems = null;
	}

	/**
	 * Override handleRequest in order to perform processing specific to WMenu.
	 *
	 * @param request the request being handled.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (isDisabled()) {
			// Protect against client-side tampering of disabled/read-only fields.
			return;
		}

		if (isPresent(request)) {
			List<MenuItemSelectable> selectedItems = new ArrayList<>();
			// Unfortunately, we need to recurse through all the menu/sub-menus
			findSelections(request, this, selectedItems);
			setSelectedMenuItems(selectedItems);
		}
	}

	/**
	 * Determine if this WMenu is on the Request.
	 *
	 * @param request the request being responded to.
	 * @return true if this WMenu is on the Request, otherwise return false.
	 */
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId() + "-h") != null;
	}

	/**
	 * Finds the selected items in a menu for a request.
	 *
	 * @param request the request being handled
	 * @param selectContainer the menu or sub-menu
	 * @param selections the current set of selections
	 */
	private void findSelections(final Request request, final MenuSelectContainer selectContainer,
			final List<MenuItemSelectable> selections) {

		// Don't bother checking disabled or invisible containers
		if (!selectContainer.isVisible()
				|| (selectContainer instanceof Disableable && ((Disableable) selectContainer).isDisabled())) {
			return;
		}

		// Get any selectable children of this container
		List<MenuItemSelectable> selectableItems = getSelectableItems(selectContainer);

		// Now add the selections (if in the request)
		for (MenuItemSelectable selectableItem : selectableItems) {
			// Check if the item is on the request
			if (request.getParameter(selectableItem.getId() + ".selected") != null) {
				selections.add(selectableItem);
				if (SelectionMode.SINGLE.equals(selectContainer.getSelectionMode())) {
					// Only select the first item at this level.
					// We still need to check other levels of the menu for selection.
					break;
				}
			}
		}

		// We need to recurse through and check for other selectable containers
		for (MenuItem item : selectContainer.getMenuItems()) {
			if (item instanceof MenuItemGroup) {
				for (MenuItem groupItem : ((MenuItemGroup) item).getMenuItems()) {
					if (groupItem instanceof MenuSelectContainer) {
						findSelections(request, (MenuSelectContainer) groupItem, selections);
					}
				}
			} else if (item instanceof MenuSelectContainer) {
				findSelections(request, (MenuSelectContainer) item, selections);
			}
		}
	}

	/**
	 * Retrieves the selectable items for the given container.
	 *
	 * @param selectContainer the component to search within.
	 * @return the list of selectable items for the given component. May be empty.
	 */
	private List<MenuItemSelectable> getSelectableItems(final MenuSelectContainer selectContainer) {
		List<MenuItemSelectable> result = new ArrayList<>(selectContainer.getMenuItems().size());

		SelectionMode selectionMode = selectContainer.getSelectionMode();

		for (MenuItem item : selectContainer.getMenuItems()) {
			if (item instanceof MenuItemGroup) {
				for (MenuItem groupItem : ((MenuItemGroup) item).getMenuItems()) {
					if (isSelectable(groupItem, selectionMode)) {
						result.add((MenuItemSelectable) groupItem);
					}
				}
			} else if (isSelectable(item, selectionMode)) {
				result.add((MenuItemSelectable) item);
			}
		}

		return result;
	}

	/**
	 * Indicates whether the given menu item is selectable.
	 *
	 * @param item the menu item to check.
	 * @param selectionMode the select mode of the current menu/sub-menu
	 * @return true if the meu item is selectable, false otherwise.
	 */
	private boolean isSelectable(final MenuItem item, final SelectionMode selectionMode) {

		if (!(item instanceof MenuItemSelectable) || !item.isVisible()
				|| (item instanceof Disableable && ((Disableable) item).isDisabled())) {
			return false;
		}

		// SubMenus are only selectable in a column menu type
		if (item instanceof WSubMenu && !MenuType.COLUMN.equals(getType())) {
			return false;
		}

		// Item is specificially set to selectable/unselectable
		Boolean itemSelectable = ((MenuItemSelectable) item).getSelectability();
		if (itemSelectable != null) {
			return itemSelectable;
		}

		// Container has selection turned on
		return SelectionMode.SINGLE.equals(selectionMode) || SelectionMode.MULTIPLE.equals(selectionMode);

	}

	/**
	 * @param items the list of menu items
	 * @param recurse true if recurse into child items that are menu containers
	 * @param container the current container
	 */
	private void getChildMenuItems(final List<MenuItem> items, final boolean recurse, final MenuContainer container) {

		for (MenuItem item : container.getMenuItems()) {
			items.add(item);
			if (recurse && item instanceof MenuContainer) {
				getChildMenuItems(items, recurse, (MenuContainer) item);
			}
		}
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
	 * @return a new MenuModel.
	 */
	@Override
	protected MenuModel newComponentModel() {
		return new MenuModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MenuModel getComponentModel() {
		return (MenuModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MenuModel getOrCreateComponentModel() {
		return (MenuModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the state information for a WMenu.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class MenuModel extends ComponentModel {

		/**
		 * The list of selected items.
		 */
		private List<MenuItemSelectable> selectedMenuItems;

		/**
		 * The select mode of the menu.
		 */
		private SelectionMode selectionMode = SelectionMode.NONE;

		/**
		 * The number of rows to display for a column menu.
		 */
		private int rows;

		/**
		 * The margins to be used on the menu.
		 */
		private Margin margin;
	}

}
