package com.github.openborders.wcomponents.examples.repeater; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WCheckBoxSelect;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WDataRenderer;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WLabel;
import com.github.openborders.wcomponents.WRepeater;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.WTextArea;
import com.github.openborders.wcomponents.WTextField;

/** 
 * An example demonstrating a WRepeater with editable fields. 
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class RepeaterExampleWithEditableRows extends WContainer
{
    /** The repeater which is used to display the data. */
    private final WRepeater repeater;
    
    /** A text field used to enter in a new contact name.*/
    private final WTextField newNameField;
    
    /** Used to display a text-only view of the data when the "print" button is pressed. */ 
    private final WTextArea console;

    /**
     * Creates a RepeaterExampleWithEditableRows.
     */
    public RepeaterExampleWithEditableRows()
    {
        add(new WHeading(WHeading.MAJOR, "Contacts"));
        
        repeater = new WRepeater();
        repeater.setRepeatedComponent(new PhoneNumberEditPanel());
        
        newNameField = new WTextField();
        WLabel nameLabel = new WLabel("New contact name", newNameField);
        
        WButton addBtn = new WButton("Add");
        addBtn.setAction(new Action() 
        {
            public void execute(final ActionEvent event)
            {
                addNewContact();
            }
        });
        
        console = new WTextArea();
        console.setColumns(60);
        console.setRows(5);
        
        WButton printBtn = new WButton("Print");
        printBtn.setAction(new Action() 
        {
            public void execute(final ActionEvent event)
            {
                printEditedDetails();
            }
        });
        
        add(repeater);
        
        // TODO: This is bad - use a layout instead
        WText lineBreak = new WText("<br />");
        lineBreak.setEncodeText(false);
        add(lineBreak);
        
        add(nameLabel);
        add(newNameField);
        add(addBtn);
        
        // TODO: This is bad - use a layout instead
        lineBreak = new WText("<br />");
        lineBreak.setEncodeText(false);
        add(lineBreak);
        lineBreak = new WText("<br />");
        lineBreak.setEncodeText(false);
        add(lineBreak);
        
        add(console);
        add(printBtn);
    }
    
    /**
     * Adds a new contact to the list of contacts.
     * The contact's name is obtained from {@link newNameField}.
     */
    private void addNewContact()
    {
        List list = new ArrayList(repeater.getBeanList());
        list.add(new ContactDetails(newNameField.getText(), null, new String[0]));
        newNameField.setText("");
        repeater.setBeanList(list);
    }
    
    /**
     * Write the list of contacts into the textarea console.
     * Any modified phone numbers should be printed out.
     */
    private void printEditedDetails()
    {
        StringBuffer buf = new StringBuffer();
        
        for (Object contact : repeater.getBeanList())
        {
            buf.append(contact).append('\n');
        }
        
        console.setText(buf.toString());
    }

    /**
     * Override to initialise some data the first time through.
     * 
     * @param request the request being responded to.
     */
    @Override
    public void preparePaintComponent(final Request request)
    {
        if (!this.isInitialised())
        {
            // Give the repeater the list of data to display.
            repeater.setData(fetchDataList());
            
            // Remember that we've done the initialisation.
            this.setInitialised(true);
        }
    }
    
    /**
     * The component to be used to render one row.
     * Note that this component remembers the given data object and keeps it up to date.
     * 
     * @author Martin Shevchenko
     */
    public static class PhoneNumberEditPanel extends WDataRenderer
    {
        /** Used to display/modify the contact's phone number. */
        private final WTextField phoneNumField = new WTextField();
        
        /** Used to display/modify the contact's roles. */
        private final WCheckBoxSelect roleSelect = new WCheckBoxSelect(new String[] {"a", "b", "c"});;

        /**
         * Creates a PhoneNumberEditPanel.
         */
        public PhoneNumberEditPanel()
        {
            roleSelect.setButtonLayout(WCheckBoxSelect.LAYOUT_FLAT);
            roleSelect.setToolTip("Role options");
            roleSelect.setFrameless(true);
            
            WHeading contactName = new WHeading(WHeading.SECTION, "");
            contactName.setBeanProperty("name");
            add(contactName);

            WFieldLayout fieldLayout = new WFieldLayout();
            fieldLayout.setLabelWidth(10);
            fieldLayout.addField("Phone", phoneNumField);
            fieldLayout.addField("Roles", roleSelect);
            add(fieldLayout);
        }

        /** {@inheritDoc} */
        @Override
        public void updateData(final Object data)
        {
            ContactDetails details = (ContactDetails) data;
            details.setPhoneNumber(phoneNumField.getText());
            details.setRoles((List<String>) roleSelect.getSelected());
        }

        /** {@inheritDoc} */
        @Override
        public void updateComponent(final Object data)
        {
            ContactDetails details = (ContactDetails) data;
            phoneNumField.setText(details.getPhoneNumber());
            roleSelect.setSelected(details.getRoles());
        }
    }

    /**
     * Retrieves dummy data used by this example.
     * @return the list of data for this example.
     */
    private static List<ContactDetails> fetchDataList()
    {
        List<ContactDetails> list = new ArrayList<ContactDetails>();
        list.add(new ContactDetails("David", "1234", new String[] {"a", "b"}));
        list.add(new ContactDetails("Jun", "1111", new String[] {"c"}));
        list.add(new ContactDetails("Martin", null, new String[] {"b"}));
        
        return list;
    }
    
    /**
     * A simple data object. 
     * @author Martin Shevchenko
     */
    public static class ContactDetails implements Serializable
    {
        /** The contact name. */
        private String name;
        /** The contact phone number. */
        private String phoneNumber;
        /** The contact roles. */
        private List<String> roles;
        
        /**
         * Creates a ContactDetails.
         * 
         * @param name the contact name.
         * @param phoneNumber the contact phone number.
         * @param roles the contact roles.
         */
        public ContactDetails(final String name, final String phoneNumber, final String[] roles)
        {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.roles = new ArrayList<String>();
            
            for (int i = 0; i < roles.length; i++)
            {
                this.roles.add(roles[i]);
            }
        }
        
        /**
         * @return the contact name
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * Sets the contact name.
         * @param name the contact name.
         */
        public void setName(final String name)
        {
            this.name = name;
        }

        /**
         * @return the contact phone number.
         */
        public String getPhoneNumber()
        {
            return phoneNumber;
        }
        
        /**
         * Sets the contact phone number.
         * @param phoneNumber the contact phone number.
         */
        public void setPhoneNumber(final String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }

        /**
         * @return the contact roles.
         */
        public List<String> getRoles()
        {
            return roles;
        }
        
        /**
         * Sets the contact roles.
         * @param roles the contact roles.
         */
        public void setRoles(final List<String> roles)
        {
            this.roles = roles;
        }

        /**
         * @return a textual representation of this Contact.
         */
        @Override
        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            buf.append(name);
            buf.append(": ");
            buf.append(phoneNumber == null ? "" : phoneNumber);
            buf.append(": ");
            
            for (int i = 0; i < roles.size(); i++)
            {
                if (i > 0)
                {
                    buf.append(',');
                }
                
                buf.append(roles.get(i).toString());
            }
            
            return buf.toString();
        }
    }
}
