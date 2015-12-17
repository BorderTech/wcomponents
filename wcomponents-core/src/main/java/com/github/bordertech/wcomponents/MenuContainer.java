package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * A component that can hold menu items.
 *
 * @author Jonathan Austin
 * @since 1.0.2
 */
public interface MenuContainer extends WComponent {

	/**
	 * @param item the menu item to add
	 */
	void addMenuItem(final MenuItem item);

	/**
	 * @param item the menu item to remove
	 */
	void removeMenuItem(final MenuItem item);

	/**
	 * Remove all menu items.
	 */
	void removeAllMenuItems();

	/**
	 * @return the items of this menu container
	 */
	List<MenuItem> getMenuItems();

}
