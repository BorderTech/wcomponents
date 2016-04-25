package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validator.FieldValidator;
import java.util.Iterator;

/**
 * Interface for components that correspond to an input field.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface Input extends WComponent, Disableable, Mandatable, DataBound, BeanBound,
		BeanProviderBound {

	// ================================
	// Validation
	/**
	 * Adds a validator to the input field. Validators are not automatically called on form submission. Using a
	 * {@link ValidatingAction} or an explicit call to {@link WComponent#validate(List)} will trigger validation on a
	 * WComponent tree.
	 *
	 * @param validator the validator to add.
	 */
	void addValidator(final FieldValidator validator);

	/**
	 * @return the list of validators for this input field.
	 */
	Iterator<FieldValidator> getValidators();

	// ================================
	// Action on change
	/**
	 * Sets the action that you want run if the input is changed by the user. The action command will be set to the new
	 * value of the input.
	 *
	 * @param actionOnChange the action to execute when the input is changed by the user.
	 */
	void setActionOnChange(final Action actionOnChange);

	/**
	 * @return the action to execute when the input is changed by the user.
	 */
	Action getActionOnChange();

	/**
	 * Returns the data object that has been associated with this input component, else null. For convenience, this data
	 * object is passed to the execute() method of the button's associated Action, in the ActionEvent parameter.
	 *
	 * @return the data object that has been associated with this input component.
	 */
	Object getActionObject();

	/**
	 * Associates this input with a data object that can be easily accessed in the execute() method of the associated
	 * Action.
	 *
	 * @param data the data object.
	 */
	void setActionObject(final Object data);

	/**
	 * Same as {#getValueAsString()}. This method exists simply to clarify the relationship between the input component,
	 * its Action, and the ActionEvent sent to the execute() method of the Action.
	 *
	 * @return the current selection, as a string.
	 */
	String getActionCommand();

	// ================================
	// Default submit button
	/**
	 * Sets the button that should be submitted when the user hits enter key and cursor is inside this input field.
	 *
	 * @param defaultSubmitButton the default submit button for this field.
	 */
	void setDefaultSubmitButton(final WButton defaultSubmitButton);

	/**
	 * @return the button that will be submitted if the user hits the enter key when the cursor is in this field.
	 */
	WButton getDefaultSubmitButton();

	// ================================
	// Mandatory flag
	/**
	 * Set whether or not this input is mandatory, and customise the error message that will be displayed.
	 *
	 * @param mandatory true for mandatory, false for optional.
	 * @param message the message to display to the user on mandatory validation failure.
	 */
	void setMandatory(final boolean mandatory, final String message);

	// ================================
	// ReadOnly flag
	/**
	 * Indicates whether the input is read only in the given context.
	 *
	 * @return true if the input is read only, otherwise false.
	 */
	boolean isReadOnly();

	/**
	 * Sets whether the input is read only.
	 *
	 * @param readOnly if true, the input is read only. If false, it is editable.
	 */
	void setReadOnly(final boolean readOnly);

	// ================================
	// Input Value
	/**
	 * Provide the value of the component on the Request.
	 * <p>
	 * If the component is not on the request, the components current value will be provided.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return the value of this component on the Request, or its current state if it is not on the request.
	 */
	Object getRequestValue(final Request request);

	/**
	 * Provide the value of the component returned by {@link #getData()} in the correct format.
	 * <p>
	 * If required, this method can convert the data into the correct type and also do any validation before the value
	 * is used.
	 * </p>
	 *
	 * @return the value of the component returned by {@link #getData()} in the correct format.
	 */
	Object getValue();

	/**
	 * Retrieves a String representation of the input field's value.
	 *
	 * @return the String representation of the input field's value
	 */
	String getValueAsString();

	/**
	 * Indicates whether this input field is empty. An input is considered empty if the value is null or an empty
	 * String.
	 *
	 * @return true if this input field is empty, false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Indicates if the input component was changed in the last request.
	 *
	 * @return true if the input was changed in the last request.
	 */
	boolean isChangedInLastRequest();

}
