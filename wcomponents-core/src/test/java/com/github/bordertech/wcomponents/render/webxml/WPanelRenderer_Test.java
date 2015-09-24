package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.AjaxOperation;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPanel.PanelMode;
import com.github.bordertech.wcomponents.WText;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WPanelRenderer}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WPanelRenderer_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testRendererCorrectlyConfigured() {
		WPanel panel = new WPanel();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(panel) instanceof WPanelRenderer);
	}

	@Test
	public void testRenderedFormatNoButton() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		assertSchemaMatch(panel);
		assertXpathNotExists("//ui:panel/@buttonId", panel);
	}

	@Test
	public void testRenderedFormatWithButton() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		WButton button = new WButton("submit");
		panel.add(button);
		panel.setDefaultSubmitButton(button);

		assertSchemaMatch(panel);
		assertXpathEvaluatesTo(button.getId(), "//ui:panel/@buttonId", panel);
	}

	@Test
	public void testRenderedLazyModePanel() throws IOException, SAXException, XpathException {
		String content = "TEST CONTENT";

		WPanel panel = new WPanel();
		panel.setMode(PanelMode.LAZY);
		panel.add(new WText(content));

		// Content NOT Hidden
		assertSchemaMatch(panel);
		// If not hidden, then the panel's content should be rendered
		assertXpathEvaluatesTo("", "//ui:panel/@type", panel);
		assertXpathEvaluatesTo("", "//ui:panel/@hidden", panel);
		assertXpathEvaluatesTo("lazy", "//ui:panel/@mode", panel);
		assertXpathEvaluatesTo(content, "//ui:panel/ui:content", panel);

		// Content Hidden
		// Create User Context with UI component
		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);
		setFlag(panel, ComponentModel.HIDE_FLAG, true);
		assertSchemaMatch(panel);
		// If hidden, then the panel's content should NOT be rendered
		assertXpathEvaluatesTo("", "//ui:panel/@type", panel);
		assertXpathEvaluatesTo("true", "//ui:panel/@hidden", panel);
		assertXpathEvaluatesTo("lazy", "//ui:panel/@mode", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:content", panel);
	}

	@Test
	public void testRenderedEagerModePanel() throws IOException, SAXException, XpathException {

		String content = "TEST CONTENT";

		WPanel panel = new WPanel();
		panel.setMode(PanelMode.EAGER);
		panel.add(new WText(content));

		UIContext uic = createUIContext();
		uic.setUI(new DefaultWComponent());
		setActiveContext(uic);

		// The panel's content should NOT be rendered
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("", "//ui:panel/@type", panel);
		assertXpathEvaluatesTo("", "//ui:panel/@hidden", panel);
		assertXpathEvaluatesTo("eager", "//ui:panel/@mode", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:content", panel);

		try {
			// Panel is the AJAX Trigger, content should be rendered
			AjaxOperation operation = new AjaxOperation(panel.getId(), panel.getId());
			AjaxHelper.setCurrentOperationDetails(operation, null);
			assertSchemaMatch(panel);
			assertXpathEvaluatesTo("", "//ui:panel/@type", panel);
			assertXpathEvaluatesTo("", "//ui:panel/@hidden", panel);
			assertXpathEvaluatesTo("eager", "//ui:panel/@mode", panel);
			assertXpathEvaluatesTo(content, "//ui:panel/ui:content", panel);
		} finally {
			AjaxHelper.clearCurrentOperationDetails();
		}

	}

	@Test
	public void testRenderedFormatWithAccessKey() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		assertXpathNotExists("//ui:panel/@accessKey", panel);
		panel.setAccessKey('x');

		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("X", "//ui:panel/@accessKey", panel);
	}

	@Test
	public void testRenderAllPanelTypes() throws IOException, SAXException, XpathException {
		// Tests that all panel types are schema valid
		for (WPanel.Type type : WPanel.Type.values()) {
			WPanel panel = new WPanel(type);
			WButton button = new WButton("submit");
			panel.add(button);
			panel.setDefaultSubmitButton(button);
			panel.setTitleText("Panel title");

			assertSchemaMatch(panel);
		}
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		panel.setTitleText(getMaliciousAttribute("ui:panel"));
		assertSafeContent(panel);
	}

	@Test
	public void testRenderedWithMargins() throws IOException, SAXException, XpathException {
		WPanel panel = new WPanel();
		assertXpathNotExists("//ui:panel/ui:margin", panel);

		Margin margin = new Margin(0);
		panel.setMargin(margin);
		assertXpathNotExists("//ui:panel/ui:margin", panel);

		margin = new Margin(1);
		panel.setMargin(margin);
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:margin/@all", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@north", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@east", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@south", panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@west", panel);

		margin = new Margin(0, 0, 0, 0);
		panel.setMargin(margin);
		assertXpathNotExists("//ui:panel/ui:margin", panel);

		margin = new Margin(1, 0, 0, 0);
		panel.setMargin(margin);
		assertXpathExists("//ui:panel/ui:margin", panel);

		margin = new Margin(0, 1, 0, 0);
		panel.setMargin(margin);
		assertXpathExists("//ui:panel/ui:margin", panel);

		margin = new Margin(0, 0, 1, 0);
		panel.setMargin(margin);
		assertXpathExists("//ui:panel/ui:margin", panel);

		margin = new Margin(0, 0, 0, 1);
		panel.setMargin(margin);
		assertXpathExists("//ui:panel/ui:margin", panel);

		margin = new Margin(1, 2, 3, 4);
		panel.setMargin(margin);
		assertSchemaMatch(panel);
		assertXpathEvaluatesTo("", "//ui:panel/ui:margin/@all", panel);
		assertXpathEvaluatesTo("1", "//ui:panel/ui:margin/@north", panel);
		assertXpathEvaluatesTo("2", "//ui:panel/ui:margin/@east", panel);
		assertXpathEvaluatesTo("3", "//ui:panel/ui:margin/@south", panel);
		assertXpathEvaluatesTo("4", "//ui:panel/ui:margin/@west", panel);
	}

}
