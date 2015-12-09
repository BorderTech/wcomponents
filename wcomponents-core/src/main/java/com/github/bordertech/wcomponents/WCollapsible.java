package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * <p>
 * The WCollapsible component enables a given component to be expanded/collapsed (shown/hidden) via clicking on the
 * collapsible's header section. When a user clicks the heading, the visibility of the collapsible's content is
 * toggled.</p>
 *
 * <p>
 * Various {@link CollapsibleMode modes of operation} are supported, which allow developers to tune performance by only
 * loading content when it is needed.</p>
 *
 * @author Martin Shevchenko
 */
public class WCollapsible extends AbstractNamingContextContainer implements AjaxTarget,
		SubordinateTarget, Marginable {

	/**
	 * The available types of collapsible mode.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum CollapsibleMode {
		/**
		 * Indicates that expanding/collapsing involves a round-trip to the server. The collapsible content will be
		 * rendered only when the collapsible is open.
		 *
		 * @deprecated Use CollapsibleMode DYNAMIC instead as a like-for-like replacement or any other mode if it is
		 * more appropriate to the individual use case.
		 */
		SERVER,
		/**
		 * Indicates that expanding/collapsing is performed client-side, using a single AJAX request to obtain the
		 * content the first time the collapsible is expanded.
		 */
		LAZY,
		/**
		 * Indicates that expanding/collapsing is handled on the client. The content is always rendered, and no
		 * additional trips to the server are made.
		 */
		CLIENT,
		/**
		 * Indicates that expanding/collapsing is performed using AJAX requests. A request will be made every time the
		 * collapsible is expanded.
		 */
		DYNAMIC,
		/**
		 * Indicates that expanding/collapsing is performed client-side, using a single AJAX request to obtain the
		 * content immediately after the page loads.
		 */
		EAGER
	};

	/**
	 * The content to display inside the collapsible.
	 */
	private final WComponent content;

	/**
	 * The label for the collapsible's heading.
	 */
	private final WDecoratedLabel label;

	/**
	 * Creates a WCollapsible with the given content and heading.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param heading the collapsible's heading.
	 */
	public WCollapsible(final WComponent content, final String heading) {
		this(content, new WDecoratedLabel(heading), CollapsibleMode.CLIENT, null);
	}

	/**
	 * Creates a WCollapsible with the given content and heading.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param label the collapsible's heading.
	 */
	public WCollapsible(final WComponent content, final WDecoratedLabel label) {
		this(content, label, CollapsibleMode.CLIENT, null);
	}

	/**
	 * Creates a WCollapsible with the given content and heading.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param heading the collapsible's heading.
	 * @param mode the mode of the collapsible
	 */
	public WCollapsible(final WComponent content, final String heading, final CollapsibleMode mode) {
		this(content, new WDecoratedLabel(heading), mode, null);
	}

	/**
	 * Creates a WCollapsible with the given content and heading.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param label the collapsible's heading.
	 * @param mode the mode of the collapsible
	 */
	public WCollapsible(final WComponent content, final WDecoratedLabel label,
			final CollapsibleMode mode) {
		this(content, label, mode, null);
	}

	/**
	 * Creates a WCollapsible with the given content, heading and group.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param heading the collapsible's heading.
	 * @param mode the mode of the collapsible
	 * @param group the {@link CollapsibleGroup} that this collapsible belongs to.
	 */
	public WCollapsible(final WComponent content, final String heading, final CollapsibleMode mode,
			final CollapsibleGroup group) {
		this(content, new WDecoratedLabel(heading), mode, group);
	}

	/**
	 * Creates a WCollapsible with the given content, heading and group.
	 *
	 * @param content the content to display inside the collapsible.
	 * @param label the collapsible's heading.
	 * @param mode the mode of the collapsible
	 * @param group the {@link CollapsibleGroup} that this collapsible belongs to.
	 */
	public WCollapsible(final WComponent content, final WDecoratedLabel label,
			final CollapsibleMode mode,
			final CollapsibleGroup group) {
		getComponentModel().mode = mode;
		this.content = content;
		this.label = label;
		add(label);
		add(content);

		if (group != null) {
			setGroup(group);
		}
	}

	/**
	 * @return this WCollapsible's mode of operation
	 */
	public CollapsibleMode getMode() {
		return getComponentModel().mode;
	}

	/**
	 * Sets this WCollapsible's mode of operation.
	 *
	 * @param mode the mode of operation.
	 */
	public void setMode(final CollapsibleMode mode) {
		getOrCreateComponentModel().mode = mode;
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
	 * Indicates whether the collapsible is collapsed in the given context.
	 *
	 * @return true if the collapsible is collapsed for the given user, false if expanded.
	 */
	public boolean isCollapsed() {
		return getComponentModel().collapsed;
	}

	/**
	 * Sets whether the collapsible is collapsed in the given context.
	 *
	 * @param collapsed true if the collapsible is to be collapsed for the given user, false if expanded.
	 */
	public void setCollapsed(final boolean collapsed) {
		getOrCreateComponentModel().collapsed = collapsed;
	}

	/**
	 * @return the content of this collapsible
	 */
	public WComponent getContent() {
		return this.content;
	}

	/**
	 * @return the decorated label that is used to render the collapsible heading.
	 */
	public WDecoratedLabel getDecoratedLabel() {
		return label;
	}

	/**
	 * Retrieves the collapsible's default heading.
	 *
	 * @return the heading text.
	 *
	 * @deprecated use {@link #getDecoratedLabel()}.getText()
	 */
	@Deprecated
	public String getHeading() {
		return label.getText();
	}

	/**
	 * Sets the collapsible's heading for the given context.
	 *
	 * @param heading the heading text to set.
	 *
	 * @deprecated use {@link #getDecoratedLabel()}.setText(String)
	 */
	@Deprecated
	public void setHeading(final String heading) {
		label.setText(heading);
	}

	/**
	 * The group name that this collapsible component belongs to. If it is not part of a group then the name defaults to
	 * {@link WComponent#getName()}.
	 *
	 * @return the collapsible group name
	 */
	public String getGroupName() {
		CollapsibleGroup group = getComponentModel().group;
		return (group == null ? getId() : group.getGroupName());
	}

	/**
	 * Set the {@link CollapsibleGroup} that this component belongs to. This will enable a {@link WCollapsibleToggle}
	 * component to target the group.
	 *
	 * @param group the group to set
	 */
	public void setGroup(final CollapsibleGroup group) {
		getOrCreateComponentModel().group = group;
		group.addCollapsible(this);
	}

	/**
	 * @return the collapsible's heading level
	 */
	public HeadingLevel getHeadingLevel() {
		return getComponentModel().headingLevel;
	}

	/**
	 * @param headingLevel the collapsible's heading level
	 */
	public void setHeadingLevel(final HeadingLevel headingLevel) {
		getOrCreateComponentModel().headingLevel = headingLevel;
	}

	/**
	 * Override handleRequest to perform processing necessary for this component. This is used to handle the server-side
	 * collapsible mode and to synchronise with the client-side state for the other modes.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		String clientState = request.getParameter(getId());

		if (clientState != null) {
			setCollapsed(clientState.equalsIgnoreCase("closed"));
		}
	}

	/**
	 * Override preparePaintComponent in order to toggle the visibility of the content, or to register the appropriate
	 * ajax operation.
	 *
	 * @param request the request being responded to
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (content != null) {
			switch (getMode()) {
				case EAGER: {
					// Always visible
					content.setVisible(true);
					AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
							request);
					break;
				}
				case LAZY:
					content.setVisible(!isCollapsed());

					if (isCollapsed()) {
						AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
								request);
					}

					break;

				case DYNAMIC: {
					content.setVisible(!isCollapsed());
					AjaxHelper.registerContainer(getId(), getId() + "-content", content.getId(),
							request);
					break;
				}
				case SERVER: {
					content.setVisible(!isCollapsed());
					break;
				}
				case CLIENT: {
					// Will always be visible
					content.setVisible(true);
					break;
				}
				default: {
					throw new SystemException("Unknown mode: " + getMode());
				}
			}
		}
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = label == null ? null : label.getText();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text, 1, 1);
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new CollapsibleModel.
	 */
	@Override
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
	 * Holds the extrinsic state information of a WCollapsible.
	 */
	public static class CollapsibleModel extends ComponentModel {

		/**
		 * Indicates whether the collapsible is collapsed.
		 */
		private boolean collapsed = true;

		/**
		 * The CollapsibleGroup, primarily used with a {@link #CollapsibleToggle} to toggle a group of collapsibles at
		 * once.
		 */
		private CollapsibleGroup group;

		/**
		 * Indicates how the collapsible should operate.
		 */
		private CollapsibleMode mode;

		/**
		 * The margins to be used on the collapsible.
		 */
		private Margin margin;

		/**
		 * The heading level.
		 */
		private HeadingLevel headingLevel;
	}
}
