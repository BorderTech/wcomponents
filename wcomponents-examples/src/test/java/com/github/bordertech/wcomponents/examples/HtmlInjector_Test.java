package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * Selenium unit tests for {@link HtmlInjector}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class HtmlInjector_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new HtmlInjector_Test.
	 */
	public HtmlInjector_Test() {
		super(new HtmlInjector());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		String divText = "Hello world&";
		String html = "<div id='HtmlInjector_Test.id'>" + WebUtilities.encode(divText) + "</div>";

		driver.findWTextArea(byWComponentPath("WTextArea")).sendKeys(html);
		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertEquals("Incorrect div text", divText, driver.findElement(By.id("HtmlInjector_Test.id")).getText());
	}
}
