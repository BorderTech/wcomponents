package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.ComponentModel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WContainer;
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
 * @author Jonathan Austin
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

		// Validate Schema
		assertSchemaMatch(button);
		assertXpathExists("//ui:button[@id]", button);
		assertXpathEvaluatesTo("Basic", "//ui:button", button);
		assertXpathNotExists("//ui:button[@type]", button);
		assertXpathNotExists("//ui:button[@disabled]", button);
		assertXpathNotExists("//ui:button[@hidden]", button);
		assertXpathNotExists("//ui:button[@toolTip]", button);
		assertXpathNotExists("//ui:button[@imageUrl]", button);
		assertXpathNotExists("//ui:button[@imagePosition]", button);
		assertXpathNotExists("//ui:button[@accessKey]", button);
		assertXpathNotExists("//ui:button[@ajax]", button);
		assertXpathNotExists("//ui:button[@validates]", button);
		assertXpathNotExists("//ui:button[@popup]", button);
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

		// Validate Schema
		assertSchemaMatch(button);

		assertXpathExists("//ui:button[@id]", button);
		assertXpathEvaluatesTo("link", "//ui:button/@type", button);
		assertXpathEvaluatesTo(button.getText(), "//ui:button", button);
		assertXpathEvaluatesTo("true", "//ui:button/@disabled", button);
		assertXpathEvaluatesTo("true", "//ui:button/@hidden", button);
		assertXpathEvaluatesTo(button.getToolTip(), "//ui:button/@toolTip", button);
		assertXpathEvaluatesTo(button.getImageUrl(), "//ui:button/@imageUrl", button);
		assertXpathEvaluatesTo("e", "//ui:button/@imagePosition", button);
		assertXpathEvaluatesTo(button.getAccessKeyAsString(), "//ui:button/@accessKey", button);
		assertXpathEvaluatesTo("true", "//ui:button/@popup", button);
		assertXpathEvaluatesTo(validationComponent.getId(), "//ui:button/@validates", button);
		assertXpathEvaluatesTo(button.getId(), "//ui:ajaxtrigger/@triggerId", button);

		button.setImagePosition(ImagePosition.NORTH);
		assertXpathEvaluatesTo("n", "//ui:button/@imagePosition", button);

		button.setImagePosition(ImagePosition.SOUTH);
		assertXpathEvaluatesTo("s", "//ui:button/@imagePosition", button);

		button.setImagePosition(ImagePosition.WEST);
		assertXpathEvaluatesTo("w", "//ui:button/@imagePosition", button);
	}

	@Test
	public void testXssEscaping() throws IOException, SAXException, XpathException {
		WButton button = new WButton(getMaliciousContent());

		assertSafeContent(button);

		button.setToolTip(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);

		button.setAccessibleText(getMaliciousAttribute("ui:button"));
		assertSafeContent(button);
	}
}
