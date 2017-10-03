package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WToggleButton;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WCheckBoxRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WToggleButtonRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WToggleButton component = new WToggleButton();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WToggleButtonRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WToggleButton toggle = new WToggleButton();
		WComponentGroup<WToggleButton> group = new WComponentGroup<>();
		WButton button = new WButton("test");

		WContainer root = new WContainer();
		root.add(toggle);
		root.add(group);
		root.add(button);

		setActiveContext(createUIContext());

		assertSchemaMatch(toggle);
		assertXpathExists("//ui:togglebutton", toggle);
		assertXpathEvaluatesTo(toggle.getId(), "//ui:togglebutton/@id", toggle);

		// Check disabled
		assertXpathNotExists("//ui:togglebutton/@disabled", toggle);
		toggle.setDisabled(true);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:togglebutton/@disabled", toggle);

		// Check hidden
		assertXpathNotExists("//ui:togglebutton/@hidden", toggle);
		setFlag(toggle, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:togglebutton/@hidden", toggle);

		// Check selected
		assertXpathNotExists("//ui:togglebutton/@selected", toggle);
		toggle.setSelected(true);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:togglebutton/@selected", toggle);

		// Check toolTip
		assertXpathNotExists("//ui:togglebutton/@toolTip", toggle);
		toggle.setToolTip("WCheckBox_Test.testRenderedFormat.title");
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo(toggle.getToolTip(), "//ui:togglebutton/@toolTip", toggle);

		// Check accessibleText
		assertXpathNotExists("//ui:togglebutton/@accessibleText", toggle);
		toggle.setAccessibleText("WCheckBox_Test.testRenderedFormat.accessibleText");
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo(toggle.getAccessibleText(), "//ui:togglebutton/@accessibleText", toggle);

	}

	@Test
	public void textReadOnly() throws IOException, SAXException, XpathException {
		WToggleButton toggle = new WToggleButton();

		// Check readOnly
		assertXpathNotExists("//ui:togglebutton/@readOnly", toggle);
		toggle.setReadOnly(true);
		assertSchemaMatch(toggle);
		assertXpathEvaluatesTo("true", "//ui:togglebutton/@readOnly", toggle);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WToggleButton checkBox = new WToggleButton();

		assertSafeContent(checkBox);

		checkBox.setToolTip(getMaliciousAttribute());
		assertSafeContent(checkBox);

		checkBox.setAccessibleText(getMaliciousAttribute());
		assertSafeContent(checkBox);
	}
}
