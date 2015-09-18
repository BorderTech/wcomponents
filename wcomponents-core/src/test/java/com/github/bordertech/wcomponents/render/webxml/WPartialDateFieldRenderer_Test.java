package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WPartialDateFieldRenderer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WPartialDateFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test title.
	 */
	private static final String TEST_TITLE = "WPartialDateField test title";

	/**
	 * Test alt text.
	 */
	private static final String TEST_ALT_TEXT = "WPartialDateField test alt text";

	/**
	 * Test buttonId.
	 */
	private static final String TEST_BUTTON_ID = "WPartialDateField button ID";

	/**
	 * Test renderer correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WPartialDateField component = new WPartialDateField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WPartialDateFieldRenderer);
	}

	/**
	 * Test doPaint - defaults.
	 */
	@Test
	public void testDoPaintDefaults() throws IOException, SAXException, XpathException {
		WPartialDateField dateField = new WPartialDateField();
		assertSchemaMatch(dateField);

		// defaults
		assertXpathExists("//ui:dateField", dateField);
		assertXpathEvaluatesTo(dateField.getId(), "//ui:dateField/@id", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@allowPartial", dateField);

		// actual date value not set
		assertXpathEvaluatesTo("", "//ui:dateField", dateField);
	}

	/**
	 * Test doPaint - optional attributes.
	 */
	@Test
	public void testDoPaintOptionals() throws IOException, SAXException, XpathException {
		WPartialDateField dateField = new WPartialDateField();

		// Optional attributes
		dateField.setDisabled(true);
		setFlag(dateField, ComponentModel.HIDE_FLAG, true);
		dateField.setMandatory(true);
		dateField.setReadOnly(true);
		dateField.setToolTip(TEST_TITLE);
		dateField.setAccessibleText(TEST_ALT_TEXT);
		dateField.setDefaultSubmitButton(new WButton(TEST_BUTTON_ID));

		assertSchemaMatch(dateField);

		// Defaults
		assertXpathExists("//ui:dateField", dateField);
		assertXpathEvaluatesTo(dateField.getId(), "//ui:dateField/@id", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@allowPartial", dateField);

		// Optionals
		assertXpathEvaluatesTo("true", "//ui:dateField/@disabled", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@hidden", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@required", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@readOnly", dateField);
		assertXpathEvaluatesTo(TEST_TITLE, "//ui:dateField/@toolTip", dateField);
		assertXpathEvaluatesTo(TEST_ALT_TEXT, "//ui:dateField/@accessibleText", dateField);
		assertXpathEvaluatesTo(String.valueOf(dateField.getDefaultSubmitButton().getId()),
				"//ui:dateField/@buttonId",
				dateField);

		// Actual date value not set
		assertXpathEvaluatesTo("", "//ui:dateField", dateField);
	}

	/**
	 * Test doPaint - default attributes - partial date value.
	 */
	@Test
	public void testDoPaintPartialDate() throws IOException, SAXException, XpathException {
		WPartialDateField dateField = new WPartialDateField();

		// Set actual date value
		dateField.setPartialDate(null, 1, 2000);

		assertSchemaMatch(dateField);

		// Defaults
		assertXpathExists("//ui:dateField", dateField);
		assertXpathEvaluatesTo(dateField.getId(), "//ui:dateField/@id", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@allowPartial", dateField);

		// Actual date value
		assertXpathEvaluatesTo("2000-01-??", "//ui:dateField/@date", dateField);
	}

	/**
	 * Test doPaint - default attributes - given date value.
	 */
	@Test
	public void testDoPaintActualDate() throws IOException, SAXException, XpathException {
		WPartialDateField dateField = new WPartialDateField();

		// Set actual date value
		dateField.setPartialDate(1, 2, 2000);

		assertSchemaMatch(dateField);

		// Defaults
		assertXpathExists("//ui:dateField", dateField);
		assertXpathEvaluatesTo(dateField.getId(), "//ui:dateField/@id", dateField);
		assertXpathEvaluatesTo("true", "//ui:dateField/@allowPartial", dateField);

		// Actual date value
		assertXpathEvaluatesTo("2000-02-01", "//ui:dateField/@date", dateField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPartialDateField dateField = new WPartialDateField();

		dateField.setData(getMaliciousContent());

		try {
			assertSafeContent(dateField);
			Assert.fail("Invalid date should not have been parsed.");
		} catch (SystemException e) {
			Assert.assertNotNull("Exception has no message", e.getMessage());
		}

		dateField.setData(null);
		dateField.setToolTip(getMaliciousAttribute("ui:dateField"));
		assertSafeContent(dateField);

		dateField.setAccessibleText(getMaliciousAttribute("ui:dateField"));
		assertSafeContent(dateField);
	}
}
