package com.github.bordertech.wcomponents;

import java.util.List;

/**
 * <p>
 * The interface for WComponents which can contain child WComponents.
 * </p>
 * <p>
 * A basic Container is often used to group related components together. Grouping components together is good for
 * readability of the code and it is also how you create reusable chunks of UI.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface Container extends WComponent {

	/**
	 * @return the number of child components currently contained within this component.
	 */
	int getChildCount();

	/**
	 * Retrieves a child component by its index.
	 *
	 * @param index the index of the child component to be retrieved.
	 * @return the child component at the given index.
	 */
	WComponent getChildAt(int index);

	/**
	 * Retrieves the index of the given child.
	 *
	 * @param childComponent the child component to retrieve the index for.
	 * @return the index of the given child component, or -1 if the component is not a child of this component.
	 */
	int getIndexOfChild(WComponent childComponent);

	/**
	 * Retrieves a list of this Container's.
	 *
         * @return an immutable list of this Container.
	 */
	List<WComponent> getChildren();
}
