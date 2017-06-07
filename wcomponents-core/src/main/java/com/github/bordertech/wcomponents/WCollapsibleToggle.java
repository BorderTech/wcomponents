package com.github.bordertech.wcomponents;

/**
 * This is component can be used to expand or collapse all collapsibles. It can also belong to a {@link CollapsibleGroup}, in this case the toggle
 * functionality will apply to the {@link WCollapsible} and {@link WTabSet} (if accordion) components in that group only.
 *
 * @author Ming Gao
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WCollapsibleToggle extends AbstractWComponent implements AjaxTarget {

	/**
	 * Creates a WCollapsibleToggle.
	 */
	public WCollapsibleToggle() {
	}

	/**
	 * Creates a WCollapsibleToggle.
	 *
	 * @param clientSide if true, the collapse/expand is handled client-side
	 * @deprecated 1.2.0 all WCollapsibleToggles are client side.
	 */
	public WCollapsibleToggle(final boolean clientSide) {
	}

	/**
	 * Creates a WCollapsibleToggle for the given CollapsibleGroup.
	 *
	 * @param clientSide if true, the collapse/expand is handled client-side.
	 * @param group the CollapsibleGroup to create the toggle for.
	 * @deprecated 1.2.0 all WCollapsibleToggles are client side use {@link #WCollapsibleToggle(com.github.bordertech.wcomponents.CollapsibleGroup)}.
	 */
	public WCollapsibleToggle(final boolean clientSide, final CollapsibleGroup group) {
		this();
		setGroup(group);
	}

	/**
	 * Creates a WCollapsibleToggle for the given CollapsibleGroup.
	 *
	 * @param group the CollapsibleGroup to create the toggle for.
	 */
	public WCollapsibleToggle(final CollapsibleGroup group) {
		this();
		setGroup(group);
	}

	/**
	 * Indicates whether processing will occur client-side.
	 *
	 * @return true if processing is handled client-side, or false for server-side.
	 * @deprecated 1.2.0 all WCollapsibleToggles are client side
	 */
	public boolean isClientSideToggleable() {
		return true;
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
