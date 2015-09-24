package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WEmailField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WEmailField_Test extends AbstractWComponentTestCase {

	@Test
	public void testDoHandleRequest() {
		WEmailField field = new WEmailField();
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
		WEmailField field = new WEmailField();
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
		WEmailField field = new WEmailField();
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
		assertAccessorsCorrect(new WEmailField(), "text", null, "A", "B");
	}

	@Test
	public void testColumnsAccessors() {
		assertAccessorsCorrect(new WEmailField(), "columns", 0, 1, 2);
	}

	@Test
	public void testMaxLengthAccessors() {
		assertAccessorsCorrect(new WEmailField(), "maxLength", 0, 1, 2);
	}

	@Test
	public void testSuggestionsAccessors() {
		assertAccessorsCorrect(new WEmailField(), "suggestions", null, new WSuggestions(),
				new WSuggestions());
	}

	@Test
	public void testValidate() {
		WEmailField field = new WEmailField();
		field.setLocked(true);

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.validate(diags);
		Assert.assertTrue("Should be valid by default (when empty)", diags.isEmpty());

		String[] invalidAddresses = {"a", "@", "a@", "@test", "a@test", "@b.test", "a@.test", "a@test.", "a@b..test", "a a@test.com", " @test.com", "a@b@c.test", "a@b c.com"};

		for (String address : invalidAddresses) {
			diags.clear();
			field.setText(address);
			field.validate(diags);
			Assert.assertFalse(
					"'" + address + "' passed as a valid email address but should be invalid",
					diags.isEmpty());
		}

		String[] validAddresses = {"a@b.test", "a.b@b.test", "a@b.c.test", "aa@bb.cc.test", "\"a a\"@test.com", "\"a@b\"@c.test"};

		for (String address : validAddresses) {
			diags.clear();
			field.setText(address);
			field.validate(diags);
			Assert.assertTrue("'" + address + "' did not pass as a valid email address", diags.
					isEmpty());
		}
	}

	@Test
	public void testValidateMaxLength() {
		WEmailField field = new WEmailField();
		field.setLocked(true);

		String text = "test@test.com";

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
	public void testSetInvalidEmailAddressErrorMessage() {
		WEmailField field = new WEmailField();
		field.setLocked(true);

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		// Set error message
		String msg = "test message";
		field.setInvalidEmailAddressErrorMessage(msg);

		// Set invalid email address
		field.setText("A");

		// Validate component
		field.validate(diags);

		// Diagnostic should contain custom error message
		Assert.assertEquals("Diagnostic should contain custom error message", msg, diags.get(0).
				getDescription());
	}

}
