package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;

/**
 * <p>
 * A WPasswordField is a wcomponent used to display a password input field. Use the method "getText" to get the text
 * entered into the field by the user.
 * </p>
 * <p>
 * A password field differs from a text field in that the text entered is typically masked from the user, to prevent
 * over-the-shoulder attacks. For security reasons, the text value of a password field is never sent to the client. This
 * means that password fields will always appear to be empty.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPasswordField extends AbstractInput implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger, SubordinateTarget {

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
			return (Util.empty(value)) ? null : value;
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
	 * Retrieves the password field's text.
	 *
	 * @return the password field's text for the current context.
	 */
	public String getText() {
		return getValue();
	}

	/**
	 * Sets the text value.
	 *
	 * @param text the text value.
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
	 * @return the minimum number of characters that the user can enter into the password field.
	 */
	public int getMinLength() {
		return getComponentModel().minLength;
	}

	/**
	 * Set the minimum number of characters that the user can enter into the password field.
	 * <p>
	 * Setting the minimum number of characters will not make a field mandatory as the validation is only applied once
	 * the user has entered some text. Use {@link #setMandatory(boolean)} to make a field mandatory, which can be used
	 * in combination with setMinLength.
	 * </p>
	 *
	 * @param minLength the minimum number of characters to allow.
	 */
	public final void setMinLength(final int minLength) {
		getOrCreateComponentModel().minLength = minLength;
	}

	/**
	 * @return the maximum number of characters that the user can enter into the password field.
	 */
	public int getMaxLength() {
		return getComponentModel().maxLength;
	}

	/**
	 * Set the maximum number of characters that the user can enter into the password field.
	 *
	 * @param maxLength the maximum number of characters to allow.
	 */
	public final void setMaxLength(final int maxLength) {
		getOrCreateComponentModel().maxLength = maxLength;
	}

	/**
	 * Override validateComponent to perform further validation.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		if (!isEmpty()) {
			String value = getValueAsString();

			// Minimum Length
			int min = getMinLength();

			if (min > 0 && value.length() < min) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MIN_LENGTH,
								this,
								String.valueOf(min)));
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
	 * Creates a new PasswordFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new PasswordFieldModel
	 */
	@Override
	protected PasswordFieldModel newComponentModel() {
		return new PasswordFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PasswordFieldModel getComponentModel() {
		return (PasswordFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PasswordFieldModel getOrCreateComponentModel() {
		return (PasswordFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * PasswordFieldModel holds Extrinsic state management of the field.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class PasswordFieldModel extends InputModel {

		/**
		 * The maximum text length to allow.
		 */
		private int maxLength;

		/**
		 * The minimum text length to allow.
		 */
		private int minLength;

		/**
		 * The number of columns to display for the field.
		 */
		private int columns;
	}
}
