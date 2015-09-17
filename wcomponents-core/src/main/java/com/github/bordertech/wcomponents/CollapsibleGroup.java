package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is used to group multiple {@link WCollapsible} components and one {@link WCollapsibleToggle} component
 * together. Each collapsible component in the same group has the same name.</p>
 *
 * <p>
 * The <code>WCollapsibleToggle</code> component of the group applies to only the <code>collapsible</code> components in
 * the same group.</p>
 *
 * @author Christina Harris
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
	private final List<WCollapsible> collapsibleList = new ArrayList<>();

	/**
	 * The Collapsbile toggle for this group.
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
		collapsibleList.add(collapsible);
	}

	/**
	 * @return a list of all collapsible components for this group.
	 */
	public List<WCollapsible> getAllCollapsibles() {
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
