package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WTab;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTabRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTabRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText(""), "", TabMode.SERVER);
		WTab tab = tabSet.getTab(0);

		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(tab) instanceof WTabRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		String tabName = "WTabRenderer_Test.testDoPaint.tabName";
		String tabContent = "WTabRenderer_Test.testDoPaint.tabContent";

		WTabSet tabSet = new WTabSet();
		WTab tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.CLIENT);

		assertXpathExists("//ui:tab", tabSet);
		assertXpathEvaluatesTo(tab.getId(), "//ui:tab/@id", tabSet);
		assertXpathEvaluatesTo(tabName, "normalize-space(//ui:tab/ui:decoratedlabel)", tabSet);
		assertXpathEvaluatesTo(tabContent, "normalize-space(//ui:tab/ui:tabcontent)", tabSet);
		assertXpathEvaluatesTo("true", "//ui:tab/@open", tabSet);
		assertXpathEvaluatesTo("client", "//ui:tab/@mode", tabSet);
		assertXpathNotExists("//ui:tab/@disabled", tabSet);
		assertXpathNotExists("//ui:tab/@accessKey", tabSet);

		tab.setDisabled(true);
		assertXpathEvaluatesTo("true", "//ui:tab/@disabled", tabSet);

		tabSet.remove(tab);
		tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.LAZY);
		assertXpathEvaluatesTo(tab.getId(), "//ui:tab/@id", tabSet);
		assertXpathEvaluatesTo(tabName, "normalize-space(//ui:tab/ui:decoratedlabel)", tabSet);
		assertXpathEvaluatesTo(tabContent, "normalize-space(//ui:tab/ui:tabcontent)", tabSet);
		assertXpathEvaluatesTo("true", "//ui:tab/@open", tabSet);
		assertXpathEvaluatesTo("lazy", "//ui:tab/@mode", tabSet);
		assertXpathNotExists("//ui:tab/@disabled", tabSet);

		tabSet.remove(tab);
		tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.EAGER);
		assertXpathEvaluatesTo("eager", "//ui:tab/@mode", tabSet);

		tabSet.remove(tab);
		tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.DYNAMIC);
		assertXpathEvaluatesTo("dynamic", "//ui:tab/@mode", tabSet);

		tabSet.remove(tab);
		tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.SERVER, 'X');
		assertXpathEvaluatesTo("server", "//ui:tab/@mode", tabSet);
		assertXpathEvaluatesTo(String.valueOf(tab.getAccessKey()), "//ui:tab/@accessKey", tabSet);

		tabSet.remove(tab);
		tab = tabSet.addTab(new WText(tabContent), tabName, TabMode.CLIENT);
		tab.setToolTip("Title");
		assertXpathEvaluatesTo(String.valueOf(tab.getToolTip()), "//ui:tab/@toolTip", tabSet);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTabSet tabSet = new WTabSet();
		WTab tab = tabSet.addTab(new WText("dummy"), getMaliciousContent(), TabMode.CLIENT);

		assertSafeContent(tabSet);

		tab.setToolTip(getMaliciousAttribute("ui:tab"));
		assertSafeContent(tabSet);

		tab.setAccessibleText(getMaliciousAttribute("ui:tab"));
		assertSafeContent(tabSet);
	}
}
