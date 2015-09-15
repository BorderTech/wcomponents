package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * WPrintButton_Test - Unit tests for {@link WPrintButton}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WPrintButton_Test {

	@Test
	public void testConstructors() {
		String text = "WPrintButton_Test.testConstructors";

		WPrintButton button = new WPrintButton();
		Assert.assertEquals("Incorrect button text", "Print", button.getText());

		button = new WPrintButton(text);
		Assert.assertEquals("Incorrect button text", text, button.getText());
	}
}
