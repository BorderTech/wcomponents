package com.github.bordertech.wcomponents;

/**
 * Indicates a menu item can be selected.
 *
 * @author Jonathan Austin
 * @since 1.0.2
 */
public interface MenuItemSelectable extends MenuItem {

	/**
	 * @return true if this item is selectable, false if not, or null if default to its container.
	 */
	Boolean isSelectable();

	/**
	 * @param selectable true if this item is selectable, false if not, or null to default to the container.
	 */
	void setSelectable(final Boolean selectable);

	/**
	 * @return true if selected, otherwise false
	 */
	boolean isSelected();
}
