package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDialog;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WDialogRenderer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class WDialogRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test title.
	 */
	public static final String TEST_TITLE = "This is the title";

	@Test
	public void testRendererCorrectlyConfigured() {
		WDialog component = new WDialog();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WDialogRenderer);
	}

	@Test
	public void testSchemaMatch() throws IOException, SAXException {
		WDialog dialog = new WDialog();
		dialog.setTitle(TEST_TITLE);
		dialog.setMode(WDialog.MODAL);

		setActiveContext(createUIContext());
		dialog.display();

		assertSchemaMatch(dialog);
	}

	@Test
	public void testDoRender() throws IOException, SAXException, XpathException {
		WDialog dialog = new WDialog();
		dialog.setTitle(TEST_TITLE);
		dialog.setMode(WDialog.MODELESS);
		dialog.setWidth(0);
		dialog.setHeight(0);
		dialog.setResizable(false);
		dialog.setLocked(true);

		String xml = renderDialog(dialog);
		assertXpathEvaluatesTo(dialog.getId(), "//ui:dialog/@id", xml);
		assertXpathNotExists("//ui:dialog/@resizable", xml);
		assertXpathNotExists("//ui:dialog/@modal", xml);
		assertXpathEvaluatesTo(TEST_TITLE, "//ui:dialog/@title", xml);
		assertXpathNotExists("//ui:dialog/@width", xml);
		assertXpathNotExists("//ui:dialog/@height", xml);

		int width = 123;
		int height = 456;
		dialog.setWidth(width);
		dialog.setHeight(height);
		xml = renderDialog(dialog);
		assertXpathEvaluatesTo(String.valueOf(width), "//ui:dialog/@width", xml);
		assertXpathEvaluatesTo(String.valueOf(height), "//ui:dialog/@height", xml);

		dialog.setMode(WDialog.MODAL);
		xml = renderDialog(dialog);
		assertXpathEvaluatesTo("true", "//ui:dialog/@modal", xml);

		dialog.setResizable(true);
		xml = renderDialog(dialog);
		assertXpathEvaluatesTo("true", "//ui:dialog/@resizable", xml);
	}

	@Test
	public void testRenderTrigger() throws IOException, SAXException, XpathException {
		WButton trigger = new WButton("Launch dialog");
		WButton content = new WButton("Dialog content");

		WDialog dialog = new WDialog(content, trigger);
		assertXpathExists("//ui:dialog", dialog);
		assertXpathEvaluatesTo(dialog.getId(), "//ui:dialog/@id", dialog);
		assertXpathNotExists("//ui:dialog/@open", dialog);
		assertXpathEvaluatesTo(trigger.getId(), "//ui:dialog/ui:button/@id", dialog);
	}

	/**
	 * Renders the dialog.
	 *
	 * @param dialog the dialog to render.
	 * @return the rendered XML.
	 */
	private String renderDialog(final WDialog dialog) {
		setActiveContext(createUIContext());
		dialog.display();
		String xml = wrapXHtml(render(dialog));
		resetContext();

		return xml;
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WDialog dialog = new WDialog();
		dialog.setTitle(TEST_TITLE);
		dialog.setMode(WDialog.MODAL);

		setActiveContext(createUIContext());
		dialog.setTitle(getMaliciousAttribute());
		dialog.display();

		assertSafeContent(dialog);
	}
}
