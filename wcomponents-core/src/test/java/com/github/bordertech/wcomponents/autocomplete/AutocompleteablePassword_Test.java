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
 * JUnit tests of INterface {@link AutocompleteablePassword}.
 * @author Mark Reeves
 */
public class AutocompleteablePassword_Test {

	/**
	 * Meta test to improve confidence in other tests.
	 */
	@Test
	public void testGetAutocomplete() {
		String testString = "foo";
		MyAutocompleteable component = new MyAutocompleteable();
		// ensure the component's autocompete is set to a specific thing outside the interface setters.
		component.setAutocompleteDirectly(testString);
		Assert.assertEquals(testString, component.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithType() {
		MyAutocompleteable component = new MyAutocompleteable();
		String expected;
		for (AutocompleteUtil.PASSWORD_AUTOCOMPLETE pword : AutocompleteUtil.PASSWORD_AUTOCOMPLETE.values()) {
			expected = pword.getValue();
			component.setAutocomplete(pword);
			Assert.assertEquals(expected, component.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullType() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocomplete(null);
		Assert.assertNull(component.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithTypeAndSection() {
		MyAutocompleteable component = new MyAutocompleteable();
		String sectionName = "foo";
		String expected;
		for (AutocompleteUtil.PASSWORD_AUTOCOMPLETE pword : AutocompleteUtil.PASSWORD_AUTOCOMPLETE.values()) {
			expected = AutocompleteUtil.getCombinedForSection(sectionName, pword.getValue());
			component.setAutocomplete(pword, sectionName);
			Assert.assertEquals(expected, component.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithTypeAndEmptySection() {
		MyAutocompleteable component = new MyAutocompleteable();
		String sectionName = "";
		for (AutocompleteUtil.PASSWORD_AUTOCOMPLETE pword : AutocompleteUtil.PASSWORD_AUTOCOMPLETE.values()) {
			component.setAutocomplete(pword, sectionName);
			Assert.assertEquals(pword.getValue(), component.getAutocomplete());
		}
	}

	@Test
	public void testSetAutocompleteWithNullTypeAndEmptySection() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setAutocompleteDirectly("some value");
		Assert.assertNotNull(component.getAutocomplete());
		component.setAutocomplete(null, "");
		Assert.assertNull(component.getAutocomplete());
	}

	@Test
	public void testSetAutocompleteWithNullTypeAndSection() {
		MyAutocompleteable component = new MyAutocompleteable();
		String sectionName = "foo";
		String expected = AutocompleteUtil.getNamedSection(sectionName);
		component.setAutocomplete(null, sectionName);
		Assert.assertEquals(expected, component.getAutocomplete());
	}

	/**
	 * Class used to test only the default methods in the interface.
	 */
	private class MyAutocompleteable implements AutocompleteablePassword {

		private String autocomplete;

		@Override
		public void setAutocomplete(final AutocompleteUtil.PASSWORD_AUTOCOMPLETE passwordType, final String sectionName) {
			if (passwordType == null && Util.empty(sectionName)) {
				autocomplete = null;
				return;
			}
			String typeVal = passwordType == null ? null : passwordType.getValue();
			if (Util.empty(sectionName)) {
				autocomplete = typeVal;
			} else {
				autocomplete = AutocompleteUtil.getCombinedForSection(sectionName, typeVal);
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
		public void paint(RenderContext renderContext) {
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
		public void showWarningIndicators(List<Diagnostic> diags) {
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
		public void setValidate(boolean flag) {
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
