package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WPhoneNumberField}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WPhoneNumberField_Test extends AbstractWComponentTestCase {

	@Test
	public void testDoHandleRequest() {
		WPhoneNumberField field = new WPhoneNumberField();
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
		WPhoneNumberField field = new WPhoneNumberField();
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
		WPhoneNumberField field = new WPhoneNumberField();
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
		assertAccessorsCorrect(new WPhoneNumberField(), "text", null, "A", "B");
	}

	@Test
	public void testColumnsAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "columns", 0, 1, 2);
	}

	@Test
	public void testMaxLengthAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "maxLength", 0, 1, 2);
	}

	@Test
	public void testMinLengthAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "minLength", 0, 1, 2);
	}

	@Test
	public void testPatternAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "pattern", null, "test1", "test2");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testSetPatternInvalid() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setPattern("[foo");
	}

	@Test
	public void testSuggestionsAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "suggestions", null, new WSuggestions(),
				new WSuggestions());
	}

	@Test
	public void testValidateMaxLength() {
		WPhoneNumberField field = new WPhoneNumberField();
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
		WPhoneNumberField field = new WPhoneNumberField();
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

	@Test
	public void testValidatePattern() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setLocked(true);

		String text = "test1";
		String pattern = "test[123]";

		List<Diagnostic> diags = new ArrayList<>();
		setActiveContext(createUIContext());

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with no pattern set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with no pattern set should be valid", diags.isEmpty());

		field.setText(text);
		field.validate(diags);
		Assert.assertTrue("Text with no pattern set should be valid", diags.isEmpty());

		field.setPattern(pattern);

		field.setText(null);
		field.validate(diags);
		Assert.assertTrue("Null text with pattern set should be valid", diags.isEmpty());

		field.setText("");
		field.validate(diags);
		Assert.assertTrue("Empty text with pattern set should be valid", diags.isEmpty());

		field.setText(text);

		field.validate(diags);
		Assert.assertTrue("Text that matches should be valid", diags.isEmpty());

		field.setText("no match");
		field.validate(diags);
		Assert.assertFalse("Text is no match so should be invalid", diags.isEmpty());
	}

	@Test
	public void testPlaceholderAccessors() {
		assertAccessorsCorrect(new WPhoneNumberField(), "placeholder", null, "A", "B");
	}

	@Test
	public void testAutocompleteDefaultsToNull() {
		WPhoneNumberField field = new WPhoneNumberField();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testSetFullPhoneAutocomplete() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetFullPhoneAutocompleteWithPhoneType() {
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();
		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.FULL.getValue());
			field.setFullPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetFullPhoneAutocompleteWithNullPhoneType() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithTypeFormat() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected;

		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.FULL.getValue());
			field.setAutocomplete(null, phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
			for (Telephone phone : Telephone.values()) {
				expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phone, phoneType);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testSetAutocompleteWithNullTypeFormat() {
		WPhoneNumberField field = new WPhoneNumberField();
		for (Telephone phone : Telephone.values()) {
			field.setAutocomplete(phone, null);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testAddAutocompleteSection() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getNamedSection(sectionName);
		WPhoneNumberField field = new WPhoneNumberField();
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSet() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, Telephone.FULL.getValue());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete();
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionWithPhoneType() {
		String sectionName = "foo";
		PhoneFormat phoneType = PhoneFormat.MOBILE;
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, phoneType.getValue(), Telephone.FULL.getValue());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete(phoneType);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionWithExistingSection() {
		String sectionName = "foo";
		String innerSection = "bar";
		PhoneFormat phoneType = PhoneFormat.MOBILE;
		String expected =
				AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.getNamedSection(innerSection),
						phoneType.getValue(), Telephone.FULL.getValue());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete(phoneType);
		field.addAutocompleteSection(innerSection);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionEmpty() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionNull() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.addAutocompleteSection(null);
	}

	@Test(expected = SystemException.class)
	public void testAddAutocompleteSectionWhenOff() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocompleteOff();
		field.addAutocompleteSection("foo");
	}

	@Test
	public void testClearAutocomplete() {
		WPhoneNumberField field = new WPhoneNumberField();
		Assert.assertNull(field.getAutocomplete());
		field.setFullPhoneAutocomplete();
		Assert.assertNotNull(field.getAutocomplete());
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocomplete() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected = Telephone.LOCAL.getValue();
		field.setLocalPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithType() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected;

		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.LOCAL.getValue());
			field.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithNullType() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setLocalPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.LOCAL.getValue(), field.getAutocomplete());
	}

}
