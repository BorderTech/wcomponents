package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AbstractInput;
import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMultiDropdown;
import com.github.bordertech.wcomponents.WMultiFileWidget;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WMultiTextField;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WTextArea;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WLabelRenderer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WLabelRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WLabel label = new WLabel();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(label) instanceof WLabelRenderer);
	}

	@Test
	public void testDoPaintBasic() throws IOException, SAXException, XpathException {
		WLabel label = new WLabel();

		// Validate Schema
		assertSchemaMatch(label);
		// Check Attributes
		assertXpathEvaluatesTo(label.getId(), "//ui:label/@id", label);
		assertXpathEvaluatesTo("", "//ui:label/@for", label);
		assertXpathEvaluatesTo("", "//ui:label/@hint", label);
		assertXpathEvaluatesTo("", "//ui:label/@required", label);
		assertXpathEvaluatesTo("", "//ui:label/@accessKey", label);
		assertXpathEvaluatesTo("", "//ui:label/@what", label);
		assertXpathEvaluatesTo("", "//ui:label/@readonly", label);
		assertXpathEvaluatesTo("", "//ui:label/@required", label);
		assertXpathEvaluatesTo("", "//ui:label/@hiddencomponent", label);
		// Check Label
		assertXpathEvaluatesTo("", "//ui:label", label);
	}

	@Test
	public void testDoPaintAllOptions() throws IOException, SAXException, XpathException {
		WTextArea text = new WTextArea();
		text.setText("text1");
		WLabel label = new WLabel();
		label.setForComponent(text);
		label.setHint("hint1");
		label.setAccessKey('A');
		label.setText("label1");

		WContainer root = new WContainer();
		root.add(label);
		root.add(text);

		// Validate Schema
		assertSchemaMatch(root);
		// Check Attributes
		assertXpathEvaluatesTo(label.getId(), "//ui:label/@id", label);
		assertXpathEvaluatesTo(text.getId(), "//ui:label/@for", label);
		assertXpathEvaluatesTo("hint1", "//ui:label/@hint", label);
		assertXpathEvaluatesTo("A", "//ui:label/@accessKey", label);
		assertXpathEvaluatesTo("input", "//ui:label/@what", label);
		// Check Label
		assertXpathEvaluatesTo("label1", "//ui:label", label);

		// Add Children to Label
		WTextArea text2 = new WTextArea();
		text2.setText("text2");
		label.add(text2);
		assertSchemaMatch(root);
		assertXpathEvaluatesTo("text2", "//ui:label/ui:textarea", label);
	}

	@Test
	public void testWhatForInput() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("input", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForNotAnInput() throws IOException, SAXException, XpathException {
		MyComponent comp = new MyComponent();
		WLabel label = new WLabel("label", comp);
		assertXpathEvaluatesTo("", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup1() throws IOException, SAXException, XpathException {
		WCheckBoxSelect comp = new WCheckBoxSelect();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup2() throws IOException, SAXException, XpathException {
		WMultiDropdown comp = new WMultiDropdown();
		WLabel label = new WLabel("label", comp);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup3() throws IOException, SAXException, XpathException {
		WMultiTextField comp = new WMultiTextField();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup4() throws IOException, SAXException, XpathException {
		WMultiSelectPair comp = new WMultiSelectPair();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup5() throws IOException, SAXException, XpathException {
		WRadioButtonSelect comp = new WRadioButtonSelect();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup6() throws IOException, SAXException, XpathException {
		RadioButtonGroup comp = new RadioButtonGroup();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroup7() throws IOException, SAXException, XpathException {
		WMultiFileWidget comp = new WMultiFileWidget();
		WLabel label = new WLabel("label", comp);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testWhatForGroupWFieldSet() throws IOException, SAXException, XpathException {
		WFieldSet comp = new WFieldSet("legend");
		WLabel label = new WLabel("label", comp);
		assertXpathEvaluatesTo("group", "//ui:label/@what", label);
	}

	@Test
	public void testReadOnlyWithInput() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		comp.setReadOnly(true);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("true", "//ui:label/@readonly", label);
	}

	@Test
	public void testMandatoryWithInput() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		comp.setMandatory(true);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("true", "//ui:label/@required", label);
	}

	@Test
	public void testHiddenComponentWithInput() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		comp.setHidden(true);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("true", "//ui:label/@hiddencomponent", label);
	}

	@Test
	public void testHidden() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		label.setHidden(true);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("", "//ui:label/@hiddencomponent", label);
		assertXpathEvaluatesTo("true", "//ui:label/@hidden", label);
	}

	@Test
	public void testHiddenBoth() throws IOException, SAXException, XpathException {
		MyInput comp = new MyInput();
		WLabel label = new WLabel("label", comp);
		label.setHidden(true);
		comp.setHidden(true);
		assertSchemaMatch(label);
		assertXpathEvaluatesTo("true", "//ui:label/@hidden", label);
		assertXpathEvaluatesTo("true", "//ui:label/@hiddencomponent", label);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WLabel label = new WLabel(getMaliciousContent());
		assertSafeContent(label);
		label.setHint(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);
		label.setToolTip(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);
		label.setAccessibleText(getMaliciousAttribute("ui:label"));
		assertSafeContent(label);
	}

	/**
	 * The simplest possible WComponent.
	 */
	private class MyComponent extends AbstractWComponent {
		// nuthin' to see here
	}

	/**
	 * A simple input used for testing aspects of WLabel which are dependant on the labeled control's states.
	 */
	private class MyInput extends AbstractInput {
		@Override
		protected boolean doHandleRequest(final Request request) {
			return false; // never changes
		}

		@Override
		public Object getRequestValue(final Request request) {
			return null; // never has a value
		}

	}
}
