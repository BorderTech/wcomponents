package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WTextAreaUI;
import com.github.bordertech.wcomponents.test.selenium.ByButtonText;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWFieldIndicatorWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextAreaWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WTextAreaWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WTextAreaWebElementTest() {
		super(new WTextAreaUI());
	}

	@Test
	public void testTextFieldFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButtonText("Validate button", false, true));

		new ByButtonText("Validate button", false, true);
		validatingButton.click();

		SeleniumWTextAreaWebElement textArea = driver.findWTextArea(new ByLabel("this is a textarea", false));

		SeleniumWFieldIndicatorWebElement message = textArea.getFieldIndicatorMessage();
		assertEquals(message.getText(), "this is a textarea must be completed.");

		textArea.sendKeys("dummy text");
		assertEquals("dummy text", textArea.getValue());
	}

}
