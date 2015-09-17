package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractInput;
import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.AbstractWMultiSelectList;
import com.github.bordertech.wcomponents.AbstractWSingleSelectList;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SubordinateTrigger;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test cases for {@link AbstractCompare}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractCompare_Test {

	/**
	 * This date format is used internally to exchange dates between the client and server.
	 */
	private static final String INTERNAL_DATE_FORMAT = "yyyy-MM-dd";

	@Test
	public void testConstructors() {
		AbstractCompare compare;

		// --------------------------
		// Constructor - Valid Value
		SubordinateTrigger trigger = new MyInput();
		String value = "test";
		compare = new MyCompare(trigger, value);

		Assert.assertEquals("Invalid trigger returned", trigger, compare.getTrigger());
		Assert.assertEquals("Invalid compare value returned", value, compare.getValue());

		// --------------------------
		// Constructor - Null Value
		trigger = new MyInput();
		compare = new MyCompare(trigger, null);

		Assert.assertEquals("Invalid trigger returned", trigger, compare.getTrigger());
		Assert.assertNull("Invalid compare value returned", compare.getValue());

		// --------------------------
		// Constructor - Null Trigger
		try {
			compare = new MyCompare(null, "Test");
			Assert.fail("A null trigger should not be allowed");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Exception message for null trigger should not be null", e.
					getMessage());
		}

		// --------------------------
		// Constructor - DateField and Valid Value
		SubordinateTrigger dateTrigger = new WDateField();
		Date dateValue = new Date();
		compare = new MyCompare(dateTrigger, dateValue);

		Assert.assertEquals("Invalid date trigger returned", dateTrigger, compare.getTrigger());
		Assert.assertEquals("Invalid date compare value returned", dateValue, compare.getValue());

		// --------------------------
		// Constructor - DateField and Invalid Value
		try {
			compare = new MyCompare(new WDateField(), "Invalid Date");
			Assert.fail("A datefield trigger and invalid date value should not be allowed");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Exception message for datefield trigger and invalid date should not be null",
					e.getMessage());
		}

		// --------------------------
		// Constructor - NumberField and Valid Value
		SubordinateTrigger numberTrigger = new WNumberField();
		BigDecimal numberValue = BigDecimal.valueOf(5);
		compare = new MyCompare(numberTrigger, numberValue);

		Assert.assertEquals("Invalid number trigger returned", numberTrigger, compare.getTrigger());
		Assert.
				assertEquals("Invalid number compare value returned", numberValue, compare.
						getValue());

		// Constructor - NumberField and Invalid Value
		try {
			compare = new MyCompare(new WNumberField(), "Invalid Number");
			Assert.fail("A numberField trigger and invalid number value should not be allowed");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Exception message for numberField trigger and invalid number should not be null",
					e.getMessage());
		}

	}

	@Test
	public void testExecute() {
		String value = "test";

		MyInput trigger = new MyInput();
		trigger.setData(value);

		// Setup Compare to be "true"
		AbstractCompare compare = new MyCompare(trigger, value);
		Assert.assertTrue("Compare of equal trigger and value should be true", compare.execute());
		Assert.assertTrue("Compare of equal trigger and value should be true with request",
				compare.execute(new MockRequest()));

		// Disabled trigger should make the compare false
		trigger.setDisabled(true);
		Assert.assertFalse("Compare of equal disabled trigger and value should be false", compare.
				execute());
		Assert.assertFalse(
				"Compare of equal disabled trigger and value should be false with request",
				compare.execute(new MockRequest()));
	}

	@Test
	public void testExecuteTriggerWithListOfValues() {
		MyMultiSelectList trigger = new MyMultiSelectList(Arrays.asList(
				new String[]{"a", "b", "c", "d", "e"}), true);

		// Test for option "b"
		AbstractCompare compare = new MyCompare(trigger, "b");

		// True
		trigger.setSelected(Arrays.asList(new String[]{"b"}));
		Assert.assertTrue("Compare using multiSelect with option 'b' condition should be true",
				compare.execute());

		// True - Multiple selected
		trigger.setSelected(Arrays.asList(new String[]{"a", "b", "e"}));
		Assert.assertTrue("Compare using multiSelect with option 'b' condition should be true",
				compare.execute());

		// False
		trigger.setSelected(Arrays.asList(new String[]{"c"}));
		Assert.assertFalse("Compare using multiSelect with option 'b' condition should be false",
				compare.execute());

		// False - Multiple selected
		trigger.setSelected(Arrays.asList(new String[]{"c", "d", "e"}));
		Assert.assertFalse("Compare using multiSelect with option 'b' condition should be false",
				compare.execute());

		// Nothing Selected
		trigger.setSelected(null);
		Assert.assertFalse("Compare using multiSelect with option 'b' condition should be false",
				compare.execute());

		// NULL Test
		compare = new Equal(trigger, null);

		// False
		trigger.setSelected(Arrays.asList(new String[]{"b"}));
		Assert.assertFalse("Compare using multiSelect with null condition should be false", compare.
				execute());

		// False - Multiple selected
		trigger.setSelected(Arrays.asList(new String[]{"c", "d", "e"}));
		Assert.assertFalse("Compare using multiSelect with null condition should be false", compare.
				execute());

		// True - Nothing Selected
		trigger.setSelected(null);
		Assert.assertTrue("Compare using multiSelect with null condition should be true", compare.
				execute());
	}

	@Test
	public void testGetTriggerValueDateField() {
		WDateField trigger = new WDateField();
		AbstractCompare compare = new MyCompare(trigger, null);

		// No Value
		trigger.setDate(null);

		Assert.assertNull("DateField Trigger Value - should be null for null date", compare.
				getTriggerValue(null));

		// Value
		Date date = new Date();
		String dateString = new SimpleDateFormat(INTERNAL_DATE_FORMAT).format(date);

		trigger.setDate(date);

		Assert.assertEquals(
				"DateField Trigger Value - should be the formatted string of the date value",
				dateString,
				compare.getTriggerValue(null));

		// Value on the Request
		date = DateUtilities.createDate(03, 02, 2001);
		dateString = new SimpleDateFormat(INTERNAL_DATE_FORMAT).format(date);

		MockRequest request = new MockRequest();
		request.setParameter(trigger.getId(), "03 FEB 2001");
		request.setParameter(trigger.getId() + "-date", dateString);

		Assert
				.assertEquals(
						"DateField Trigger Value - should be the formatted string of the date value on the request",
						dateString, compare.getTriggerValue(request));
	}

	@Test
	public void testGetTriggerValueNumberField() {
		WNumberField trigger = new WNumberField();
		AbstractCompare compare = new MyCompare(trigger, null);

		// No Value
		trigger.setNumber(null);

		Assert.assertNull("NumberField Trigger Value - should be null for null number", compare.
				getTriggerValue(null));

		// Value
		BigDecimal value = BigDecimal.valueOf(5);
		trigger.setNumber(value);

		Assert.assertEquals("NumberField Trigger Value - should be the number value", value,
				compare.getTriggerValue(null));

		// Value on the Request
		value = BigDecimal.valueOf(200);

		MockRequest request = new MockRequest();
		request.setParameter(trigger.getId(), value.toString());

		Assert.assertEquals("NumberField Trigger Value - should be the value on the request", value,
				compare.getTriggerValue(request));
	}

	@Test
	public void testGetTriggerValueSingleSelectLists() {
		List<Boolean> options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		MySingleSelectList trigger = new MySingleSelectList(options, true);

		AbstractCompare compare = new MyCompare(trigger, null);

		// No Value
		trigger.setSelected(null);

		Assert.assertNull("SingleSelectList Trigger Value - should be null for null data",
				compare.getTriggerValue(null));

		// Value
		trigger.setSelected(Boolean.TRUE);
		String valueCode = trigger.optionToCode(Boolean.TRUE);

		Assert.assertEquals(
				"SingleSelectList Trigger Value - should be the code value of the selected option",
				valueCode, compare.getTriggerValue(null));

		// Value on the Request
		valueCode = trigger.optionToCode(Boolean.FALSE);

		MockRequest request = new MockRequest();
		request.setParameter(trigger.getId() + "-h", "x");
		request.setParameter(trigger.getId(), valueCode);

		Assert.assertEquals(
				"SingleSelectList Trigger Value - should be the code value on the option on the request",
				valueCode, compare.getTriggerValue(request));
	}

	@Test
	public void testGetTriggerValueMultiSelectLists() {
		List<Boolean> options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		MyMultiSelectList trigger = new MyMultiSelectList(options, true);

		AbstractCompare compare = new MyCompare(trigger, null);

		// No Value
		trigger.setSelected(null);

		Assert
				.assertNull("MultiSelectList Trigger Value - should be null for null data", compare.
						getTriggerValue(null));

		// Value
		trigger.setSelected(Arrays.asList(Boolean.TRUE));

		List<String> valueCodes = Arrays.asList(trigger.optionToCode(Boolean.TRUE));
		Assert
				.assertEquals(
						"MultiSelectList Trigger Value - should be a list of the code value of the selected option",
						valueCodes, compare.getTriggerValue(null));

		// Value on the Request
		valueCodes = Arrays.asList(trigger.optionToCode(Boolean.FALSE));

		MockRequest request = new MockRequest();
		request.setParameter(trigger.getId() + "-h", "x");
		request.setParameter(trigger.getId(), trigger.optionToCode(Boolean.FALSE));

		Assert.assertEquals(
				"MultiSelectList Trigger Value - should be the code value on the option on the request",
				valueCodes, compare.getTriggerValue(request));
	}

	@Test
	public void testGetTriggerValueInput() {
		MyInput trigger = new MyInput();
		AbstractCompare compare = new MyCompare(trigger, null);

		// No Value
		trigger.setData(null);

		Assert.assertNull("Input Trigger Value - should be null for null data", compare.
				getTriggerValue(null));

		// Empty Value
		trigger.setData("");

		Assert.assertNull("Input Trigger Value - should be null for empty data", compare.
				getTriggerValue(null));

		// Value
		trigger.setData(Boolean.TRUE);

		Assert.assertEquals("Input Trigger Value - should be the string value of the data",
				Boolean.TRUE.toString(),
				compare.getTriggerValue(null));

		// Value on the Request
		String value = "test value";

		MockRequest request = new MockRequest();
		request.setParameter(trigger.getId(), value);

		Assert.assertEquals("Input Trigger Value - should be the value on the request", value,
				compare.getTriggerValue(request));
	}

	@Test
	public void testGetTriggerValueInvalidTrigger() {
		SubordinateTrigger trigger = new MyInvalidTrigger();
		AbstractCompare compare = new MyCompare(trigger, null);

		try {
			compare.execute();
			Assert.fail("Should have thrown exception for invalid subordinate trigger.");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception for invalid subordinate trigger should have a message",
					e.getMessage());
		}
	}

	@Test
	public void testGetCompareValueDateField() {
		WDateField trigger = new WDateField();

		// Null Value
		AbstractCompare compare = new MyCompare(trigger, null);

		Assert.assertNull("DateField Compare Value - should be null for null date", compare.
				getCompareValue());

		// Value
		Date date = new Date();
		String dateString = new SimpleDateFormat(INTERNAL_DATE_FORMAT).format(date);

		compare = new MyCompare(trigger, date);

		Assert.assertEquals(
				"DateField Compare Value - should be the formatted string of the date value",
				dateString,
				compare.getCompareValue());
	}

	@Test
	public void testGetCompareValueNumberField() {
		WNumberField trigger = new WNumberField();

		// Null Value
		AbstractCompare compare = new MyCompare(trigger, null);

		Assert.assertNull("NumberField Compare Value - should be null for null number", compare.
				getCompareValue());

		// Value
		BigDecimal value = BigDecimal.valueOf(5);

		compare = new MyCompare(trigger, value);

		Assert.assertEquals("NumberField Compare Value - should be the number value", value,
				compare.getCompareValue());
	}

	@Test
	public void testGetCompareValueRadioButtonGroup() {
		RadioButtonGroup trigger = new RadioButtonGroup();

		// Null Value
		AbstractCompare compare = new MyCompare(trigger, null);

		Assert.assertNull("RadioButtonGroup Compare Value - should be null for null value", compare.
				getCompareValue());

		// Empty Value
		compare = new MyCompare(trigger, "");
		Assert.assertNull("RadioButtonGroup Compare Value - should be null for empty value",
				compare.getCompareValue());

		// Value
		compare = new MyCompare(trigger, Boolean.FALSE);

		Assert.assertEquals("RadioButtonGroup Compare Value - should be the string of the value",
				Boolean.FALSE.toString(), compare.getCompareValue());

		// Button - Null Value
		compare = new MyCompare(trigger, trigger.addRadioButton(null));
		Assert.assertNull(
				"RadioButtonGroup Compare Value - should be null for button with null value",
				compare.getCompareValue());

		// Button - Empty Value
		compare = new MyCompare(trigger, trigger.addRadioButton(""));
		Assert.assertNull(
				"RadioButtonGroup Compare Value - should be null for button with empty value",
				compare.getCompareValue());

		// Button - Value
		compare = new MyCompare(trigger, trigger.addRadioButton("B1"));
		Assert.assertEquals("RadioButtonGroup Compare Value - should be the string of the value",
				"B1", compare.getCompareValue());

	}

	@Test
	public void testGetCompareValueListComponent() {
		MyMultiSelectList trigger = new MyMultiSelectList(null, true);

		// No Options - null value
		AbstractCompare compare = new MyCompare(trigger, null);

		Assert.assertNull("NumberField Compare Value - should be null for null number", compare.
				getCompareValue());

		// Empty Options - null value
		trigger.setOptions(new ArrayList<String>());

		Assert.assertNull("NumberField Compare Value - should be null for null number", compare.
				getCompareValue());

		// No Options - with value
		compare = new MyCompare(trigger, Boolean.TRUE);
		Assert.assertEquals("List Component Compare Value - should be the string of the value",
				Boolean.TRUE.toString(), compare.getCompareValue());

		// Set Options
		List<Boolean> options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		trigger.setOptions(options);

		// Valid option
		compare = new MyCompare(trigger, Boolean.TRUE);
		String valueCode = trigger.optionToCode(Boolean.TRUE);

		Assert.assertEquals("List Component Compare Value - should be the code of the option",
				valueCode,
				compare.getCompareValue());

		// Valid option - legacy match
		compare = new MyCompare(trigger, "false");
		valueCode = trigger.optionToCode(Boolean.FALSE);
		Assert
				.assertEquals(
						"List Component Compare Value - should be the code of the option matched via legacy matching",
						valueCode, compare.getCompareValue());

		// Invalid option
		compare = new MyCompare(trigger, "X");
		Assert.assertEquals(
				"List Component Compare Value - should be the string of the invalid option", "X",
				compare.getCompareValue());

		// Invalid option (Empty)
		compare = new MyCompare(trigger, "");
		Assert.assertNull("List Component Compare Value - should null for empty invalid option",
				compare.getCompareValue());
	}

	@Test
	public void testGetCompareValueInput() {
		MyInput trigger = new MyInput();

		// Null Value
		AbstractCompare compare = new MyCompare(trigger, null);

		Assert.assertNull("Input Compare Value - should be null for null value", compare.
				getCompareValue());

		// Empty Value
		compare = new MyCompare(trigger, "");

		Assert.assertNull("Input Compare Value - should be null for empty value", compare.
				getCompareValue());

		// Value
		compare = new MyCompare(trigger, Boolean.TRUE);

		Assert.assertEquals("Input Compare Value - should be the string of the value", Boolean.TRUE.
				toString(),
				compare.getCompareValue());
	}

	@Test
	public void testGetComparePaintValue() {
		// Null Value
		AbstractCompare compare = new MyCompare(new MyInput(), null);

		Assert.assertEquals("Compare paint value should be empty string for null value", "",
				compare.getComparePaintValue());

		// Value
		compare = new MyCompare(new MyInput(), Boolean.TRUE);

		Assert.assertEquals("Compare paint value should be the string of the value", Boolean.TRUE.
				toString(),
				compare.getComparePaintValue());
	}

	/**
	 * Test class for AbstractCompare (Basic Equals).
	 */
	private static final class MyCompare extends AbstractCompare {

		/**
		 * @param trigger the trigger
		 * @param value the value to compare
		 */
		private MyCompare(final SubordinateTrigger trigger, final Object value) {
			super(trigger, value);
		}

		@Override
		public CompareType getCompareType() {
			return null;
		}

		@Override
		protected boolean doCompare(final Object aVal, final Object bVal) {
			// Basic Equals
			return Util.equals(aVal, bVal);
		}
	}

	/**
	 * Test class for SubordinateTrigger that is not supported.
	 */
	private static class MyInvalidTrigger extends AbstractWComponent implements SubordinateTrigger {
	}

	/**
	 * Test class for AbstractWSingleSelectList.
	 */
	private static final class MySingleSelectList extends AbstractWSingleSelectList implements
			SubordinateTrigger {

		/**
		 * @param options the list's options.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MySingleSelectList(final List<?> options, final boolean allowNoSelection) {
			super(options, allowNoSelection);
		}

		/**
		 * @param lookupTable the lookup table identifier to obtain the list's options from.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MySingleSelectList(final Object lookupTable, final boolean allowNoSelection) {
			super(lookupTable, allowNoSelection);
		}
	}

	/**
	 * Test class for AbstractWMultiSelectList.
	 */
	private static final class MyMultiSelectList extends AbstractWMultiSelectList implements
			SubordinateTrigger {

		/**
		 * @param options the list's options.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyMultiSelectList(final List<?> options, final boolean allowNoSelection) {
			super(options, allowNoSelection);
		}

		/**
		 * @param lookupTable the lookup table identifier to obtain the list's options from.
		 * @param allowNoSelection if true, allow no option to be selected
		 */
		private MyMultiSelectList(final Object lookupTable, final boolean allowNoSelection) {
			super(lookupTable, allowNoSelection);
		}
	}

	/**
	 * Test class for AbstractInput.
	 */
	private static class MyInput extends AbstractInput implements SubordinateTrigger {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doHandleRequest(final Request request) {
			Object value = getRequestValue(request);
			Object current = getValue();
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
		public Object getRequestValue(final Request request) {
			if (isPresent(request)) {
				return request.getParameter(getId());
			} else {
				return getValue();
			}
		}

	}

}
