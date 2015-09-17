package com.github.bordertech.wcomponents;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AbstractBeanTableDataModel}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AbstractBeanTableDataModel_Test {

	@Test
	public void testGetBeanDirect() {
		MyDataModel model = new MyDataModel();
		model.setBeanProvider(new MyBeanProvider());

		Assert.assertEquals("Should not have any rows (provider has no id)", 0, model.getRowCount());

		model.setBeanId(2);
		Assert.assertEquals("Should not have any rows (property not set)", 0, model.getRowCount());

		model.setBeanProperty(".");
		Assert.assertEquals("Should not have any rows (property incorrect)", 0, model.getRowCount());

		model.setBeanProperty("myProperty");
		Assert.assertEquals("Incorrect number of rows", 2, model.getRowCount());
		Assert.assertEquals("Incorrect model value", "1", model.getValueAt(0, 0));
		Assert.assertEquals("Incorrect model value", "2", model.getValueAt(1, 0));
	}

	@Test
	public void testGetBeanFromTable() {
		MyDataModel model = new MyDataModel();

		WDataTable table = new WDataTable();
		table.setDataModel(model);
		table.setBeanProvider(new MyBeanProvider());

		Assert.assertEquals("Should not have any rows (provider has no id)", 0, model.getRowCount());

		table.setBeanId(2);
		Assert.assertEquals("Should not have any rows (property not set)", 0, model.getRowCount());

		table.setBeanProperty(".");
		Assert.assertEquals("Should not have any rows (property incorrect)", 0, model.getRowCount());

		table.setBeanProperty("myProperty");
		Assert.assertEquals("Incorrect number of rows", 2, model.getRowCount());
		Assert.assertEquals("Incorrect model value", "1", model.getValueAt(0, 0));
		Assert.assertEquals("Incorrect model value", "2", model.getValueAt(1, 0));
	}

	/**
	 * A simple bean provider implementation that returns different data depending on the bean id.
	 */
	private static final class MyBeanProvider implements BeanProvider {

		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			Object beanId = beanProviderBound.getBeanId();

			if (beanId instanceof Integer) {
				MyBean bean = new MyBean();

				int count = (Integer) beanId;
				String[] data = new String[count];

				for (int i = 0; i < count; i++) {
					data[i] = String.valueOf(i + 1);
				}

				bean.setMyProperty(data);
				return bean;
			}

			return null;
		}
	}

	/**
	 * A simple AbstractBeanTableDataModel implementation with a single column.
	 */
	private static final class MyDataModel extends AbstractBeanTableDataModel {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			Object bean = getBean();

			if (bean instanceof String[]) {
				return ((String[]) bean)[row];
			}

			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			Object bean = getBean();

			if (bean instanceof String[]) {
				return ((String[]) bean).length;
			}

			return 0;
		}
	};

	/**
	 * An aribitrary bean for testing.
	 */
	public static final class MyBean {

		/**
		 * An arbitrary bean property.
		 */
		private String[] myProperty;

		/**
		 * @param myProperty the bean property value to set.
		 */
		public void setMyProperty(final String[] myProperty) {
			this.myProperty = myProperty;
		}

		/**
		 * @return the bean property value.
		 */
		public String[] getMyProperty() {
			return myProperty;
		}
	}
}
