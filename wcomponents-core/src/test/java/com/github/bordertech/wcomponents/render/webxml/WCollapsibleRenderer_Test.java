package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WCollapsibleRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsibleRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * The collapsible heading to use when testing.
	 */
	private static final String COLLAPSIBLE_HEADING = "WCollapsibleRenderer_Test.collapsibleHeading";

	/**
	 * The collapsible content to use when testing.
	 */
	private static final String COLLAPSIBLE_CONTENT = "WCollapsibleRenderer_Test.collapsibleContent";

	@Test
	public void testRendererCorrectlyConfigured() {
		WCollapsible collapsible = new WCollapsible(new WText(), "");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(collapsible) instanceof WCollapsibleRenderer);
	}

	@Test
	public void testDoRenderServerSideCollapse() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.SERVER);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("server", "//ui:collapsible/@mode", collapsible);
		assertRenderContentCorrectly(collapsible, false, true);
	}

	@Test
	public void testDoRenderClientSideCollapse() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.CLIENT);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("client", "//ui:collapsible/@mode", collapsible);
		assertRenderContentCorrectly(collapsible, true, true);
	}

	@Test
	public void testDoRenderLazyCollapse() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.LAZY);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("lazy", "//ui:collapsible/@mode", collapsible);
		assertRenderContentCorrectly(collapsible, false, true);
	}

	@Test
	public void testDoRenderDynamicCollapse() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.DYNAMIC);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("dynamic", "//ui:collapsible/@mode", collapsible);
		assertRenderContentCorrectly(collapsible, false, true);
	}

	@Test
	public void testDoRenderEagerCollapse() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.EAGER);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("eager", "//ui:collapsible/@mode", collapsible);
		assertRenderContentCorrectly(collapsible, false, false);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.EAGER);
		assertXpathNotExists("//ui:collapsible/ui:margin", collapsible);

		Margin margin = new Margin(0);
		collapsible.setMargin(margin);
		assertXpathNotExists("//ui:collapsible/ui:margin", collapsible);

		margin = new Margin(1);
		collapsible.setMargin(margin);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("1", "//ui:collapsible/ui:margin/@all", collapsible);
		assertXpathEvaluatesTo("", "//ui:collapsible/ui:margin/@north", collapsible);
		assertXpathEvaluatesTo("", "//ui:collapsible/ui:margin/@east", collapsible);
		assertXpathEvaluatesTo("", "//ui:collapsible/ui:margin/@south", collapsible);
		assertXpathEvaluatesTo("", "//ui:collapsible/ui:margin/@west", collapsible);

		margin = new Margin(1, 2, 3, 4);
		collapsible.setMargin(margin);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("", "//ui:collapsible/ui:margin/@all", collapsible);
		assertXpathEvaluatesTo("1", "//ui:collapsible/ui:margin/@north", collapsible);
		assertXpathEvaluatesTo("2", "//ui:collapsible/ui:margin/@east", collapsible);
		assertXpathEvaluatesTo("3", "//ui:collapsible/ui:margin/@south", collapsible);
		assertXpathEvaluatesTo("4", "//ui:collapsible/ui:margin/@west", collapsible);
	}

	@Test
	public void testRenderedWithHeadingLevel() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText(COLLAPSIBLE_CONTENT),
				COLLAPSIBLE_HEADING, WCollapsible.CollapsibleMode.EAGER);
		assertSchemaMatch(collapsible);
		assertXpathNotExists("//ui:collapsible/@level", collapsible);

		// Set level
		collapsible.setHeadingLevel(HeadingLevel.H1);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("1", "//ui:collapsible/@level", collapsible);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WCollapsible collapsible = new WCollapsible(new WText("dummy"), getMaliciousContent(),
				WCollapsible.CollapsibleMode.CLIENT);
		assertSafeContent(collapsible);
	}

	/**
	 * Asserts that the collapsible renders its content correctly.
	 *
	 * @param collapsible the collapsible to render.
	 * @param shouldRenderContentWhenClosed true if the content should render when the collapsible is closed.
	 * @param shouldRenderContentWhenOpen true if the content should render when the collapsible is open.
	 *
	 * @throws IOException an IO exception
	 * @throws SAXException a SAX exception
	 * @throws XpathException an Xpath exception
	 */
	private void assertRenderContentCorrectly(final WCollapsible collapsible,
			final boolean shouldRenderContentWhenClosed, final boolean shouldRenderContentWhenOpen)
			throws IOException, SAXException, XpathException {
		collapsible.setCollapsed(true);
		assertSchemaMatch(collapsible);
		assertXpathEvaluatesTo("true", "//ui:collapsible/@collapsed", collapsible);
		assertXpathEvaluatesTo(COLLAPSIBLE_HEADING,
				"normalize-space(//ui:collapsible/ui:decoratedlabel/ui:labelbody)", collapsible);

		if (shouldRenderContentWhenClosed) {
			assertXpathEvaluatesTo(COLLAPSIBLE_CONTENT,
					"normalize-space(//ui:collapsible/ui:content)", collapsible);
		} else {
			assertXpathEvaluatesTo("", "normalize-space(//ui:collapsible/ui:content)", collapsible);
		}

		collapsible.setCollapsed(false);
		assertSchemaMatch(collapsible);
		assertXpathNotExists("//ui:collapsible/@collapsed", collapsible);
		assertXpathEvaluatesTo(COLLAPSIBLE_HEADING,
				"normalize-space(//ui:collapsible/ui:decoratedlabel/ui:labelbody)", collapsible);

		if (shouldRenderContentWhenOpen) {
			assertXpathEvaluatesTo(COLLAPSIBLE_CONTENT,
					"normalize-space(//ui:collapsible/ui:content)", collapsible);
		} else {
			assertXpathEvaluatesTo("", "normalize-space(//ui:collapsible/ui:content)", collapsible);
		}
	}
}
