package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.List;

/**
 * This is component can be used to expand or collapse all collapsibles. This component can work as a server-side or a
 * client-side component. It can also belong to a {@link CollapsibleGroup}, in this case the toggle functionality will
 * apply to the {@link WCollapsible} components in that group only.
 *
 * @author Ming Gao
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsibleToggle extends AbstractWComponent implements AjaxTarget {

	/**
	 * Indicates whether processing will occur client-side (true) or server-side (false).
	 */
	private final boolean clientSide;

	/**
	 * Creates a client-side WCollapsibleToggle.
	 */
	public WCollapsibleToggle() {
		this(true);
	}

	/**
	 * Creates a WCollapsibleToggle.
	 *
	 * @param clientSide if true, the collapse/expand is handled client-side
	 */
	public WCollapsibleToggle(final boolean clientSide) {
		this.clientSide = clientSide;
	}

	/**
	 * Creates a WCollapsibleToggle for the given CollapsibleGroup.
	 *
	 * @param clientSide if true, the collapse/expand is handled client-side.
	 * @param group the CollapsibleGroup to create the toggle for.
	 */
	public WCollapsibleToggle(final boolean clientSide, final CollapsibleGroup group) {
		this(clientSide);
		setGroup(group);
	}

	/**
	 * Indicates whether processing will occur client-side.
	 *
	 * @return true if processing is handled client-side, or false for server-side.
	 */
	public boolean isClientSideToggleable() {
		return clientSide;
	}

	/**
	 * Retrieves the name of the {@link CollapsibleGroup} associated with this toggle. If no group has been associated,
	 * this component's name is returned.
	 *
	 * @return the group name.
	 */
	public String getGroupName() {
		CollapsibleGroup group = getGroup();
		return (group == null ? getId() : group.getGroupName());
	}

	/**
	 * Sets the collapsible group that this WCollapsibleToggle can expand/collapse.
	 *
	 * @param group the CollapsibleGroup to expand/collapse.
	 */
	public void setGroup(final CollapsibleGroup group) {
		getOrCreateComponentModel().group = group;
		group.setCollapsibleToggle(this);
	}

	/**
	 * @return the CollapsibleGroup that this WCollapsibleToggle can expand/collapse.
	 */
	public CollapsibleGroup getGroup() {
		return getComponentModel().group;
	}

	/**
	 * If not running client-side, it is WCollapsibleToggle's responsibility to expand/collapse each individual
	 * WCollapsible in the group.
	 *
	 * @param request the request being responded to
	 */
	@Override
	public void handleRequest(final Request request) {
		if (!isClientSideToggleable()) {
			String operation = request.getParameter(getId());
			boolean expand = "expand".equals(operation);
			final boolean collapse = "collapse".equals(operation);

			if (expand || collapse) {
				// We need to invoke a runnable later, as the expand/collapse needs
				// to occur after the collapsibles' handleRequest has been called.
				Runnable later = new Runnable() {
					@Override
					public void run() {
						CollapsibleGroup group = getGroup();
						UIContext uic = UIContextHolder.getCurrent();

						// if no group is defined then just find all the collapsibles in the ui
						List<WCollapsible> collapsibles = (group == null) ? findAllCollapsibles(
								uic.getUI(), new ArrayList<WCollapsible>()) : group.
								getAllCollapsibles();

						for (WCollapsible next : collapsibles) {
							setCollapsed(next, collapse);
						}

						if (uic.getFocussed() == null) {
							WCollapsibleToggle.this.setFocussed();
						}
					}
				};

				invokeLater(later);
			}
		}
	}

	/**
	 * Expands/collapses the given collapsibles, taking into account any repeaters present in the UI hierarchy.
	 *
	 * @param collapsible the collapsible whose state will be changed.
	 * @param collapsed true if the collapsible is to be collapsed, false if it is to be expanded.
	 */
	private static void setCollapsed(final WCollapsible collapsible, final boolean collapsed) {
		List<WRepeater> repeaters = new ArrayList<>();
		findRepeaters(collapsible, repeaters);
		setCollapsed(repeaters, collapsible, collapsed);
	}

	/**
	 * Expands/collapses the given collapsible under the given nested repeaters.
	 *
	 * @param collapsible the collapsible whose state will be changed.
	 * @param repeaters the list of nested repeaters, parent-first.
	 * @param collapsed true if the collapsible is to be collapsed, false if it is to be expanded.
	 */
	private static void setCollapsed(final List<WRepeater> repeaters,
			final WCollapsible collapsible, final boolean collapsed) {
		if (repeaters.isEmpty()) {
			// If the collapsible's state differs from the current operation, change it.
			if (collapsed != collapsible.isCollapsed()) {
				collapsible.setCollapsed(collapsed);
			}
		} else {
			// Recurse for all rows of the current repeater
			WRepeater repeater = repeaters.get(0);
			List<WRepeater> childRepeaters = repeaters.subList(1, repeaters.size());

			for (UIContext subContext : repeater.getRowContexts()) {
				UIContextHolder.pushContext(subContext);

				try {
					setCollapsed(childRepeaters, collapsible, collapsed);
				} finally {
					UIContextHolder.popContext();
				}
			}
		}
	}

	/**
	 * Finds all repeaters in the component hierarchy that are an ancestor of <code>child</code>. Repeaters are added to
	 * the list in hierarchical order, parent first.
	 *
	 * @param child the child component to start the search from.
	 * @param repeaters the list to add repeaters to.
	 */
	private static void findRepeaters(final WComponent child, final List<WRepeater> repeaters) {
		WRepeater repeater = WebUtilities.getAncestorOfClass(WRepeater.class, child);

		if (repeater != null) {
			repeaters.add(0, repeater);
			findRepeaters(repeater, repeaters);
		}
	}

	/**
	 * A utility used by the expand/collapse buttons when no {@link CollapsibleGroup} group is defined for this class.
	 *
	 * @param comp the component to search for {@link WCollapsible}s.
	 * @param results the list to receive all the collapsibles in the ui tree rooted at <code>comp</code>.
	 * @return The <code>results</code> parameter.
	 */
	private static List<WCollapsible> findAllCollapsibles(final WComponent comp,
			final List<WCollapsible> results) {
		if (comp instanceof WCollapsible) {
			results.add((WCollapsible) comp);
		}

		if (comp instanceof Container) {
			Container container = (Container) comp;

			int size = container.getChildCount();

			for (int i = 0; i < size; i++) {
				WComponent next = container.getChildAt(i);
				findAllCollapsibles(next, results);
			}
		}

		return results;
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new CollapsibleModel.
	 */
	@Override // For type safety only
	protected CollapsibleModel newComponentModel() {
		return new CollapsibleModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CollapsibleModel getComponentModel() {
		return (CollapsibleModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CollapsibleModel getOrCreateComponentModel() {
		return (CollapsibleModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class CollapsibleModel extends ComponentModel {

		/**
		 * The collapsible group to be expanded/collapsed by this collapsible toggle.
		 */
		private CollapsibleGroup group;
	}
}
