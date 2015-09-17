package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WMultiDropdown;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMultiDropdownRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMultiDropdownRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(dropdown) instanceof WMultiDropdownRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});
		setActiveContext(createUIContext());

		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("3", "count(//ui:multiDropdown/ui:option)", dropdown);
		assertXpathNotExists("//ui:multiDropdown/@rows", dropdown);

		// Check selected (default to first option)
		assertXpathEvaluatesTo("a",
				"normalize-space(//ui:multiDropdown/ui:option[@selected='true'])", dropdown);

		setActiveContext(createUIContext());
		dropdown.setSelected(Arrays.asList(new String[]{"b"}));
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("3", "count(//ui:multiDropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo("b",
				"normalize-space(//ui:multiDropdown/ui:option[@selected='true'])", dropdown);

		// Check Readonly - only render selected option
		dropdown.setReadOnly(true);
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@readOnly", dropdown);
		assertXpathEvaluatesTo("1", "count(//ui:multiDropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo("b",
				"normalize-space(//ui:multiDropdown/ui:option[@selected='true'])", dropdown);

		// Check max inputs
		dropdown.setMaxInputs(123);
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("123", "//ui:multiDropdown/@max", dropdown);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});

		dropdown.setDisabled(true);
		setFlag(dropdown, ComponentModel.HIDE_FLAG, true);
		dropdown.setMandatory(true);
		dropdown.setReadOnly(true);
		dropdown.setSubmitOnChange(true);
		dropdown.setToolTip("tool tip");
		dropdown.setAccessibleText("accessible text");
		dropdown.setMinSelect(1);
		dropdown.setMaxSelect(2);

		assertSchemaMatch(dropdown);

		assertXpathEvaluatesTo(dropdown.getId(), "//ui:multiDropdown/@id", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@disabled", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@hidden", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@required", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@readOnly", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multiDropdown/@submitOnChange", dropdown);
		assertXpathEvaluatesTo("tool tip", "//ui:multiDropdown/@toolTip", dropdown);
		assertXpathEvaluatesTo("accessible text", "//ui:multiDropdown/@accessibleText", dropdown);
		assertXpathEvaluatesTo("1", "//ui:multiDropdown/@min", dropdown);
		assertXpathEvaluatesTo("2", "//ui:multiDropdown/@max", dropdown);
	}

	@Test
	public void testDataList() throws IOException, SAXException, XpathException {
		WMultiDropdown dropdown = new WMultiDropdown(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		setActiveContext(createUIContext());

		assertSchemaMatch(dropdown);

		// Default first option
		String code = dropdown.getCode(dropdown.getOptions().get(0), 0);
		String desc = dropdown.getDesc(dropdown.getOptions().get(0), 0);

		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("1", "count(//ui:multiDropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo(desc, "//ui:multiDropdown/ui:option[@value='" + code + "']/text()",
				dropdown);
		assertXpathEvaluatesTo(dropdown.getListCacheKey(), "//ui:multiDropdown/@data", dropdown);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"), Arrays.
				asList(new String[]{"dummy"}));
		WMultiDropdown drop = new WMultiDropdown(Arrays.asList(
				new Object[]{getInvalidCharSequence(), getMaliciousContent(), optionGroup}));

		assertSafeContent(drop);

		drop.setToolTip(getMaliciousAttribute("ui:multiDropdown"));
		assertSafeContent(drop);

		drop.setAccessibleText(getMaliciousAttribute("ui:multiDropdown"));
		assertSafeContent(drop);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WMultiDropdown drop = new WMultiDropdown(options);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("5", "count(//ui:multiDropdown/ui:option)", drop);

		assertXpathEvaluatesTo("", "//ui:multiDropdown/ui:option[@value='']/text()", drop);

		for (int i = 0; i < options.length; i++) {
			String code = drop.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("",
						"//ui:multiDropdown/ui:option[@value='" + code + "']/text()", drop);
				assertXpathEvaluatesTo("true",
						"//ui:multiDropdown/ui:option[@value='" + code + "']/@isNull", drop);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:multiDropdown/ui:option[@value='" + code + "']/text()", drop);
				assertXpathEvaluatesTo("",
						"//ui:multiDropdown/ui:option[@value='" + code + "']/@isNull", drop);
			}
		}
	}

}
