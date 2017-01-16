package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * A WNumberField is a wcomponent used to display a numeric input field. Use the method "{@link #getValue()}" to get the
 * number entered into the field by the user.
 * </p>
 * <p>
 * Additional methods are available to return the value entered as an integer or decimal value, and there are methods
 * which can be used to restrict the range of values which are allowed to be entered.
 * </p>
 * <p>
 * A number field differs from a text field in the way in which some user agents interact with it. For example,
 * touchscreen devices may display a numeric data entry pad rather than an alphanumeric keyboard.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WNumberField extends AbstractInput implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger,
		SubordinateTarget {

	/**
	 * @return the number value, or the text entered by the user if there is no valid number.
	 */
	@Override
	public String getValueAsString() {
		BigDecimal value = getValue();
		return value == null ? getText() : value.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// Valid Number
		BigDecimal numberValue = getRequestValue(request);

		// Text entered by the user (An empty string is treated as null)
		String value = request.getParameter(getId());
		String text = (Util.empty(value)) ? null : value;

		// Current Value
		BigDecimal current = getValue();

		boolean changed;

		// If a "valid" number value has not been entered, then check if the "user text" has changed
		if (numberValue == null) {
			// User entered text
			changed = !Util.equals(text, getText()) || current != null;
		} else {
			// Valid Number
			changed = !Util.equals(numberValue, current);
		}

		if (changed) {
			boolean valid = numberValue != null || text == null;
			handleRequestValue(numberValue, valid, text);
		}

		return changed;
	}

	/**
	 * Set the request value.
	 *
	 * @param value the number value
	 * @param valid true if valid value
	 * @param text the user text
	 */
	protected void handleRequestValue(final BigDecimal value, final boolean valid, final String text) {
		// As setData() clears the text value (if valid), this must be called first so it can be set after
		setData(value);
		NumberFieldModel model = getOrCreateComponentModel();
		model.validNumber = valid;
		model.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getRequestValue(final Request request) {
		if (isPresent(request)) {
			String value = request.getParameter(getId());
			// An empty string is treated as null
			if (Util.empty(value)) {
				return null;
			}
			// Check number is valid
			try {
				return new BigDecimal(value);
			} catch (NumberFormatException ex) {
				return null;
			}
		} else {
			return getValue();
		}
	}

	/**
	 * Retrieves the numeric value of this field.
	 *
	 * @return the numeric value, or null if the field does not contain a valid number.
	 */
	@Override
	public BigDecimal getValue() {
		return convertValue(getData());
	}

	/**
	 * Attempts to convert a value to a BigDecimal. Throws a SystemException on error.
	 *
	 * @param value the value to convert.
	 * @return the converted value, or null if <code>value</code> was null/empty.
	 */
	private BigDecimal convertValue(final Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		// Try and convert "String" value
		String dataString = value.toString();
		if (Util.empty(dataString)) {
			return null;
		}

		try {
			return new BigDecimal(dataString);
		} catch (NumberFormatException ex) {
			throw new SystemException(
					"Could not convert data of type " + value.getClass() + " with String value "
					+ dataString + " to BigDecimal", ex);
		}
	}

	// ================================
	// Attributes
	/**
	 * Retrieves the text as entered by the user. This is not necessarily a valid date.
	 *
	 * @return the text, as entered by the user.
	 */
	public String getText() {
		return getComponentModel().text;
	}

	/**
	 * Indicates whether the text value held in this field is a valid number.
	 *
	 * @return true if the field contains text which is a valid number, false otherwise.
	 */
	public boolean isValidNumber() {
		return getComponentModel().validNumber;
	}

	/**
	 * Retrieves the numeric value of this field.
	 *
	 * @return the numeric value, or null if the field does not contain a valid number.
	 */
	public BigDecimal getNumber() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final Object data) {
		// This override is necessary to maintain other internal state
		NumberFieldModel model = getOrCreateComponentModel();

		try {
			super.setData(convertValue(data));
			model.text = null;
			model.validNumber = true;
		} catch (SystemException e) {
			super.setData(data);
			model.text = data.toString();
			model.validNumber = false;
		}
	}

	/**
	 * Sets the value of this number field.
	 *
	 * @param value the value to set.
	 */
	public void setNumber(final BigDecimal value) {
		setData(value);
	}

	/**
	 * Sets the value of this number field.
	 *
	 * @param value the value.
	 */
	public void setNumber(final long value) {
		setNumber(new BigDecimal(value));
	}

	/**
	 * Sets the value of this number field.
	 *
	 * @param value the value to set.
	 */
	public void setNumber(final double value) {
		setNumber(BigDecimal.valueOf(value));
	}

	/**
	 * Sets the value of this number field.
	 *
	 * @param value the value.
	 * @deprecated Use {@link #setNumber(long)}
	 */
	@Deprecated
	public void setValue(final long value) {
		setNumber(value);
	}

	/**
	 * Sets the value of this number field.
	 *
	 * @param value the value to set.
	 * @deprecated Use {@link #setNumber(double)}
	 */
	@Deprecated
	public void setValue(final double value) {
		setNumber(value);
	}

	/**
	 * Retrieves the minimum allowable value for this number field. The minimum value is enforced server-side using the
	 * WComponent validation framework, and <b>may</b> be enforced client-side.
	 *
	 * @return the minimum allowable value, or null if there is no minimum.
	 */
	public BigDecimal getMinValue() {
		return getComponentModel().minValue;
	}

	/**
	 * Sets the minimum allowable value for this number field.
	 *
	 * @param minValue the minimum allowable value.
	 */
	public void setMinValue(final long minValue) {
		setMinValue(BigDecimal.valueOf(minValue));
	}

	/**
	 * Sets the minimum allowable value for this number field.
	 *
	 * @param minValue the minimum allowable value.
	 */
	public void setMinValue(final double minValue) {
		setMinValue(BigDecimal.valueOf(minValue));
	}

	/**
	 * Sets the minimum allowable value for this number field.
	 *
	 * @param minValue the minimum allowable value, or null for no minimum.
	 */
	public void setMinValue(final BigDecimal minValue) {
		getOrCreateComponentModel().minValue = minValue;
	}

	/**
	 * Retrieves the maximum allowable value for this number field. The minimum value is enforced server-side using the
	 * WComponent validation framework, and <b>may</b> be enforced client-side.
	 *
	 * @return the maximum allowable value, or null if there is no maximum.
	 */
	public BigDecimal getMaxValue() {
		return getComponentModel().maxValue;
	}

	/**
	 * Sets the maximum allowable value for this number field.
	 *
	 * @param maxValue the maximum allowable value.
	 */
	public void setMaxValue(final long maxValue) {
		setMaxValue(BigDecimal.valueOf(maxValue));
	}

	/**
	 * Sets the maximum allowable value for this number field.
	 *
	 * @param maxValue the maximum allowable value.
	 */
	public void setMaxValue(final double maxValue) {
		setMaxValue(BigDecimal.valueOf(maxValue));
	}

	/**
	 * Sets the maximum allowable value for this number field.
	 *
	 * @param maxValue the maximum allowable value, or null for no maximum.
	 */
	public void setMaxValue(final BigDecimal maxValue) {
		getOrCreateComponentModel().maxValue = maxValue;
	}

	/**
	 * Retrieves the step value for this number field. The step may be used by some user agents to provide a convenient
	 * increment/decrement function, such to a spinner control.
	 *
	 * @return the step value, or null if there is no step value set.
	 */
	public BigDecimal getStep() {
		return getComponentModel().step;
	}

	/**
	 * Sets the step value for this field.
	 *
	 * @param step the step value.
	 */
	public void setStep(final long step) {
		setStep(BigDecimal.valueOf(step));
	}

	/**
	 * Sets the step value for this field.
	 *
	 * @param step the step value.
	 */
	public void setStep(final double step) {
		setStep(BigDecimal.valueOf(step));
	}

	/**
	 * Sets the step value for this field.
	 *
	 * @param step the step value, or null to use the default step.
	 */
	public void setStep(final BigDecimal step) {
		getOrCreateComponentModel().step = step;
	}

	/**
	 * Retrieves the number of decimal places to use for this number field. A value of zero indicates that the fields
	 * should only accept integer values.
	 *
	 * @return the number of decimal places to use.
	 */
	public int getDecimalPlaces() {
		return getComponentModel().decimalPlaces;
	}

	/**
	 * Sets the number of decimal places to use for this field.
	 *
	 * @param decimalPlaces the number of decimal places.
	 */
	public void setDecimalPlaces(final int decimalPlaces) {
		if (decimalPlaces < 0) {
			throw new IllegalArgumentException("Decimal places must be >= 0");
		}

		getOrCreateComponentModel().decimalPlaces = decimalPlaces;
	}

	/**
	 * @return the width of the input field in characters.
	 * @deprecated 1.3 size not used as it is incompatible with HTML specification.
	 */
	@Deprecated
	public int getColumns() {
		return getComponentModel().columns;
	}

	/**
	 * Sets the width of the input field in characters.
	 *
	 * @param columns the number of characters to display.
	 * @deprecated 1.3 size not used as it is incompatible with HTML specification.
	 */
	@Deprecated
	public void setColumns(final int columns) {
		getOrCreateComponentModel().columns = columns;
	}

	/**
	 * Override WInput's validateComponent to perform futher validation on email addresses.
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	@Override
	protected void validateComponent(final List<Diagnostic> diags) {
		if (isValidNumber()) {
			super.validateComponent(diags);
			validateNumber(diags);
		} else {
			diags.add(createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID, this));
		}
	}

	/**
	 * <p>
	 * Performs validation of the number. Validation ensures that the entered text is a valid number, and is between the
	 * minimum/maximum values (if applicable).
	 * </p>
	 * <p>
	 * Subclasses can override this method to perform more specific validation.
	 * </p>
	 *
	 * @param diags the list into which any validation diagnostics are added.
	 */
	protected void validateNumber(final List<Diagnostic> diags) {
		BigDecimal value = getValue();
		if (value == null) {
			return;
		}
		BigDecimal min = getComponentModel().minValue;
		BigDecimal max = getComponentModel().maxValue;

		int decimals = getComponentModel().decimalPlaces;

		if (min != null && value.compareTo(min) < 0) {
			diags.add(createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MIN_VALUE,
					this, min));
		}

		if (max != null && value.compareTo(max) > 0) {
			diags.add(createErrorDiagnostic(InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_VALUE,
					this, max));
		}

		if (value.scale() > decimals) {
			diags.add(createErrorDiagnostic(
					InternalMessages.DEFAULT_VALIDATION_ERROR_MAX_DECIMAL_PLACES, this,
					decimals));
		}

	}

	/**
	 * Creates a new NumberFieldModel holds Extrinsic state management of the field.
	 *
	 * @return a new NumberFieldModel
	 */
	@Override
	protected NumberFieldModel newComponentModel() {
		return new NumberFieldModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected NumberFieldModel getComponentModel() {
		return (NumberFieldModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected NumberFieldModel getOrCreateComponentModel() {
		return (NumberFieldModel) super.getOrCreateComponentModel();
	}

	/**
	 * NumberFieldModel holds Extrinsic state management of the field.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class NumberFieldModel extends InputModel {

		/**
		 * The number of columns to display for the field.
		 * @deprecated 1.3 columns not used as it is incompatible with HTML specification.
		 */
		@Deprecated
		private int columns;

		/**
		 * The minimum value to allow.
		 */
		private BigDecimal minValue;

		/**
		 * The maximum value to allow.
		 */
		private BigDecimal maxValue;

		/**
		 * The "step" value used for increment/decrement.
		 */
		private BigDecimal step;

		/**
		 * The maximum number of decimal places to display/enter, defaults to zero (integer).
		 */
		private int decimalPlaces;

		/**
		 * The text entered by the user.
		 */
		private String text;

		/**
		 * Flag to indicate if the text entered is a valid partial date.
		 */
		private boolean validNumber = true;

		/**
		 * Maintain internal state.
		 */
		@Override
		public void resetData() {
			super.resetData();
			ComponentModel model = getSharedModel();
			if (model instanceof NumberFieldModel) {
				NumberFieldModel shared = (NumberFieldModel) model;
				this.text = shared.text;
				this.validNumber = shared.validNumber;
			}
		}

	}
}
