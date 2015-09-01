package ${package}.ui;

import ${package}.model.Customer;
import ${package}.util.DatabaseUtils;
import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WBeanContainer;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCancelButton;
import com.github.dibp.wcomponents.WDateField;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WFieldSet;
import com.github.dibp.wcomponents.WMessages;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.layout.BorderLayout;
import com.github.dibp.wcomponents.validation.ValidatingAction;

/**
 * This screen is used to edit customer details.
 */
public class EditScreen extends WBeanContainer
{
    /**
     * Creates an EditScreen.
     */
    public EditScreen()
    {
        // Customer details
        WFieldSet fieldSet = new WFieldSet("Customer details");
        add(fieldSet);

        WFieldLayout fieldLayout = new WFieldLayout();
        fieldSet.add(fieldLayout);

        WTextField customerId = new WTextField();
        customerId.setBeanProperty("customerId");
        customerId.setReadOnly(true);
        fieldLayout.addField("Customer id", customerId);

        WTextField firstName = new WTextField();
        firstName.setBeanProperty("firstName");
        firstName.setMandatory(true);
        fieldLayout.addField("First name", firstName);

        WTextField lastName = new WTextField();
        lastName.setBeanProperty("lastName");
        lastName.setMandatory(true);
        fieldLayout.addField("Last name", lastName);

        WDateField dateOfBirth = new WDateField();
        dateOfBirth.setBeanProperty("dateOfBirth");
        fieldLayout.addField("Date of Birth", dateOfBirth);

        // Customer address details
        WFieldSet addressFieldSet = new WFieldSet("Customer address");
        add(addressFieldSet);

        WFieldLayout addressFieldLayout = new WFieldLayout();
        addressFieldSet.add(addressFieldLayout);

        WTextField addressLine1 = new WTextField();
        addressLine1.setBeanProperty("address.line1");
        addressLine1.setMandatory(true);
        addressFieldLayout.addField("Street address", addressLine1);

        WTextField addressLine2 = new WTextField();
        addressLine2.setBeanProperty("address.line2");
        addressFieldLayout.addField("Street address line 2", addressLine2).getLabel().setHidden(true);

        WTextField city = new WTextField();
        city.setBeanProperty("address.city");
        city.setMandatory(true);
        addressFieldLayout.addField("Town/City", city);

        WTextField state = new WTextField();
        state.setBeanProperty("address.state");
        state.setMandatory(true);
        addressFieldLayout.addField("State/Province", state);

        WTextField country = new WTextField();
        country.setBeanProperty("address.country");
        addressLine1.setMandatory(true);
        addressFieldLayout.addField("Country", country);

        WButton saveButton = new WButton("Save");
        WCancelButton cancelButton = new WCancelButton("Cancel");

        WPanel buttonPanel = new WPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(cancelButton, BorderLayout.EAST);
        buttonPanel.add(saveButton, BorderLayout.EAST);
        add(buttonPanel);

        saveButton.setAction(new ValidatingAction(WMessages.getInstance(EditScreen.this).getValidationErrors(), EditScreen.this)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                WebUtilities.updateBeanValue(EditScreen.this);
                Customer customer = (Customer) getBean();
                DatabaseUtils.save(customer);
                setCustomer(customer); // details may have been updated after save
                WMessages.getInstance(EditScreen.this).info("Customer saved succesfully.");
            }
        });

        cancelButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                MyApp.getInstance(EditScreen.this).navigateToSearch();
            }
        });
    }

    /**
     * Sets the customer to edit.
     * @param customer the customer to edit.
     */
    public void setCustomer(final Customer customer)
    {
        reset(); // clear out any user data
        setBean(customer);
    }
}
