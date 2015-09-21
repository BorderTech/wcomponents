package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WPopupExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WPopupExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new WPopupExample_Test.
	 */
	public WPopupExample_Test() {
		super(new WPopupExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		Set<String> oldWindows = driver.getWindowHandles();
		String newWindowName = null;

		driver.findElement(byWComponentPath("WButton[0]")).click();

		// Wait for theme JS to pop-up new window (100ms polling interval, 10s max)
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}

			Set<String> newWindows = driver.getWindowHandles();
			newWindows.removeAll(oldWindows);

			if (newWindows.size() == 1) {
				newWindowName = newWindows.iterator().next();
				break;
			}
		}

		String url = driver.switchTo().window(newWindowName).getCurrentUrl();
		Assert.assertTrue("Incorrect popup URL", url.startsWith("http://www.ubuntu.com/"));
	}
}
