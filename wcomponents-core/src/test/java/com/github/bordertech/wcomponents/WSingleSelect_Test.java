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
 * WSingleSelect_Test - unit tests for {@link WSingleSelect}.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WSingleSelect_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WSingleSelect single = new WSingleSelect();
		Assert.assertNull("Incorrect options returned", single.getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WSingleSelect single = new WSingleSelect(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), single.
				getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WSingleSelect single = new WSingleSelect(options);
		Assert.assertEquals("Incorrect options returned", options, single.getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WSingleSelect single = new WSingleSelect(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, single.
				getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testRowAccessors() {
		assertAccessorsCorrect(new WSingleSelect(), "rows", 0, 1, 2);
	}

	// autocomplete

	@Test
	public void testAutocompleteDefault() {
		WSingleSelect field = new WSingleSelect();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocomplete() {
		WSingleSelect field = new WSingleSelect();
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
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AutocompleteSegment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressPart() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressPart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullAddressType() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((AddressType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullOrganization() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Organization) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPayment() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Payment) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPerson() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Person) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhoneFormat() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhoneFormat) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteNullPhonePart() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((PhonePart) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAddressAutocomplete() {
		WSingleSelect field = new WSingleSelect();
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
		WSingleSelect field = new WSingleSelect();
		for (AddressPart part : AddressPart.values()) {
			field.setAddressAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAddressAutocompleteNull() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAddressAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetPhoneSegmentAutocomplete() {
		WSingleSelect field = new WSingleSelect();
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
		WSingleSelect field = new WSingleSelect();
		for (PhonePart part : PhonePart.values()) {
			field.setPhoneSegmentAutocomplete(null, part);
			Assert.assertEquals(part.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetPhoneSegmentAutocompleteNull() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.GIVEN);
		Assert.assertNotNull(field.getAutocomplete());
		field.setPhoneSegmentAutocomplete(null, null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteOff() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testSetAutocompleteOffAfterSetting() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection("foo");
		field.setAutocompleteOff();
		Assert.assertTrue(field.isAutocompleteOff());
	}

	@Test
	public void testClearAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Person.FAMILY);
		Assert.assertNotNull(field.getAutocomplete());
		field.clearAutocomplete();
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSection() {
		WSingleSelect field = new WSingleSelect();
		String sectionName = "foo";
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals("section-foo", field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSetting() {
		WSingleSelect field = new WSingleSelect();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getCombinedForSection(sectionName, Person.FAMILY.getValue());
		field.setAutocomplete(Person.FAMILY);
		field.addAutocompleteSection(sectionName);
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testAddAutocompleteSectionAfterSettingWithSection() {
		WSingleSelect field = new WSingleSelect();
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
		WSingleSelect field = new WSingleSelect();
		field.addAutocompleteSection("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAutocompleteSectionNull() {
		WSingleSelect field = new WSingleSelect();
		field.addAutocompleteSection(null);
	}

	@Test(expected = SystemException.class)
	public void testAddAutocompleteSectionWhenOff() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocompleteOff();
		field.addAutocompleteSection("foo");
	}

	// with date field autocomplete
	@Test
	public void testSetAutocompleteDate() {
		WSingleSelect field = new WSingleSelect();
		for (DateType date : DateType.values()) {
			field.setAutocomplete(date);
			Assert.assertEquals(date.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullDate() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(DateType.BIRTHDAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((DateType) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetBirthdayAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		field.setBirthdayAutocomplete();
		Assert.assertEquals(DateType.BIRTHDAY.getValue(), field.getAutocomplete());
	}

	// With Email field autocomplete
	@Test
	public void testSetAutocompleteEmail() {
		WSingleSelect field = new WSingleSelect();
		for (Email email : Email.values()) {
			field.setAutocomplete(email);
			Assert.assertEquals(email.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullEmail() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Email.EMAIL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Email) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetEmailAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		field.setEmailAutocomplete();
		Assert.assertEquals(Email.EMAIL.getValue(), field.getAutocomplete());
	}

	// with number field autocomplete
	@Test
	public void testSetAutocompleteNumeric() {
		WSingleSelect field = new WSingleSelect();
		for (Numeric number : Numeric.values()) {
			field.setAutocomplete(number);
			Assert.assertEquals(number.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullNumeric() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Numeric.BIRTHDAY_DAY);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Numeric) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// with password autocomplete
	@Test
	public void testSetAutocompletePassword() {
		WSingleSelect field = new WSingleSelect();
		for (Password pword : Password.values()) {
			field.setAutocomplete(pword);
			Assert.assertEquals(pword.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullPassword() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Password.CURRENT);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Password) null);
		Assert.assertNull(field.getAutocomplete());
	}

	// phone number autocomplete
	@Test
	public void testSetFullPhoneAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		field.setFullPhoneAutocomplete();
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetFullPhoneAutocompleteWithType() {
		String expected;
		WSingleSelect field = new WSingleSelect();
		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.FULL.getValue());
			field.setFullPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetFullPhoneAutocompleteWithNullType() {
		WSingleSelect field = new WSingleSelect();
		field.setFullPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.FULL.getValue(), field.getAutocomplete());
	}

	@Test
	public void testSetAutocompletePhoneTypeFormat() {
		WSingleSelect field = new WSingleSelect();
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
		WSingleSelect field = new WSingleSelect();
		for (Telephone phone : Telephone.values()) {
			field.setAutocomplete(phone, null);
			Assert.assertEquals(phone.getValue(), field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		String expected = Telephone.LOCAL.getValue();
		field.setLocalPhoneAutocomplete();
		Assert.assertEquals(expected, field.getAutocomplete());
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithType() {
		WSingleSelect field = new WSingleSelect();
		String expected;

		for (PhoneFormat phoneType : PhoneFormat.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), Telephone.LOCAL.getValue());
			field.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetLocalPhoneAutocompleteWithNullType() {
		WSingleSelect field = new WSingleSelect();
		field.setLocalPhoneAutocomplete(null);
		Assert.assertEquals(Telephone.LOCAL.getValue(), field.getAutocomplete());
	}

	// with URL autocomplete
	@Test
	public void testSetAutocompleteUrl() {
		WSingleSelect field = new WSingleSelect();
		String expected;

		for (Url url : Url.values()) {
			expected = url.getValue();
			field.setAutocomplete(url);
			Assert.assertEquals(expected, field.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteNullUrl() {
		WSingleSelect field = new WSingleSelect();
		field.setAutocomplete(Url.URL);
		Assert.assertNotNull(field.getAutocomplete());
		field.setAutocomplete((Url) null);
		Assert.assertNull(field.getAutocomplete());
	}

	@Test
	public void testSetUrlAutocomplete() {
		WSingleSelect field = new WSingleSelect();
		field.setUrlAutocomplete();
		Assert.assertEquals(Url.URL.getValue(), field.getAutocomplete());
	}
}
