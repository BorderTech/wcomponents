package com.github.bordertech.wcomponents;

/**
 * Indicates a menu container can have its menu items selected.
 *
 * @author Jonathan Austin
 * @since 1.0.2
 */
public interface MenuSelectContainer extends MenuContainer {

	/**
	 * The available types of selection mode for the items in a menu.
	 */
	enum SelectionMode {
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
	 * @return the selection mode of the container
	 */
	SelectionMode getSelectionMode();

	/**
	 * @param selectionMode the selection mode for the items in this menu container.
	 */
	void setSelectionMode(final SelectionMode selectionMode);

}
