package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link SimpleTableDataModel}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SimpleTableDataModel_Test extends AbstractWComponentTestCase {

	@Test
	public void testRowCount() {
		setActiveContext(createUIContext());

		SimpleTableDataModel model = new SimpleTableDataModel(new String[0][0]);
		Assert.assertEquals("Incorrect row count", 0, model.getRowCount());

		model = new SimpleTableDataModel(new String[5][2]);
		Assert.assertEquals("Incorrect row count", 5, model.getRowCount());

		model = new SimpleTableDataModel(new ArrayList<List<Serializable>>());
		Assert.assertEquals("Incorrect row count", 0, model.getRowCount());

		List<List<Serializable>> list = new ArrayList<>();
		list.add(new ArrayList<Serializable>());

		model = new SimpleTableDataModel(list);
		Assert.assertEquals("Incorrect row count", 1, model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		setActiveContext(createUIContext());

		String[][] data = new String[][]{{"1.1", "1.2", "1.3"}, {"2.1", "2.2", "2.3"}};
		SimpleTableDataModel model = new SimpleTableDataModel(data);

		Assert.assertEquals("Incorrect valueAt(0,0)", data[0][0], model.getValueAt(0, 0));
		Assert.assertEquals("Incorrect valueAt(0,1)", data[0][1], model.getValueAt(0, 1));
		Assert.assertEquals("Incorrect valueAt(0,2)", data[0][2], model.getValueAt(0, 2));
		Assert.assertEquals("Incorrect valueAt(1,0)", data[1][0], model.getValueAt(1, 0));
		Assert.assertEquals("Incorrect valueAt(1,1)", data[1][1], model.getValueAt(1, 1));
		Assert.assertEquals("Incorrect valueAt(1,2)", data[1][2], model.getValueAt(1, 2));
	}

	@Test
	public void testIsSortable() {
		setActiveContext(createUIContext());

		SimpleTableDataModel model = new SimpleTableDataModel(new String[5][2]);
		Assert.assertFalse("Model should not be sortable by default", model.isSortable(0));
		Assert.assertFalse("Model should not be sortable by default", model.isSortable(1));

		model.setComparator(1, SimpleTableDataModel.COMPARABLE_COMPARATOR);
		Assert.assertFalse("Column with no comparator should not be sortable", model.isSortable(0));
		Assert.assertTrue("Column with comparator should be sortable", model.isSortable(1));
	}

	@Test
	public void testEditability() {
		setActiveContext(createUIContext());

		SimpleTableDataModel model = new SimpleTableDataModel(new String[0][0]);
		Assert.assertFalse("Model should not be editable by default", model.isEditable());
		Assert.assertFalse("Model should not be editable by default", model.isCellEditable(0, 0));

		model.setEditable(true);
		Assert.assertTrue("Model should be editable after setEditable(true)", model.isEditable());
		Assert.assertTrue("Model should be editable after setEditable(true)", model.
				isCellEditable(0, 0));
	}

	@Test
	public void testSetValueAt() {
		final String[][] data = new String[][]{{"1.1", "1.2", "1.3"}, {"2.1", "2.2", "2.3"}};
		final String oldValue = data[1][2];
		final String newValue = "newValue";

		SimpleTableDataModel model = new SimpleTableDataModel(data);
		setActiveContext(createUIContext());

		// Test on uneditable table
		try {
			model.setValueAt(newValue, 1, 2);
			Assert.fail("Should have thrown an IllegalStateException");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("Thrown exception should have a message", e.getMessage());
			Assert.assertEquals("Value should not have changed", oldValue, model.getValueAt(1, 2));
		}

		model.setEditable(true);
		model.setValueAt(newValue, 1, 2);
		Assert.assertEquals("Incorrect valueAt(1,2)", newValue, model.getValueAt(1, 2));
	}

	@Test
	public void testSort() {
		final String[][] data = new String[][]{{"a", "2"}, {"c", "1"}, {"b", "3"}};

		SimpleTableDataModel model = new SimpleTableDataModel(data);
		setActiveContext(createUIContext());

		try {
			model.sort(0, true);
			Assert.fail("Should have thrown an IllegalStateException");
		} catch (IllegalStateException e) {
			Assert.assertNotNull("Thrown exception should have a message", e.getMessage());
			Assert.assertEquals("Should not have sorted data", "a", model.getValueAt(0, 0));
			Assert.assertEquals("Should not have sorted data", "c", model.getValueAt(1, 0));
			Assert.assertEquals("Should not have sorted data", "b", model.getValueAt(2, 0));
		}

		model.setComparator(0, SimpleTableDataModel.COMPARABLE_COMPARATOR);

		int[] sortIndices = model.sort(0, true);
		int[] expected = {0, 2, 1};

		for (int i = 0; i < sortIndices.length; i++) {
			Assert.assertEquals("Incorrect ascending sort index[" + i + ']', expected[i],
					sortIndices[i]);
		}

		expected = new int[]{1, 2, 0};
		sortIndices = model.sort(0, false);

		for (int i = 0; i < sortIndices.length; i++) {
			Assert.assertEquals("Incorrect descending sort index[" + i + ']', expected[i],
					sortIndices[i]);
		}
	}
}
