package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WCollapsibleToggle;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WCollapsibleToggleRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCollapsibleToggleRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WCollapsibleToggle component = new WCollapsibleToggle();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WCollapsibleToggleRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		// Client-side
		WCollapsibleToggle toggle = new WCollapsibleToggle(true);
		setActiveContext(createUIContext());

		assertSchemaMatch(toggle);
		assertXpathNotExists("//ui:collapsibletoggle/@roundTrip", toggle);
		assertXpathEvaluatesTo(toggle.getGroupName(), "//ui:collapsibletoggle/@groupName", toggle);

		// Server-side
		toggle = new WCollapsibleToggle(false);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:collapsibletoggle/@roundTrip", toggle);
	}
}
