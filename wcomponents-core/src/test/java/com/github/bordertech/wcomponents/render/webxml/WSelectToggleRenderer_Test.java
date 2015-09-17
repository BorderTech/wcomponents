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

		assertXpathNotExists("//ui:selectToggle/@roundTrip", toggle);
		assertXpathEvaluatesTo(toggle.getId(), "//ui:selectToggle/@id", toggle);
		assertXpathEvaluatesTo(toggle.getTarget().getId(), "//ui:selectToggle/@target", toggle);
		assertXpathEvaluatesTo("control", "//ui:selectToggle/@renderAs", toggle);
		assertXpathEvaluatesTo("none", "//ui:selectToggle/@selected", toggle);

		// Test Server-side
		toggle.setClientSide(false);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:selectToggle/@roundTrip", toggle);

		// Test when selected
		toggle.setState(WSelectToggle.State.ALL);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("all", "//ui:selectToggle/@selected", toggle);

		// Test when partially selected
		toggle.setState(WSelectToggle.State.SOME);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("some", "//ui:selectToggle/@selected", toggle);
	}
}
