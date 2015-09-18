package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link ForwardExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class ForwardExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new ForwardExample_Test.
	 */
	public ForwardExample_Test() {
		super(new ForwardExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		String url = "http://www.ubuntu.com/";
		driver.findElement(byWComponentPath("WTextField")).clear();
		driver.findElement(byWComponentPath("WTextField")).sendKeys(url);
		driver.findElement(byWComponentPath("WButton")).click();

		Assert.assertTrue("Incorrect forward location", driver.getCurrentUrl().startsWith(url));
	}
}
