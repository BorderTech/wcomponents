package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.util.TreeUtil;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WDropdownSpaceHandlingExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownSpaceHandlingExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Construct test.
	 */
	public WDropdownSpaceHandlingExample_Test() {
		super(new WDropdownSpaceHandlingExample());
	}

	@Test
	public void testDropdown() {
		WDropdownSpaceHandlingExample example = (WDropdownSpaceHandlingExample) getUi();

		WDropdown dropdown = (WDropdown) TreeUtil.findWComponent(example, new String[]{"WDropdown"}).
				getComponent();
		List<?> options = dropdown.getOptions();

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		for (Object option : options) {
			driver.findElement(byWComponent(dropdown, option)).click();
			driver.findElement(byWComponentPath("WButton")).click();

			Assert.assertEquals("Incorrect option selected", option, dropdown.getSelected());
		}
	}

	@Test
	public void testCheckbox() {
		WDropdownSpaceHandlingExample example = (WDropdownSpaceHandlingExample) getUi();

		WCheckBoxSelect group = (WCheckBoxSelect) TreeUtil.findWComponent(example,
				new String[]{"WCheckBoxSelect"}).getComponent();
		List<?> options = group.getOptions();

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		for (Object option : options) {
			driver.findElement(byWComponent(group, option)).click();
			driver.findElement(byWComponentPath("WButton")).click();

			Assert.assertEquals("Incorrect option selected", option, group.getSelected().get(0));

			// De-select option
			driver.findElement(byWComponent(group, option)).click();
		}
	}
}
