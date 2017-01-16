package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WDropdown.DropdownType;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDropdownRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDropdownRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WDropdown component = new WDropdown();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WDropdownRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		// Shared options.
		WDropdown drop = new WDropdown();
		drop.setLocked(true);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("0", "count(//ui:dropdown/ui:option)", drop);

		drop.setOptions(new String[]{"A", "B", "C"});
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("3", "count(//ui:dropdown/ui:option)", drop);

		drop.setSubmitOnChange(true);
		assertSchemaMatch(drop);

		// User specific options.
		drop.setLocked(true);
		setActiveContext(createUIContext());

		resetContext();
		drop = new WDropdown(new String[]{"A", "B"});
		assertXpathEvaluatesTo("2", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo(drop.getId(), "//ui:dropdown/@id", drop);
		assertXpathExists("//ui:dropdown[ui:option='A']", drop);
		assertXpathExists("//ui:dropdown[ui:option='B']", drop);
		Assert.assertTrue("Dropdown should be in default state", drop.isDefaultState());

		setActiveContext(createUIContext());
		drop.setOptions(new String[]{"X"});
		assertXpathEvaluatesTo("1", "count(//ui:dropdown/ui:option)", drop);
		assertXpathExists("//ui:dropdown[ui:option='X']", drop);
	}

	@Test
	public void testDoPaintReadOnly() throws IOException, SAXException, XpathException {
		// Shared options.
		WDropdown drop = new WDropdown();
		drop.setOptions(new String[]{"A", "B", "C"});
		setActiveContext(createUIContext());

		// Check Readonly - only render selected option
		drop.setReadOnly(true);
		drop.setSelected("B");
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("true", "//ui:dropdown/@readOnly", drop);
		assertXpathEvaluatesTo("1", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo("1", "count(//ui:dropdown/ui:option[@selected='true'])", drop);
		assertXpathEvaluatesTo("B", "//ui:dropdown/ui:option[@selected='true']", drop);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		WDropdown drop = new WDropdown(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);

		assertSchemaMatch(drop);
		assertXpathEvaluatesTo(drop.getId(), "//ui:dropdown/@id", drop);
		assertXpathEvaluatesTo(drop.getListCacheKey(), "//ui:dropdown/@data", drop);

		assertXpathNotExists("//ui:dropdown/@disabled", drop);
		assertXpathNotExists("//ui:dropdown/@hidden", drop);
		assertXpathNotExists("//ui:dropdown/@required", drop);
		assertXpathNotExists("//ui:dropdown/@readOnly", drop);
		assertXpathNotExists("//ui:dropdown/@submitOnChange", drop);
		assertXpathNotExists("//ui:dropdown/@toolTip", drop);
		assertXpathNotExists("//ui:dropdown/@accessibleText", drop);
		assertXpathNotExists("//ui:dropdown/@optionWidth", drop);
		assertXpathNotExists("//ui:dropdown/@type", drop);

		drop.setDisabled(true);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("true", "//ui:dropdown/@disabled", drop);

		setFlag(drop, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("true", "//ui:dropdown/@hidden", drop);

		drop.setMandatory(true);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("true", "//ui:dropdown/@required", drop);

		drop.setSubmitOnChange(true);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("true", "//ui:dropdown/@submitOnChange", drop);

		drop.setToolTip("tooltip");
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo(drop.getToolTip(), "//ui:dropdown/@toolTip", drop);

		drop.setAccessibleText("accessible");
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo(drop.getAccessibleText(), "//ui:dropdown/@accessibleText", drop);

		drop.setOptionWidth(20);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("20", "//ui:dropdown/@optionWidth", drop);

		drop.setType(DropdownType.COMBO);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("combo", "//ui:dropdown/@type", drop);
	}

	@Test
	public void testCrtEntryExpiration() throws IOException, SAXException, XpathException {
		UIContext uic1 = new UIContextImpl();

		UIContext uic2 = new UIContextImpl() {
			@Override
			public long getCreationTime() {
				return 0; // 01 jan 1970 - should have no options
			}
		};

		// Test getOptions accessor
		WDropdown drop = new WDropdown(TestLookupTable.YesNoTable.class);

		// Test rendered format
		setActiveContext(uic1);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("2", "count(//ui:dropdown/ui:option)", drop);

		setActiveContext(uic2);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("0", "count(//ui:dropdown/ui:option)", drop);
	}

	@Test
	public void testSpecialCharacters() throws IOException, SAXException, XpathException {
		String optionA = "<A";
		String optionB = "B&B";
		String optionC = "C";

		WDropdown drop = new WDropdown(new String[]{optionA, optionB, optionC});
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("3", "count(//ui:dropdown/ui:option)", drop);
	}

	@Test
	public void testNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "A", "B", "C"};

		WDropdown drop = new WDropdown(options);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("4", "count(//ui:dropdown/ui:option)", drop);

		assertXpathEvaluatesTo("", "//ui:dropdown/ui:option[@value='']/text()", drop);

		for (int i = 0; i < options.length; i++) {
			String code = drop.optionToCode(options[i]);
			String option;
			if (i == 0) {
				option = "";  // null
			} else {
				option = options[i];
			}
			assertXpathEvaluatesTo(option, "//ui:dropdown/ui:option[@value='" + code + "']/text()",
					drop);
		}
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WDropdown drop = new WDropdown(options);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("5", "count(//ui:dropdown/ui:option)", drop);

		assertXpathEvaluatesTo("", "//ui:dropdown/ui:option[@value='']/text()", drop);

		for (int i = 0; i < options.length; i++) {
			String code = drop.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("", "//ui:dropdown/ui:option[@value='" + code + "']/text()",
						drop);
				assertXpathEvaluatesTo("true",
						"//ui:dropdown/ui:option[@value='" + code + "']/@isNull", drop);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:dropdown/ui:option[@value='" + code + "']/text()", drop);
				assertXpathEvaluatesTo("", "//ui:dropdown/ui:option[@value='" + code + "']/@isNull",
						drop);
			}
		}
	}

	@Test
	public void testEditableComboOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{"A", "B", "C"};

		WDropdown drop = new WDropdown(options);
		drop.setType(WDropdown.DropdownType.COMBO);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("3", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", drop);
		assertXpathExists("//ui:dropdown/ui:option[text()='A'][@selected='true']", drop);

		// Set a new selection which is not in the list.
		drop.setSelected("D");

		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("4", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", drop);
		assertXpathExists("//ui:dropdown/ui:option[text()='D'][@selected='true']", drop);
	}

	@Test
	public void testDataList() throws IOException, SAXException, XpathException {
		WDropdown drop = new WDropdown(TestLookupTable.CACHEABLE_DAY_OF_WEEK_TABLE);
		assertSchemaMatch(drop);

		String code = drop.getCode(drop.getOptions().get(0), 0);
		String desc = drop.getDesc(drop.getOptions().get(0), 0);

		assertXpathEvaluatesTo("1", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo(desc, "//ui:dropdown/ui:option[@value='" + code + "']/text()", drop);
		assertXpathEvaluatesTo(drop.getListCacheKey(), "//ui:dropdown/@data", drop);
	}

	@Test
	public void testOptionGroups() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WDropdown drop = new WDropdown(options);
		assertSchemaMatch(drop);
		assertXpathEvaluatesTo("2", "count(//ui:dropdown/ui:option)", drop);
		assertXpathEvaluatesTo("1", "count(//ui:dropdown/ui:optgroup)", drop);
		assertXpathEvaluatesTo("4", "count(//ui:dropdown/ui:optgroup/ui:option)", drop);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:dropdown/ui:optgroup/@label", drop);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:dropdown/ui:optgroup/ui:option[1]", drop);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:dropdown/ui:optgroup/ui:option[2]", drop);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:dropdown/ui:optgroup/ui:option[3]", drop);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:dropdown/ui:optgroup/ui:option[4]", drop);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:dropdown/ui:option[1]/@value", drop);
		assertXpathEvaluatesTo("2", "//ui:dropdown/ui:optgroup/ui:option[1]/@value", drop);
		assertXpathEvaluatesTo("3", "//ui:dropdown/ui:optgroup/ui:option[2]/@value", drop);
		assertXpathEvaluatesTo("4", "//ui:dropdown/ui:optgroup/ui:option[3]/@value", drop);
		assertXpathEvaluatesTo("5", "//ui:dropdown/ui:optgroup/ui:option[4]/@value", drop);
		assertXpathEvaluatesTo("6", "//ui:dropdown/ui:option[2]/@value", drop);

		// Check selection
		drop.setSelected("B.3");
		assertXpathEvaluatesTo("1", "count(//ui:option[@selected='true'])", drop);
		assertXpathExists("//ui:dropdown/ui:optgroup/ui:option[text()='B.3'][@selected='true']",
				drop);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"), Arrays.
				asList(new String[]{"dummy"}));
		WDropdown drop = new WDropdown(Arrays.asList(
				new Object[]{getInvalidCharSequence(), getMaliciousContent(), optionGroup}));

		assertSafeContent(drop);

		drop.setToolTip(getMaliciousAttribute("ui:dropdown"));
		assertSafeContent(drop);

		drop.setAccessibleText(getMaliciousAttribute("ui:dropdown"));
		assertSafeContent(drop);
	}
}
