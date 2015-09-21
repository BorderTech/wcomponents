package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link EntryFieldExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class EntryFieldExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new EntryFieldExample_Test.
	 */
	public EntryFieldExample_Test() {
		super(new EntryFieldExample());
	}

	@Test
	public void testInitialState() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Test initial state
		Assert.assertEquals("Incorrect default values", "", driver.findElement(byWComponentPath(
				"WTextArea")).getAttribute("value"));

		Assert.assertEquals("Incorrect default text for tf1", "blah blah", driver.findElement(
				byWComponentPath("WTextField[0]")).getAttribute("value"));
		Assert.assertEquals("Incorrect default text for tf2", "abc", driver.findElement(
				byWComponentPath("WTextField[1]")).getAttribute("value"));
		Assert.assertEquals("Incorrect default text for tf3", "abc", driver.findElement(
				byWComponentPath("WTextField[2]")).getAttribute("value"));
		Assert.assertEquals("Incorrect default text for tf4", "", driver.findElement(
				byWComponentPath("WTextField[3]")).getAttribute("value"));

		Assert.assertTrue("Incorrect default value for drop1", driver.findElement(byWComponentPath(
				"WDropdown[0]", "One")).isSelected());
		Assert.assertTrue("Incorrect default value for drop2", driver.findElement(byWComponentPath(
				"WDropdown[1]", "")).isSelected());

		Assert.assertEquals("Incorrect default value for multi1", "", driver.findElement(
				byWComponentPath("WMultiSelect[0]")).getAttribute("value"));
		Assert.assertEquals("Incorrect default value for multi2", "", driver.findElement(
				byWComponentPath("WMultiSelect[1]")).getAttribute("value"));

		Assert.assertFalse("Incorrect default value for checkbox group", driver.findElement(
				byWComponentPath("WCheckBoxSelect", "Female")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox group", driver.findElement(
				byWComponentPath("WCheckBoxSelect", "Male")).isSelected());

		Assert.assertFalse("Incorrect default value for radio button 1", driver.findElement(
				byWComponentPath("WRadioButton[0]")).isSelected());
		Assert.assertFalse("Incorrect default value for radio button 2", driver.findElement(
				byWComponentPath("WRadioButton[1]")).isSelected());
		Assert.assertFalse("Incorrect default value for radio button 3", driver.findElement(
				byWComponentPath("WRadioButton[2]")).isSelected());

		Assert.assertFalse("Incorrect default value for checkbox 1", driver.findElement(
				byWComponentPath("WCheckBox[0]")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox 2", driver.findElement(
				byWComponentPath("WCheckBox[1]")).isSelected());
		Assert.assertFalse("Incorrect default value for checkbox 3", driver.findElement(
				byWComponentPath("WCheckBox[2]")).isSelected());
	}

	@Test
	public void testDefaultSubmit() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Need to scroll the button into view, otherwise Selenium won't click it.
		driver.findElement(byWComponentPath("WTextArea")).sendKeys(" ");
		driver.findElement(byWComponentPath("WButton")).click();

		String text = driver.findElement(byWComponentPath("WTextArea")).getAttribute("value");

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
		WebDriver driver = getDriver();

		driver.findElement(byWComponentPath("WTextField[0]")).clear();
		driver.findElement(byWComponentPath("WTextField[0]")).sendKeys("tf1");

		driver.findElement(byWComponentPath("WTextField[1]")).clear();
		driver.findElement(byWComponentPath("WTextField[1]")).sendKeys("tf2");

		driver.findElement(byWComponentPath("WTextField[3]")).clear();
		driver.findElement(byWComponentPath("WTextField[3]")).sendKeys("tf4");

		driver.findElement(byWComponentPath("WDropdown[0]", "Three")).click();

		driver.findElement(byWComponentPath("WDropdown[1]", "Cat")).click();

		driver.findElement(byWComponentPath("WMultiSelect[0]", "Circle")).click();
		driver.findElement(byWComponentPath("WMultiSelect[0]", "Triangle")).click();

		driver.findElement(byWComponentPath("WMultiSelect[1]", "AUSTRALIA")).click();
		driver.findElement(byWComponentPath("WMultiSelect[1]", "NEW ZEALAND")).click();

		driver.findElement(byWComponentPath("WCheckBoxSelect", "Male")).click();

		driver.findElement(byWComponentPath("WRadioButton[1]")).click();

		driver.findElement(byWComponentPath("WCheckBox[1]")).click();
		driver.findElement(byWComponentPath("WCheckBox[2]")).click();

		driver.findElement(byWComponentPath("WButton")).click();

		String text = driver.findElement(byWComponentPath("WTextArea")).getAttribute("value");

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
