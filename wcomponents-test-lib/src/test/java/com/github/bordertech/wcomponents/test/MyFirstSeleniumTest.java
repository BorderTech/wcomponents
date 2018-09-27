package com.github.bordertech.wcomponents.test;


import com.github.bordertech.wcomponents.test.components.ExampleUI;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWTextFieldWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class MyFirstSeleniumTest extends SeleniumJettyTestCase {


	public MyFirstSeleniumTest() {
		super(new ExampleUI());
	}

	@Test
	public void firstSeleniumTest() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Paths to frequently used components
		SeleniumWTextFieldWebElement textToDuplicate = driver.findWTextField(new ByLabel("Text to duplicate", false));

		// Enter some text and use the duplicate button
		textToDuplicate.sendKeys("dummy");
		assertEquals("dummy", textToDuplicate.getValue());

	}


}
