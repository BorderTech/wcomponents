package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWDropdownExample;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link AjaxWDropdownExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AjaxWDropdownExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a AjaxWDropdownExample_Test.
	 */
	public AjaxWDropdownExample_Test() {
		super(new AjaxWDropdownExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Region and suburb drop-downs should be blank to begin with
		Assert.assertTrue("Region drop-down should be empty", !driver.getPageSource().contains("Woden"));
		Assert.assertTrue("Suburb drop-down should be empty", !driver.getPageSource().contains("Torrens"));

		// Select "ACT" from State dropdown
		driver.findElement(byWComponentPath("WDropdown[0]")).click();
		driver.findElement(byWComponentPath("WDropdown[0]", "ACT")).click();

		// Select "Woden" from Region dropdown
		Assert.assertTrue("Region drop-down should contain 'Woden'", driver.getPageSource().contains("Woden"));
		Assert.assertTrue("Suburb drop-down should be empty", !driver.getPageSource().contains("Torrens"));
		driver.findElement(byWComponentPath("WDropdown[1]")).click();
		driver.findElement(byWComponentPath("WDropdown[1]", "Woden")).click();

		// Select "Torrens" from Suburb dropdown
		Assert.assertTrue("Suburb drop-down should contain 'Torrens'", driver.getPageSource().contains("Torrens"));
		driver.findElement(byWComponentPath("WDropdown[2]")).click();
		driver.findElement(byWComponentPath("WDropdown[2]", "Torrens")).click();

		// No server-side interaction for last dropdown
		Assert.assertTrue("Incorrect suburb selection on client", driver.findElement(
				byWComponentPath("WDropdown[2]", "Torrens")).isSelected());
	}
}
