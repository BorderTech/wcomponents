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
 * @since 1.0.0
 */
public class WMenu extends AbstractNamingContextContainer implements Disableable, AjaxTarget,
		Marginable {

	/**
	 * The available types of client-side menus.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static enum MenuType {
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
	 */
	public static enum SelectMode {
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
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * @return the select mode for the items in this subMenu.
	 */
	public SelectMode getSelectMode() {
		return getComponentModel().selectMode;
	}

	/**
	 * @param selectMode the select mode for the items in this subMenu.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		getOrCreateComponentModel().selectMode = selectMode;
	}

	/**
	 * Adds a separator to the end of the menu.
	 */
	public void addSeparator() {
		add(new WSeparator());
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
	 * Adds the given menu item to this component.
	 *
	 * @param item the item to add.
	 */
	public void add(final WMenuItem item) {
		super.add(item);
	}

	/**
	 * Adds the given sub-menu as a child of this component.
	 *
	 * @param item the sub-menu to add.
	 */
	public void add(final WSubMenu item) {
		super.add(item);
	}

	/**
	 * Adds the given group as a child of this component.
	 *
	 * @param item group the group to add.
	 */
	public void add(final WMenuItemGroup item) {
		super.add(item);
	}

	/**
	 * Adds the given separator as a child of this component.
	 *
	 * @param separator the separator to add.
	 */
	public void add(final WSeparator separator) {
		super.add(separator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public void remove(final WComponent child) {
		super.remove(child);
	}

	/**
	 * Returns the selected item (WMenUItem/WSubMenu, depending on the menu type) in the given context.
	 *
	 * @return the selected item, or null if no item has been selected.
	 */
	public WComponent getSelectedItem() {
		List<WComponent> selectedItems = getSelectedItems();
		if (selectedItems.isEmpty()) {
			return null;
		} else {
			return selectedItems.get(0);
		}
	}

	/**
	 * Returns the selected items (WMenUItems/WSubMenus, depending on the menu type) in the given context.
	 *
	 * @return the selected items, or an empty list if nothing is selected.
	 */
	public List<WComponent> getSelectedItems() {
		List<WComponent> selectedItems = getComponentModel().selectedItems;

		if (selectedItems == null) {
			selectedItems = Collections.emptyList();
		} else {
			selectedItems = Collections.unmodifiableList(selectedItems);
		}

		return selectedItems;
	}

	/**
	 * Sets the selected items (WMenuItems or WSubMenus, depending on the menu type).
	 *
	 * @param selectedItems the selected items.
	 */
	public void setSelectedItems(final List<WComponent> selectedItems) {
		MenuModel model = getOrCreateComponentModel();
		model.selectedItems = new ArrayList<>(selectedItems);
	}

	/**
	 * Clears an existing list of selected items.
	 */
	public void clearSelectedItems() {
		MenuModel model = getOrCreateComponentModel();
		model.selectedItems = null;
	}

	/**
	 * Sets the selected item (WMenuItem and/or WSubMenu, depending on the menu type).
	 *
	 * @param selectedItem the selected item.
	 */
	public void setSelectedItem(final WComponent selectedItem) {
		MenuModel model = getOrCreateComponentModel();

		if (model.selectedItems == null) {
			model.selectedItems = new ArrayList<>();
		}

		model.selectedItems.clear();
		model.selectedItems.add(selectedItem);
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
			List<WComponent> selections = new ArrayList<>();
			// Unfortunately, we need to recurse through all the menu/sub-menus
			findSelections(request, this, selections);
			setSelectedItems(selections);
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
	 * @param component the menu or sub-menu
	 * @param selections the current set of selections
	 */
	private void findSelections(final Request request, final Container component,
			final List<WComponent> selections) {
		// Don't bother checking disabled or invisible components
		if (!component.isVisible()
				|| (component instanceof Disableable && ((Disableable) component).isDisabled())) {
			return;
		}

		final SelectMode selectMode;
		if (component instanceof WMenu) {
			selectMode = ((WMenu) component).getSelectMode();
		} else {
			selectMode = ((WSubMenu) component).getSelectMode();
		}

		// Get any selectable children of this menu/sub-menu
		List<WComponent> selectableChildren = getSelectableChildren(component, selectMode);

		// Now add the selections
		if (selectableChildren != null) {
			for (WComponent selectableItem : selectableChildren) {
				if (request.getParameter(selectableItem.getId() + ".selected") != null) {
					selections.add(selectableItem);

					if (SelectMode.SINGLE.equals(selectMode)) {
						// Only select the first item at this level.
						// We still need to check other levels of the menu for selection.
						break;
					}
				}
			}
		}

		// We need to recurse through any sub-menus in this menu/sub-menu
		final int childCount = component.getChildCount();

		for (int i = 0; i < childCount; i++) {
			WComponent child = component.getChildAt(i);

			if (child instanceof WMenuItemGroup) {
				WMenuItemGroup group = (WMenuItemGroup) child;
				final int groupChildCount = group.getChildCount();

				for (int j = 0; j < groupChildCount; j++) {
					if (group.getChildAt(j) instanceof WSubMenu) {
						findSelections(request, (WSubMenu) group.getChildAt(j), selections);
					}
				}
			} else if (child instanceof WSubMenu) {
				findSelections(request, (WSubMenu) child, selections);
			}
		}
	}

	/**
	 * Retrieves the selectable children for the given component.
	 *
	 * @param parent the component to search within.
	 * @param parentSelectMode the select mode of the current menu/sub-menu
	 * @return the list of selectable children for the given component. May be empty.
	 */
	private List<WComponent> getSelectableChildren(final Container parent,
			final SelectMode parentSelectMode) {
		List<WComponent> result = new ArrayList<>(parent.getChildCount());

		for (int i = 0; i < parent.getChildCount(); i++) {
			WComponent child = parent.getChildAt(i);

			if (child instanceof WMenuItemGroup) {
				WMenuItemGroup group = (WMenuItemGroup) child;

				// Grouping doesn't affect selectability.
				// Groups can not be nested, so just loop through the group's children.
				for (int j = 0; j < group.getChildCount(); j++) {
					WComponent groupedChild = group.getChildAt(j);

					if (isSelectable(groupedChild, parentSelectMode)) {
						result.add(groupedChild);
					}
				}
			} else if (isSelectable(child, parentSelectMode)) {
				result.add(child);
			}
		}

		return result;
	}

	/**
	 * Indicates whether the given component is selectable.
	 *
	 * @param component the component to check.
	 * @param parentSelectMode the select mode of the current menu/sub-menu
	 * @return true if the component is selectable, false otherwise.
	 */
	private boolean isSelectable(final WComponent component, final SelectMode parentSelectMode) {
		if (!component.isVisible()
				|| (component instanceof Disableable && ((Disableable) component).isDisabled())) {
			return false;
		}

		boolean parentSupportsSelection = SelectMode.SINGLE.equals(parentSelectMode)
				|| SelectMode.MULTIPLE.equals(parentSelectMode);

		if (component instanceof WMenuItem) {
			WMenuItem menuItem = (WMenuItem) component;
			Boolean itemSelectable = menuItem.isSelectable();

			return Boolean.TRUE.equals(itemSelectable)
					|| (parentSupportsSelection && !Boolean.FALSE.equals(itemSelectable));
		} else if (component instanceof WSubMenu && MenuType.COLUMN.equals(type)) { // sub-menus are only selectable in a column menu
			WSubMenu subMenu = (WSubMenu) component;
			Boolean itemSelectable = subMenu.isSelectable();

			return Boolean.TRUE.equals(itemSelectable)
					|| (parentSupportsSelection && !Boolean.FALSE.equals(itemSelectable));
		}

		return false;
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
		private List<WComponent> selectedItems;

		/**
		 * The select mode of the menu.
		 */
		private SelectMode selectMode = SelectMode.NONE;

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
