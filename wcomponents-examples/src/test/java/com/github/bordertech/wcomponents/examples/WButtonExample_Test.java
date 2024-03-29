package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Selenium unit tests for {@link WButtonExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WButtonExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WButtonExample_Test.
	 */
	public WButtonExample_Test() {
		super(new WButtonExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WButton[0]")).click();
		assertHasMessage("Plain button should have been pressed", "Plain button pressed");

		driver.findElement(byWComponentPath("WButton[1]")).click();
		assertHasMessage("Link button should have been pressed", "Link button pressed");

		driver.findElement(byWComponentPath("WButton[2]")).click();
		Assert.assertFalse("Plain button should be disabled", driver.findElement(byWComponentPath(
				"WButton[0]")).isEnabled());
		Assert.assertFalse("Link button should be disabled", driver.findElement(byWComponentPath(
				"WButton[1]")).isEnabled());

		driver.findElement(byWComponentPath("WButton[2]")).click();
		Assert.assertTrue("Plain button should be enabled", driver.findElement(byWComponentPath(
				"WButton[0]")).isEnabled());
		Assert.assertTrue("Link button should be enabled", driver.findElement(byWComponentPath(
				"WButton[1]")).isEnabled());
	}

	/**
	 * Asserts that the given message is being displayed.
	 *
	 * @param assertText the assertion being tested.
	 * @param message the message to search for.
	 */
	private void assertHasMessage(final String assertText, final String message) {
		SeleniumWComponentsWebDriver driver = getDriver();
		WebElement messageElement = driver.findElementImmediate(byWComponentPath("WMessageBox")).findElementImmediate(By.tagName("div"));
		Assert.assertTrue(assertText, messageElement.getText().contains(message));
	}
}
