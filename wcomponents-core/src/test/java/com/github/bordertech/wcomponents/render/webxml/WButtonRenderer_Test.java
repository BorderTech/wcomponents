package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.io.IOException;
import junit.framework.Assert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Junit test case for {@link WButtonRenderer}.
 *
 * @author Jonathan Austin, Mark Reeves
 * @since 1.0.0
 */
public class WButtonRenderer_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Test the Layout is correctly configured.
	 */
	@Test
	public void testRendererCorrectlyConfigured() {
		WButton component = new WButton();
		Assert.assertTrue("Incorrect renderer supplied",
				getWebXmlRenderer(component) instanceof WButtonRenderer);
	}

	@Test(expected = SystemException.class)
	public void testInvalid() {
		// A button with no text and no image is invalid, will produce an exception
		render(new WButton());
	}

	@Test
	public void testBasic() throws IOException, SAXException, XpathException {
		WButton button = new WButton("Basic");

		assertXpathExists("//html:button[@id]", button);
		assertXpathEvaluatesTo("Basic", "//html:button", button);
		assertXpathNotExists("//html:button[@disabled]", button);
		assertXpathNotExists("//html:button[@hidden]", button);
		assertXpathNotExists("//html:button[@title]", button);
		assertXpathNotExists("//html:button[//html:img]", button);
		assertXpathNotExists("//html:button[@accesskey]", button);
		assertXpathNotExists("//html:button[following-sibling::ui:ajaxcontrol]", button);
		assertXpathNotExists("//html:button[@data-wc-validate]", button);
		assertXpathNotExists("//html:button[@aria-haspopup]", button);
		assertXpathNotExists("//html:button[@type='button']", button);
	}

	@Test
	public void testAllOptions() throws IOException, SAXException, XpathException {
		WButton button = new WButton("All");
		button.setDisabled(true);
		setFlag(button, ComponentModel.HIDE_FLAG, true);
		button.setToolTip("Title");
		button.setAccessKey('T');
		button.setImageUrl("http://localhost/image.png");
		button.setImagePosition(ImagePosition.EAST);
		button.setRenderAsLink(true);
		button.setAjaxTarget(new WTextField());
		button.setPopupTrigger(true);
		setActiveContext(createUIContext());

		WPanel validationComponent = new WPanel();
		button.setAction(new ValidatingAction(new WValidationErrors(), validationComponent) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Do nothing
			}
		});

		WContainer root = new WContainer();
		root.add(button);
		root.add(validationComponent);

		assertXpathExists("//html:button[@id]", button);
		assertXpathExists("//html:button[contains(@class, 'wc-linkbutton')]", button);
		assertXpathEvaluatesTo(button.getText(), "//html:button", button);
		assertXpathEvaluatesTo("disabled", "//html:button/@disabled", button);
		assertXpathEvaluatesTo("hidden", "//html:button/@hidden", button);
		assertXpathEvaluatesTo(button.getToolTip(), "//html:button/@title", button);
		assertXpathUrlEvaluatesTo(button.getImageUrl(), "//html:button//html:img/@src", button);
		assertXpathExists("//html:button/html:span[contains(@class, 'wc_btn_imge')]", button);
		assertXpathEvaluatesTo(button.getAccessKeyAsString(), "//html:button/@accesskey", button);
		assertXpathEvaluatesTo("true", "//html:button/@aria-haspopup", button);
		assertXpathEvaluatesTo(validationComponent.getId(), "//html:button/@data-wc-validate", button);
		assertXpathEvaluatesTo(button.getId(), "//ui:ajaxtrigger/@triggerId", button);

		button.setImagePosition(ImagePosition.NORTH);
		assertXpathExists("//html:button/html:span[contains(@class, 'wc_btn_imgn')]", button);

		button.setImagePosition(ImagePosition.SOUTH);
		assertXpathExists("//html:button/html:span[contains(@class, 'wc_btn_imgs')]", button);

		button.setImagePosition(ImagePosition.WEST);
		assertXpathExists("//html:button/html:span[contains(@class, 'wc_btn_imgw')]", button);

		button.setClientCommandOnly(true);
		assertXpathEvaluatesTo("button", "//html:button/@type", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WButton button = new WButton(getMaliciousContent());

		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute("html:button"));
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute("html:button"));
		assertSafeContent(button);

		button.setImageUrl(getMaliciousAttribute());
		assertSafeContent(button);
	}

	@Test
	public void testButtonImageToolTipRender() throws IOException, SAXException, XpathException {
		WButton button = new WButton();
		String expected = "alt text";
		WImage buttonImage = new WImage("http://localhost/image.png", expected);
		button.setImage(buttonImage.getImage());
		assertXpathEvaluatesTo(expected, "//html:button/@title", button);
	}
}
