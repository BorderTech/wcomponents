package com.github.bordertech.wcomponents;

/**
 * This abstract class extends AbstractContainer and implements the MutableContainer interface to expose methods for
 * modifying the contents of a container. This class is extended by other container classes which allow arbitrary
 * content.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class AbstractMutableContainer extends AbstractNamingContextContainer implements
		MutableContainer {

	/**
	 * Adds the given component as a child of this component.
	 *
	 * @param component the component to add.
	 */
	@Override
	public void add(final WComponent component) {
		super.add(component);
	}

	/**
	 * Add the given component as a child of this component. The tag is used to identify the child in this component's
	 * velocity template.
	 *
	 * @param component the component to add.
	 * @param tag the tag used to identify the component.
	 * @deprecated Use {@link WTemplate} instead
	 */
	@Deprecated
	@Override
	public void add(final WComponent component, final String tag) {
		super.add(component, tag);
	}

	/**
	 * Removes the given component from this component.
	 *
	 * @param aChild the child component to remove
	 */
	@Override
	public void remove(final WComponent aChild) {
		super.remove(aChild);
	}

	/**
	 * Removes all the child components from this component.
	 */
	@Override
	public void removeAll() {
		super.removeAll();
	}
}
