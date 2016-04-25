package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This component enables rendering of a set of tabbed components.
 * </p>
 *
 * <p>
 * A <code>WTabSet</code> WComponent has the following functionality:
 * </p>
 * <ul>
 * <li>The ability to render as different types of client-side tab controls.</li>
 * <li>The ability to process each tab control on the client side (via JavaScript) or Server side processing.</li>
 * <li> The ability to set the active tab(s). By default it is the first in the list and can be set via the following
 * methods:
 * <ul>
 * <li>{@link #setActiveIndex(int)} to set the active tab as the index corresponding to the order of tabs at
 * construction time.</li>
 * <li>{@link #setActiveTab(WComponent)} to set the active tab that matches the WComponent supplied at construction time
 * (see {@link #addTab(WComponent, String, TabMode)}).</li>
 * </ul>
 * </li>
 * </ul>
 *
 * <p>
 * <b>NOTE:</b> When setting the tab set type to be LEFT or RIGHT you should make use of the method
 * {@link #setContentHeight(String)} to provide a sensible default height for the tab set. Otherwise, it will default to
 * the height of one tab.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTabSet extends AbstractNamingContextContainer implements Disableable, AjaxTarget,
		Marginable, SubordinateTarget {

	/**
	 * The available types of client-side tab sets.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum TabSetType {
		/**
		 * Displays the TabSet tabs horizontally, above the tab content area.
		 */
		TOP,
		/**
		 * Displays the TabSet tabs vertically, to the left of the tab content area.
		 */
		LEFT,
		/**
		 * Displays the TabSet tabs vertically, to the right of the tab content area.
		 */
		RIGHT,
		/**
		 * A special TabSet display, where Tabs and their content are interleaved vertically.
		 */
		ACCORDION,
		/**
		 * A styled version of the Left TabSet, where tabs do not contain any content.
		 */
		APPLICATION
	};

	/**
	 * The available types of tab invocation.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum TabMode {
		/**
		 * Indicates that a round-trip should be made whenever the tab is selected.
		 *
		 * @deprecated Use TabMode DYNAMIC instead as a like-for-like replacement or any other mode if it is more
		 * appropriate to the individual use case.
		 */
		SERVER,
		/**
		 * Indicates that an ajax request should be made the first time the tab is selected.
		 */
		LAZY,
		/**
		 * Indicates that the tab content is always sent to the client.
		 */
		CLIENT,
		/**
		 * Indicates that an ajax request should be made whenever the tab is selected.
		 */
		DYNAMIC,
		/**
		 * Indicates that an ajax request should be made immediately after the page is loaded.
		 */
		EAGER
	};

	/**
	 * A tab-set where tab buttons are placed above the content.
	 */
	public static final TabSetType TYPE_TOP = TabSetType.TOP;
	/**
	 * A tab-set where tab buttons are placed to the left of the content.
	 */
	public static final TabSetType TYPE_LEFT = TabSetType.LEFT;
	/**
	 * A tab-set where tab buttons are placed to the right of the content.
	 */
	public static final TabSetType TYPE_RIGHT = TabSetType.RIGHT;
	/**
	 * An "accordion" type tab-set, that supports having multiple open tabs.
	 */
	public static final TabSetType TYPE_ACCORDION = TabSetType.ACCORDION;
	/**
	 * An "application" type tab-set, that supports having multiple open tabs.
	 */
	public static final TabSetType TYPE_APPLICATION = TabSetType.APPLICATION;

	/**
	 * A tab mode where invoking the tab will always perform a round-trip to the server.
	 *
	 * @deprecated Use TAB_MODE_DYNAMIC instead as a like-for-like replacement or any other mode if it is more
	 * appropriate to the individual use case.
	 */
	@Deprecated
	public static final TabMode TAB_MODE_SERVER = TabMode.SERVER;
	/**
	 * A tab mode where invoking the tab will perform an ajax request the first time the tab is requested.
	 */
	public static final TabMode TAB_MODE_LAZY = TabMode.LAZY;
	/**
	 * A tab mode where tab content is always rendered, and invoking the tab only results in a client-side switch.
	 */
	public static final TabMode TAB_MODE_CLIENT = TabMode.CLIENT;
	/**
	 * A tab mode where invoking the tab will perform an ajax request every time the tab is requested.
	 */
	public static final TabMode TAB_MODE_DYNAMIC = TabMode.DYNAMIC;
	/**
	 * A tab mode where invoking the tab will perform an ajax request immediately after the page is loaded.
	 */
	public static final TabMode TAB_MODE_EAGER = TabMode.EAGER;

	/**
	 * Creates a WTabSet with the tabs positioned on the top.
	 */
	public WTabSet() {
		this(TYPE_TOP);
	}

	/**
	 * Creates a WTabSet of the given type.
	 *
	 * @param type the tab set type.
	 */
	public WTabSet(final TabSetType type) {
		getComponentModel().type = type;
	}

	/**
	 * @return the tab set type.
	 */
	public TabSetType getType() {
		return getComponentModel().type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * Sets the content height of the tab set. This is primarily used for tab sets where the tabs are positioned on the
	 * left or right, to limit the height of the tab set.
	 *
	 * @param contentHeight the content height, measured in a valid CSS unit, e.g. "10em".
	 */
	public void setContentHeight(final String contentHeight) {
		getOrCreateComponentModel().contentHeight = contentHeight;
	}

	/**
	 * @return the tab set's content height, if set, otherwise null.
	 */
	public String getContentHeight() {
		return getComponentModel().contentHeight;
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param content the tab set content.
	 * @param tabName the tab name.
	 * @param mode the tab mode.
	 * @return the tab which was added to the tab set.
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
	 * @return the tab which was added to the tab set.
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
	 * @return the tab which was added to the tab set.
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
	 * @return the tab which was added to the tab set.
	 */
	public WTab addTab(final WComponent content, final WDecoratedLabel label, final TabMode mode,
			final char accessKey) {
		WTab tab = new WTab(content, label, mode, accessKey);
		add(tab);

		return tab;
	}

	/**
	 * Adds a separator to the tab set.
	 */
	public void addSeparator() {
		add(new WSeparator());
	}

	/**
	 * Adds a tab to the tab set.
	 *
	 * @param tab the tab to add.
	 * @deprecated use e.g. {@link #addTab(WComponent, String, TabMode)}
	 */
	@Deprecated
	public void add(final WTab tab) {
		super.add(tab);
	}

	/**
	 * Adds a tab group to the tab set.
	 *
	 * @param group the group to add.
	 */
	public void add(final WTabGroup group) {
		super.add(group);
	}

	/**
	 * Adds a separator to the tab set.
	 *
	 * @param separator the separator to add.
	 */
	public void add(final WSeparator separator) {
		super.add(separator);
	}

	/**
	 * Retrieves the total number of tabs in this tab set.
	 *
	 * @return the number of tabs in this tab set.
	 */
	public int getTotalTabs() {
		return getTabs().size();
	}

	/**
	 * Returns the default active index. Note that some tab sets support multiple active tabs, see
	 * {@link #getActiveIndices()}.
	 * <p>
	 * If there are no active tabs, then the first tab will be returned as the default tab.
	 * </p>
	 *
	 * @return the default active tab index.
	 */
	public int getActiveIndex() {
		List<Integer> activeTabs = getActiveIndices();

		return activeTabs.isEmpty() ? 0 : activeTabs.get(0);
	}

	/**
	 * Returns the active indices (as seen by the given context/session).
	 *
	 * @return the active tab indices (may be an empty list).
	 */
	public List<Integer> getActiveIndices() {
		TabSetModel model = getOrCreateComponentModel(); // this model may be updated
		List<Integer> activeTabs = model.activeTabs;

		// Remove invisible tabs from the active tab list
		if (activeTabs != null) {
			for (Iterator<Integer> i = activeTabs.iterator(); i.hasNext();) {
				if (!isTabVisible(i.next())) {
					i.remove();
				}
			}

			if (activeTabs.isEmpty()) {
				activeTabs = null;
				model.activeTabs = null;
			}
		}

		if (activeTabs == null) {
			if (getTotalTabs() == 0) {
				return Collections.emptyList();
			} else {
				// If there are no active tabs, then the first visible tab will be returned as the active tab.
				int idx = findFirstVisibleTab();
				activeTabs = new ArrayList<>(1);
				activeTabs.add(idx);
			}
		}

		return Collections.unmodifiableList(activeTabs);
	}

	/**
	 * Returns the active tab (as seen by the given context/session). Note that some tab sets support multiple active
	 * tabs, see {@link #getActiveTabs()}.
	 *
	 * @return the active tab (as seen by the given context/session).
	 */
	public WTab getActiveTab() {
		List<WTab> activeTabs = getActiveTabs();

		return activeTabs.isEmpty() ? null : activeTabs.get(0);
	}

	/**
	 * Returns the active tabs (as seen by the given context/session).
	 *
	 * @return the active tabs (as seen by the given context/session).
	 */
	public List<WTab> getActiveTabs() {
		List<Integer> activeIndices = getActiveIndices();

		if (activeIndices.isEmpty()) {
			return Collections.emptyList();
		}

		List<WTab> activeTabs = new ArrayList<>(activeIndices.size());
		List<WTab> tabs = getTabs();

		for (int index : activeIndices) {
			activeTabs.add(tabs.get(index));
		}

		return Collections.unmodifiableList(activeTabs);
	}

	/**
	 * Sets the active index(as seen by the given context/session).
	 *
	 * @param activeIndex the index of the tab to mark as the active one.
	 */
	public void setActiveIndex(final int activeIndex) {
		setActiveIndices(new int[]{activeIndex});
	}

	/**
	 * Sets the active tab indices (as seen by the given context/session).
	 *
	 * @param indices the tab indices to set
	 */
	public void setActiveIndices(final int[] indices) {
		TabSetModel model = getOrCreateComponentModel();

		if (model.activeTabs == null) {
			model.activeTabs = new ArrayList<>(1);
		}

		model.activeTabs.clear();

		for (int index : indices) {
			model.activeTabs.add(index);
		}
	}

	/**
	 * Sets the active tab using tab content. TODO: this is stupid! setActiveTab should use the WTab not its content!
	 *
	 * @param content the active tab's content.
	 */
	public void setActiveTab(final WComponent content) {
		int index = getTabIndex(content);

		if (index != -1) {
			setActiveIndex(index);
		}
	}

	/**
	 * Sets the visibility of the tab at the given index.
	 *
	 * @param tabIndex the tab index.
	 * @param visible true to set the tab visible, false to set invisible.
	 */
	public void setTabVisible(final int tabIndex, final boolean visible) {
		getTab(tabIndex).setVisible(visible);
	}

	/**
	 * Sets the visibility of the tab which holds the given content.
	 *
	 * @param tabContent the tab content.
	 * @param visible true to set the tab visible, false to set invisible.
	 */
	public void setTabVisible(final WComponent tabContent, final boolean visible) {
		setTabVisible(getTabIndex(tabContent), visible);
	}

	/**
	 * Indicates whether the tab at the given index is visible.
	 *
	 * @param tabIndex the tab index.
	 * @return true if the tab at the given index is visible, false if it is invisible.
	 */
	public boolean isTabVisible(final int tabIndex) {
		WTab tab = getTab(tabIndex);
		Container tabParent = tab.getParent();

		if (tabParent instanceof WTabGroup) {
			return tab.isVisible() && tabParent.isVisible();
		} else {
			return tab.isVisible();
		}
	}

	/**
	 * Indicats whether the tab which holds the given content is visible.
	 *
	 * @param tabContent the tab content.
	 * @return true if the tab at the given index is visible, false if it is invisible.
	 */
	public boolean isTabVisible(final WComponent tabContent) {
		return isTabVisible(getTabIndex(tabContent));
	}

	/**
	 * Indicates whether the tab is active (selected).
	 *
	 * @param tab the WTab to check.
	 * @return true if the tab is active, false otherwise.
	 */
	public boolean isActive(final WComponent tab) {
		int activeIndex = getActiveIndex();
		List<WTab> tabs = getTabs();
		int tabIndex = tabs.indexOf(tab);

		return activeIndex == tabIndex;
	}

	/**
	 * Retrieves the tab index for the given tab content.
	 *
	 * @param content the tab content
	 * @return the tab index, or -1 if the content is not in a tab in this tab set.
	 */
	public int getTabIndex(final WComponent content) {
		List<WTab> tabs = getTabs();
		final int count = tabs.size();

		for (int i = 0; i < count; i++) {
			WTab tab = tabs.get(i);

			if (content == tab.getContent()) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Returns the tab at the given index. Bounds checking is not performed.
	 *
	 * @param index the tab index.
	 * @return the tab at the selected index.
	 */
	public WTab getTab(final int index) {
		return getTabs().get(index);
	}

	/**
	 * @return the list of tabs in this tabset
	 */
	private List<WTab> getTabs() {
		List<WTab> tabs = new ArrayList<>(getChildCount());
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			WComponent child = getChildAt(i);

			if (child instanceof WTab) {
				tabs.add((WTab) child);
			} else if (child instanceof WTabGroup) {
				final int groupChildCount = ((WTabGroup) child).getChildCount();

				for (int j = 0; j < groupChildCount; j++) {
					WComponent child2 = ((WTabGroup) child).getChildAt(j);

					if (child2 instanceof WTab) {
						tabs.add((WTab) child2);
					}
				}
			}
		}

		return tabs;
	}

	/**
	 * Override handleRequest in order to perform processing specific to this component.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (isDisabled()) {
			// Protect against client-side tampering of disabled/read-only fields.
			return;
		}

		// Remember which client side tab we were on.
		String[] indicesStr = request.getParameterValues(getId());

		if (indicesStr != null && indicesStr.length > 0) {
			List<Integer> oldIndices = getActiveIndices();
			List<Integer> changes = new ArrayList<>(1);
			int[] indices = new int[indicesStr.length];

			if (Util.empty(indicesStr[0])) {
				// Special case - no tab selected
				int idx = findFirstVisibleTab();
				indices = new int[idx];
			} else {
				// Normal case - one or more tabs selected
				for (int i = 0; i < indices.length; i++) {
					int clientIndex = Integer.parseInt(indicesStr[i]);
					int tabIndex = clientIndexToTabIndex(clientIndex);
					indices[i] = tabIndex;

					if (!oldIndices.contains(tabIndex)) {
						// Check for a server mode tab and set focus
						WTab tab = getTab(tabIndex);
						if (TabMode.SERVER == tab.getMode() && UIContextHolder.getCurrent().
								getFocussed() == null) {
							tab.setFocussed();
						}
						changes.add(tabIndex);
					}
				}
			}

			setActiveIndices(indices);

			// Invoke action if tab selection has changed
			final Action action = getActionOnChange();

			if (action != null && !changes.isEmpty()) {
				final ActionEvent event = new ActionEvent(this, changes.toString(), null);

				Runnable later = new Runnable() {
					@Override
					public void run() {
						action.execute(event);
					}
				};

				invokeLater(later);
			}
		}

		String showHeadOnlyStr = request.getParameter(getId() + ".showHeadOnly");

		if (showHeadOnlyStr != null) {
			boolean showHeadOnly = "true".equals(showHeadOnlyStr);
			setShowHeadOnly(showHeadOnly);
		}
	}

	/**
	 * The client-side tab indices will differ from the WTabSet's indices when one or more tabs is invisible.
	 *
	 * @param clientIndex the client-side index
	 * @return the WTabSet index corresponding to the given client index
	 */
	private int clientIndexToTabIndex(final int clientIndex) {
		int childCount = getTotalTabs();
		int serverIndex = clientIndex;

		for (int i = 0; i <= serverIndex && i < childCount; i++) {
			if (!isTabVisible(i)) {
				serverIndex++;
			}
		}

		return serverIndex;
	}

	/**
	 * Find the index of the first visible tab. If there are no visible tabs then return the first tab.
	 *
	 * @return the index of the first visible tab
	 */
	private int findFirstVisibleTab() {
		for (int i = 0; i < getTotalTabs(); i++) {
			if (isTabVisible(i)) {
				return i;
			}
		}

		// If there are no visible tabs, then return the first tab.
		return 0;
	}

	/**
	 * Indicates whether the WTabSet is disabled in the given context.
	 *
	 * @return true if the input is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the WTabSet is disabled.
	 *
	 * @param disabled if true, the input is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // to make public
	public void remove(final WComponent child) {
		super.remove(child);
	}

	/**
	 * Sets the action to be executed when the tab selection of this <code>tabset</code> changes.
	 *
	 * @param action the action to execute
	 */
	public void setActionOnChange(final Action action) {
		getOrCreateComponentModel().action = action;
	}

	/**
	 * Gets the action that is executed when the tab selection of this <code>tabset</code> changes.
	 *
	 * @return The <code>action</code> associated with this <code>tabset</code>.
	 */
	public Action getActionOnChange() {
		return getComponentModel().action;
	}

	/**
	 * Sets whether only the "head" portion of a tab's decorated label should be visible. At present, this only has a
	 * visible effect on {@link TabSetType#APPLICATION} tab sets.
	 *
	 * @param showHeadOnly true if only the "head" part of the tab label should be shown.
	 */
	public void setShowHeadOnly(final boolean showHeadOnly) {
		getOrCreateComponentModel().showHeadOnly = showHeadOnly;
	}

	/**
	 * Indicates whether only the "head" portion of a tab's decorated label should be visible. At present, this only has
	 * a visible effect on {@link TabSetType#APPLICATION} tab sets.
	 *
	 * @return true if only the "head" part of the tab label should be shown.
	 */
	public boolean isShowHeadOnly() {
		return getComponentModel().showHeadOnly;
	}

	/**
	 * @param single true if only one tab should be open at a time for an accordion tabset
	 */
	public void setSingle(final boolean single) {
		getOrCreateComponentModel().single = single;
	}

	/**
	 * @return true if only one tab should be open at a time for an accordion tabset
	 */
	public boolean isSingle() {
		return getComponentModel().single;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String details = "active tab=" + getActiveIndices();
		return toString(details, 1, 1);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new Component model.
	 *
	 * @return a new TabSetModel.
	 */
	@Override // For type safety only
	protected TabSetModel newComponentModel() {
		return new TabSetModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TabSetModel getComponentModel() {
		return (TabSetModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TabSetModel getOrCreateComponentModel() {
		return (TabSetModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WTabSet.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class TabSetModel extends ComponentModel {

		/**
		 * List of active tabs.
		 */
		private List<Integer> activeTabs;

		/**
		 * Show head only flag.
		 */
		private boolean showHeadOnly;

		/**
		 * The type of TabSet to render as.
		 */
		private TabSetType type;

		/**
		 * The content height, in e.g. EMs to use for e.g. left/right tab-set types.
		 */
		private String contentHeight;

		/**
		 * TabSet action on change.
		 */
		private Action action;

		/**
		 * The margins to be used on the section.
		 */
		private Margin margin;

		/**
		 * Accordion tab only opens one tab at a time.
		 */
		private boolean single;
	}
}
