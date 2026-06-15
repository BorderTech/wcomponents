package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WCheckBoxSelectRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCheckBoxSelectRenderer_Test extends AbstractWebXmlRendererTestCase {

	private static final String TAG_CHECK_BOX_SELECT = "wc-check-box-select";
	private static final String TAG_OPTION = "wc-option";

	@Test
	public void testRendererCorrectlyConfigured() {
		WCheckBoxSelect component = new WCheckBoxSelect();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WCheckBoxSelectRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WCheckBoxSelect wcbTest = new WCheckBoxSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s)", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);

		// Check selected
		assertXpathNotExists(String.format("//html:%s/html:%s[@selected='true']", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);

		setActiveContext(createUIContext());
		wcbTest.setSelected(Arrays.asList(new String[]{"b"}));
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s[@selected='true'])", TAG_CHECK_BOX_SELECT, TAG_OPTION),
				wcbTest);
		assertXpathEvaluatesTo("b", String.format("//html:%s/html:%s[@selected='true']", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);

		// Check Readonly - only render selected option
		wcbTest.setReadOnly(true);
		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@readOnly", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s[@selected='true'])", TAG_CHECK_BOX_SELECT, TAG_OPTION),
				wcbTest);
		assertXpathEvaluatesTo("b", String.format("//html:%s/html:%s[@selected='true']", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);
	}


	@Test
	public void testDoPaintReadOnly() throws IOException, SAXException, XpathException {
		WCheckBoxSelect wcbTest = new WCheckBoxSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(wcbTest);
		setActiveContext(createUIContext());
		// Check Readonly - only render selected option
		wcbTest.setReadOnly(true);
		wcbTest.setSelected(Arrays.asList(new String[]{"b"}));

		assertSchemaMatch(wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@readOnly", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s)", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s[@selected='true'])", TAG_CHECK_BOX_SELECT, TAG_OPTION),
				wcbTest);
		assertXpathEvaluatesTo("b", String.format("//html:%s/html:%s[@selected='true']", TAG_CHECK_BOX_SELECT, TAG_OPTION), wcbTest);
	}


	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WCheckBoxSelect wcbTest = new WCheckBoxSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(wcbTest);

		wcbTest.setDisabled(true);
		setFlag(wcbTest, ComponentModel.HIDE_FLAG, true);
		wcbTest.setMandatory(true);
		wcbTest.setSubmitOnChange(true);
		wcbTest.setToolTip("tool tip");
		wcbTest.setAccessibleText("accessible text");
		wcbTest.setFrameless(true);
		wcbTest.setMinSelect(1);
		wcbTest.setMaxSelect(2);

		assertSchemaMatch(wcbTest);

		assertXpathEvaluatesTo(wcbTest.getId(), String.format("//html:%s/@id", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@disabled", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@hidden", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@required", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@submitOnChange", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("tool tip", String.format("//html:%s/@toolTip", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("accessible text", String.format("//html:%s/@accessibleText", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@frameless", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("1", String.format("//html:%s/@min", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("2", String.format("//html:%s/@max", TAG_CHECK_BOX_SELECT), wcbTest);

		// Button Layouts
		wcbTest.setButtonLayout(WCheckBoxSelect.LAYOUT_COLUMNS);
		wcbTest.setButtonColumns(3);
		assertXpathEvaluatesTo("column", String.format("//html:%s/@layout", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathEvaluatesTo("3", String.format("//html:%s/@layoutColumnCount", TAG_CHECK_BOX_SELECT), wcbTest);

		wcbTest.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
		assertXpathEvaluatesTo("flat", String.format("//html:%s/@layout", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathNotExists(String.format("//html:%s/@layoutColumnCount", TAG_CHECK_BOX_SELECT), wcbTest);

		wcbTest.setButtonLayout(WCheckBoxSelect.LAYOUT_STACKED);
		assertXpathEvaluatesTo("stacked", String.format("//html:%s/@layout", TAG_CHECK_BOX_SELECT), wcbTest);
		assertXpathNotExists(String.format("//html:%s/@layoutColumnCount", TAG_CHECK_BOX_SELECT), wcbTest);
	}

	@Test(expected = SystemException.class)
	public void testOptGroupException() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("Test", Arrays.asList(new String[]{"A", "B"}));
		WCheckBoxSelect group = new WCheckBoxSelect(Arrays.asList(new Object[]{"X", optionGroup}));
		assertSchemaMatch(group);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WCheckBoxSelect wcb = new WCheckBoxSelect(Arrays.asList(
				new Object[]{getInvalidCharSequence(),
					getMaliciousContent()}));

		assertSafeContent(wcb);

		wcb.setToolTip(getMaliciousAttribute("html:" + TAG_CHECK_BOX_SELECT));
		assertSafeContent(wcb);

		wcb.setAccessibleText(getMaliciousAttribute("html:" + TAG_CHECK_BOX_SELECT));
		assertSafeContent(wcb);
	}
}
