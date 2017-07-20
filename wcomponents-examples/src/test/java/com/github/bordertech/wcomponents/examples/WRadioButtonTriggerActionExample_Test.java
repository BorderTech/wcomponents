package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WRadioButtonTriggerActionExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WRadioButtonTriggerActionExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WRadioButtonTriggerActionExample_Test.
	 */
	public WRadioButtonTriggerActionExample_Test() {
		super(new WRadioButtonTriggerActionExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// "Lunch" should be initially selected
		Assert.assertTrue("Lunch should be selected by default", driver.findWRadioButton(byWComponentPath("WRadioButton[1]")).isSelected());

		// Select "Breakfast"
		driver.findWRadioButton(byWComponentPath("WRadioButton[0]")).click();
		Assert.assertTrue("Should have submitted 'Breakfast' to server", getMessageText().startsWith("Breakfast selected"));

		// Select "Lunch"
		driver.findWRadioButton(byWComponentPath("WRadioButton[1]")).click();
		Assert.assertTrue("Should have submitted 'Lunch' to server", getMessageText().startsWith("Lunch selected"));

		// Select "Dinner"
		driver.findWRadioButton(byWComponentPath("WRadioButton[2]")).click();
		Assert.assertTrue("Should have submitted 'Dinner' to server", getMessageText().startsWith("Dinner selected"));

		// A round-trip should not trigger the action to update the message text (which includes a timestamp).
		String oldText = getMessageText();
		Assert.assertEquals("Action should not have been fired", oldText, getMessageText());
	}

	/**
	 * @return the message currently being displayed by the example.
	 */
	private String getMessageText() {
		WebDriver driver = getDriver();
		WRadioButtonTriggerActionExample ui = (WRadioButtonTriggerActionExample) getUi();

		return driver.findElement(byWComponent(ui.getInformationTextBox())).getText().trim();
	}
}
