package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
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
 * <p>
 * "Partial dates" can be specified without day or month components, e.g. "JAN 2001", or just "2001".
 * </p>
 * <p>
 * When the component is bound to a bean, the bean property must be a string containing the date in the format
 * "yyyyMMdd". Parts of the date that do not have a value are padded with space characters by default. The padding
 * character can be changed using the {@link #setPaddingChar(char)} method. An example of a partial date bean value for
 * "Mar 2012" would be "201203". Note, trailing spaces are removed. If a padding character of '@' was specified, then
 * the bean value would be "201203@@".
 * </p>
 *
 * @author Ming Gao
 * @author Jonathan Austin
 */
public class WPartialDateField extends AbstractInput implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger,
		SubordinateTarget {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WPartialDateField.class);

	/**
	 * Default character used to pad the partial date value.
	 */
	private static final char DEFAULT_PADDING_CHAR = ' ';

	/**
	 * Character used by the theme to pad the partial date value.
	 */
	private static final char THEME_PADDING_CHAR = '?';

	/**
	 * Year start position.
	 */
	private static final int YEAR_START = 0;
	/**
	 * Year end position.
	 */
	private static final int YEAR_END = 4;
	/**
	 * Month start position.
	 */
	private static final int MONTH_START = 4;
	/**
	 * Month end position.
	 */
	private static final int MONTH_END = 6;
	/**
	 * Day start position.
	 */
	private static final int DAY_START = 6;
	/**
	 * Day end position.
	 */
	private static final int DAY_END = 8;

	/**
	 * Internal Format - Year start position.
	 */
	private static final int INTERNAL_YEAR_START = 0;
	/**
	 * Internal Format - Year end position.
	 */
	private static final int INTERNAL_YEAR_END = 4;
	/**
	 * Internal Format - Dash 1.
	 */
	private static final int INTERNAL_DASH1_POS = 4;
	/**
	 * Internal Format - Month start position.
	 */
	private static final int INTERNAL_MONTH_START = 5;
	/**
	 * Internal Format - Month end position.
	 */
	private static final int INTERNAL_MONTH_END = 7;
	/**
	 * Internal Format - Dash 2.
	 */
	private static final int INTERNAL_DASH2_POS = 7;
	/**
	 * Internal Format - Day start position.
	 */
	private static final int INTERNAL_DAY_START = 8;
	/**
	 * Internal Format - Day end position.
	 */
	private static final int INTERNAL_DAY_END = 10;
	/**
	 * Internal Format - Number of digits in the date.
	 */
	private static final int INTERNAL_DATE_TOTAL_CHARS = 10;

	/**
	 * Number of digits in the date.
	 */
	private static final int DATE_TOTAL_CHARS = 8;
	/**
	 * Number of digits in the year.
	 */
	private static final int YEAR_DIGITS = 4;
	/**
	 * Number of digits in the month.
	 */
	private static final int MONTH_DIGITS = 2;
	/**
	 * Number of digits in the year.
	 */
	private static final int DAY_DIGITS = 2;

	/**
	 * Maximum value for year.
	 */
	private static final int YEAR_MAX = 9999;
	/**
	 * Maximum value for month.
	 */
	private static final int MONTH_MAX = 12;
	/**
	 * Maximum value for day.
	 */
	private static final int DAY_MAX = 31;

	/**
	 * Minimum value for year.
	 */
	private static final int YEAR_MIN = 0;
	/**
	 * Minimum value for month.
	 */
	private static final int MONTH_MIN = 1;
	/**
	 * Minimum value for day.
	 */
	private static final int DAY_MIN = 1;

	/**
	 * Creates a WPartialDateField with no date specified.
	 */
	public WPartialDateField() {
		// Do Nothing
	}

	/**
	 * Creates a WPartialDateField with the specified date.
	 *
	 * @param day A number from 1 to 31 or null if unknown.
	 * @param month A number from 1 to 12, or null if unknown.
	 * @param year A number, or null if unknown.
	 */
	public WPartialDateField(final Integer day, final Integer month, final Integer year) {
		// Validate Year
		if (!isValidYear(year)) {
			throw new IllegalArgumentException(
					"Invalid partial year value (" + year + "). Year should be between "
					+ YEAR_MIN + " to " + YEAR_MAX + ".");
		}

		// Validate Month
		if (!isValidMonth(month)) {
			throw new IllegalArgumentException(
					"Invalid partial month value (" + month + "). Month should be between "
					+ MONTH_MIN + " to " + MONTH_MAX + ".");
		}

		// Validate Day
		if (!isValidDay(day)) {
			throw new IllegalArgumentException(
					"Invalid partial day value (" + day + "). Day should be between "
					+ DAY_MIN + " to " + DAY_MAX + ".");
		}

		String formatted = formatPartialDateToString(day, month, year, DEFAULT_PADDING_CHAR);

		getComponentModel().setData(formatted);
	}

	/**
	 * Set the WPartialDateField with the given day, month and year. Each of the day, month and year parameters that
	 * make up the partial date are optional.
	 *
	 * @param day A number from 1 to 31 or null if unknown.
	 * @param month A number from 1 to 12, or null if unknown.
	 * @param year A number, or null if unknown.
	 */
	public void setPartialDate(final Integer day, final Integer month, final Integer year) {
		// Validate Year
		if (!isValidYear(year)) {
			throw new IllegalArgumentException("Setting invalid partial year value (" + year
					+ "). Year should be between " + YEAR_MIN + " to " + YEAR_MAX + ".");
		}

		// Validate Month
		if (!isValidMonth(month)) {
			throw new IllegalArgumentException("Setting invalid partial month value (" + month
					+ "). Month should be between " + MONTH_MIN + " to " + MONTH_MAX + ".");
		}

		// Validate Day
		if (!isValidDay(day)) {
			throw new IllegalArgumentException("Setting invalid partial day value (" + day
					+ "). Day should be between " + DAY_MIN + " to " + DAY_MAX + ".");
		}

		String formatted = formatPartialDateToString(day, month, year, getPaddingChar());

		setData(formatted);
		getOrCreateComponentModel().text = null;
		getOrCreateComponentModel().validDate = true;
	}

	/**
	 * @return the padding character used in the partial date value
	 */
	public char getPaddingChar() {
		return getComponentModel().paddingChar;
	}

	/**
	 * The padding character used in the partial date value. The default padding character is a space. If the padding
	 * character is a space, then the date value will be right trimmed to remove the trailing spaces.
	 *
	 * @param paddingChar the padding character used in the partial date value.
	 */
	public void setPaddingChar(final char paddingChar) {
		if (Character.isDigit(paddingChar)) {
			throw new IllegalArgumentException("Padding character should not be a digit.");
		}

		getOrCreateComponentModel().paddingChar = paddingChar;
	}

	// ================================
	// Action/Event handling
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// Valid date entered by the user
		String dateValue = getRequestValue(request);
		// Text entered by the user (An empty string is treated as null)
		String value = request.getParameter(getId());
		String text = (Util.empty(value)) ? null : value;

		// Current date value
		String currentDate = getValue();

		boolean changed = false;

		// If a "valid" date value has not been entered, then check if the "user text" has changed
		if (dateValue == null) {
			// User entered text
			changed = !Util.equals(text, getText()) || currentDate != null;
		} else {
			// Valid Date
			changed = !Util.equals(dateValue, currentDate);
		}

		if (changed) {
			getOrCreateComponentModel().text = text;
			getOrCreateComponentModel().validDate = dateValue != null || text == null;
			setData(dateValue);
		}

		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestValue(final Request request) {
		if (isPresent(request)) {
			// User entered a valid date
			String dateParam = request.getParameter(getId() + "-date");
			if (dateParam == null) {
				return null;
			}

			// Validate Transfer Date Format - YYYY-MM-DD
			if (dateParam.length() != INTERNAL_DATE_TOTAL_CHARS || dateParam.charAt(
					INTERNAL_DASH1_POS) != '-'
					|| dateParam.charAt(INTERNAL_DASH2_POS) != '-') {
				LOG.warn("Date parameter is not in the format yyyy-MM-dd (" + dateParam
						+ ") and will be treated as null.");
				return null;
			}

			// Transform YYYY-MM-DD to YYYYMMDD
			StringBuffer buf = new StringBuffer(DATE_TOTAL_CHARS);
			buf.append(dateParam.substring(INTERNAL_YEAR_START, INTERNAL_YEAR_END));
			buf.append(dateParam.substring(INTERNAL_MONTH_START, INTERNAL_MONTH_END));
			buf.append(dateParam.substring(INTERNAL_DAY_START, INTERNAL_DAY_END));

			String dateFormat = buf.toString();

			// Validate the date
			if (!isValidPartialDateStringFormat(dateFormat, THEME_PADDING_CHAR)) {
				LOG.warn("Date parameter ("
						+ dateParam
						+ ") could not be transformed from YYYY-MM-DD to the format YYYYMMDD and will be treated as null.");
				return null;
			}

			// Convert from Theme padding character to the correct padding character
			return dateFormat.replace(THEME_PADDING_CHAR, getPaddingChar());
		} else {
			return getValue();
		}
	}

	/**
	 * Sets the validation error message.
	 *
	 * @param message The errorMessage to set, or null to use the default error message.
	 */
	public void setInvalidDateErrorMessage(final String message) {
		getOrCreateComponentModel().errorMessage = message;
	}

	/**
	 * Override WInput's validateComponent to perform further validation on the date. A partial date is invalid if there
	 * was text submitted but no date components were parsed.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		super.validateComponent(diags);

		if (!isValidDate()) {
			diags.add(createErrorDiagnostic(getComponentModel().errorMessage, this));
		}
	}

	// ================================
	/**
	 * Set the WPartialDateField with the given java date.
	 *
	 * @param date the date
	 */
	public void setDate(final Date date) {
		if (date == null) {
			setPartialDate(null, null, null);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			Integer year = cal.get(Calendar.YEAR);
			Integer month = cal.get(Calendar.MONTH) + 1;
			Integer day = cal.get(Calendar.DAY_OF_MONTH);
			setPartialDate(day, month, year);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		Object data = getData();
		String value = data == null ? null : data.toString();

		// Empty date is treated as null
		if (Util.empty(value)) {
			return null;
		}

		// Check the format is correct - yyyyMMdd
		if (!isValidPartialDateStringFormat(value, getPaddingChar())) {
			throw new SystemException(
					"PartialDate data value (" + value + ") is not in the format yyyyMMdd");
		}

		return value;
	}

	/**
	 * Get the partial date as its formatted string.
	 * <p>
	 * The expected format of the partial date is "yyyyMMdd", where parts of the date that do not have a value are
	 * padded with the padding character.
	 * </p>
	 *
	 * @return the partial data formatted as a string, or null
	 */
	public String getPartialDate() {
		return getValue();
	}

	/**
	 * Returns the day of the month value.
	 *
	 * @return the day of the month, or null if unspecified.
	 */
	public Integer getDay() {
		String dateValue = getValue();

		if (dateValue != null && dateValue.length() == DAY_END) {
			return parseDateComponent(dateValue.substring(DAY_START, DAY_END), getPaddingChar());
		} else {
			return null;
		}
	}

	/**
	 * Returns the month value.
	 *
	 * @return the month, or null if unspecified.
	 */
	public Integer getMonth() {
		String dateValue = getValue();

		if (dateValue != null && dateValue.length() >= MONTH_END) {
			return parseDateComponent(dateValue.substring(MONTH_START, MONTH_END), getPaddingChar());
		} else {
			return null;
		}
	}

	/**
	 * Returns the year value.
	 *
	 * @return the year, or null if unspecified.
	 */
	public Integer getYear() {
		String dateValue = getValue();

		if (dateValue != null && dateValue.length() >= YEAR_END) {
			return parseDateComponent(dateValue.substring(YEAR_START, YEAR_END), getPaddingChar());
		} else {
			return null;
		}
	}

	/**
	 * Returns the java date value, else null if the value cannot be parsed.
	 *
	 * @return the java date or null
	 */
	public Date getDate() {
		if (getYear() != null && getMonth() != null && getDay() != null) {
			return DateUtilities.createDate(getDay(), getMonth(), getYear());
		}

		return null;
	}

	/**
	 * Retrieves the text as entered by the user. This is not necessarily a valid date.
	 *
	 * @return the text, as entered by the user.
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
		String dateValue = getValue();
		return dateValue == null ? getText() : dateValue;
	}

	/**
	 * Set the WPartialDateField with the given day, month and year. Each of the day, month and year parameters that
	 * make up the partial date are optional.
	 *
	 * @param day a number from 1 to 31 or null if unknown.
	 * @param month a number from 1 to 12, or null if unknown.
	 * @param year a number, or null if unknown.
	 * @param padding the padding character for the partial date
	 * @return the formatted partial date, or null if invalid
	 */
	private String formatPartialDateToString(final Integer day, final Integer month,
			final Integer year,
			final char padding) {
		if (year == null && month == null && day == null) {
			return null;
		}

		if (!isValidYear(year) || !isValidMonth(month) || !isValidDay(day)) {
			return null;
		}

		StringBuffer dateString = new StringBuffer(DATE_TOTAL_CHARS);

		append(dateString, year, YEAR_DIGITS, padding);
		append(dateString, month, MONTH_DIGITS, padding);
		append(dateString, day, DAY_DIGITS, padding);

		// TRIM trailing spaces (will only "trim" if the padding character is a space)
		String trimmed = Util.rightTrim(dateString.toString());
		return trimmed;
	}

	/**
	 * Check if the year component is valid.
	 *
	 * @param year A number, or null if unknown.
	 * @return true if valid, otherwise false
	 */
	private boolean isValidYear(final Integer year) {
		return (year == null || (year >= YEAR_MIN && year <= YEAR_MAX));
	}

	/**
	 * Check if the month component is valid.
	 *
	 * @param month a number from 1 to 12, or null if unknown.
	 * @return true if valid, otherwise false
	 */
	private boolean isValidMonth(final Integer month) {
		return (month == null || (month >= MONTH_MIN && month <= MONTH_MAX));
	}

	/**
	 * Check if the day component is valid.
	 *
	 * @param day A number from 1 to 31 or null if unknown.
	 * @return true if valid, otherwise false
	 */
	private boolean isValidDay(final Integer day) {
		return (day == null || (day >= DAY_MIN && day <= DAY_MAX));
	}

	/**
	 * Parses a component of a partial date.
	 *
	 * @param component the date component.
	 * @param padding the padding character.
	 * @return the parsed value, may be null.
	 */
	private boolean isValidPartialDateStringFormat(final String component, final char padding) {
		// Empty is not valid
		if (Util.empty(component)) {
			return false;
		}

		int length = component.length();

		// Check length YYYY, or YYYYMM, or YYYYMMDD
		if (length != YEAR_END && length != MONTH_END && length != DAY_END) {
			return false;
		}

		// Year - ???? or YYYY
		String year = component.substring(YEAR_START, YEAR_END);
		if (!isValidCharacters(year, padding) || !isValidYear(parseDateComponent(year, padding))) {
			return false;
		}

		// Month - ?? or MM
		if (component.length() >= MONTH_END) {
			String month = component.substring(MONTH_START, MONTH_END);
			if (!isValidCharacters(month, padding) || !isValidMonth(parseDateComponent(month,
					padding))) {
				return false;
			}
		}

		// Day - ?? or DD
		if (component.length() == DAY_END) {
			String day = component.substring(DAY_START, DAY_END);
			if (!isValidCharacters(day, padding) || !isValidDay(parseDateComponent(day, padding))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check the component is either all padding chars or all digit chars.
	 *
	 * @param component the date component.
	 * @param padding the padding character.
	 * @return true if the component is valid, otherwise false
	 */
	private boolean isValidCharacters(final String component, final char padding) {
		// Check the component is either all padding chars or all digit chars
		boolean paddingChars = false;
		boolean digitChars = false;
		for (int i = 0; i < component.length(); i++) {
			char chr = component.charAt(i);
			// Padding
			if (chr == padding) {
				if (digitChars) {
					return false;
				}
				paddingChars = true;
			} else if (chr >= '0' && chr <= '9') {  // Digit
				if (paddingChars) {
					return false;
				}
				digitChars = true;
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * Parses a component of a partial date.
	 *
	 * @param component the date component.
	 * @param padding the padding character.
	 * @return the parsed value, may be null.
	 */
	private Integer parseDateComponent(final String component, final char padding) {
		if (component != null && component.indexOf(padding) == -1) {
			try {
				return Integer.valueOf(component);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Appends a single date component to the given StringBuffer. Nulls are replaced with the padding char, and numbers
	 * are padded with zeros.
	 *
	 * @param buf the buffer to append to.
	 * @param num the number to append, may be null.
	 * @param digits the minimum number of digits to append.
	 * @param padding the padding character for null values
	 */
	private void append(final StringBuffer buf, final Integer num, final int digits,
			final char padding) {
		if (num == null) {
			for (int i = 0; i < digits; i++) {
				buf.append(padding);
			}
		} else {
			for (int digit = 1, test = 10; digit < digits; digit++, test *= 10) {
				if (num < test) {
					buf.append('0');
				}
			}

			buf.append(num);
		}
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * Creates a new component model appropriate for this component.
	 *
	 * @return a new PartialDateFieldModel.
	 */
	@Override
	protected PartialDateFieldModel newComponentModel() {
		return new PartialDateFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PartialDateFieldModel getComponentModel() {
		return (PartialDateFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PartialDateFieldModel getOrCreateComponentModel() {
		return (PartialDateFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the partial date.
	 */
	public static class PartialDateFieldModel extends InputModel {

		/**
		 * The text entered by the user.
		 */
		private String text;

		/**
		 * Flag to indicate if the text entered is a valid partial date.
		 */
		private boolean validDate = true;

		/**
		 * Character used to pad the partial date value.
		 */
		private char paddingChar = DEFAULT_PADDING_CHAR;

		/**
		 * The error message to display when the input fails the date validation check.
		 */
		private String errorMessage = InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID_PARTIAL_DATE;
	}
}
