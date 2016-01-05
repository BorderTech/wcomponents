package com.github.bordertech.wcomponents;

/**
 * The WFilterControl lets the user filter data within a {@link WDataTable} component. The filtering occurs client-side,
 * based on text matching.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 * @deprecated WDataTable is deprecated. WFilterControl does not apply to WTable which _must_ be filtered using AJAX if
 *   filtering is required; WDataTable filtering _should_ be done using AJAX.
 */
public class WFilterControl extends AbstractContainer implements AjaxTarget {

	/**
	 * The label for the filter.
	 */
	private final WDecoratedLabel filterLabel;

	/**
	 * Create a WFilterControl with a label.
	 *
	 * @param filterLabel the label for this filter control
	 */
	public WFilterControl(final WDecoratedLabel filterLabel) {
		if (filterLabel == null) {
			throw new IllegalArgumentException("A filter label must be provided.");
		}

		this.filterLabel = filterLabel;
		this.add(filterLabel);
	}

	/**
	 * Create a WFilterControl with a label, target and value.
	 *
	 * @param filterLabel the label for this filter control
	 * @param target the target component
	 * @param value the value of this filter control
	 */
	public WFilterControl(final WDecoratedLabel filterLabel, final WComponent target,
			final String value) {
		this(filterLabel);
		getComponentModel().target = target;
		getComponentModel().value = value;
	}

	/**
	 * Override handleRequest in order to implement processing specific to this component.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void handleRequest(final Request request) {
		if (request.getParameter(getId()) != null) {
			boolean active = request.getParameter(getId()).equals("true");
			setActive(active);
		}
	}

	/**
	 * Gets the label for filter control.
	 *
	 * @return the label for the filter control
	 */
	public WDecoratedLabel getFilterLabel() {
		return filterLabel;
	}

	/**
	 * Retrieves the filter target.
	 *
	 * @return the filter target
	 */
	public WComponent getTarget() {
		return getComponentModel().target;
	}

	/**
	 * Sets the filter target.
	 *
	 * @param target the filter target
	 */
	public void setTarget(final WComponent target) {
		getOrCreateComponentModel().target = target;
	}

	/**
	 * Gets the default filter value.
	 *
	 * @return the default filter value
	 */
	public String getValue() {
		return getComponentModel().value;
	}

	/**
	 * Sets the filter value.
	 *
	 * @param value the filter value
	 */
	public void setValue(final String value) {
		getOrCreateComponentModel().value = value;
	}

	/**
	 * Retrieves the active state for the filter.
	 *
	 * @return true if the filter is active
	 */
	public boolean isActive() {
		return getComponentModel().active;
	}

	/**
	 * Sets the active state of the filter.
	 *
	 * @param active the state of the filter
	 */
	public void setActive(final boolean active) {
		getOrCreateComponentModel().active = active;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = filterLabel.getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FilterControlModel getComponentModel() {
		return (FilterControlModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FilterControlModel getOrCreateComponentModel() {
		return (FilterControlModel) super.getOrCreateComponentModel();
	}

	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new FilterControlModel.
	 */
	@Override
	protected FilterControlModel newComponentModel() {
		return new FilterControlModel();
	}

	/**
	 * A class used to hold the state for this component.
	 *
	 * @author Jonathan Austin
	 */
	public static class FilterControlModel extends ComponentModel {

		/**
		 * The target of the filter.
		 */
		private WComponent target;
		/**
		 * The value of the filter.
		 */
		private String value;
		/**
		 * Active state of filter.
		 */
		private boolean active;
	}
}
