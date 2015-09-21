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
		assertXpathEvaluatesTo(single.getId(), "//ui:listBox/@id", single);
		assertXpathEvaluatesTo("3", "count(//ui:listBox/ui:option)", single);
		assertXpathNotExists("//ui:listBox/@rows", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@single", single);

		// Check selected
		assertXpathNotExists("//ui:listBox/ui:option[@selected='true']", single);

		single.setSelected("b");
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("3", "count(//ui:listBox/ui:option)", single);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listBox/ui:option[@selected='true'])",
				single);

		// Check Readonly - only render selected option
		single.setReadOnly(true);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@readOnly", single);
		assertXpathEvaluatesTo("1", "count(//ui:listBox/ui:option)", single);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listBox/ui:option[@selected='true'])",
				single);

		// Check rows
		single.setRows(123);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("123", "//ui:listBox/@rows", single);
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
		single.setReadOnly(true);
		single.setSubmitOnChange(true);
		single.setToolTip(tooltip);
		single.setAccessibleText(accessible);
		single.setRows(rows);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@disabled", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@hidden", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@required", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@readOnly", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@submitOnChange", single);
		assertXpathEvaluatesTo(tooltip, "//ui:listBox/@toolTip", single);
		assertXpathEvaluatesTo(accessible, "//ui:listBox/@accessibleText", single);
		assertXpathEvaluatesTo(Integer.toString(rows), "//ui:listBox/@rows", single);
		assertXpathEvaluatesTo("true", "//ui:listBox/@single", single);
	}

	@Test
	public void testDataList() throws IOException, SAXException, XpathException {
		WSingleSelect single = new WSingleSelect(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		assertSchemaMatch(single);

		assertXpathEvaluatesTo("0", "count(//ui:listBox/ui:option)", single);
		assertXpathEvaluatesTo(single.getListCacheKey(), "//ui:listBox/@data", single);

		// Set Selected
		String code = single.getCode(single.getOptions().get(0), 0);
		String desc = single.getDesc(single.getOptions().get(0), 0);
		single.setSelected(code);

		assertSchemaMatch(single);
		assertXpathEvaluatesTo("1", "count(//ui:listBox/ui:option)", single);
		assertXpathEvaluatesTo(desc, "//ui:listBox/ui:option[@value='" + code + "']/text()", single);
		assertXpathEvaluatesTo(single.getListCacheKey(), "//ui:listBox/@data", single);
	}

	@Test
	public void testOptionGroups() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WSingleSelect single = new WSingleSelect(options);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("2", "count(//ui:listBox/ui:option)", single);
		assertXpathEvaluatesTo("1", "count(//ui:listBox/ui:optgroup)", single);
		assertXpathEvaluatesTo("4", "count(//ui:listBox/ui:optgroup/ui:option)", single);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:listBox/ui:optgroup/@label", single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:listBox/ui:optgroup/ui:option[1]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:listBox/ui:optgroup/ui:option[2]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:listBox/ui:optgroup/ui:option[3]",
				single);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:listBox/ui:optgroup/ui:option[4]",
				single);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:listBox/ui:option[1]/@value", single);
		assertXpathEvaluatesTo("2", "//ui:listBox/ui:optgroup/ui:option[1]/@value", single);
		assertXpathEvaluatesTo("3", "//ui:listBox/ui:optgroup/ui:option[2]/@value", single);
		assertXpathEvaluatesTo("4", "//ui:listBox/ui:optgroup/ui:option[3]/@value", single);
		assertXpathEvaluatesTo("5", "//ui:listBox/ui:optgroup/ui:option[4]/@value", single);
		assertXpathEvaluatesTo("6", "//ui:listBox/ui:option[2]/@value", single);

		// Check selection
		single.setSelected("A");
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", single);
		assertXpathExists("//ui:listBox/ui:option[text()='A'][@selected='true']", single);

		single.setSelected("B.3");
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", single);
		assertXpathExists("//ui:listBox/ui:optgroup/ui:option[text()='B.3'][@selected='true']",
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

		single.setToolTip(getMaliciousAttribute("ui:listBox"));
		assertSafeContent(single);

		single.setAccessibleText(getMaliciousAttribute("ui:listBox"));
		assertSafeContent(single);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WSingleSelect single = new WSingleSelect(options);
		assertSchemaMatch(single);
		assertXpathEvaluatesTo("5", "count(//ui:listBox/ui:option)", single);

		assertXpathEvaluatesTo("", "//ui:listBox/ui:option[@value='']/text()", single);

		for (int i = 0; i < options.length; i++) {
			String code = single.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("", "//ui:listBox/ui:option[@value='" + code + "']/text()",
						single);
				assertXpathEvaluatesTo("true",
						"//ui:listBox/ui:option[@value='" + code + "']/@isNull", single);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:listBox/ui:option[@value='" + code + "']/text()", single);
				assertXpathEvaluatesTo("", "//ui:listBox/ui:option[@value='" + code + "']/@isNull",
						single);
			}
		}
	}

}
