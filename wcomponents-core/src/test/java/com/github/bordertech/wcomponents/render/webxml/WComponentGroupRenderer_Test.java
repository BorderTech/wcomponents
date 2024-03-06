package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import org.junit.Assert;
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
		Assert.assertTrue("Incorrect renderer supplied", getWebXmlRenderer(group) instanceof WComponentGroupRenderer);
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
		assertXpathEvaluatesTo("1", String.format("count(//html:%s)", WComponentGroupRenderer.TAG_GROUP), root);
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s)", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo(group.getId(), String.format("//html:%s/@id", WComponentGroupRenderer.TAG_GROUP), root);
		assertXpathEvaluatesTo(actionTarget1.getId(), String.format("//html:%s/html:%s[position()=1]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo(actionTarget2.getId(), String.format("//html:%s/html:%s[position()=2]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
		assertXpathEvaluatesTo(actionTarget3.getId(), String.format("//html:%s/html:%s[position()=3]/@refid", WComponentGroupRenderer.TAG_GROUP, WComponentGroupRenderer.TAG_COMPONENT), root);
	}

}
