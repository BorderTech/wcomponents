package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WSuggestions;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WPhoneNumberFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WPhoneNumberFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WPhoneNumberField textField = new WPhoneNumberField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(textField) instanceof WPhoneNumberFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WPhoneNumberField field = new WPhoneNumberField();
		WButton button = new WButton();
		WSuggestions suggestions = new WSuggestions();

		WContainer root = new WContainer();
		root.add(field);
		root.add(button);
		root.add(suggestions);

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:phoneNumberField/@id", field);
		assertXpathNotExists("//ui:phoneNumberField/@disabled", field);
		assertXpathNotExists("//ui:phoneNumberField/@hidden", field);
		assertXpathNotExists("//ui:phoneNumberField/@required", field);
		assertXpathNotExists("//ui:phoneNumberField/@readOnly", field);
		assertXpathNotExists("//ui:phoneNumberField/@minLength", field);
		assertXpathNotExists("//ui:phoneNumberField/@maxLength", field);
		assertXpathNotExists("//ui:phoneNumberField/@toolTip", field);
		assertXpathNotExists("//ui:phoneNumberField/@accessibleText", field);
		assertXpathNotExists("//ui:phoneNumberField/@size", field);
		assertXpathNotExists("//ui:phoneNumberField/@buttonId", field);
		assertXpathNotExists("//ui:phoneNumberField/@pattern", field);
		assertXpathNotExists("//ui:phoneNumberField/@list", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phoneNumberField/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phoneNumberField/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phoneNumberField/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phoneNumberField/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:phoneNumberField/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:phoneNumberField/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:phoneNumberField/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:phoneNumberField/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:phoneNumberField/@size", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:phoneNumberField/@buttonId", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:phoneNumberField/@pattern", field);

		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getPattern(), "//ui:phoneNumberField/@pattern", field);

		field.setText("(12) 3456 7890");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getText(), "normalize-space(//ui:phoneNumberField)", field);

		field.setSuggestions(suggestions);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(suggestions.getId(), "//ui:phoneNumberField/@list", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPhoneNumberField field = new WPhoneNumberField();

		field.setText(getMaliciousContent());
		assertSafeContent(field);

		field.setToolTip(getMaliciousAttribute("ui:phoneNumberField"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:phoneNumberField"));
		assertSafeContent(field);
	}
}
