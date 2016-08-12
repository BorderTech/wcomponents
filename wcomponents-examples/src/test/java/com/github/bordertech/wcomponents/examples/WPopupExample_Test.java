package com.github.bordertech.wcomponents.examples;

import java.util.Set;
import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WPopupExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
//@Category(SeleniumTests.class)
//@RunWith(MultiBrowserRunner.class)
public class WPopupExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WPopupExample_Test.
	 */
	public WPopupExample_Test() {
		super(new WPopupExample());
	}

	// Joshua-Barclay: Deactivate this test as the requirement to access an external website is not ideal for repeatable testing.
	//	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Set<String> oldWindows = driver.getWindowHandles();

		driver.findElement(byWComponentPath("WButton[0]")).click();
		Set<String> newWindows = driver.getWindowHandles();
		newWindows.removeAll(oldWindows);

		Assert.assertEquals("Unexpected number of windows", 1, newWindows.size());
		String newWindowName = newWindows.iterator().next();

		WebDriver newWindowDriver = driver.switchTo().window(newWindowName);
		//Force the driver to wait until an element is present.
		newWindowDriver.findElement(By.tagName("html")).click();
		String url = newWindowDriver.getCurrentUrl();
		Assert.assertTrue("Incorrect popup URL: " + url, url.startsWith("http://www.example.com/"));
	}
}
