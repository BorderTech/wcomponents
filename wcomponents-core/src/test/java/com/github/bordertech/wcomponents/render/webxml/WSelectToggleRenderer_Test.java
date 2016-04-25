package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WSelectToggle;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSelectToggleRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSelectToggleRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSelectToggle component = new WSelectToggle();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WSelectToggleRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		// Client-side
		WCheckBox target = new WCheckBox();

		WSelectToggle toggle = new WSelectToggle(true);
		toggle.setTarget(target);

		WContainer root = new WContainer();
		root.add(toggle);
		root.add(target);

		assertSchemaMatch(toggle);

		assertXpathNotExists("//ui:selecttoggle/@roundTrip", toggle);
		assertXpathEvaluatesTo(toggle.getId(), "//ui:selecttoggle/@id", toggle);
		assertXpathEvaluatesTo(toggle.getTarget().getId(), "//ui:selecttoggle/@target", toggle);
		assertXpathEvaluatesTo("control", "//ui:selecttoggle/@renderAs", toggle);
		assertXpathEvaluatesTo("none", "//ui:selecttoggle/@selected", toggle);

		// Test Server-side
		toggle.setClientSide(false);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:selecttoggle/@roundTrip", toggle);

		// Test when selected
		toggle.setState(WSelectToggle.State.ALL);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("all", "//ui:selecttoggle/@selected", toggle);

		// Test when partially selected
		toggle.setState(WSelectToggle.State.SOME);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("some", "//ui:selecttoggle/@selected", toggle);
	}
}
