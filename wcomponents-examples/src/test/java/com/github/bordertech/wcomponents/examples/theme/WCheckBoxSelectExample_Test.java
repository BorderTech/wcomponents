package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.examples.SeleniumTests;
import com.github.bordertech.wcomponents.examples.WComponentExamplesTestCase;
import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWCheckBoxSelectWebElement;
import com.github.bordertech.wcomponents.util.SystemException;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

/**
 * Unit tests for WCheckBoxSelectExample which are really used to test {@link SeleniumWCheckBoxSelectWebElement}. These
 * tests combine assertions to compensate for the slowness of reloading the example to be tested in Selenium.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WCheckBoxSelectExample_Test extends WComponentExamplesTestCase {

	/**
	 * Create a test instance.
	 */
	public WCheckBoxSelectExample_Test() {
		super(new WCheckBoxSelectExample());
	}

	/**
	 * @return The first WCheckBoxSelect in the example. This is an interactive component with no selections and no
	 * optional properties set.
	 */
	private SeleniumWCheckBoxSelectWebElement getExampleNoSelections() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(byWComponentPath("WCheckBoxSelect[0]"));
	}

	/**
	 * @return a known good example which is read-only and has selections.
	 */
	private SeleniumWCheckBoxSelectWebElement getExampleWithSelections() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(new ByLabel("Many selections with frame", false));
	}

	/**
	 * @return a known good example which is read-only and has selections.
	 */
	private SeleniumWCheckBoxSelectWebElement getExampleWithReadOnlySelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(new ByLabel("Many selections with frame (read only)", false));
	}

	/**
	 * @return a known good example which is disabled.
	 */
	private SeleniumWCheckBoxSelectWebElement getDisabledExample() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(new ByLabel("Disabled with no default selection", false));
	}

	/**
	 * @return a known anti-pattern example which is interactive but has no options.
	 */
	private SeleniumWCheckBoxSelectWebElement getExampleNoOptions() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(new ByLabel("WCheckBoxSelect with no options", false));
	}

	/**
	 * @return a known good example of read-only with no selections.
	 */
	private SeleniumWCheckBoxSelectWebElement getReadOnlyNoSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWCheckBoxSelect(new ByLabel("No selections were made (read only)", false));
	}

	@Test
	public void testWCheckBoxSelect() {
		Assert.assertNotNull(getExampleNoSelections());
		Assert.assertNotNull(getExampleWithReadOnlySelection());
		Assert.assertNotNull(getDisabledExample());
	}

	@Test
	public void testComponentLevelBooleanProperties() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWCheckBoxSelectWebElement interactive = getExampleNoSelections();
		SeleniumWCheckBoxSelectWebElement readOnly = getExampleWithReadOnlySelection();
		SeleniumWCheckBoxSelectWebElement disabled = getDisabledExample();
		// read-only
		Assert.assertFalse(interactive.isReadOnly());
		Assert.assertTrue(readOnly.isReadOnly());
		Assert.assertFalse(disabled.isReadOnly());
		// enabled
		Assert.assertTrue(interactive.isEnabled());
		Assert.assertFalse(readOnly.isEnabled());
		Assert.assertFalse(disabled.isEnabled());
		// mandatory
		Assert.assertFalse(interactive.isMandatory());
		Assert.assertFalse(readOnly.isMandatory());
		Assert.assertFalse(disabled.isMandatory());
		SeleniumWCheckBoxSelectWebElement cbs = driver.findWCheckBoxSelect(new ByLabel("A selection is required", true));
		Assert.assertTrue(cbs.isMandatory());
	}

	@Test
	public void testGetOptions() {
		Assert.assertEquals(9, getExampleNoSelections().getOptions().size());
		Assert.assertEquals(9, getExampleWithReadOnlySelection().getOptions().size());
		// when empty
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleNoOptions().getOptions()));
		// read-only and empty
		Assert.assertTrue(CollectionUtils.isEmpty(getReadOnlyNoSelection().getOptions()));
	}

	@Test
	public void testGetOption() {
		SeleniumWCheckBoxSelectWebElement interactive = getExampleNoSelections();
		SeleniumWCheckBoxSelectWebElement readOnly = getExampleWithReadOnlySelection();
		// by index
		Assert.assertNotNull(interactive.getOption(0));
		Assert.assertNotNull(readOnly.getOption(0));
		// by label
		Assert.assertNotNull(interactive.getOption("Outside Australia"));
		Assert.assertNotNull(readOnly.getOption("Outside Australia"));
	}

	@Test
	public void testGetInput() {
		SeleniumWCheckBoxSelectWebElement cbs = getExampleNoSelections();
		// by index
		Assert.assertNotNull(cbs.getInput(0));
		// by label
		Assert.assertNotNull(cbs.getInput("Tasmania"));
		// by option
		WebElement option = cbs.getOption(0);
		Assert.assertNotNull(cbs.getInput(option));
	}

	@Test
	public void testGetSelected() {
		SeleniumWComponentsWebDriver driver = getDriver();
		// use item with default selection
		Assert.assertEquals(9, getExampleWithSelections().getSelected().size());
		// read only with selection
		Assert.assertEquals(9, getExampleWithReadOnlySelection().getSelected().size());
		// no selection
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleNoSelections().getSelected()));
		// read-only, no selection
		Assert.assertTrue(CollectionUtils.isEmpty(getReadOnlyNoSelection().getSelected()));
		// single selection
		SeleniumWCheckBoxSelectWebElement cbs = driver.findWCheckBoxSelect(new ByLabel("One selection was made", false));
		Assert.assertEquals(1, cbs.getSelected().size());
		// read-only one selection
		cbs = driver.findWCheckBoxSelect(new ByLabel("One selection was made (read only)", false));
		Assert.assertEquals(1, cbs.getSelected().size());
		// disabled always empty
		cbs = driver.findWCheckBoxSelect(new ByLabel("Disabled with many selections and COLUMN layout", false));
		Assert.assertTrue(CollectionUtils.isEmpty(cbs.getSelected()));
	}

	@Test
	public void testIsSelected() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWCheckBoxSelectWebElement interactive = driver.findWCheckBoxSelect(new ByLabel("One selection was made", false));
		SeleniumWCheckBoxSelectWebElement readOnly = driver.findWCheckBoxSelect(new ByLabel("One selection was made (read only)", false));
		// by index
		Assert.assertTrue(interactive.isSelected(0));
		Assert.assertFalse(interactive.isSelected(1));
		Assert.assertTrue(readOnly.isSelected(0));
		Assert.assertFalse(readOnly.isSelected(1)); // only one option so anything else is no selected.
		// by text
		String labelText = "Outside Australia";
		Assert.assertTrue(interactive.isSelected(labelText));
		Assert.assertFalse(interactive.isSelected("Australian Capital Territory"));
		Assert.assertTrue(readOnly.isSelected(labelText));
		Assert.assertFalse(readOnly.isSelected("Australian Capital Territory"));
		// by option
		Assert.assertTrue(interactive.isSelected(interactive.getOption(0)));
		Assert.assertFalse(interactive.isSelected(interactive.getOption(1)));
		Assert.assertTrue(readOnly.isSelected(readOnly.getOption(0)));
		// cannot test for false in read only mode
		// disabled always false
		SeleniumWCheckBoxSelectWebElement disabled
				= driver.findWCheckBoxSelect(new ByLabel("Disabled with many selections and COLUMN layout", false));
		int i = 0;
		for (WebElement option : disabled.getOptions()) {
			Assert.assertFalse(disabled.isSelected(option));
			Assert.assertFalse(disabled.isSelected(i++));
			Assert.assertFalse(disabled.isSelected(option.getText()));
		}
	}

	@Test
	public void testSelect() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWCheckBoxSelectWebElement cbs = getExampleNoSelections();
		// by option
		WebElement option = cbs.getOption(0);
		Assert.assertFalse(cbs.isSelected(option));
		cbs.select(option);
		Assert.assertTrue("Option should be selected", cbs.isSelected(option));
		//by index
		int idx = 1; // not 0 as we used that already, not that it matters
		Assert.assertFalse(cbs.isSelected(idx));
		cbs.select(idx);
		Assert.assertTrue("Option at index 'idx' should be selected", cbs.isSelected(idx));
		// by label
		String labelText = "South Australia";
		Assert.assertFalse(cbs.isSelected(labelText));
		cbs.select(labelText);
		Assert.assertTrue("Option labelled '" + labelText + "' should be selected", cbs.isSelected(labelText));

		// Selecting a selected option should not change the selection
		cbs = driver.findWCheckBoxSelect(new ByLabel("One selection was made", false));
		idx = 0; // the selected option in this example
		Assert.assertTrue(cbs.isSelected(idx));
		cbs.select(idx);
		Assert.assertTrue("Option should still be selected", cbs.isSelected(idx));
		// select disabled does nothing
		cbs = getDisabledExample();
		Assert.assertFalse(cbs.isSelected(0));
		cbs.select(0);
		getDriver().waitForPageReady();
		Assert.assertFalse(cbs.isSelected(0));
		// readOnly does nothing
		cbs = getExampleWithReadOnlySelection();
		Assert.assertTrue(cbs.isSelected(0));
		cbs.select(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(cbs.isSelected(0));
	}

	@Test
	public void testDeselect() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWCheckBoxSelectWebElement cbs = getExampleWithSelections();
		// by option
		WebElement option = cbs.getOption(0);
		Assert.assertTrue(cbs.isSelected(option));
		cbs.deselect(option);
		Assert.assertFalse("Option should not selected", cbs.isSelected(option));
		//by index
		int idx = 1; // not 0 as we used that already, not that it matters
		Assert.assertTrue(cbs.isSelected(idx));
		cbs.deselect(idx);
		Assert.assertFalse("Option at index 'idx' should not be selected", cbs.isSelected(idx));
		// by label
		String labelText = "South Australia";
		Assert.assertTrue(cbs.isSelected(labelText));
		cbs.deselect(labelText);
		Assert.assertFalse("Option labelled '" + labelText + "' should not be selected", cbs.isSelected(labelText));
		// Deselecting an unselected option should not change the selection
		cbs = driver.findWCheckBoxSelect(new ByLabel("One selection was made", false));
		idx = 1; // an unselected option in this example
		Assert.assertFalse(cbs.isSelected(idx));
		cbs.deselect(idx);
		Assert.assertFalse("Option should still not be selected", cbs.isSelected(idx));
		// readOnly does nothing
		cbs = getExampleWithReadOnlySelection();
		Assert.assertTrue(cbs.isSelected(0));
		cbs.deselect(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(cbs.isSelected(0));
	}

	@Test
	public void testClick() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWCheckBoxSelectWebElement cbs = driver.findWCheckBoxSelect(new ByLabel("One selection was made", false));
		// by option
		WebElement option = cbs.getOption(0); // the selected option
		boolean selected = cbs.isSelected(option);
		cbs.clickNoWait(option);
		Assert.assertEquals("Option selection should have changed.", !selected, cbs.isSelected(option));
		cbs.clickNoWait(option);
		Assert.assertEquals("Option selection should have changed back.", selected, cbs.isSelected(option));
		// by label
		String labelText = "New South Wales";
		selected = cbs.isSelected(labelText);
		cbs.clickNoWait(labelText);
		Assert.assertEquals("Option with label '" + labelText + "' selection should have changed.", !selected, cbs.isSelected(labelText));
		cbs.clickNoWait(labelText);
		Assert.assertEquals("Option with label '" + labelText + "' selection should have changed back.", selected, cbs.isSelected(labelText));
		// by index
		int idx = 3;
		selected = cbs.isSelected(idx);
		cbs.clickNoWait(idx);
		Assert.assertEquals("Option with index '" + String.valueOf(idx) + "' selection should have changed.", !selected, cbs.isSelected(idx));
		cbs.clickNoWait(idx);
		Assert.assertEquals("Option with index '" + String.valueOf(idx) + "' selection should have changed back.", selected, cbs.isSelected(idx));
		// disabled does nothing
		cbs = getDisabledExample();
		Assert.assertFalse(cbs.isSelected(0));
		cbs.clickNoWait(0);
		getDriver().waitForPageReady();
		Assert.assertFalse(cbs.isSelected(0));
		// readOnly does nothing
		cbs = getExampleWithReadOnlySelection();
		Assert.assertTrue(cbs.isSelected(0));
		cbs.clickNoWait(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(cbs.isSelected(0));
	}

	@Test
	public void testToggle() {
		SeleniumWCheckBoxSelectWebElement cbs = getExampleNoSelections();
		// by option
		WebElement option = cbs.getOption(0);
		boolean selected = cbs.isSelected(option);
		cbs.toggle(option);
		Assert.assertEquals("Option should not be in the same selected state", !selected, cbs.isSelected(option));
		cbs.toggle(option);
		Assert.assertEquals("Option should be back in the previous selected state", selected, cbs.isSelected(option));
		// by text
		String labelText = "New South Wales";
		selected = cbs.isSelected(labelText);
		cbs.toggle(labelText);
		Assert.assertEquals("Option with label should not be in the same selected state", !selected, cbs.isSelected(labelText));
		cbs.toggle(labelText);
		Assert.assertEquals("Option with label should be back in the previous selected state", selected, cbs.isSelected(labelText));
		// by index
		int idx = 3;
		selected = cbs.isSelected(idx);
		cbs.toggle(idx);
		Assert.assertEquals("Option `idx` should not be in the same selected state", !selected, cbs.isSelected(idx));
		cbs.toggle(idx);
		Assert.assertEquals("Option `idx` should be back in the previous selected state", selected, cbs.isSelected(idx));
		// disabled does nothing
		cbs = getDisabledExample();
		Assert.assertFalse(cbs.isSelected(0));
		cbs.toggle(0);
		getDriver().waitForPageReady();
		Assert.assertFalse(cbs.isSelected(0));
		// readOnly does nothing
		cbs = getExampleWithReadOnlySelection();
		Assert.assertTrue(cbs.isSelected(0));
		cbs.toggle(0);
		getDriver().waitForPageReady();
		Assert.assertTrue(cbs.isSelected(0));
	}

	// Exception testing - explicit exceptions thrown in SeleniumWCheckBoxSelectWebElement
	@Test(expected = SystemException.class)
	public void testGetOptionByIdxWhenEmptyNotReadOnly() {
		getExampleNoOptions().getOption(0);
	}

	@Test(expected = SystemException.class)
	public void testGetOptionByTextWhenEmptyNotReadOnly() {
		getExampleNoOptions().getOption("");
	}

	@Test(expected = SystemException.class)
	public void testGetOptionReadOnlyNoOptions() {
		getReadOnlyNoSelection().getOption(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetOptionWithTextNoOptionReadOnly() {
		getExampleWithReadOnlySelection().getOption("USA");
	}

	// getInput Exception tests
	@Test(expected = SystemException.class)
	public void testGetInputByIdxReadOnly() {
		getExampleWithReadOnlySelection().getInput(0);
	}

	@Test(expected = SystemException.class)
	public void testGetInputByTextReadOnly() {
		getExampleWithReadOnlySelection().getInput("Tasmania");
	}
}
