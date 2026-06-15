package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.OptionGroup;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
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

	private static final String TAG_RADIO_BUTTON_SELECT = "wc-radio-button-select";
	private static final String TAG_OPTION = "wc-option";

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
		assertXpathEvaluatesTo("3", String.format("count(//html:%s/html:%s)", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
		// Check selected
		assertXpathNotExists(String.format("//html:%s/html:%s[@selected='true']", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
		buttonGroup.setSelected("b");
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s[@selected='true'])", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
		assertXpathEvaluatesTo("b", String.format("//html:%s/html:%s[@selected='true']", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
	}

	@Test
	public void testReadOnly() throws IOException, SAXException, XpathException {
		WRadioButtonSelect buttonGroup = new WRadioButtonSelect(new String[]{"a", "b", "c"});
		// Check Readonly - only render selected option
		buttonGroup.setReadOnly(true);
		buttonGroup.setSelected("b");
		assertSchemaMatch(buttonGroup);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@readOnly", TAG_RADIO_BUTTON_SELECT), buttonGroup);
		assertXpathEvaluatesTo("1", String.format("count(//html:%s/html:%s[@selected='true'])", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
		assertXpathEvaluatesTo("b", String.format("//html:%s/html:%s[@selected='true']", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), buttonGroup);
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
		assertXpathEvaluatesTo(group.getId(), String.format("//html:%s/@id", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@disabled", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@hidden", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@required", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@submitOnChange", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("tip", String.format("//html:%s/@toolTip", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("true", String.format("//html:%s/@frameless", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("column", String.format("//html:%s/@layout", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo("2", String.format("//html:%s/@layoutColumnCount", TAG_RADIO_BUTTON_SELECT), group);
		assertXpathEvaluatesTo(group.getId(), "//html:" + WAjaxControlRenderer.WC_AJAXTRIGGER + "/@triggerId", group);
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

		group.setToolTip(getMaliciousAttribute("html:" + TAG_RADIO_BUTTON_SELECT));
		assertSafeContent(group);

		group.setAccessibleText(getMaliciousAttribute("html:" + TAG_RADIO_BUTTON_SELECT));
		assertSafeContent(group);
	}

	@Test
	public void testIsNullOption() throws IOException, SAXException, XpathException {
		String[] options = new String[]{null, "", "A", "B", "C"};

		WRadioButtonSelect select = new WRadioButtonSelect(options);
		assertSchemaMatch(select);
		assertXpathEvaluatesTo("5", String.format("count(//html:%s/html:%s)", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), select);

		assertXpathEvaluatesTo("", String.format("//html:%s/html:%s[@value='']/text()", TAG_RADIO_BUTTON_SELECT, TAG_OPTION), select);

		for (int i = 0; i < options.length; i++) {
			String code = select.optionToCode(options[i]);
			String option = options[i];
			if (option == null || option.equals("")) {
				assertXpathEvaluatesTo("",
						String.format("//html:%s/html:%s[@value='%s']/text()", TAG_RADIO_BUTTON_SELECT, TAG_OPTION, code), select);
				assertXpathEvaluatesTo("true",
						String.format("//html:%s/html:%s[@value='%s']/@isNull", TAG_RADIO_BUTTON_SELECT, TAG_OPTION, code), select);
			} else {
				assertXpathEvaluatesTo(option,
						String.format("//html:%s/html:%s[@value='%s']/text()", TAG_RADIO_BUTTON_SELECT, TAG_OPTION, code), select);
				assertXpathEvaluatesTo("",
						String.format("//html:%s/html:%s[@value='%s']/@isNull", TAG_RADIO_BUTTON_SELECT, TAG_OPTION, code), select);
			}
		}
	}

}
