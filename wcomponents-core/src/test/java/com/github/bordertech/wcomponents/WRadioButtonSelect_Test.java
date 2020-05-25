package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WRadioButtonSelect}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WRadioButtonSelect_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect();
		Assert.assertNull("Incorrect options returned", radioButtonSelect.getOptions());
		Assert.assertTrue("allowNoSelection should be true", radioButtonSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), radioButtonSelect.
				getOptions());
		Assert.assertTrue("allowNoSelection should be true", radioButtonSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect(options);
		Assert.assertEquals("Incorrect options returned", options, radioButtonSelect.getOptions());
		Assert.assertTrue("allowNoSelection should be true", radioButtonSelect.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, radioButtonSelect.
				getLookupTable());
		Assert.assertTrue("allowNoSelection should be true", radioButtonSelect.isAllowNoSelection());
	}

	@Test
	public void testFramelessAccessors() {
		assertAccessorsCorrect(new WRadioButtonSelect(), WRadioButtonSelect::isFrameless, WRadioButtonSelect::setFrameless, false, true, false);
	}

	@Test
	public void testButtonColumnsAccessors() {
		assertAccessorsCorrect(new WRadioButtonSelect(), WRadioButtonSelect::getButtonColumns, WRadioButtonSelect::setButtonColumns, 0, 1, 2);
		try {
			WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect();
			radioButtonSelect.setButtonColumns(0);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
		}
	}

	@Test
	public void testButtonlayoutAccessors() {
		assertAccessorsCorrect(new WRadioButtonSelect(), WRadioButtonSelect::getButtonLayout, WRadioButtonSelect::setButtonLayout,
			WRadioButtonSelect.LAYOUT_STACKED, WRadioButtonSelect.LAYOUT_COLUMNS, WRadioButtonSelect.LAYOUT_FLAT);
	}

	@Test
	public void testAjaxAccessors() {
		WRadioButtonSelect radioButtonSelect = new WRadioButtonSelect();
		Assert.assertFalse("isAjax enabled should be false", radioButtonSelect.isAjax());

		// With User Context
		setActiveContext(createUIContext());
		radioButtonSelect.setLocked(true);
		Assert.assertFalse("isAjax enabled should be false in uic", radioButtonSelect.isAjax());

		// Set AJAX Target
		radioButtonSelect.setAjaxTarget(new WPanel());
		Assert.assertTrue("isAjax enabled should be true", radioButtonSelect.isAjax());

		resetContext();
		Assert.assertFalse("isAjax enabled should be false after reset", radioButtonSelect.isAjax());
	}

	@Test
	public void testAjaxTargetAccessors() {
		assertAccessorsCorrect(new WRadioButtonSelect(), WRadioButtonSelect::getAjaxTarget, WRadioButtonSelect::setAjaxTarget,
			null, new WPanel(), new WPanel());
	}

}
