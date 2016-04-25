package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WTextArea;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTextAreaRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WTextAreaRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTextArea textArea = new WTextArea();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(textArea) instanceof WTextAreaRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WTextArea field = new WTextArea();
		WButton button = new WButton();

		WContainer root = new WContainer();
		root.add(field);
		root.add(button);

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:textarea/@id", field);
		assertXpathNotExists("//ui:textarea/@disabled", field);
		assertXpathNotExists("//ui:textarea/@hidden", field);
		assertXpathNotExists("//ui:textarea/@required", field);
		assertXpathNotExists("//ui:textarea/@readOnly", field);
		assertXpathNotExists("//ui:textarea/@minLength", field);
		assertXpathNotExists("//ui:textarea/@maxLength", field);
		assertXpathNotExists("//ui:textarea/@toolTip", field);
		assertXpathNotExists("//ui:textarea/@accessibleText", field);
		assertXpathNotExists("//ui:textarea/@rows", field);
		assertXpathNotExists("//ui:textarea/@cols", field);
		assertXpathNotExists("//ui:textarea/ui:rtf", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textarea/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textarea/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textarea/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textarea/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:textarea/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:textarea/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:textarea/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:textarea/@accessibleText", field);

		field.setRows(20);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("20", "//ui:textarea/@rows", field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:textarea/@cols", field);

		field.setRichTextArea(false);
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textarea/ui:rtf", field);

		field.setRichTextArea(true);
		assertSchemaMatch(field);
		assertXpathExists("//ui:textarea/ui:rtf", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:textarea/@buttonId", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textarea/@pattern", field);

		// Pattern is not supported on the client for TextArea, and will not be rendered
		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textarea/@pattern", field);

		field.setText("Hello");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getText(), "normalize-space(//ui:textarea)", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTextArea textArea = new WTextArea();

		textArea.setText(getMaliciousContent());
		assertSafeContent(textArea);

		textArea.setToolTip(getMaliciousAttribute("ui:textarea"));
		assertSafeContent(textArea);

		textArea.setAccessibleText(getMaliciousAttribute("ui:textarea"));
		assertSafeContent(textArea);
	}
}
