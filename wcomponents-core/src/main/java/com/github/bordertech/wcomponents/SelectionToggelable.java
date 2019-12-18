package com.github.bordertech.wcomponents;

/**
 * Components that have a selection which can be toggled to all selected or to 
 * all unselected, can implement this interface.
 */
public interface SelectionToggelable {

	/**
	 * Sets the selections for this component.
	 *
	 * @param selected if true, select everything. If false, deselect everything.
	 */
	void toggleSelection(boolean selected);
	
}
