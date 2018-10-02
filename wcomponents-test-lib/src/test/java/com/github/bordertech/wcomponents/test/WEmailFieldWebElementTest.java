package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WEmailFieldUI;
import com.github.bordertech.wcomponents.test.selenium.ByButton;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWEmailFieldWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWFieldIndicatorWebElement;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WEmailFieldWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WEmailFieldWebElementTest() {
		super(new WEmailFieldUI());
	}


	@Test
	public void testFeaturesOfEmailField() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButton("Validate button", false, true));
		validatingButton.click();

		SeleniumWEmailFieldWebElement emailField = driver.findWEmailField(new ByLabel("this is a email field", false));

		emailField.sendKeys("dummy text");
		assertEquals("dummy text", emailField.getValue());

		validatingButton = driver.findWButton(new ByButton("Validate button", false, true));
		validatingButton.click();

		SeleniumWFieldIndicatorWebElement message = emailField.getFieldIndicatorMessage();
		assertEquals(message.getText(), "this is a email field must be an email address.");
		assertEquals(message.getIndicatorType(), AbstractWFieldIndicator.FieldIndicatorType.ERROR);


		emailField.clear();
		emailField.sendKeys("dummy@gmail.com");
		assertEquals("dummy@gmail.com", emailField.getValue());

		message = emailField.getFieldIndicatorMessage();
		assertEquals(message.getText(), "Successfully updated.");
		assertEquals(message.getIndicatorType(), AbstractWFieldIndicator.FieldIndicatorType.SUCCESS);
	}
}
