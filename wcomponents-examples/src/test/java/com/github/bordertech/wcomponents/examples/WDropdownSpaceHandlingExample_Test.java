package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.util.TreeUtil;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Selenium unit tests for {@link WDropdownSpaceHandlingExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownSpaceHandlingExample_Test extends WComponentExamplesTestCase {

	/**
	 * Construct test.
	 */
	public WDropdownSpaceHandlingExample_Test() {
		super(new WDropdownSpaceHandlingExample());
	}

	@Test
	public void testDropdown() {
		WDropdownSpaceHandlingExample example = (WDropdownSpaceHandlingExample) getUi();
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		UIContext uic = getUserContextForSession();
		UIContextHolder.pushContext(uic);
		try {
			WDropdown dropdown = (WDropdown) TreeUtil.findWComponent(example, new String[]{"WDropdown"}).
					getComponent();
			List<?> options = dropdown.getOptions();


			for (Object option : options) {
				driver.findElement(byWComponent(dropdown, option)).click();
				driver.findWButtonByText("Submit").click();

				Assert.assertEquals("Incorrect option selected", option, dropdown.getSelected());
			}
		} finally {
			UIContextHolder.popContext();
		}
	}
}
