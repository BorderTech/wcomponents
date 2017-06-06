package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.MemoryUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is used to group multiple {@link WCollapsible} or [@link WTabSet} components and one {@link WCollapsibleToggle} component
 * together. Each collapsible component in the same group has the same name.</p>
 *
 * <p>
 * The <code>WCollapsibleToggle</code> component of the group applies to only the <code>collapsible</code> components in
 * the same group. A WTabSet of type accordion is considered a collapsible component <strong>unless its single property is set</strong>.
 * </p>
 *
 * @author Christina Harris
 * @author Mark Reeves
 * @since 1.0.0
 */
public class CollapsibleGroup implements Serializable {

	/**
	 * The version Id for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of collapsibles in this group.
	 */
	private final List<WComponent> collapsibleList = new ArrayList<>();

	/**
	 * The Collapsible toggle for this group.
	 */
	private WCollapsibleToggle collapsibleToggle;

	/**
	 * Retrieves the common name used by all collapsible components in the group.
	 *
	 * @return the common name to use.
	 */
	public String getGroupName() {
		if (collapsibleToggle != null) {
			return collapsibleToggle.getId();
		} else if (!collapsibleList.isEmpty()) {
			// This is only to retain compatibility with the
			// previous implementation, and should be removed post-Sfp11,
			// as it doesn't make much sense to create a group without
			// something to toggle it.
			return collapsibleList.get(0).getId();
		}

		return "";
	}

	/**
	 * Adds a collpsible component to this group.
	 *
	 * @param collapsible the collapsible to add.
	 */
	public void addCollapsible(final WCollapsible collapsible) {
		addComponent(collapsible);
	}

	/**
	 * Adds a {@link WTabSet} to this group. Only really useful for accordions.
	 * @param collapsible the WTabSet to add to the group.
	 */
	public void addCollapsible(final WTabSet collapsible) {
		addComponent(collapsible);
	}

	/**
	 * Responsible for updating the underlying group store.
	 * @param component The component to add to the group.
	 */
	private void addComponent(final WComponent component) {
		collapsibleList.add(component);
		MemoryUtil.checkSize(collapsibleList.size(), this.getClass().getSimpleName());
	}

	/**
	 * @return a list of all collapsible components for this group.
	 */
	public List<WComponent> getAllCollapsibles() {
		return Collections.unmodifiableList(collapsibleList);
	}

	/**
	 * Sets the toggle component for this group.
	 *
	 * @param collapsibleToggle the toggle to set.
	 */
	public void setCollapsibleToggle(final WCollapsibleToggle collapsibleToggle) {
		this.collapsibleToggle = collapsibleToggle;
	}

	/**
	 * @return the toggle component for this group.
	 */
	public WCollapsibleToggle getCollapsibleToggle() {
		return collapsibleToggle;
	}
}
