package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteableDate;
import com.github.bordertech.wcomponents.autocomplete.type.DateType;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This component is a date entry component. Individual themes may add features such as automatic text formatting and
 * date picker widgets. Convenience methods exist to set and get the value as a java.time.LocalDate.
 * </p>
 *
 * @author John McGuinness
 * @since 1.5.15
 */
public class WLocalDateField extends AbstractInput implements AjaxTrigger, AjaxTarget, SubordinateTrigger,
		SubordinateTarget, AutocompleteableDate {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WLocalDateField.class);

	/**
	 * This date format is used internally to exchange dates between the client and server.
	 */
	private static final String INTERNAL_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Creates a WLocalDateField.
	 */
	public WLocalDateField() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final Object data) {
		// This override is necessary to maintain other internal state
		LocalDateFieldModel model = getOrCreateComponentModel();
		try {
			super.setData(convertDate(data));
			model.text = null;
			model.validDate = true;
		} catch (SystemException e) {
			super.setData(data);
			model.text = data.toString();
			model.validDate = false;
		}
	}

	/**
	 * Set the value of the field.
	 *
	 * @param date the date to set.
	 */
	public void setLocalDate(final LocalDate date) {
		setData(date);
	}

	/**
	 * Returns the text entered into the WDateField as a java date. Returns null if the data cannot be converted into a
	 * java date.
	 *
	 * @return the java date or null
	 */
	public LocalDate getLocalDate() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate getValue() {
		return convertDate(getData());
	}

	/**
	 * Attempts to convert the given object to a local date. Throws a SystemException on error.
	 *
	 * @param data the data to convert.
	 * @return the converted date, or null if <code>data</code> was null/empty.
	 */
	private LocalDate convertDate(final Object data) {
		if (data == null) {
			return null;
		} else if (data instanceof LocalDate) {
			return (LocalDate) data;
		} else if (data instanceof Date) {
			return fromInstant(((Date) data).toInstant());
		} else if (data instanceof Long) {
			return fromInstant(Instant.ofEpochMilli((Long) data));
		} else if (data instanceof Calendar) {
			return fromInstant(Instant.ofEpochMilli(((Calendar) data).getTimeInMillis()));
		} else if (data instanceof String) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INTERNAL_DATE_FORMAT);
				return LocalDate.parse((String) data, formatter);
			} catch (DateTimeParseException e) {
				throw new SystemException("Could not convert String data [" + data + "] to a date.");
			}
		}

		throw new SystemException("Cannot convert data type " + data.getClass() + " to a date.");
	}

	/**
	 * Converts an instant to a local date in the system's default zone id.
	 *
	 * @param instant the instant to convert
	 * @return a LocalDate or null
	 */
	private LocalDate fromInstant(final Instant instant) {
		return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Retrieves the text entered into the field by a specific user. This is not necessarily a valid date.
	 *
	 * @return the text field's text for the given context.
	 */
	public String getText() {
		return getComponentModel().text;
	}

	/**
	 * Indicates whether the text value held in this field is a valid date.
	 *
	 * @return true if the field contains text which is a valid date, false otherwise.
	 */
	public boolean isValidDate() {
		return getComponentModel().validDate;
	}

	/**
	 * Retrieves a String representation of the date field's value. The date value will be returned using its default
	 * String representation.
	 *
	 * @return the date value, or the text entered by the user if there is no valid date.
	 */
	@Override
	public String getValueAsString() {
		LocalDate date = getValue();
		return date == null ? getText() : date.toString();
	}

	// ================================
	// Action/Event handling
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// Valid DateType on the request
		LocalDate dateValue = getRequestValue(request);

		// Text entered by the user (An empty string is treated as null)
		String value = request.getParameter(getId());
		String text = Util.empty(value) ? null : value;

		// Current DateType
		LocalDate currentDate = getValue();

		boolean changed;

		// If a "valid" date value has not been entered, then check if the "user text" has changed
		if (dateValue == null) {
			// User entered text
			changed = !Util.equals(text, getText()) || currentDate != null;
		} else {
			// Valid DateType
			changed = !Util.equals(dateValue, currentDate);
		}

		if (changed) {
			boolean valid = dateValue != null || text == null;
			handleRequestValue(dateValue, valid, text);
		}

		return changed;
	}

	/**
	 * Set the request value.
	 *
	 * @param value the date value
	 * @param valid true if valid value
	 * @param text the user text
	 */
	protected void handleRequestValue(final LocalDate value, final boolean valid, final String text) {
		// As setData() clears the text value (if valid), this must be called first so it can be set after
		setData(value);
		LocalDateFieldModel model = getOrCreateComponentModel();
		model.validDate = valid;
		model.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate getRequestValue(final Request request) {
		if (isPresent(request)) {
			// User entered a valid date
			String dateParam = request.getParameter(getId() + "-date");
			if (dateParam == null) {
				return null;
			}

			// Check the date is not empty and correct length
			if (Util.empty(dateParam) || dateParam.length() != INTERNAL_DATE_FORMAT.length()) {
				LOG.warn("Date parameter is not the valid length of " + INTERNAL_DATE_FORMAT.
						length() + " characters ("
						+ dateParam + ") and will be treated as null");
				return null;
			}

			// Check it is valid
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INTERNAL_DATE_FORMAT);
				return LocalDate.parse((String) dateParam, formatter);
			} catch (DateTimeParseException e) {
				LOG.warn("Date parameter could not be parsed (" + dateParam + ") and will be treated as null", e);
				return null;
			}
		} else {
			return getValue();
		}
	}

	/**
	 * Sets the validation error message.
	 *
	 * @param errorMessage The errorMessage to set, or null to use the default error message.
	 */
	public void setInvalidDateErrorMessage(final String errorMessage) {
		getOrCreateComponentModel().errorMessage = errorMessage;
	}

	/**
	 * Override WInput's validateComponent to perform further validation on the date.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		if (isValidDate()) {
			super.validateComponent(diags);
			validateDate(diags);
		} else {
			diags.add(createErrorDiagnostic(getComponentModel().errorMessage, this));
		}
	}

	/**
	 * <p>
	 * Performs validation of the date. Validation ensures that the entered date is between the minimum/maximum values
	 * (if applicable).
	 * </p>
	 * <p>
	 * Subclasses can override this method to perform more specific validation.
	 * </p>
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	protected void validateDate(final List<Diagnostic> diags) {
		LocalDate value = getValue();
		if (value == null) {
			return;
		}

		LocalDate min = getMinDate();
		LocalDate max = getMaxDate();

		if (min != null && value.isBefore(min)) {
			diags.add(createErrorDiagnostic(
					InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL, this, min));
		}

		if (max != null && value.isAfter(max)) {
			diags.add(createErrorDiagnostic(
					InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_BEFORE_OR_EQUAL, this, max));
		}
	}

	/**
	 * Retrieves the minimum allowable value for this date field. The minimum value is enforced server-side using the
	 * WComponent validation framework, and <b>may</b> be enforced client-side.
	 *
	 * @return the minimum allowable value, or null if there is no minimum.
	 */
	public LocalDate getMinDate() {
		return getComponentModel().minDate;
	}

	/**
	 * Sets the minimum allowable value for this date field.
	 *
	 * @param minDate the minimum allowable value.
	 */
	public void setMinDate(final LocalDate minDate) {
		getOrCreateComponentModel().minDate = minDate;
	}

	/**
	 * Retrieves the maximum allowable value for this date field. The maximum value is enforced server-side using the
	 * WComponent validation framework, and <b>may</b> be enforced client-side.
	 *
	 * @return the maximum allowable value, or null if there is no maximum.
	 */
	public LocalDate getMaxDate() {
		return getComponentModel().maxDate;
	}

	/**
	 * Sets the maximum allowable value for this date field.
	 *
	 * @param maxDate the maximum allowable value.
	 */
	public void setMaxDate(final LocalDate maxDate) {
		getOrCreateComponentModel().maxDate = maxDate;
	}

	/**
	 * Creates a new LocalDateFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new LocalDateFieldModel
	 */
	@Override
	protected LocalDateFieldModel newComponentModel() {
		return new LocalDateFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected LocalDateFieldModel getComponentModel() {
		return (LocalDateFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected LocalDateFieldModel getOrCreateComponentModel() {
		return (LocalDateFieldModel) super.getOrCreateComponentModel();
	}

	@Override
	public void setAutocomplete(final DateType value) {
		if (value == null) {
			clearAutocomplete();
			return;
		}
		String newValue = value.getValue();
		if (!Util.equals(getAutocomplete(), newValue)) {
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

	/**
	 * LocalDateFieldModel holds Extrinsic state management of the field.
	 */
	public static class LocalDateFieldModel extends InputModel {

		/**
		 * The error message to display when the input fails the date validation check.
		 */
		private String errorMessage = InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID_DATE;

		/**
		 * The user-entered text.
		 */
		private String text;

		/**
		 * Indicates whether the entered date was valid.
		 */
		private boolean validDate = true;

		/**
		 * The minimum date value to allow.
		 */
		private LocalDate minDate;

		/**
		 * The maximum date value to allow.
		 */
		private LocalDate maxDate;

		/**
		 * Maintain internal state.
		 */
		@Override
		public void resetData() {
			super.resetData();
			ComponentModel model = getSharedModel();
			if (model instanceof LocalDateFieldModel) {
				LocalDateFieldModel shared = (LocalDateFieldModel) model;
				this.text = shared.text;
				this.validDate = shared.validDate;
			}
		}

		/**
		 * The auto-fill hint for the field.
		 */
		private String autocomplete;

	}
}
