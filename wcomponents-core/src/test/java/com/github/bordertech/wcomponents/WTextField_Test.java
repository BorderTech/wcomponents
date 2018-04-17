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
 * Unit tests for {@link WTextField}.
 *
 * @author Ming Gao
 * @author Jonathan Austin
 * @since 15/04/2008
 */
public class WTextField_Test extends AbstractWComponentTestCase {

	@Test
	public void testDoHandleRequest() {
		WTextField field = new WTextField();
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
		WTextField field = new WTextField();
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
		WTextField field = new WTextField();
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
		assertAccessorsCorrect(new WTextField(), "text", null, "A", "B");
	}

	@Test
	public void testColumnsAccessors() {
		assertAccessorsCorrect(new WTextField(), "columns", 0, 1, 2);
	}

	@Test
	public void testMaxLengthAccessors() {
		assertAccessorsCorrect(new WTextField(), "maxLength", 0, 1, 2);
	}

	@Test
	public void testMinLengthAccessors() {
		assertAccessorsCorrect(new WTextField(), "minLength", 0, 1, 2);
	}

	@Test
	public void testPatternAccessors() {
		assertAccessorsCorrect(new WTextField(), "pattern", null, "test1", "test2");
	}

	@Test(expected = PatternSyntaxException.class)
	public void testSetPatternInvalid() {
		WTextField field = new WTextField();
		field.setPattern("[foo");
	}

	@Test
	public void testSuggestionsAccessors() {
		assertAccessorsCorrect(new WTextField(), "suggestions", null, new WSuggestions(),
				new WSuggestions());
	}

	@Test
	public void testValidateMaxLength() {
		WTextField field = new WTextField();
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
		WTextField field = new WTextField();
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
		WTextField field = new WTextField();
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
		assertAccessorsCorrect(new WTextField(), "placeholder", null, "A", "B");
	}


	// autocomplete

	@Test
	public void testAutocompleteDefault() {
		WTextField field = new WTextField();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocomplete() {
		WTextField field = new WTextField();
		String expected = AutocompleteUtil.ADDITIONAL_NAME;
		field.setAutocomplete(expected);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteEmpty() {
		// setting autocomplete to an empty value should result in  it not being set.
		WTextField field = new WTextField();
		field.setAutocomplete("");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WTextField field = new WTextField();
		field.setAutocompleteOff();
		Assert.assertEquals(AutocompleteUtil.OFF, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff_afterSetting() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME, "foo");
		field.setAutocompleteOff();
		Assert.assertEquals(AutocompleteUtil.OFF, field.getAutocomplete());
	}

	@Test
	public void testClearAutocomplete() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME, "foo");
		Assert.assertNotNull(field.getAutocomplete());
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals("section-foo", field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSetting() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.FAMILY_NAME);
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSettingWithSection() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String otherSectionName = "bar";
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME, otherSectionName);
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.getNamedSection(otherSectionName),
				AutocompleteUtil.FAMILY_NAME);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSection_empty () {
		WTextField field = new WTextField();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSection_null () {
		WTextField field = new WTextField();
		field.addAutocompleteSection(null);
	}

	@Test(expected = SystemException.class)
	public void testAddAutocompleteSectionWhenOff() {
		WTextField field = new WTextField();
		field.setAutocompleteOff();
		field.addAutocompleteSection("foo");
	}

	// with date field autocomplete
	@Test
	public void testSetAutocomplete_withDate() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullType_withDate() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithSection_withDate() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String expected;
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, date.getValue());
			field.setAutocomplete(date, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithEmptySection_withDate() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date, "");
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withDate() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date, null);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullTypeEmptySection_withDate() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null, "");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullTypeNullSection_withDate() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocomplete() {
		WTextField field = new WTextField();
		field.setDateAutocomplete();
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithSection() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue());
		field.setDateAutocomplete(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithNullSection() {
		WTextField field = new WTextField();
		field.setDateAutocomplete(null);
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithEmptySection() {
		WTextField field = new WTextField();
		field.setDateAutocomplete("");
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}



	// With Email field autocomplete
	private static final String DEFAULT_VALUE_EMAIL = AutocompleteUtil.EmailAutocomplete.EMAIL.getValue();

	@Test
	public void testSetAutocomplete_withEmail() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullType_withEmail() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.EmailAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocomplete() {
		WTextField field = new WTextField();
		field.setEmailAutocomplete();
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWithSection() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, DEFAULT_VALUE_EMAIL);
		field.setEmailAutocomplete(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWithEmptySection() {
		WTextField field = new WTextField();
		field.setEmailAutocomplete("");
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWitNullSection() {
		WTextField field = new WTextField();
		field.setEmailAutocomplete(null);
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteTypeAndSection_withEmail() {
		WTextField field = new WTextField();
		final String sectionName = "foo";
		String expected;

		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, email.getValue());
			field.setAutocomplete(email, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteTypeAndEmptySection_withEmail() {
		WTextField field = new WTextField();

		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email, "");
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteTypeAndNullSection_withEmail() {
		WTextField field = new WTextField();

		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email, null);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	// with number field autocomplete
	@Test
	public void testSetAutocomplete_withNumber() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullValue_withNumber() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.NumericAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithSection_withNumber() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String expected;
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, number.getValue());
			field.setAutocomplete(number, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithEmptySection_withNumber() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number, "");
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withNumber() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number, null);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullValueEmptySection_withNumber() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.NumericAutocomplete)null, "");
		Assert.assertNull(field.getAutocomplete());
	}


	// with password autocomplete
	@Test
	public void testSetAutocomplete_withPassword() {
		WTextField field = new WTextField();
		String expected;

		for (AutocompleteUtil.PasswordAutocomplete pword : AutocompleteUtil.PasswordAutocomplete.values()) {
			expected = pword.getValue();
			field.setAutocomplete(pword);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithSection_withPassword() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = "foo";

		for (AutocompleteUtil.PasswordAutocomplete pword : AutocompleteUtil.PasswordAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, pword.getValue());
			field.setAutocomplete(pword, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithEmptySection_withPassword() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = "";

		for (AutocompleteUtil.PasswordAutocomplete pword : AutocompleteUtil.PasswordAutocomplete.values()) {
			expected = pword.getValue();
			field.setAutocomplete(pword, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withPassword() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = null;

		for (AutocompleteUtil.PasswordAutocomplete pword : AutocompleteUtil.PasswordAutocomplete.values()) {
			expected = pword.getValue();
			field.setAutocomplete(pword, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullPasswordType_withPassword() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.PasswordAutocomplete.CURRENT);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.PasswordAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPasswordType_withSection_withPassword() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.PasswordAutocomplete.CURRENT, "foo");
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.PasswordAutocomplete)null, "bar");
		Assert.assertNull(field.getAutocomplete());
	}

	// phone number autocomplete
	private static final String DEFAULT_VALUE_PHONE = AutocompleteUtil.TelephoneAutocomplete.FULL.getValue();

	@Test
	public void testSetAutoCompleteDefaults_withPhone() {
		String expected = DEFAULT_VALUE_PHONE;

		WTextField field = new WTextField();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithPhoneType() {
		String strPhoneType;
		String expected;
		WTextField field = new WTextField();
		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			strPhoneType = phoneType.getValue();
			expected = AutocompleteUtil.getCombinedAutocomplete(strPhoneType, DEFAULT_VALUE_PHONE);
			field.setAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithPhoneType_null() {
		WTextField field = new WTextField();
		String expected = AutocompleteUtil.getCombinedAutocomplete(null, DEFAULT_VALUE_PHONE);
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocompleteType) null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_withPhone() {
		String sectionName = "foo";
		String strPhoneType;
		String expected;
		WTextField field = new WTextField();
		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			strPhoneType = phoneType.getValue();
			expected = AutocompleteUtil.getCombinedForSection(sectionName, strPhoneType, DEFAULT_VALUE_PHONE);
			field.setAutocomplete(phoneType, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_emptySectionName_withPhone() {
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.FAX;
		String expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), DEFAULT_VALUE_PHONE);
		WTextField field = new WTextField();
		field.setAutocomplete(phoneType, "");
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullSectionName_withPhone() {
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.FAX;
		String expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), DEFAULT_VALUE_PHONE);
		WTextField field = new WTextField();
		field.setAutocomplete(phoneType, (String)null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullType_withPhone() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, DEFAULT_VALUE_PHONE);
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocompleteType)null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_withPhone() {
		String sectionName = "foo";
		String expected;
		WTextField field = new WTextField();

		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
				expected = AutocompleteUtil.getCombinedForSection(sectionName, phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phoneType, phone, sectionName);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_emptySection_withPhone() {
		String sectionName = "";
		String expected;
		WTextField field = new WTextField();

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
		WTextField field = new WTextField();

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
		WTextField field = new WTextField();

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(),
					AutocompleteUtil.TelephoneAutocomplete.FULL.getValue());
			field.setAutocomplete(phoneType, null, null);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noType_withPhone() {
		String sectionName = "foo";
		String expected;
		WTextField field = new WTextField();

		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, phone.getValue());
			field.setAutocomplete(null, phone, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormat_withPhone() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, DEFAULT_VALUE_PHONE);
		WTextField field = new WTextField();
		field.setAutocomplete(null, null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatNullSection_withPhone() {
		WTextField field = new WTextField();
		field.setAutocomplete(null, null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatEmptySection_withPhone() {
		WTextField field = new WTextField();
		field.setAutocomplete(null, null, "");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete_onlyPhone() {
		WTextField field = new WTextField();
		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			field.setAutocomplete(phone);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetAutocomplete_onlyNullPhone() {
		WTextField field = new WTextField();
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteTypeFormat_withPhone() {
		WTextField field = new WTextField();
		String expected;

		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
				expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), phone.getValue());
				field.setAutocomplete(phoneType, phone);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	// with URL autocomplete
	@Test
	public void testSetAutocomplete_withUrl() {
		WTextField field = new WTextField();
		String expected;

		for (AutocompleteUtil.UrlAutocomplete url : AutocompleteUtil.UrlAutocomplete.values()) {
			expected = url.getValue();
			field.setAutocomplete(url);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithSection_withUrl() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = "foo";

		for (AutocompleteUtil.UrlAutocomplete url : AutocompleteUtil.UrlAutocomplete.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, url.getValue());
			field.setAutocomplete(url, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithEmptySection_withUrl() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = "";

		for (AutocompleteUtil.UrlAutocomplete url : AutocompleteUtil.UrlAutocomplete.values()) {
			expected = url.getValue();
			field.setAutocomplete(url, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withUrl() {
		WTextField field = new WTextField();
		String expected;
		String sectionName = null;

		for (AutocompleteUtil.UrlAutocomplete url : AutocompleteUtil.UrlAutocomplete.values()) {
			expected = url.getValue();
			field.setAutocomplete(url, sectionName);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullUrlType() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.UrlAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullUrlType_withSection() {
		WTextField field = new WTextField();
		field.setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL, "foo");
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.UrlAutocomplete)null, "bar");
		Assert.assertNull(field.getAutocomplete());
	}

}
