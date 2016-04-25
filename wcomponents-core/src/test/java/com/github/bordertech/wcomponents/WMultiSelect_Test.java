package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
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
}
