package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.util.Config;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link AppPreferenceParameterExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AppPreferenceParameterExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new AppPreferenceParameterExample_Test.
	 */
	public AppPreferenceParameterExample_Test() {
		super(new AppPreferenceParameterExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Note: When deployed to portal, the parameter "portlet.wcomponents-examples.example.preferred.state" will be used.
		String preferenceParam = Config.getInstance().getString("example.preferred.state");

		Assert.assertTrue("Incorrect default selection",
				driver.findElement(byWComponentPath("WDropdown", preferenceParam)).isSelected());

		driver.findElement(byWComponentPath("WDropdown")).click();
		driver.findElement(byWComponentPath("WDropdown", "")).click();
		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Incorrect default selection",
				driver.findElement(byWComponentPath("WDropdown", preferenceParam)).isSelected());
	}
}
