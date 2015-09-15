package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WConfirmationButton_Test - Unit tests for {@link WConfirmationButton}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WConfirmationButton_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		final String buttonText = "WConfirmationButton_Test.testConstructors.buttonText";
		final char accessKey = 'A';

		WConfirmationButton button = new WConfirmationButton();
		Assert.assertNull("Button text should be null", button.getText());
		Assert.assertNull("Access key should be null", button.getAccessKeyAsString());

		button = new WConfirmationButton(buttonText);
		Assert.assertEquals("Incorrect button text", buttonText, button.getText());
		Assert.assertNull("Access key should be null", button.getAccessKeyAsString());

		button = new WConfirmationButton(buttonText, accessKey);
		Assert.assertEquals("Incorrect button text", buttonText, button.getText());
		Assert.assertEquals("Incorrect access key", accessKey, button.getAccessKey());
	}

	@Test
	public void testMessageAccessors() {
		String message = "WConfirmationButton_Test.testMessageAccessors";

		WConfirmationButton button = new WConfirmationButton();
		button.setMessage(message);
		Assert.assertEquals("Incorrect message", message, button.getMessage());
	}
}
