package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.ByLabel;
import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.element.SeleniumWMultiDropdownWebElement;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Mark Reeves
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WMultiDropdownTestingExample_Test extends WComponentExamplesTestCase {

	/**
	 * Helper to get a handle to a given WMultiDropdown.
	 * @param text the label for the WMultiDropdown
	 * @return a reference to the SeleniumWMultiDropdownWebElement representing the labelled option.
	 */
	private SeleniumWMultiDropdownWebElement getByLabel(final String text) {
		SeleniumWComponentsWebDriver driver = getDriver();
		return driver.findWMultiDropdown(new ByLabel(text, false));
	}

	/**
	 * Create a test instance.
	 */
	public WMultiDropdownTestingExample_Test() {
		super(new WMultiDropdownTestingExample());
	}

	// test find to ensure we can get editable and read-only components
	@Test
	public void testFind() {
		Assert.assertNotNull(getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION));
		Assert.assertNotNull(getByLabel(WMultiDropdownTestingExample.LABEL_RO_NO_SELECTION));
		Assert.assertNotNull(getByLabel(WMultiDropdownTestingExample.LABEL_DISABLED));
	}

	/**
	 * Helper for getOptions tests.
	 * @param component the SeleniumWMultiDropdownWebElement to test
	 */
	private void getOptionsHelper(final SeleniumWMultiDropdownWebElement component) {
		List<WebElement> options = component.getOptions();
		Assert.assertEquals(WMultiDropdownTestingExample.DATA_LIST.size(), options.size());
		for (WebElement option : options) {
			Assert.assertTrue(WMultiDropdownTestingExample.DATA_LIST.contains(option.getText()));
		}
	}

	/**
	 * Helper for getOptions tests for read-only components.
	 * @param component the component to test
	 * @param expected the list of strings we expect to be selected
	 */
	private void getOptionsHelper(final SeleniumWMultiDropdownWebElement component, final List expected) {
		List<WebElement> options = component.getOptions();
		Assert.assertEquals(expected.size(), options.size());
		for (WebElement option : options) {
			Assert.assertTrue(expected.contains(option.getText()));
		}
	}

	@Test
	public void testGetOptions() {
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED));
		// makes no difference if there is no selection or if all are selected
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION));
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED));

		// read-only is different as only selected options are output
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED), WMultiDropdownTestingExample.DATA_SOME_SELECTED);
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_RO_ONE_SELECTED), WMultiDropdownTestingExample.DATA_ONE_SELECTED);
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_RO_ALL_SELECTED), WMultiDropdownTestingExample.DATA_LIST);
		getOptionsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_RO_NO_SELECTION),
				Arrays.asList(new String[]{WMultiDropdownTestingExample.DATA_LIST.get(0)}));
	}

	@Test
	public void testEnabled() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		Assert.assertTrue(component.isEnabled());
		component = getByLabel(WMultiDropdownTestingExample.LABEL_DISABLED);
		Assert.assertFalse(component.isEnabled());
		component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED);
		Assert.assertFalse(component.isEnabled());
	}

	@Test
	public void testMandatory() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_MANDATORY);
		Assert.assertTrue(component.isMandatory());
		component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		Assert.assertFalse(component.isMandatory());
	}

	@Test
	public void testDefaultSelection() {
		List<WebElement> actual;
		SeleniumWMultiDropdownWebElement component;
		WebElement selected;

		List<String> expectedData;
		String selectedValue;
		String expectedValue;

		// no selection will _always_ have one selected item - the first one
		component = getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION);
		actual = component.getSelected();
		Assert.assertEquals(1, actual.size());
		expectedValue = WMultiDropdownTestingExample.DATA_LIST.get(0);
		selectedValue = actual.get(0).getText();
		Assert.assertTrue(expectedValue.equalsIgnoreCase(selectedValue));

		// no selection read only has the default option (0)
		component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_NO_SELECTION);
		actual = component.getSelected();
		Assert.assertEquals(1, actual.size());
		expectedValue = WMultiDropdownTestingExample.DATA_LIST.get(0);
		selectedValue = actual.get(0).getText();
		Assert.assertTrue(expectedValue.equalsIgnoreCase(selectedValue));

		// one selection
		component = getByLabel(WMultiDropdownTestingExample.LABEL_ONE_SELECTED);
		actual = component.getSelected();
		Assert.assertEquals(1, actual.size());
		expectedValue = WMultiDropdownTestingExample.DATA_ONE_SELECTED.get(0);
		selectedValue = actual.get(0).getText();
		Assert.assertTrue(expectedValue.equalsIgnoreCase(selectedValue));

		// one selection read-only
		component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_ONE_SELECTED);
		actual = component.getSelected();
		Assert.assertEquals(1, actual.size());
		expectedValue = WMultiDropdownTestingExample.DATA_ONE_SELECTED.get(0);
		selectedValue = actual.get(0).getText();
		Assert.assertTrue(expectedValue.equalsIgnoreCase(selectedValue));

		// many selections
		component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		actual = component.getSelected();
		expectedData = WMultiDropdownTestingExample.DATA_SOME_SELECTED;
		Assert.assertEquals(expectedData.size(), actual.size());
		for (WebElement option : actual) {
			selectedValue = option.getText();
			Assert.assertTrue(expectedData.contains(selectedValue));
		}

		// many selections readonly
		component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED);
		actual = component.getSelected();
		expectedData = WMultiDropdownTestingExample.DATA_SOME_SELECTED;
		Assert.assertEquals(expectedData.size(), actual.size());
		for (WebElement option : actual) {
			selectedValue = option.getText();
			Assert.assertTrue(expectedData.contains(selectedValue));
		}

		// many selections but disabled is a bit different
		component = getByLabel(WMultiDropdownTestingExample.LABEL_DISABLED);
		Assert.assertTrue(component.getSelected().isEmpty());
		// getSelected is empty but there are still dropdowns available
		Assert.assertEquals(WMultiDropdownTestingExample.DATA_SOME_SELECTED.size(), component.getDropdowns().size());
	}

	/**
	 * Helper for getDropdowns tests.
	 * @param component the conponent to test
	 * @param expected the List of expected selected options.
	 */
	private void getDropdownsHelper(final SeleniumWMultiDropdownWebElement component, final List expected) {
		List<WebElement> dropdowns = component.getDropdowns();
		Assert.assertEquals(expected.size(), dropdowns.size());
		Select se;
		for (WebElement dropdown : dropdowns) {
			se = new Select(dropdown);
			Assert.assertTrue(expected.contains(se.getFirstSelectedOption().getText()));
		}
	}

	@Test
	public void testGetDropdowns() {
		getDropdownsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED), WMultiDropdownTestingExample.DATA_SOME_SELECTED);
		getDropdownsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_ONE_SELECTED), WMultiDropdownTestingExample.DATA_ONE_SELECTED);
		getDropdownsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED), WMultiDropdownTestingExample.DATA_LIST);
		getDropdownsHelper(getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION),
				Arrays.asList(new String[]{WMultiDropdownTestingExample.DATA_LIST.get(0)}));

		// Disabled should not be able to rely on getFirstSelectedOption as it should not have one, so we are not going to rely on it
		// so the best we can do is check that the component contains the expected number of dropdowns
		Assert.assertEquals(WMultiDropdownTestingExample.DATA_SOME_SELECTED.size(),
				getByLabel(WMultiDropdownTestingExample.LABEL_DISABLED).getDropdowns().size());
	}

	@Test (expected = SystemException.class)
	public void testGetDropdownsReadOnly() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_ONE_SELECTED);
		component.getDropdowns();
	}

	@Test
	public void testGetAddButton() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION);
		WebElement button = component.getAddButton();
		Assert.assertNotNull(button);
		// what can we do with the add button?
		component = getByLabel(WMultiDropdownTestingExample.LABEL_DISABLED);
		button = component.getAddButton();
		Assert.assertNotNull(button);
		Assert.assertFalse(button.isEnabled());
	}

	@Test (expected = SystemException.class)
	public void testGetAddButtonReadOnly() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_ONE_SELECTED);
		component.getAddButton();
	}

	@Test
	public void testGetDropdownByIdx() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		for (int i = 0; i < WMultiDropdownTestingExample.DATA_SOME_SELECTED.size(); i++) {
			Assert.assertNotNull(component.getDropdown(i));
		}
	}

	@Test (expected = SystemException.class)
	public void testGetDropdownByIdxReadOnly() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED);
		component.getDropdown(0);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetDropdownByLowIdx() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		component.getDropdown(-1);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetDropdownByHighIdx() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		component.getDropdown(WMultiDropdownTestingExample.DATA_SOME_SELECTED.size());
	}

	@Test
	public void testGetDropdownByText() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		for (String text : WMultiDropdownTestingExample.DATA_SOME_SELECTED) {
			Assert.assertNotNull(component.getDropdown(text));
		}
	}

	@Test (expected = SystemException.class)
	public void testGetDropdownByTextRO() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED);
		component.getDropdown(WMultiDropdownTestingExample.DATA_SOME_SELECTED.get(0));
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetDropdownByTextNull() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		component.getDropdown(null);
	}

	@Test (expected = SystemException.class)
	public void testGetDropdownByTextNonsense() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		String notInThisWorld = StringUtils.join(WMultiDropdownTestingExample.DATA_SOME_SELECTED, " ");
		component.getDropdown(notInThisWorld);
	}

	@Test
	public void testGetFirstDropdown() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		Assert.assertNotNull(component.getFirstDropdown());
	}

	@Test (expected = SystemException.class)
	public void testGetFirstDropdownRO() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED);
		component.getFirstDropdown();
	}

	@Test
	public void testGetRemoveButton() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
		WebElement dropdown = component.getDropdown(1); // anything other than 0 for this test
		Assert.assertNotNull(component.getRemoveButton(dropdown));
	}

	@Test (expected = SystemException.class)
	public void testGetRemoveButtonRO() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_ALL_SELECTED);
		component.getRemoveButton(null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetRemoveButtonNull() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
		component.getRemoveButton(null);
	}

	@Test (expected = SystemException.class)
	public void testGetRemoveButtonFirstDropdown() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
		component.getRemoveButton(component.getFirstDropdown());
	}

	/**
	 * Helper for the isSelected tests.
	 * @param component the component to test
	 * @param expected the list of expected values
	 */
	private void isSelectedHelper(final SeleniumWMultiDropdownWebElement component, final List expected) {
		boolean actual;
		for (String value : WMultiDropdownTestingExample.DATA_LIST) {
			actual = component.isSelected(value);
			if (expected.contains(value)) {
				Assert.assertTrue(actual);
			} else {
				Assert.assertFalse(actual);
			}
		}
	}

	@Test
	public void testIsSelected() {
		isSelectedHelper(getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED), WMultiDropdownTestingExample.DATA_SOME_SELECTED);
		isSelectedHelper(getByLabel(WMultiDropdownTestingExample.LABEL_RO_SOME_SELECTED), WMultiDropdownTestingExample.DATA_SOME_SELECTED);
	}

	@Test
	public void testIsSelectedByValue() {

	}

	/**
	 * Helper to reset after changing selections.
	 */
	private void reset() {
		WebElement resetButton = getDriver().findElement(byWComponentPath("WButton[0]"), false);
		resetButton.click();
	}

	@Test
	public void testSelect() {
		try {
			// we know from the default selection tests that the component with no selection has a defalt seletion of the 0th element in the data list
			SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_NO_SELECTION);
			List<WebElement> startSelection = component.getSelected();
			String text = WMultiDropdownTestingExample.DATA_LIST.get(1);
			component.select(text);
			List<WebElement> endSelection = component.getSelected();
			Assert.assertEquals(startSelection.size() + 1, endSelection.size());
			// the new selection is placed in the zeroth slot
			Assert.assertTrue(text.equalsIgnoreCase(endSelection.get(0).getText()));
		} finally {
			reset();
		}
	}
	// TODO: need exception tests for select

	@Test
	public void testDeselect() {
		// to do: this needs to be beefed up a bit.
		try {
			SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
			List<WebElement> startSelection = component.getSelected();
			// cannot deselect the oth item: have to switch it.
			String text = WMultiDropdownTestingExample.DATA_LIST.get(1);
			component.deselect(text);
			List<WebElement> endSelection = component.getSelected();
			Assert.assertEquals(startSelection.size() - 1, endSelection.size());
			for (WebElement option : endSelection) {
				if (text.equalsIgnoreCase(option.getText())) {
					Assert.assertFalse("Should not have this in the selection", true);
				}
			}
		} finally {
			reset();
		}
	}

	@Test (expected = SystemException.class)
	public void testDeselectReadOnly() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_RO_ALL_SELECTED);
		String text = WMultiDropdownTestingExample.DATA_LIST.get(1);
		component.deselect(text);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testDeselectNull() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
		component.deselect(null);
	}

	@Test (expected = SystemException.class)
	public void testDeselectNotSelected() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		component.deselect(StringUtils.join(WMultiDropdownTestingExample.DATA_SOME_SELECTED, " "));
	}

	@Test (expected = SystemException.class)
	public void testDeselectFirstOption() {
		SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_SOME_SELECTED);
		component.deselect(WMultiDropdownTestingExample.DATA_SOME_SELECTED.get(0));
	}

	@Test
	public void testDeselectAll() {
		try {
			SeleniumWMultiDropdownWebElement component = getByLabel(WMultiDropdownTestingExample.LABEL_ALL_SELECTED);
			component.deselectAll();
			List<WebElement> endSelection = component.getSelected();
			Assert.assertEquals(1, endSelection.size());
			Assert.assertTrue(WMultiDropdownTestingExample.DATA_LIST.get(0).equalsIgnoreCase(endSelection.get(0).getText()));
		} finally {
			reset();
		}
	}

	// TODO switch option tests
}
