package com.github.bordertech.wcomponents.validator;

import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RegExFieldValidator_Test - unit test for {@link RegExFieldValidator}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class RegExFieldValidator_Test {

	private static final String ERROR_MESSAGE = "Error message";

	private static final String TEST_TEXT = "Hello world";

	private static final String PATTERN = "^[A-Za-z ]*$";

	private WTextField field1;

	private WTextField field2;

	@Before
	public void setUp() {
		field1 = new WTextField();
		field1.addValidator(new RegExFieldValidator(PATTERN, ERROR_MESSAGE));

		field2 = new WTextField();
		field2.addValidator(new RegExFieldValidator(PATTERN));
	}

	@Test
	public void testValidateNullField() {
		List<Diagnostic> diags = new ArrayList<>();

		field1.validate(diags);
		Assert.assertTrue("Null field should not have a validation error", diags.isEmpty());

		field2.validate(diags);
		Assert.assertTrue("Null field should not have a validation error", diags.isEmpty());
	}

	@Test
	public void testValidateEmptyField() {
		List<Diagnostic> diags = new ArrayList<>();

		field1.setText("");
		field1.validate(diags);
		Assert.assertTrue("Empty String field should not have a validation error", diags.isEmpty());
	}

	@Test
	public void testValidateCorrectText() {
		List<Diagnostic> diags = new ArrayList<>();

		field1.setText(TEST_TEXT);
		field1.validate(diags);
		Assert.assertTrue("Field with matching text should not have a validation error", diags.
				isEmpty());

		field2.setText(TEST_TEXT);
		field2.validate(diags);
		Assert.assertTrue("Field with matching text should not have a validation error", diags.
				isEmpty());
	}

	@Test
	public void testValidateIncorrectText() {
		List<Diagnostic> diags = new ArrayList<>();

		field1.setText(TEST_TEXT + '_');
		field1.validate(diags);
		Assert.assertFalse("Field with non-matching text should have a validation error", diags.
				isEmpty());

		Diagnostic diag = diags.get(0);
		Assert.assertEquals("Incorrect error message", ERROR_MESSAGE, diag.getDescription());

		diags.clear();

		field2.setText(TEST_TEXT + '_');
		field2.validate(diags);
		Assert.assertFalse("Field with non-matching text should have a validation error", diags.
				isEmpty());

		diag = diags.get(0);
		String text = diag.getDescription();
		Assert.assertTrue("Error message should not be empty", text != null && text.length() > 0);
	}
}
