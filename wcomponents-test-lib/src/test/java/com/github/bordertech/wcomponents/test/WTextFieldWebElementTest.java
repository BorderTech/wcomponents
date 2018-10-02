package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WTextFieldUI;
import com.github.bordertech.wcomponents.test.selenium.ByButton;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWFieldIndicatorWebElement;
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
	public void testTextFieldFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButton("Validate button", false, true));

		new ByButton("Validate button", false, true);
		validatingButton.click();

		SeleniumWTextFieldWebElement textField = driver.findWTextField(new ByLabel("this is a textfield", false));

		SeleniumWFieldIndicatorWebElement message = textField.getFieldIndicatorMessage();
		assertEquals(message.getText(), "this is a textfield must be completed.");

		textField.sendKeys("dummy text");
		assertEquals("dummy text", textField.getValue());
	}

}
