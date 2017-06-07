package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link WRadioButtonSubmitOnChangeExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WRadioButtonSubmitOnChangeExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WRadioButtonSubmitOnChangeExample_Test.
	 */
	public WRadioButtonSubmitOnChangeExample_Test() {
		super(new WRadioButtonSubmitOnChangeExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Select "ACT"
		driver.findWRadioButton(byWComponentPath("WRadioButton", "ACT")).click();

		// Should have round-tripped, check server and client-side states
		Assert.assertTrue("Incorrect state selection on server", driver.getPageSource().contains("the heart of the nation!"));
		Assert.assertTrue("Incorrect state selection on client", driver.findWRadioButton(byWComponentPath("WRadioButton", "ACT")).isSelected());
		Assert.assertEquals("", driver.findWDropdown(byWComponentPath("WDropdown")).getValue());


		// Select "City" from Region dropdown (no round trip)
		driver.findWDropdown(byWComponentPath("WDropdown")).click(); // click the dropdown to open the options
		driver.findElement(byWComponentPath("WDropdown", "City")).click();
		Assert.assertEquals("City", driver.findWDropdown(byWComponentPath("WDropdown")).getValue());

		// Select "NSW"
		driver.findWRadioButton(byWComponentPath("WRadioButton", "NSW")).click();

		// Should have round-tripped, check server and client-side states
		Assert.assertFalse("Incorrect state selection on server", driver.getPageSource().contains("the heart of the nation!"));
		Assert.assertTrue("Incorrect state selection on client", driver.findWRadioButton(byWComponentPath("WRadioButton", "NSW")).isSelected());
		Assert.assertEquals("", driver.findWDropdown(byWComponentPath("WDropdown")).getValue());

		// Select "Hunter" from Region dropdown (no round trip)
		driver.findWDropdown(byWComponentPath("WDropdown")).click(); // click the dropdown to open the options
		driver.findElement(byWComponentPath("WDropdown", "Hunter")).click();
		Assert.assertEquals("Hunter", driver.findWDropdown(byWComponentPath("WDropdown")).getValue());
	}
}
