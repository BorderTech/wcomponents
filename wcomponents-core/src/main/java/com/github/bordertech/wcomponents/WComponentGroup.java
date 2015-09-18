package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Define a group of components that can be used or controlled by other components like {@link WSubordinateControl}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 *
 * @param <T> the type of component which the group contains.
 */
public class WComponentGroup<T extends WComponent> extends AbstractWComponent implements
		SubordinateTarget {

	/**
	 * Add a component to this group.
	 *
	 * @param component the component to add.
	 */
	public void addToGroup(final T component) {
		getOrCreateComponentModel().components.add(component);
	}

	/**
	 * Remove a component from this group.
	 *
	 * @param component the component to add.
	 */
	public void removeFromGroup(final T component) {
		getOrCreateComponentModel().components.remove(component);
	}

	/**
	 * @return the list of WComponents in this group.
	 */
	public List<T> getComponents() {
		return Collections.unmodifiableList((List<T>) getComponentModel().components);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		List<T> components = getComponents();

		for (int i = 0; i < components.size(); i++) {
			if (i > 0) {
				buf.append(", ");
			}

			T component = components.get(i);
			buf.append(component == null ? "null" : component.getClass().getSimpleName());
		}

		buf.append(']');

		return toString(buf.toString());
	}

	// ---------------------------------------------------------------------------
	// Extrinsic state management
	// ---------------------------------------------------------------------------
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ComponentGroupModel newComponentModel() {
		return new ComponentGroupModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ComponentGroupModel getComponentModel() {
		return (ComponentGroupModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ComponentGroupModel getOrCreateComponentModel() {
		return (ComponentGroupModel) super.getOrCreateComponentModel();
	}

	/**
	 * A class used to hold the list of components.
	 *
	 * @author Jonathan Austin
	 */
	public static class ComponentGroupModel extends ComponentModel {

		/**
		 * The list of WComponents in this group.
		 */
		private final List<WComponent> components = new ArrayList<>();
	}
}
