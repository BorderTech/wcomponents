package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.WComponentSeleniumTestCase;
import com.github.bordertech.wcomponents.util.TreeUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link AutoReFocusRepeaterExample}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AutoReFocusRepeaterExample_Test extends WComponentSeleniumTestCase {

	/**
	 * Creates a new AutoReFocusRepeaterExample_Test.
	 */
	public AutoReFocusRepeaterExample_Test() {
		super(new AutoReFocusRepeaterExample());
	}

	@Test
	public void testAutoReFocus() {
		String[] paths
				= {
					"WDropdownTriggerActionExample[0]/WDropdown",
					"WDropdownTriggerActionExample[1]/WDropdown",
					"WRadioButtonTriggerActionExample[0]/WRadioButton",
					"WRadioButtonTriggerActionExample[1]/WRadioButton"
				};

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		for (String path : paths) {
			driver.findElement(byWComponentPath(path)).click();

			// The dropdowns in the example need something to be selected to trigger the submit
			WComponent comp = TreeUtil.findWComponent(getUi(), path.split("/")).getComponent();

			if (comp instanceof WDropdown) {
				WDropdown dropdown = (WDropdown) comp;
				driver.findElement(byWComponentPath(path, dropdown.getOptions().get(0))).click();
			}

			Assert.assertEquals("Incorrect focus for " + path,
					driver.findElement(byWComponentPath(path)).getAttribute("id"),
					driver.switchTo().activeElement().getAttribute("id"));
		}
	}
}
