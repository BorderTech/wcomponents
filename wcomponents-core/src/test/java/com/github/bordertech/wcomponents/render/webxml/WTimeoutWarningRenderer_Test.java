package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WTimeoutWarning;
import java.io.IOException;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Unit tests for WTimeoutWarningRenderer.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTimeoutWarningRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTimeoutWarning component = new WTimeoutWarning();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WTimeoutWarningRenderer);
	}

	@Test
	public void testDoPaintDefault() throws IOException, SAXException, XpathException {
		WTimeoutWarning warning = new WTimeoutWarning();
		assertSchemaMatch(warning);
	}

	@Test
	public void testDoPaintWithBoth() throws IOException, SAXException, XpathException {
		WTimeoutWarning warning = new WTimeoutWarning(3000, 300);
		assertXpathEvaluatesTo("3000", String.format("//html:%s/@timeout", WTimeoutWarningRenderer.TAG_SESSION), warning);
		assertXpathEvaluatesTo("300", String.format("//html:%s/@warn", WTimeoutWarningRenderer.TAG_SESSION), warning);
	}

	/**
	 * WTimeoutWarning does not participate in the Request phase: it is a one-way instruction to the client. If the
	 * timeout is set to -1 then the timeout warning is not rendered. This complies with the definition of a http
	 * session timeout in which a value of -1 means that the session does not time out.
	 *
	 * @throws IOException an exception
	 * @throws SAXException an exception
	 * @throws XpathException an exception
	 */
	@Test
	public void testDoPaintWithMinusOne() throws IOException, SAXException, XpathException {
		WTimeoutWarning warning = new WTimeoutWarning(3000, 300);
		warning.setTimeoutPeriod(-1);
		assertXpathNotExists(String.format("//html:%s", WTimeoutWarningRenderer.TAG_SESSION), warning);
	}

	/**
	 * If warningPeriod is exactly ZERO the warn attribute is not output and the warning time is determined entirely in
	 * the client layer.
	 *
	 * @throws IOException an exception
	 * @throws SAXException an exception
	 * @throws XpathException an exception
	 */
	@Test
	public void testDoPaintWithZeroWarning() throws IOException, SAXException, XpathException {
		WTimeoutWarning warning = new WTimeoutWarning(3000, 0);
		assertXpathExists(String.format("//html:%s", WTimeoutWarningRenderer.TAG_SESSION), warning);
		assertXpathNotExists(String.format("//html:%s/@warn", WTimeoutWarningRenderer.TAG_SESSION), warning);
	}

}
