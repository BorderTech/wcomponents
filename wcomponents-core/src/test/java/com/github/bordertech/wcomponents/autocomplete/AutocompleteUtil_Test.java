package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests of {@link AutocompleteUtil}.
 * @author Mark Reeves
 */
public class AutocompleteUtil_Test {

	@Test
	public void testGetCombinedAutocomplete() {
		String inOne = "foo";
		String inTwo = "bar";
		String expected = "foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne, inTwo));
	}

	@Test
	public void testGetCombinedAutocompleteOneInput() {
		String inOne = "foo";
		String expected = "foo";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne));
	}

	@Test
	public void testGetCombinedAutocompleteTrims() {
		String inOne = " foo";
		String inTwo = "bar ";
		String expected = "foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne, inTwo));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmpty() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(""));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfNullIn() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparators() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete("", ""));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparatorsAndNullIn() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null, ""));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparatorsAndSpace() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null, "", null, " "));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffIn() {
		Assert.assertEquals(AutocompleteUtil.getOff(), AutocompleteUtil.getCombinedAutocomplete(AutocompleteUtil.getOff(), "anything"));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInVarArgs() {
		Assert.assertEquals(AutocompleteUtil.getOff(), AutocompleteUtil.getCombinedAutocomplete("anything", AutocompleteUtil.getOff()));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInCaseInsensitive() {
		Assert.assertEquals(AutocompleteUtil.getOff(), AutocompleteUtil.getCombinedAutocomplete(AutocompleteUtil.getOff().toUpperCase(), "anything"));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInVarArgsCaseInsensitive() {
		Assert.assertEquals(AutocompleteUtil.getOff(), AutocompleteUtil.getCombinedAutocomplete("anything", "oFf"));
	}

	@Test
	public void testGetNamedSection() {
		String in = "foo";
		String expected = "section-foo";

		Assert.assertEquals("Unexpected output", expected, AutocompleteUtil.getNamedSection(in));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedSectionNullName() {
		AutocompleteUtil.getNamedSection(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedSectionEmptyName() {
		AutocompleteUtil.getNamedSection("");
	}

	@Test
	public void testGetCombinedForSection() {
		String sectionName = "foo";
		String otherName = "bar";
		String expected = "section-foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedForSection(sectionName, otherName));
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetCombinedForSectionEmptySection() {
		AutocompleteUtil.getCombinedForSection("", "bar");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetCombinedForSectionNullSection() {
		AutocompleteUtil.getCombinedForSection(null, "bar");
	}

	@Test
	public void testGetCombinedForSectionEmptyOther() {
		String sectionName = "foo";
		String expected = "section-foo";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedForSection(sectionName, ""));
	}

	@Test
	public void testGetCombinedForSectionNullOther() {
		String sectionName = "foo";
		String expected = "section-foo";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedForSection(sectionName, (String) null));
	}

	@Test
	public void testGetCombinedForSectionUndefinedOther() {
		String sectionName = "foo";
		String expected = "section-foo";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedForSection(sectionName));
	}

	@Test
	public void testGetCombinedFullPhone() {
		String expected;
		for (PhoneFormat format : PhoneFormat.values()) {
			expected = format.getValue().concat(" ").concat(Telephone.FULL.getValue());
			Assert.assertEquals(expected, AutocompleteUtil.getCombinedFullPhone(format, null));
			for (Telephone phone : Telephone.values()) {
				expected = format.getValue().concat(" ").concat(phone.getValue());
				Assert.assertEquals(expected, AutocompleteUtil.getCombinedFullPhone(format, phone));
			}
		}
	}

	@Test
	public void testGetCombinedFullPhoneNullFormat() {
		for (Telephone phone : Telephone.values()) {
			Assert.assertEquals(phone.getValue(), AutocompleteUtil.getCombinedFullPhone(null, phone));
		}
	}

	@Test
	public void testGetCombinedFullPhoneNull() {
		Assert.assertNull(AutocompleteUtil.getCombinedFullPhone(null, null));
	}

	@Test
	public void testGetCombinedPhoneSegment() {
		String expected;
		for (PhoneFormat format : PhoneFormat.values()) {
			Assert.assertEquals(format.getValue(), AutocompleteUtil.getCombinedPhoneSegment(format, null));
			for (PhonePart segment : PhonePart.values()) {
				expected = format.getValue().concat(" ").concat(segment.getValue());
				Assert.assertEquals(expected, AutocompleteUtil.getCombinedPhoneSegment(format, segment));
			}
		}
	}

	@Test
	public void testGetCombinedPhoneSegmentNullFormat() {
		for (PhonePart segment : PhonePart.values()) {
			Assert.assertEquals(segment.getValue(), AutocompleteUtil.getCombinedPhoneSegment(null, segment));
		}
	}

	@Test
	public void testGetCombinedPhoneSegmentNull() {
		Assert.assertNull(AutocompleteUtil.getCombinedPhoneSegment(null, null));
	}

	@Test
	public void testGetCombinedAddress() {
		String expected;
		for (AddressType addrType : AddressType.values()) {
			Assert.assertEquals(addrType.getValue(), AutocompleteUtil.getCombinedAddress(addrType, null));
			for (AddressPart segment : AddressPart.values()) {
				expected = addrType.getValue().concat(" ").concat(segment.getValue());
				Assert.assertEquals(expected, AutocompleteUtil.getCombinedAddress(addrType, segment));
			}
		}
	}

	@Test
	public void testGetCombinedAddressNullType() {
		for (AddressPart segment : AddressPart.values()) {
			Assert.assertEquals(segment.getValue(), AutocompleteUtil.getCombinedAddress(null, segment));
		}
	}

	@Test
	public void testGetCombinedAddressNull() {
		Assert.assertNull(AutocompleteUtil.getCombinedAddress(null, null));
	}

	@Test
	public void testGetCombinedForAddSection() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocomplete("foo");
		Assert.assertEquals("section-bar foo", AutocompleteUtil.getCombinedForAddSection("bar", component));
	}

	@Test
	public void testGetCombinedForAddSectionNoACValue() {
		MyAutocompleteable component = new MyAutocompleteable();
		Assert.assertEquals("section-bar", AutocompleteUtil.getCombinedForAddSection("bar", component));
	}

	@Test
	public void testGetCombinedForAddSectionNoComponent() {
		Assert.assertEquals("section-bar", AutocompleteUtil.getCombinedForAddSection("bar", null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCombinedForAddSectionNullSection() {
		AutocompleteUtil.getCombinedForAddSection(null, new MyAutocompleteable());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetCombinedForAddSectionEmptySection() {
		AutocompleteUtil.getCombinedForAddSection("", new MyAutocompleteable());
	}

	@Test (expected = SystemException.class)
	public void testGetCombinedForAddSectionWhenOff() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocompleteOff();
		AutocompleteUtil.getCombinedForAddSection("foo", component);
	}

	/**
	 * Mock for testing.
	 */
	private class MyAutocompleteable implements Autocompleteable {
		private String autocomplete;

		@Override
		public String getAutocomplete() {
			return autocomplete;
		}

		/**
		 * Set the autocomplete value.
		 * @param val the value to set
		 */
		public void setAutocomplete(final String val) {
			autocomplete = val;
		}

		@Override
		public void setAutocompleteOff() {
			autocomplete = AutocompleteUtil.getOff();
		}

		@Override
		public void addAutocompleteSection(final String sectionName) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void clearAutocomplete() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getInternalId() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getIdName() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setIdName(final String idName) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getName() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getId() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void serviceRequest(final Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void invokeLater(final Runnable runnable) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void handleRequest(final Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void forward(final String url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void preparePaint(final Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void paint(final RenderContext renderContext) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void validate(final List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void showErrorIndicators(final List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void showWarningIndicators(final List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setLocked(final boolean lock) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isLocked() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isInitialised() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setInitialised(final boolean flag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isValidate() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setValidate(final boolean flag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isVisible() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setVisible(final boolean visible) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isHidden() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean hasTabIndex() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getTabIndex() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public WLabel getLabel() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setFocussed() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void tidyUpUIContextForTree() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isDefaultState() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Container getParent() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getTag() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setTag(final String tag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Environment getEnvironment() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setEnvironment(final Environment environment) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Headers getHeaders() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getBaseUrl() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setAttribute(final String key, final Serializable value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Serializable getAttribute(final String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Serializable removeAttribute(final String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setToolTip(final String text, final Serializable... args) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getToolTip() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setAccessibleText(final String text, final Serializable... args) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getAccessibleText() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setTrackingEnabled(final boolean track) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isTrackingEnabled() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isTracking() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setHtmlClass(final String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setHtmlClass(final HtmlClassProperties className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addHtmlClass(final String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addHtmlClass(final HtmlClassProperties className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getHtmlClass() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Set getHtmlClasses() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void removeHtmlClass(final String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void removeHtmlClass(final HtmlClassProperties className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
