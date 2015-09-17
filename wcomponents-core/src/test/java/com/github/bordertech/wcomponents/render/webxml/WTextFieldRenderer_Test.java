package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.WTextField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WTextFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WTextFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WTextField textField = new WTextField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(textField) instanceof WTextFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WTextField textField = new WTextField();
		WButton button = new WButton();
		WSuggestions suggestions = new WSuggestions();

		WContainer root = new WContainer();
		root.add(textField);
		root.add(button);
		root.add(suggestions);

		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getId(), "//ui:textField/@id", textField);
		assertXpathNotExists("//ui:textField/@disabled", textField);
		assertXpathNotExists("//ui:textField/@hidden", textField);
		assertXpathNotExists("//ui:textField/@required", textField);
		assertXpathNotExists("//ui:textField/@readOnly", textField);
		assertXpathNotExists("//ui:textField/@minLength", textField);
		assertXpathNotExists("//ui:textField/@maxLength", textField);
		assertXpathNotExists("//ui:textField/@toolTip", textField);
		assertXpathNotExists("//ui:textField/@accessibleText", textField);
		assertXpathNotExists("//ui:textField/@size", textField);
		assertXpathNotExists("//ui:textField/@buttonId", textField);
		assertXpathNotExists("//ui:textField/@pattern", textField);
		assertXpathNotExists("//ui:textField/@list", textField);

		textField.setDisabled(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textField/@disabled", textField);

		setFlag(textField, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textField/@hidden", textField);

		textField.setMandatory(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textField/@required", textField);

		textField.setReadOnly(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textField/@readOnly", textField);

		textField.setMinLength(45);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("45", "//ui:textField/@minLength", textField);

		textField.setMaxLength(50);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("50", "//ui:textField/@maxLength", textField);

		textField.setToolTip("tooltip");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getToolTip(), "//ui:textField/@toolTip", textField);

		textField.setAccessibleText("accessible");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getAccessibleText(), "//ui:textField/@accessibleText",
				textField);

		textField.setColumns(40);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("40", "//ui:textField/@size", textField);

		textField.setDefaultSubmitButton(button);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(button.getId(), "//ui:textField/@buttonId", textField);

		textField.setPattern("");
		assertSchemaMatch(textField);
		assertXpathNotExists("//ui:textField/@pattern", textField);

		textField.setPattern("test[123]");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getPattern(), "//ui:textField/@pattern", textField);

		textField.setText("Hello");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getText(), "normalize-space(//ui:textField)", textField);

		textField.setSuggestions(suggestions);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(suggestions.getId(), "//ui:textField/@list", textField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTextField textField = new WTextField();

		textField.setText(getMaliciousContent());
		assertSafeContent(textField);

		textField.setToolTip(getMaliciousAttribute("ui:textField"));
		assertSafeContent(textField);

		textField.setAccessibleText(getMaliciousAttribute("ui:textField"));
		assertSafeContent(textField);
	}
}
