package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link CheckBoxExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class CheckBoxExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new CheckBoxExample_Test.
	 */
	public CheckBoxExample_Test() {
		super(new CheckBoxExample());
	}

	@Test
	public void testExample() {
		SeleniumWComponentsWebDriver driver = getDriver();
		Assert.assertFalse("First checkbox should be unselected",
				driver.findWCheckBox(byWComponentPath("WCheckBox[0]")).isSelected());
		Assert.assertTrue("First checkbox should be enabled",
				driver.findWCheckBox(byWComponentPath("WCheckBox[0]")).isEnabled());
	}

	@Test
	public void testSelectedCheckBox() {
		SeleniumWComponentsWebDriver driver = getDriver();
		Assert.assertTrue("Second checkbox should be selected",
				driver.findWCheckBox(byWComponentPath("WCheckBox[1]")).isSelected());
		Assert.assertTrue("Second checkbox should be enabled",
				driver.findWCheckBox(byWComponentPath("WCheckBox[1]")).isEnabled());
	}

	@Test
	public void testDisabledCheckBox() {
		SeleniumWComponentsWebDriver driver = getDriver();
		Assert.assertFalse("Third checkbox should be unselected",
				driver.findWCheckBox(byWComponentPath("WCheckBox[2]")).isSelected());
		Assert.assertFalse("Third checkbox should be disabled",
				driver.findWCheckBox(byWComponentPath("WCheckBox[2]")).isEnabled());
	}
}
