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
		assertXpathEvaluatesTo(field.getId(), "//ui:passwordfield/@id", field);
		assertXpathNotExists("//ui:passwordfield/@disabled", field);
		assertXpathNotExists("//ui:passwordfield/@hidden", field);
		assertXpathNotExists("//ui:passwordfield/@required", field);
		assertXpathNotExists("//ui:passwordfield/@readOnly", field);
		assertXpathNotExists("//ui:passwordfield/@minLength", field);
		assertXpathNotExists("//ui:passwordfield/@maxLength", field);
		assertXpathNotExists("//ui:passwordfield/@toolTip", field);
		assertXpathNotExists("//ui:passwordfield/@accessibleText", field);
		assertXpathNotExists("//ui:passwordfield/@size", field);
		assertXpathNotExists("//ui:passwordfield/@buttonId", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordfield/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordfield/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordfield/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:passwordfield/@readOnly", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:passwordfield/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:passwordfield/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:passwordfield/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:passwordfield/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:passwordfield/@size", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:passwordfield/@buttonId", field);

		field.setText("Hello");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("", "normalize-space(//ui:passwordfield)", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPasswordField field = new WPasswordField();

		field.setToolTip(getMaliciousAttribute("ui:passwordfield"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:passwordfield"));
		assertSafeContent(field);
	}
}
