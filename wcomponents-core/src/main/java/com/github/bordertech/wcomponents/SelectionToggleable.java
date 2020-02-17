package com.github.bordertech.wcomponents;

/**
 * Components that have a selection which can be toggled by {@link WSelectToggle}
 * must implement this interface.
 */
@FunctionalInterface
public interface SelectionToggleable {

	/**
	 * Sets the selections for this component.
	 *
	 * @param selected if true, select everything. If false, deselect everything.
	 */
	void toggleSelection(boolean selected);
	
}
