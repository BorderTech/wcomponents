package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WDropdownTriggerActionExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownTriggerActionExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WDropdownTriggerActionExample_Test.
	 */
	public WDropdownTriggerActionExample_Test() {
		super(new WDropdownTriggerActionExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		WDropdownTriggerActionExample ui = (WDropdownTriggerActionExample) getUi();

		// Select "ACT" from State dropdown
		driver.findElement(byWComponent(ui.getStateDropdown(), "ACT")).click();
		Assert.assertTrue("Incorrect region selection on client", driver.findElement(byWComponent(
				ui.getRegionDropdown(), "")).isSelected());

		// Should have round-tripped, check server and client-side states
		Assert.assertEquals("Incorrect state selection on server", "ACT", ui.getStateDropdown().
				getSelected());
		Assert.assertTrue("Incorrect state selection on client", driver.findElement(byWComponent(ui.
				getStateDropdown(), "ACT")).isSelected());

		// Select "Woden" from Region dropdown
		driver.findElement(byWComponent(ui.getRegionDropdown(), "Woden")).click();

		// Should have round-tripped, check server and client-side states
		Assert.assertEquals("Incorrect region selection on server", "Woden", ui.getRegionDropdown().
				getSelected());
		Assert.assertTrue("Incorrect region selection on client", driver.findElement(byWComponent(
				ui.getRegionDropdown(), "Woden")).isSelected());

		// Select "Torrens" from Suburb dropdown (no round trip)
		driver.findElement(byWComponent(ui.getSuburbDropdown(), "Torrens")).click();
		Assert.assertTrue("Incorrect suburb selection on client", driver.findElement(byWComponent(
				ui.getSuburbDropdown(), "Torrens")).isSelected());

		// Select "VIC" from the State dropdown
		driver.findElement(byWComponent(ui.getStateDropdown(), "VIC")).click();
		Assert.assertTrue("Incorrect region selection on client", driver.findElement(byWComponent(
				ui.getRegionDropdown(), "")).isSelected());

		// Select "Melbourne" from Region dropdown
		driver.findElement(byWComponent(ui.getRegionDropdown(), "Melbourne")).click();

		// Select "Torrens" from Suburb dropdown (no round trip)
		driver.findElement(byWComponent(ui.getSuburbDropdown(), "Blackburn")).click();
		Assert.assertTrue("Incorrect suburb selection on client", driver.findElement(byWComponent(
				ui.getSuburbDropdown(), "Blackburn")).isSelected());
	}
}
