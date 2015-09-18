package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WSelectToggle_Test - Unit tests for {@link WSelectToggle}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSelectToggle_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		WSelectToggle toggle = new WSelectToggle();
		Assert.assertTrue("No-args constructor should set client-side", toggle.isClientSide());
		Assert.assertNull("No-args constructor should not set target", toggle.getTarget());

		toggle = new WSelectToggle(false);
		Assert.assertFalse("Should be server-side", toggle.isClientSide());
		Assert.assertNull("Should not have target set", toggle.getTarget());

		WCheckBox target = new WCheckBox();
		toggle = new WSelectToggle(false, target);
		Assert.assertFalse("Should be server-side", toggle.isClientSide());
		Assert.assertSame("Incorrect target set", target, toggle.getTarget());
	}

	@Test
	public void testClientSideAccessors() {
		WSelectToggle toggle = new WSelectToggle(false);
		Assert.assertFalse("Should be server-side", toggle.isClientSide());
		Assert.assertNull("Should not have target set", toggle.getTarget());

		toggle.setClientSide(true);
		Assert.assertTrue("Should be client-side after setClientSide(true)", toggle.isClientSide());

		toggle.setLocked(true);
		setActiveContext(createUIContext());
		toggle.setClientSide(false);

		Assert.assertEquals("Incorrect session client-side after session setClientSide(false)",
				false, toggle.isClientSide());

		resetContext();
		Assert.assertEquals("Default client-side flag should not have changed", true, toggle.
				isClientSide());
	}

	@Test
	public void testTargetAccessors() {
		final WCheckBox target1 = new WCheckBox();
		final WCheckBox target2 = new WCheckBox();
		final WCheckBox target3 = new WCheckBox();

		WSelectToggle toggle = new WSelectToggle(true, target1);
		Assert.assertSame("Incorrect toggle target", target1, toggle.getTarget());

		toggle.setTarget(target2);
		Assert.assertSame("Incorrect toggle target after setTarget", target2, toggle.getTarget());

		toggle.setLocked(true);
		setActiveContext(createUIContext());
		toggle.setTarget(target3);

		Assert.assertSame("Incorrect toggle target after session setTarget", target3, toggle.
				getTarget());

		resetContext();
		Assert.assertSame("Incorrect default toggle target after session setTarget", target2,
				toggle.getTarget());
	}

	@Test
	public void testRenderAsTextAccessors() {
		WSelectToggle toggle = new WSelectToggle(false);
		Assert.assertFalse("Should be server-side", toggle.isRenderAsText());
		Assert.assertNull("Should not have target set", toggle.getTarget());

		toggle.setRenderAsText(true);
		Assert.assertTrue("Should be renderAsText after setRenderAsText(true)", toggle.
				isRenderAsText());

		toggle.setLocked(true);
		setActiveContext(createUIContext());
		toggle.setRenderAsText(false);

		Assert.assertEquals("Incorrect renderAsText type after session setProgressBarType", false,
				toggle.isRenderAsText());

		resetContext();
		Assert.assertEquals("Incorrect default renderAsText after session setRenderAsText(true)",
				true, toggle.isRenderAsText());
	}

	@Test
	public void testSetDisabled() {
		WSelectToggle toggle = new WSelectToggle();

		Assert.assertFalse("Table should not be disabled by default", toggle.isDisabled());

		toggle.setLocked(true);
		setActiveContext(createUIContext());
		toggle.setDisabled(true);
		Assert.assertTrue("Should be disabled for modified session", toggle.isDisabled());

		resetContext();
		Assert.assertFalse("Should not be disabled for other sessions", toggle.isDisabled());
	}

	@Test
	public void testHandleRequest() {
		// Create a target panel containing a variety of "selectable" components.
		WCheckBox checkBox = new WCheckBox();
		WCheckBoxSelect checkBoxSelect = new WCheckBoxSelect(new String[]{"a", "b", "c"});
		WMultiSelect multiSelectList = new WMultiSelect(new String[]{"a", "b", "c"});

		WDataTable dataTable = new WDataTable();
		dataTable.setSelectMode(WDataTable.SelectMode.MULTIPLE);
		dataTable.addColumn(new WTableColumn("dummy", WText.class));
		dataTable.setDataModel(new MyDataModel());

		WPanel targetPanel = new WPanel();
		targetPanel.add(checkBox);
		targetPanel.add(checkBoxSelect);
		targetPanel.add(multiSelectList);
		targetPanel.add(dataTable);

		// Set some default selections, to ensure that existing selections are not toggled.
		checkBoxSelect.setSelected(Arrays.asList(new String[]{"a"}));
		multiSelectList.setSelected(Arrays.asList(new String[]{"b"}));
		dataTable.setSelectedRows(Arrays.asList(new Integer[]{2}));

		// Set up the toggle to be tested
		WSelectToggle toggle = new WSelectToggle(false, targetPanel);

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Check select all
		request.setParameter(toggle.getId(), "all");
		toggle.serviceRequest(request);

		Assert.assertTrue("Checkbox should be checked", checkBox.isSelected());
		Assert.assertEquals("CheckboxSelect should have all items checked", 3, checkBoxSelect.
				getSelected().size());
		Assert.assertEquals("List should have all items selected", 3, multiSelectList.getSelected().
				size());
		Assert.assertEquals("Table should have all rows selected", 3, dataTable.getSelectedRows().
				size());

		// Check select none
		request.setParameter(toggle.getId(), "none");
		toggle.serviceRequest(request);

		Assert.assertFalse("Checkbox should not be checked", checkBox.isSelected());
		Assert.assertEquals("CheckboxSelect should have no items checked", 0, checkBoxSelect.
				getSelected().size());
		Assert.assertEquals("List should have no items selected", 0, multiSelectList.getSelected().
				size());
		Assert.assertEquals("Table should have no rows selected", 0, dataTable.getSelectedRows().
				size());

		// Check that nothing happens when disabled
		toggle.setDisabled(true);
		request.setParameter(toggle.getId(), "all");
		toggle.serviceRequest(request);

		Assert.assertFalse("Checkbox should not be checked", checkBox.isSelected());
		Assert.assertEquals("CheckboxSelect should have no items checked", 0, checkBoxSelect.
				getSelected().size());
		Assert.assertEquals("List should have no items selected", 0, multiSelectList.getSelected().
				size());
		Assert.assertEquals("Table should have no rows selected", 0, dataTable.getSelectedRows().
				size());
	}

	/**
	 * Simple data model that allows row selection.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class MyDataModel extends SimpleTableDataModel {

		/**
		 * Construct model.
		 */
		private MyDataModel() {
			super(new String[][]{{"a"}, {"b"}, {"c"}});
		}

		/**
		 * Indicate that all rows are selectable.
		 *
		 * @param row ignored.
		 * @return true.
		 */
		@Override
		public boolean isSelectable(final int row) {
			return true;
		}
	}
}
