package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.examples.theme.WCheckBoxSelectExample;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WCheckBoxSelectExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WCheckBoxSelectExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WCheckBoxSelectExample_Test.
	 */
	public WCheckBoxSelectExample_Test() {
		super(new WCheckBoxSelectExample());
	}

	@Test
	public void testStatesExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Select "ACT", "NSW" and "QLD"
		driver.findElement(byWComponentPath("WCheckBoxSelect[0]", "Australian Capital Territory")).
				click();
		driver.findElement(byWComponentPath("WCheckBoxSelect[0]", "New South Wales")).click();
		driver.findElement(byWComponentPath("WCheckBoxSelect[0]", "Queensland")).click();

		Assert.assertFalse("Should not have submitted selections yet.", driver.getPageSource().
				contains("The selected states are"));

		// Click "Update"
		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Incorrect selected options",
				driver.getPageSource().contains(
						"The selected states are: [Australian Capital Territory, New South Wales, Queensland]"));

		// De-select "NSW"
		driver.findElement(byWComponentPath("WCheckBoxSelect[0]", "New South Wales")).click();

		// Click "Update"
		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Incorrect selected options",
				driver.getPageSource().contains(
						"The selected states are: [Australian Capital Territory, Queensland]"));
	}

	@Test
	public void testCarsExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// note we are using sendKeys instead of click there appears
		// to be a selenium bug where there are multiple check boxes
		// on a page.
		// this has been reported http://code.google.com/p/selenium/issues/detail?id=3025
		driver.findElement(byWComponentPath("WCheckBoxSelect[1]", "Toyota, Prius")).click();
		driver.findElement(byWComponentPath("WCheckBoxSelect[1]", "Nissan, Skyline")).click();

		Assert.assertFalse("Should not have submitted selections yet.", driver.getPageSource().
				contains("The selected cars are"));

		// Click "Update"
		driver.findElement(byWComponentPath("WButton[1]")).click();

		Assert.assertTrue("Incorrect selected options",
				driver.getPageSource().contains(
						"The selected cars are: [{ Nissan, Skyline }, { Toyota, Prius }]"));

		// De-select the toyota
		driver.findElement(byWComponentPath("WCheckBoxSelect", "Toyota, Prius")).click();

		// Click "Update"
		driver.findElement(byWComponentPath("WButton[1]")).click();

		Assert.assertTrue("Incorrect selected options",
				driver.getPageSource().contains("The selected cars are: [{ Nissan, Skyline }]"));
	}
}
