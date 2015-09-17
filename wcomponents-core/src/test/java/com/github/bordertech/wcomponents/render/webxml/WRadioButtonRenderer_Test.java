package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WRadioButtonRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRadioButtonRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton component = group.addRadioButton(1);
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WRadioButtonRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton button = group.addRadioButton(1);

		button.setVisible(true);
		assertSchemaMatch(button);
		assertXpathExists("//ui:radioButton", button);
		assertXpathEvaluatesTo(button.getId(), "//ui:radioButton/@id", button);
		assertXpathEvaluatesTo(button.getGroupName(), "//ui:radioButton/@groupName", button);
		assertXpathEvaluatesTo(button.getValue(), "//ui:radioButton/@value", button);
		assertXpathNotExists("//ui:radioButton/@submitOnChange", button);
		assertXpathNotExists("//ui:radioButton/@tabIndex", button);

		// Check selected
		assertXpathNotExists("//ui:radioButton/@selected", button);
		button.setSelected(true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@selected", button);

		// Check disabled
		assertXpathNotExists("//ui:radioButton/@disabled", button);
		button.setDisabled(true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@disabled", button);

		// Check hidden
		assertXpathNotExists("//ui:radioButton/@hidden", button);
		setFlag(button, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@hidden", button);

		// Check required
		assertXpathNotExists("//ui:radioButton/@required", button);
		group.setMandatory(true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@required", button);

		// Check readOnly
		assertXpathNotExists("//ui:radioButton/@readOnly", button);
		button.setReadOnly(true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@readOnly", button);

		// Check toolTip
		String toolTip = "WRadioButton_Test.testRenderedFormat.toolTip";
		button.setToolTip(toolTip);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo(toolTip, "//ui:radioButton/@toolTip", button);
		button.setToolTip(null);
		assertSchemaMatch(button);
		assertXpathNotExists("//ui:radioButton/@toolTip", button);

		// Check submitOnChange
		group = new RadioButtonGroup();
		button = group.addRadioButton(1);

		group.setSubmitOnChange(true);
		assertSchemaMatch(button);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@submitOnChange", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton button = group.addRadioButton(1);

		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute());
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute());
		assertSafeContent(button);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		WPanel root = new WPanel();

		RadioButtonGroup group = new RadioButtonGroup();
		root.add(group);

		WRadioButton button1 = group.addRadioButton(null);
		WRadioButton button2 = group.addRadioButton("");
		WRadioButton button3 = group.addRadioButton("A");
		root.add(button1);
		root.add(button2);
		root.add(button3);

		assertSchemaMatch(root);

		assertXpathEvaluatesTo("true", "//ui:radioButton/@isNull", button1);
		assertXpathEvaluatesTo("true", "//ui:radioButton/@isNull", button2);
		assertXpathEvaluatesTo("", "//ui:radioButton/@isNull", button3);
	}

}
