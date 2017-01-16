package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.util.DateUtilities;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import java.util.Date;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDateFieldRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDateFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test Date.
	 */
	private static final Date TEST_DATE = DateUtilities.createDate(31, 12, 2010);

	/**
	 * A string representation of the test Date, using the internal date format .
	 */
	private static final String TEST_INTERNAL_DATE_STRING = "2010-12-31";

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WDateField dateField = new WDateField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(dateField) instanceof WDateFieldRenderer);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WDateField dateField = new WDateField();

		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(dateField);
		// Check Attributes
		assertXpathEvaluatesTo(dateField.getId(), "//ui:datefield/@id", dateField);
		// Optional
		assertXpathEvaluatesTo("", "//ui:datefield", dateField);
		assertXpathNotExists("//ui:datefield[@disabled]", dateField);
		assertXpathNotExists("//ui:datefield[@hidden]", dateField);
		assertXpathNotExists("//ui:datefield[@required]", dateField);
		assertXpathNotExists("//ui:datefield[@readOnly]", dateField);
		assertXpathNotExists("//ui:datefield[@tabIndex]", dateField);
		assertXpathNotExists("//ui:datefield[@toolTip]", dateField);
		assertXpathNotExists("//ui:datefield[@accessibleText]", dateField);
		assertXpathNotExists("//ui:datefield[@buttonId]", dateField);
		assertXpathNotExists("//ui:datefield[@date]", dateField);
		assertXpathNotExists("//ui:datefield[@min]", dateField);
		assertXpathNotExists("//ui:datefield[@max]", dateField);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WButton button = new WButton();
		WDateField dateField = new WDateField();
		dateField.setDate(TEST_DATE);
		dateField.setDisabled(true);
		setFlag(dateField, ComponentModel.HIDE_FLAG, true);
		dateField.setMandatory(true);
		dateField.setToolTip("TITLE");
		dateField.setAccessibleText("ALT");
		dateField.setDefaultSubmitButton(button);
		dateField.setMinDate(DateUtilities.createDate(01, 02, 2011));
		dateField.setMaxDate(DateUtilities.createDate(02, 03, 2012));

		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(dateField);
		// Check Attributes
		assertXpathEvaluatesTo(dateField.getId(), "//ui:datefield/@id", dateField);
		// Optional
		assertXpathEvaluatesTo(TEST_INTERNAL_DATE_STRING, "//ui:datefield/@date", dateField);
		assertXpathEvaluatesTo("true", "//ui:datefield/@disabled", dateField);
		assertXpathEvaluatesTo("true", "//ui:datefield/@hidden", dateField);
		assertXpathEvaluatesTo("true", "//ui:datefield/@required", dateField);
		assertXpathEvaluatesTo("", "//ui:datefield/@tabIndex", dateField);
		assertXpathEvaluatesTo("TITLE", "//ui:datefield/@toolTip", dateField);
		assertXpathEvaluatesTo("ALT", "//ui:datefield/@accessibleText", dateField);
		assertXpathEvaluatesTo(button.getId(), "//ui:datefield/@buttonId", dateField);
		assertXpathEvaluatesTo("2011-02-01", "//ui:datefield/@min", dateField);
		assertXpathEvaluatesTo("2012-03-02", "//ui:datefield/@max", dateField);
	}

	@Test
	public void testDoPaintReadOnly() throws IOException, SAXException, XpathException {
		WDateField dateField = new WDateField();
		dateField.setDate(TEST_DATE);
		dateField.setReadOnly(true);
		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(dateField);
		assertXpathEvaluatesTo("true", "//ui:datefield/@readOnly", dateField);
		assertXpathEvaluatesTo(TEST_INTERNAL_DATE_STRING, "//ui:datefield/@date", dateField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WDateField dateField = new WDateField();
		setActiveContext(createUIContext());

		dateField.setToolTip(getMaliciousAttribute("ui:datefield"));
		assertSafeContent(dateField);

		dateField.setAccessibleText(getMaliciousAttribute("ui:datefield"));
		assertSafeContent(dateField);

		MockRequest request = new MockRequest();
		request.setParameter(dateField.getId(), getMaliciousContent());
		request.setParameter(dateField.getId() + "-date", getMaliciousContent());
		dateField.serviceRequest(request);

		assertSafeContent(dateField);
	}
}
