package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WMultiTextField;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMultiTextFieldRenderer}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiTextFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMultiTextField wmtf = new WMultiTextField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(wmtf) instanceof WMultiTextFieldRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WMultiTextField wmtf = new WMultiTextField();
		assertSchemaMatch(wmtf);

		assertXpathEvaluatesTo("0", "count(//ui:multitextfield/ui:value)", wmtf);

		wmtf.setTextInputs(new String[]{"a", "b"});

		assertXpathEvaluatesTo("2", "count(//ui:multitextfield/ui:value)", wmtf);
		assertXpathEvaluatesTo("a", "normalize-space(//ui:multitextfield/ui:value[position()=1])",
				wmtf);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:multitextfield/ui:value[position()=2])",
				wmtf);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMultiTextField field = new WMultiTextField();

		field.setTextInputs(new String[]{getInvalidCharSequence(), getMaliciousContent()});
		assertSafeContent(field);

		field.setToolTip(getMaliciousAttribute("ui:multitextfield"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:multitextfield"));
		assertSafeContent(field);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		WMultiTextField field = new WMultiTextField();
		field.setTextInputs(new String[]{"a", "b"});

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:multitextfield/@id", field);

		assertXpathNotExists("//ui:multitextfield/@disabled", field);
		assertXpathNotExists("//ui:multitextfield/@hidden", field);
		assertXpathNotExists("//ui:multitextfield/@required", field);
		assertXpathNotExists("//ui:multitextfield/@readOnly", field);
		assertXpathNotExists("//ui:multitextfield/@toolTip", field);
		assertXpathNotExists("//ui:multitextfield/@accessibleText", field);
		assertXpathNotExists("//ui:multitextfield/@size", field);
		assertXpathNotExists("//ui:multitextfield/@minLength", field);
		assertXpathNotExists("//ui:multitextfield/@maxLength", field);
		assertXpathNotExists("//ui:multitextfield/@max", field);
		assertXpathNotExists("//ui:multitextfield/@pattern", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multitextfield/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multitextfield/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multitextfield/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multitextfield/@readOnly", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:multitextfield/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:multitextfield/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:multitextfield/@size", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:multitextfield/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:multitextfield/@maxLength", field);

		field.setMaxInputs(10);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("10", "//ui:multitextfield/@max", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:multitextfield/@pattern", field);

		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getPattern(), "//ui:multitextfield/@pattern", field);
	}

}
