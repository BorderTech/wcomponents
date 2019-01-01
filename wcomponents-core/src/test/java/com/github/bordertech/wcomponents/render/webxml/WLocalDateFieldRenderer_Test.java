package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WLocalDateField;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDateFieldRenderer}.
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WLocalDateFieldRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test Date.
	 */
	private static final LocalDate TEST_DATE = LocalDate.of(2010, Month.DECEMBER, 31);

	/**
	 * A string representation of the test Date, using the internal date format .
	 */
	private static final String TEST_INTERNAL_DATE_STRING = "2010-12-31";

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WLocalDateField dateField = new WLocalDateField();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(dateField) instanceof WLocalDateFieldRenderer);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WLocalDateField dateField = new WLocalDateField();

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
		assertXpathNotExists("//ui:datefield[@toolTip]", dateField);
		assertXpathNotExists("//ui:datefield[@accessibleText]", dateField);
		assertXpathNotExists("//ui:datefield[@buttonId]", dateField);
		assertXpathNotExists("//ui:datefield[@date]", dateField);
		assertXpathNotExists("//ui:datefield[@min]", dateField);
		assertXpathNotExists("//ui:datefield[@max]", dateField);
		assertXpathNotExists("//ui:datefield[@autocomplete]", dateField);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WButton button = new WButton();
		WLocalDateField dateField = new WLocalDateField();
		dateField.setLocalDate(TEST_DATE);
		dateField.setDisabled(true);
		setFlag(dateField, ComponentModel.HIDE_FLAG, true);
		dateField.setMandatory(true);
		dateField.setToolTip("TITLE");
		dateField.setAccessibleText("ALT");
		dateField.setDefaultSubmitButton(button);
		dateField.setMinDate(LocalDate.of(2011, Month.FEBRUARY, 1));
		dateField.setMaxDate(LocalDate.of(2012, Month.MARCH, 2));

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
		assertXpathEvaluatesTo("TITLE", "//ui:datefield/@toolTip", dateField);
		assertXpathEvaluatesTo("ALT", "//ui:datefield/@accessibleText", dateField);
		assertXpathEvaluatesTo(button.getId(), "//ui:datefield/@buttonId", dateField);
		assertXpathEvaluatesTo("2011-02-01", "//ui:datefield/@min", dateField);
		assertXpathEvaluatesTo("2012-03-02", "//ui:datefield/@max", dateField);
	}

	@Test
	public void testDoPaintReadOnly() throws IOException, SAXException, XpathException {
		WLocalDateField dateField = new WLocalDateField();
		dateField.setLocalDate(TEST_DATE);
		dateField.setReadOnly(true);
		setActiveContext(createUIContext());

		// Validate Schema
		assertSchemaMatch(dateField);
		assertXpathEvaluatesTo("true", "//ui:datefield/@readOnly", dateField);
		assertXpathEvaluatesTo(TEST_INTERNAL_DATE_STRING, "//ui:datefield/@date", dateField);
	}

	@Test
	public void testDoPaintAutocomplete() throws IOException, SAXException, XpathException {
		WLocalDateField dateField = new WLocalDateField();
		dateField.setLocalDate(TEST_DATE);
		dateField.setBirthdayAutocomplete();

		// Validate Schema
		assertSchemaMatch(dateField);
		assertXpathEvaluatesTo("bday", "//ui:datefield/@autocomplete", dateField);
	}

	@Test
	public void testDoPaintAutocompleteOff() throws IOException, SAXException, XpathException {
		WLocalDateField dateField = new WLocalDateField();
		dateField.setLocalDate(TEST_DATE);
		dateField.setAutocompleteOff();

		// Validate Schema
		assertSchemaMatch(dateField);
		assertXpathEvaluatesTo("off", "//ui:datefield/@autocomplete", dateField);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WLocalDateField dateField = new WLocalDateField();
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
