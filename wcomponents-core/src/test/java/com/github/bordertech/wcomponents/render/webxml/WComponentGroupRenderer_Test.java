package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WComponentGroupRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WComponentGroupRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WComponentGroup<WComponent> group = new WComponentGroup<>();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(group) instanceof WComponentGroupRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		// Setup Group
		WComponent actionTarget1 = new WTextField();
		WComponent actionTarget2 = new WTextField();
		WComponent actionTarget3 = new WTextField();
		WComponentGroup<WComponent> group = new WComponentGroup<>();
		group.addToGroup(actionTarget1);
		group.addToGroup(actionTarget2);
		group.addToGroup(actionTarget3);

		WContainer root = new WContainer();
		root.add(actionTarget1);
		root.add(actionTarget2);
		root.add(actionTarget3);
		root.add(group);

		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(root);
		// Check group
		assertXpathEvaluatesTo("1", "count(//ui:componentGroup)", root);
		assertXpathEvaluatesTo("3", "count(//ui:componentGroup/ui:component)", root);
		assertXpathEvaluatesTo(group.getId(), "//ui:componentGroup/@id", root);
		assertXpathEvaluatesTo(actionTarget1.getId(),
				"//ui:componentGroup/ui:component[position()=1]/@id", root);
		assertXpathEvaluatesTo(actionTarget2.getId(),
				"//ui:componentGroup/ui:component[position()=2]/@id", root);
		assertXpathEvaluatesTo(actionTarget3.getId(),
				"//ui:componentGroup/ui:component[position()=3]/@id", root);
	}

}
