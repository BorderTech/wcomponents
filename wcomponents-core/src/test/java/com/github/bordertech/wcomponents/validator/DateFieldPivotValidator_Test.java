package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * DateFieldPivotValidator_Test - unit tests for {@link DateFieldPivotValidator}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DateFieldPivotValidator_Test {

	/**
	 * The series of dates to test.
	 */
	private static final Date[][] DATES
			= {
				{
					null,
					DateUtilities.createDate(31, 12, 2007),
					DateUtilities.createDate(1, 1, 2008),
					DateUtilities.createDate(2, 1, 2008)
				},
				{
					null,
					DateUtilities.createDate(31, 12, 2007),
					new Date(DateUtilities.createDate(1, 1, 2008).getTime() + 12 * 60 * 60 * 1000), // midday
					DateUtilities.createDate(2, 1, 2008)
				}
			};

	@Test
	public void testBefore() {
		String[][] results = runTest(DateFieldPivotValidator.BEFORE);

		for (String[] result : results) {
			Assert.assertNull("Empty field should not have a validation error", result[0]);
			Assert.assertNull("Date before pivot should not have a validation error", result[1]);
			Assert.assertNotNull("Date equal to pivot should have a validation error", result[2]);
			Assert.assertNotNull("Date after pivot should have a validation error", result[3]);
		}
	}

	@Test
	public void testBeforeOrEqual() {
		String[][] results = runTest(DateFieldPivotValidator.BEFORE_OR_EQUAL);

		for (String[] result : results) {
			Assert.assertNull("Empty field should not have a validation error", result[0]);
			Assert.assertNull("Date before pivot should not have a validation error", result[1]);
			Assert.assertNull("Date equal to pivot should not have a validation error", result[2]);
			Assert.assertNotNull("Date after pivot should have a validation error", result[3]);
		}
	}

	@Test
	public void testEqual() {
		String[][] results = runTest(DateFieldPivotValidator.EQUAL);

		for (String[] result : results) {
			Assert.assertNull("Empty field should not have a validation error", result[0]);
			Assert.assertNotNull("Date before pivot should have a validation error", result[1]);
			Assert.assertNull("Date equal to pivot should not have a validation error", result[2]);
			Assert.assertNotNull("Date after pivot should have a validation error", result[3]);
		}
	}

	@Test
	public void testAfterOrEqual() {
		String[][] results = runTest(DateFieldPivotValidator.AFTER_OR_EQUAL);

		for (String[] result : results) {
			Assert.assertNull("Empty field should not have a validation error", result[0]);
			Assert.assertNotNull("Date before pivot should have a validation error", result[1]);
			Assert.assertNull("Date equal to pivot should not have a validation error", result[2]);
			Assert.assertNull("Date after pivot should not have a validation error", result[3]);
		}
	}

	@Test
	public void testAfter() {
		String[][] results = runTest(DateFieldPivotValidator.AFTER);

		for (String[] result : results) {
			Assert.assertNull("Empty field should not have a validation error", result[0]);
			Assert.assertNotNull("Date before pivot should have a validation error", result[1]);
			Assert.assertNotNull("Date equal to pivot should have a validation error", result[2]);
			Assert.assertNull("Date after pivot should not have a validation error", result[3]);
		}
	}

	@Test
	public void testGetErrorMessage() {
		final String errorMessage = "DateFieldPivotValidator_Test.testGetErrorMessage.errorMessage";

		DateFieldPivotValidator validator = new DateFieldPivotValidator(
				DateFieldPivotValidator.EQUAL);
		Assert.assertEquals("Incorrect error message",
				InternalMessages.DEFAULT_VALIDATION_ERROR_DATE_EQUAL_TODAY, validator.
				getErrorMessage());

		validator.setErrorMessage(errorMessage);
		Assert.assertEquals("Incorrect error message", errorMessage, validator.getErrorMessage());
	}

	@Test
	public void testVariablePivot() {
		WDateField variableField = new WDateField();
		variableField.setDate(DateUtilities.createDate(31, 1, 2000));

		DateFieldPivotValidator validator = new DateFieldPivotValidator(
				DateFieldPivotValidator.EQUAL, variableField);

		WDateField dateField = new WDateField();
		dateField.addValidator(validator);

		List<Diagnostic> diags = new ArrayList<>();

		dateField.setDate(DateUtilities.createDate(31, 1, 2000));
		dateField.validate(diags);
		Assert.assertTrue("Should not have any validation errors", diags.isEmpty());

		dateField.setDate(DateUtilities.createDate(1, 2, 2000));
		dateField.validate(diags);
		Assert.assertEquals("Should have one validation error", 1, diags.size());
	}

	/**
	 *
	 * @param operator the date operator
	 * @return the test results
	 */
	private String[][] runTest(final int operator) {
		String[][] result = new String[DATES.length][DATES[0].length];

		for (int run = 0; run < DATES.length; run++) {
			DateFieldPivotValidator validator = new DateFieldPivotValidator(operator);
			validator.setFixedPivot(DATES[run][2]);

			WDateField dateField = new WDateField();
			dateField.addValidator(validator);

			List<Diagnostic> diags = new ArrayList<>();

			for (int i = 0; i < DATES[run].length; i++) {
				diags.clear();
				dateField.setDate(DATES[run][i]);
				dateField.validate(diags);

				Assert.assertTrue("Should only have a maximum of 1 error", diags.size() < 2);

				if (!diags.isEmpty()) {
					result[run][i] = (diags.get(0)).getDescription();
				}
			}
		}

		return result;
	}
}
