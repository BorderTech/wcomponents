package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
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
public class WDialogExample_Test extends WComponentExamplesTestCase {

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
		SeleniumWComponentsWebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		String expectedText = example.getModalText();
		final WButton testButton = example.getModalButton();

		// Display the modal dialog
		driver.findElement(byWComponent(testButton)).click();

		Assert.assertTrue("Dialog must be open.", driver.getDialog().isOpen());

		driver.getDialog().close();

		Assert.assertFalse("Should not be displaying the dialog", driver.isOpenDialog());
		Assert.assertFalse("Should not be displaying the dialog", driver.getPageSource().contains(
				expectedText));
	}

	@Test
	public void testModalDialogSearch() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();
		WDialogExample example = (WDialogExample) getUi();
		final WButton testButton = example.getModalButton();

		// Display the modal dialog
		driver.findElement(byWComponent(testButton)).click();

		driver.findWTextField(byWComponentPath("WDialogExample$SelectPersonPanel/WTextField[0]")).
				sendKeys("First");
		driver.findWTextField(byWComponentPath("WDialogExample$SelectPersonPanel/WTextField[1]")).
				sendKeys("Last");
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WButton[1]")).click();

		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WRadioButtonSelect",
				"Last, First")).click();
		driver.findElement(byWComponentPath("WDialogExample$SelectPersonPanel/WButton[1]")).click();

		String message = driver.findElementImmediate(byWComponentPath("WMessageBox")).getText();
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
