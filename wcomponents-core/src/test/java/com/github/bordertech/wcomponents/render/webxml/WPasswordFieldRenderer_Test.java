package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WPasswordField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WPasswordFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WPasswordFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WPasswordField field = new WPasswordField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(field) instanceof WPasswordFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WPasswordField field = new WPasswordField();
		WButton button = new WButton();

		WContainer root = new WContainer();
		root.add(field);
		root.add(button);

		assertSchemaMatch(field);

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:passwordField/@id", field);
		assertXpathNotExists("//ui:passwordField/@disabled", field);
		assertXpathNotExists("//ui:passwordField/@hidden", field);
		assertXpathNotExists("//ui:passwordField/@required", field);
		assertXpathNotExists("//ui:passwordField/@readOnly", field);
		assertXpathNotExists("//ui:passwordField/@minLength", field);
		assertXpathNotExists("//ui:passwordField/@maxLength", field);
		assertXpathNotExists("//ui:passwordField/@toolTip", field);
		assertXpathNotExists("//ui:passwordField/@accessibleText", field);
		assertXpathNotExists("//ui:passwordField/@size", field);
		assertXpathNotExists("//ui:passwordField/@buttonId", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordField/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordField/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordField/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordField/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:passwordField/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:passwordField/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:passwordField/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:passwordField/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:passwordField/@size", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:passwordField/@buttonId", field);

		field.setText("Hello");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("", "normalize-space(//ui:passwordField)", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPasswordField field = new WPasswordField();

		field.setToolTip(getMaliciousAttribute("ui:passwordField"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:passwordField"));
		assertSafeContent(field);
	}
}
