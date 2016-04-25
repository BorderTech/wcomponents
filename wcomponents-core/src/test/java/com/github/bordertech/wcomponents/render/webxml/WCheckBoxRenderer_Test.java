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
		assertXpathExists("//ui:checkbox", wcbTest);
		assertXpathEvaluatesTo(wcbTest.getId(), "//ui:checkbox/@id", wcbTest);
		assertXpathNotExists("//ui:checkbox/@tabIndex", wcbTest);

		// Check groupName
		assertXpathNotExists("//ui:checkbox/@groupName", wcbTest);
		wcbTest.setGroup(group);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(group.getId(), "//ui:checkbox/@groupName", wcbTest);

		// Check disabled
		assertXpathNotExists("//ui:checkbox/@disabled", wcbTest);
		wcbTest.setDisabled(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@disabled", wcbTest);

		// Check hidden
		assertXpathNotExists("//ui:checkbox/@hidden", wcbTest);
		setFlag(wcbTest, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@hidden", wcbTest);

		// Check required
		assertXpathNotExists("//ui:checkbox/@required", wcbTest);
		wcbTest.setMandatory(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@required", wcbTest);

		// Check readOnly
		assertXpathNotExists("//ui:checkbox/@readOnly", wcbTest);
		wcbTest.setReadOnly(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@readOnly", wcbTest);

		// Check selected
		assertXpathNotExists("//ui:checkbox/@selected", wcbTest);
		wcbTest.setSelected(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@selected", wcbTest);

		// Check submitOnChange
		assertXpathNotExists("//ui:checkbox/@submitOnChange", wcbTest);
		wcbTest.setSubmitOnChange(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", "//ui:checkbox/@submitOnChange", wcbTest);

		// Check toolTip
		assertXpathNotExists("//ui:checkbox/@toolTip", wcbTest);
		wcbTest.setToolTip("WCheckBox_Test.testRenderedFormat.title");
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(wcbTest.getToolTip(), "//ui:checkbox/@toolTip", wcbTest);

		// Check accessibleText
		assertXpathNotExists("//ui:checkbox/@accessibleText", wcbTest);
		wcbTest.setAccessibleText("WCheckBox_Test.testRenderedFormat.accessibleText");
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(wcbTest.getAccessibleText(), "//ui:checkbox/@accessibleText", wcbTest);

		// Check button id
		assertXpathNotExists("//ui:checkbox/@buttonId", wcbTest);
		wcbTest.setDefaultSubmitButton(button);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo(button.getId(), "//ui:checkbox/@buttonId", wcbTest);
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
