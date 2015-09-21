package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WRadioButtonSelectRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRadioButtonSelectRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WRadioButtonSelect component = new WRadioButtonSelect();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WRadioButtonSelectRenderer);
	}

	@Test
	public void testDoPaint() throws IOException, SAXException, XpathException {
		WRadioButtonSelect buttonGroup = new WRadioButtonSelect(new String[]{"a", "b", "c"});
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("3", "count(//ui:radioButtonSelect/ui:option)", buttonGroup);

		// Check selected
		assertXpathNotExists("//ui:radioButtonSelect/ui:option[@selected='true']", buttonGroup);

		buttonGroup.setSelected("b");
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("1", "count(//ui:radioButtonSelect/ui:option[@selected='true'])",
				buttonGroup);
		assertXpathEvaluatesTo("b", "//ui:radioButtonSelect/ui:option[@selected='true']",
				buttonGroup);

		// Check Readonly - only render selected option
		buttonGroup.setReadOnly(true);
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@readOnly", buttonGroup);
		assertXpathEvaluatesTo("1", "count(//ui:radioButtonSelect/ui:option[@selected='true'])",
				buttonGroup);
		assertXpathEvaluatesTo("b", "//ui:radioButtonSelect/ui:option[@selected='true']",
				buttonGroup);

	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WRadioButtonSelect group = new WRadioButtonSelect();

		// Set ALL Options
		group.setDisabled(true);
		setFlag(group, ComponentModel.HIDE_FLAG, true);
		group.setMandatory(true);
		group.setReadOnly(true);
		group.setSubmitOnChange(true);
		group.setToolTip("tip");
		group.setFrameless(true);
		group.setAjaxTarget(new WPanel());
		group.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		group.setButtonColumns(2);

		// Validate ALL Options
		assertSchemaMatch(group);
		assertXpathEvaluatesTo(group.getId(), "//ui:radioButtonSelect/@id", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@disabled", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@hidden", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@required", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@readOnly", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@submitOnChange", group);
		assertXpathEvaluatesTo("tip", "//ui:radioButtonSelect/@toolTip", group);
		assertXpathEvaluatesTo("true", "//ui:radioButtonSelect/@frameless", group);
		assertXpathEvaluatesTo("column", "//ui:radioButtonSelect/@layout", group);
		assertXpathEvaluatesTo("2", "//ui:radioButtonSelect/@layoutColumnCount", group);
		assertXpathEvaluatesTo(group.getId(), "//ui:ajaxTrigger/@triggerId", group);
	}

	@Test(expected = SystemException.class)
	public void testOptGroupException() throws IOException, SAXException, XpathException {
		OptionGroup optionGroup = new OptionGroup("Test", Arrays.asList(new String[]{"A", "B"}));
		WRadioButtonSelect group = new WRadioButtonSelect(Arrays.asList(
				new Object[]{"X", optionGroup}));
		assertSchemaMatch(group);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WRadioButtonSelect group = new WRadioButtonSelect(Arrays.asList(
				new Object[]{getInvalidCharSequence(), getMaliciousContent()}));

		assertSafeContent(group);

		group.setToolTip(getMaliciousAttribute("ui:radioButtonSelect"));
		assertSafeContent(group);

		group.setAccessibleText(getMaliciousAttribute("ui:radioButtonSelect"));
		assertSafeContent(group);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WRadioButtonSelect select = new WRadioButtonSelect(options);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("5", "count(//ui:radioButtonSelect/ui:option)", select);

		assertXpathEvaluatesTo("", "//ui:radioButtonSelect/ui:option[@value='']/text()", select);

		for (int i = 0; i < options.length; i++) {
			String code = select.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("",
						"//ui:radioButtonSelect/ui:option[@value='" + code + "']/text()", select);
				assertXpathEvaluatesTo("true",
						"//ui:radioButtonSelect/ui:option[@value='" + code + "']/@isNull", select);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:radioButtonSelect/ui:option[@value='" + code + "']/text()", select);
				assertXpathEvaluatesTo("",
						"//ui:radioButtonSelect/ui:option[@value='" + code + "']/@isNull", select);
			}
		}
	}

}
