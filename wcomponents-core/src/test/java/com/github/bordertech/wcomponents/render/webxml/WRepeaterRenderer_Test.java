package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WRepeaterRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRepeaterRenderer_Test extends AbstractWebXmlRendererTestCase {

	private static final String[] ROW_DATA = new String[]{
		"WRepeater_Test.row1",
		"",
		"WRepeater_Test.row3"
	};

	@Test
	public void testRendererCorrectlyConfigured() {
		WRepeater component = new WRepeater();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WRepeaterRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WRepeater repeater = new WRepeater();
		repeater.setRepeatedComponent(new WTextField());
		repeater.setBeanList(Arrays.asList(ROW_DATA));

		// Should have 3 inputs
		assertXpathEvaluatesTo("3", "count(//ui:textfield)", repeater);
		assertXpathEvaluatesTo(ROW_DATA[0], "//ui:textfield[1]/text()", repeater);
		assertXpathEvaluatesTo(ROW_DATA[1], "//ui:textfield[2]/text()", repeater);
		assertXpathEvaluatesTo(ROW_DATA[2], "//ui:textfield[3]/text()", repeater);
	}
}
