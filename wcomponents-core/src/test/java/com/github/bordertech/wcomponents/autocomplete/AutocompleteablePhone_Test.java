package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test of the {@link Autocompleteable} Interface
 * @author Mark Reeves
 */
public class AutocompleteablePhone_Test {

	private static final String TEST_SECTION_NAME = "foo";

	/**
	 * Typing helper
	 * @return the value "tel"
	 */
	private String getDefaultTel() {
		return AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.FULL.getValue();
	}

	/**
	 * Meta test to improve confidence in other tests.
	 */
	@Test
	public void testGetValue() {
		String testString = "foo";

		MyAutocompleteable component = new MyAutocompleteable();
		// ensure the component's autocompete is set to a specific thing outside the
		// interface setters.
		component.setAutocompleteDirectly(testString);
		Assert.assertEquals(testString, component.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete_withTypeAndName() {
		MyAutocompleteable component = new MyAutocompleteable();
		String expected; // = AutocompleteUtil.getCombinedForSection(TEST_SECTION_NAME, args)

		for (AutocompleteUtil.TELEPHONE_TYPE phoneType : AutocompleteUtil.TELEPHONE_TYPE.values()) {
			expected = AutocompleteUtil.getCombinedForSection(TEST_SECTION_NAME, phoneType.getValue(), getDefaultTel());
			component.setAutocomplete(phoneType, TEST_SECTION_NAME);
			Assert.assertEquals(expected, component.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetAutocomplete_withTypeAndNameNullEmpty() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocomplete(null, "");
		Assert.assertEquals(getDefaultTel(), component.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete_withType() {
		MyAutocompleteable component = new MyAutocompleteable();
		String expected;

		for (AutocompleteUtil.TELEPHONE_TYPE phoneType : AutocompleteUtil.TELEPHONE_TYPE.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(), getDefaultTel());
			component.setAutocomplete(phoneType);
			Assert.assertEquals(expected, component.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetAutocomplete_withNullType() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocomplete(null);
		Assert.assertEquals(getDefaultTel(), component.getAutocomplete());
	}

	@Test
	public void testDefaultSetAutocomplete() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocomplete();
		Assert.assertEquals(getDefaultTel(), component.getAutocomplete());
	}

	@Test
	public void testDefaultSetLocalPhoneAutocomplete_withType() {
		MyAutocompleteable component = new MyAutocompleteable();
		String expected;

		for (AutocompleteUtil.TELEPHONE_TYPE phoneType : AutocompleteUtil.TELEPHONE_TYPE.values()) {
			expected = AutocompleteUtil.getCombinedAutocomplete(phoneType.getValue(),
					AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.LOCAL.getValue());
			component.setLocalPhoneAutocomplete(phoneType);
			Assert.assertEquals(expected, component.getAutocomplete());
		}
	}

	@Test
	public void testDefaultSetLocalPhoneAutocomplete_withNullType() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setLocalPhoneAutocomplete(null);
		Assert.assertEquals(AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.LOCAL.getValue(), component.getAutocomplete());
	}

	@Test
	public void testDefaultSetLocalPhoneAutocomplete() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setLocalPhoneAutocomplete();
		Assert.assertEquals(AutocompleteUtil.TELEPHONE_AUTOCOMPLETE.LOCAL.getValue(), component.getAutocomplete());
	}


	/**
	 * Mock class to test the default methods.
	 */
	private class MyAutocompleteable implements AutocompleteablePhone {
		private String autocomplete;

		/**
		 * See {@link com.github.bordertech.wcomponents.WPhoneNumberField#setAutocomplete(
		 *    com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil.TELEPHONE_TYPE,
		 *    com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil.TELEPHONE_AUTOCOMPLETE,
		 *    java.lang.String) for a real implementation.
		*/
		@Override
		public void setAutocomplete(AutocompleteUtil.TELEPHONE_TYPE phoneType, AutocompleteUtil.TELEPHONE_AUTOCOMPLETE phone, String sectionName) {
			if (phoneType == null && phone == null && Util.empty(sectionName)) {
				autocomplete = null;
				return;
			}

			String typeString = phoneType == null ? null : phoneType.getValue();
			String phoneFormatString = phone == null ? null : phone.getValue();

			if (Util.empty(sectionName)) {
				autocomplete = AutocompleteUtil.getCombinedAutocomplete(typeString, phoneFormatString);
			} else {
				autocomplete = AutocompleteUtil.getCombinedForSection(sectionName, typeString, phoneFormatString);
			}
		}

		public void setAutocompleteDirectly(final String val) {
			autocomplete = val;
		}

		@Override
		public String getAutocomplete() {
			return autocomplete;
		}

		@Override
		public void setAutocompleteOff() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addAutocompleteSection(String sectionName) {
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
		public void setIdName(String idName) {
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
		public void serviceRequest(Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void invokeLater(Runnable runnable) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void handleRequest(Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void forward(String url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void preparePaint(Request request) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void paint(RenderContext renderContext) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void validate(List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void showErrorIndicators(List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void showWarningIndicators(List<Diagnostic> diags) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setLocked(boolean lock) {
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
		public void setInitialised(boolean flag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isValidate() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setValidate(boolean flag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isVisible() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setVisible(boolean visible) {
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
		public void setTag(String tag) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Environment getEnvironment() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setEnvironment(Environment environment) {
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
		public void setAttribute(String key, Serializable value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Serializable getAttribute(String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Serializable removeAttribute(String key) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setToolTip(String text, Serializable... args) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getToolTip() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setAccessibleText(String text, Serializable... args) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getAccessibleText() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setTrackingEnabled(boolean track) {
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
		public void setHtmlClass(String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setHtmlClass(HtmlClassProperties className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addHtmlClass(String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addHtmlClass(HtmlClassProperties className) {
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
		public void removeHtmlClass(String className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void removeHtmlClass(HtmlClassProperties className) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
}
