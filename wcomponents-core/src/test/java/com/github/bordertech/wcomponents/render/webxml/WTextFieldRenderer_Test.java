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
		assertXpathEvaluatesTo(textField.getId(), "//ui:textfield/@id", textField);
		assertXpathNotExists("//ui:textfield/@disabled", textField);
		assertXpathNotExists("//ui:textfield/@hidden", textField);
		assertXpathNotExists("//ui:textfield/@required", textField);
		assertXpathNotExists("//ui:textfield/@readOnly", textField);
		assertXpathNotExists("//ui:textfield/@minLength", textField);
		assertXpathNotExists("//ui:textfield/@maxLength", textField);
		assertXpathNotExists("//ui:textfield/@toolTip", textField);
		assertXpathNotExists("//ui:textfield/@accessibleText", textField);
		assertXpathNotExists("//ui:textfield/@size", textField);
		assertXpathNotExists("//ui:textfield/@buttonId", textField);
		assertXpathNotExists("//ui:textfield/@pattern", textField);
		assertXpathNotExists("//ui:textfield/@list", textField);

		textField.setDisabled(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textfield/@disabled", textField);

		setFlag(textField, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textfield/@hidden", textField);

		textField.setMandatory(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textfield/@required", textField);

		textField.setReadOnly(true);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("true", "//ui:textfield/@readOnly", textField);

		textField.setMinLength(45);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("45", "//ui:textfield/@minLength", textField);

		textField.setMaxLength(50);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("50", "//ui:textfield/@maxLength", textField);

		textField.setToolTip("tooltip");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getToolTip(), "//ui:textfield/@toolTip", textField);

		textField.setAccessibleText("accessible");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getAccessibleText(), "//ui:textfield/@accessibleText",
				textField);

		textField.setColumns(40);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo("40", "//ui:textfield/@size", textField);

		textField.setDefaultSubmitButton(button);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(button.getId(), "//ui:textfield/@buttonId", textField);

		textField.setPattern("");
		assertSchemaMatch(textField);
		assertXpathNotExists("//ui:textfield/@pattern", textField);

		textField.setPattern("test[123]");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getPattern(), "//ui:textfield/@pattern", textField);

		textField.setText("Hello");
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(textField.getText(), "normalize-space(//ui:textfield)", textField);

		textField.setSuggestions(suggestions);
		assertSchemaMatch(textField);
		assertXpathEvaluatesTo(suggestions.getId(), "//ui:textfield/@list", textField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WTextField textField = new WTextField();

		textField.setText(getMaliciousContent());
		assertSafeContent(textField);

		textField.setToolTip(getMaliciousAttribute("ui:textfield"));
		assertSafeContent(textField);

		textField.setAccessibleText(getMaliciousAttribute("ui:textfield"));
		assertSafeContent(textField);
	}
}
