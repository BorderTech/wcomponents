package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPanel;
import java.io.IOException;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WAjaxControlRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WAjaxControlRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WAjaxControl component = new WAjaxControl(new WButton("x"));
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WAjaxControlRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WContainer root = new WContainer();
		WButton trigger = new WButton("x");
		WPanel target1 = new WPanel();
		WPanel target2 = new WPanel();
		WPanel target3 = new WPanel();
		WAjaxControl control = new WAjaxControl(trigger);

		root.add(trigger);
		root.add(target1);
		root.add(target2);
		root.add(target3);
		root.add(control);

		// No Targets
		assertSchemaMatch(root);
		assertXpathEvaluatesTo("0", "count(//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + ")", root);

		// With Targets
		control.addTargets(new AjaxTarget[]{target1, target2, target3});

		setActiveContext(createUIContext());
		assertSchemaMatch(root);
		assertXpathEvaluatesTo(trigger.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@triggerId", root);
		assertXpathNotExists("//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@loadOnce", root);
		assertXpathNotExists("//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@delay", root);
		assertXpathEvaluatesTo("3", "count(//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/html:" + WAjaxControlRenderer.WC_AJAXTARGETID + ")", root);
		assertXpathEvaluatesTo(target1.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/html:" + WAjaxControlRenderer.WC_AJAXTARGETID + "[1]/@targetId",
				root);
		assertXpathEvaluatesTo(target2.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/html:" + WAjaxControlRenderer.WC_AJAXTARGETID + "[2]/@targetId",
				root);
		assertXpathEvaluatesTo(target3.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/html:" + WAjaxControlRenderer.WC_AJAXTARGETID + "[3]/@targetId",
				root);

		control.setLoadOnce(true);
		assertSchemaMatch(root);
		assertXpathEvaluatesTo("true", "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@loadOnce", root);

		// remove loadOnce then reset it using loadCount
		control.setLoadOnce(false);
		assertSchemaMatch(root);
		assertXpathNotExists("//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@loadOnce", root);

		// With Targets and optional attributes
		control.setLoadCount(6); // any number greateer than 0...
		control.setDelay(1000);

		assertSchemaMatch(root);
		assertXpathEvaluatesTo(trigger.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@triggerId", root);
		assertXpathEvaluatesTo("true", "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@loadOnce", root);
		assertXpathEvaluatesTo("1000", "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@delay", root);
		assertXpathEvaluatesTo("3", "count(//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/html:" + WAjaxControlRenderer.WC_AJAXTARGETID + ")", root);
	}
}
