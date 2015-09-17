package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WSingleSelect_Test - unit tests for {@link WSingleSelect}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSingleSelect_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WSingleSelect single = new WSingleSelect();
		Assert.assertNull("Incorrect options returned", single.getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WSingleSelect single = new WSingleSelect(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), single.
				getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WSingleSelect single = new WSingleSelect(options);
		Assert.assertEquals("Incorrect options returned", options, single.getOptions());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WSingleSelect single = new WSingleSelect(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, single.
				getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", single.isAllowNoSelection());
	}

	@Test
	public void testRowAccessors() {
		assertAccessorsCorrect(new WSingleSelect(), "rows", 0, 1, 2);
	}

}
