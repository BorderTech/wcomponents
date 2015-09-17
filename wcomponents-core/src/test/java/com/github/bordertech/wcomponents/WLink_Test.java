package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WLink.WindowAttributes;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WLink_Test - unit tests for {@link WLink}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WLink_Test extends AbstractWComponentTestCase {

	/**
	 * test text.
	 */
	private static final String TEST_TEXT = "WLink_Test.testText";

	/**
	 * test url.
	 */
	private static final String TEST_URL = "http://localhost/WLink_Test.url";

	/**
	 * test window name.
	 */
	private static final String TEST_WINDOW_NAME = "WLink_Test.WindowName";

	/**
	 * test window name - alt.
	 */
	private static final String TEST_WINDOW_NAME2 = "WLink_Test.WindowName2";

	@Test
	public void testConstructors() {
		final String text = "WLink_Test.testConstructors";
		final String url = "http://localhost/WLink_Test.url";

		WLink link = new WLink();
		setActiveContext(createUIContext());
		Assert.assertNull("Text should not be set", link.getText());
		Assert.assertNull("Url should not be set", link.getUrl());
		Assert.assertTrue("Link should be in default state", link.isDefaultState());
		Assert.assertFalse("Link should not render as button by default", link.isRenderAsButton());

		link = new WLink(text, url);
		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect link text", text, link.getText());
		Assert.assertEquals("Incorrect url", url, link.getUrl());
		Assert.assertTrue("Link should be in default state", link.isDefaultState());
		Assert.assertFalse("Link should not render as button by default", link.isRenderAsButton());
	}

	@Test
	public void testConstructorBuilderEmpty() {
		WLink.Builder builder = new WLink.Builder();
		WLink link = builder.build();

		Assert.assertNull("text should not be set", link.getText());
		Assert.assertNull("url should not be set", link.getUrl());
	}

	@Test
	public void testConstructorBuilderSetAllProperties() {
		final int width = 100;
		final int height = 120;
		final int left = 30;
		final int top = 20;

		WLink.Builder builder = new WLink.Builder(TEST_TEXT, TEST_URL);
		builder.windowName(TEST_WINDOW_NAME);
		builder.width(width);
		builder.height(height);
		builder.resizable(true);
		builder.scrollbars(true);
		builder.toolbar(true);
		builder.location(true);
		builder.directories(true);
		builder.status(true);
		builder.menubar(true);
		builder.left(left);
		builder.top(top);

		WLink link = builder.build();
		Assert.assertEquals("Incorrect link text", TEST_TEXT, link.getText());
		Assert.assertEquals("Incorrect url", TEST_URL, link.getUrl());

		WindowAttributes attributes = link.getWindowAttrs();
		Assert.assertEquals("incorrect attribute windowName", TEST_WINDOW_NAME, attributes.
				getWindowName());
		Assert.assertEquals("incorrect attribute width", width, attributes.getWidth());
		Assert.assertEquals("incorrect attribute height", height, attributes.getHeight());
		Assert.assertTrue("incorrect attribute resizable", attributes.isResizable());
		Assert.assertTrue("incorrect attribute scrollbars", attributes.isScrollbars());
		Assert.assertTrue("incorrect attribute toolbar", attributes.isToolbars());
		Assert.assertTrue("incorrect attribute location", attributes.isLocation());
		Assert.assertTrue("incorrect attribute directories", attributes.isDirectories());
		Assert.assertTrue("incorrect attribute status", attributes.isStatus());
		Assert.assertTrue("incorrect attribute menubar", attributes.isMenubar());
		Assert.assertEquals("incorrect attribute left", left, attributes.getLeft());
		Assert.assertEquals("incorrect attribute top", top, attributes.getTop());
	}

	@Test
	public void testConstructorBuilderUnSetAllBooleanProperties() {
		WLink.Builder builder = new WLink.Builder(TEST_TEXT, TEST_URL);
		builder.resizable(false);
		builder.scrollbars(false);
		builder.toolbar(false);
		builder.location(false);
		builder.directories(false);
		builder.status(false);
		builder.menubar(false);

		WLink link = builder.build();

		WindowAttributes attributes = link.getWindowAttrs();
		Assert.assertFalse("incorrect attribute resizable", attributes.isResizable());
		Assert.assertFalse("incorrect attribute scrollbars", attributes.isScrollbars());
		Assert.assertFalse("incorrect attribute toolbar", attributes.isToolbars());
		Assert.assertFalse("incorrect attribute location", attributes.isLocation());
		Assert.assertFalse("incorrect attribute directories", attributes.isDirectories());
		Assert.assertFalse("incorrect attribute status", attributes.isStatus());
		Assert.assertFalse("incorrect attribute menubar", attributes.isMenubar());
	}

	@Test
	public void testSetText() {
		final String sharedText = "WLink_Test.sharedText";
		final String text = "WLink_Test.text";

		WLink link = new WLink(sharedText, null);

		link.setLocked(true);
		setActiveContext(createUIContext());
		link.setText(text);
		Assert.assertFalse("Link should not be in default state with session text is set", link.
				isDefaultState());
		Assert.assertEquals("Session text should be returend for modified session", text, link.
				getText());

		resetContext();
		Assert.assertSame("Shared text should not have changed", sharedText, link.getText());
	}

	@Test
	public void testSetUrl() {
		final String sharedUrl = "WLink_Test.sharedUrl";
		final String url = "WLink_Test.url";

		WLink link = new WLink(null, sharedUrl);

		link.setLocked(true);
		setActiveContext(createUIContext());
		link.setUrl(url);
		Assert.assertFalse("Link should not be in default state with session url is set", link.
				isDefaultState());
		Assert.assertEquals("Session url should be returend for modified session", url, link.
				getUrl());

		resetContext();
		Assert.assertSame("Shared url should not have changed", sharedUrl, link.getUrl());
	}

	@Test
	public void testSetAccessKey() {
		final char accessKey = 'X';

		WLink link = new WLink();
		Assert.assertNull("Default access key should be null", link.getAccessKeyAsString());

		link.setAccessKey(accessKey);
		Assert.assertEquals("Incorrect access key returned", accessKey, link.getAccessKey());
	}

	@Test
	public void testSetDisabled() {
		WLink link = new WLink();

		link.setLocked(true);
		setActiveContext(createUIContext());
		link.setDisabled(true);
		Assert.assertFalse("Link should not be in default state with session disabled flag is set",
				link.isDefaultState());
		Assert.assertTrue("Should be disabled for modified session", link.isDisabled());

		resetContext();
		Assert.assertFalse("Should not be disabled for other sessions", link.isDisabled());
	}

	@Test
	public void testSetRel() {
		final String rel = "WLink_Test.rel";

		WLink link = new WLink();
		Assert.assertNull("Default rel should be null", link.getRel());

		link.setRel(rel);
		Assert.assertSame("Incorrect rel returned", rel, link.getRel());
	}

	@Test
	public void testSetOpenNewWindow() {
		WLink link = new WLink();
		Assert.assertTrue("Should open in new window by default", link.getOpenNewWindow());

		link.setOpenNewWindow(false);
		Assert.assertFalse("Open in new window should be false", link.getOpenNewWindow());

		link.setOpenNewWindow(true);
		Assert.assertTrue("Open in new window should be true", link.getOpenNewWindow());
	}

	@Test
	public void testSetRenderAsButton() {
		WLink link = new WLink();
		Assert.assertFalse("Should render as link by default", link.isRenderAsButton());

		link.setRenderAsButton(true);
		Assert.assertTrue("Should render as button after setRenderAsButton(true)", link.
				isRenderAsButton());

		link.setRenderAsButton(false);
		Assert.assertFalse("Should render as link after setRenderAsButton(false)", link.
				isRenderAsButton());
	}

	@Test
	public void testGetTargetWindowName() {
		WLink link = new WLink();
		link.setTargetWindowName(TEST_WINDOW_NAME);

		link.setLocked(true);
		setActiveContext(createUIContext());
		link.setTargetWindowName(TEST_WINDOW_NAME2);

		Assert.assertEquals("TargetWindowName should be changed for uic2", TEST_WINDOW_NAME2, link.
				getTargetWindowName());

		resetContext();
		Assert.assertEquals("TargetWindowName should be unchanged for shared", TEST_WINDOW_NAME,
				link.getTargetWindowName());
	}

	@Test
	public void testSetDisabledByDefault() {
		WLink link = new WLink();

		link.setLocked(true);
		setActiveContext(createUIContext());
		link.setDisabled(true);
		Assert.assertTrue("link should be disabled", link.isDisabled());

		resetContext();
		Assert.assertFalse("link should be not disabled by default", link.isDisabled());
	}

	@Test
	public void testSetImage() {
		WLink link = new WLink();
		link.setLocked(true);

		ImageResource image = new ImageResource("test.png");

		setActiveContext(createUIContext());
		link.setImage(image);
		Assert.assertSame("Uic 1 image position should be returned for uic 1", image, link.
				getImage());
		Assert.assertFalse("Image should not be in default state for uic1", link.isDefaultState());

		resetContext();
		Assert.assertNull("Default image should be null", link.getImage());
	}

	@Test
	public void testSetImageUrl() {
		WLink link = new WLink();
		link.setLocked(true);
		String imageUrl = "http://localhost/image.jpg";

		setActiveContext(createUIContext());
		link.setImageUrl(imageUrl);
		Assert.assertEquals("Uic 1 image url should be returned for uic 1", imageUrl, link.
				getImageUrl());
		Assert.assertFalse("Button should not be in default state for uic1", link.isDefaultState());

		resetContext();
		Assert.assertNull("Default image url should be null", link.getImageUrl());
	}

	@Test
	public void testSetImagePosition() {
		WLink link = new WLink();
		link.setLocked(true);

		setActiveContext(createUIContext());
		link.setImagePosition(WLink.ImagePosition.EAST);
		Assert.assertEquals("Uic 1 image position should be returned for uic 1",
				WLink.ImagePosition.EAST,
				link.getImagePosition());
		Assert.assertFalse("Button should not be in default state for uic1", link.isDefaultState());

		resetContext();
		Assert.assertNull("Default image position should be null", link.getImagePosition());
	}

	@Test
	public void testSetAction() {
		WLink link = new WLink();
		Action action = new TestAction();
		AjaxTarget target1 = new WPanel();
		AjaxTarget target2 = new WPanel();

		Assert.assertNull("Action should be null by default", link.getAction());
		Assert.assertNull("Action targets should be null by default", link.getActionTargets());

		link.setAction(action);
		Assert.assertEquals("Incorrect action returned", action, link.getAction());
		Assert.assertNotNull("Action targets should not be null", link.getActionTargets());
		Assert.assertEquals("Action targets should be empty", 0, link.getActionTargets().length);

		link.setAction(action, target1, target2);
		Assert.assertEquals("Incorrect action returned", action, link.getAction());
		Assert.assertNotNull("Action targets should not be null", link.getActionTargets());
		Assert.assertEquals("Incorrect action targets returned", 2, link.getActionTargets().length);
		Assert.assertEquals("Incorrect action target returned for target1", target1, link.
				getActionTargets()[0]);
		Assert.assertEquals("Incorrect action target returned for target2", target2, link.
				getActionTargets()[1]);
	}

	@Test
	public void testHandleRequest() {
		MockRequest request = new MockRequest();
		TestAction action = new TestAction();
		WLink link = new WLink();
		link.setAction(action);

		// Request with link NOT pressed
		UIContext uic = createUIContext();
		setActiveContext(uic);
		link.serviceRequest(request);
		Assert.assertFalse("Action should not have triggered", action.wasTriggered());

		// Setup request for UIC1
		try {
			// Setup AJAX Operation (not for the WLink)
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation("X", "Z"), null);

			// Request with link NOT pressed (not current AJAX operation)
			link.serviceRequest(request);
			Assert.assertFalse("Action should not have triggered", action.wasTriggered());

			// Setup AJAX Operation trigger by the link
			AjaxHelper.setCurrentOperationDetails(new AjaxOperation(link.getId(), link.getId()),
					null);

			// Request with link pressed (via AJAX operation)
			link.serviceRequest(request);
			Assert.assertTrue("Action should have triggered", action.wasTriggered());

			// Request with link pressed disabled (via AJAX operation)
			action.reset();
			link.setDisabled(true);
			link.serviceRequest(request);
			Assert.assertFalse("Action should not have triggered when disabled", action.
					wasTriggered());
		} finally {
			// Clear AJAX operation
			AjaxHelper.clearCurrentOperationDetails();
		}
	}
}
