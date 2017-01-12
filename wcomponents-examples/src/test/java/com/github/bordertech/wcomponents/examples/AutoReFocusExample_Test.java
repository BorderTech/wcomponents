package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.util.TreeUtil;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link AutoReFocusExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AutoReFocusExample_Test extends WComponentExamplesTestCase {

	/**
	 * The WebDriver used in these tests.
	 */
	private static SeleniumWComponentsWebDriver driver;

	/**
	 * Creates a new AutoReFocusExample_Test.
	 */
	public AutoReFocusExample_Test() {
		super(new AutoReFocusExample());
	}

	@Before
	public void beforeEach() {
		driver = getDriver();
	}

	@Test
	public void testAutoRefocusWButton() {
		String path = "TextDuplicator/WButton";
		driver.findElement(byWComponentPath(path)).click();

		driver.waitForPageReady();
		Assert.assertEquals("Incorrect focus",
				driver.findElement(byWComponentPath(path)).getActiveId(),
				driver.switchTo().activeElement().getAttribute("id"));
	}

	@Test
	public void testRefocusWDropdown() {
		String path = "WDropdownSubmitOnChangeExample/WDropdown[0]";
		driver.findWDropdown(byWComponentPath(path)).click();

		// The dropdowns in the example need something to be selected to trigger the submit
		WComponent comp = TreeUtil.findWComponent(getUi(), path.split("/")).getComponent();

		if (comp instanceof WDropdown) {
			WDropdown dropdown = (WDropdown) comp;
			driver.findElement(byWComponentPath(path, dropdown.getOptions().get(1))).click();
		}

		driver.waitForPageReady();
		Assert.assertEquals("Incorrect focus",
				driver.findWDropdown(byWComponentPath(path)).getActiveId(),
				driver.switchTo().activeElement().getAttribute("id"));
	}
}
