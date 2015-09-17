package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WCheckBoxSelect}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WCheckBoxSelect_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect();
		Assert.assertNull("Incorrect options returned", checkBoxSelect.getOptions());
		Assert.assertTrue("allowNoSelection should be true", checkBoxSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), checkBoxSelect.
				getOptions());
		Assert.assertTrue("allowNoSelection should be true", checkBoxSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect(options);
		Assert.assertEquals("Incorrect options returned", options, checkBoxSelect.getOptions());
		Assert.assertTrue("allowNoSelection should be true", checkBoxSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, checkBoxSelect.
				getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", checkBoxSelect.isAllowNoSelection());
	}

	@Test
	public void testFramelessAccessors() {
		assertAccessorsCorrect(new WCheckBoxSelect(), "frameless", false, true, false);
	}

	@Test
	public void testButtonColumnsAccessors() {
		assertAccessorsCorrect(new WCheckBoxSelect(), "buttonColumns", 0, 1, 2);
		try {
			WCheckBoxSelect checkboxSelect = new WCheckBoxSelect();
			checkboxSelect.setButtonColumns(0);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
		}
	}

	@Test
	public void testButtonlayoutAccessors() {
		assertAccessorsCorrect(new WCheckBoxSelect(), "buttonLayout", WCheckBoxSelect.LAYOUT_STACKED,
				WCheckBoxSelect.LAYOUT_COLUMNS, WCheckBoxSelect.LAYOUT_FLAT);
	}
}
