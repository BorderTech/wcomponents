package com.github.dibp.wcomponents.examples.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dibp.wcomponents.AbstractBeanBoundTableModel;
import com.github.dibp.wcomponents.BeanProvider;
import com.github.dibp.wcomponents.BeanProviderBound;
import com.github.dibp.wcomponents.WBeanContainer;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WDateField;
import com.github.dibp.wcomponents.WTable;
import com.github.dibp.wcomponents.WTableColumn;
import com.github.dibp.wcomponents.WText;

/**
 * This example shows the use of a {@link WTable} with a bean provider that provides the list of beans.
 * <p>
 * It also demonstrates, in a very simplistic way, how to implement caching.
 * </p>
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class TableBeanProviderExample extends WBeanContainer
{
    /** A fake "application cache", that holds the data which is displayed by the table. */
    private static final Map<Object, Object> APPLICATION_CACHE = new HashMap<Object, Object>();

    /** Dummy cache key. */
    private static final String CACHE_KEY = "dummyCacheKey";

    /** The example table. */
    private final WTable table = new WTable();

    // Create the example data and add it to the "application cache"
    static
    {
        APPLICATION_CACHE.put(CACHE_KEY, ExampleDataUtil.createExampleData());
    }

    /**
     * Create example.
     */
    public TableBeanProviderExample()
    {
        // Setup bean provider that gets the data from the cache
        setBeanProvider(new BeanProvider()
        {
            public Object getBean(final BeanProviderBound beanProviderBound)
            {
                return APPLICATION_CACHE.get(CACHE_KEY);
            }
        });

        add(table);
        table.addColumn(new WTableColumn("First name", new WText()));
        table.addColumn(new WTableColumn("Last name", new WText()));
        table.addColumn(new WTableColumn("DOB", new WDateField()));
        table.setTableModel(new PersonDataModel());
        table.addAction(new WButton("Refresh"));

        // Set the bean property so the table goes looking for the "bean"
        table.setBeanProperty(".");
    }

    /**
     * An example data model that shows how to display data from a bean.
     */
    public static final class PersonDataModel extends AbstractBeanBoundTableModel
    {
        /** The first name column id. */
        private static final int FIRST_NAME = 0;
        /** The last name column id. */
        private static final int LAST_NAME = 1;
        /** The date of birth name column id. */
        private static final int DOB = 2;

        /** {@inheritDoc} */
        public int getRowCount()
        {
            List<PersonBean> bean = (List<PersonBean>) getBean();
            return bean.size();
        }

        /** {@inheritDoc} */
        public Object getValueAt(final List<Integer> row, final int col)
        {
            Integer idx = row.get(0);

            List<PersonBean> bean = (List<PersonBean>) getBean();
            PersonBean person = bean.get(idx);

            switch (col)
            {
                case FIRST_NAME:
                    return person.getFirstName();

                case LAST_NAME:
                    return person.getLastName();

                case DOB:
                {
                    return person.getDateOfBirth();
                }

                default:
                    return null;
            }
        }

        /** {@inheritDoc} */
        public int getChildCount(final List<Integer> row)
        {
            return 0;
        }

    }
}
