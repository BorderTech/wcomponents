package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.AutocompleteSegment;
import com.github.bordertech.wcomponents.autocomplete.segment.Organization;
import com.github.bordertech.wcomponents.autocomplete.segment.Payment;
import com.github.bordertech.wcomponents.autocomplete.segment.Person;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.DateType;
import com.github.bordertech.wcomponents.autocomplete.type.Email;
import com.github.bordertech.wcomponents.autocomplete.type.Numeric;
import com.github.bordertech.wcomponents.autocomplete.type.Password;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.autocomplete.type.Url;
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
 * @author Mark Reeves
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
		String expected;
		for (AutocompleteSegment segment : AddressPart.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : AddressType.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : Organization.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : Payment.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : Person.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : PhoneFormat.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
		for (AutocompleteSegment segment : PhonePart.values()) {
			expected = segment.getValue();
			field.setAutocomplete(segment);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}


	@Test
	public void testSetAutocompleteNullSegment() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteSegment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressPart() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressPart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressType() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullOrganization() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Organization) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPayment() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Payment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPerson() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Person) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhoneFormat() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhoneFormat) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhonePart() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhonePart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testAddressAutocomplete() {
		WTextField field = new WTextField();
		String expected;
		for (AddressType addrType : AddressType.values()) {
			field.setAddressAutocomplete(addrType, null);
			Assert.assertEquals(addrType.getValue(), field.getAutocomplete());
			for (AddressPart part : AddressPart.values()) {
				expected = AutocompleteUtil.getCombinedAddress(addrType, part);
				field.setAddressAutocomplete(addrType, part);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testAddressAutocompleteNullType() {
		WTextField field = new WTextField();
		for (AddressPart part : AddressPart.values()) {
			field.setAddressAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testAddressAutocompleteNull() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAddressAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetPhoneSegmentAutocomplete() {
		WTextField field = new WTextField();
		String expected;
		for (PhoneFormat phoneType : PhoneFormat.values()) {
			field.setPhoneSegmentAutocomplete(phoneType, null);
			Assert.assertEquals(phoneType.getValue(), field.getAutocomplete());
			for (PhonePart part : PhonePart.values()) {
				expected = AutocompleteUtil.getCombinedPhoneSegment(phoneType, part);
				field.setPhoneSegmentAutocomplete(phoneType, part);
				Assert.assertEquals(expected, field.getAutocomplete());
			}
		}
	}

	@Test
	public void testSetPhoneSegmentNullType() {
		WTextField field = new WTextField();
		for (PhonePart part : PhonePart.values()) {
			field.setPhoneSegmentAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetPhoneSegmentAutocompleteNull() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setPhoneSegmentAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WTextField field = new WTextField();
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testSetAutocompleteOffAfterSetting() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection("foo");
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testClearAutocomplete() {
		WTextField field = new WTextField();
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection("foo");
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
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, Person.FAMILY.getValue());
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSettingWithSection() {
		WTextField field = new WTextField();
		String sectionName = "foo";
		String otherSectionName = "bar";
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection(otherSectionName);
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.getNamedSection(otherSectionName),
				Person.FAMILY.getValue());
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionEmpty() {
		WTextField field = new WTextField();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionNull() {
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
	public void testSetAutocompleteDate() {
		WTextField field = new WTextField();
		for (DateType date : DateType.values()) {
			field.setAutocomplete(date);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullDate() {
		WTextField field = new WTextField();
		field.setAutocomplete(DateType.BIRTHDAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((DateType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetBirthdayAutocomplete() {
		WTextField field = new WTextField();
		field.setBirthdayAutocomplete();
		Assert.assertEquals(DateType.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	// With Email field autocomplete
	@Test
	public void testSetAutocompleteEmail() {
		WTextField field = new WTextField();
		for (Email email : Email.values()) {
			field.setAutocomplete(email);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullEmail() {
		WTextField field = new WTextField();
		field.setAutocomplete(Email.EMAIL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Email) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocomplete() {
		WTextField field = new WTextField();
		field.setEmailAutocomplete();
		Assert.assertEquals(Email.EMAIL.getValue(), field.getAutocomplete());
	}

	// with number field autocomplete
	@Test
	public void testSetAutocompleteNumeric() {
		WTextField field = new WTextField();
		for (Numeric number : Numeric.values()) {
			field.setAutocomplete(number);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullNumeric() {
		WTextField field = new WTextField();
		field.setAutocomplete(Numeric.BIRTHDAY_DAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Numeric) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// with password autocomplete
	@Test
	public void testSetAutocompletePassword() {
		WTextField field = new WTextField();
		for (Password pword : Password.values()) {
			field.setAutocomplete(pword);
			Assert.assertEquals(pword.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullPassword() {
		WTextField field = new WTextField();
		field.setAutocomplete(Password.CURRENT);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Password) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// phone number autocomplete
	@Test
	public void testSetFullPhoneAutocomplete() {
		String expected = Telephone.FULL.getValue();

		WTextField field = new WTextField();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetFullPhoneAutocompleteWithPhoneType() {
		String expected;
		WTextField field = new WTextField();
		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.FULL.getValue());
			field.setFullPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetFullPhoneAutocompleteWithNullPhoneType() {
		WTextField field = new WTextField();
		field.setFullPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetAutocompletePhoneTypeFormat() {
		WTextField field = new WTextField();
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
		WTextField field = new WTextField();
		for (Telephone phone : Telephone.values()) {
			field.setAutocomplete(phone, null);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocomplete() {
		WTextField field = new WTextField();
		String expected = Telephone.LOCAL.getValue();
		field.setLocalPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithType() {
		WTextField field = new WTextField();
		String expected;

		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.LOCAL.getValue());
			field.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithNullType() {
		WTextField field = new WTextField();
		field.setLocalPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.LOCAL.getValue(), field.getAutocomplete());
	}

	// with URL autocomplete
	@Test
	public void testSetAutocompleteUrl() {
		WTextField field = new WTextField();
		String expected;

		for (Url url : Url.values()) {
			expected = url.getValue();
			field.setAutocomplete(url);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullUrl() {
		WTextField field = new WTextField();
		field.setAutocomplete(Url.URL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Url) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetUrlAutocomplete() {
		WTextField field = new WTextField();
		field.setUrlAutocomplete();
		Assert.assertEquals(Url.URL.getValue(), field.getAutocomplete());
	}

}
