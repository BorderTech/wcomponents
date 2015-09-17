package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
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
public class WCheckBoxRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WCheckBox component = new WCheckBox();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WCheckBoxRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WCheckBox wcbTest = new WCheckBox();
		WComponentGroup<WCheckBox> group = new WComponentGroup<>();
		WButton button = new WButton("test");

		WContainer root = new WContainer();
		root.add(wcbTest);
		root.add(group);
		root.add(button);

		setActiveContext(createUIContext());

		assertSchemaMatch(wcbTest);
		assertXpathExists("//ui:checkBox", wcbTest);
		assertXpathEvaluatesTo(wcbTest.getId(), "//ui:checkBox/@id", wcbTest);
		assertXpathNotExists("//ui:checkBox/@tabIndex", wcbTest);

		// Check groupName
		assertXpathNotExists("//ui:checkBox/@groupName", wcbTest);
		wcbTest.setGroup(group);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(group.getId(), "//ui:checkBox/@groupName", wcbTest);

		// Check disabled
		assertXpathNotExists("//ui:checkBox/@disabled", wcbTest);
		wcbTest.setDisabled(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@disabled", wcbTest);

		// Check hidden
		assertXpathNotExists("//ui:checkBox/@hidden", wcbTest);
		setFlag(wcbTest, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@hidden", wcbTest);

		// Check required
		assertXpathNotExists("//ui:checkBox/@required", wcbTest);
		wcbTest.setMandatory(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@required", wcbTest);

		// Check readOnly
		assertXpathNotExists("//ui:checkBox/@readOnly", wcbTest);
		wcbTest.setReadOnly(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@readOnly", wcbTest);

		// Check selected
		assertXpathNotExists("//ui:checkBox/@selected", wcbTest);
		wcbTest.setSelected(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@selected", wcbTest);

		// Check submitOnChange
		assertXpathNotExists("//ui:checkBox/@submitOnChange", wcbTest);
		wcbTest.setSubmitOnChange(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkBox/@submitOnChange", wcbTest);

		// Check toolTip
		assertXpathNotExists("//ui:checkBox/@toolTip", wcbTest);
		wcbTest.setToolTip("WCheckBox_Test.testRenderedFormat.title");
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(wcbTest.getToolTip(), "//ui:checkBox/@toolTip", wcbTest);

		// Check accessibleText
		assertXpathNotExists("//ui:checkBox/@accessibleText", wcbTest);
		wcbTest.setAccessibleText("WCheckBox_Test.testRenderedFormat.accessibleText");
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(wcbTest.getAccessibleText(), "//ui:checkBox/@accessibleText", wcbTest);

		// Check button id
		assertXpathNotExists("//ui:checkBox/@buttonId", wcbTest);
		wcbTest.setDefaultSubmitButton(button);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(button.getId(), "//ui:checkBox/@buttonId", wcbTest);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WCheckBox checkBox = new WCheckBox();

		assertSafeContent(checkBox);

		checkBox.setToolTip(getMaliciousAttribute());
		assertSafeContent(checkBox);

		checkBox.setAccessibleText(getMaliciousAttribute());
		assertSafeContent(checkBox);
	}
}
