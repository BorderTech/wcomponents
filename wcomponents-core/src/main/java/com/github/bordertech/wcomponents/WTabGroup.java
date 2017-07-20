package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WTabSet.TabMode;

/**
 * WTabGroup encapsulates a related group of tabs.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated No replacement: must not be used as causes accessibility failure.
 */
public class WTabGroup extends AbstractContainer implements Disableable {

	/**
	 * Creates a WTabGroup.
	 *
	 * @param groupName the tab group name.
	 */
	public WTabGroup(final String groupName) {
		// NO OP
	}

	/**
	 * Creates a WTabGroup.
	 *
	 * @param label the tab group label.
	 */
	public WTabGroup(final WDecoratedLabel label) {
		// NO OP
	}

	/**
	 * Adds a separator to the tab group.
	 *
	 * @deprecated No separators in TabGroups as it does not conform with a11y requirements.
	 */
	@Deprecated
	public void addSeparator() {
		// NO OP
	}

	/**
	 * Indicates whether the WTabGroup is disabled.
	 *
	 * @return true if the input is disabled, otherwise false.
	 * @deprecated TabGroups do not exist and therefore cannot be disabled.
	 */
	@Deprecated
	@Override
	public boolean isDisabled() {
		return false;
	}

	/**
	 * Sets whether the WTabSet is disabled.
	 *
	 * @param disabled if true, the input is disabled. If false, it is enabled.
	 * @deprecated TabGroups do not exist and therefore cannot be disabled.
	 */
	@Deprecated
	@Override
	public void setDisabled(final boolean disabled) {
		// No-Op
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param content the tab set content.
	 * @param tabName the tab name.
	 * @param mode the tab mode.
	 * @return the tab which was added to the group.
	 */
	public WTab addTab(final WComponent content, final String tabName, final TabMode mode) {
		WTab tab = new WTab(content, tabName, mode);
		add(tab);

		return tab;
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param content the tab set content.
	 * @param tabName the tab name.
	 * @param mode the tab mode.
	 * @param accessKey the access key used to activate the tab.
	 * @return the tab which was added to the group.
	 */
	public WTab addTab(final WComponent content, final String tabName, final TabMode mode,
			final char accessKey) {
		WTab tab = new WTab(content, tabName, mode, accessKey);
		add(tab);

		return tab;
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param content the tab set content.
	 * @param label the tab's label, which can contain rich content (images or other components).
	 * @param mode the tab mode.
	 * @return the tab which was added to the group.
	 */
	public WTab addTab(final WComponent content, final WDecoratedLabel label, final TabMode mode) {
		WTab tab = new WTab(content, label, mode);
		add(tab);

		return tab;
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param content the tab set content.
	 * @param label the tab's label, which can contain rich content (images or other components).
	 * @param mode the tab mode.
	 * @param accessKey the access key used to activate the tab.
	 * @return the tab which was added to the group.
	 */
	public WTab addTab(final WComponent content, final WDecoratedLabel label, final TabMode mode,
			final char accessKey) {
		WTab tab = new WTab(content, label, mode, accessKey);
		add(tab);

		return tab;
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param tab the tab to add.
	 * @deprecated use e.g.
	 * {@link #addTab(com.github.bordertech.wcomponents.WComponent, java.lang.String, com.github.bordertech.wcomponents.WTabSet.TabMode)}
	 */
	@Deprecated
	public void add(final WTab tab) {
		super.add(tab);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public void remove(final WComponent child) {
		super.remove(child);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = ((WDecoratedLabel) getChildAt(0)).getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, 1, getChildCount() - 1);
	}
}
