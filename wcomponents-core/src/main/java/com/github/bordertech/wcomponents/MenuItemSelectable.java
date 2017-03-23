package com.github.bordertech.wcomponents;

/**
 * Indicates a menu item can be selected.
 *
 * @author Jonathan Austin
 * @since 1.0.2
 */
public interface MenuItemSelectable extends MenuItem {

	/**
	 * Symmetric accessor which should not generally be used. If you need to know if an instance of MenuItemSelectable is
	 * selectable then it is better to use {@link #isSelectAllowed()}.
	 * @return true if this item is selectable, false if not, or null if default to its container.
	 */
	Boolean getSelectability();

	/**
	 * @param selectability true if this item is selectable, false if not, or null to default to the container.
	 */
	void setSelectability(final Boolean selectability);

	/**
	 * @return true if selected, otherwise false
	 */
	boolean isSelected();

	/**
	 * @return {@code true} if the menu item may be selected.
	 */
	boolean isSelectAllowed();
}
