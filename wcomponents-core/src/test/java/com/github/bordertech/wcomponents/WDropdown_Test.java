package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for WDropdown.
 *
 * @author Ming Gao
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDropdown_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WDropdown dropdown = new WDropdown();
		Assert.assertNull("Incorrect options returned", dropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WDropdown dropdown = new WDropdown(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), dropdown.
				getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WDropdown dropdown = new WDropdown(options);
		Assert.assertEquals("Incorrect options returned", options, dropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WDropdown dropdown = new WDropdown(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, dropdown.
				getLookupTable());
		Assert.assertFalse("allowNoSelection should be false", dropdown.isAllowNoSelection());
	}

	@Test
	public void testEditableAccessors() {
		assertAccessorsCorrect(new WDropdown(), "editable", false, true, false);
	}

	@Test
	public void testOptionWidthAccessors() {
		assertAccessorsCorrect(new WDropdown(), "optionWidth", 0, 1, 2);
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WDropdown(), "type", null, WDropdown.DropdownType.NATIVE,
				WDropdown.DropdownType.COMBO);
	}

}
