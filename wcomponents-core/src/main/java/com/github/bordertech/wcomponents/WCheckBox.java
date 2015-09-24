package com.github.bordertech.wcomponents;

/**
 * <p>
 * A WCheckBox is a wcomponent used to display a checkbox input field. Note that WCheckBox does not render any text. To
 * display text for the check box, developers should associate a {@link WLabel} with the check box.
 * </p>
 * <p>
 * The {@link #isSelected()} method is used to determine if the checkbox has been ticked.
 * </p>
 *
 * @author James Gifford
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WCheckBox extends AbstractInput implements AjaxTrigger, AjaxTarget, SubordinateTrigger,
		SubordinateTarget {

	/**
	 * Creates an initially unselected check box with no set text.
	 */
	public WCheckBox() {
		this(false);
	}

	/**
	 * Creates a check box with the specified text and selection state.
	 *
	 * @param selected if true, the check box is initially selected; otherwise, the check box is initially unselected
	 */
	public WCheckBox(final boolean selected) {
		getComponentModel().setData(selected);
	}

	/**
	 * Returns the check box value as a String.
	 *
	 * @return "true" if the checkbox is checked, otherwise null.
	 */
	@Override
	public String getValueAsString() {
		return getValue() ? "true" : null;
	}

	/**
	 * Sets the checkbox group. When a single grouped checkbox is targeted with a <code>WSelectToggle</code>, all
	 * checkboxes in that group will be updated.
	 *
	 * @param group The group to set.
	 */
	public void setGroup(final WComponentGroup<WCheckBox> group) {
		getOrCreateComponentModel().group = group;
	}

	/**
	 * @return the check box group.
	 */
	public WComponentGroup<WCheckBox> getGroup() {
		return getComponentModel().group;
	}

	// ================================
	// Action/Event handling
	/**
	 * <p>
	 * Override handleRequest in order to perform processing for this component. This implementation checks the checkbox
	 * state in the request.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return true if the check box has changed
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		boolean selected = getRequestValue(request);
		boolean current = getValue();

		boolean changed = current != selected;

		if (changed) {
			setData(selected);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getRequestValue(final Request request) {
		if (isPresent(request)) {
			String aText = request.getParameter(getId());
			return "true".equals(aText);
		} else {
			return getValue();
		}
	}

	// ================================
	// Attributes
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getValue() {
		return Boolean.TRUE.equals(getData());
	}

	/**
	 * Returns the state of the check box. True if selected, false if not.
	 *
	 * @return true if the check box is selected, otherwise false
	 */
	public boolean isSelected() {
		return getValue();
	}

	/**
	 * Sets the state of the check box.
	 *
	 * @param selected true if the check box is selected, otherwise false
	 */
	public void setSelected(final boolean selected) {
		setData(selected);
	}

	/**
	 * Sets whether the form should be submitted when the checkbox is checked/unchecked. This sets the default option
	 * SubmitOnChange seen by all user sessions.
	 *
	 * @param submitOnChange true to submit the form on change.
	 */
	@Override
	public void setSubmitOnChange(final boolean submitOnChange) {
		super.setSubmitOnChange(submitOnChange);
	}

	/**
	 * Indicates whether the form should be submitted when the checkbox is checked/unchecked.
	 *
	 * @return true if the form should be submitted on change.
	 */
	@Override
	public boolean isSubmitOnChange() {
		return super.isSubmitOnChange();
	}

	/**
	 * Same as {@link #getName()}. This method exists simply to clarify the relationship between the WCheckBox, its
	 * Action, and the ActionEvent sent to the execute() method of the Action.
	 *
	 * @return this checkbox's name.
	 */
	@Override
	public String getActionCommand() {
		return getId();
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new CheckBoxModel.
	 */
	@Override
	// For type safety only
	protected CheckBoxModel newComponentModel() {
		return new CheckBoxModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected CheckBoxModel getComponentModel() {
		return (CheckBoxModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected CheckBoxModel getOrCreateComponentModel() {
		return (CheckBoxModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class CheckBoxModel extends InputModel {

		/**
		 * The check box group.
		 */
		private WComponentGroup<WCheckBox> group;
	}
}
