package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WPhoneNumberFieldUI;
import com.github.bordertech.wcomponents.test.selenium.by.ByButtonText;
import com.github.bordertech.wcomponents.test.selenium.by.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWButtonWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWFieldIndicatorWebElement;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWPhoneNumberFieldWebElement;
import com.github.bordertech.wcomponents.validation.AbstractWFieldIndicator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WPhoneNumberFieldWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WPhoneNumberFieldWebElementTest() {
		super(new WPhoneNumberFieldUI());
	}


	@Test
	public void testTextFieldFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWButtonWebElement validatingButton = driver.findWButton(new ByButtonText("Validate button", false, true));

		validatingButton.click();

		SeleniumWPhoneNumberFieldWebElement phoneNumberField = driver.findWPhoneNumberField(new ByLabel("this is a phoneNumberField", false));

		phoneNumberField.sendKeys("0400000000");
		phoneNumberField.sendKeys(Keys.TAB);
		assertEquals("0400000000", phoneNumberField.getValue());

		SeleniumWFieldIndicatorWebElement message = phoneNumberField.getFieldIndicatorMessage();
		assertEquals("Successfully updated.", message.getText());
		assertEquals(message.getIndicatorType(), AbstractWFieldIndicator.FieldIndicatorType.SUCCESS);
	}

}
