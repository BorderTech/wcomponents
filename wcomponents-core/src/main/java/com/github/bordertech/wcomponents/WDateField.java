package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This component is a date entry component. Individual themes may add features such as automatic text formatting and
 * date picker widgets. Convenience methods exist to set and get the value as a java date object.
 * </p>
 *
 * @author Ming Gao
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDateField extends AbstractInput implements AjaxTrigger, AjaxTarget, SubordinateTrigger,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WDateField.class);

	/**
	 * This date format is used internally to exchange dates between the client and server.
	 */
	private static final String INTERNAL_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Indicates whether date parsing is lenient.
	 */
	private final boolean lenient;

	/**
	 * Creates a WDateField that does not allow lenient parsing.
	 */
	public WDateField() {
		this(false);
	}

	/**
	 * Creates a WDateField.
	 *
	 * @param lenient If true, date parsing will be lenient.
	 */
	public WDateField(final boolean lenient) {
		this.lenient = lenient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final Object data) {
		// This override is necessary to maintain other internal state
		DateFieldModel model = getOrCreateComponentModel();

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
	 * Set the value of the date field.
	 *
	 * @param date the date to set.
	 */
	public void setDate(final Date date) {
		setData(date);
	}

	/**
	 * Returns the text entered into the WDateField as a java date. Returns null if the data cannot be converted into a
	 * java date.
	 *
	 * @return the java date or null
	 */
	public Date getDate() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getValue() {
		return convertDate(getData());
	}

	/**
	 * Attempts to convert the given object to a date. Throws a SystemException on error.
	 *
	 * @param data the data to convert.
	 * @return the converted date, or null if <code>data</code> was null/empty.
	 */
	private Date convertDate(final Object data) {
		if (data == null) {
			return null;
		} else if (data instanceof Date) {
			return (Date) data;
		} else if (data instanceof Long) {
			return new Date((Long) data);
		} else if (data instanceof Calendar) {
			return ((Calendar) data).getTime();
		} else if (data instanceof String) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(INTERNAL_DATE_FORMAT);
				sdf.setLenient(lenient);
				return sdf.parse((String) data);
			} catch (ParseException e) {
				throw new SystemException("Could not convert String data [" + data + "] to a date.");
			}
		}

		throw new SystemException("Cannot convert data type " + data.getClass() + " to a date.");
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
	public boolean isParseable() {
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
		Date date = getValue();
		return date == null ? getText() : date.toString();
	}

	// ================================
	// Action/Event handling
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// Valid Date on the request
		Date dateValue = getRequestValue(request);

		// Text entered by the user (An empty string is treated as null)
		String value = request.getParameter(getId());
		String text = Util.empty(value) ? null : value;

		// Current Date
		Date currentDate = getValue();

		boolean changed;

		// If a "valid" date value has not been entered, then check if the "user text" has changed
		if (dateValue == null) {
			// User entered text
			changed = !Util.equals(text, getText()) || currentDate != null;
		} else {
			// Valid Date
			changed = !Util.equals(dateValue, currentDate);
		}

		if (changed) {
			setData(dateValue);
			DateFieldModel model = getOrCreateComponentModel();
			model.validDate = dateValue != null || text == null;
			model.text = text;
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getRequestValue(final Request request) {
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
				SimpleDateFormat sdf = new SimpleDateFormat(INTERNAL_DATE_FORMAT);
				sdf.setLenient(lenient);
				return sdf.parse(dateParam);
			} catch (ParseException e) {
				LOG.warn(
						"Date parameter could not be parsed (" + dateParam + ") and will be treated as null",
						e);
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
		super.validateComponent(diags);

		if (isParseable()) {
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
		Date value = getValue();
		if (value == null) {
			return;
		}

		Date min = getMinDate();
		Date max = getMaxDate();

		if (min != null && value.compareTo(min) < 0) {
			diags.add(createErrorDiagnostic(
					InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL, this, min));
		}

		if (max != null && value.compareTo(max) > 0) {
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
	public Date getMinDate() {
		return getComponentModel().minDate;
	}

	/**
	 * Sets the minimum allowable value for this date field.
	 *
	 * @param minDate the minimum allowable value.
	 */
	public void setMinDate(final Date minDate) {
		getOrCreateComponentModel().minDate = minDate;
	}

	/**
	 * Retrieves the maximum allowable value for this date field. The maximum value is enforced server-side using the
	 * WComponent validation framework, and <b>may</b> be enforced client-side.
	 *
	 * @return the maximum allowable value, or null if there is no maximum.
	 */
	public Date getMaxDate() {
		return getComponentModel().maxDate;
	}

	/**
	 * Sets the maximum allowable value for this date field.
	 *
	 * @param maxDate the maximum allowable value.
	 */
	public void setMaxDate(final Date maxDate) {
		getOrCreateComponentModel().maxDate = maxDate;
	}

	/**
	 * Creates a new DateFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new DateFieldModel
	 */
	@Override
	protected DateFieldModel newComponentModel() {
		return new DateFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected DateFieldModel getComponentModel() {
		return (DateFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected DateFieldModel getOrCreateComponentModel() {
		return (DateFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * DateFieldModel holds Extrinsic state management of the field.
	 */
	public static class DateFieldModel extends InputModel {

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
		private Date minDate;

		/**
		 * The maximum date value to allow.
		 */
		private Date maxDate;

	}
}
