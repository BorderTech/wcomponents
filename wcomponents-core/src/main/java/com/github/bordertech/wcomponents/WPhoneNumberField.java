package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * A WPhoneNumberField is a wcomponent used to display a telephone number field. Use the method "getText" to get the
 * text entered into the field by the user.
 * </p>
 * <p>
 * A telephone number field differs from a text field in the way in which some user agents interact with it. For
 * example, touchscreen devices may display a numeric data entry pad rather than an alphanumeric keyboard.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPhoneNumberField extends AbstractInput implements AjaxTrigger, AjaxTarget,
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
	 * Retrieves the phone field's text for a specific user.
	 *
	 * @return the phone field's text for the given context.
	 */
	public String getText() {
		return getValue();
	}

	/**
	 * Sets the text value to be shown in the input field.
	 *
	 * @param text the text to display.
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
	 * @return the minimum number of characters that the user can enter into the phone number field.
	 */
	public int getMinLength() {
		return getComponentModel().minLength;
	}

	/**
	 * Set the minimum number of characters that the user can enter into the phone number field.
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
	 * @return the maximum number of characters that the user can enter into the phone number field.
	 */
	public int getMaxLength() {
		return getComponentModel().maxLength;
	}

	/**
	 * Set the maximum number of characters that the user can enter into the phone number field.
	 *
	 * @param maxLength the maximum number of characters to allow.
	 */
	public final void setMaxLength(final int maxLength) {
		getOrCreateComponentModel().maxLength = maxLength;
	}

	/**
	 * The pattern to validate against.
	 * <p>
	 * The pattern must be supported natively by your target user agent (e.g. browser).
	 * </p>
	 * <p>
	 * It is expected the {@link WLabel} for this component describes the required format of the component.
	 * </p>
	 *
	 * @param pattern the pattern to validate against.
	 */
	public final void setPattern(final String pattern) {
		getOrCreateComponentModel().pattern = pattern == null ? null : Pattern.compile(pattern);
	}

	/**
	 * @return the pattern to validate against.
	 */
	public String getPattern() {
		Pattern pattern = getComponentModel().pattern;
		return pattern == null ? null : pattern.toString();
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

			// Pattern
			Pattern pattern = getComponentModel().pattern;
			if (pattern != null) {
				Matcher matcher = pattern.matcher(value);
				if (!matcher.matches()) {
					diags.add(createErrorDiagnostic(
							InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID_PATTERN, this));
				}
			}
		}
	}

	/**
	 * @return the {@link WSuggestions} for this phone number field.
	 */
	public WSuggestions getSuggestions() {
		return getComponentModel().suggestions;
	}

	/**
	 * Set the {@link WSuggestions} for this phone number field.
	 *
	 * @param suggestions the {@link WSuggestions} for this phone number field
	 */
	public final void setSuggestions(final WSuggestions suggestions) {
		getOrCreateComponentModel().suggestions = suggestions;
	}

	/**
	 * Creates a new PhoneFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new PhoneFieldModel
	 */
	@Override
	protected PhoneFieldModel newComponentModel() {
		return new PhoneFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PhoneFieldModel getComponentModel() {
		return (PhoneFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected PhoneFieldModel getOrCreateComponentModel() {
		return (PhoneFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * PhoneFieldModel holds Extrinsic state management of the field.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class PhoneFieldModel extends InputModel {

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

		/**
		 * The pattern to allow.
		 */
		private Pattern pattern;

		/**
		 * The list of suggestions.
		 */
		private WSuggestions suggestions;
	}
}
