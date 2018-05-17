package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteableText;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.AutocompleteSegment;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.DateType;
import com.github.bordertech.wcomponents.autocomplete.type.Email;
import com.github.bordertech.wcomponents.autocomplete.type.Numeric;
import com.github.bordertech.wcomponents.autocomplete.type.Password;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.autocomplete.type.Url;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A WTextField is a wcomponent used to display a html text input field. Use the method "getText" to get the text
 * entered into the field by the user. Common configuration methods include "setColumns" and "setMaxlength".
 *
 * @author James Gifford
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTextField extends AbstractInput implements AjaxTrigger, AjaxTarget, SubordinateTrigger,
		SubordinateTarget, Placeholderable, AutocompleteableText {

	/**
	 * Override handleRequest in order to perform processing for this component. This implementation updates the text
	 * field's text if it has changed.
	 *
	 * @param request the request being responded to.
	 * @return true if the text field has changed, otherwise false
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
	 * Retrieves the text field's text.
	 *
	 * @return the text field's text.
	 */
	public String getText() {
		return getValue();
	}

	/**
	 * Sets the text value to be shown in the input field for the given user.
	 *
	 * @param text the text to display for the given context.
	 */
	public void setText(final String text) {
		setData(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlaceholder(final String placeholder) {
		getOrCreateComponentModel().placeholder = placeholder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPlaceholder() {
		return getComponentModel().placeholder;
	}

	/**
	 * @return the width of the input field in characters.
	 */
	public int getColumns() {
		return getComponentModel().columns;
	}

	/**
	 * Sets the width of the input field more-or-less in characters. The actual width of the input will vary from
	 * browser to browser bit will be in the region of 1.4 × columns.
	 *
	 * @param columns the number of characters to display.
	 */
	public void setColumns(final int columns) {
		getOrCreateComponentModel().columns = columns;
	}

	/**
	 * @return the minimum number of characters that the user can enter into the text field.
	 */
	public int getMinLength() {
		return getComponentModel().minLength;
	}

	/**
	 * <p>
	 * Set the minimum number of characters that the user can enter into the text field.
	 * </p>
	 * <p>
	 * Setting the minimum number of characters will not make a field mandatory as the validation is only applied once
	 * the user has entered some text. Use {@link #setMandatory(boolean)} to make a field mandatory, which can be used
	 * in combination with setMinLength.
	 * </p>
	 *
	 * @param minLength the minimum number of characters to allow.
	 */
	public void setMinLength(final int minLength) {
		getOrCreateComponentModel().minLength = minLength;
	}

	/**
	 * @return the maximum number of characters that the user can enter into the text field.
	 */
	public int getMaxLength() {
		return getComponentModel().maxLength;
	}

	/**
	 * Set the maximum number of characters that the user can enter into the text field.
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
	 * @return the {@link WSuggestions} for this textfield.
	 */
	public WSuggestions getSuggestions() {
		return getComponentModel().suggestions;
	}

	/**
	 * Set the {@link WSuggestions} for this textfield.
	 *
	 * @param suggestions the {@link WSuggestions} for this textfield
	 */
	public final void setSuggestions(final WSuggestions suggestions) {
		getOrCreateComponentModel().suggestions = suggestions;
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

			// Maximum Length
			int max = getMaxLength();

			if (max > 0 && value.length() > max) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_LENGTH,
								this,
								String.valueOf(max)));
			}

			// Minimum Length
			int min = getMinLength();

			if (min > 0 && value.length() < min) {
				diags.add(
						createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MIN_LENGTH,
								this,
								String.valueOf(min)));
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
	 * Creates a new TextFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new TextFieldModel
	 */
	@Override
	protected TextFieldModel newComponentModel() {
		return new TextFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected TextFieldModel getComponentModel() {
		return (TextFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected TextFieldModel getOrCreateComponentModel() {
		return (TextFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * Set the value of the {@code autocomplete} attribute for the current field.
	 * @param autocompleteValue the value to set as a (optionally space delimited list of) String value(s).
	 */
	protected void setAutocomplete(final String autocompleteValue) {
		final String newValue = Util.empty(autocompleteValue) ? null : autocompleteValue;
		if (!Util.equals(newValue, getAutocomplete())) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public String getAutocomplete() {
		return getComponentModel().autocomplete;
	}

	@Override
	public void setAutocompleteOff() {
		if (!isAutocompleteOff()) {
			getOrCreateComponentModel().autocomplete = AutocompleteUtil.getOff();
		}
	}

	@Override
	public void addAutocompleteSection(final String sectionName) {
		String newValue = AutocompleteUtil.getCombinedForAddSection(sectionName, this);
		if (!Util.equals(getAutocomplete(), newValue)) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public void clearAutocomplete() {
		if (getAutocomplete() != null) {
			getOrCreateComponentModel().autocomplete = null;
		}
	}

	@Override
	public void setAutocomplete(final DateType value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Email value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Numeric value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Password value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Url value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final AutocompleteSegment value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Telephone phone, final PhoneFormat phoneType) {
		setAutocomplete(AutocompleteUtil.getCombinedFullPhone(phoneType, phone));
	}

	@Override
	public void setPhoneSegmentAutocomplete(final PhoneFormat phoneType, final PhonePart phoneSegment) {
		setAutocomplete(AutocompleteUtil.getCombinedPhoneSegment(phoneType, phoneSegment));
	}

	@Override
	public void setAddressAutocomplete(final AddressType addressType, final AddressPart addressPart) {
		setAutocomplete(AutocompleteUtil.getCombinedAddress(addressType, addressPart));
	}

	/**
	 * TextFieldModel holds Extrinsic state management of the field.
	 *
	 * @author Martin Shevchenko
	 */
	public static class TextFieldModel extends InputModel {

		/**
		 * The number of columns to display for the field.
		 */
		private int columns;

		/**
		 * The maximum text length to allow.
		 */
		private int maxLength;

		/**
		 * The minimum text length to allow.
		 */
		private int minLength;

		/**
		 * The pattern to allow.
		 */
		private Pattern pattern;

		/**
		 * The list of suggestions.
		 */
		private WSuggestions suggestions;

		/**
		 * The placeholder text which appears if the field is editable and empty.
		 */
		private String placeholder;

		/**
		 * The auto-fill hint for the field.
		 */
		private String autocomplete;
	}
}
