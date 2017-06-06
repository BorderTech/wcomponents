package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link EntryFieldExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class EntryFieldExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new EntryFieldExample_Test.
	 */
	public EntryFieldExample_Test() {
		super(new EntryFieldExample());
	}

	@Test
	public void testInitialState() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Test initial state
		Assert.assertEquals("Incorrect default values", "", driver.findWTextArea(byWComponentPath("WTextArea")).getAttribute("value"));

		Assert.assertEquals("Incorrect default text for tf1", "blah blah", driver.findWTextField(byWComponentPath("WTextField[0]")).getValue());
		Assert.assertEquals("Incorrect default text for tf2", "abc", driver.findWTextField(byWComponentPath("WTextField[1]")).getValue());
		Assert.assertEquals("Incorrect default text for tf3", "abc", driver.findWTextField(byWComponentPath("WTextField[2]")).getValue());
		Assert.assertEquals("Incorrect default text for tf4", "", driver.findWTextField(byWComponentPath("WTextField[3]")).getValue());

		Assert.assertTrue("Incorrect default value for drop1", driver.findElement(byWComponentPath("WDropdown[0]", "One")).isSelected());
		Assert.assertTrue("Incorrect default value for drop2", driver.findElement(byWComponentPath("WDropdown[1]", "")).isSelected());

		Assert.assertEquals("Incorrect default value for multi1", "", driver.findWDropdown(byWComponentPath("WMultiSelect[0]")).getValue());
		Assert.assertEquals("Incorrect default value for multi2", "", driver.findWDropdown(byWComponentPath("WMultiSelect[1]")).getValue());

		Assert.assertFalse("Incorrect default value for checkbox group",
				driver.findElement(byWComponentPath("WCheckBoxSelect", "Female")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox group",
				driver.findElement(byWComponentPath("WCheckBoxSelect", "Male")).isSelected());

		Assert.assertFalse("Incorrect default value for radio button 1", driver.findWRadioButton(byWComponentPath("WRadioButton[0]")).isSelected());
		Assert.assertFalse("Incorrect default value for radio button 2", driver.findWRadioButton(byWComponentPath("WRadioButton[1]")).isSelected());
		Assert.assertFalse("Incorrect default value for radio button 3", driver.findWRadioButton(byWComponentPath("WRadioButton[2]")).isSelected());

		Assert.assertFalse("Incorrect default value for checkbox 1", driver.findWCheckBox(byWComponentPath("WCheckBox[0]")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox 2", driver.findWCheckBox(byWComponentPath("WCheckBox[1]")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox 3", driver.findWCheckBox(byWComponentPath("WCheckBox[2]")).isSelected());
	}

	@Test
	public void testDefaultSubmit() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		// Need to scroll the button into view, otherwise Selenium won't click it.
		driver.findWTextArea(byWComponentPath("WTextArea")).getInputField().sendKeys(" ");
		driver.findElement(byWComponentPath("WButton")).click();

		String text = driver.findWTextArea(byWComponentPath("WTextArea")).getValue();

		Assert.assertEquals("Incorrect text value",
				"Text 1 = blah blah"
				+ "\nText 2 = abc"
				+ "\nText 3 = abc"
				+ "\nText 4 = "
				+ "\nNumeric 1 = "
				+ "\nNumeric 2 = "
				+ "\nNumeric 3 = "
				+ "\nEmail = "
				+ "\nDrop 1 = One"
				+ "\nMulti1 = "
				+ "\nMulti2 = "
				+ "\nMultiCb = "
				+ "\nCheckboxes ="
				+ "\n",
				text);
	}

	@Test
	public void testDataEntry() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		driver.findWTextField(byWComponentPath("WTextField[0]")).clear();
		driver.findWTextField(byWComponentPath("WTextField[0]")).sendKeys("tf1");

		driver.findWTextField(byWComponentPath("WTextField[1]")).clear();
		driver.findWTextField(byWComponentPath("WTextField[1]")).sendKeys("tf2");

		driver.findWTextField(byWComponentPath("WTextField[3]")).clear();
		driver.findWTextField(byWComponentPath("WTextField[3]")).sendKeys("tf4");

		driver.findElement(byWComponentPath("WDropdown[0]", "Three")).click();

		driver.findElement(byWComponentPath("WDropdown[1]", "Cat")).click();

		driver.findElement(byWComponentPath("WMultiSelect[0]", "Circle")).click();
		driver.findElement(byWComponentPath("WMultiSelect[0]", "Triangle")).click();

		driver.findElement(byWComponentPath("WMultiSelect[1]", "AUSTRALIA")).click();
		driver.findElement(byWComponentPath("WMultiSelect[1]", "NEW ZEALAND")).click();

		driver.findElement(byWComponentPath("WCheckBoxSelect", "Male")).click();

		driver.findWRadioButton(byWComponentPath("WRadioButton[1]")).click();

		driver.findWCheckBox(byWComponentPath("WCheckBox[1]")).click();
		driver.findWCheckBox(byWComponentPath("WCheckBox[2]")).click();

		driver.findElement(byWComponentPath("WButton")).click();

		String text = driver.findWTextArea(byWComponentPath("WTextArea")).getValue();

		Assert.assertEquals("Incorrect text value",
				"Text 1 = tf1"
				+ "\nText 2 = tf2"
				+ "\nText 3 = abc"
				+ "\nText 4 = tf4"
				+ "\nNumeric 1 = "
				+ "\nNumeric 2 = "
				+ "\nNumeric 3 = "
				+ "\nEmail = "
				+ "\nDrop 1 = Three"
				+ "\nDrop 2 = Cat"
				+ "\nMulti1 = Circle, Triangle"
				+ "\nMulti2 = AUS, NZL"
				+ "\nMultiCb = M"
				+ "\nRadioButtonGroup = better"
				+ "\nCheckboxes = Lunch Dinner"
				+ "\n",
				text);
	}
}
