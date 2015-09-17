package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.DiagnosticImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the common functionality required by a {@link FieldValidator} implementation.
 *
 * @author Adam Millard
 */
public abstract class AbstractFieldValidator implements FieldValidator, Serializable {

	/**
	 * The input field to validate.
	 */
	private Input input;

	/**
	 * The error message to display on validation failure.
	 */
	private String errorMessage;

	/**
	 * Creates an AbstractFieldValidator with no message.
	 */
	public AbstractFieldValidator() {
	}

	/**
	 * Creates an AbstractFieldValidator with the given error message.
	 *
	 * The format of the error message String must conform to the pattern required by <code>MessageFormat</code>.
	 *
	 * @see java.text.MessageFormat
	 *
	 * @param errorMessage the error message.
	 */
	public AbstractFieldValidator(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Sets the input field.
	 *
	 * @param inputField the input field to set.
	 */
	@Override
	public void setInputField(final Input inputField) {
		this.input = inputField;
	}

	/**
	 * @return the input field.
	 */
	@Override
	public Input getInputField() {
		return input;
	}

	/**
	 * Sets the error message to display on validation failure.
	 *
	 * This is the format of the error message for a validator. It must conform to the pattern required by
	 * <code>MessageFormat</code>.
	 *
	 * @see java.text.MessageFormat
	 *
	 * @param errorMessage the error message.
	 */
	@Override
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the error message to display on validation failure.
	 */
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Retrieves the input field's value in the given context.
	 *
	 * @return the input field's value, as a string.
	 */
	public String getInputAsString() {
		return getInputField().getValueAsString();
	}

	/**
	 * Sub classes should remember to call super.getMessageArguments() to ensure that the field label is added to the
	 * list of arguments.
	 *
	 * @return The list of arguments to be applied to the validators error message.
	 */
	protected List<Serializable> getMessageArguments() {
		List<Serializable> args = new ArrayList<>(1);
		WLabel label = input.getLabel();

		if (label == null) {
			args.add("");
		} else {
			args.add(label.getText());
		}

		return args;
	}

	/**
	 * Validates the input field.
	 *
	 * @param diags the list of Diagnostics to add validation errors to.
	 *
	 * @return the updated list of validation errors.
	 */
	@Override
	public List<Diagnostic> validate(final List<Diagnostic> diags) {
		if (!isValid()) {
			List<Serializable> argList = getMessageArguments();
			Serializable[] args = argList.toArray(new Serializable[argList.size()]);

			diags.add(new DiagnosticImpl(Diagnostic.ERROR, input, getErrorMessage(), args));
		}

		return diags;
	}

	/**
	 * Subclasses should implement this method with the actual validation logic.
	 *
	 * @return true if the component is valid.
	 */
	protected abstract boolean isValid();
}
