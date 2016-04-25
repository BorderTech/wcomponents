package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;

/**
 * <p>
 * A WRadioButton is a wcomponent used to display a radio button and must be used with a {@link RadioButtonGroup}.
 * </p>
 * <p>
 * A WRadioButton instance can only be created by using the {@link RadioButtonGroup#addRadioButton(Object)
 * addRadioButton(value)} method or {@link RadioButtonGroup#addRadioButton() addRadioButton()} method. Call one of these
 * methods to get an instance of a radio button and then add the radio button to the required location in the UI. Each
 * radio button added to a group must have a unique value for that group.
 * </p>
 * <p>
 * If the value of the radio button is known when creating its instance, then use the
 * {@link RadioButtonGroup#addRadioButton(Object) addRadioButton(Object)}.
 * </p>
 * <p>
 * If a radio button needs to be used in a repeating component and will have different values depending on its context,
 * then use the {@link RadioButtonGroup#addRadioButton() addRadioButton()}. Radio buttons used with a {@link WRepeater}
 * will get their value from the "bean" associated to the row in the repeater. By default, the bean property for the
 * radio button will be set to ".", but this can be overridden by calling {@link #setBeanProperty(String)} on the radio
 * button instance.
 * </p>
 * <p>
 * For the radio button group to work correctly, it is important that each radio button in the group has a unique value.
 * The radio button group uses the {@link String} representation of the radio button's value to identify which button
 * has been selected. As the string representation of the radio button's value is sent to the client, be mindful that it
 * should not be too large.
 * </p>
 * <p>
 * To determine if a WRadioButton has been selected use the {@link #isSelected() isSelected} method. Alternatively, you
 * can call the {@link RadioButtonGroup#getSelectedValue() getSelectedValue} method on the {@link RadioButtonGroup}
 * which will return the value associated with the WRadioButton which is currently selected.
 * </p>
 *
 * @author James Gifford
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WRadioButton extends WBeanComponent implements AjaxTarget, SubordinateTarget,
		Disableable {

	/**
	 * The RadioButtonGroup the button belongs to.
	 */
	private final RadioButtonGroup group;

	/**
	 * Creates a WRadioButton associated to the group passed in.
	 *
	 * @param group the RadioButtonGroup the button belongs to
	 */
	WRadioButton(final RadioButtonGroup group) {
		if (group == null) {
			throw new IllegalArgumentException("RadioButtonGroup cannot be null");
		}
		this.group = group;
	}

	// ================================
	// Handle Request
	/**
	 * This method will only process the request if the value for the group matches the button's value.
	 *
	 * @param request the request being processed.
	 */
	@Override
	public void handleRequest(final Request request) {
		// Protect against client-side tampering of disabled/read-only fields.
		if (isDisabled() || isReadOnly()) {
			return;
		}

		// Check if the group is not on the request (do nothing)
		if (!getGroup().isPresent(request)) {
			return;
		}

		// Check if the group has a null value (will be handled by the group handle request)
		if (request.getParameter(getGroup().getId()) == null) {
			return;
		}

		// Get the groups value on the request
		String requestValue = getGroup().getRequestValue(request);

		// Check if this button's value matches the request
		boolean onRequest = Util.equals(requestValue, getValue());

		if (onRequest) {
			boolean changed = getGroup().handleButtonOnRequest(request);
			if (changed && isSubmitOnChange() && UIContextHolder.getCurrent().getFocussed() == null) {
				setFocussed();
			}
		}
	}

	// ================================
	// Attributes
	/**
	 * Retrieves the name of the RadioButtonGroup that this button is associated with.
	 *
	 * @return the group name, or null if not associated to a group.
	 */
	public String getGroupName() {
		return getGroup().getId();
	}

	/**
	 * @return true if the radio button is selected.
	 */
	public boolean isSelected() {
		// Check if the Groups current value matches this radio button's value
		return Util.equals(getGroup().getValue(), getValue());
	}

	/**
	 * Sets whether the radio button is selected.
	 *
	 * @param selected true if the button should be selected, false if not
	 */
	public void setSelected(final boolean selected) {
		if (selected) {
			if (isDisabled()) {
				throw new IllegalStateException("Cannot select a disabled radio button");
			}
			getGroup().setSelectedValue(getValue());
		} else if (isSelected()) {
			// Clear selection
			getGroup().setData(null);
		}
	}

	/**
	 * Retrieves the radio button's value.
	 * <p>
	 * The radio button group uses the {@link String} value of the radio button's value to identify which button has
	 * been selected. As the string representation of the radio button's value is sent to the client, be mindful that it
	 * should not be too large.
	 * </p>
	 *
	 * @return the radio button's value.
	 */
	public String getValue() {
		Object data = getData();
		if (data == null) {
			return null;
		}
		// Treat empty the same as null
		return Util.empty(data.toString()) ? null : data.toString();
	}

	/**
	 * Indicates whether or not the radio button group is mandatory for a specific user.
	 *
	 * @return true if the radio button group is mandatory, false otherwise.
	 */
	public boolean isMandatory() {
		return group.isMandatory();
	}

	/**
	 * Indicates whether selection of the radio button should trigger a form submit.
	 *
	 * @return true if selection should trigger a submit.
	 */
	public boolean isSubmitOnChange() {
		return group.isSubmitOnChange();
	}

	/**
	 * Retrieves the RadioButtonGroup associated with this radio button.
	 *
	 * @return the associated RadioButtonGroup
	 */
	public RadioButtonGroup getGroup() {
		return group;
	}

	/**
	 * Indicates whether the radio button is disabled.
	 *
	 * @return true if the input is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return getGroup().isDisabled() || isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether the radio button is disabled.
	 *
	 * @param disabled if true, the input is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * Indicates whether the radio button is read only in the given context.
	 *
	 * @return true if the input is read only, otherwise false.
	 */
	public boolean isReadOnly() {
		return getGroup().isReadOnly() || isFlagSet(ComponentModel.READONLY_FLAG);
	}

	/**
	 * Sets whether the radio button is read only.
	 *
	 * @param readOnly if true, the input is read only. If false, it is editable.
	 */
	public void setReadOnly(final boolean readOnly) {
		setFlag(ComponentModel.READONLY_FLAG, readOnly);
	}

}
