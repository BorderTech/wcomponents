package com.github.bordertech.wcomponents;

/**
 * The interface for Containers which can have content added/removed.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface MutableContainer extends NamingContextable {

	/**
	 * Adds the given component as a "shared" child of this component.
	 *
	 * @param component the component to add.
	 */
	void add(WComponent component);

	/**
	 * Removes the given component from this components "shared" list of children.
	 *
	 * @param aChild the child component to remove
	 */
	void remove(WComponent aChild);

	/**
	 * Removes all the "shared" children from this component.
	 */
	void removeAll();
}
