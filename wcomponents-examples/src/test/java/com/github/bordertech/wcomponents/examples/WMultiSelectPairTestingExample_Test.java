package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMultiSelectPairWebElement;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

/**
 * Tests for {@link WMultiSelectPairTestingExample}. Really these are tests of the Selenium WebElement extension
 * SeleniumWMultiSelectPairWebElement.
 *
 * @author Mark Reeves
 * @since 1.4.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WMultiSelectPairTestingExample_Test extends WComponentExamplesTestCase {

	/**
	 * The set of shape options used in the test example.
	 */
	private static final String[] SHAPES = new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"};

	/**
	 * @return a known good interactive example with no selections
	 */
	private SeleniumWMultiSelectPairWebElement getExampleNoSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("Simple", false));
	}

	/**
	 * @return a known good interactive example with selections
	 */
	private SeleniumWMultiSelectPairWebElement getExampleWithSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("All selected", false));
	}

	/**
	 * @return a known good disabled example with default selections
	 */
	private SeleniumWMultiSelectPairWebElement getExampleDisabledWithSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("Disabled with apparent selection", false));
	}

	/**
	 * @return a known good read-only example with no selections
	 */
	private SeleniumWMultiSelectPairWebElement getExampleRadOnlyNoSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("Read-only no selection", false));
	}

	/**
	 * @return a known good read-only example with selections
	 */
	private SeleniumWMultiSelectPairWebElement getExampleReadOnlyWithSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("Read-only with selection", false));
	}

	/**
	 * @return a known good example with some selected options
	 */
	private SeleniumWMultiSelectPairWebElement getExampleWithSomeSelection() {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiSelectPair(new ByLabel("Some selected", false));
	}

	/**
	 * Create a test instance.
	 */
	public WMultiSelectPairTestingExample_Test() {
		super(new WMultiSelectPairTestingExample());
	}

	@Test
	public void testFind() {
		Assert.assertNotNull(getExampleNoSelection());
		Assert.assertNotNull(getExampleDisabledWithSelection());
		Assert.assertNotNull(getExampleReadOnlyWithSelection());
	}

	@Test
	public void testComponentProperties() {
		SeleniumWComponentsWebDriver driver = getDriver();
		SeleniumWMultiSelectPairWebElement interactive = getExampleNoSelection();
		SeleniumWMultiSelectPairWebElement disabled = getExampleDisabledWithSelection();
		SeleniumWMultiSelectPairWebElement readOnly = getExampleReadOnlyWithSelection();

		// isEnabled
		Assert.assertTrue(interactive.isEnabled());
		Assert.assertFalse(disabled.isEnabled());
		Assert.assertFalse(readOnly.isEnabled());

		// read-only
		Assert.assertFalse(interactive.isReadOnly());
		Assert.assertFalse(disabled.isReadOnly());
		Assert.assertTrue(readOnly.isReadOnly());

		// mandatory
		Assert.assertFalse(interactive.isMandatory());
		Assert.assertFalse(disabled.isMandatory());
		Assert.assertFalse(readOnly.isMandatory());
		SeleniumWMultiSelectPairWebElement msp = driver.findWMultiSelectPair(new ByLabel("Mandatory", false));
		Assert.assertTrue(msp.isMandatory());

		// hidden
		Assert.assertTrue(interactive.isDisplayed());
		Assert.assertTrue(disabled.isDisplayed());
		Assert.assertTrue(readOnly.isDisplayed());
		msp = driver.findWMultiSelectPair(new ByLabel("Hidden", false));
		Assert.assertFalse(msp.isDisplayed());
	}

	@Test
	public void testGetOptions() {
		SeleniumWComponentsWebDriver driver = getDriver();
		int expected = SHAPES.length;
		// get options should get a single ArrayList containing all of the same options as in SHAPES
		Assert.assertEquals(expected, getExampleNoSelection().getOptions().size());
		Assert.assertEquals(expected, getExampleWithSelection().getOptions().size());
		Assert.assertEquals(expected, getExampleReadOnlyWithSelection().getOptions().size());
		Assert.assertEquals(expected, getExampleDisabledWithSelection().getOptions().size());
		Assert.assertEquals(expected, getExampleWithSomeSelection().getOptions().size());
		// read-only with no selection has no options
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleRadOnlyNoSelection().getOptions()));
		// make sure we are not erroring when we have no options
		SeleniumWMultiSelectPairWebElement msp = driver.findWMultiSelectPair(new ByLabel("No options", false));
		Assert.assertTrue(CollectionUtils.isEmpty(msp.getOptions()));
	}

	@Test
	public void testGetOption() {
		SeleniumWMultiSelectPairWebElement noSelection = getExampleNoSelection();
		SeleniumWMultiSelectPairWebElement withSelection = getExampleWithSelection();
		SeleniumWMultiSelectPairWebElement disabled = getExampleDisabledWithSelection();
		SeleniumWMultiSelectPairWebElement readOnly = getExampleReadOnlyWithSelection();
		SeleniumWMultiSelectPairWebElement msp = getExampleWithSomeSelection();
		for (String text : SHAPES) {
			Assert.assertEquals(text, noSelection.getOption(text).getText());
			Assert.assertEquals(text, withSelection.getOption(text).getText());
			Assert.assertEquals(text, readOnly.getOption(text).getText());
			Assert.assertEquals(text, disabled.getOption(text).getText());
			Assert.assertEquals(text, msp.getOption(text).getText());
		}
	}

	@Test
	public void testGetSelected() {
		int expected = SHAPES.length;
		Assert.assertEquals(expected, getExampleWithSelection().getSelected().size());
		Assert.assertEquals(expected, getExampleReadOnlyWithSelection().getSelected().size());
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleDisabledWithSelection().getSelected()));
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleNoSelection().getSelected()));
		Assert.assertTrue(CollectionUtils.isEmpty(getExampleRadOnlyNoSelection().getSelected()));

		SeleniumWMultiSelectPairWebElement msp = getExampleWithSomeSelection();
		List<WebElement> options = msp.getSelected();
		Assert.assertEquals(3, options.size());
		String text;
		for (WebElement option : options) {
			boolean found = false;
			text = option.getText();
			for (String shape : SHAPES) {
				if (shape.equals(text)) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("Expected to find a match", found);
		}
	}

	@Test
	public void testIsSelected() {
		SeleniumWMultiSelectPairWebElement noSelection = getExampleNoSelection();
		SeleniumWMultiSelectPairWebElement withSelection = getExampleWithSelection();
		SeleniumWMultiSelectPairWebElement disabled = getExampleDisabledWithSelection();
		SeleniumWMultiSelectPairWebElement readOnly = getExampleReadOnlyWithSelection();
		SeleniumWMultiSelectPairWebElement readOnlyNoSelection = getExampleRadOnlyNoSelection();
		SeleniumWMultiSelectPairWebElement someSelected = getExampleWithSomeSelection();
		int i = 0;
		for (String shape : SHAPES) {
			Assert.assertFalse(noSelection.isSelected(shape));
			Assert.assertTrue(withSelection.isSelected(shape));
			Assert.assertFalse(disabled.isSelected(shape));
			Assert.assertTrue(readOnly.isSelected(shape));
			Assert.assertFalse(readOnlyNoSelection.isSelected(shape));
			if (i++ % 2 == 0) {
				Assert.assertTrue(someSelected.isSelected(shape));
			} else {
				Assert.assertFalse(someSelected.isSelected(shape));
			}
		}
	}

	@Test
	public void testSelect() {
		SeleniumWMultiSelectPairWebElement msp = getExampleNoSelection();
		for (String shape : SHAPES) {
			Assert.assertFalse(msp.isSelected(shape));
			msp.select(shape);
			Assert.assertTrue(msp.isSelected(shape));
		}
	}

	@Test
	public void testDeselect() {
		SeleniumWMultiSelectPairWebElement msp = getExampleWithSelection();
		for (String shape : SHAPES) {
			Assert.assertTrue(msp.isSelected(shape));
			msp.deselect(shape);
			Assert.assertFalse(msp.isSelected(shape));
		}
	}

	@Test
	public void testSelectAll() {
		SeleniumWMultiSelectPairWebElement msp = getExampleNoSelection();
		for (String shape : SHAPES) {
			Assert.assertFalse(msp.isSelected(shape));
		}
		msp.selectAll();
		for (String shape : SHAPES) {
			Assert.assertTrue(msp.isSelected(shape));
		}
	}

	@Test
	public void testDeselectAll() {
		SeleniumWMultiSelectPairWebElement msp = getExampleWithSelection();
		for (String shape : SHAPES) {
			Assert.assertTrue(msp.isSelected(shape));
		}
		msp.deselectAll();
		for (String shape : SHAPES) {
			Assert.assertFalse(msp.isSelected(shape));
		}
	}
}
