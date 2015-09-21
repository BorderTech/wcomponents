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

		assertXpathEvaluatesTo("0", "count(//ui:multiTextField/ui:value)", wmtf);

		wmtf.setTextInputs(new String[]{"a", "b"});

		assertXpathEvaluatesTo("2", "count(//ui:multiTextField/ui:value)", wmtf);
		assertXpathEvaluatesTo("a", "normalize-space(//ui:multiTextField/ui:value[position()=1])",
				wmtf);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:multiTextField/ui:value[position()=2])",
				wmtf);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WMultiTextField field = new WMultiTextField();

		field.setTextInputs(new String[]{getInvalidCharSequence(), getMaliciousContent()});
		assertSafeContent(field);

		field.setToolTip(getMaliciousAttribute("ui:multiTextField"));
		assertSafeContent(field);

		field.setAccessibleText(getMaliciousAttribute("ui:multiTextField"));
		assertSafeContent(field);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		WMultiTextField field = new WMultiTextField();
		field.setTextInputs(new String[]{"a", "b"});

		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getId(), "//ui:multiTextField/@id", field);

		assertXpathNotExists("//ui:multiTextField/@disabled", field);
		assertXpathNotExists("//ui:multiTextField/@hidden", field);
		assertXpathNotExists("//ui:multiTextField/@required", field);
		assertXpathNotExists("//ui:multiTextField/@readOnly", field);
		assertXpathNotExists("//ui:multiTextField/@toolTip", field);
		assertXpathNotExists("//ui:multiTextField/@accessibleText", field);
		assertXpathNotExists("//ui:multiTextField/@size", field);
		assertXpathNotExists("//ui:multiTextField/@minLength", field);
		assertXpathNotExists("//ui:multiTextField/@maxLength", field);
		assertXpathNotExists("//ui:multiTextField/@max", field);
		assertXpathNotExists("//ui:multiTextField/@pattern", field);

		field.setDisabled(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multiTextField/@disabled", field);

		setFlag(field, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multiTextField/@hidden", field);

		field.setMandatory(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multiTextField/@required", field);

		field.setReadOnly(true);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("true", "//ui:multiTextField/@readOnly", field);

		field.setToolTip("tooltip");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getToolTip(), "//ui:multiTextField/@toolTip", field);

		field.setAccessibleText("accessible");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getAccessibleText(), "//ui:multiTextField/@accessibleText",
				field);

		field.setColumns(40);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("40", "//ui:multiTextField/@size", field);

		field.setMinLength(45);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("45", "//ui:multiTextField/@minLength", field);

		field.setMaxLength(50);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("50", "//ui:multiTextField/@maxLength", field);

		field.setMaxInputs(10);
		assertSchemaMatch(field);
		assertXpathEvaluatesTo("10", "//ui:multiTextField/@max", field);

		field.setPattern("");
		assertSchemaMatch(field);
		assertXpathNotExists("//ui:multiTextField/@pattern", field);

		field.setPattern("test[123]");
		assertSchemaMatch(field);
		assertXpathEvaluatesTo(field.getPattern(), "//ui:multiTextField/@pattern", field);
	}

}
