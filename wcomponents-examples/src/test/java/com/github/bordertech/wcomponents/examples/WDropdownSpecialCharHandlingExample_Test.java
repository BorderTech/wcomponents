package com.github.bordertech.wcomponents.examples;

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
 * Selenium unit tests for {@link WDropdownSpecialCharHandlingExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownSpecialCharHandlingExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Construct test.
	 */
	public WDropdownSpecialCharHandlingExample_Test() {
		super(new WDropdownSpecialCharHandlingExample());
	}

	@Test
	public void testExample() {
		WDropdownSpecialCharHandlingExample example = (WDropdownSpecialCharHandlingExample) getUi();

		WDropdown dropdown = (WDropdown) TreeUtil.findWComponent(example, new String[]{"WDropdown"}).
				getComponent();
		List<?> options = dropdown.getOptions();

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		for (Object option : options) {
			driver.findElement(byWComponent(dropdown, option)).click();
			driver.findElement(byWComponentPath("WButton")).click();

			Assert.assertEquals("Incorrect option selected", option, dropdown.getSelected());
			Assert.assertEquals("Incorrect text field text", (option == null ? "" : option),
					driver.findElement(byWComponentPath("WTextField")).getAttribute("value"));
		}
	}
}
