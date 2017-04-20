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
		assertXpathEvaluatesTo("3", "count(//ui:radiobuttonselect/ui:option)", buttonGroup);
		// Check selected
		assertXpathNotExists("//ui:radiobuttonselect/ui:option[@selected='true']", buttonGroup);
		buttonGroup.setSelected("b");
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("1", "count(//ui:radiobuttonselect/ui:option[@selected='true'])", buttonGroup);
		assertXpathEvaluatesTo("b", "//ui:radiobuttonselect/ui:option[@selected='true']", buttonGroup);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WRadioButtonSelect buttonGroup = new WRadioButtonSelect(new String[]{"a", "b", "c"});
		// Check Readonly - only render selected option
		buttonGroup.setReadOnly(true);
		buttonGroup.setSelected("b");
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@readOnly", buttonGroup);
		assertXpathEvaluatesTo("1", "count(//ui:radiobuttonselect/ui:option[@selected='true'])", buttonGroup);
		assertXpathEvaluatesTo("b", "//ui:radiobuttonselect/ui:option[@selected='true']", buttonGroup);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WRadioButtonSelect group = new WRadioButtonSelect();

		// Set ALL Options
		group.setDisabled(true);
		setFlag(group, ComponentModel.HIDE_FLAG, true);
		group.setMandatory(true);
		group.setSubmitOnChange(true);
		group.setToolTip("tip");
		group.setFrameless(true);
		group.setAjaxTarget(new WPanel());
		group.setButtonLayout(WRadioButtonSelect.LAYOUT_COLUMNS);
		group.setButtonColumns(2);

		// Validate ALL Options
		assertSchemaMatch(group);
		assertXpathEvaluatesTo(group.getId(), "//ui:radiobuttonselect/@id", group);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@disabled", group);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@hidden", group);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@required", group);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@submitOnChange", group);
		assertXpathEvaluatesTo("tip", "//ui:radiobuttonselect/@toolTip", group);
		assertXpathEvaluatesTo("true", "//ui:radiobuttonselect/@frameless", group);
		assertXpathEvaluatesTo("column", "//ui:radiobuttonselect/@layout", group);
		assertXpathEvaluatesTo("2", "//ui:radiobuttonselect/@layoutColumnCount", group);
		assertXpathEvaluatesTo(group.getId(), "//ui:ajaxtrigger/@triggerId", group);
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

		group.setToolTip(getMaliciousAttribute("ui:radiobuttonselect"));
		assertSafeContent(group);

		group.setAccessibleText(getMaliciousAttribute("ui:radiobuttonselect"));
		assertSafeContent(group);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WRadioButtonSelect select = new WRadioButtonSelect(options);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("5", "count(//ui:radiobuttonselect/ui:option)", select);

		assertXpathEvaluatesTo("", "//ui:radiobuttonselect/ui:option[@value='']/text()", select);

		for (int i = 0; i < options.length; i++) {
			String code = select.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("",
						"//ui:radiobuttonselect/ui:option[@value='" + code + "']/text()", select);
				assertXpathEvaluatesTo("true",
						"//ui:radiobuttonselect/ui:option[@value='" + code + "']/@isNull", select);
			} else {
				assertXpathEvaluatesTo(option,
						"//ui:radiobuttonselect/ui:option[@value='" + code + "']/text()", select);
				assertXpathEvaluatesTo("",
						"//ui:radiobuttonselect/ui:option[@value='" + code + "']/@isNull", select);
			}
		}
	}

}
