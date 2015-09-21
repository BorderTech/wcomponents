package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.util.I18nUtilities;
import com.github.bordertech.wcomponents.util.InternalMessages;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMultiSelectPair_Test - unit tests for {@link WMultiSelectPair}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiSelectPair_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WMultiSelectPair multi = new WMultiSelectPair();
		Assert.assertNull("Incorrect options returned", multi.getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WMultiSelectPair multi = new WMultiSelectPair(options);
		Assert.
				assertEquals("Incorrect options returned", Arrays.asList(options), multi.
						getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WMultiSelectPair multi = new WMultiSelectPair(options);
		Assert.assertEquals("Incorrect options returned", options, multi.getOptions());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WMultiSelectPair multi = new WMultiSelectPair(DayOfWeekTable.class);
		Assert.
				assertEquals("Incorrect table returned", DayOfWeekTable.class, multi.
						getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", multi.isAllowNoSelection());
	}

	@Test
	public void testAvailableListNameAccessors() {
		assertAccessorsCorrect(new WMultiSelectPair(), "availableListName",
				I18nUtilities.format(null,
						InternalMessages.DEFAULT_MULTI_SELECT_PAIR_OPTIONS_LIST_HEADING),
				"A", "B");
	}

	@Test
	public void testSelectableListNameAccessors() {
		assertAccessorsCorrect(new WMultiSelectPair(),
				"selectedListName",
				I18nUtilities.format(null,
						InternalMessages.DEFAULT_MULTI_SELECT_PAIR_SELECTIONS_LIST_HEADING),
				"A", "B");
	}

	@Test
	public void testShuffleAccessors() {
		assertAccessorsCorrect(new WMultiSelectPair(), "shuffle", false, true, false);
	}

	@Test
	public void testSelectionOrderable() {
		WMultiSelectPair multi = new WMultiSelectPair();
		Assert.assertFalse("SelectionOrderable should default to false", multi.
				isSelectionOrderable());

		multi.setShuffle(true);
		Assert.assertTrue("SelectionOrderable should be true", multi.isSelectionOrderable());
	}
}
