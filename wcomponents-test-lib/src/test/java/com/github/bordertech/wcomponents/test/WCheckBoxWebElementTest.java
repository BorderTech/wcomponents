package com.github.bordertech.wcomponents.test;

import com.github.bordertech.wcomponents.test.components.WCheckBoxFieldUI;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.SeleniumJettyTestCase;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWCheckBoxWebElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(MultiBrowserRunner.class)
public class WCheckBoxWebElementTest extends SeleniumJettyTestCase {


	/**
	 * Constructor...
	 */
	public WCheckBoxWebElementTest() {
		super(new WCheckBoxFieldUI());
	}

	@Test
	public void testTextFieldFeatures() {
		SeleniumWComponentsWebDriver driver = getDriver();

		SeleniumWCheckBoxWebElement checkBox = driver.findWCheckBox(new ByLabel("this is a checkbox", false));

		assertEquals(false, checkBox.isChecked());
		checkBox.click();
		assertEquals(true, checkBox.isChecked());
	}
}
