package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WTextFieldUI;
import com.github.bordertech.wcomponents.test.selenium.ByButtonValue;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextFieldWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WTextFieldWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WTextFieldWebElementTest() {
		super(new WTextFieldUI());
	}

	@Test
	public void testTextFieldGetSet() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButtonValue("Validate button", false, true));

		new ByButtonValue("Validate button", false, true);
		validatingButton.click();

		// Enter some text and use the duplicate button
		SeleniumWTextFieldWebElement textField = driver.findWTextField(new ByLabel("this is a textfield", false));

		String message = textField.getFieldIndicatorMessage();
		assertEquals(message, "this is a textfield must be completed.");

		textField.sendKeys("dummy text");
		assertEquals("dummy text", textField.getValue());
	}

}
