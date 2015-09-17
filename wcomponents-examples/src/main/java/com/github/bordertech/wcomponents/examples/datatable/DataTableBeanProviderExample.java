package com.github.bordertech.wcomponents.examples.datatable;

import com.github.bordertech.wcomponents.AbstractBeanTableDataModel;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.LogFactory;

/**
 * This example shows the use of a {@link WDataTable} with a list of beans.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DataTableBeanProviderExample extends WBeanContainer {

	/**
	 * A fake "application cache", that holds the data which is displayed by the table.
	 */
	private static final Map<Object, Object> APPLICATION_CACHE = new HashMap<>();

	/**
	 * Creates a DataTableBeanExample.
	 */
	public DataTableBeanProviderExample() {
		// Create the example data and add it to the "application cache"
		final String cacheKey = "dummyCacheKey";
		APPLICATION_CACHE.put(cacheKey, createExampleData());

		setBeanProvider(new BeanProvider() {
			@Override
			public Object getBean(final BeanProviderBound beanProviderBound) {
				return APPLICATION_CACHE.get(cacheKey);
			}
		});

		WDataTable table = createTable();
		table.setDataModel(new PersonDataModel());
		table.setBeanProperty(".");

		add(table);
		table.addAction(new WButton("Refresh"));
	}

	/**
	 * Creates and configures the table to be used by the example. The table is configured with global rather than user
	 * data. Although this is not a realistic scenario, it will suffice for this example.
	 *
	 * @return a new configured table.
	 */
	private WDataTable createTable() {
		WDataTable tbl = new WDataTable();
		tbl.addColumn(new WTableColumn("First name", new WText()));
		tbl.addColumn(new WTableColumn("Last name", new WText()));
		tbl.addColumn(new WTableColumn("DOB", new WText()));
		return tbl;
	}

	/**
	 * Creates the example data.
	 *
	 * @return the example data.
	 */
	private List<PersonBean> createExampleData() {
		List<PersonBean> data = new ArrayList<>(3);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");

		try {
			data.add(new PersonBean("Joe", "Bloggs", sdf.parse("01/02/1973")));
			data.add(new PersonBean("Jane", "Bloggs", sdf.parse("04/05/1976")));
			data.add(new PersonBean("Kid", "Bloggs", sdf.parse("31/12/1999")));
		} catch (ParseException e) {
			LogFactory.getLog(DataTableBeanExample.class).error("Failed to create test data", e);
		}

		return data;
	}

	/**
	 * An example data model that shows how to display data from a bean.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class PersonDataModel extends AbstractBeanTableDataModel {

		/**
		 * The first name column id.
		 */
		private static final int FIRST_NAME = 0;
		/**
		 * The last name column id.
		 */
		private static final int LAST_NAME = 1;
		/**
		 * The date of birth name column id.
		 */
		private static final int DOB = 2;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			List<PersonBean> bean = (List<PersonBean>) getBean();
			return bean.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			List<PersonBean> bean = (List<PersonBean>) getBean();
			PersonBean person = bean.get(row);

			switch (col) {
				case FIRST_NAME:
					return person.getFirstName();

				case LAST_NAME:
					return person.getLastName();

				case DOB: {
					if (person.getDateOfBirth() == null) {
						return null;
					}

					return new SimpleDateFormat("d MMM yyyy").format(person.getDateOfBirth());
				}

				default:
					return null;
			}
		}
	}
}
