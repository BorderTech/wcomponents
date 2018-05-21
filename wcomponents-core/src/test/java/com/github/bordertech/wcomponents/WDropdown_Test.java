package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
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
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteSegment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressPart() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressPart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressType() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullOrganization() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Organization) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPayment() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Payment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPerson() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Person) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhoneFormat() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhoneFormat) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhonePart() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhonePart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAddressAutocomplete() {
		WDropdown field = new WDropdown();
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
	public void testSetAddressAutocompleteNullType() {
		WDropdown field = new WDropdown();
		for (AddressPart part : AddressPart.values()) {
			field.setAddressAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAddressAutocompleteNull() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAddressAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetPhoneSegmentAutocomplete() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		for (PhonePart part : PhonePart.values()) {
			field.setPhoneSegmentAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetPhoneSegmentAutocompleteNull() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setPhoneSegmentAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WDropdown field = new WDropdown();
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testSetAutocompleteOffAfterSetting() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection("foo");
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testClearAutocomplete() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Person.FAMILY);
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
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, Person.FAMILY.getValue());
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSettingWithSection() {
		WDropdown field = new WDropdown();
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
		WDropdown field = new WDropdown();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionNull() {
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
	public void testSetAutocompleteDate() {
		WDropdown field = new WDropdown();
		for (DateType date : DateType.values()) {
			field.setAutocomplete(date);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullDate() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(DateType.BIRTHDAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((DateType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetBirthdayAutocomplete() {
		WDropdown field = new WDropdown();
		field.setBirthdayAutocomplete();
		Assert.assertEquals(DateType.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	// With Email field autocomplete
	@Test
	public void testSetAutocompleteEmail() {
		WDropdown field = new WDropdown();
		for (Email email : Email.values()) {
			field.setAutocomplete(email);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullEmail() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Email.EMAIL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Email) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocomplete() {
		WDropdown field = new WDropdown();
		field.setEmailAutocomplete();
		Assert.assertEquals(Email.EMAIL.getValue(), field.getAutocomplete());
	}

	// with number field autocomplete
	@Test
	public void testSetAutocompleteNumeric() {
		WDropdown field = new WDropdown();
		for (Numeric number : Numeric.values()) {
			field.setAutocomplete(number);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullNumeric() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Numeric.BIRTHDAY_DAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Numeric) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// with password autocomplete
	@Test
	public void testSetAutocompletePassword() {
		WDropdown field = new WDropdown();
		for (Password pword : Password.values()) {
			field.setAutocomplete(pword);
			Assert.assertEquals(pword.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullPassword() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Password.CURRENT);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Password) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// phone number autocomplete
	@Test
	public void testSetFullPhoneAutocomplete() {
		WDropdown field = new WDropdown();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetFullPhoneAutocompleteWithType() {
		String expected;
		WDropdown field = new WDropdown();
		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.FULL.getValue());
			field.setFullPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetFullPhoneAutocompleteWithNullType() {
		WDropdown field = new WDropdown();
		field.setFullPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetAutocompletePhoneTypeFormat() {
		WDropdown field = new WDropdown();
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
	public void testSetAutocompleteNullPhoneTypeFormat() {
		WDropdown field = new WDropdown();
		for (Telephone phone : Telephone.values()) {
			field.setAutocomplete(phone, null);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocomplete() {
		WDropdown field = new WDropdown();
		String expected = Telephone.LOCAL.getValue();
		field.setLocalPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithType() {
		WDropdown field = new WDropdown();
		String expected;

		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.LOCAL.getValue());
			field.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithNullType() {
		WDropdown field = new WDropdown();
		field.setLocalPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.LOCAL.getValue(), field.getAutocomplete());
	}

	// with URL autocomplete
	@Test
	public void testSetAutocompleteUrl() {
		WDropdown field = new WDropdown();
		String expected;

		for (Url url : Url.values()) {
			expected = url.getValue();
			field.setAutocomplete(url);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullUrl() {
		WDropdown field = new WDropdown();
		field.setAutocomplete(Url.URL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Url) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetUrlAutocomplete() {
		WDropdown field = new WDropdown();
		field.setUrlAutocomplete();
		Assert.assertEquals(Url.URL.getValue(), field.getAutocomplete());
	}
}
