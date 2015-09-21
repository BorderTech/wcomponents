package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WButton}.
 *
 * @author Ming Gao
 * @since 1.0.0
 */
public class WButton_Test extends AbstractWComponentTestCase {

	private static final String SHARED_VALUE = "shared value";
	private static final String USER_VALUE = "user value";
	private static final String SHARED_TEXT = "shared text";
	private static final String USER_TEXT = "user text";
	private static final String BEAN_VALUE = "bean value";

	@Test
	public void testConstructors() {
		final String text = "WButton_Test.testConstructors";
		final char accessKey = 'W';

		WButton button = new WButton(text);
		Assert.assertEquals("Incorrect button text", text, button.getText());
		Assert.assertNull("Accesskey should not be set", button.getAccessKeyAsString());

		button = new WButton(text, accessKey);
		Assert.assertEquals("Incorrect button text", text, button.getText());
		Assert.assertEquals("Incorrect access key returned", accessKey, button.getAccessKey());
	}

	/**
	 * Test the access key methods on button.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testAccessKeyHandling() throws Exception {
		String buttonText = "ABCDE abcde";
		WButton button = new WButton(buttonText);
		button.setAccessKey('E');

		// reset shared button text. Access key should be automatically reset.
		button.setText("Test");

		// override tool tip.
		String myToolTip = "My ToolTip";
		button.setToolTip(myToolTip);
		Assert.assertEquals("Unexpected ToolTip text", myToolTip, button.getToolTip());
	}

	@Test
	public void testSetPressed() {
		WButton button = new WButton("Test");
		button.setLocked(true);
		setActiveContext(createUIContext());

		button.setPressed(true, new MockRequest());
		Assert.assertTrue("Button should be pressed", button.isPressed());
		Assert.assertFalse("Button should not be in default state", button.isDefaultState());

		button.setPressed(false, new MockRequest());
		Assert.assertFalse("Button should be pressed", button.isPressed());

		button.setDisabled(true);
		button.setPressed(true, new MockRequest());
		Assert.assertTrue("Button should be disabled", button.isDisabled());
		Assert.assertFalse("Button should be pressed when disabled", button.isPressed());
	}

	@Test
	public void testActionSetPressed() {
		WButton button = new WButton("Test");
		button.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);
		uic.setUI(button);

		TestAction testAction = new TestAction();
		testAction.reset();
		button.setAction(testAction);
		button.setPressed(true, new MockRequest());
		Assert.assertEquals("Button was not pressed", true, button.isPressed());
	}

	@Test
	public void testSetActionCommand() {
		String text = "WButton_Test.text";
		String sharedValue = "WButton_Test.sharedValue";
		String value = "WButton_Test.value";
		UIContext uic1 = new UIContextImpl();

		WButton button = new WButton(text);
		button.setLocked(true);
		Assert.assertEquals("Default action command should be button text", text, button.
				getActionCommand());

		button.setActionCommand(sharedValue);
		Assert.assertEquals("Incorrect shared action command returned", sharedValue, button.
				getActionCommand());

		setActiveContext(uic1);
		button.setActionCommand(value);
		Assert.assertEquals("Uic 1 action command should be returned for uic 1", value, button.
				getActionCommand());
		Assert.
				assertFalse("Button should not be in default state for uic1", button.
						isDefaultState());
		resetContext();

		Assert.assertEquals("Incorrect shared action command returned", sharedValue, button.
				getActionCommand());
	}

	@Test
	public void testSetActionObject() {
		Serializable actionObject = "testSetActionObject.actionObject1";

		WButton button = new WButton();
		button.setLocked(true);

		setActiveContext(createUIContext());
		button.setActionObject(actionObject);
		Assert.assertSame("Incorrect action object", actionObject, button.getActionObject());

		resetContext();
		Assert.assertNull("Action object should be null by default", button.getActionObject());
	}

	@Test
	public void testSetImageUrl() {
		WButton button = new WButton();
		button.setLocked(true);
		String imageUrl = "http://127.0.0.1/image.jpg";

		setActiveContext(createUIContext());
		button.setImageUrl(imageUrl);
		button.setLocked(true);
		Assert.assertEquals("Uic 1 image url should be returned for uic 1", imageUrl, button.
				getImageUrl());
		Assert.
				assertFalse("Button should not be in default state for uic1", button.
						isDefaultState());

		resetContext();
		Assert.assertNull("Default image url should be null", button.getImageUrl());
	}

	@Test
	public void testSetImagePosition() {
		WButton button = new WButton();
		button.setLocked(true);

		setActiveContext(createUIContext());
		button.setImagePosition(ImagePosition.EAST);
		button.setLocked(true);
		Assert.assertEquals("Uic 1 image position should be returned for uic 1", ImagePosition.EAST,
				button.getImagePosition());
		Assert.
				assertFalse("Button should not be in default state for uic1", button.
						isDefaultState());

		resetContext();
		Assert.assertNull("Default image position should be null", button.getImagePosition());
	}

	@Test
	public void testPopupTrigger() {
		WButton button = new WButton("Test");
		button.setLocked(true);
		setActiveContext(createUIContext());

		Assert.assertFalse("Should not be a popup trigger by default", button.isPopupTrigger());

		button.setPopupTrigger(true);
		Assert.assertTrue("Button should be a popup trigger", button.isPopupTrigger());

		resetContext();
		Assert.assertFalse("Default popup trigger status should not have changed", button.
				isPopupTrigger());
	}

	// TODO rewrite as a parameterised test when we upgrade to jUnit4?
	@Test
	public void testGetText() {
		Assert.assertNull("Incorrect text for button with nothing",
				getButtonText(false, false, false, false, false, false));

		Assert.assertEquals("Incorrect text for button with Shared text",
				SHARED_TEXT,
				getButtonText(true, false, false, false, false, false));

		Assert.assertEquals("Incorrect text for button with User text",
				USER_TEXT,
				getButtonText(false, true, false, false, false, false));

		Assert.assertEquals("Incorrect text for button with User + shared text",
				USER_TEXT,
				getButtonText(true, true, false, false, false, false));

		Assert.assertEquals("Incorrect text for button with Shared value only",
				SHARED_VALUE,
				getButtonText(false, false, true, false, false, false));

		Assert.assertEquals("Incorrect text for button with Shared text, shared value",
				SHARED_TEXT,
				getButtonText(true, false, true, false, false, false));

		Assert.assertEquals("Incorrect text for button with User text, shared value",
				USER_TEXT,
				getButtonText(false, true, true, false, false, false));

		Assert.assertEquals("Incorrect text for button with Shared + user text, shared value",
				USER_TEXT,
				getButtonText(true, true, true, false, false, false));

		Assert.assertEquals("Incorrect text for button with User value only",
				USER_VALUE,
				getButtonText(false, false, false, true, false, false));

		Assert.assertEquals("Incorrect text for button with Shared text, user value",
				SHARED_TEXT,
				getButtonText(true, false, false, true, false, false));

		Assert.assertEquals("Incorrect text for button with User text, user value",
				USER_TEXT,
				getButtonText(false, true, false, true, false, false));

		Assert.assertEquals("Incorrect text for button with Shared + user text, user value",
				USER_TEXT,
				getButtonText(true, true, false, true, false, false));

		Assert.assertEquals("Incorrect text for button with Shared + user value",
				USER_VALUE,
				getButtonText(false, false, true, true, false, false));

		Assert.assertEquals("Incorrect text for button with Shared text, shared + user value",
				SHARED_TEXT,
				getButtonText(true, false, true, true, false, false));

		Assert.assertEquals("Incorrect text for button with User text, shared + user value",
				USER_TEXT,
				getButtonText(false, true, true, true, false, false));

		Assert.
				assertEquals(
						"Incorrect text for button with Shared + user text, shared + user value",
						USER_TEXT,
						getButtonText(true, true, true, true, false, false));

		Assert.assertNull("Incorrect text for button with null bean only",
				getButtonText(false, false, false, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, Shared text",
				SHARED_TEXT,
				getButtonText(true, false, false, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, User text",
				USER_TEXT,
				getButtonText(false, true, false, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, User + shared text",
				USER_TEXT,
				getButtonText(true, true, false, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, Shared value only",
				SHARED_VALUE,
				getButtonText(false, false, true, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, Shared text, shared value",
				SHARED_TEXT,
				getButtonText(true, false, true, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, User text, shared value",
				USER_TEXT,
				getButtonText(false, true, true, false, true, true));

		Assert.assertEquals(
				"Incorrect text for button with null bean, Shared + user text, shared value",
				USER_TEXT,
				getButtonText(true, true, true, false, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, User value only",
				USER_VALUE,
				getButtonText(false, false, false, true, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, Shared text, user value",
				SHARED_TEXT,
				getButtonText(true, false, false, true, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, User text, user value",
				USER_TEXT,
				getButtonText(false, true, false, true, true, true));

		Assert.assertEquals(
				"Incorrect text for button with null bean, Shared + user text, user value",
				USER_TEXT,
				getButtonText(true, true, false, true, true, true));

		Assert.assertEquals("Incorrect text for button with null bean, Shared + user value",
				USER_VALUE,
				getButtonText(false, false, true, true, true, true));

		Assert.assertEquals(
				"Incorrect text for button with null bean, Shared text, shared + user value",
				SHARED_TEXT,
				getButtonText(true, false, true, true, true, true));

		Assert.assertEquals(
				"Incorrect text for button with null bean, User text, shared + user value",
				USER_TEXT,
				getButtonText(false, true, true, true, true, true));

		Assert.assertEquals(
				"Incorrect text for button with null bean, Shared + user text, shared + user value",
				USER_TEXT,
				getButtonText(true, true, true, true, true, true));

		Assert.assertEquals("Incorrect text for button with Bean only",
				BEAN_VALUE,
				getButtonText(false, false, false, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared text",
				SHARED_TEXT,
				getButtonText(true, false, false, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User text",
				USER_TEXT,
				getButtonText(false, true, false, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User + shared text",
				USER_TEXT,
				getButtonText(true, true, false, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared value only",
				BEAN_VALUE,
				getButtonText(false, false, true, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared text, shared value",
				SHARED_TEXT,
				getButtonText(true, false, true, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User text, shared value",
				USER_TEXT,
				getButtonText(false, true, true, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared + user text, shared value",
				USER_TEXT,
				getButtonText(true, true, true, false, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User value only",
				USER_VALUE,
				getButtonText(false, false, false, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared text, user value",
				SHARED_TEXT,
				getButtonText(true, false, false, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User text, user value",
				USER_TEXT,
				getButtonText(false, true, false, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared + user text, user value",
				USER_TEXT,
				getButtonText(true, true, false, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared + user value",
				USER_VALUE,
				getButtonText(false, false, true, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, Shared text, shared + user value",
				SHARED_TEXT,
				getButtonText(true, false, true, true, true, false));

		Assert.assertEquals("Incorrect text for button with bean, User text, shared + user value",
				USER_TEXT,
				getButtonText(false, true, true, true, true, false));

		Assert.assertEquals(
				"Incorrect text for button with bean, Shared + user text, shared + user value",
				USER_TEXT,
				getButtonText(true, true, true, true, true, false));
	}

	// TODO rewrite as a parameterised test when we upgrade to jUnit4?
	@Test
	public void testGetValue() {
		Assert.assertEquals("Incorrect value for button with nothing",
				WButton.NO_VALUE,
				getButtonValue(false, false, false, false, false, false));

		Assert.assertEquals("Incorrect value for button with Shared text",
				SHARED_TEXT,
				getButtonValue(true, false, false, false, false, false));

		Assert.assertEquals("Incorrect value for button with User text",
				USER_TEXT,
				getButtonValue(false, true, false, false, false, false));

		Assert.assertEquals("Incorrect value for button with User + shared text",
				USER_TEXT,
				getButtonValue(true, true, false, false, false, false));

		Assert.assertEquals("Incorrect value for button with Shared value only",
				SHARED_VALUE,
				getButtonValue(false, false, true, false, false, false));

		Assert.assertEquals("Incorrect value for button with Shared text, shared value",
				SHARED_VALUE,
				getButtonValue(true, false, true, false, false, false));

		Assert.assertEquals("Incorrect value for button with User text, shared value",
				SHARED_VALUE,
				getButtonValue(false, true, true, false, false, false));

		Assert.assertEquals("Incorrect value for button with Shared + user text, shared value",
				SHARED_VALUE,
				getButtonValue(true, true, true, false, false, false));

		Assert.assertEquals("Incorrect value for button with User value only",
				USER_VALUE,
				getButtonValue(false, false, false, true, false, false));

		Assert.assertEquals("Incorrect value for button with Shared text, user value",
				USER_VALUE,
				getButtonValue(true, false, false, true, false, false));

		Assert.assertEquals("Incorrect value for button with User text, user value",
				USER_VALUE,
				getButtonValue(false, true, false, true, false, false));

		Assert.assertEquals("Incorrect value for button with Shared + user text, user value",
				USER_VALUE,
				getButtonValue(true, true, false, true, false, false));

		Assert.assertEquals("Incorrect value for button with Shared + user value",
				USER_VALUE,
				getButtonValue(false, false, true, true, false, false));

		Assert.assertEquals("Incorrect value for button with Shared text, shared + user value",
				USER_VALUE,
				getButtonValue(true, false, true, true, false, false));

		Assert.assertEquals("Incorrect value for button with User text, shared + user value",
				USER_VALUE,
				getButtonValue(false, true, true, true, false, false));

		Assert.assertEquals(
				"Incorrect value for button with Shared + user text, shared + user value",
				USER_VALUE,
				getButtonValue(true, true, true, true, false, false));

		Assert.assertEquals("Incorrect value for button with null bean only",
				WButton.NO_VALUE,
				getButtonValue(false, false, false, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, Shared text",
				SHARED_TEXT,
				getButtonValue(true, false, false, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, User text",
				USER_TEXT,
				getButtonValue(false, true, false, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, User + shared text",
				USER_TEXT,
				getButtonValue(true, true, false, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, Shared value only",
				SHARED_VALUE,
				getButtonValue(false, false, true, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, Shared text, shared value",
				SHARED_VALUE,
				getButtonValue(true, false, true, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, User text, shared value",
				SHARED_VALUE,
				getButtonValue(false, true, true, false, true, true));

		Assert.assertEquals(
				"Incorrect value for button with null bean, Shared + user text, shared value",
				SHARED_VALUE,
				getButtonValue(true, true, true, false, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, User value only",
				USER_VALUE,
				getButtonValue(false, false, false, true, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, Shared text, user value",
				USER_VALUE,
				getButtonValue(true, false, false, true, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, User text, user value",
				USER_VALUE,
				getButtonValue(false, true, false, true, true, true));

		Assert.assertEquals(
				"Incorrect value for button with null bean, Shared + user text, user value",
				USER_VALUE,
				getButtonValue(true, true, false, true, true, true));

		Assert.assertEquals("Incorrect value for button with null bean, Shared + user value",
				USER_VALUE,
				getButtonValue(false, false, true, true, true, true));

		Assert.assertEquals(
				"Incorrect value for button with null bean, Shared text, shared + user value",
				USER_VALUE,
				getButtonValue(true, false, true, true, true, true));

		Assert.assertEquals(
				"Incorrect value for button with null bean, User text, shared + user value",
				USER_VALUE,
				getButtonValue(false, true, true, true, true, true));

		Assert.assertEquals(
				"Incorrect value for button with null bean, Shared + user text, shared + user value",
				USER_VALUE,
				getButtonValue(true, true, true, true, true, true));

		Assert.assertEquals("Incorrect value for button with Bean only",
				BEAN_VALUE,
				getButtonValue(false, false, false, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared text",
				BEAN_VALUE,
				getButtonValue(true, false, false, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User text",
				BEAN_VALUE,
				getButtonValue(false, true, false, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User + shared text",
				BEAN_VALUE,
				getButtonValue(true, true, false, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared value only",
				BEAN_VALUE,
				getButtonValue(false, false, true, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared text, shared value",
				BEAN_VALUE,
				getButtonValue(true, false, true, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User text, shared value",
				BEAN_VALUE,
				getButtonValue(false, true, true, false, true, false));

		Assert.
				assertEquals(
						"Incorrect value for button with bean, Shared + user text, shared value",
						BEAN_VALUE,
						getButtonValue(true, true, true, false, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User value only",
				USER_VALUE,
				getButtonValue(false, false, false, true, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared text, user value",
				USER_VALUE,
				getButtonValue(true, false, false, true, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User text, user value",
				USER_VALUE,
				getButtonValue(false, true, false, true, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared + user text, user value",
				USER_VALUE,
				getButtonValue(true, true, false, true, true, false));

		Assert.assertEquals("Incorrect value for button with bean, Shared + user value",
				USER_VALUE,
				getButtonValue(false, false, true, true, true, false));

		Assert.
				assertEquals(
						"Incorrect value for button with bean, Shared text, shared + user value",
						USER_VALUE,
						getButtonValue(true, false, true, true, true, false));

		Assert.assertEquals("Incorrect value for button with bean, User text, shared + user value",
				USER_VALUE,
				getButtonValue(false, true, true, true, true, false));

		Assert.assertEquals(
				"Incorrect value for button with bean, Shared + user text, shared + user value",
				USER_VALUE,
				getButtonValue(true, true, true, true, true, false));
	}

	/**
	 * Returns the text for a button created with the given data.
	 *
	 * @param useSharedText true if the button should have shared text
	 * @param useUserText true if the button should have user text
	 * @param useSharedValue true if the button should have a shared value
	 * @param useUserValue true if the button should have a user value
	 * @param useBeanValue true if the button should have a bean provider
	 * @param nullBean true if the provided bean should be null
	 *
	 * @return the button text that will be displayed to the user.
	 */
	private String getButtonText(final boolean useSharedText, final boolean useUserText,
			final boolean useSharedValue, final boolean useUserValue,
			final boolean useBeanValue, final boolean nullBean) {
		WButton button = createButton(useSharedText, useUserText, useSharedValue, useUserValue,
				useBeanValue, nullBean);

		setActiveContext(createUIContext());

		try {
			button.preparePaintComponent(new MockRequest());
			return button.getText();
		} finally {
			UIContextHolder.reset();
		}
	}

	/**
	 * Returns the value for a button created with the given data.
	 *
	 * @param useSharedText true if the button should have shared text
	 * @param useUserText true if the button should have user text
	 * @param useSharedValue true if the button should have a shared value
	 * @param useUserValue true if the button should have a user value
	 * @param useBeanValue true if the button should have a bean provider
	 * @param nullBean true if the provided bean should be null
	 *
	 * @return the button value will be used for a user.
	 */
	private String getButtonValue(final boolean useSharedText, final boolean useUserText,
			final boolean useSharedValue, final boolean useUserValue,
			final boolean useBeanValue, final boolean nullBean) {
		WButton button = createButton(useSharedText, useUserText, useSharedValue, useUserValue,
				useBeanValue, nullBean);
		setActiveContext(createUIContext());

		try {
			button.preparePaintComponent(new MockRequest());
			return button.getValue();
		} finally {
			UIContextHolder.reset();
		}
	}

	/**
	 * Creates a button configured to have the given data.
	 *
	 * @param useSharedText true if the button should have shared text
	 * @param useUserText true if the button should have user text
	 * @param useSharedValue true if the button should have a shared value
	 * @param useUserValue true if the button should have a user value
	 * @param useBeanValue true if the button should have a bean provider
	 * @param nullBean true if the provided bean should be null
	 *
	 * @return the button.
	 */
	private WButton createButton(final boolean useSharedText, final boolean useUserText,
			final boolean useSharedValue, final boolean useUserValue,
			final boolean useBeanValue, final boolean nullBean) {
		final WButton button = new WButton() {
			@Override
			protected void preparePaintComponent(final Request request) {
				if (!isInitialised()) {
					if (useUserValue) {
						setValue(USER_VALUE);
					}

					if (useUserText) {
						setText(USER_TEXT);
					}

					setInitialised(true);
				}
				super.preparePaintComponent(request);
			}
		};

		if (useSharedText) {
			button.setText(SHARED_TEXT);
		}

		if (useSharedValue) {
			button.setValue(SHARED_VALUE);
		}

		if (useBeanValue) {
			button.setBeanProvider(new BeanProvider() {
				@Override
				public Object getBean(final BeanProviderBound beanProviderBound) {
					return nullBean ? null : BEAN_VALUE;
				}
			});
		}

		return button;
	}
}
