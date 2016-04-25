package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This class can be used to validate that a WDateField is "before" or "after" a particular "pivot" date. If no pivot
 * date is supplied then "today" is used as the default.
 *
 * @author Martin Schevchenko
 * @since 1.0.0
 */
public class DateFieldPivotValidator extends AbstractFieldValidator {

	// Available operators
	/**
	 * Indicates that the date in the input field must be before the pivot date.
	 */
	public static final int BEFORE = 1;

	/**
	 * Indicates that the date in the input field must be before or equal to the pivot date.
	 */
	public static final int BEFORE_OR_EQUAL = 2;

	/**
	 * Indicates that the date in the input field must be equal to the pivot date.
	 */
	public static final int EQUAL = 3;

	/**
	 * Indicates that the date in the input field must be after or equal to the pivot date.
	 */
	public static final int AFTER_OR_EQUAL = 4;

	/**
	 * Indicates that the date in the input field must be after the pivot date.
	 */
	public static final int AFTER = 5;

	/**
	 * A fixed point in time around which the date field will be compared.
	 */
	private Date fixedPivot;

	/**
	 * A date field which supplies a variable point in time around which the date field will be compared.
	 */
	private WDateField variablePivot;

	/**
	 * The chosen comparison operation.
	 */
	private int operator;

	/**
	 * Creates a DateFieldPivotValidator. The default validation is that the field must be "before or equal to today".
	 */
	public DateFieldPivotValidator() {
		this(BEFORE_OR_EQUAL);
	}

	/**
	 * Creates a DateFieldPivotValidator. As no "pivot" date is supplied, the operation will be relative to "today".
	 *
	 * @param operator the operator which controls how the dates will be compared.
	 *    Must be one of {@link #BEFORE}, {@link #BEFORE_OR_EQUAL}, {@link #EQUAL}, {@link #AFTER_OR_EQUAL} or {@link #AFTER}.
	 */
	public DateFieldPivotValidator(final int operator) {
		this(operator, (Date) null);
	}

	/**
	 * Creates a DateFieldPivotValidator which compares against the given fixed date.
	 *
	 * @param operator the operator which controls how the dates will be compared.
	 *    Must be one of {@link #BEFORE}, {@link #BEFORE_OR_EQUAL}, {@link #EQUAL}, {@link #AFTER_OR_EQUAL} or {@link #AFTER}.
	 *
	 * @param pivot the date to compare against.
	 */
	public DateFieldPivotValidator(final int operator, final Date pivot) {
		setOperator(operator);
		setFixedPivot(pivot);
	}

	/**
	 * Creates a DateFieldPivotValidator which compares against the a variable date.
	 *
	 * @param operator the operator which controls how the dates will be compared.
	 *    Must be one of {@link #BEFORE}, {@link #BEFORE_OR_EQUAL}, {@link #EQUAL}, {@link #AFTER_OR_EQUAL} or {@link #AFTER}.
	 *
	 * @param pivot the date field which will supply the date to compare against.
	 */
	public DateFieldPivotValidator(final int operator, final WDateField pivot) {
		super(InternalMessages.DEFAULT_VALIDATION_ERROR_INVALID);
		setOperator(operator);
		setVariablePivot(pivot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		String errorMessage = super.getErrorMessage();

		if (!Util.empty(errorMessage)) {
			return errorMessage;
		}

		if (variablePivot == null && fixedPivot == null) {
			switch (operator) {
				case BEFORE:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_BEFORE_TODAY;
				case BEFORE_OR_EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_BEFORE_OR_EQUAL_TODAY;
				case EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_EQUAL_TODAY;
				case AFTER_OR_EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL_TODAY;
				case AFTER:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER_TODAY;

				default:
					throw new SystemException("Unknown operator. [" + operator + "]");
			}
		} else {
			switch (operator) {
				case BEFORE:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_BEFORE;
				case BEFORE_OR_EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_BEFORE_OR_EQUAL;
				case EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_EQUAL;
				case AFTER_OR_EQUAL:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL;
				case AFTER:
					return InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_AFTER;
				default:
					throw new SystemException("Unknown operator. [" + operator + "]");
			}
		}
	}

	/**
	 * Sets the fixed date which will be compared against.
	 *
	 * @param fixedPivot the date.
	 */
	public void setFixedPivot(final Date fixedPivot) {
		this.fixedPivot = fixedPivot;
		this.variablePivot = null;
	}

	/**
	 * Sets a variable date which will be compared against.
	 *
	 * @param variablePivot the date field which supplies the date.
	 */
	public void setVariablePivot(final WDateField variablePivot) {
		this.variablePivot = variablePivot;
		this.fixedPivot = null;
	}

	/**
	 * Sets the operator which controls date validation.
	 *
	 * @param operator the operator which controls how the dates will be compared.
	 *    Must be one of {@link #BEFORE}, {@link #BEFORE_OR_EQUAL}, {@link #EQUAL}, {@link #AFTER_OR_EQUAL} or {@link #AFTER}.
	 */
	public void setOperator(final int operator) {
		this.operator = operator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isValid() {
		// Get the date we are validating.
		WDateField dateField = (WDateField) this.getInputField();
		Date date = dateField.getDate();

		if (date == null) {
			// No date, so nothing to validate.
			return true;
		}

		// Determine the pivot date
		Date pivot = null;

		if (variablePivot != null) {
			pivot = variablePivot.getDate();

			if (pivot == null) {
				// No pivot value, so default to true.
				return true;
			}
		} else if (fixedPivot != null) {
			pivot = fixedPivot;
		}

		// We take a null pivot date as meaning "today"
		if (pivot == null) {
			pivot = new Date();
		}

		// Round the dates to nearest day.
		pivot = DateUtilities.roundToDay(pivot);
		date = DateUtilities.roundToDay(date);

		// Perform the comparison with the pivot
		switch (operator) {
			case BEFORE:
				return date.before(pivot);
			case BEFORE_OR_EQUAL:
				return !pivot.before(date);
			case EQUAL:
				return date.equals(pivot);
			case AFTER_OR_EQUAL:
				return !pivot.after(date);
			case AFTER:
				return date.after(pivot);
			default:
				throw new SystemException("Unknown operator. [" + operator + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Serializable> getMessageArguments() {
		List<Serializable> args = super.getMessageArguments();

		if (variablePivot != null) {
			args.add(variablePivot.getDate());
		} else if (fixedPivot != null) {
			args.add(fixedPivot);
		}

		return args;
	}
}
