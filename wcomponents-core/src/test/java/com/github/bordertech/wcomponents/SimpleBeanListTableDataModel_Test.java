package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.AbstractComparator;
import com.github.bordertech.wcomponents.util.ComparableComparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SimpleBeanListTableDataModel}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SimpleBeanListTableDataModel_Test {

	/**
	 * The model to test.
	 */
	private SimpleBeanListTableDataModel model;

	/**
	 * The backing bean array for the data model.
	 */
	private PersonBean[] beans;

	@Before
	public void setUpModel() {
		beans = new PersonBean[]{
			new PersonBean("first3", "last1"),
			new PersonBean("first1", "last2"),
			new PersonBean("first2", "last3")
		};

		model = new SimpleBeanListTableDataModel(new String[]{".", "givenNames", "lastName"},
				Arrays.asList(beans));
	}

	@Test
	public void testGetRowCount() {
		Assert.assertEquals("Incorrect row count", beans.length, model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		for (int i = 0; i < beans.length; i++) {
			Assert.assertEquals("Incorrect value at row " + i + " column 0", beans[i], model.
					getValueAt(i, 0));
			Assert.assertEquals("Incorrect value at row " + i + " column 1", beans[i].
					getGivenNames(), model.getValueAt(i, 1));
			Assert.assertEquals("Incorrect value at row " + i + " column 2", beans[i].getLastName(),
					model.getValueAt(i, 2));
		}
	}

	@Test
	public void testIsSortable() {
		Assert.assertFalse("Column 0 should not be sortable by default", model.isSortable(0));
		Assert.assertFalse("Column 1 should not be sortable by default", model.isSortable(1));
		Assert.assertFalse("Column 2 should not be sortable by default", model.isSortable(2));

		model.setComparator(1, new ComparableComparator());
		Assert.assertFalse("Column 0 should not be sortable by default", model.isSortable(0));
		Assert.assertTrue("Column 1 should be sortable after comparator has been set", model.
				isSortable(1));
		Assert.assertFalse("Column 2 should not be sortable by default", model.isSortable(2));

		model.setComparator(1, null);
		Assert.assertFalse("Column 0 should not be sortable by default", model.isSortable(0));
		Assert.assertFalse("Column 1 should be sortable after comparator has been removed", model.
				isSortable(1));
		Assert.assertFalse("Column 2 should not be sortable by default", model.isSortable(2));
	}

	@Test
	public void testEditableAccessors() {
		Assert.assertFalse("Should not be editable by default", model.isEditable());
		Assert.assertFalse("Should not be editable by default", model.isCellEditable(0, 0));

		model.setEditable(true);
		Assert.assertTrue("Should be editable after call to setEditable(true)", model.isEditable());
		Assert.assertTrue("Should be editable after call to setEditable(true)", model.
				isCellEditable(0, 0));

		model.setEditable(false);
		Assert.assertFalse("Should not be editable after call to setEditable(false)", model.
				isEditable());
		Assert.assertFalse("Should not be editable after call to setEditable(false)", model.
				isCellEditable(0, 0));
	}

	@Test(expected = IllegalStateException.class)
	public void testSetValueAtNotEditable() {
		model.setValueAt("fred", 0, 1);
	}

	@Test
	public void testSetValue() {
		model.setEditable(true);
		model.setValueAt("fred", 1, 1);
		Assert.assertEquals("Bean value should have changed", "fred", beans[1].getGivenNames());

		PersonBean editedBean = new PersonBean("first3", "last1");
		model.setValueAt(editedBean, 1, 0);
		Assert.assertSame("Bean should have changed", editedBean, model.getValueAt(1, 0));
	}

	@Test(expected = IllegalStateException.class)
	public void testSortNotSortable() {
		model.setComparator(1, new ComparableComparator());
		model.sort(2, true);
	}

	@Test
	public void testSort() {
		List<PersonBean> beanList = new ArrayList<>(Arrays.asList(beans));

		Comparator givenNameComparator = new AbstractComparator() {
			@Override
			protected Comparable getComparable(final Object obj) {
				return ((PersonBean) obj).getGivenNames();
			}
		};

		model.setComparator(1, new ComparableComparator());

		Collections.sort(beanList, givenNameComparator);
		int[] sortIndices = model.sort(1, true);

		for (int i = 0; i < beans.length; i++) {
			Assert.assertEquals("Incorrect value at row " + i + " after asc sort", beanList.get(i),
					model.getValueAt(sortIndices[i], 0));
		}

		Collections.reverse(beanList);
		sortIndices = model.sort(1, false);

		for (int i = 0; i < beans.length; i++) {
			Assert.assertEquals("Incorrect value at row " + i + " after desc sort", beanList.get(i),
					model.getValueAt(sortIndices[i], 0));
		}
	}

	/**
	 * An aribitrary bean for testing.
	 */
	public static final class PersonBean implements Serializable {

		/**
		 * The person's given names.
		 */
		private String givenNames;

		/**
		 * The person's last name.
		 */
		private String lastName;

		/**
		 * Creates a PersonBean.
		 *
		 * @param givenNames the person's given names.
		 * @param lastName the person's last name.
		 */
		public PersonBean(final String givenNames, final String lastName) {
			this.givenNames = givenNames;
			this.lastName = lastName;
		}

		/**
		 * @return Returns the givenNames.
		 */
		public String getGivenNames() {
			return givenNames;
		}

		/**
		 * @return Returns the lastName.
		 */
		public String getLastName() {
			return lastName;
		}

		/**
		 * Sets the given names.
		 *
		 * @param givenNames the given names to set.
		 */
		public void setGivenNames(final String givenNames) {
			this.givenNames = givenNames;
		}

		/**
		 * @param lastName The lastName to set.
		 */
		public void setLastName(final String lastName) {
			this.lastName = lastName;
		}

		@Override
		public String toString() {
			return givenNames + ' ' + lastName;
		}
	}
}
