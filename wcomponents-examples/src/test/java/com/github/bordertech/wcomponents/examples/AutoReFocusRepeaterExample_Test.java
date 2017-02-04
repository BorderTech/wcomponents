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
 * Selenium unit tests for {@link AutoReFocusRepeaterExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AutoReFocusRepeaterExample_Test extends WComponentExamplesTestCase {

	/**
	 * The web driver used in these tests.
	 */
	private static SeleniumWComponentsWebDriver driver;

	/**
	 * Root of the complex selector paths.
	 */
	private static final String ROOT_PATH = "AutoReFocusRepeaterExample/WRepeater/AutoReFocusRepeaterExample$FocusRepeatRenderer";

	/**
	 * Creates a new AutoReFocusRepeaterExample_Test.
	 */
	public AutoReFocusRepeaterExample_Test() {
		super(new AutoReFocusRepeaterExample());
	}

	@Before
	public void beforeEach() {
		driver = getDriver();
	}

	@Test
	public void testAutoReFocusWDropdowns() {
		String[] paths = {
			ROOT_PATH + "[0]/WDropdownTriggerActionExample/WDropdown",
			ROOT_PATH + "[1]/WDropdownTriggerActionExample/WDropdown"
		};
		for (String path : paths) {
			driver.findWDropdown(byWComponentPath(path)).getInputField().click();

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

	@Test
	public void testAutoReFocusWRadioButtons() {
		String[] paths = {
			ROOT_PATH + "[0]/WRadioButtonTriggerActionExample/WRadioButton",
			ROOT_PATH + "[1]/WRadioButtonTriggerActionExample/WRadioButton"
		};
		for (String path : paths) {
			driver.findWRadioButton(byWComponentPath(path)).getInputField().click();
			driver.waitForPageReady();
			Assert.assertEquals("Incorrect focus",
					driver.findWRadioButton(byWComponentPath(path)).getActiveId(),
					driver.switchTo().activeElement().getAttribute("id"));
		}
	}
}
