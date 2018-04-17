package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for WDropdown.
 *
 * @author Ming Gao
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDropdown_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WDropdown dropdown = new WDropdown();
		Assert.assertNull("Incorrect options returned", dropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WDropdown dropdown = new WDropdown(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), dropdown.
				getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WDropdown dropdown = new WDropdown(options);
		Assert.assertEquals("Incorrect options returned", options, dropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WDropdown dropdown = new WDropdown(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, dropdown.
				getLookupTable());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testEditableAccessors() {
		assertAccessorsCorrect(new WDropdown(), "editable", false, true, false);
	}

	@Test
	public void testOptionWidthAccessors() {
		assertAccessorsCorrect(new WDropdown(), "optionWidth", 0, 1, 2);
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WDropdown(), "type", null, WDropdown.DropdownType.NATIVE, WDropdown.DropdownType.COMBO);
	}

	// autocomplete

	@Test
	public void testAutocompleteDefault() {
		WDropdown field = new WDropdown();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocomplete() {
		WDropdown field = new WDropdown();
		String expected = AutocompleteUtil.ADDITIONAL_NAME;
		field.setAutocomplete(expected);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteEmpty() {
		// setting autocomplete to an empty value should result in  it not being set.
		WDropdown field = new WDropdown();
		field.setAutocomplete("");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WDropdown field = new WDropdown();
		field.setAutocompleteOff();
		Assert.assertEquals(AutocompleteUtil.OFF, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff_afterSetting() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME, "foo");
		field.setAutocompleteOff();
		Assert.assertEquals(AutocompleteUtil.OFF, field.getAutocomplete());
	}

	@Test
	public void testClearAutocomplete() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME, "foo");
		Assert.assertNotNull(field.getAutocomplete());
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection() {
		WDropdown field = new WDropdown();
		String sectionName = "foo";
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals("section-foo", field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSetting() {
		WDropdown field = new WDropdown();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.FAMILY_NAME);
		field.setAutocomplete(AutocompleteUtil.FAMILY_NAME);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSettingWithSection() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSection_null () {
		WDropdown field = new WDropdown();
		field.addAutocompleteSection(null);
	}

	@Test(expected = SystemException.class)
	public void testAddAutocompleteSectionWhenOff() {
		WDropdown field = new WDropdown();
		field.setAutocompleteOff();
		field.addAutocompleteSection("foo");
	}

	// with date field autocomplete
	@Test
	public void testSetAutocomplete_withDate() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullType_withDate() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithSection_withDate() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date, "");
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withDate() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.DateAutocomplete date : AutocompleteUtil.DateAutocomplete.values()) {
			field.setAutocomplete(date, null);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullTypeEmptySection_withDate() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null, "");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullTypeNullSection_withDate() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.DateAutocomplete)null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocomplete() {
		WDropdown field = new WDropdown();
		field.setDateAutocomplete();
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithSection() {
		WDropdown field = new WDropdown();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue());
		field.setDateAutocomplete(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithNullSection() {
		WDropdown field = new WDropdown();
		field.setDateAutocomplete(null);
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetDateAutocompleteWithEmptySection() {
		WDropdown field = new WDropdown();
		field.setDateAutocomplete("");
		Assert.assertEquals(AutocompleteUtil.DateAutocomplete.BIRTHDAY.getValue(), field.getAutocomplete());
	}



	// With Email field autocomplete
	private static final String DEFAULT_VALUE_EMAIL = AutocompleteUtil.EmailAutocomplete.EMAIL.getValue();

	@Test
	public void testSetAutocomplete_withEmail() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullType_withEmail() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.EmailAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocomplete() {
		WDropdown field = new WDropdown();
		field.setEmailAutocomplete();
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWithSection() {
		WDropdown field = new WDropdown();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, DEFAULT_VALUE_EMAIL);
		field.setEmailAutocomplete(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWithEmptySection() {
		WDropdown field = new WDropdown();
		field.setEmailAutocomplete("");
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocompleteWitNullSection() {
		WDropdown field = new WDropdown();
		field.setEmailAutocomplete(null);
		Assert.assertEquals(DEFAULT_VALUE_EMAIL, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteTypeAndSection_withEmail() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();

		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email, "");
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteTypeAndNullSection_withEmail() {
		WDropdown field = new WDropdown();

		for (AutocompleteUtil.EmailAutocomplete email : AutocompleteUtil.EmailAutocomplete.values()) {
			field.setAutocomplete(email, null);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	// with number field autocomplete
	@Test
	public void testSetAutocomplete_withNumber() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullValue_withNumber() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.NumericAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithSection_withNumber() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number, "");
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullSection_withNumber() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.NumericAutocomplete number : AutocompleteUtil.NumericAutocomplete.values()) {
			field.setAutocomplete(number, null);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullValueEmptySection_withNumber() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.NumericAutocomplete)null, "");
		Assert.assertNull(field.getAutocomplete());
	}


	// with password autocomplete
	@Test
	public void testSetAutocomplete_withPassword() {
		WDropdown field = new WDropdown();
		String expected;

		for (AutocompleteUtil.PasswordAutocomplete pword : AutocompleteUtil.PasswordAutocomplete.values()) {
			expected = pword.getValue();
			field.setAutocomplete(pword);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithSection_withPassword() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		field.setAutocomplete(AutocompleteUtil.PasswordAutocomplete.CURRENT);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.PasswordAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPasswordType_withSection_withPassword() {
		WDropdown field = new WDropdown();
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

		WDropdown field = new WDropdown();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithPhoneType() {
		String strPhoneType;
		String expected;
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.TelephoneAutocompleteType phoneType : AutocompleteUtil.TelephoneAutocompleteType.values()) {
			strPhoneType = phoneType.getValue();
			expected = AutocompleteUtil.getCombinedAutocomplete(strPhoneType, DEFAULT_VALUE_PHONE);
			field.setAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithPhoneType_null() {
		WDropdown field = new WDropdown();
		String expected = AutocompleteUtil.getCombinedAutocomplete(null, DEFAULT_VALUE_PHONE);
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocompleteType) null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_withPhone() {
		String sectionName = "foo";
		String strPhoneType;
		String expected;
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		field.setAutocomplete(phoneType, "");
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullSectionName_withPhone() {
		AutocompleteUtil.TelephoneAutocompleteType phoneType = AutocompleteUtil.TelephoneAutocompleteType.FAX;
		String expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), DEFAULT_VALUE_PHONE);
		WDropdown field = new WDropdown();
		field.setAutocomplete(phoneType, (String)null);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithSectionAndType_nullType_withPhone() {
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, DEFAULT_VALUE_PHONE);
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocompleteType)null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_withPhone() {
		String sectionName = "foo";
		String expected;
		WDropdown field = new WDropdown();

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
		WDropdown field = new WDropdown();

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
		WDropdown field = new WDropdown();

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
		WDropdown field = new WDropdown();

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
		WDropdown field = new WDropdown();

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
		WDropdown field = new WDropdown();
		field.setAutocomplete(null, null, sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatNullSection_withPhone() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(null, null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutoCompleteWithTypeFormatAndSection_noTypeNoFormatEmptySection_withPhone() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(null, null, "");
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete_onlyPhone() {
		WDropdown field = new WDropdown();
		for (AutocompleteUtil.TelephoneAutocomplete phone : AutocompleteUtil.TelephoneAutocomplete.values()) {
			field.setAutocomplete(phone);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetAutocomplete_onlyNullPhone() {
		WDropdown field = new WDropdown();
		field.setAutocomplete((AutocompleteUtil.TelephoneAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteTypeFormat_withPhone() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		String expected;

		for (AutocompleteUtil.UrlAutocomplete url : AutocompleteUtil.UrlAutocomplete.values()) {
			expected = url.getValue();
			field.setAutocomplete(url);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithSection_withUrl() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		field.setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.UrlAutocomplete)null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullUrlType_withSection() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(AutocompleteUtil.UrlAutocomplete.URL, "foo");
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteUtil.UrlAutocomplete)null, "bar");
		Assert.assertNull(field.getAutocomplete());
	}
}
