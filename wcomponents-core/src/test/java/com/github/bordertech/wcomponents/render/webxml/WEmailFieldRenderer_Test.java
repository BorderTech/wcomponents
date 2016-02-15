package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WSuggestions;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WEmailFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WEmailFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WEmailField textField = new WEmailField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(textField) instanceof WEmailFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WEmailField field = new WEmailField();
		WButton button = new WButton();
		WSuggestions suggestions = new WSuggestions();

		WContainer root = new WContainer();
		root.add(field);
		root.add(button);
		root.add(suggestions);

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:emailfield/@id", field);
		assertXpathNotExists("//ui:emailfield/@disabled", field);
		assertXpathNotExists("//ui:emailfield/@hidden", field);
		assertXpathNotExists("//ui:emailfield/@required", field);
		assertXpathNotExists("//ui:emailfield/@readOnly", field);
		assertXpathNotExists("//ui:emailfield/@maxLength", field);
		assertXpathNotExists("//ui:emailfield/@toolTip", field);
		assertXpathNotExists("//ui:emailfield/@accessibleText", field);
		assertXpathNotExists("//ui:emailfield/@size", field);
		assertXpathNotExists("//ui:emailfield/@buttonId", field);
		assertXpathNotExists("//ui:emailfield/@list", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:emailfield/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:emailfield/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:emailfield/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:emailfield/@readOnly", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:emailfield/@maxLength", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:emailfield/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:emailfield/@accessibleText", field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:emailfield/@size", field);

		field.setDefaultSubmitButton(button);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(button.getId(), "//ui:emailfield/@buttonId", field);

		field.setText("nobody@wc.test"); // RFC 2606
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getText(), "normalize-space(//ui:emailfield)", field);

		field.setSuggestions(suggestions);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(suggestions.getId(), "//ui:emailfield/@list", field);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WEmailField field = new WEmailField();

		field.setText(getMaliciousContent());
		assertSafeContent(field);

		field.setToolTip(getMaliciousAttribute("ui:emailfield"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:emailfield"));
		assertSafeContent(field);
	}
}
