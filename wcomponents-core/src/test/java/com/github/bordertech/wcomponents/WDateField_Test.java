package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link WDateField} component.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDateField_Test extends AbstractWComponentTestCase {

	/**
	 * Valid date text.
	 */
	private static final String VALID_INTERNAL_DATE = "2099-01-01";
	/**
	 * Invalid date text 1.
	 */
	private static final String INVALID_INTERNAL_DATE = "ABCD-01-01";
	/**
	 * Invalid date text 2.
	 */
	private static final String INVALID_INTERNAL_DATE2 = "20AB-01-01";
	/**
	 * Invalid date text 3.
	 */
	private static final String INVALID_INTERNAL_DATE3 = "2010-01-999";
	/**
	 * Lenient Date - user text.
	 */
	private static final String LENIENT_VALID_USER_TEXT = "99 JAN 2008";
	/**
	 * Lenient Date - date text.
	 */
	private static final String LENIENT_VALID_INTERNAL_DATE_TEXT = "2008-01-99";
	/**
	 * Lenient Date - date value.
	 */
	private static final Date LENIENT_VALID_DATE_VALUE = DateUtilities.createDate(8, 4, 2008);

	/**
	 * Test message.
	 */
	private static final String ERROR_MESSAGE = "DateFieldValidator_Test error message";

	/**
	 * Valid Date - user text.
	 */
	private static final String REQUEST_VALID_USER_TEXT = "03 FEB 2001";
	/**
	 * Valid Date - date text.
	 */
	private static final String REQUEST_VALID_INTERNAL_DATE_TEXT = "2001-02-03";
	/**
	 * Valid Date - date value.
	 */
	private static final Date REQUEST_VALID_DATE_VALUE = DateUtilities.createDate(03, 02, 2001);

	/**
	 * Bad Date - user text.
	 */
	private static final String REQUEST_BAD_USER_TEXT = "BAD DATE";

	/**
	 * Theme Bad Date - user text.
	 */
	private static final String REQUEST_THEME_BAD_DATE_USER_TEXT = "10 MAR";
	/**
	 * Theme Bad Date - invalid date text.
	 */
	private static final String REQUEST_THEME_BAD_INTERNAL_DATE_VALUE = "9999-99-99";

	@Test
	public void testDateAccessors() throws ParseException {
		assertAccessorsCorrect(new WDateField(), "date", null, DateUtilities.createDate(1, 0, 2008),
				DateUtilities.createDate(1, 0, 2009));
	}

	@Test
	public void testGetValue() {
		WDateField dateField = new WDateField();

		// Data is null
		Assert.assertNull("Date getValue should be null by default", dateField.getValue());

		// Data is a Date
		Date date = DateUtilities.createDate(1, 0, 2008);
		dateField.setData(date);
		Assert.assertEquals("Date getValue incorrect date value for Date type", date, dateField.
				getValue());

		// Data is a Long
		Date dateForLong = DateUtilities.createDate(1, 0, 2009);
		Long longValue = dateForLong.getTime();
		dateField.setData(longValue);
		Assert.assertEquals("Date getValue incorretc date value for Long type", dateForLong,
				dateField.getValue());

		// Data is a Calendar
		Date dateForCalendar = DateUtilities.createDate(1, 0, 2010);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateForCalendar);
		dateField.setData(calendar);
		Assert.assertEquals("Date getValue incorrect date value for Calendar Type", dateForCalendar,
				dateField.getValue());

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
		WDateField dateField = new WDateField();

		// Default to null
		Assert.assertNull(
				"User text on a datefield should default to null until a handleRequest has been processed",
				dateField.getText());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertEquals("User text should be the valid text for valid date on request",
				REQUEST_VALID_USER_TEXT,
				dateField.getText());

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
	public void testIsParseable() {
		WDateField dateField = new WDateField();

		// Default to true
		Assert.assertTrue(
				"Parseable on a datefield should default to true until a handleRequest has been processed",
				dateField.isParseable());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertTrue("Parseable should be true for a valid date on the request", dateField.
				isParseable());

		// Bad date on request
		dateField = processDoHandleRequestWithBadDate();
		Assert.assertFalse("Parseable should be false for a bad date on the request", dateField.
				isParseable());

		// Bad theme date on request (bad date parameter by T&S)
		dateField = processDoHandleRequestWithBadThemeDate();
		Assert.assertFalse("Parseable should be false for a bad theme date on the request",
				dateField.isParseable());

		// Empty Request
		dateField = processDoHandleRequestWithEmptyRequest();
		Assert.assertTrue("Parseable should be true for an empty request", dateField.isParseable());

		// When set a date value, parseable should be true
		dateField = processDoHandleRequestWithBadDate(); // Make parseable false and make sure it gets set to true
		dateField.setDate(new Date());
		Assert.assertTrue("Parseable should be true when a valid date is set", dateField.
				isParseable());
	}

	@Test
	public void testGetValueAsString() {
		WDateField dateField = new WDateField();

		// Value and User Text null
		Assert.assertNull("getValue on a datefield should default to null", dateField.
				getValueAsString());

		// Valid date on request
		dateField = processDoHandleRequestWithValidDate();
		Assert.assertEquals(
				"getValue should return the string value of the valid date on the request",
				REQUEST_VALID_DATE_VALUE.toString(), dateField.getValueAsString());

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
		dateField.setDate(REQUEST_VALID_DATE_VALUE);
		Assert.assertEquals("getValue should return the string value of the valid date when set",
				REQUEST_VALID_DATE_VALUE.toString(), dateField.getValueAsString());
	}

	@Test
	public void testDoHandleRequestChanged() {
		WDateField dateField = new WDateField();

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
		WDateField dateField = new WDateField();
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
		WDateField dateField = new WDateField();
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
		WDateField dateField = new WDateField();
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

		WDateField dateField = new WDateField();
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		// Set current value
		Date current = new Date();
		dateField.setDate(current);

		// Empty Request (not present, should return current value)
		MockRequest request = new MockRequest();
		Assert.
				assertEquals(
						"Current value of the field should have been returned for empty request",
						current,
						dateField.getRequestValue(request));

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

		// Request with invalid value but could be parsed if lenient (should return null)
		request = new MockRequest();
		request.setParameter(dateField.getId(), LENIENT_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", LENIENT_VALID_INTERNAL_DATE_TEXT);
		Assert.assertNull("Null should have been returned for request with invalid value",
				dateField.getRequestValue(request));

		// Request with valid value
		request = new MockRequest();
		request.setParameter(dateField.getId(), REQUEST_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", REQUEST_VALID_INTERNAL_DATE_TEXT);
		Assert.assertEquals("Value from the request should have been returned",
				REQUEST_VALID_DATE_VALUE,
				dateField.getRequestValue(request));
	}

	@Test
	public void getRequestValueLenient() throws ParseException {
		WDateField dateField = new WDateField(true);
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		// Request with invalid value but passes with lenient (should return date)
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), LENIENT_VALID_USER_TEXT);
		request.setParameter(dateField.getId() + "-date", LENIENT_VALID_INTERNAL_DATE_TEXT);

		Assert.assertEquals("Value from request should have been returned when lenient",
				LENIENT_VALID_DATE_VALUE,
				dateField.getRequestValue(request));
	}

	@Test
	public void testValidate() {
		WDateField dateField = new WDateField();
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();

		dateField.validate(diags);
		Assert.assertTrue("Empty field should not have a validation error", diags.isEmpty());

		dateField.setMandatory(true);
		dateField.validate(diags);
		Assert.assertFalse("Empty should not validation error when mandatory", diags.isEmpty());

		diags.clear();

		// Valid date
		doRequest(dateField, VALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertTrue("A valid date should not have a validation error", diags.isEmpty());

		// Valid (lenient) date
		doRequest(dateField, LENIENT_VALID_INTERNAL_DATE_TEXT);
		dateField.validate(diags);
		Assert.assertFalse("Validator should not be lenient when validating dates", diags.isEmpty());

		Diagnostic diag = diags.get(0);
		String text = diag.getDescription();
		Assert.assertTrue("Error message should not be empty", text != null && text.length() > 0);

		diags.clear();

		// Invalid date
		doRequest(dateField, INVALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

		diag = diags.get(0);
		text = diag.getDescription();
		Assert.assertTrue("Error message should not be empty", text != null && text.length() > 0);

		// Invalid date - 2
		diags.clear();
		doRequest(dateField, INVALID_INTERNAL_DATE2);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

		// Invalid date - 3
		diags.clear();
		doRequest(dateField, INVALID_INTERNAL_DATE3);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

	}

	@Test
	public void testValidateLenient() {
		WDateField dateField = new WDateField(true);
		dateField.setLocked(true);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();

		dateField.validate(diags);
		Assert.assertTrue("Empty field should not have a validation error", diags.isEmpty());

		// Valid date
		doRequest(dateField, VALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertTrue("A valid date should not have a validation error", diags.isEmpty());

		// Valid (lenient) date
		doRequest(dateField, LENIENT_VALID_INTERNAL_DATE_TEXT);
		dateField.validate(diags);
		Assert.assertTrue("Validator should be lenient when validating dates", diags.isEmpty());

		// Invalid date
		doRequest(dateField, INVALID_INTERNAL_DATE);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

		Diagnostic diag = diags.get(0);
		String text = diag.getDescription();
		Assert.assertTrue("Error message should not be empty", text != null && text.length() > 0);

		// Invalid date - 2
		diags.clear();
		doRequest(dateField, INVALID_INTERNAL_DATE2);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

		// Invalid date - 3
		diags.clear();
		doRequest(dateField, INVALID_INTERNAL_DATE3);
		dateField.validate(diags);
		Assert.assertEquals("Invalid date should have a validation error", 1, diags.size());

	}

	@Test
	public void testValidateCustomError() {
		WDateField dateField = new WDateField(false);
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

	@Test
	public void testMinDateAccessors() {
		WDateField dateField = new WDateField();
		assertAccessorsCorrect(dateField, "minDate", null, DateUtilities.createDate(01, 02, 2003),
				DateUtilities.createDate(02, 03, 2004));
	}

	@Test
	public void testMaxDateAccessors() {
		WDateField dateField = new WDateField();
		assertAccessorsCorrect(dateField, "maxDate", null, DateUtilities.createDate(01, 02, 2003),
				DateUtilities.createDate(02, 03, 2004));
	}

	@Test
	public void testValidateMaxValue() {
		WDateField dateField = new WDateField();
		dateField.setLocked(true);

		Date value = DateUtilities.createDate(02, 02, 2003);

		Date valueMinusOne = DateUtilities.createDate(01, 02, 2003);
		Date valuePlusOne = DateUtilities.createDate(03, 02, 2003);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Null value with no maximum set should be valid", diags.isEmpty());

		dateField.setDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value with no maximum set should be valid", diags.isEmpty());

		dateField.reset();
		dateField.setMaxDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Null value with maximum set should be valid", diags.isEmpty());

		dateField.setDate(value);

		dateField.setMaxDate(valuePlusOne);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value is less than maximum so should be valid", diags.isEmpty());

		dateField.setMaxDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value is the same as maximum so should be valid", diags.isEmpty());

		dateField.setMaxDate(valueMinusOne);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertFalse("Value is larger than maximum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateMinValue() {
		WDateField dateField = new WDateField();
		dateField.setLocked(true);

		Date value = DateUtilities.createDate(01, 02, 2003);

		Date valueMinusOne = DateUtilities.createDate(01, 02, 2003);
		Date valuePlusOne = DateUtilities.createDate(03, 02, 2003);

		setActiveContext(createUIContext());

		List<Diagnostic> diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Null value with no minimum set should be valid", diags.isEmpty());

		dateField.setDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value with no minimum set should be valid", diags.isEmpty());

		dateField.reset();
		dateField.setMinDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Null value with minimum set should be valid", diags.isEmpty());

		dateField.setDate(value);

		dateField.setMinDate(valueMinusOne);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value is greater than minimum so should be valid", diags.isEmpty());

		dateField.setMinDate(value);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertTrue("Value is the same as minimum so should be valid", diags.isEmpty());

		dateField.setMinDate(valuePlusOne);
		diags = new ArrayList<>();
		dateField.validate(diags);
		Assert.assertFalse("Value is less than minimum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testSetData() {
		WDateField dateField = processDoHandleRequestWithValidDate();
		Assert.assertEquals("Incorrect text", REQUEST_VALID_USER_TEXT, dateField.getText());

		dateField.setData(new Date());
		Assert.assertEquals("Text should have been cleared", null, dateField.getText());
	}

	/**
	 * @return a datefield processed with a request with a valid date
	 */
	private WDateField processDoHandleRequestWithValidDate() {
		resetContext();

		WDateField dateField = new WDateField();
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
	 * @return a datefield processed with a request with a bad date
	 */
	private WDateField processDoHandleRequestWithBadDate() {
		resetContext();

		WDateField dateField = new WDateField();
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
	private WDateField processDoHandleRequestWithEmptyRequest() {
		resetContext();

		WDateField dateField = new WDateField();
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
	private WDateField processDoHandleRequestWithBadThemeDate() {
		resetContext();

		WDateField dateField = new WDateField();
		String dateFieldId = dateField.getId();
		dateField.setLocked(true);

		// Valid Date
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dateFieldId, REQUEST_THEME_BAD_DATE_USER_TEXT);
		request.setParameter(dateFieldId + "-date", REQUEST_THEME_BAD_INTERNAL_DATE_VALUE);
		dateField.doHandleRequest(request);

		return dateField;
	}

	/**
	 * Check the datefield is in the correct state for a null value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldNullValue(final WDateField dateField, final String prefix) {
		Assert.assertNull(prefix + " - Date should be null", dateField.getDate());
		Assert.assertNull(prefix + " - User Text should be null", dateField.getText());
		Assert.assertNull(prefix + " - ValueAsString should be null", dateField.getValueAsString());
		Assert.assertTrue(prefix + " - Parseable should be true", dateField.isParseable());
	}

	/**
	 * Check the datefield is in the correct state for a bad date value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldBadDate(final WDateField dateField, final String prefix) {
		Assert.assertNull(prefix + " - Date should be null", dateField.getDate());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_BAD_USER_TEXT, dateField.
				getText());
		Assert.assertEquals(prefix + " - Incorrect date string returned", REQUEST_BAD_USER_TEXT,
				dateField.getValueAsString());
		Assert.assertFalse(prefix + " - Parseable flag should be false", dateField.isParseable());
	}

	/**
	 * Check the datefield is in the correct state for a valid date value.
	 *
	 * @param dateField the datefield being checked
	 * @param prefix the prefix for assert messages
	 */
	private void assertDateFieldValidDate(final WDateField dateField, final String prefix) {
		Assert.assertEquals(prefix + " - Date incorrectly set", REQUEST_VALID_DATE_VALUE, dateField.
				getDate());
		Assert.assertEquals(prefix + " - Incorrect user text set", REQUEST_VALID_USER_TEXT,
				dateField.getText());
		Assert.assertEquals(prefix + " - Incorrect date string returned", REQUEST_VALID_DATE_VALUE.
				toString(),
				dateField.getValueAsString());
		Assert.assertTrue(prefix + " - Parseable flag should be true", dateField.isParseable());
	}

	/**
	 * Emulates user interaction with the date field.
	 *
	 * @param dateField the date field to modify.
	 * @param dateStr the parsed date string.
	 */
	private void doRequest(final WDateField dateField, final String dateStr) {
		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), dateStr); // don't really care about the user text
		request.setParameter(dateField.getId() + "-date", dateStr);
		dateField.serviceRequest(request);
	}
}
