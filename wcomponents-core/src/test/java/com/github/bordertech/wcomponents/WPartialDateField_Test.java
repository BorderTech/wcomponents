package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WPartialDateField}.
 *
 * @author Anthony O'Connor
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPartialDateField_Test extends AbstractWComponentTestCase {

	/**
	 * Valid date text.
	 */
	private static final String VALID_INTERNAL_DATE = "2099-01-01";
	/**
	 * Invalid date text 1.
	 */
	private static final String INVALID_INTERNAL_DATE = "ABCD0101";

	/**
	 * List of valid dates.
	 */
	private static final List<String> VALID_INTERNAL_DATES = Arrays.asList(
			new String[]{"????-??-??", "2011-??-??",
				"2011-10-??", "2011-10-14",
				"2011-??-14", "????-10-??",
				"????-10-14", "????-??-14"});

	/**
	 * List of invalid dates.
	 */
	private static final List<String> INVALID_INTERNAL_DATES = Arrays.asList(
			new String[]{"ABCD-01-01", "20AB-01-01",
				"1234-56-789", "20?1-10-14",
				"2011-?0-14", "2011-10-?4",
				"20A1-10-14", "2011-A0-14",
				"2011-10-A4", "123-123-12",
				"1234-123-1", "1234567890",
				""});

	/**
	 * Valid Date - user text.
	 */
	private static final String REQUEST_VALID_USER_TEXT = "03 FEB 2001";
	/**
	 * Valid Date - date text.
	 */
	private static final String REQUEST_VALID_DATE_TEXT = "20010203";
	/**
	 * Valid Date - internal date text.
	 */
	private static final String REQUEST_VALID_INTERNAL_DATE_TEXT = "2001-02-03";
	/**
	 * Valid Date - date value.
	 */
	private static final Date REQUEST_VALID_DATE_DATE = DateUtilities.createDate(03, 02, 2001);
	/**
	 * Valid Date - Day value.
	 */
	private static final Integer REQUEST_VALID_DAY_VALUE = Integer.valueOf(03);
	/**
	 * Valid Date - Month value.
	 */
	private static final Integer REQUEST_VALID_MONTH_VALUE = Integer.valueOf(02);
	/**
	 * Valid Date - Year value.
	 */
	private static final Integer REQUEST_VALID_YEAR_VALUE = Integer.valueOf(2001);

	/**
	 * Valid Date - user text.
	 */
	private static final String REQUEST_VALID_PARTIAL_USER_TEXT = "APR 2002";
	/**
	 * Valid Date - date text.
	 */
	private static final String REQUEST_VALID_PARTIAL_INTERNAL_DATE_TEXT = "2002-04-??";
	/**
	 * Valid Date - date text.
	 */
	private static final String REQUEST_VALID_PARTIAL_DATE_TEXT = "200204  ";

	/**
	 * Bad Date - user text.
	 */
	private static final String REQUEST_BAD_USER_TEXT = "BAD DATE";

	/**
	 * Theme Bad Date - user text.
	 */
	private static final String REQUEST_THEME_BAD_DATE_USER_TEXT = "10 XXX";
	/**
	 * Theme Bad Date - invalid date text.
	 */
	private static final String REQUEST_THEME_BAD_INTERNAL_DATE_TEXT = "9999-99-99";

	/**
	 * Test message.
	 */
	private static final String ERROR_MESSAGE = "DateFieldValidator_Test error message";

	@Test
	public void testConstructor() {
		// Constructor 1
		WPartialDateField partial = new WPartialDateField();
		setActiveContext(createUIContext());
		Assert.assertNull("Day should be null", partial.getDay());
		Assert.assertNull("Month should be null", partial.getMonth());
		Assert.assertNull("Year should be null", partial.getYear());
		Assert.assertNull("Date string should be null", partial.getValueAsString());

		// Constructor 2
		Integer day = 1;
		Integer month = 10;
		Integer year = 2012;
		partial = new WPartialDateField(day, month, year);
		Assert.assertEquals("Incorrect Day value returned", day, partial.getDay());
		Assert.assertEquals("Incorrect Month value returned", month, partial.getMonth());
		Assert.assertEquals("Incorrect Year value returned", year, partial.getYear());
		Assert.assertEquals("Incorrect String value returned", "20121001", partial.
				getValueAsString());

		// Invalid Year
		day = null;
		month = null;
		year = -1;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial year should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.
					assertNotNull(
							"Invalid argument exception for invalid year should have a message", e.
							getMessage());

		}

		day = null;
		month = null;
		year = 10000;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial date should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());
		}

		// Invalid Month
		day = null;
		month = 0;
		year = null;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial month should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());

		}

		day = null;
		month = 13;
		year = null;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial month should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());
		}

		// Invalid Day
		day = 0;
		month = null;
		year = null;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial day should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid argument exception for invalid day should have a message",
					e.getMessage());

		}

		day = 32;
		month = null;
		year = null;
		try {
			partial = new WPartialDateField(day, month, year);
			Assert.fail("Invalid partial day should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid argument exception for invalid day should have a message",
					e.getMessage());
		}

	}

	/**
	 * test setPartialDate - with Integer inputs.
	 */
	@Test
	public void testSetPartialDate() {
		WPartialDateField partialDateField = new WPartialDateField();

		Integer day = null;
		Integer month = null;
		Integer year = null;
		partialDateField.setPartialDate(day, month, year);
		Assert.assertNull("date should be null", partialDateField.getDate());
		Assert.assertNull("string value should be null", partialDateField.getValueAsString());

		day = null;
		month = 10;
		year = null;
		partialDateField.setPartialDate(day, month, year);
		Assert.assertNull("date should be null", partialDateField.getDate());
		Assert.assertEquals("Incorrect String value returned", "    10", partialDateField.
				getValueAsString());

		// Invalid Year
		day = null;
		month = null;
		year = -1;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial year should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.
					assertNotNull(
							"Invalid argument exception for invalid year should have a message", e.
							getMessage());

		}

		day = null;
		month = null;
		year = 10000;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial date should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());
		}

		// Invalid Month
		day = null;
		month = 0;
		year = null;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial month should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());

		}

		day = null;
		month = 13;
		year = null;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial month should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull(
					"Invalid argument exception for invalid month should have a message", e.
					getMessage());
		}

		// Invalid Day
		day = 0;
		month = null;
		year = null;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial day should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid argument exception for invalid day should have a message",
					e.getMessage());

		}

		day = 32;
		month = null;
		year = null;
		try {
			partialDateField.setPartialDate(day, month, year);
			Assert.fail("Invalid partial day should throw an exception");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Invalid argument exception for invalid day should have a message",
					e.getMessage());
		}

		// Valid Date
		day = 12;
		month = 11;
		year = 2007;
		Date date = DateUtilities.createDate(12, 11, 2007);
		partialDateField.setPartialDate(day, month, year);
		Assert.assertEquals("date incorrectly set", date, partialDateField.getDate());
		Assert.assertEquals("Incorrect String value returned", "20071112", partialDateField.
				getValueAsString());
	}

	@Test
	public void testPaddingCharAccessors() {
		WPartialDateField partial = new WPartialDateField();
		assertAccessorsCorrect(partial, "paddingChar", ' ', '@', '#');
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPaddingCharInvalidArg() {
		new WPartialDateField().setPaddingChar('1');
	}

	@Test
	public void testGetValue() {
		WPartialDateField dateField = new WPartialDateField();

		// Data is null
		Assert.assertNull("Date getValue should be null by default", dateField.getValue());

		// Data is empty
		dateField.setData("");
		Assert.assertNull("Date getValue should be null by default", dateField.getValue());

		// Data is a Full PartialDate
		dateField.setData(REQUEST_VALID_DATE_TEXT);
		Assert.assertEquals("Date getValue incorrect date value for Date type",
				REQUEST_VALID_DATE_TEXT,
				dateField.getValue());

		// Data is a Partial PartialDate
		dateField.setData(REQUEST_VALID_PARTIAL_DATE_TEXT);
		Assert.assertEquals("Date getValue incorrect date value for Date type",
				REQUEST_VALID_PARTIAL_DATE_TEXT,
				dateField.getValue());

		// Object with a String
		Object dateString = new Object() {
			@Override
			public String toString() {
				return REQUEST_VALID_DATE_TEXT;
			}
		};
		dateField.setData(dateString);
		Assert.assertEquals("Date getValue incorrecr date value for Object to String type",
				REQUEST_VALID_DATE_TEXT,
				dateField.getValue());

		// Invalid Date
		dateField.setData("XXX");
		try {
			dateField.getValue();
			Assert.fail("Unsupported data type should have thrown an exception");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception should have a message for unsupported data type", e.
					getMessage());
		}

		// Unsupported Type
		dateField.setData(Boolean.FALSE);
		try {
			dateField.getValue();
			Assert.fail("Unsupported data type should have thrown an exception");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception should have a message for unsupported data type", e.
					getMessage());
		}
	}

	@Test
	public void testGetText() {
		WPartialDateField dateField = new WPartialDateField();

		// Default to null
		Assert.assertNull(
				"User text on a datefield should default to null until a handleRequest has been processed",
				dateField.getText());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertEquals("User text should be the valid text for valid date on request",
				REQUEST_VALID_USER_TEXT,
				dateField.getText());

		// Valid partial date on request
		dateField = processDoHandleRequestWithValidPartialDate();
		Assert.assertEquals("User text should be the valid text for valid date on request",
				REQUEST_VALID_PARTIAL_USER_TEXT, dateField.getText());

		// Bad date on request
		dateField = processDoHandleRequestWithBadDate();
		Assert.assertEquals("User text should be the invalid text for bad date on request",
				REQUEST_BAD_USER_TEXT,
				dateField.getText());

		// Bad theme date on request (bad date parameter by T&S)
		dateField = processDoHandleRequestWithBadThemeDate();
		Assert.assertEquals("User text should be the invalid text for bad date on request",
				REQUEST_THEME_BAD_DATE_USER_TEXT, dateField.getText());

		// Empty Request
		dateField = processDoHandleRequestWithEmptyRequest();
		Assert.assertNull("User text should be null for an empty request", dateField.getText());

		// When set a date value, user text should be null
		dateField = processDoHandleRequestWithBadDate(); // Put a value in User Text and make sure it gets cleared
		dateField.setDate(new Date());
		Assert.assertNull("User text should be null when a valid date is set", dateField.getText());
	}

	@Test
	public void testIsValidDate() {
		WPartialDateField dateField = new WPartialDateField();

		// Default to true
		Assert.assertTrue(
				"ValidDate on a datefield should default to true until a handleRequest has been processed",
				dateField.isValidDate());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertTrue("ValidDate should be true for a valid date on the request", dateField.
				isValidDate());

		// Valid partial date on request
		dateField = processDoHandleRequestWithValidPartialDate();
		Assert.assertTrue("ValidDate should be true for a valid partial date on the request",
				dateField.isValidDate());

		// Bad date on request
		dateField = processDoHandleRequestWithBadDate();
		Assert.assertFalse("ValidDate should be false for a bad date on the request", dateField.
				isValidDate());

		// Bad theme date on request (bad date parameter by T&S)
		dateField = processDoHandleRequestWithBadThemeDate();
		Assert.assertFalse("ValidDate should be false for a bad theme date on the request",
				dateField.isValidDate());

		// Empty Request
		dateField = processDoHandleRequestWithEmptyRequest();
		Assert.assertTrue("ValidDate should be true for an empty request", dateField.isValidDate());

		// When set a date value, parseable should be true
		dateField = processDoHandleRequestWithBadDate(); // Make parseable false and make sure it gets set to true
		dateField.setDate(new Date());
		Assert.assertTrue("ValidDate should be true when a valid date is set", dateField.
				isValidDate());
	}

	@Test
	public void testGetValueAsString() {
		WPartialDateField dateField = new WPartialDateField();

		// Value and User Text null
		Assert.assertNull("getValue on a datefield should default to null", dateField.
				getValueAsString());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertEquals(
				"getValue should return the string value of the valid date on the request",
				REQUEST_VALID_DATE_TEXT, dateField.getValueAsString());

		// Valid partial date on request
		dateField = processDoHandleRequestWithValidPartialDate();
		Assert.assertEquals(
				"getValue should return the string value of the valid partial date on the request",
				REQUEST_VALID_PARTIAL_DATE_TEXT, dateField.getValueAsString());

		// Bad date on request
		dateField = processDoHandleRequestWithBadDate();
		Assert.assertEquals("getValue should return the user text for bad date on request",
				REQUEST_BAD_USER_TEXT,
				dateField.getValueAsString());

		// Bad theme date on request (bad date parameter by T&S)
		dateField = processDoHandleRequestWithBadThemeDate();
		Assert.assertEquals("getValue should return the user text for bad date on request",
				REQUEST_THEME_BAD_DATE_USER_TEXT, dateField.getValueAsString());

		// Empty Request
		dateField = processDoHandleRequestWithEmptyRequest();
		Assert.assertNull("getValue should be null for an empty request", dateField.
				getValueAsString());

		// When set a date value, value should be the value
		dateField.setDate(REQUEST_VALID_DATE_DATE);
		Assert.assertEquals("getValue should return the string value of the valid date when set",
				REQUEST_VALID_DATE_TEXT, dateField.getValueAsString());
	}

	@Test
	public void testDoHandleRequestChanged() {
		WPartialDateField dateField = new WPartialDateField();

		dateField.setLocked(true);
		setActiveContext(createUIContext());

		// Request with Empty Value and Field is null (no change)
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), "");
		boolean changed = dateField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for empty request and field is null",
				changed);

		// Request with valid value and Field is null (changed)
		request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);

		changed = dateField.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should have returned true for request with valid value and field is null",
				changed);

		// Request with same value (no change)
		changed = dateField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same valid value",
				changed);

		// Request with invalid value (changed)
		request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_BAD_USER_TEXT);

		changed = dateField.doHandleRequest(request);
		Assert
				.assertTrue(
						"doHandleRequest should have returned true for request with different invalid value",
						changed);

		// Request with same invalid value (no change)
		changed = dateField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same invalid value",
				changed);

		// Request with empty value (changed)
		request = new MockRequest();
		request.setParameter(dateField.getId(), "");

		changed = dateField.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should have returned true for request going back to empty value",
				changed);

		// Request with same empty value (no change)
		changed = dateField.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should have returned false for request with same empty value",
				changed);
	}

	@Test
	public void testDoHandleRequestEmptyScenarios() {
		WPartialDateField dateField = new WPartialDateField();
		dateField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with empty value
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), "");

		// Scenario #1
		// Setup datefield with null value
		dateField.setDate(null);

		// Empty Request and Field is null (no change)
		boolean changed = dateField.doHandleRequest(request);

		Assert.assertFalse(
				"Empty request and field is null - doHandleRequest should have returned false",
				changed);
		assertDateFieldNullValue(dateField, "Empty request and field is null");

		// Scenario #2
		// Setup datefield with a valid date set
		dateField.setDate(new Date());

		// Empty Request and Field has a valid date set (change)
		changed = dateField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has a valid date set - doHandleRequest should have returned true",
				changed);
		assertDateFieldNullValue(dateField, "Empty request and field has a valid date set");

		// Scenario #3
		// Setup datefield with invalid date value
		MockRequest request2 = new MockRequest();
		request2.setParameter(dateField.getId(), REQUEST_BAD_USER_TEXT);
		dateField.doHandleRequest(request2);

		// Empty Request and Field has an invalid date value (change)
		changed = dateField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has an invalid date - doHandleRequest should have returned true",
				changed);
		assertDateFieldNullValue(dateField, "Empty request and field has an invalid date");

		// Scenario #4
		// Setup datefield with valid date value
		request2 = new MockRequest();
		request2.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request2.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);
		dateField.doHandleRequest(request2);

		// Empty Request and Field has a valid date value (change)
		changed = dateField.doHandleRequest(request);
		Assert.assertTrue(
				"Empty request and field has a valid value - doHandleRequest should have returned true",
				changed);
		assertDateFieldNullValue(dateField, "Empty request and field has a valid value");
	}

	@Test
	public void testDoHandleRequestBadDateScenarios() {
		WPartialDateField dateField = new WPartialDateField();
		dateField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with Invalid Date (User text only)
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_BAD_USER_TEXT);

		// Scenario #1
		// Setup datefield with null value
		dateField.setDate(null);

		boolean changed = dateField.doHandleRequest(request);

		Assert.assertTrue(
				"Request with Bad Date and field is null - doHandleRequest should have returned true",
				changed);
		assertDateFieldBadDate(dateField, "Request with Bad Date and field is null");

		// Scenario #2
		// Setup datefield with a valid date set
		dateField.setDate(new Date());

		changed = dateField.doHandleRequest(request);
		Assert
				.assertTrue(
						"Request with Bad Date and field has a valid date set - doHandleRequest should have returned true",
						changed);
		assertDateFieldBadDate(dateField, "Request with Bad Date and field has a valid date set");

		// Scenario #3
		// Setup datefield with DIFFERENT invalid date value
		MockRequest request2 = new MockRequest();
		request2.setParameter(dateField.getId(), "ANOTHER BAD DATE");
		dateField.doHandleRequest(request2);

		changed = dateField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Bad Date and field has a different invalid date - doHandleRequest should have returned true",
						changed);
		assertDateFieldBadDate(dateField,
				"Request with Bad Date and field has a different invalid date");

		// Scenario #4
		// Setup datefield with SAME invalid date value
		changed = dateField.doHandleRequest(request);

		Assert
				.assertFalse(
						"Request with Bad Date and field has same invalid date - doHandleRequest should have returned false",
						changed);
		assertDateFieldBadDate(dateField, "Request with Bad Date and field has same invalid date");

		// Scenario #5
		// Setup datefield with valid date value
		request2 = new MockRequest();
		request2.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request2.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);
		dateField.doHandleRequest(request2);

		changed = dateField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Bad Date and field has a valid value - doHandleRequest should have returned true",
						changed);
		assertDateFieldBadDate(dateField, "Request with Bad Date and field has a valid value");
	}

	@Test
	public void testDoHandleRequestValidDateScenarios() {
		WPartialDateField dateField = new WPartialDateField();
		dateField.setLocked(true);
		setActiveContext(createUIContext());

		// Setup Request with Valid Date
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);

		// Scenario #1
		// Setup datefield with null value
		dateField.setDate(null);

		boolean changed = dateField.doHandleRequest(request);

		Assert.assertTrue(
				"Request with Valid Date and field is null - doHandleRequest should have returned true",
				changed);
		assertDateFieldValidDate(dateField, "Request with Valid Date and field is null");

		// Scenario #2
		// Setup datefield with a different valid date set
		dateField.setDate(new Date());

		changed = dateField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Valid Date and field has a different valid date set - doHandleRequest should have returned true",
						changed);
		assertDateFieldValidDate(dateField,
				"Request with Valid Date and field has a different valid date set");

		// Scenario #3
		// Datefield with same valid date set
		changed = dateField.doHandleRequest(request);

		Assert
				.assertFalse(
						"Request with Valid Date and field has same valid date set - doHandleRequest should have returned false",
						changed);
		assertDateFieldValidDate(dateField,
				"Request with Valid Date and field has same valid date set");

		// Scenario #4
		// Setup datefield with invalid date value
		MockRequest request2 = new MockRequest();
		request2.setParameter(dateField.getId(), REQUEST_BAD_USER_TEXT);
		dateField.doHandleRequest(request2);

		changed = dateField.doHandleRequest(request);

		Assert
				.assertTrue(
						"Request with Valid Date and field has an invalid date - doHandleRequest should have returned true",
						changed);
		assertDateFieldValidDate(dateField, "Request with Valid Date and field has an invalid date");
	}

	@Test
	public void getRequestValue() throws ParseException {

		WPartialDateField dateField = new WPartialDateField();
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		// Set current value
		dateField.setDate(REQUEST_VALID_DATE_DATE);

		// Empty Request (not present, should return current value)
		MockRequest request = new MockRequest();
		Assert.
				assertEquals(
						"Current value of the field should have been returned for empty request",
						REQUEST_VALID_DATE_TEXT, dateField.getRequestValue(request));

		// Request with "empty" value (should return null)
		request = new MockRequest();
		request.setParameter(dateField.getId(), "");
		Assert.assertNull("Null should have been returned for request with empty value",
				dateField.getRequestValue(request));

		// Request with "invalid" data parameter by T&S - wrong length (should return null)
		request = new MockRequest();
		request.setParameter(dateField.getId(), "TEST");
		request.setParameter(dateField.getId() + "-date", "ABCDEFGHIHJKLMNOP");
		Assert.assertNull(
				"Null should have been returned for request with invalid length date parameter",
				dateField.getRequestValue(request));

		// Request with "invalid" data parameter by T&S - empty (should return null)
		request = new MockRequest();
		request.setParameter(dateField.getId(), "TEST");
		request.setParameter(dateField.getId() + "-date", "");
		Assert.assertNull(
				"Null should have been returned for request with invalid empty date parameter",
				dateField.getRequestValue(request));

		// Request with valid value
		request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);
		Assert.assertEquals("Value from the request should have been returned",
				REQUEST_VALID_DATE_TEXT,
				dateField.getRequestValue(request));

		// Request with partial valid value
		request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_VALID_PARTIAL_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", REQUEST_VALID_PARTIAL_INTERNAL_DATE_TEXT);
		Assert.assertEquals("Value from the request should have been returned",
				REQUEST_VALID_PARTIAL_DATE_TEXT,
				dateField.getRequestValue(request));

	}

	@Test
	public void testValidate() {
		WPartialDateField dateField = new WPartialDateField();
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();

		dateField.validate(diags);
		Assert.assertTrue("Empty field should not have a validation error", diags.isEmpty());

		dateField.setMandatory(true);
		dateField.validate(diags);
		Assert.assertFalse("Empty should not validation error when mandatory", diags.isEmpty());

		// Valid dates
		for (String validDate : VALID_INTERNAL_DATES) {
			diags.clear();
			doRequest(dateField, validDate);
			dateField.validate(diags);
			Assert.assertTrue("A valid date (" + validDate + ") should not have a validation error",
					diags.isEmpty());
		}

		// Invalid dates
		for (String invalidDate : INVALID_INTERNAL_DATES) {
			doRequest(dateField, invalidDate);
			diags.clear();
			dateField.validate(diags);
			Assert.assertEquals("Invalid date (" + invalidDate + ") should have a validation error",
					1, diags.size());
		}

	}

	@Test
	public void testValidateCustomError() {
		WPartialDateField dateField = new WPartialDateField();
		dateField.setInvalidDateErrorMessage(ERROR_MESSAGE);
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();

		dateField.validate(diags);
		Assert.assertTrue("Empty field should not have a validation error", diags.isEmpty());

		// Valid date
		doRequest(dateField, VALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertTrue("A valid date should not have a validation error", diags.isEmpty());

		// Invalid date
		doRequest(dateField, INVALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

		Diagnostic diag = diags.get(0);
		String text = diag.getDescription();
		Assert.assertEquals("Incorrect error text", ERROR_MESSAGE, text);
	}

	/**
	 * test setDate - with Date input.
	 */
	@Test
	public void testDateAccessors() {
		assertAccessorsCorrect(new WPartialDateField(), "date", null, DateUtilities.createDate(01,
				01, 2010),
				DateUtilities.createDate(2, 2, 2012));
	}

	/**
	 * test getDay.
	 */
	@Test
	public void testGetDay() {
		final Integer testDay = 7;
		WPartialDateField partialDateField = new WPartialDateField();
		partialDateField.setPartialDate(testDay, null, null);

		Assert.assertEquals("Day should be set", testDay, partialDateField.getDay());
		Assert.assertNull("Month should not be set", partialDateField.getMonth());
		Assert.assertNull("Year should not be set", partialDateField.getYear());
	}

	/**
	 * test getMonth.
	 */
	@Test
	public void testGetMonth() {
		final Integer testMonth = 11;
		WPartialDateField partialDateField = new WPartialDateField();
		partialDateField.setPartialDate(null, testMonth, null);

		Assert.assertEquals("Month should be set", testMonth, partialDateField.getMonth());
		Assert.assertNull("Day should not be set", partialDateField.getDay());
		Assert.assertNull("Year should not bet set", partialDateField.getYear());
	}

	/**
	 * test getYear.
	 */
	@Test
	public void testGetYear() {
		final Integer testYear = 1999;
		WPartialDateField partialDateField = new WPartialDateField();
		partialDateField.setPartialDate(null, null, testYear);

		Assert.assertEquals("Year should be set", testYear, partialDateField.getYear());
		Assert.assertNull("Day should not be set", partialDateField.getDay());
		Assert.assertNull("Month should not be set", partialDateField.getMonth());
	}

	/**
	 * @return a datefield processed with a request with a valid date
	 */
	private WPartialDateField processDoHandleRequestWithValidDate() {
		resetContext();

		WPartialDateField dateField = new WPartialDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Valid Date
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, REQUEST_VALID_USER_TEXT);
		request.setParameter(dateFieldId + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * @return a datefield processed with a request with a valid date
	 */
	private WPartialDateField processDoHandleRequestWithValidPartialDate() {
		resetContext();

		WPartialDateField dateField = new WPartialDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Valid Date
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, REQUEST_VALID_PARTIAL_USER_TEXT);
		request.setParameter(dateFieldId + "-date", REQUEST_VALID_PARTIAL_INTERNAL_DATE_TEXT);
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * @return a datefield processed with a request with a bad date
	 */
	private WPartialDateField processDoHandleRequestWithBadDate() {
		resetContext();

		WPartialDateField dateField = new WPartialDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Bad Date
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, REQUEST_BAD_USER_TEXT);
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * @return a datefield processed with a request with an empty value
	 */
	private WPartialDateField processDoHandleRequestWithEmptyRequest() {
		resetContext();

		WPartialDateField dateField = new WPartialDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Empty Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, "");
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * @return a datefield processed with a request with a bad theme date
	 */
	private WPartialDateField processDoHandleRequestWithBadThemeDate() {
		resetContext();

		WPartialDateField dateField = new WPartialDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Valid Date
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, REQUEST_THEME_BAD_DATE_USER_TEXT);
		request.setParameter(dateFieldId + "-date", REQUEST_THEME_BAD_INTERNAL_DATE_TEXT);
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * Check the datefield is in the correct state for a null value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldNullValue(final WPartialDateField dateField, final String prefix) {
		Assert.assertNull(prefix + " - Date should be null", dateField.getDate());
		Assert.assertNull(prefix + " - Date day incorrectly set", dateField.getDay());
		Assert.assertNull(prefix + " - Date month incorrectly set", dateField.getMonth());
		Assert.assertNull(prefix + " - Date year incorrectly set", dateField.getYear());
		Assert.assertNull(prefix + " - User Text should be null", dateField.getText());
		Assert.assertNull(prefix + " - ValueAsString should be null", dateField.getValueAsString());
		Assert.assertTrue(prefix + " - ValidDate should be true", dateField.isValidDate());
	}

	/**
	 * Check the datefield is in the correct state for a bad date value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldBadDate(final WPartialDateField dateField, final String prefix) {
		Assert.assertNull(prefix + " - Date should be null", dateField.getDate());
		Assert.assertNull(prefix + " - Date day incorrectly set", dateField.getDay());
		Assert.assertNull(prefix + " - Date month incorrectly set", dateField.getMonth());
		Assert.assertNull(prefix + " - Date year incorrectly set", dateField.getYear());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_BAD_USER_TEXT, dateField.
				getText());
		Assert.assertEquals(prefix + " - Incorrect date string returned", REQUEST_BAD_USER_TEXT,
				dateField.getValueAsString());
		Assert.assertFalse(prefix + " - ValidDate flag should be false", dateField.isValidDate());
	}

	/**
	 * Check the datefield is in the correct state for a valid date value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldValidDate(final WPartialDateField dateField, final String prefix) {
		Assert.assertEquals(prefix + " - Date incorrectly set", REQUEST_VALID_DATE_DATE, dateField.
				getDate());
		Assert.assertEquals(prefix + " - Date day incorrectly set", REQUEST_VALID_DAY_VALUE,
				dateField.getDay());
		Assert.assertEquals(prefix + " - Date month incorrectly set", REQUEST_VALID_MONTH_VALUE,
				dateField.getMonth());
		Assert.assertEquals(prefix + " - Date year incorrectly set", REQUEST_VALID_YEAR_VALUE,
				dateField.getYear());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_VALID_USER_TEXT,
				dateField.getText());
		Assert.assertEquals(prefix + " - Incorrect date string returned", REQUEST_VALID_DATE_TEXT.
				toString(),
				dateField.getValueAsString());
		Assert.assertTrue(prefix + " - ValidDate flag should be true", dateField.isValidDate());
	}

	/**
	 * Emulates user interaction with the date field.
	 *
	 * @param dateField the date field to modify.
	 * @param dateStr the parsed date string.
	 */
	private void doRequest(final WPartialDateField dateField, final String dateStr) {
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), dateStr); // don't really care about the user text
		request.setParameter(dateField.getId() + "-date", dateStr);
		dateField.serviceRequest(request);
	}

}
