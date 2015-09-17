package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link RadioButtonExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class RadioButtonExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new RadioButtonExample_Test.
	 */
	public RadioButtonExample_Test() {
		super(new RadioButtonExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Assert.assertFalse("First radioButton should be unselected",
				driver.findElement(byWComponentPath("WRadioButton[0]")).isSelected());
		Assert.assertTrue("First radioButton should be enabled",
				driver.findElement(byWComponentPath("WRadioButton[0]")).isEnabled());

		Assert.assertTrue("Second radioButton should be selected",
				driver.findElement(byWComponentPath("WRadioButton[1]")).isSelected());
		Assert.assertTrue("Second radioButton should be enabled",
				driver.findElement(byWComponentPath("WRadioButton[1]")).isEnabled());

		Assert.assertFalse("Third radioButton should be unselected",
				driver.findElement(byWComponentPath("WRadioButton[2]")).isSelected());
		Assert.assertFalse("Third radioButton should be disabled",
				driver.findElement(byWComponentPath("WRadioButton[2]")).isEnabled());
	}
}
