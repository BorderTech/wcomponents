package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWMultiSelectList;
import com.github.bordertech.wcomponents.AbstractWSelectList;
import com.github.bordertech.wcomponents.AbstractWSingleSelectList;
import com.github.bordertech.wcomponents.Disableable;
import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SelectListUtil;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A logical condition that compares the trigger and its compare value.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractCompare extends AbstractCondition {

	/**
	 * The first argument (trigger).
	 */
	private final SubordinateTrigger trigger;

	/**
	 * The second argument (compare value).
	 */
	private final Object value;

	/**
	 * This date format is used internally to exchange dates between the client and server.
	 */
	private static final String INTERNAL_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Create a Compare condition with a trigger and compare value.
	 *
	 * @param trigger the trigger input field.
	 * @param value the value to use in the compare.
	 */
	public AbstractCompare(final SubordinateTrigger trigger, final Object value) {
		if (trigger == null) {
			throw new IllegalArgumentException("Trigger cannot be null.");
		}

		// Check Date and Number field trigger values
		if (value != null && !CompareType.MATCH.equals(getCompareType())) {
			// WNumberField trigger value must be a BigDecimal
			if (trigger instanceof WNumberField && !(value instanceof BigDecimal)) {
				throw new IllegalArgumentException(
						"The value for a WNumberField trigger must be null or a BigDecimal.");
			}

			// WDateField trigger value must be a Date
			if (trigger instanceof WDateField && !(value instanceof Date)) {
				throw new IllegalArgumentException(
						"The value for a WDateField trigger must be null or a Date.");
			}
		}

		this.trigger = trigger;
		this.value = value;
	}

	/**
	 * Determine the type of action.
	 *
	 * @return the action type.
	 */
	public abstract CompareType getCompareType();

	/**
	 * Compare the trigger and compare value.
	 *
	 * @return true if the trigger input's value compares to the compare value, otherwise false
	 */
	@Override
	protected boolean execute() {
		// Disabled triggers are always false
		if ((trigger instanceof Disableable) && ((Disableable) trigger).isDisabled()) {
			return false;
		}

		final Object triggerValue = getTriggerValue(null);
		final Object compareValue = getCompareValue();

		return executeCompare(triggerValue, compareValue);
	}

	/**
	 * Compare the trigger value from the request and compare value.
	 *
	 * @param request the request being processed.
	 * @return true if the trigger input's value compares to the compare value, otherwise false
	 */
	@Override
	protected boolean execute(final Request request) {
		// Disabled triggers are always false
		if ((trigger instanceof Disableable) && ((Disableable) trigger).isDisabled()) {
			return false;
		}

		final Object triggerValue = getTriggerValue(request);
		final Object compareValue = getCompareValue();

		return executeCompare(triggerValue, compareValue);
	}

	/**
	 * @param triggerValue the trigger value
	 * @param compareValue the compare value
	 * @return true if the values compare, otherwise false
	 */
	private boolean executeCompare(final Object triggerValue, final Object compareValue) {
		// If the trigger value is a list, check if any option in the list compares
		if (triggerValue instanceof List<?>) {
			final List<?> selected = (List<?>) triggerValue;
			for (Object option : selected) {
				if (doCompare(option, compareValue)) {
					return true;
				}
			}
			return false;
		} else {
			return doCompare(triggerValue, compareValue);
		}
	}

	/**
	 * Return true if the two values compare.
	 *
	 * @param aVal the trigger value
	 * @param bVal the compare value
	 * @return true if the values compare, otherwise false
	 */
	protected abstract boolean doCompare(final Object aVal, final Object bVal);

	/**
	 * Get the value for the trigger.
	 * <p>
	 * If no request is passed in, the current value of the trigger is used.
	 * </p>
	 * <p>
	 * It will return the same "value" the client would have used in its subordinate logic.
	 * </p>
	 * <p>
	 * The trigger value will either be (i) a date formatted String for WDateFields, (ii) a BigDecimal for WNumberFields
	 * or (iii) a List of String values for MultiSelect components or (iv) a String value.
	 * </p>
	 *
	 * @param request the request being processed, can be null
	 * @return the value to be used for the trigger
	 */
	protected Object getTriggerValue(final Request request) {
		// Date Compare (Use Date Formatted String - YYYY-MM-DD)
		if (trigger instanceof WDateField) {
			final WDateField input = (WDateField) trigger;
			Date date;
			if (request == null) {
				date = input.getValue();
			} else {
				date = input.getRequestValue(request);
			}
			return date == null ? null : new SimpleDateFormat(INTERNAL_DATE_FORMAT).format(date);
		} else if (trigger instanceof WNumberField) { // Number Compare (Use Number Object)
			final WNumberField input = (WNumberField) trigger;
			if (request == null) {
				return input.getValue();
			} else {
				return input.getRequestValue(request);
			}
		} else if (trigger instanceof AbstractWSingleSelectList) { // String Compare for Single Select Lists (Use the Option's Code)
			final AbstractWSingleSelectList list = (AbstractWSingleSelectList) trigger;
			final Object selected;
			if (request == null) {
				selected = list.getValue();
			} else {
				selected = list.getRequestValue(request);
			}
			// Convert selected option to its "code" (Should always have a value)
			String code = list.optionToCode(selected);
			return code;
		} else if (trigger instanceof AbstractWMultiSelectList) { // String Compare for Multi Select Lists (Use the Option's Code)
			final AbstractWMultiSelectList list = (AbstractWMultiSelectList) trigger;
			final List<?> selected;
			if (request == null) {
				selected = list.getValue();
			} else {
				selected = list.getRequestValue(request);
			}
			// Empty is treated the same as null
			if (selected == null || selected.isEmpty()) {
				return null;
			}

			// Convert selected options to their "code" (Should always have a value)
			List<String> codes = new ArrayList<>(selected.size());
			for (Object select : selected) {
				String code = list.optionToCode(select);
				codes.add(code);
			}
			return codes;
		} else if (trigger instanceof Input) { // String Compare - Use the String Value of the Input
			final Input input = (Input) trigger;
			final Object inputValue;
			if (request == null) {
				inputValue = input.getValue();
			} else {
				inputValue = input.getRequestValue(request);
			}
			// Treat empty the same as null
			return (inputValue == null || Util.empty(inputValue.toString())) ? null : inputValue.
					toString();
		} else {
			throw new SystemException("Trigger is not a valid type.");
		}
	}

	/**
	 * Get the value to use in the compare.
	 * <p>
	 * It will return the same "value" the client would have used in its subordinate logic.
	 * </p>
	 * <p>
	 * The compare value will either be (i) a date formatted String for WDateFields, (ii) a BigDecimal for WNumberFields
	 * or (iii) a String value.
	 * </p>
	 *
	 * @return the value to be used for the compare.
	 */
	protected Object getCompareValue() {
		// Date Compare (Use Date Formatted String - YYYY-MM-DD)
		if (trigger instanceof WDateField) {
			return value == null ? null : new SimpleDateFormat(INTERNAL_DATE_FORMAT).format(value);
		} else if (trigger instanceof WNumberField) { // Number Compare (Use Number Object)
			return value;
		} else if (trigger instanceof AbstractWSelectList) {  // String Compare - List (Use the Option's Code)
			final AbstractWSelectList listTrigger = (AbstractWSelectList) trigger;
			final List<?> options = listTrigger.getOptions();

			// No options, just return the compare value (so that a test against null works correctly)
			if (options == null || options.isEmpty()) {
				return value == null ? null : value.toString();
			}

			// Check if the value is a valid option allowing for "Legacy" matching
			if (SelectListUtil.containsOptionWithMatching(options, value)) {
				Object option = SelectListUtil.getOptionWithMatching(options, value);
				String code = listTrigger.optionToCode(option);
				return code;
			}

			// Return the value as a String - Treat empty the same as null
			return (value == null || Util.empty(value.toString())) ? null : value.toString();
		} else if (trigger instanceof RadioButtonGroup && value instanceof WRadioButton) {
			// String Compare for RadioButtonGroup and value is WRadioButton (Use the button value)
			// Note - This is only for backward compatibility where projects have used a radio button
			// in the trigger. Projects should use the value expected, not the radio button.
			// If the radio button passed into the compare is used in a repeater, then this compare will not work.

			String data = ((WRadioButton) value).getValue();
			// Treat empty the same as null
			return Util.empty(data) ? null : data;
		} else { // String Compare
			// Treat empty the same as null
			return (value == null || Util.empty(value.toString())) ? null : value.toString();
		}
	}

	/**
	 * Get the value to paint for the compare value.
	 *
	 * @return the value to paint for the compare value.
	 */
	public String getComparePaintValue() {
		Object data = getCompareValue();
		return data == null ? "" : data.toString();
	}

	/**
	 * @return the trigger.
	 */
	public SubordinateTrigger getTrigger() {
		return trigger;
	}

	/**
	 * @return the value to use in the compare.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * An enumerated class for the type of compares.
	 */
	public enum CompareType {
		/**
		 * Equal compare.
		 */
		EQUAL,
		/**
		 * Not equal compare.
		 */
		NOT_EQUAL,
		/**
		 * Less than compare.
		 */
		LESS_THAN,
		/**
		 * Less than or equal compare.
		 */
		LESS_THAN_OR_EQUAL,
		/**
		 * Greater than compare.
		 */
		GREATER_THAN,
		/**
		 * Greater than or equal compare.
		 */
		GREATER_THAN_OR_EQUAL,
		/**
		 * Regular expression compare.
		 */
		MATCH
	}

}
