package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWRadioButtonSelectWebElement;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

/**
 * Unit tests of WRadioButtonSelectExample. Real use is as unit tests of the SeleniumWRadioButtonSelectWebElement.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WRadioButtonSelectExample_Test extends WComponentExamplesTestCase {

	/**
	 * @return an example which is interactive and has no default selection.
	 */
	private SeleniumWRadioButtonSelectWebElement getExampleNoSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWRadioButtonSelect(new ByLabel("Select a state or territory", false));
	}

	/**
	 * @return an example which is interactive and has no default selection.
	 */
	private SeleniumWRadioButtonSelectWebElement getExampleWithSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWRadioButtonSelect(new ByLabel("Frameless with default selection", false));
	}

	/**
	 * @return an example which is interactive and has no default selection.
	 */
	private SeleniumWRadioButtonSelectWebElement getReadOnlyExampleNoSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWRadioButtonSelect(new ByLabel("Read only with no selection", false));
	}

	/**
	 * @return an example which is interactive and has no default selection.
	 */
	private SeleniumWRadioButtonSelectWebElement getReadOnlyExampleWithSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWRadioButtonSelect(new ByLabel("Read only with selection", false));
	}

	/**
	 * @return an example which is interactive and has no default selection.
	 */
	private SeleniumWRadioButtonSelectWebElement getDisabledExample() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWRadioButtonSelect(new ByLabel("Disabled with selection", false));
	}

	/**
	 * Create a test instance.
	 */
	public WRadioButtonSelectExample_Test() {
		super(new WRadioButtonSelectExample());
	}

	@Test
	public void testFindWRadioButtonSelect() {
		Assert.assertNotNull(getExampleNoSelection());
		Assert.assertNotNull(getReadOnlyExampleNoSelection());
		Assert.assertNotNull(getDisabledExample());
	}

	@Test
	public void testComponentLevelProperties() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWRadioButtonSelectWebElement interactive = getExampleNoSelection();
		SeleniumWRadioButtonSelectWebElement disabled = getDisabledExample();
		SeleniumWRadioButtonSelectWebElement readOnly = getReadOnlyExampleWithSelection();

		// isReadOnly
		Assert.assertFalse(interactive.isReadOnly());
		Assert.assertFalse(disabled.isReadOnly());
		Assert.assertTrue(readOnly.isReadOnly());

		// isDisabled
		Assert.assertTrue(interactive.isEnabled());
		Assert.assertFalse(disabled.isEnabled());
		Assert.assertFalse(readOnly.isEnabled());

		// isMandatory
		Assert.assertFalse(interactive.isMandatory());
		Assert.assertFalse(readOnly.isMandatory());
		Assert.assertFalse(disabled.isMandatory());
		SeleniumWRadioButtonSelectWebElement cbs = driver.findWRadioButtonSelect(new ByLabel("Mandatory selection", false));
		Assert.assertTrue(cbs.isMandatory());
	}

	@Test
	public void testGetOptions() {
		SeleniumWComponentsWebDriver driver = getDriver();
		Assert.assertEquals(9, getExampleNoSelection().getOptions().size());
		Assert.assertEquals(9, getDisabledExample().getOptions().size());
		Assert.assertTrue(CollectionUtils.isEmpty(getReadOnlyExampleNoSelection().getOptions()));
		// interactive but no options
		Assert.assertTrue(CollectionUtils.isEmpty(driver.findWRadioButtonSelect(new ByLabel("Select from no options", false)).getOptions()));
	}

	@Test
	public void testGetOption() {
		SeleniumWRadioButtonSelectWebElement interactive = getExampleNoSelection();
		SeleniumWRadioButtonSelectWebElement disabled = getDisabledExample();
		SeleniumWRadioButtonSelectWebElement readOnly = getReadOnlyExampleWithSelection();
		// by index
		Assert.assertNotNull(interactive.getOption(0));
		Assert.assertNotNull(readOnly.getOption(0));
		Assert.assertNotNull(disabled.getOption(0));
		// by label
		Assert.assertNotNull(interactive.getOption("Outside Australia"));
		Assert.assertNotNull(readOnly.getOption("Outside Australia"));
		Assert.assertNotNull(disabled.getOption("Outside Australia"));
	}

	@Test
	public void testGetInput() {
		SeleniumWRadioButtonSelectWebElement rbs = getExampleNoSelection();
		// by index
		Assert.assertNotNull(rbs.getInput(0));
		// by label
		Assert.assertNotNull(rbs.getInput("Tasmania"));
		// by option
		WebElement option = rbs.getOption(0);
		Assert.assertNotNull(rbs.getInput(option));
	}

	@Test
	public void testGetSelected() {
		// examples with default selection
		Assert.assertEquals(1, getExampleWithSelection().getSelected().size());
		// disabled is always unselected
		Assert.assertTrue(CollectionUtils.isEmpty(getDisabledExample().getSelected()));
		Assert.assertEquals(1, getReadOnlyExampleWithSelection().getSelected().size());

		// no selection
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleNoSelection().getSelected()));
		Assert.assertTrue(CollectionUtils.isEmpty(getReadOnlyExampleNoSelection().getSelected()));
	}

	@Test
	public void testIsSelected() {
		SeleniumWRadioButtonSelectWebElement rbs = getExampleWithSelection();
		// by index
		Assert.assertTrue(rbs.isSelected(0));
		Assert.assertFalse(rbs.isSelected(1));
		// by labelText
		Assert.assertTrue(rbs.isSelected("Outside Australia"));
		Assert.assertFalse(rbs.isSelected("Victoria"));
		// by option
		WebElement option = rbs.getOption(0);
		Assert.assertTrue(rbs.isSelected(option));
		option = rbs.getOption(3);
		Assert.assertFalse(rbs.isSelected(option));

		// read only
		rbs = getReadOnlyExampleWithSelection();
		Assert.assertTrue(rbs.isSelected(0));
		Assert.assertFalse(rbs.isSelected(1));
		Assert.assertTrue(rbs.isSelected("Outside Australia"));
		Assert.assertFalse(rbs.isSelected("Victoria"));
		option = rbs.getOption(0);
		Assert.assertTrue(rbs.isSelected(option));

		// disabled - never selected
		rbs = getDisabledExample();
		Assert.assertFalse(rbs.isSelected(0)); // looks selected - this is the default selection but disabled is always false
		Assert.assertFalse(rbs.isSelected(1));
		// by labelText
		Assert.assertFalse(rbs.isSelected("Outside Australia"));
		Assert.assertFalse(rbs.isSelected("Victoria"));
		// by option
		option = rbs.getOption(0);
		Assert.assertFalse(rbs.isSelected(option));
		option = rbs.getOption(3);
		Assert.assertFalse(rbs.isSelected(option));
	}

	@Test
	public void testSelect() {
		SeleniumWRadioButtonSelectWebElement rbs = getExampleNoSelection();
		// idx
		Assert.assertFalse(rbs.isSelected(0));
		rbs.select(0);
		Assert.assertTrue(rbs.isSelected(0));
		rbs.select(1);
		Assert.assertFalse(rbs.isSelected(0));
		Assert.assertTrue(rbs.isSelected(1));
		// labelText
		Assert.assertFalse(rbs.isSelected("Outside Australia"));
		rbs.select("Outside Australia");
		Assert.assertTrue(rbs.isSelected("Outside Australia"));
		rbs.select("Victoria");
		Assert.assertFalse(rbs.isSelected("Outside Australia"));
		Assert.assertTrue(rbs.isSelected("Victoria"));
		// option
		WebElement option = rbs.getOption(0);
		Assert.assertFalse(rbs.isSelected(option));
		rbs.select(option);
		Assert.assertTrue(rbs.isSelected(option));
		WebElement option2 = rbs.getOption(1);
		Assert.assertFalse(rbs.isSelected(option2));
		rbs.select(option2);
		Assert.assertTrue(rbs.isSelected(option2));
		Assert.assertFalse(rbs.isSelected(option));

		// selecting a selected option does nothing
		rbs = getExampleWithSelection();
		Assert.assertTrue(rbs.isSelected(0));
		rbs.select(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(rbs.isSelected(0));

		// disabled does nothing
		rbs = getDisabledExample();
		Assert.assertFalse(rbs.isSelected(0));
		rbs.select(0);
		Assert.assertFalse(rbs.isSelected(0));
		rbs.select(1);
		Assert.assertFalse(rbs.isSelected(0));
		Assert.assertFalse(rbs.isSelected(1));
		// read-only does nothing
		rbs = getReadOnlyExampleWithSelection();
		Assert.assertTrue(rbs.isSelected(0));
		rbs.select(0); // cannot select outside of selection.
		Assert.assertTrue(rbs.isSelected(0));
	}

	@Test
	public void testClick() {
		SeleniumWRadioButtonSelectWebElement rbs = getExampleNoSelection();
		// idx
		Assert.assertFalse(rbs.isSelected(0));
		rbs.clickNoWait(0);
		Assert.assertTrue(rbs.isSelected(0));
		rbs.clickNoWait(1);
		Assert.assertFalse(rbs.isSelected(0));
		Assert.assertTrue(rbs.isSelected(1));
		// labelText
		Assert.assertFalse(rbs.isSelected("Outside Australia"));
		rbs.clickNoWait("Outside Australia");
		Assert.assertTrue(rbs.isSelected("Outside Australia"));
		rbs.clickNoWait("Victoria");
		Assert.assertFalse(rbs.isSelected("Outside Australia"));
		Assert.assertTrue(rbs.isSelected("Victoria"));
		// option
		WebElement option = rbs.getOption(0);
		Assert.assertFalse(rbs.isSelected(option));
		rbs.clickNoWait(option);
		Assert.assertTrue(rbs.isSelected(option));
		WebElement option2 = rbs.getOption(1);
		Assert.assertFalse(rbs.isSelected(option2));
		rbs.clickNoWait(option2);
		Assert.assertTrue(rbs.isSelected(option2));
		Assert.assertFalse(rbs.isSelected(option));

		// clicking a selected option does nothing
		rbs = getExampleWithSelection();
		Assert.assertTrue(rbs.isSelected(0));
		rbs.clickNoWait(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(rbs.isSelected(0));

		// disabled does nothing
		rbs = getDisabledExample();
		Assert.assertFalse(rbs.isSelected(0));
		rbs.clickNoWait(0);
		Assert.assertFalse(rbs.isSelected(0));
		rbs.clickNoWait(1);
		Assert.assertFalse(rbs.isSelected(0));
		Assert.assertFalse(rbs.isSelected(1));
		// read-only does nothing
		rbs = getReadOnlyExampleWithSelection();
		Assert.assertTrue(rbs.isSelected(0));
		rbs.clickNoWait(0); // cannot select outside of selection.
		Assert.assertTrue(rbs.isSelected(0));
	}
}
