package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * A WEmailField is a wcomponent used to display an email input field. Use the method "getText" to get the text entered
 * into the field by the user.
 * </p>
 * <p>
 * An email field differs from a text field in the way in which some user agents interact with it. For example,
 * touchscreen devices may display a different soft keyboard from the default layout. In addition, they may present
 * email addresses from the user's address book.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WEmailField extends AbstractInput implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger, SubordinateTarget {
	// ================================
	// Action/Event handling

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		String value = getRequestValue(request);
		String current = getValue();

		boolean changed = !Util.equals(value, current);

		if (changed) {
			setData(value);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestValue(final Request request) {
		if (isPresent(request)) {
			String value = request.getParameter(getId());
			// An empty string is treated as null
			return Util.empty(value) ? null : value;
		} else {
			return getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		Object data = getData();
		if (data == null) {
			return null;
		}
		// An empty string is treated as null
		return Util.empty(data.toString()) ? null : data.toString();
	}

	// ================================
	// Attributes
	/**
	 * Retrieves the phone field's text.
	 *
	 * @return the phone field's text for the given context.
	 */
	public String getText() {
		return getValue();
	}

	/**
	 * Sets the text value to be shown in the input field.
	 *
	 * @param text the text to display for the given context.
	 */
	public void setText(final String text) {
		setData(text);
	}

	/**
	 * @return the width of the input field in characters.
	 */
	public int getColumns() {
		return getComponentModel().columns;
	}

	/**
	 * Sets the width of the input field in characters.
	 *
	 * @param columns the number of characters to display.
	 */
	public void setColumns(final int columns) {
		getOrCreateComponentModel().columns = columns;
	}

	/**
	 * @return the maximum number of characters that the user can enter into the email field.
	 */
	public int getMaxLength() {
		return getComponentModel().maxLength;
	}

	/**
	 * Set the maximum number of characters that the user can enter into the email field.
	 *
	 * @param maxLength the maximum number of characters to allow.
	 */
	public final void setMaxLength(final int maxLength) {
		getOrCreateComponentModel().maxLength = maxLength;
	}

	/**
	 * @return the {@link WSuggestions} for this email field.
	 */
	public WSuggestions getSuggestions() {
		return getComponentModel().suggestions;
	}

	/**
	 * Set the {@link WSuggestions} for this email field.
	 *
	 * @param suggestions the {@link WSuggestions} for this email field
	 */
	public final void setSuggestions(final WSuggestions suggestions) {
		getOrCreateComponentModel().suggestions = suggestions;
	}

	/**
	 * Override WInput's validateComponent to perform futher validation on email addresses.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);
		validateEmailAddress(diags);
	}

	/**
	 * <p>
	 * Performs validation of the email address. This only performs very basic validation - an email address must
	 * contain some text, followed by an '@', and then something which resembles a domain/host name.
	 * </p>
	 * <p>
	 * Subclasses can override this method to perform more specific validation.
	 * </p>
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	protected void validateEmailAddress(final List<Diagnostic> diags) {
		if (!isEmpty()) {
			String value = getValueAsString();
			String errorMessage = getComponentModel().errorMessage;

			// Email Pattern
			if (!Pattern.matches(
					"^(?:\".+\"|[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+)@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+$",
					value)) {
				diags.add(createErrorDiagnostic(errorMessage, this));
			}

			// Maximum Length
			int max = getMaxLength();
			if (max > 0 && value.length() > max) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_LENGTH,
								this,
								String.valueOf(max)));
			}
		}
	}

	/**
	 * Sets the validation error message.
	 *
	 * @param message The errorMessage to set, or null to use the default error message.
	 */
	public void setInvalidEmailAddressErrorMessage(final String message) {
		getOrCreateComponentModel().errorMessage = message;
	}

	/**
	 * Creates a new EmailFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new EmailFieldModel
	 */
	@Override
	protected EmailFieldModel newComponentModel() {
		return new EmailFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected EmailFieldModel getComponentModel() {
		return (EmailFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected EmailFieldModel getOrCreateComponentModel() {
		return (EmailFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * EmailFieldModel holds Extrinsic state management of the field.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class EmailFieldModel extends InputModel {

		/**
		 * The maximum text length to allow.
		 */
		private int maxLength;

		/**
		 * The number of columns to display for the field.
		 */
		private int columns;

		/**
		 * The error message to display when the input fails the email address validation check.
		 */
		private String errorMessage = InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID;

		/**
		 * The list of suggestions.
		 */
		private WSuggestions suggestions;
	}
}
