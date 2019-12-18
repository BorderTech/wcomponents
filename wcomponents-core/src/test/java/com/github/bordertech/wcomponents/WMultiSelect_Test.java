package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * WMultiSelect_Test - unit tests for {@link WMultiSelect}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiSelect_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WMultiSelect multi = new WMultiSelect();
		Assert.assertNull("Incorrect options returned", multi.getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WMultiSelect multi = new WMultiSelect(options);
		Assert.
				assertEquals("Incorrect options returned", Arrays.asList(options), multi.
						getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WMultiSelect multi = new WMultiSelect(options);
		Assert.assertEquals("Incorrect options returned", options, multi.getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WMultiSelect multi = new WMultiSelect(DayOfWeekTable.class);
		Assert.
				assertEquals("Incorrect table returned", DayOfWeekTable.class, multi.
						getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testRowAccessors() {
		assertAccessorsCorrect(new WMultiSelect(), "rows", 0, 1, 2);
	}
	
	@Test
	public void testToggleSelection() {
		List<String> options = Arrays.asList("A", "B", "C");
		WMultiSelect multi = new WMultiSelect(options);
		
		Assert.assertTrue("Initial selection should be empty", multi.getSelected().isEmpty());
		
		multi.toggleSelection(true);
		Assert.assertEquals("Selection after toggleSelection(true) should include all options", 3, multi.getSelected().size());
		
		multi.toggleSelection(false);
		Assert.assertTrue("Selection after toggleSelection(false) should be empty", multi.getSelected().isEmpty());
	}
}
