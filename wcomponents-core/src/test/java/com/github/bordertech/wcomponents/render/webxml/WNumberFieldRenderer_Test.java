package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WNumberFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WNumberFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WNumberField numberField = new WNumberField();
		Assert
				.assertTrue("Incorrect renderer supplied",
						getWebXmlRenderer(numberField) instanceof WNumberFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WNumberField numberField = new WNumberField();
		WButton button = new WButton("Test");

		WContainer root = new WContainer();
		root.add(numberField);
		root.add(button);

		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(numberField.getId(), "//ui:numberfield/@id", numberField);

		assertXpathNotExists("//ui:numberfield/@disabled", numberField);
		assertXpathNotExists("//ui:numberfield/@hidden", numberField);
		assertXpathNotExists("//ui:numberfield/@required", numberField);
		assertXpathNotExists("//ui:numberfield/@readOnly", numberField);
		assertXpathNotExists("//ui:numberfield/@toolTip", numberField);
		assertXpathNotExists("//ui:numberfield/@accessibleText", numberField);
		assertXpathNotExists("//ui:numberfield/@min", numberField);
		assertXpathNotExists("//ui:numberfield/@max", numberField);
		assertXpathNotExists("//ui:numberfield/@step", numberField);
		assertXpathNotExists("//ui:numberfield/@decimals", numberField);
		assertXpathNotExists("//ui:numberfield/@buttonId", numberField);

		numberField.setDisabled(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberfield/@disabled", numberField);

		setFlag(numberField, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberfield/@hidden", numberField);

		numberField.setMandatory(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberfield/@required", numberField);

		numberField.setToolTip("toolTip");
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(numberField.getToolTip(), "//ui:numberfield/@toolTip", numberField);

		numberField.setAccessibleText("accessibleText");
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(numberField.getAccessibleText(), "//ui:numberfield/@accessibleText",
				numberField);

		numberField.setMinValue(45);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("45", "//ui:numberfield/@min", numberField);

		numberField.setMaxValue(50);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("50", "//ui:numberfield/@max", numberField);

		numberField.setStep(0.5);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("0.5", "//ui:numberfield/@step", numberField);

		numberField.setDecimalPlaces(2);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("2", "//ui:numberfield/@decimals", numberField);

		numberField.setDefaultSubmitButton(button);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(button.getId(), "//ui:numberfield/@buttonId", numberField);

		numberField.setNumber(123);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("123", "normalize-space(//ui:numberfield)", numberField);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WNumberField numberField = new WNumberField();

		numberField.setReadOnly(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberfield/@readOnly", numberField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WNumberField numberField = new WNumberField();

		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), getMaliciousContent());
		numberField.serviceRequest(request);

		assertSafeContent(numberField);

		numberField.setToolTip(getMaliciousAttribute("ui:numberfield"));
		assertSafeContent(numberField);

		numberField.setAccessibleText(getMaliciousAttribute("ui:numberfield"));
		assertSafeContent(numberField);
	}
}
