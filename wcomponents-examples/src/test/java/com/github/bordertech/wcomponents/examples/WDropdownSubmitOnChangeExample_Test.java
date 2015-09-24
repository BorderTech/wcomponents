package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WDropdownSubmitOnChangeExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownSubmitOnChangeExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WDropdownSubmitOnChangeExample_Test.
	 */
	public WDropdownSubmitOnChangeExample_Test() {
		super(new WDropdownSubmitOnChangeExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Select "ACT" from State dropdown
		driver.findElement(byWComponentPath("WDropdown[0]", "ACT")).click();

		// Should have round-tripped, check server and client-side states
		Assert.assertTrue("Incorrect state selection on server", driver.getPageSource().contains(
				"the heart of the nation!"));
		Assert.assertTrue("Incorrect state selection on client", driver.findElement(
				byWComponentPath("WDropdown[0]", "ACT")).isSelected());

		// Select "Woden" from Region dropdown
		driver.findElement(byWComponentPath("WDropdown[1]", "City")).click();

		// No round-trip for last dropdown
		Assert.assertTrue("Incorrect region selection on client", driver.findElement(
				byWComponentPath("WDropdown[1]", "City")).isSelected());
	}
}
