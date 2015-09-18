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
		assertXpathEvaluatesTo(numberField.getId(), "//ui:numberField/@id", numberField);

		assertXpathNotExists("//ui:numberField/@disabled", numberField);
		assertXpathNotExists("//ui:numberField/@hidden", numberField);
		assertXpathNotExists("//ui:numberField/@required", numberField);
		assertXpathNotExists("//ui:numberField/@readOnly", numberField);
		assertXpathNotExists("//ui:numberField/@size", numberField);
		assertXpathNotExists("//ui:numberField/@toolTip", numberField);
		assertXpathNotExists("//ui:numberField/@accessibleText", numberField);
		assertXpathNotExists("//ui:numberField/@min", numberField);
		assertXpathNotExists("//ui:numberField/@max", numberField);
		assertXpathNotExists("//ui:numberField/@step", numberField);
		assertXpathNotExists("//ui:numberField/@decimals", numberField);
		assertXpathNotExists("//ui:numberField/@buttonId", numberField);

		numberField.setDisabled(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberField/@disabled", numberField);

		setFlag(numberField, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberField/@hidden", numberField);

		numberField.setMandatory(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberField/@required", numberField);

		numberField.setReadOnly(true);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("true", "//ui:numberField/@readOnly", numberField);

		numberField.setColumns(40);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("40", "//ui:numberField/@size", numberField);

		numberField.setToolTip("toolTip");
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(numberField.getToolTip(), "//ui:numberField/@toolTip", numberField);

		numberField.setAccessibleText("accessibleText");
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(numberField.getAccessibleText(), "//ui:numberField/@accessibleText",
				numberField);

		numberField.setMinValue(45);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("45", "//ui:numberField/@min", numberField);

		numberField.setMaxValue(50);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("50", "//ui:numberField/@max", numberField);

		numberField.setStep(0.5);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("0.5", "//ui:numberField/@step", numberField);

		numberField.setDecimalPlaces(2);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("2", "//ui:numberField/@decimals", numberField);

		numberField.setDefaultSubmitButton(button);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo(button.getId(), "//ui:numberField/@buttonId", numberField);

		numberField.setNumber(123);
		assertSchemaMatch(numberField);
		assertXpathEvaluatesTo("123", "normalize-space(//ui:numberField)", numberField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WNumberField numberField = new WNumberField();

		MockRequest request = new MockRequest();
		request.setParameter(numberField.getId(), getMaliciousContent());
		numberField.serviceRequest(request);

		assertSafeContent(numberField);

		numberField.setToolTip(getMaliciousAttribute("ui:numberField"));
		assertSafeContent(numberField);

		numberField.setAccessibleText(getMaliciousAttribute("ui:numberField"));
		assertSafeContent(numberField);
	}
}
