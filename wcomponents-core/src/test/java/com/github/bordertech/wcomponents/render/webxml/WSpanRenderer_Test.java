package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WSpan;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSpanRenderer}.
 */
public class WSpanRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSpan component = new WSpan();
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(component) instanceof WSpanRenderer);
	}

	@Test
	public void testBasic() throws IOException, SAXException, XpathException {
		WSpan span = new WSpan("Basic");

		assertXpathExists("//html:span[@id]", span);
		assertXpathEvaluatesTo("Basic", "//html:span", span);

		assertXpathNotExists("//html:span[@class]", span);
		assertXpathNotExists("//html:span[@hidden]", span);
		assertXpathNotExists("//html:span[@aria-hidden]", span);
		assertXpathNotExists("//html:span[@aria-label]", span);
		assertXpathNotExists("//html:span[@type='img']", span);
	}

	@Test
	public void testAllOptions() throws IOException, SAXException, XpathException {
		WSpan span = new WSpan("All");
		span.setHtmlClass("CLASS");
		span.setHidden(true);
		span.setAriaHidden(true);
		span.setAccessibleText("ACCESSIBLE");
		span.setImageRole(true);

		assertXpathExists("//html:span[@id]", span);
		assertXpathEvaluatesTo("All", "//html:span", span);

		assertXpathEvaluatesTo("CLASS", "//html:span/@class", span);
		assertXpathEvaluatesTo("hidden", "//html:span/@hidden", span);
		assertXpathEvaluatesTo("true", "//html:span/@aria-hidden", span);
		assertXpathEvaluatesTo("ACCESSIBLE", "//html:span/@aria-label", span);
		assertXpathEvaluatesTo("img", "//html:span/@role", span);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WSpan span = new WSpan(getMaliciousContent());

		assertSafeContent(span);

		span.setHtmlClass(getMaliciousAttribute("html:span"));
		assertSafeContent(span);

		span.setAccessibleText(getMaliciousAttribute("html:span"));
		assertSafeContent(span);
	}

}
