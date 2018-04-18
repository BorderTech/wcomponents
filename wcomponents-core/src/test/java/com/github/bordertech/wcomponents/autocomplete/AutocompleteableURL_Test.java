package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.autocomplete.type.Url;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests of default methods of Interface {@link AutocompleteableURL}.
 * @author Mark Reeves
 * @since 1.5.3
 */
public class AutocompleteableURL_Test {

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
	public void testSetUrlAutocomplete() {
		MyAutocompleteable component = new MyAutocompleteable();
		component.setUrlAutocomplete();
		Assert.assertEquals(Url.URL.getValue(), component.getAutocomplete());
	}

	/**
	 * Mock to test default methods.
	 */
	private class MyAutocompleteable implements AutocompleteableURL {
		private String autocomplete;

		/**
		 * Set autocomplete without using interface methods.
		 * @param val the value to set.
		 */
		public void setAutocompleteDirectly(final String val) {
			autocomplete = val;
		}

		@Override
		public void setAutocomplete(final Url value) {
			autocomplete = value == null ? null : value.getValue();
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
