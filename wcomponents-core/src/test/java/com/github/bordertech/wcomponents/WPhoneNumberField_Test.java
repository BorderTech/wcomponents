package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
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

	/**
	 * Internal helper for consistency.
	 * @return the value of the full phone number autocomplete attribute value
	 */
	private String getACDefault() {
		return AutocompleteUtil.TelephoneAutocomplete.FULL.getValue();
	}

	@Test
	public void testSetAutocompleteOff() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocompleteOff();
		Assert.assertEquals(AutocompleteUtil.OFF, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteDefaults() {
		String expected = getACDefault();

		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithPhoneType() {
		String strPhoneType;
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();
		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			strPhoneType = phoneType.getValue();
			expected = AutocompleteUtil.getCombinedAutocomplete(strPhoneType, getACDefault());
			field.setAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithPhoneType_null() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected = AutocompleteUtil.getCombinedAutocomplete(null, getACDefault());
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocompleteType) null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType() {
		String sectionName = "foo";
		String strPhoneType;
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();
		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			strPhoneType = phoneType.getValue();
			expected = AutocompleteUtil.getCombinedForSection(sectionName, strPhoneType, getACDefault());
			field.setAutocomplete(phoneType, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_emptySectionName() {
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.FAX;
		String expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(phoneType, "");
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullSectionName() {
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.FAX;
		String expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(phoneType, (String)null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullType() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection() {
		String sectionName = "foo";
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();

		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
				expected = AutocompleteUtil.getCombinedForSection(sectionName, phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phoneType, phone, sectionName);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_emptySection() {
		String sectionName = "";
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();

		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
				expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phoneType, phone, sectionName);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noPhoneFormat() {
		String sectionName = "foo";
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, phoneType.getValue(),
					AutocompleteUtil.TelephoneAutocomplete.FULL.getValue());
			field.setAutocomplete(phoneType, null, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noPhoneFormatNoSection() {
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(),
					AutocompleteUtil.TelephoneAutocomplete.FULL.getValue());
			field.setAutocomplete(phoneType, null, null);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noType() {
		String sectionName = "foo";
		String expected;
		WPhoneNumberField field = new WPhoneNumberField();

		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, phone.getValue());
			field.setAutocomplete(null, phone, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormat() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(null, null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatNullSection() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(null, null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete_onlyPhone() {
		WPhoneNumberField field = new WPhoneNumberField();
		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			field.setAutocomplete(phone);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetAutocomplete_onlyNullPhone() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatEmptySection() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(null, null, "");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteTypeFormat() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected;

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
				expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phoneType, phone);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testAddAutocompleteSection() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection_afterDefaultSet() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setFullPhoneAutocomplete();
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection_withPhoneType() {
		String sectionName = "foo";
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.MOBILE;
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, phoneType.getValue(), getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(phoneType);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection_withExistingSection() {
		String sectionName = "foo";
		String section2Name = "bar";
		String expectedSection2Name = AutocompleteUtil.getNamedSection(section2Name);
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.MOBILE;
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, expectedSection2Name, phoneType.getValue(), getACDefault());
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(phoneType, section2Name);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSection_empty () {
		WPhoneNumberField field = new WPhoneNumberField();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSection_null () {
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
	public void testClearAutocomplete_afterComplexSetUp() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.setAutocomplete(AutocompleteUtil.TelephoneAutocompleteType.MOBILE, "foo");
		field.addAutocompleteSection("bar");
		Assert.assertNotNull(field.getAutocomplete());
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testClearAutocomplete_nothingToClear() {
		WPhoneNumberField field = new WPhoneNumberField();
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocomplete() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected = AutocompleteUtil.TelephoneAutocomplete.LOCAL.getValue();
		field.setLocalPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithType() {
		WPhoneNumberField field = new WPhoneNumberField();
		String expected;

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), AutocompleteUtil.TelephoneAutocomplete.LOCAL.getValue());
			field.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

}
