package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTabGroup;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTabGroupRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTabGroupRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTabGroup tabGroup = new WTabGroup("");
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(tabGroup) instanceof WTabGroupRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String groupName = "WTabGroupRenderer_Test.testDoPaint.groupName";

		WTabGroup tabGroup = new WTabGroup(groupName);
		WComponent wrapped = wrapTabGroup(tabGroup);

		assertXpathExists("//ui:tabGroup", wrapped);
		assertXpathEvaluatesTo(groupName, "normalize-space(//ui:tabGroup/ui:decoratedLabel)",
				wrapped);
		assertXpathEvaluatesTo(tabGroup.getId(), "//ui:tabGroup/@id", wrapped);
		assertXpathNotExists("//ui:tabGroup/@disabled", wrapped);
		assertXpathNotExists("//ui:tabGroup/ui:tab", wrapped);
		assertXpathNotExists("//ui:tabGroup/ui:separator", wrapped);

		tabGroup.setDisabled(true);
		assertXpathEvaluatesTo("true", "//ui:tabGroup/@disabled", wrapped);

		tabGroup.addSeparator();
		assertXpathExists("//ui:tabGroup/ui:separator", wrapped);
		assertSchemaMatch(wrapped);

		tabGroup.addTab(new WText("dummy"), "dummy", TabMode.CLIENT);
		assertXpathExists("//ui:tabGroup/ui:tab", wrapped);
		assertSchemaMatch(wrapped);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTabGroup tabGroup = new WTabGroup(getMaliciousContent());
		tabGroup.addSeparator();
		WComponent wrapped = wrapTabGroup(tabGroup);

		assertSafeContent(wrapped);

		tabGroup.setToolTip(getMaliciousAttribute("ui:tab"));
		assertSafeContent(wrapped);

		tabGroup.setAccessibleText(getMaliciousAttribute("ui:tab"));
		assertSafeContent(wrapped);
	}

	/**
	 * Tabs can not be used stand-alone, so we must test them through a WTabSet.
	 *
	 * @param tabGroup the tab group to wrap
	 *
	 * @return wrapped tab group
	 */
	private WComponent wrapTabGroup(final WTabGroup tabGroup) {
		WTabSet tabSet = new WTabSet();
		tabSet.add(tabGroup);
		return tabSet;
	}
}
