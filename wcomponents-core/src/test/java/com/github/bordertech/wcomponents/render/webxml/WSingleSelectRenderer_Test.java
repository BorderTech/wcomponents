package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WSingleSelect;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WSingleSelectRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSingleSelectRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WSingleSelect single = new WSingleSelect(new String[]{"a", "b", "c"});
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(single) instanceof WSingleSelectRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WSingleSelect single = new WSingleSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(single);
		assertXpathEvaluatesTo(single.getId(), "//ui:listbox/@id", single);
		assertXpathEvaluatesTo("3", "count(//ui:listbox/ui:option)", single);
		assertXpathNotExists("//ui:listbox/@rows", single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@single", single);

		// Check selected
		assertXpathNotExists("//ui:listbox/ui:option[@selected='true']", single);

		single.setSelected("b");
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("3", "count(//ui:listbox/ui:option)", single);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listbox/ui:option[@selected='true'])", single);
		// Check rows
		single.setRows(123);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("123", "//ui:listbox/@rows", single);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WSingleSelect single = new WSingleSelect(new String[]{"a", "b", "c"});
		single.setSelected("b");
		single.setReadOnly(true);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@readOnly", single);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:option)", single);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listbox/ui:option[@selected='true'])", single);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		String tooltip = "test tooltip";
		String accessible = "test accessible text";
		int rows = 2;

		WSingleSelect single = new WSingleSelect(new String[]{"a", "b", "c"});
		single.setDisabled(true);
		setFlag(single, ComponentModel.HIDE_FLAG, true);
		single.setMandatory(true);
		single.setSubmitOnChange(true);
		single.setToolTip(tooltip);
		single.setAccessibleText(accessible);
		single.setRows(rows);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@disabled", single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@hidden", single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@required", single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@submitOnChange", single);
		assertXpathEvaluatesTo(tooltip, "//ui:listbox/@toolTip", single);
		assertXpathEvaluatesTo(accessible, "//ui:listbox/@accessibleText", single);
		assertXpathEvaluatesTo(Integer.toString(rows), "//ui:listbox/@rows", single);
		assertXpathEvaluatesTo("true", "//ui:listbox/@single", single);
	}

	@Test
	public void testDataList() throws IOException, SAXException, XpathException {
		WSingleSelect single = new WSingleSelect(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		assertSchemaMatch(single);

		assertXpathEvaluatesTo("0", "count(//ui:listbox/ui:option)", single);
		assertXpathEvaluatesTo(single.getListCacheKey(), "//ui:listbox/@data", single);

		// Set Selected
		String code = single.getCode(single.getOptions().get(0), 0);
		String desc = single.getDesc(single.getOptions().get(0), 0);
		single.setSelected(code);

		assertSchemaMatch(single);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:option)", single);
		assertXpathEvaluatesTo(desc, "//ui:listbox/ui:option[@value='" + code + "']/text()", single);
		assertXpathEvaluatesTo(single.getListCacheKey(), "//ui:listbox/@data", single);
	}

	@Test
	public void testOptionGroups() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WSingleSelect single = new WSingleSelect(options);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("2", "count(//ui:listbox/ui:option)", single);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:optgroup)", single);
		assertXpathEvaluatesTo("4", "count(//ui:listbox/ui:optgroup/ui:option)", single);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:listbox/ui:optgroup/@label", single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:listbox/ui:optgroup/ui:option[1]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:listbox/ui:optgroup/ui:option[2]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:listbox/ui:optgroup/ui:option[3]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:listbox/ui:optgroup/ui:option[4]",
				single);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:listbox/ui:option[1]/@value", single);
		assertXpathEvaluatesTo("2", "//ui:listbox/ui:optgroup/ui:option[1]/@value", single);
		assertXpathEvaluatesTo("3", "//ui:listbox/ui:optgroup/ui:option[2]/@value", single);
		assertXpathEvaluatesTo("4", "//ui:listbox/ui:optgroup/ui:option[3]/@value", single);
		assertXpathEvaluatesTo("5", "//ui:listbox/ui:optgroup/ui:option[4]/@value", single);
		assertXpathEvaluatesTo("6", "//ui:listbox/ui:option[2]/@value", single);

		// Check selection
		single.setSelected("A");
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", single);
		assertXpathExists("//ui:listbox/ui:option[text()='A'][@selected='true']", single);

		single.setSelected("B.3");
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", single);
		assertXpathExists("//ui:listbox/ui:optgroup/ui:option[text()='B.3'][@selected='true']",
				single);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"),
				Arrays.asList(new String[]{"dummy"}));
		WSingleSelect single = new WSingleSelect(Arrays.asList(
				new Object[]{getInvalidCharSequence(),
					getMaliciousContent(), optionGroup}));

		assertSafeContent(single);

		single.setToolTip(getMaliciousAttribute("ui:listbox"));
		assertSafeContent(single);

		single.setAccessibleText(getMaliciousAttribute("ui:listbox"));
		assertSafeContent(single);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WSingleSelect single = new WSingleSelect(options);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("5", "count(//ui:listbox/ui:option)", single);

		assertXpathEvaluatesTo("", "//ui:listbox/ui:option[@value='']/text()", single);

		for (int i = 0; i < options.length; i++) {
			String code = single.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("", "//ui:listbox/ui:option[@value='" + code + "']/text()",
						single);
				assertXpathEvaluatesTo("true",
						"//ui:listbox/ui:option[@value='" + code + "']/@isNull", single);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:listbox/ui:option[@value='" + code + "']/text()", single);
				assertXpathEvaluatesTo("", "//ui:listbox/ui:option[@value='" + code + "']/@isNull",
						single);
			}
		}
	}

}
