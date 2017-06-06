package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPanel;
import java.io.IOException;
import junit.framework.Assert;
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
		assertXpathEvaluatesTo("0", "count(//ui:ajaxtrigger)", root);

		// With Targets
		control.addTargets(new AjaxTarget[]{target1, target2, target3});

		setActiveContext(createUIContext());
		assertSchemaMatch(root);
		assertXpathEvaluatesTo(trigger.getId(), "//ui:ajaxtrigger/@triggerId", root);
		assertXpathNotExists("//ui:ajaxtrigger/@loadOnce", root);
		assertXpathNotExists("//ui:ajaxtrigger/@delay", root);
		assertXpathEvaluatesTo("3", "count(//ui:ajaxtrigger/ui:ajaxtargetid)", root);
		assertXpathEvaluatesTo(target1.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[1]/@targetId",
				root);
		assertXpathEvaluatesTo(target2.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[2]/@targetId",
				root);
		assertXpathEvaluatesTo(target3.getId(), "//ui:ajaxtrigger/ui:ajaxtargetid[3]/@targetId",
				root);

		control.setLoadOnce(true);
		assertSchemaMatch(root);
		assertXpathEvaluatesTo("true", "//ui:ajaxtrigger/@loadOnce", root);

		// remove loadOnce then reset it using loadCount
		control.setLoadOnce(false);
		assertSchemaMatch(root);
		assertXpathNotExists("//ui:ajaxtrigger/@loadOnce", root);

		// With Targets and optional attributes
		control.setLoadCount(6); // any number greateer than 0...
		control.setDelay(1000);

		assertSchemaMatch(root);
		assertXpathEvaluatesTo(trigger.getId(), "//ui:ajaxtrigger/@triggerId", root);
		assertXpathEvaluatesTo("true", "//ui:ajaxtrigger/@loadOnce", root);
		assertXpathEvaluatesTo("1000", "//ui:ajaxtrigger/@delay", root);
		assertXpathEvaluatesTo("3", "count(//ui:ajaxtrigger/ui:ajaxtargetid)", root);
	}
}
