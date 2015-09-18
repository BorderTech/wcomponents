package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.Headers;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRendererTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * HeadLineComponent_Test - unit tests for {@link HeadLineInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class HeadLineInterceptor_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Label text to test for.
	 */
	private static final String LABEL_TEXT = "FormComponent_Test.labelText";

	/**
	 * The headline interceptor being tested.
	 */
	private HeadLineInterceptor headLineComponent;

	/**
	 * The backing WComponent UI.
	 */
	private WLabel content;

	@Before
	public void setUp() {
		headLineComponent = new HeadLineInterceptor();

		content = new WLabel(LABEL_TEXT);
		headLineComponent.setBackingComponent(content);
		setActiveContext(createUIContext());
	}

	@Test
	public void testBasicRenderedFormat() throws Exception {
		assertSchemaMatch(headLineComponent);
		assertXpathNotExists("//script", headLineComponent);
		assertXpathNotExists("//style", headLineComponent);
		assertXpathEvaluatesTo(LABEL_TEXT, "normalize-space(//ui:label)", headLineComponent);
	}

	@Test
	public void testRenderedFormatWithGeneralHeader() throws Exception {
		final String generic1 = "(Generic heading 1)";
		final String generic2 = "(Generic heading 2)";

		content.getHeaders().addHeadLine(Headers.UNTYPED_HEADLINE, generic1);

		content = new WLabelWithHeaders(LABEL_TEXT, Headers.UNTYPED_HEADLINE, generic2);
		headLineComponent.setBackingComponent(content);

		// Script 1 emulates a previous render, and should have been cleared out.
		// Script 2 should be present.
		assertSchemaMatch(headLineComponent);
		assertXpathNotExists("//script", headLineComponent);
		assertXpathNotExists("//style", headLineComponent);
		assertXpathEvaluatesTo(LABEL_TEXT, "normalize-space(//ui:label)", headLineComponent);

		String headings = evaluateXPath(headLineComponent, "/");
		Assert.assertTrue("Headings should contain generic 2", headings.indexOf(generic2) != -1);
		Assert.
				assertFalse("Headings should not contain generic 1",
						headings.indexOf(generic1) != -1);
	}

	@Test
	public void testRenderedFormatWithJavascript() throws Exception {
		final String script = "function blah1() { alert(\"blah1\") }";

		content = new WLabelWithHeaders(LABEL_TEXT, Headers.JAVASCRIPT_HEADLINE, script);
		headLineComponent.setBackingComponent(content);

		assertSchemaMatch(headLineComponent);
		assertXpathEvaluatesTo("1", "count(//html:script)", headLineComponent);
		assertXpathEvaluatesTo("text/javascript", "//html:script/@type", headLineComponent);
		assertXpathNotExists("//style", headLineComponent);
		assertXpathEvaluatesTo(LABEL_TEXT, "normalize-space(//ui:label)", headLineComponent);

		String renderedScript = evaluateXPath(headLineComponent, "//html:script");
		Assert.assertTrue("Javascript should contain given script",
				renderedScript.indexOf(script) != -1);
	}

	@Test
	public void testRenderedFormatWithCss() throws Exception {
		final String aCss = "a { color: blue; dummy1: dummy1; }";

		content = new WLabelWithHeaders(LABEL_TEXT, Headers.CSS_HEADLINE, aCss);
		headLineComponent.setBackingComponent(content);

		// css should be present.
		assertSchemaMatch(headLineComponent);
		assertXpathEvaluatesTo("1", "count(//html:style)", headLineComponent);
		assertXpathEvaluatesTo("text/css", "//html:style/@type", headLineComponent);
		assertXpathEvaluatesTo("screen", "//html:style/@media", headLineComponent);
		assertXpathNotExists("//html:script", headLineComponent);
		assertXpathEvaluatesTo(LABEL_TEXT, "normalize-space(//ui:label)", headLineComponent);

		String style = evaluateXPath(headLineComponent, "//html:style");
		Assert.assertTrue("Style should contain aCss", style.indexOf(aCss) != -1);
	}

	/**
	 * A simple test component which adds headers during rendering.
	 */
	private static final class WLabelWithHeaders extends WLabel {

		/**
		 * The header key.
		 */
		private final String key;

		/**
		 * The header value.
		 */
		private final String value;

		/**
		 * Creates a WLabelWithHeaders.
		 *
		 * @param text the label text.
		 * @param key the key of the header to set.
		 * @param value the value of the header to set.
		 */
		private WLabelWithHeaders(final String text, final String key, final String value) {
			setText(text);
			this.key = key;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);
			getHeaders().addHeadLine(key, value);
		}
	}
}
