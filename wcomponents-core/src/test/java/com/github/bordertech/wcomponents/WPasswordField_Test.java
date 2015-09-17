package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WPasswordField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPasswordField_Test extends AbstractWComponentTestCase {

	@Test
	public void testDoHandleRequest() {
		WPasswordField field = new WPasswordField();
		field.setLocked(true);

		setActiveContext(createUIContext());

		// Request with Empty Value and Field is null (No Change)
		field.setData(null);
		MockRequest request = new MockRequest();
		request.setParameter(field.getId(), "");
		boolean changed = field.doHandleRequest(request);

		Assert.assertFalse(
				"doHandleRequest should have returned false for request with empty value and field is null",
				changed);
		Assert.assertNull("Value should still be null after empty request", field.getData());

		// Request with Empty Value and Field is empty (No Change)
		field.setData("");
		request = new MockRequest();
		request.setParameter(field.getId(), "");
		changed = field.doHandleRequest(request);

		Assert
				.assertFalse(
						"doHandleRequest should have returned false for request with empty value and field is empty",
						changed);
		Assert.assertEquals("Value should still be empty after empty request", "", field.getData());

		// Request with Different Value (Change)
		request = new MockRequest();
		request.setParameter(field.getId(), "X");
		changed = field.doHandleRequest(request);

		Assert.assertTrue(
				"doHandleRequest should have returned true for request with different value",
				changed);
		Assert.assertEquals("Value not set after request", "X", field.getData());

		// Request with Same Value (No Change)
		request = new MockRequest();
		request.setParameter(field.getId(), "X");
		changed = field.doHandleRequest(request);

		Assert.assertFalse("doHandleRequest should have returned false for request with same value",
				changed);
		Assert.assertEquals("Value should not have changed after request with same value", "X",
				field.getData());

		// Request with Empty Value (Change)
		request = new MockRequest();
		request.setParameter(field.getId(), "");
		changed = field.doHandleRequest(request);

		Assert
				.assertTrue(
						"doHandleRequest should have returned true for request going back to an empty value",
						changed);
		Assert.assertNull("Value should go back to null for request with empty value", field.
				getData());
	}

	@Test
	public void testGetRequestValue() {
		WPasswordField field = new WPasswordField();
		field.setLocked(true);

		setActiveContext(createUIContext());

		// Set current value
		field.setText("A");

		// Empty Request (not present, should return current value)
		MockRequest request = new MockRequest();
		Assert.
				assertEquals(
						"Current value of the field should have been returned for empty request",
						"A",
						field.getRequestValue(request));

		// Request with "empty" value (should return null as an empty value on the request is treated as null)
		request = new MockRequest();
		request.setParameter(field.getId(), "");
		Assert
				.assertNull("Null should have been returned for request with empty value", field.
						getRequestValue(request));

		// Request with value (should return the value on the request)
		request = new MockRequest();
		request.setParameter(field.getId(), "X");
		Assert.assertEquals("Value from the request should have been returned", "X", field.
				getRequestValue(request));
	}

	@Test
	public void testGetValue() {
		WPasswordField field = new WPasswordField();
		field.setLocked(true);

		setActiveContext(createUIContext());

		// Set data as a null value
		field.setData(null);
		Assert.assertNull("getValue should return null when data is null", field.getValue());

		// Set data as a empty string
		field.setData("");
		Assert.assertNull("getValue should return null when data is an empty string", field.
				getValue());

		// Set data as a String value
		field.setData("A");
		Assert.assertEquals("getValue returned the incorrect value for the data", "A", field.
				getValue());

		// Set data as an Object
		Object object = new Date();
		field.setData(object);
		Assert.
				assertEquals("getValue should return the string value of the data", object.
						toString(), field.getValue());
	}

	@Test
	public void testTextAccessors() {
		assertAccessorsCorrect(new WPasswordField(), "text", null, "A", "B");
	}

	@Test
	public void testColumnsAccessors() {
		assertAccessorsCorrect(new WPasswordField(), "columns", 0, 1, 2);
	}

	@Test
	public void testMaxLengthAccessors() {
		assertAccessorsCorrect(new WPasswordField(), "maxLength", 0, 1, 2);
	}

	@Test
	public void testMinLengthAccessors() {
		assertAccessorsCorrect(new WPasswordField(), "minLength", 0, 1, 2);
	}

	@Test
	public void testValidateMaxLength() {
		WPasswordField field = new WPasswordField();
		field.setLocked(true);

		String text = "test";

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no maximum set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with no maximum set should be valid", diags.isEmpty());

		field.setText(text);
		field.validate(diags);
		Assert.assertTrue("Text with no maximum set should be valid", diags.isEmpty());

		field.setMaxLength(1);

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with maximum set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with maximum set should be valid", diags.isEmpty());

		field.setText(text);

		field.setMaxLength(text.length() + 1);
		field.validate(diags);
		Assert.assertTrue("Text is less than maximum so should be valid", diags.isEmpty());

		field.setMaxLength(text.length());
		field.validate(diags);
		Assert.assertTrue("Text is the same as maximum so should be valid", diags.isEmpty());

		field.setMaxLength(text.length() - 1);
		field.validate(diags);
		Assert.assertFalse("Text is longer than maximum so should be invalid", diags.isEmpty());
	}

	@Test
	public void testValidateMinLength() {
		WPasswordField field = new WPasswordField();
		field.setLocked(true);

		String text = "test";

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no minimum set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with no minimum set should be valid", diags.isEmpty());

		field.setText(text);
		field.validate(diags);
		Assert.assertTrue("Text with no minimum set should be valid", diags.isEmpty());

		field.setMinLength(1);

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with minimum set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with minimum set should be valid", diags.isEmpty());

		field.setText(text);

		field.setMinLength(text.length() - 1);
		field.validate(diags);
		Assert.assertTrue("Text is longer than minimum so should be valid", diags.isEmpty());

		field.setMinLength(text.length());
		field.validate(diags);
		Assert.assertTrue("Text is the same as minimum so should be valid", diags.isEmpty());

		field.setMinLength(text.length() + 1);
		field.validate(diags);
		Assert.assertFalse("Text is shorter than minimum so should be invalid", diags.isEmpty());
	}

}
