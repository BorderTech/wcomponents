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
		assertXpathEvaluatesTo(field.getId(), "//ui:textArea/@id", field);
		assertXpathNotExists("//ui:textArea/@disabled", field);
		assertXpathNotExists("//ui:textArea/@hidden", field);
		assertXpathNotExists("//ui:textArea/@required", field);
		assertXpathNotExists("//ui:textArea/@readOnly", field);
		assertXpathNotExists("//ui:textArea/@minLength", field);
		assertXpathNotExists("//ui:textArea/@maxLength", field);
		assertXpathNotExists("//ui:textArea/@toolTip", field);
		assertXpathNotExists("//ui:textArea/@accessibleText", field);
		assertXpathNotExists("//ui:textArea/@rows", field);
		assertXpathNotExists("//ui:textArea/@cols", field);
		assertXpathNotExists("//ui:textArea/ui:rtf", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textArea/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textArea/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textArea/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:textArea/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:textArea/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:textArea/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:textArea/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:textArea/@accessibleText", field);

		field.setRows(20);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("20", "//ui:textArea/@rows", field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:textArea/@cols", field);

		field.setRichTextArea(false);
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textArea/ui:rtf", field);

		field.setRichTextArea(true);
		assertSchemaMatch(field);
		assertXpathExists("//ui:textArea/ui:rtf", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:textArea/@buttonId", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textArea/@pattern", field);

		// Pattern is not supported on the client for TextArea, and will not be rendered
		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:textArea/@pattern", field);

		field.setText("Hello");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getText(), "normalize-space(//ui:textArea)", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTextArea textArea = new WTextArea();

		textArea.setText(getMaliciousContent());
		assertSafeContent(textArea);

		textArea.setToolTip(getMaliciousAttribute("ui:textArea"));
		assertSafeContent(textArea);

		textArea.setAccessibleText(getMaliciousAttribute("ui:textArea"));
		assertSafeContent(textArea);
	}
}
