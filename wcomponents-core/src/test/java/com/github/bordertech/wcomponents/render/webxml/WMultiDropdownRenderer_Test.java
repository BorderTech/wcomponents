package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
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
	public void testDoPaint() throws IOException, SAXException {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});
		setActiveContext(createUIContext());

		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("3", "count(//ui:multidropdown/ui:option)", dropdown);
		assertXpathNotExists("//ui:multidropdown/@rows", dropdown);

		// Check selected (default to first option)
		assertXpathEvaluatesTo("a", "normalize-space(//ui:multidropdown/ui:option[@selected='true'])", dropdown);
		assertXpathEvaluatesTo(I18nUtilities.format(null, InternalMessages.DEFAULT_MULTIDROPDOWN_TIP), "//ui:multidropdown/@title", dropdown);

		setActiveContext(createUIContext());
		dropdown.setSelected(Arrays.asList(new String[]{"b"}));
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("3", "count(//ui:multidropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:multidropdown/ui:option[@selected='true'])", dropdown);

		// Check max inputs
		dropdown.setMaxInputs(123);
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("123", "//ui:multidropdown/@max", dropdown);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});
		setActiveContext(createUIContext());

		// Check Readonly - only render selected option
		dropdown.setReadOnly(true);
		dropdown.setSelected(Arrays.asList(new String[]{"b"}));
		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("true", "//ui:multidropdown/@readOnly", dropdown);
		assertXpathEvaluatesTo("1", "count(//ui:multidropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:multidropdown/ui:option[@selected='true'])", dropdown);
		assertXpathNotExists("//ui:multidropdown/@title", dropdown);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException {
		WMultiDropdown dropdown = new WMultiDropdown(new String[]{"a", "b", "c"});

		dropdown.setDisabled(true);
		setFlag(dropdown, ComponentModel.HIDE_FLAG, true);
		dropdown.setMandatory(true);
		dropdown.setSubmitOnChange(true);
		dropdown.setToolTip("tool tip");
		dropdown.setAccessibleText("accessible text");
		dropdown.setMinSelect(1);
		dropdown.setMaxSelect(2);

		assertSchemaMatch(dropdown);

		assertXpathEvaluatesTo(dropdown.getId(), "//ui:multidropdown/@id", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multidropdown/@disabled", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multidropdown/@hidden", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multidropdown/@required", dropdown);
		assertXpathEvaluatesTo("true", "//ui:multidropdown/@submitOnChange", dropdown);
		assertXpathEvaluatesTo("tool tip", "//ui:multidropdown/@toolTip", dropdown);
		assertXpathEvaluatesTo("accessible text", "//ui:multidropdown/@accessibleText", dropdown);
		assertXpathEvaluatesTo("1", "//ui:multidropdown/@min", dropdown);
		assertXpathEvaluatesTo("2", "//ui:multidropdown/@max", dropdown);
	}

	@Test
	public void testDataList() throws IOException, SAXException {
		WMultiDropdown dropdown = new WMultiDropdown(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		setActiveContext(createUIContext());

		assertSchemaMatch(dropdown);

		// Default first option
		String code = dropdown.getCode(dropdown.getOptions().get(0), 0);
		String desc = dropdown.getDesc(dropdown.getOptions().get(0), 0);

		assertSchemaMatch(dropdown);
		assertXpathEvaluatesTo("1", "count(//ui:multidropdown/ui:option)", dropdown);
		assertXpathEvaluatesTo(desc, "//ui:multidropdown/ui:option[@value='" + code + "']/text()",
				dropdown);
		assertXpathEvaluatesTo(dropdown.getListCacheKey(), "//ui:multidropdown/@data", dropdown);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"), Arrays.
				asList(new String[]{"dummy"}));
		WMultiDropdown drop = new WMultiDropdown(Arrays.asList(
				new Object[]{getInvalidCharSequence(), getMaliciousContent(), optionGroup}));

		assertSafeContent(drop);

		drop.setToolTip(getMaliciousAttribute("ui:multidropdown"));
		assertSafeContent(drop);

		drop.setAccessibleText(getMaliciousAttribute("ui:multidropdown"));
		assertSafeContent(drop);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WMultiDropdown drop = new WMultiDropdown(options);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("5", "count(//ui:multidropdown/ui:option)", drop);

		assertXpathEvaluatesTo("", "//ui:multidropdown/ui:option[@value='']/text()", drop);

		for (int i = 0; i < options.length; i++) {
			String code = drop.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("",
						"//ui:multidropdown/ui:option[@value='" + code + "']/text()", drop);
				assertXpathEvaluatesTo("true",
						"//ui:multidropdown/ui:option[@value='" + code + "']/@isNull", drop);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:multidropdown/ui:option[@value='" + code + "']/text()", drop);
				assertXpathEvaluatesTo("",
						"//ui:multidropdown/ui:option[@value='" + code + "']/@isNull", drop);
			}
		}
	}

}
