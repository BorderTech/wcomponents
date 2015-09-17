package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WMultiSelectPairRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMultiSelectPairRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WMultiSelectPair multiSelectPair = new WMultiSelectPair();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(multiSelectPair) instanceof WMultiSelectPairRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		final String option1 = "WMultiSelectPairRenderer_Test.testDoPaint.option1";
		final String option2 = "WMultiSelectPairRenderer_Test.testDoPaint.option2";
		final String option3 = "WMultiSelectPairRenderer_Test.testDoPaint.option3";

		// Empty list
		WMultiSelectPair select = new WMultiSelectPair();

		setActiveContext(createUIContext());

		assertSchemaMatch(select);
		assertXpathEvaluatesTo("0", "count(//ui:multiSelectPair/option)", select);
		assertXpathEvaluatesTo(select.getId(), "//ui:multiSelectPair/@id", select);
		assertXpathEvaluatesTo(String.valueOf(select.getRows()), "//ui:multiSelectPair/@size",
				select);
		assertXpathNotExists("//ui:multiSelectPair/@disabled", select);
		assertXpathNotExists("//ui:multiSelectPair/@required", select);
		assertXpathNotExists("//ui:multiSelectPair/@accessibleText", select);
		assertXpathEvaluatesTo(select.getAvailableListName(), "//ui:multiSelectPair/@fromListName",
				select);
		assertXpathEvaluatesTo(select.getSelectedListName(), "//ui:multiSelectPair/@toListName",
				select);

		// List with options
		select.setOptions(new String[]{option1, option2, option3});
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("3", "count(//ui:multiSelectPair/ui:option)", select);
		assertXpathEvaluatesTo("0", "count(//ui:multiSelectPair/ui:option[@selected='true'])",
				select);
		assertXpathEvaluatesTo(option1, "//ui:multiSelectPair/ui:option[1]", select);
		assertXpathEvaluatesTo(option2, "//ui:multiSelectPair/ui:option[2]", select);
		assertXpathEvaluatesTo(option3, "//ui:multiSelectPair/ui:option[3]", select);

		// List with selected options
		select.setSelected(Arrays.asList(new String[]{option2}));
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("3", "count(//ui:multiSelectPair/ui:option)", select);
		assertXpathEvaluatesTo("1", "count(//ui:multiSelectPair/ui:option[@selected='true'])",
				select);
		assertXpathEvaluatesTo(option2, "//ui:multiSelectPair/ui:option[@selected='true']", select);

		// Check Readonly - only render selected option
		select.setReadOnly(true);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@readOnly", select);
		assertXpathEvaluatesTo("1", "count(//ui:multiSelectPair/ui:option)", select);
		assertXpathEvaluatesTo(option2, "//ui:multiSelectPair/ui:option[@selected='true']", select);

		// Required
		select.setMandatory(true);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@required", select);

		// Accessible text
		String accessibleText = "WMultiSelectPairRenderer_Test.testDoPaint.accessibleText";
		select.setAccessibleText(accessibleText);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo(accessibleText, "//ui:multiSelectPair/@accessibleText", select);

		// Disabled list
		select.setDisabled(true);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@disabled", select);

		// Rows - Less than 2 should return default
		select.setRows(1);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo(String.valueOf(WMultiSelectPair.DEFAULT_ROWS),
				"//ui:multiSelectPair/@size", select);
		// Rows - Set valid rows
		select.setRows(10);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo(String.valueOf(10), "//ui:multiSelectPair/@size", select);
	}

	@Test
	public void testDoPaintOptions() throws IOException, SAXException, XpathException {
		WMultiSelectPair select = new WMultiSelectPair(new String[]{"a", "b", "c"});

		select.setRows(3);
		select.setDisabled(true);
		setFlag(select, ComponentModel.HIDE_FLAG, true);
		select.setMandatory(true);
		select.setReadOnly(true);
		select.setShuffle(true);
		select.setAvailableListName("available");
		select.setSelectedListName("selected");
		select.setAccessibleText("accessible text");
		select.setMinSelect(1);
		select.setMaxSelect(2);

		assertSchemaMatch(select);

		assertXpathEvaluatesTo(select.getId(), "//ui:multiSelectPair/@id", select);
		assertXpathEvaluatesTo("3", "//ui:multiSelectPair/@size", select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@disabled", select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@hidden", select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@required", select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@readOnly", select);
		assertXpathEvaluatesTo("true", "//ui:multiSelectPair/@shuffle", select);
		assertXpathEvaluatesTo("available", "//ui:multiSelectPair/@fromListName", select);
		assertXpathEvaluatesTo("selected", "//ui:multiSelectPair/@toListName", select);
		assertXpathEvaluatesTo("accessible text", "//ui:multiSelectPair/@accessibleText", select);
		assertXpathEvaluatesTo("1", "//ui:multiSelectPair/@min", select);
		assertXpathEvaluatesTo("2", "//ui:multiSelectPair/@max", select);
	}

	@Test
	public void testOptionGroups() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WMultiSelectPair select = new WMultiSelectPair(options);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("2", "count(//ui:multiSelectPair/ui:option)", select);
		assertXpathEvaluatesTo("1", "count(//ui:multiSelectPair/ui:optgroup)", select);
		assertXpathEvaluatesTo("4", "count(//ui:multiSelectPair/ui:optgroup/ui:option)", select);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:multiSelectPair/ui:optgroup/@label",
				select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:multiSelectPair/ui:optgroup/ui:option[1]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:multiSelectPair/ui:optgroup/ui:option[2]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:multiSelectPair/ui:optgroup/ui:option[3]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:multiSelectPair/ui:optgroup/ui:option[4]", select);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:multiSelectPair/ui:option[1]/@value", select);
		assertXpathEvaluatesTo("2", "//ui:multiSelectPair/ui:optgroup/ui:option[1]/@value", select);
		assertXpathEvaluatesTo("3", "//ui:multiSelectPair/ui:optgroup/ui:option[2]/@value", select);
		assertXpathEvaluatesTo("4", "//ui:multiSelectPair/ui:optgroup/ui:option[3]/@value", select);
		assertXpathEvaluatesTo("5", "//ui:multiSelectPair/ui:optgroup/ui:option[4]/@value", select);
		assertXpathEvaluatesTo("6", "//ui:multiSelectPair/ui:option[2]/@value", select);

		// Check selection
		select.setSelected(Arrays.asList(new String[]{"A", "B.3"}));
		assertXpathEvaluatesTo("2", "count(//ui:option[@selected='true'])", select);
		assertXpathExists("//ui:multiSelectPair/ui:option[text()='A'][@selected='true']", select);
		assertXpathExists(
				"//ui:multiSelectPair/ui:optgroup/ui:option[text()='B.3'][@selected='true']", select);
	}

	@Test
	public void testOptionGroupsWithShuffle() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("B", Arrays.asList(
				new String[]{"B.1", "B.2", "B.3", "B.4"}));
		Object[] options = new Object[]{"A", optionGroup, "C"};

		WMultiSelectPair select = new WMultiSelectPair(options);
		select.setShuffle(true);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("2", "count(//ui:multiSelectPair/ui:option)", select);
		assertXpathEvaluatesTo("1", "count(//ui:multiSelectPair/ui:optgroup)", select);
		assertXpathEvaluatesTo("4", "count(//ui:multiSelectPair/ui:optgroup/ui:option)", select);

		// Check grouped options
		assertXpathEvaluatesTo(optionGroup.getDesc(), "//ui:multiSelectPair/ui:optgroup/@label",
				select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(0),
				"//ui:multiSelectPair/ui:optgroup/ui:option[1]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(1),
				"//ui:multiSelectPair/ui:optgroup/ui:option[2]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(2),
				"//ui:multiSelectPair/ui:optgroup/ui:option[3]", select);
		assertXpathEvaluatesTo((String) optionGroup.getOptions().get(3),
				"//ui:multiSelectPair/ui:optgroup/ui:option[4]", select);

		// Check values
		assertXpathEvaluatesTo("1", "//ui:multiSelectPair/ui:option[1]/@value", select);
		assertXpathEvaluatesTo("2", "//ui:multiSelectPair/ui:optgroup/ui:option[1]/@value", select);
		assertXpathEvaluatesTo("3", "//ui:multiSelectPair/ui:optgroup/ui:option[2]/@value", select);
		assertXpathEvaluatesTo("4", "//ui:multiSelectPair/ui:optgroup/ui:option[3]/@value", select);
		assertXpathEvaluatesTo("5", "//ui:multiSelectPair/ui:optgroup/ui:option[4]/@value", select);
		assertXpathEvaluatesTo("6", "//ui:multiSelectPair/ui:option[2]/@value", select);

		// Check selection order
		select.setSelected(Arrays.asList(new String[]{"A", "B.3", "B.1"}));
		assertXpathEvaluatesTo("3", "count(//ui:option[@selected='true'])", select);
		assertXpathExists("//ui:multiSelectPair/ui:option[text()='A'][@selected='true']", select);
		assertXpathExists(
				"//ui:multiSelectPair/ui:optgroup/ui:option[text()='B.3'][@selected='true']", select);
		assertXpathExists(
				"//ui:multiSelectPair/ui:optgroup/ui:option[text()='B.1'][@selected='true']", select);
		assertXpathEvaluatesTo("A", "//ui:multiSelectPair/ui:option[@selected='true']/text()",
				select);
		assertXpathEvaluatesTo("B.3",
				"//ui:multiSelectPair/ui:optgroup[1]/ui:option[@selected='true'][1]/text()", select);
		assertXpathEvaluatesTo("B.1",
				"//ui:multiSelectPair/ui:optgroup[1]/ui:option[@selected='true'][2]/text()", select);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup(getMaliciousAttribute("ui:optgroup"), Arrays.
				asList(new String[]{"dummy"}));
		WMultiSelectPair select = new WMultiSelectPair(Arrays.asList(
				new Object[]{getInvalidCharSequence(), getMaliciousContent(), optionGroup}));
		select.setAvailableListName(getMaliciousAttribute("ui:multiSelectPair"));
		select.setSelectedListName(getMaliciousAttribute("ui:multiSelectPair"));

		assertSafeContent(select);

		select.setToolTip(getMaliciousAttribute("ui:multiSelectPair"));
		assertSafeContent(select);

		select.setAccessibleText(getMaliciousAttribute("ui:multiSelectPair"));
		assertSafeContent(select);
	}
}
