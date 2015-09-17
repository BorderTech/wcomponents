package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTabSetRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTabSetRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTabSet tabSet = new WTabSet();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(tabSet) instanceof WTabSetRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText("1"), "1", TabMode.CLIENT);
		tabSet.addTab(new WText("2"), "3", TabMode.CLIENT);
		tabSet.addTab(new WText("2"), "3", TabMode.CLIENT);

		assertSchemaMatch(tabSet);
		assertXpathExists("//ui:tabset", tabSet);
		assertXpathEvaluatesTo("3", "count(//ui:tabset/ui:tab)", tabSet);
		assertXpathEvaluatesTo(tabSet.getId(), "//ui:tabset/@id", tabSet);
		assertXpathEvaluatesTo(WTabSetRenderer.getTypeAsString(tabSet.getType()),
				"//ui:tabset/@type", tabSet);
		assertXpathNotExists("//ui:tabset/@disabled", tabSet);

		tabSet.setDisabled(true);
		assertXpathEvaluatesTo("true", "//ui:tabset/@disabled", tabSet);
		tabSet.setDisabled(false);
	}

	@Test
	public void testGetTypeAsString() {
		Assert.assertEquals("Incorrect tab set type for TOP", "top",
				WTabSetRenderer.getTypeAsString(WTabSet.TabSetType.TOP));
		Assert.assertEquals("Incorrect tab set type for LEFT", "left",
				WTabSetRenderer.getTypeAsString(WTabSet.TabSetType.LEFT));
		Assert.assertEquals("Incorrect tab set type for RIGHT", "right",
				WTabSetRenderer.getTypeAsString(WTabSet.TabSetType.RIGHT));
		Assert.assertEquals("Incorrect tab set type for ACCORDION", "accordion",
				WTabSetRenderer.getTypeAsString(WTabSet.TabSetType.ACCORDION));
		Assert.assertEquals("Incorrect tab set type for APPLICATION", "application",
				WTabSetRenderer.getTypeAsString(WTabSet.TabSetType.APPLICATION));
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WTabSet tabSet = new WTabSet();
		tabSet.addTab(new WText("1"), "1", TabMode.CLIENT);

		assertXpathNotExists("//ui:tabset/ui:margin", tabSet);

		Margin margin = new Margin(0);
		tabSet.setMargin(margin);
		assertXpathNotExists("//ui:tabset/ui:margin", tabSet);

		margin = new Margin(1);
		tabSet.setMargin(margin);
		assertSchemaMatch(tabSet);
		assertXpathEvaluatesTo("1", "//ui:tabset/ui:margin/@all", tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/ui:margin/@north", tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/ui:margin/@east", tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/ui:margin/@south", tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/ui:margin/@west", tabSet);

		margin = new Margin(1, 2, 3, 4);
		tabSet.setMargin(margin);
		assertSchemaMatch(tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/ui:margin/@all", tabSet);
		assertXpathEvaluatesTo("1", "//ui:tabset/ui:margin/@north", tabSet);
		assertXpathEvaluatesTo("2", "//ui:tabset/ui:margin/@east", tabSet);
		assertXpathEvaluatesTo("3", "//ui:tabset/ui:margin/@south", tabSet);
		assertXpathEvaluatesTo("4", "//ui:tabset/ui:margin/@west", tabSet);
	}

	@Test
	public void testSingleAccordian() throws IOException, SAXException, XpathException {
		WTabSet tabSet = new WTabSet(WTabSet.TabSetType.ACCORDION);
		tabSet.addTab(new WText("1"), "1", TabMode.CLIENT);

		// Default should be single is false (ie not rendered)
		assertSchemaMatch(tabSet);
		assertXpathEvaluatesTo("", "//ui:tabset/@single", tabSet);

		// Default should be single is false (ie not rendered)
		tabSet.setSingle(true);
		assertSchemaMatch(tabSet);
		assertXpathEvaluatesTo("true", "//ui:tabset/@single", tabSet);
	}

}
