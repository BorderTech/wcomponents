package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This component provides a logical grouping of related menu items. The rendered version in the UI usually provides a
 * visual grouping as well, including the menu item group's title.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 *
 * @deprecated menu groups are not compatible with WCAG 2.0.
 */
@Deprecated
public class WMenuItemGroup extends AbstractContainer implements Disableable, MenuItemGroup {

	/**
	 * Menu item group label.
	 */
	private final WDecoratedLabel label;

	/**
	 * Menu group items.
	 */
	private final WContainer content = new WContainer();

	/**
	 * Creates a new WMenuItem containing the specified button.
	 *
	 * @param headingText the heading text for the group.
	 */
	public WMenuItemGroup(final String headingText) {
		this(new WDecoratedLabel(headingText));
	}

	/**
	 * Creates a new WMenuItem containing the specified button.
	 *
	 * @param label the label for the group.
	 */
	public WMenuItemGroup(final WDecoratedLabel label) {
		this.label = label;
		add(label);
		add(content);
	}

	/**
	 * @return the decorated label for this menu item group
	 */
	public WDecoratedLabel getDecoratedLabel() {
		return label;
	}

	/**
	 * @return returns the group heading text.
	 */
	public String getHeadingText() {
		return getDecoratedLabel().getText();
	}

	/**
	 * Sets the group heading text.
	 *
	 * @param headingText the heading to set.
	 */
	public void setHeadingText(final String headingText) {
		getDecoratedLabel().setText(headingText);
	}

	/**
	 * Indicates whether this group is disabled.
	 *
	 * @return true if this group is disabled.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this group is disabled.
	 *
	 * @param disabled true to disable the group, false to enable it.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * Adds a separator to this group.
	 */
	public void addSeparator() {
		addMenuItem(new WSeparator());
	}

	/**
	 * @param item add a {@link WSeparator}
	 */
	public void add(final WSeparator item) {
		addMenuItem(item);
	}

	/**
	 * @param item add a {@link WMenuItem}
	 */
	public void add(final WMenuItem item) {
		addMenuItem(item);
	}

	/**
	 * @param item add a {@link WSubMenu}
	 */
	public void add(final WSubMenu item) {
		addMenuItem(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMenuItem(final MenuItem item) {
		if (item instanceof MenuItemGroup) {
			throw new IllegalArgumentException("Cannot add a nested menu group to another menu group.");
		}
		getContent().add(item);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link #removeMenuItem(com.github.bordertech.wcomponents.MenuItem)} instead.
	 */
	@Deprecated
	@Override
	public void remove(final WComponent item) {
		if (item instanceof MenuItem) {
			removeMenuItem((MenuItem) item);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMenuItem(final MenuItem item) {
		getContent().remove(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllMenuItems() {
		getContent().removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MenuItem> getMenuItems() {
		List<MenuItem> items = new ArrayList(getContent().getChildren());
		return Collections.unmodifiableList(items);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getLabel().getText();
		text = text == null ? "null" : ('"' + text + '"');
		return getContent().toString(text, -1, -1);
	}

	/**
	 * @return the container holding the menu items
	 */
	private WContainer getContent() {
		return content;
	}

}
