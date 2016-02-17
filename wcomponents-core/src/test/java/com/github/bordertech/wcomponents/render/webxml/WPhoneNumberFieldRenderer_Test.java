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
		assertXpathEvaluatesTo(field.getId(), "//ui:phonenumberfield/@id", field);
		assertXpathNotExists("//ui:phonenumberfield/@disabled", field);
		assertXpathNotExists("//ui:phonenumberfield/@hidden", field);
		assertXpathNotExists("//ui:phonenumberfield/@required", field);
		assertXpathNotExists("//ui:phonenumberfield/@readOnly", field);
		assertXpathNotExists("//ui:phonenumberfield/@minLength", field);
		assertXpathNotExists("//ui:phonenumberfield/@maxLength", field);
		assertXpathNotExists("//ui:phonenumberfield/@toolTip", field);
		assertXpathNotExists("//ui:phonenumberfield/@accessibleText", field);
		assertXpathNotExists("//ui:phonenumberfield/@size", field);
		assertXpathNotExists("//ui:phonenumberfield/@buttonId", field);
		assertXpathNotExists("//ui:phonenumberfield/@pattern", field);
		assertXpathNotExists("//ui:phonenumberfield/@list", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phonenumberfield/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phonenumberfield/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phonenumberfield/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:phonenumberfield/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:phonenumberfield/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:phonenumberfield/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:phonenumberfield/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:phonenumberfield/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:phonenumberfield/@size", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:phonenumberfield/@buttonId", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:phonenumberfield/@pattern", field);

		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getPattern(), "//ui:phonenumberfield/@pattern", field);

		field.setText("(12) 3456 7890");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getText(), "normalize-space(//ui:phonenumberfield)", field);

		field.setSuggestions(suggestions);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(suggestions.getId(), "//ui:phonenumberfield/@list", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPhoneNumberField field = new WPhoneNumberField();

		field.setText(getMaliciousContent());
		assertSafeContent(field);

		field.setToolTip(getMaliciousAttribute("ui:phonenumberfield"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:phonenumberfield"));
		assertSafeContent(field);
	}
}
