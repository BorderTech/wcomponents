package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WInternalLink;
import com.github.bordertech.wcomponents.WPanel;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WInternalLinkRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WInternalLinkRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WInternalLink link = new WInternalLink();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(link) instanceof WInternalLinkRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WPanel refer = new WPanel();
		WInternalLink link = new WInternalLink();

		WContainer root = new WContainer();
		root.add(refer);
		root.add(link);

		String linkText = "TEXT TEST";
		String linkTip = "TIP TEST";
		String linkAccessibleText = "ACCESS TEST";

		link.setText(linkText);
		link.setReference(refer);

		assertSchemaMatch(link);
		assertXpathEvaluatesTo(link.getId(), "//ui:link/@id", link);
		assertXpathEvaluatesTo("#" + refer.getId(), "//ui:link/@url", link);
		assertXpathEvaluatesTo(linkText, "//ui:link", link);
		assertXpathNotExists("//ui:link/@type", link);
		assertXpathNotExists("//ui:link/@toolTip", link);
		assertXpathNotExists("//ui:link/@accessibleText", link);

		link.setToolTip(linkTip);
		link.setAccessibleText(linkAccessibleText);

		assertSchemaMatch(link);
		assertXpathEvaluatesTo(linkTip, "//ui:link/@toolTip", link);
		assertXpathEvaluatesTo(linkAccessibleText, "//ui:link/@accessibleText", link);

		link.setText(null);
		Assert.assertEquals("A link with no text should not render any output", "", render(link));
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WInternalLink link = new WInternalLink(getMaliciousContent(), new DefaultWComponent());
		assertSafeContent(link);

		link.setToolTip(getMaliciousAttribute("ui:link"));
		assertSafeContent(link);

		link.setAccessibleText(getMaliciousAttribute("ui:link"));
		assertSafeContent(link);
	}
}
