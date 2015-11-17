package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WNumberField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WNumberField_Test extends AbstractWComponentTestCase {

	/**
	 * Valid Number - user text.
	 */
	private static final String REQUEST_VALID_NUMBER_TEXT = "100.12";

	/**
	 * Valid Number - user text.
	 */
	private static final BigDecimal REQUEST_VALID_NUMBER_VALUE = new BigDecimal("100.12");

	/**
	 * Bad Number - user text.
	 */
	private static final String REQUEST_BAD_NUMBER_TEXT = "BAD NUMBER";

	@Test
	public void testGetValueAsString() {
		WNumberField numberField = new WNumberField();

		// Value and User Text null
		Assert.assertNull("getValue on a numberField should default to null", numberField.
				getValueAsString());

		// Valid number on request
		numberField = processDoHandleRequestWithValidNumber();
		Assert.assertEquals(
				"getValue should return the string value of the valid number on the request",
				REQUEST_VALID_NUMBER_VALUE.toString(), numberField.getValueAsString());

		// Bad number on request
		numberField = processDoHandleRequestWithBadNumber();
		Assert.assertEquals("getValue should return the user text for bad number on request",
				REQUEST_BAD_NUMBER_TEXT,
				numberField.getValueAsString());

		// Empty Request
		numberField = processDoHandleRequestWithEmptyRequest();
		Assert.assertNull("getValue should be null for an empty request", numberField.
				getValueAsString());

		// When set a number value, value string should be the value
		numberField.setNumber(REQUEST_VALID_NUMBER_VALUE);
		Assert.assertEquals("getValue should return the string value of the valid number when set",
				REQUEST_VALID_NUMBER_VALUE.toString(), numberField.getValueAsString());
	}

	@Test
	public void testDoHandleRequestChanged() {
		WNumberField numberField = new WNumberField();

		numberField.setLocked(true);
		setActiveContext(createUIContext());

		// Request with Empty Value and Field is null (no change)
		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), "");
		boolean changed = numberField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for empty request and field is null",
				changed);

		// Request with valid value and Field is null (changed)
		request = new MockRequest();
		request.setParameter(numberField.getId(), REQUEST_VALID_NUMBER_TEXT);

		changed = numberField.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should have returned true for request with valid value and field is null",
				changed);

		// Request with same value (no change)
		changed = numberField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same valid value",
				changed);

		// Request with invalid value (changed)
		request = new MockRequest();
		request.setParameter(numberField.getId(), REQUEST_BAD_NUMBER_TEXT);

		changed = numberField.doHandleRequest(request);
		Assert
				.assertTrue(
						"doHandleRequest should have returned true for request with different invalid value",
						changed);

		// Request with same invalid value (no change)
		changed = numberField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same invalid value",
				changed);

		// Request with empty value (changed)
		request = new MockRequest();
		request.setParameter(numberField.getId(), "");

		changed = numberField.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should have returned true for request going back to empty value",
				changed);

		// Request with same empty value (no change)
		changed = numberField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same empty value",
				changed);
	}

	@Test
	public void testDoHandleRequestEmptyScenarios() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with empty value
		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), "");

		// Scenario #1
		// Setup numberField with null value
		numberField.setNumber(null);

		// Empty Request and Field is null (no change)
		boolean changed = numberField.doHandleRequest(request);

		Assert.assertFalse(
				"Empty request and field is null - doHandleRequest should have returned false",
				changed);
		assertNumberFieldNullValue(numberField, "Empty request and field is null");

		// Scenario #2
		// Setup numberField with a valid number set
		numberField.setNumber(new BigDecimal(200));

		// Empty Request and Field has a valid number set (change)
		changed = numberField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has a valid number set - doHandleRequest should have returned true",
				changed);
		assertNumberFieldNullValue(numberField, "Empty request and field has a valid number set");

		// Scenario #3
		// Setup numberField with invalid number value
		MockRequest request2 = new MockRequest();
		request2.setParameter(numberField.getId(), REQUEST_BAD_NUMBER_TEXT);
		numberField.doHandleRequest(request2);

		// Empty Request and Field has an invalid number value (change)
		changed = numberField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has an invalid number - doHandleRequest should have returned true",
				changed);
		assertNumberFieldNullValue(numberField, "Empty request and field has an invalid number");

		// Scenario #4
		// Setup numberField with valid number value
		request2 = new MockRequest();
		request2.setParameter(numberField.getId(), REQUEST_VALID_NUMBER_TEXT);
		numberField.doHandleRequest(request2);

		// Empty Request and Field has a valid number value (change)
		changed = numberField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has a valid value - doHandleRequest should have returned true",
				changed);
		assertNumberFieldNullValue(numberField, "Empty request and field has a valid value");
	}

	@Test
	public void testDoHandleRequestBadNumberScenarios() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with Invalid Number
		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), REQUEST_BAD_NUMBER_TEXT);

		// Scenario #1
		// Setup numberField with null value
		numberField.setNumber(null);

		boolean changed = numberField.doHandleRequest(request);

		Assert.assertTrue(
				"Request with Bad Number and field is null - doHandleRequest should have returned true",
				changed);
		assertNumberFieldBadNumber(numberField, "Request with Bad Number and field is null");

		// Scenario #2
		// Setup numberField with a valid number set
		numberField.setNumber(new BigDecimal(200));

		changed = numberField.doHandleRequest(request);
		Assert
				.assertTrue(
						"Request with Bad Number and field has a valid number set - doHandleRequest should have returned true",
						changed);
		assertNumberFieldBadNumber(numberField,
				"Request with Bad Number and field has a valid number set");

		// Scenario #3
		// Setup numberField with DIFFERENT invalid number value
		MockRequest request2 = new MockRequest();
		request2.setParameter(numberField.getId(), "ANOTHER BAD NUMBER");
		numberField.doHandleRequest(request2);

		changed = numberField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Bad Number and field has a different invalid number - doHandleRequest should have returned true",
						changed);
		assertNumberFieldBadNumber(numberField,
				"Request with Bad Number and field has a different invalid number");

		// Scenario #4
		// Setup numberField with SAME invalid number value
		changed = numberField.doHandleRequest(request);

		Assert
				.assertFalse(
						"Request with Bad Number and field has same invalid number - doHandleRequest should have returned false",
						changed);
		assertNumberFieldBadNumber(numberField,
				"Request with Bad Number and field has same invalid number");

		// Scenario #5
		// Setup numberField with valid number value
		request2 = new MockRequest();
		request2.setParameter(numberField.getId(), REQUEST_VALID_NUMBER_TEXT);
		numberField.doHandleRequest(request2);

		changed = numberField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Bad Number and field has a valid value - doHandleRequest should have returned true",
						changed);
		assertNumberFieldBadNumber(numberField,
				"Request with Bad Number and field has a valid value");
	}

	@Test
	public void testDoHandleRequestValidNumberScenarios() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with Valid Number
		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), REQUEST_VALID_NUMBER_TEXT);

		// Scenario #1
		// Setup numberField with null value
		numberField.setNumber(null);

		boolean changed = numberField.doHandleRequest(request);

		Assert.assertTrue(
				"Request with Valid Number and field is null - doHandleRequest should have returned true",
				changed);
		assertNumbderFieldValidNumber(numberField, "Request with Valid Number and field is null");

		// Scenario #2
		// Setup numberField with a different valid number set
		numberField.setNumber(new BigDecimal(200));

		changed = numberField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Valid Number and field has a different valid number set - doHandleRequest should have returned true",
						changed);
		assertNumbderFieldValidNumber(numberField,
				"Request with Valid Number and field has a different valid number set");

		// Scenario #3
		// NumberField with same valid number set
		changed = numberField.doHandleRequest(request);

		Assert
				.assertFalse(
						"Request with Valid Number and field has same valid number set - doHandleRequest should have returned false",
						changed);
		assertNumbderFieldValidNumber(numberField,
				"Request with Valid Number and field has same valid number set");

		// Scenario #4
		// Setup numberField with invalid number value
		MockRequest request2 = new MockRequest();
		request2.setParameter(numberField.getId(), REQUEST_BAD_NUMBER_TEXT);
		numberField.doHandleRequest(request2);

		changed = numberField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Valid Number and field has an invalid number - doHandleRequest should have returned true",
						changed);
		assertNumbderFieldValidNumber(numberField,
				"Request with Valid Number and field has an invalid number");
	}

	@Test
	public void getRequestValue() throws ParseException {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);

		setActiveContext(createUIContext());

		// Set current value
		BigDecimal current = new BigDecimal(200);
		numberField.setNumber(current);

		// Empty Request (not present, should return current value)
		MockRequest request = new MockRequest();
		Assert.
				assertEquals(
						"Current value of the field should have been returned for empty request",
						current,
						numberField.getRequestValue(request));

		// Request with "empty" value (should return null)
		request = new MockRequest();
		request.setParameter(numberField.getId(), "");
		Assert.assertNull("Null should have been returned for request with empty value",
				numberField.getRequestValue(request));

		// Request with valid value
		request = new MockRequest();
		request.setParameter(numberField.getId(), REQUEST_VALID_NUMBER_TEXT);
		Assert.assertEquals("Value from the request should have been returned",
				REQUEST_VALID_NUMBER_VALUE,
				numberField.getRequestValue(request));
	}

	@Test
	public void testGetValue() {
		WNumberField numberField = new WNumberField();

		// Data is null
		Assert.assertNull("getValue should be null by default", numberField.getValue());

		// Data is a BigDecimal
		numberField.setData(REQUEST_VALID_NUMBER_VALUE);
		Assert.assertEquals("getValue incorrect value for BigDecimal type",
				REQUEST_VALID_NUMBER_VALUE,
				numberField.getValue());

		// Data is a Long
		BigDecimal value = new BigDecimal(100);
		numberField.setData(Long.valueOf(value.intValue()));
		Assert.assertEquals("getValue incorrect value for Long type", value, numberField.getValue());

		// Data is an empty string
		numberField.setData("");
		Assert.assertNull("getValue incorrect value for empty String type", numberField.getValue());

		// Data is a valid string
		numberField.setData(REQUEST_VALID_NUMBER_TEXT);
		Assert.assertEquals("getValue incorrect value for String type", REQUEST_VALID_NUMBER_VALUE,
				numberField.getValue());

		// Data is an invalid string
		numberField.setData(REQUEST_BAD_NUMBER_TEXT);
		try {
			numberField.getValue();
			Assert.fail("Invalid string should have thrown an exception");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception should have a message for invalid string", e.
					getMessage());
		}

		// Unsupported Type
		numberField.setData(Boolean.FALSE);
		try {
			numberField.getValue();
			Assert.fail("Unsupported data type should have thrown an exception");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception should have a message for unsupported data type", e.
					getMessage());
		}
	}

	@Test
	public void testGetText() {
		WNumberField numberField = new WNumberField();

		// Default to null
		Assert.assertNull(
				"User text on a NumberField should default to null until a handleRequest has been processed",
				numberField.getText());

		// Valid number on request
		numberField = processDoHandleRequestWithValidNumber();
		Assert.assertEquals("User text should be the valid text for valid number on request",
				REQUEST_VALID_NUMBER_TEXT, numberField.getText());

		// Bad number on request
		numberField = processDoHandleRequestWithBadNumber();
		Assert.assertEquals("User text should be the invalid text for bad number on request",
				REQUEST_BAD_NUMBER_TEXT,
				numberField.getText());

		// Empty Request
		numberField = processDoHandleRequestWithEmptyRequest();
		Assert.assertNull("User text should be null for an empty request", numberField.getText());

		// When set a number value, user text should be null
		numberField = processDoHandleRequestWithBadNumber(); // Put a value in User Text and make sure it gets cleared
		numberField.setNumber(new BigDecimal(200));
		Assert.assertNull("User text should be null when a valid number is set", numberField.
				getText());
	}

	@Test
	public void testIsValidNumber() {
		WNumberField numberField = new WNumberField();

		// Default to true
		Assert.assertTrue(
				"isValidNumber on a NumberField should default to true until a handleRequest has been processed",
				numberField.isValidNumber());

		// Valid number on request
		numberField = processDoHandleRequestWithValidNumber();
		Assert.assertTrue("isValidNumber should be true for a valid number on the request",
				numberField.isValidNumber());

		// Bad number on request
		numberField = processDoHandleRequestWithBadNumber();
		Assert.assertFalse("isValidNumber should be false for a bad number on the request",
				numberField.isValidNumber());

		// Empty Request
		numberField = processDoHandleRequestWithEmptyRequest();
		Assert.assertTrue("isValidNumber should be true for an empty request", numberField.
				isValidNumber());

		// When set a number value, valid number should be true
		numberField = processDoHandleRequestWithBadNumber(); // Make valid number false and make sure it gets set to
		// true
		numberField.setNumber(new BigDecimal(200));
		Assert.assertTrue("isValidNumber should be true when a valid number is set", numberField.
				isValidNumber());
	}

	@Test
	public void testNumberAccessors() {
		WNumberField numberField = new WNumberField();

		BigDecimal value = new BigDecimal(100);

		// BigDecimal
		assertAccessorsCorrect(numberField, "number", null, BigDecimal.valueOf(1), BigDecimal.valueOf(2));

		// Long Value
		numberField.setNumber(value.longValue());
		Assert.assertEquals("getData should be a BigDecimal value", value, numberField.getData());

		// Double Value
		numberField.setNumber(value.doubleValue());
		Assert.assertEquals("getData should be a BigDecimal value", value.setScale(1), numberField.getData());
	}

	@Test
	public void testMinValueAccessors() {

		WNumberField numberField = new WNumberField();
		assertAccessorsCorrect(numberField, "minValue", null, BigDecimal.valueOf(1), BigDecimal.valueOf(2));

		BigDecimal value = new BigDecimal(100);

		// Long Value
		numberField.setMinValue(value.longValue());
		Assert.assertEquals("getMinValue should be a BigDecimal value", value, numberField.
				getMinValue());

		// Double Value
		numberField.setMinValue(value.doubleValue());
		BigDecimal actual = numberField.getMinValue();
		Assert.assertEquals("getMinValue should be a BigDecimal value", value.setScale(actual.scale()), actual);
	}

	@Test
	public void testMaxValueAccessors() {
		WNumberField numberField = new WNumberField();
		assertAccessorsCorrect(numberField, "maxValue", null, BigDecimal.valueOf(1), BigDecimal.valueOf(2));

		BigDecimal value = new BigDecimal(100);

		// Long Value
		numberField.setMaxValue(value.longValue());
		Assert.assertEquals("getMaxValue should be a BigDecimal value", value, numberField.
				getMaxValue());

		// Double Value
		numberField.setMaxValue(value.doubleValue());
		BigDecimal actual = numberField.getMaxValue();
		Assert.assertEquals("getMaxValue should be a BigDecimal value", value.setScale(actual.scale()), actual);
	}

	@Test
	public void testStepAccessors() {
		WNumberField numberField = new WNumberField();
		assertAccessorsCorrect(numberField, "step", null, BigDecimal.valueOf(1), BigDecimal.valueOf(2));

		BigDecimal value = new BigDecimal(100);

		// Long Value
		numberField.setStep(value.longValue());
		Assert.assertEquals("getStep should be a BigDecimal value", value, numberField.getStep());

		// Double Value
		numberField.setStep(value.doubleValue());
		BigDecimal actual = numberField.getStep();
		Assert.assertEquals("getStep should be a BigDecimal value", value.setScale(actual.scale()), actual);
	}

	@Test
	public void testDecimalPlacesAccessors() {
		WNumberField numberField = new WNumberField();
		assertAccessorsCorrect(numberField, "decimalPlaces", 0, 1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDecimalPlacesInvalidArg() {
		new WNumberField().setDecimalPlaces(-1);
	}

	@Test
	public void testColumnsAccessors() {
		WNumberField numberField = new WNumberField();
		assertAccessorsCorrect(numberField, "columns", 0, 1, 2);
	}

	@Test
	public void testValidateMaxValue() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);

		long value = 1234;

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with no maximum set should be valid", diags.isEmpty());

		numberField.setNumber(value);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value with no maximum set should be valid", diags.isEmpty());

		numberField.reset();
		numberField.setMaxValue(1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with maximum set should be valid", diags.isEmpty());

		numberField.setNumber(value);

		numberField.setMaxValue(value + 1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value is less than maximum so should be valid", diags.isEmpty());

		numberField.setMaxValue(value);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value is the same as maximum so should be valid", diags.isEmpty());

		numberField.setMaxValue(value - 1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertFalse("Value is larger than maximum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateMinValue() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);

		long value = 1234;

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with no minimum set should be valid", diags.isEmpty());

		numberField.setNumber(value);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value with no minimum set should be valid", diags.isEmpty());

		numberField.reset();
		numberField.setMinValue(1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with minimum set should be valid", diags.isEmpty());

		numberField.setNumber(value);

		numberField.setMinValue(value - 1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value is greater than minimum so should be valid", diags.isEmpty());

		numberField.setMinValue(value);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value is the same as minimum so should be valid", diags.isEmpty());

		numberField.setMinValue(value + 1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertFalse("Value is less than minimum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateDecimalPlaces() {
		WNumberField numberField = new WNumberField();
		numberField.setLocked(true);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with no decimal places set should be valid", diags.isEmpty());

		numberField.setDecimalPlaces(1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Null value with 1 decimal place set should be valid", diags.isEmpty());

		numberField.setNumber(100);
		numberField.setDecimalPlaces(0);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value with no decimal places and no decimal place set should be valid",
				diags.isEmpty());

		BigDecimal value = new BigDecimal("1.12");

		numberField.setNumber(value);

		numberField.setDecimalPlaces(0);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertFalse("Value has more decimal places so should be invalid", diags.isEmpty());

		numberField.setDecimalPlaces(1);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertFalse("Value has more decimal places so should be invalid", diags.isEmpty());

		numberField.setDecimalPlaces(2);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value has the same decimal places so should be valid", diags.isEmpty());

		numberField.setDecimalPlaces(3);
		diags = new ArrayList<>();
		numberField.validate(diags);
		Assert.assertTrue("Value has less decimal places so should be valid", diags.isEmpty());
	}

	@Test
	public void testSetData() {
		WNumberField numberField = processDoHandleRequestWithValidNumber();
		Assert.assertEquals("Incorrect text", REQUEST_VALID_NUMBER_TEXT, numberField.getText());

		numberField.setData(BigDecimal.valueOf(1));
		Assert.assertEquals("Text should have been cleared", null, numberField.getText());
	}

	/**
	 * Check the numberField is in the correct state for a null value.
	 *
	 * @param numberField the numberField being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertNumberFieldNullValue(final WNumberField numberField, final String prefix) {
		Assert.assertNull(prefix + " - Number should be null", numberField.getValue());
		Assert.assertNull(prefix + " - User Text should be null", numberField.getText());
		Assert.
				assertNull(prefix + " - ValueAsString should be null", numberField.
						getValueAsString());
		Assert.assertTrue(prefix + " - ValidNumber should be true", numberField.isValidNumber());
	}

	/**
	 * Check the numberField is in the correct state for a bad number value.
	 *
	 * @param numberField the numberField being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertNumberFieldBadNumber(final WNumberField numberField, final String prefix) {
		Assert.assertNull(prefix + " - Number should be null", numberField.getValue());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_BAD_NUMBER_TEXT,
				numberField.getText());
		Assert.assertEquals(prefix + " - Incorrect number string returned", REQUEST_BAD_NUMBER_TEXT,
				numberField.getValueAsString());
		Assert.assertFalse(prefix + " - ValidNumber flag should be false", numberField.
				isValidNumber());
	}

	/**
	 * Check the numberField is in the correct state for a valid number value.
	 *
	 * @param numberField the numberField being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertNumbderFieldValidNumber(final WNumberField numberField, final String prefix) {
		Assert.assertEquals(prefix + " - Number incorrectly set", REQUEST_VALID_NUMBER_VALUE,
				numberField.getValue());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_VALID_NUMBER_TEXT,
				numberField.getText());
		Assert.assertEquals(prefix + " - Incorrect number string returned",
				REQUEST_VALID_NUMBER_VALUE.toString(),
				numberField.getValueAsString());
		Assert.
				assertTrue(prefix + " - ValidNumber flag should be true", numberField.
						isValidNumber());
	}

	/**
	 * @return a numberField processed with a request with a valid number
	 */
	private WNumberField processDoHandleRequestWithValidNumber() {
		resetContext();

		WNumberField numberField = new WNumberField();
		String numberFieldId = numberField.getId();
		numberField.setLocked(true);

		// Valid Number
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(numberFieldId, REQUEST_VALID_NUMBER_TEXT);
		numberField.doHandleRequest(request);

		return numberField;
	}

	/**
	 * @return a numberField processed with a request with a bad number
	 */
	private WNumberField processDoHandleRequestWithBadNumber() {
		resetContext();

		WNumberField numberField = new WNumberField();
		String numberFieldId = numberField.getId();
		numberField.setLocked(true);

		// Bad Number
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(numberFieldId, REQUEST_BAD_NUMBER_TEXT);
		numberField.doHandleRequest(request);

		return numberField;
	}

	/**
	 * @return a numberField processed with a request with an empty value
	 */
	private WNumberField processDoHandleRequestWithEmptyRequest() {
		resetContext();

		WNumberField numberField = new WNumberField();
		String numberFieldId = numberField.getId();
		numberField.setLocked(true);

		// Empty Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(numberFieldId, "");
		numberField.doHandleRequest(request);

		return numberField;
	}
}
