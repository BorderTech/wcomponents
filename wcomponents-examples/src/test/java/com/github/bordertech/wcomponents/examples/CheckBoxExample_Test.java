package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link CheckBoxExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class CheckBoxExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new CheckBoxExample_Test.
	 */
	public CheckBoxExample_Test() {
		super(new CheckBoxExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Assert.assertFalse("First checkbox should be unselected",
				driver.findElement(byWComponentPath("WCheckBox[0]")).isSelected());
		Assert.assertTrue("First checkbox should be enabled",
				driver.findElement(byWComponentPath("WCheckBox[0]")).isEnabled());

		Assert.assertTrue("Second checkbox should be selected",
				driver.findElement(byWComponentPath("WCheckBox[1]")).isSelected());
		Assert.assertTrue("Second checkbox should be enabled",
				driver.findElement(byWComponentPath("WCheckBox[1]")).isEnabled());

		Assert.assertFalse("Third checkbox should be unselected",
				driver.findElement(byWComponentPath("WCheckBox[2]")).isSelected());
		Assert.assertFalse("Third checkbox should be disabled",
				driver.findElement(byWComponentPath("WCheckBox[2]")).isEnabled());
	}
}
