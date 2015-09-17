package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.validator.AbstractFieldValidator;
import com.github.bordertech.wcomponents.validator.FieldValidator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * WField_Test - Unit tests for {@link WField}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WField_Test extends AbstractWComponentTestCase {

	@Test
	public void testGetField() {
		WTextField textField = new WTextField();
		WField field = new WField("dummy", textField);
		Assert.assertEquals("getField returned incorrect field", textField, field.getField());
	}

	@Test
	public void testSetMandatory() {
		WTextField textField = new WTextField();
		WField field = new WField("dummy", textField);
		Assert.assertFalse("Field should not be mandatory by default", textField.isMandatory());

		field.setMandatory(true);
		Assert.assertTrue("Field should be mandatory after setMandatory(true)", textField.
				isMandatory());

		field.setMandatory(false);
		Assert.assertFalse("Field should be mandatory after setMandatory(true)", textField.
				isMandatory());
	}

	@Test
	public void testAddValidator() {
		MyValidator validator = new MyValidator();

		WTextField textField = new WTextField();
		WField field = new WField("dummy", textField);
		field.addValidator(validator);

		List<FieldValidator> validators = new ArrayList<>();

		for (Iterator<FieldValidator> i = textField.getValidators(); i.hasNext();) {
			validators.add(i.next());
		}

		Assert.assertTrue("addValidator Assert.failed", validators.contains(validator));
	}

	@Test
	public void testSetLabelText() throws IOException, SAXException, XpathException {
		String label1 = "WField_Test.testRenderedFormat";
		String label2 = "WField_Test.testRenderedFormat";

		WField field = new WField(label1, new WTextField());

		setActiveContext(createUIContext());
		field.setLabelText(label2);

		// UIC should have the new label, default should be unchanged
		Assert.assertEquals("Incorrect label text", label2, field.getLabelText());

		resetContext();
		Assert.assertEquals("Incorrect label text", label1, field.getLabelText());
	}

	@Test
	public void testInputWidthAccessors() {
		WField field = new WField("dummy1", new WTextField());
		assertAccessorsCorrect(field, "inputWidth", 0, 1, 2);
	}

	@Test
	public void testSetInputWidthRange() {
		WField field = new WField("dummy2", new WTextField());

		field.setInputWidth(-1);
		Assert.assertEquals("Incorrect width percentage from setter -1", 0, field.getInputWidth());

		field.setInputWidth(0);
		Assert.assertEquals("Incorrect width percentage from setter 0", 0, field.getInputWidth());

		field.setInputWidth(100);
		Assert.
				assertEquals("Incorrect width percentage from setter 100", 100, field.
						getInputWidth());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInputWidthInvalidGreater100() {
		WField field = new WField("dummy3", new WTextField());
		field.setInputWidth(101);
	}

	/**
	 * Test Validator used for testing validation logic.
	 */
	private static class MyValidator extends AbstractFieldValidator {

		@Override
		protected boolean isValid() {
			return false;
		}
	}
}
