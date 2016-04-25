package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link SimpleTabs}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class SimpleTabs_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new SimpleTabs_Test.
	 */
	public SimpleTabs_Test() {
		super(createSimpleTabs());
	}

	/**
	 * @return a simple tabs for testing.
	 */
	private static WComponent createSimpleTabs() {
		SimpleTabs tabs = new SimpleTabs();

		tabs.addTab(new WText("tab1content"), "tab1name");
		tabs.addTab(new WText("tab2content"), "tab2name");
		tabs.addTab(new WText("tab3content"), "tab3name");

		return tabs;
	}

	@Test
	public void testExample() throws IOException {
		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		// Tab1 should be visible by default.
		assertTabVisible(1);

		// No change
		driver.findElement(byWComponentPath("WButton[0]")).click();
		assertTabVisible(1);

		// Tab 2
		driver.findElement(byWComponentPath("WButton[1]")).click();
		assertTabVisible(2);

		// Tab 3
		driver.findElement(byWComponentPath("WButton[2]")).click();
		assertTabVisible(3);
	}

	/**
	 * Asserts that the given tab is the only visible one.
	 *
	 * @param index the tab index to assert visible.
	 */
	private void assertTabVisible(final int index) {
		WebDriver driver = getDriver();

		for (int i = 1; i <= 3; i++) {
			String tabHeading = "[tab" + i + "name]:";
			String tabContent = "tab" + i + "content";

			if (i == index) {
				Assert.assertTrue("Tab " + i + " heading should be visible", driver.getPageSource().
						contains(tabHeading));
				Assert.assertTrue("Tab " + i + " content should be visible", driver.getPageSource().
						contains(tabContent));
			} else {
				Assert.assertFalse("Tab " + i + " heading should not be visible", driver.
						getPageSource().contains(tabHeading));
				Assert.assertFalse("Tab " + i + " content should not be visible", driver.
						getPageSource().contains(tabContent));
			}
		}
	}
}
