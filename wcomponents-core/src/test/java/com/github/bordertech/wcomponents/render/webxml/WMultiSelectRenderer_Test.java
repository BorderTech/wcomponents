package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WMultiSelect;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMultiSelectRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiSelectRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMultiSelect multi = new WMultiSelect(new String[]{"a", "b", "c"});
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(multi) instanceof WMultiSelectRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WMultiSelect multi = new WMultiSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("3", "count(//ui:listbox/ui:option)", multi);
		assertXpathNotExists("//ui:listbox/@rows", multi);
		assertXpathNotExists("//ui:listbox/@single", multi);

		// Check selected
		assertXpathNotExists("//ui:listbox/ui:option[@selected='true']", multi);

		setActiveContext(createUIContext());
		multi.setSelected(Arrays.asList(new String[]{"b"}));
		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("3", "count(//ui:listbox/ui:option)", multi);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listbox/ui:option[@selected='true'])", multi);

		// Check rows
		multi.setRows(123);
		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("123", "//ui:listbox/@rows", multi);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WMultiSelect multi = new WMultiSelect(new String[]{"a", "b", "c"});
		setActiveContext(createUIContext());
		multi.setSelected(Arrays.asList(new String[]{"b"}));

		// Check Readonly - only render selected option
		multi.setReadOnly(true);
		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("true", "//ui:listbox/@readOnly", multi);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:option)", multi);
		assertXpathEvaluatesTo("b", "normalize-space(//ui:listbox/ui:option[@selected='true'])", multi);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		String tooltip = "test tooltip";
		String accessible = "test accessible text";
		int rows = 2;

		WMultiSelect multi = new WMultiSelect(new String[]{"a", "b", "c"});

		multi.setDisabled(true);
		setFlag(multi, ComponentModel.HIDE_FLAG, true);
		multi.setMandatory(true);
		multi.setSubmitOnChange(true);
		multi.setToolTip(tooltip);
		multi.setAccessibleText(accessible);
		multi.setRows(rows);
		multi.setMinSelect(1);
		multi.setMaxSelect(2);

		assertSchemaMatch(multi);

		assertXpathEvaluatesTo(multi.getId(), "//ui:listbox/@id", multi);
		assertXpathEvaluatesTo("true", "//ui:listbox/@disabled", multi);
		assertXpathEvaluatesTo("true", "//ui:listbox/@hidden", multi);
		assertXpathEvaluatesTo("true", "//ui:listbox/@required", multi);
		assertXpathEvaluatesTo("true", "//ui:listbox/@submitOnChange", multi);
		assertXpathEvaluatesTo(tooltip, "//ui:listbox/@toolTip", multi);
		assertXpathEvaluatesTo(accessible, "//ui:listbox/@accessibleText", multi);
		assertXpathEvaluatesTo(Integer.toString(rows), "//ui:listbox/@rows", multi);
		assertXpathEvaluatesTo("", "//ui:listbox/@single", multi);
		assertXpathEvaluatesTo("1", "//ui:listbox/@min", multi);
		assertXpathEvaluatesTo("2", "//ui:listbox/@max", multi);
	}

	@Test
	public void testDataList() throws IOException, SAXException, XpathException {
		WMultiSelect multi = new WMultiSelect(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		setActiveContext(createUIContext());

		assertSchemaMatch(multi);

		assertXpathEvaluatesTo("0", "count(//ui:listbox/ui:option)", multi);
		assertXpathEvaluatesTo(multi.getListCacheKey(), "//ui:listbox/@data", multi);

		// Set Selected
		String code = multi.getCode(multi.getOptions().get(0), 0);
		String desc = multi.getDesc(multi.getOptions().get(0), 0);
		multi.setSelected(Arrays.asList(new String[]{code}));

		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:option)", multi);
		assertXpathEvaluatesTo(desc, "//ui:listbox/ui:option[@value='" + code + "']/text()", multi);
		assertXpathEvaluatesTo(multi.getListCacheKey(), "//ui:listbox/@data", multi);
	}

	@Test
	public void testOptionGroups() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WMultiSelect multi = new WMultiSelect(options);
		assertSchemaMatch(multi);
		assertXpathEvaluatesTo("2", "count(//ui:listbox/ui:option)", multi);
		assertXpathEvaluatesTo("1", "count(//ui:listbox/ui:optgroup)", multi);
		assertXpathEvaluatesTo("4", "count(//ui:listbox/ui:optgroup/ui:option)", multi);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:listbox/ui:optgroup/@label", multi);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:listbox/ui:optgroup/ui:option[1]", multi);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:listbox/ui:optgroup/ui:option[2]", multi);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:listbox/ui:optgroup/ui:option[3]", multi);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:listbox/ui:optgroup/ui:option[4]", multi);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:listbox/ui:option[1]/@value", multi);
		assertXpathEvaluatesTo("2", "//ui:listbox/ui:optgroup/ui:option[1]/@value", multi);
		assertXpathEvaluatesTo("3", "//ui:listbox/ui:optgroup/ui:option[2]/@value", multi);
		assertXpathEvaluatesTo("4", "//ui:listbox/ui:optgroup/ui:option[3]/@value", multi);
		assertXpathEvaluatesTo("5", "//ui:listbox/ui:optgroup/ui:option[4]/@value", multi);
		assertXpathEvaluatesTo("6", "//ui:listbox/ui:option[2]/@value", multi);

		// Check selection
		multi.setSelected(Arrays.asList(new String[]{"A", "B.3"}));
		assertXpathEvaluatesTo("2", "count(//ui:option[@selected='true'])", multi);
		assertXpathExists("//ui:listbox/ui:option[text()='A'][@selected='true']", multi);
		assertXpathExists("//ui:listbox/ui:optgroup/ui:option[text()='B.3'][@selected='true']",
				multi);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"),
				Arrays.asList(new String[]{"dummy"}));
		WMultiSelect multi = new WMultiSelect(Arrays.asList(new Object[]{getInvalidCharSequence(),
			getMaliciousContent(), optionGroup}));

		assertSafeContent(multi);

		multi.setToolTip(getMaliciousAttribute("ui:listbox"));
		assertSafeContent(multi);

		multi.setAccessibleText(getMaliciousAttribute("ui:listbox"));
		assertSafeContent(multi);
	}
}
