package ${package}.ui;

import java.util.Collections;
import java.util.List;

import ${package}.model.Address;
import ${package}.model.Customer;
import ${package}.util.DatabaseUtils;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableDataModel;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WButton.ImagePosition;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDataTable.ActionConstraint;
import com.github.bordertech.wcomponents.WDataTable.PaginationMode;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * An example screen to search for customer records.
 */
public class SearchScreen extends WPanel
{
    /** The search criteria. */
    private final SearchCriteriaPanel searchCriteria = new SearchCriteriaPanel();

    /** The search criteria. */
    private final SearchResultsPanel searchResults = new SearchResultsPanel();

    /**
     * Creates a search screen.
     */
    public SearchScreen()
    {
        setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL));
        add(new WHeading(WHeading.SECTION, "Customer search"));
        add(searchCriteria);

        WButton searchButton = new WButton("Search");
        WPanel buttonPanel = new WPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(searchButton, BorderLayout.EAST);
        add(buttonPanel);

        add(new WHeading(WHeading.MINOR, "Search results"));
        WPanel searchResultsPanel = new WPanel();
        searchResultsPanel.add(searchResults);
        add(searchResultsPanel);

        searchButton.setAction(new ValidatingAction(WMessages.getInstance(this).getValidationErrors(), searchCriteria)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                // Get search criteria as entered by the user
                WebUtilities.updateBeanValue(searchCriteria);
                Customer customerBean = (Customer) searchCriteria.getBean();

                // Create the search criteria. In this example it's also a Customer bean.
                Customer criteria = new Customer();
                criteria.setFirstName(customerBean.getFirstName());
                criteria.setLastName(customerBean.getLastName());
                criteria.setDateOfBirth(customerBean.getDateOfBirth());

                searchResults.doSearch(criteria);
            }
        });

        searchButton.setAjaxTarget(searchResultsPanel);
    }

    /**
     * Override preparePaint to initialise the data for the user.
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        if (!isInitialised())
        {
            super.preparePaintComponent(request);
            searchCriteria.setBean(new Customer());
            setInitialised(true);
        }
        else
        {
            super.preparePaintComponent(request);
        }
    }

    /**
     * The search criteria panel.
     * Expects a {@link Customer} bean to be available.
     */
    private static final class SearchCriteriaPanel extends WBeanContainer
    {
        /**
         * Creates a SearchCriteriaPanel.
         */
        public SearchCriteriaPanel()
        {
            WFieldSet fieldSet = new WFieldSet("Search criteria");
            add(fieldSet);

            WFieldLayout fieldLayout = new WFieldLayout();
            fieldSet.add(fieldLayout);

            WTextField firstName = new WTextField();
            firstName.setBeanProperty("firstName");
            fieldLayout.addField("First name", firstName).getLabel().setHint("Partial match, case insensitive");

            WTextField lastName = new WTextField();
            lastName.setBeanProperty("lastName");
            fieldLayout.addField("Last name", lastName).getLabel().setHint("Partial match, case insensitive");
        }
    }

    /**
     * The search results panel.
     */
    private static final class SearchResultsPanel extends WBeanContainer
    {
        /** The search results table. */
        private final WDataTable resultsTable = new WDataTable();

        /** The table action to delete a customer record. */
        private final WButton deleteButton = new WButton("Delete");

        /** The table action to edit a customer record. */
        private final WButton editButton = new WButton("Edit");

        /**
         * Creates a SearchResultsPanel.
         */
        public SearchResultsPanel()
        {
            setBeanProvider(new BeanProvider()
            {
                public Object getBean(final BeanProviderBound beanProviderBound)
                {
                    Customer criteria = (Customer) beanProviderBound.getBeanId();

                    if (criteria == null)
                    {
                        return Collections.emptyList();
                    }

                    // This bean returned by this method should be cached, otherwise
                    // there will be multiple trips made to the d/b per request.
                    return DatabaseUtils.search(criteria);
                }
            });

            resultsTable.setDataModel(new SimpleBeanBoundTableDataModel(new String[] { "customerId", "firstName", "lastName", "address" }));
            resultsTable.setSelectMode(WDataTable.SelectMode.SINGLE);
            resultsTable.setRowsPerPage(10);
            resultsTable.setPaginationMode(PaginationMode.DYNAMIC);
            resultsTable.setBeanProperty(".");

            resultsTable.addColumn(new WTableColumn("Customer Id", new WText()));
            resultsTable.addColumn(new WTableColumn("First name", new WText()));
            resultsTable.addColumn(new WTableColumn("Last name", new WText()));
            resultsTable.addColumn(new WTableColumn("Address", new AddressRenderer()));

            deleteButton.setImage(new ImageResource("/icons/delete.png"));
            deleteButton.setImagePosition(ImagePosition.WEST);
            resultsTable.addAction(deleteButton);
            resultsTable.addActionConstraint(deleteButton, new ActionConstraint(1, 1, true, "Please select a customer to delete"));
            resultsTable.addActionConstraint(deleteButton, new ActionConstraint(-1, -1, false, "Are you sure that you want to delete this customer?"));

            deleteButton.setAction(new ValidatingAction(WMessages.getInstance(this).getValidationErrors(), this)
            {
                @Override
                public void executeOnValid(final ActionEvent event)
                {
                    List<Customer> customers = (List<Customer>) getBean();

                    for (int row : resultsTable.getSelectedRows())
                    {
                        DatabaseUtils.delete(customers.get(row));
                    }

                    WMessages.getInstance(SearchResultsPanel.this).info("Customer(s) successfully deleted");
                    doSearch((Customer) getBeanId());
                }
            });

            editButton.setImage(new ImageResource("/icons/edit.png"));
            editButton.setImagePosition(ImagePosition.WEST);
            resultsTable.addAction(editButton);
            resultsTable.addActionConstraint(editButton, new ActionConstraint(1, 1, true, "Please select a customer to edit"));

            editButton.setAction(new ValidatingAction(WMessages.getInstance(this).getValidationErrors(), this)
            {
                @Override
                public void executeOnValid(final ActionEvent event)
                {
                    List<Customer> customers = (List<Customer>) getBean();
                    int rowIndex = resultsTable.getSelectedRows().get(0);
                    int customerId = customers.get(rowIndex).getCustomerId();
                    MyApp.getInstance(SearchResultsPanel.this).navigateToEdit(customerId);
                }
            });

            add(resultsTable);
            editButton.setDisabled(true);
            deleteButton.setDisabled(true);
        }

        /**
         * Conducts a search.
         * @param criteria the search criteria.
         */
        public void doSearch(final Customer criteria)
        {
            reset();
            setBeanId(criteria);

            boolean hasResults = resultsTable.getDataModel().getRowCount() > 0;
            editButton.setDisabled(!hasResults);
            deleteButton.setDisabled(!hasResults);
        }
    }

    /**
     * This text extension is used to format the address correctly.
     * It expects to be bound to an Address bean.
     */
    private static final class AddressRenderer extends WText
    {
        /**
         * @return a formatted Address text string
         */
        @Override
        public String getText()
        {
            Address address = (Address) getBean();

            if (address == null)
            {
                return "";
            }

            StringBuffer text = new StringBuffer(address.getLine1());

            if (address.getLine2() != null)
            {
                text.append(", ").append(address.getLine2());
            }

            if (address.getCity() != null)
            {
                text.append(", ").append(address.getCity());
            }

            text.append(", ").append(address.getState());
            text.append(", ").append(address.getCountry());
            text.append(' ').append(address.getPostcode());

            return text.toString();
        }
    }
}
