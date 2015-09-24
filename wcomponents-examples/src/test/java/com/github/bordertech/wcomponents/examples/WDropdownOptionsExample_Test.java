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
import org.openqa.selenium.WebElement;

/**
 * Unit test for the {@link WDropDownOptionsExample} example.
 *
 * @author Steve Harney
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WDropdownOptionsExample_Test extends WComponentSeleniumTestCase {

	/**
	 * constructor.
	 */
	public WDropdownOptionsExample_Test() {
		super(new WDropdownOptionsExample());
	}

	/**
	 * Updates the controls to produce the expected dropdown.
	 *
	 * @param driver the selenium driver
	 * @param type the type of drop down.
	 * @param actionNumber the number of the action to use, base off position in the radio button select.
	 */
	private void configureDropDown(final WebDriver driver, final WDropdown.DropdownType type,
			final int actionNumber) {

		// action on change.
		WebElement actionOnChange = driver.findElement(byWComponentPath("WCheckBox[1]"));
		while (!actionOnChange.isSelected()) {
			actionOnChange.click();
		}

		WebElement action = driver.findElement(byWComponentPath("WCheckBox[" + actionNumber + "]"));
		while (!action.isSelected()) {
			action.click();
		}
		driver.findElement(byWComponentPath("WRadioButtonSelect[0]", type)).click();

		// set null option to true.
		WebElement includeNullOption = driver.findElement(byWComponentPath("WCheckBox[0]"));

		while (!includeNullOption.isSelected()) {
			includeNullOption.click();
		}

		driver.findElement(byWComponentPath("WButton")).click();
	}

	/**
	 * submit on change test.
	 */
	@Test
	public void testExampleSubmitOnChange() {
		WDropdownOptionsExample example = (WDropdownOptionsExample) getUi();

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		WDropdown.DropdownType type = WDropdown.DropdownType.NATIVE;
		configureDropDown(driver, type, 4);

		WDropdown dropdown = (WDropdown) TreeUtil.findWComponent(example, new String[]{"WDropdown"})
				.getComponent();
		List<?> options = dropdown.getOptions();

		for (Object option : options) {

			driver.findElement(byWComponent(dropdown, option)).click();

			Assert.assertEquals("Incorrect option selected", option, dropdown.getSelected());
			Assert.assertEquals("Incorrect text field text", (option == null ? "" : option),
					driver.findElement(byWComponentPath("WPanel")).getText());
		}
	}

	/**
	 * Ajax test.
	 */
	@Test
	public void testExampleAjax() {
		WDropdownOptionsExample example = (WDropdownOptionsExample) getUi();

		// Launch the web browser to the LDE
		WebDriver driver = getDriver();

		WDropdown.DropdownType type = WDropdown.DropdownType.NATIVE;
		configureDropDown(driver, type, 2);

		WDropdown dropdown = (WDropdown) TreeUtil.findWComponent(example, new String[]{"WDropdown"})
				.getComponent();
		List<?> options = dropdown.getOptions();

		for (Object option : options) {
			driver.findElement(byWComponent(dropdown, option)).click();

			Assert.assertEquals("Incorrect option selected", option, dropdown.getSelected());
			Assert.assertEquals("Incorrect text field text", (option == null ? "" : option),
					driver.findElement(byWComponentPath("WPanel")).getText());
		}
	}
}
