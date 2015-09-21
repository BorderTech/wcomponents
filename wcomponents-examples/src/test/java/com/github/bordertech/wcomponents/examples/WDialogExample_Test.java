package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WDialogExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDialogExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WDialogExample_Test.
	 */
	public WDialogExample_Test() {
		super(new WDialogExample());
	}

	@Test
	public void testModalDialogNotPresentOnLoad() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getModalText();

		Assert.assertFalse("Should not be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}

	@Test
	public void testModalDialog() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getModalText();
		final WButton testButton = example.getModalButton();

		// Display the modal dialog
		driver.findElement(byWComponent(testButton)).click();
		Assert.assertTrue("Should be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}

	@Test
	public void testModalDialogCloseOnCancel() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getModalText();
		final WButton testButton = example.getModalButton();

		// Display the modal dialog
		driver.findElement(byWComponent(testButton)).click();

		// Cancel the dialog
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WCancelButton[0]")).
				click();
		Assert.assertFalse("Should not be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}

	@Test
	public void testModalDialogSearch() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		final WButton testButton = example.getModalButton();

		// Display the modal dialog
		driver.findElement(byWComponent(testButton)).click();

// TODO Wait for client side validation to be worked out
//        // Click search without entering mandatory fields - should give a validation error
//        driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel$SearchPanel/WButton")).click();
//
//        Assert.assertTrue("Should have a validation error", driver.getPageSource().contains("Please enter First name"));
		// Enter search info & submit
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WTextField[0]")).
				sendKeys("First");
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WTextField[1]")).
				sendKeys("Last");
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WButton[2]")).click();

// TODO Wait for client side validation to be worked out
//        // Try to select nothing
//        driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WButton[1]")).click();
//
//        Assert.assertTrue("Should have a validation error", driver.getPageSource().contains("Please select a name from the list"));
		// Select first result
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WRadioButtonSelect",
				"Last, First")).click();
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WButton[1]")).click();

		String message = driver.findElement(byWComponentPath("WMessageBox")).getText();
		Assert.assertTrue("Incorrect message text", message.contains("Selected: Last, First"));
	}

	@Test
	public void testModelessDialogNotPresentOnLoad() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getNonModalText();
		Assert.assertFalse("Should not be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}

	@Test
	public void testModelessDialog() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getNonModalText();
		final WButton testButton = example.getNonModalButton();

		// Display the modeless dialog
		driver.findElement(byWComponent(testButton)).click();

		Assert.assertTrue("Should not be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}
}
