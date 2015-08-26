package com.github.openborders.wcomponents.examples.theme.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.Margin;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WAjaxControl;
import com.github.openborders.wcomponents.WBeanContainer;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WField;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WFieldSet;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WRepeater;
import com.github.openborders.wcomponents.WTextField;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.util.Util;

/**
 * Demonstrates how AJAX can be used with a {@link WRepeater}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AjaxWRepeaterExample extends WContainer
{
    /** Repeater with rows using AJAX. */
    private final WRepeater repeat;

    /**
     * Construct the example.
     */
    public AjaxWRepeaterExample()
    {
        WPanel panel = new WPanel();
        repeat = new WRepeater(new RowComponent());

        panel.add(repeat);

        // Button used to add a new row via AJAX.
        WButton button = new WButton("Add row via AJAX");
        button.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                List<NameBean> names = new ArrayList<NameBean>(repeat.getBeanList());
                names.add(new NameBean(new Date().toString(), "F", "L"));
                repeat.setBeanList(names);
            }
        });
        button.setAjaxTarget(panel);

        add(new WHeading(WHeading.MAJOR, "WRepeater using ajax"));
        add(panel);
        
        WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,3,0));
        buttonPanel.setMargin(new Margin(12, 0, 0, 0));
        add(buttonPanel);
        buttonPanel.add(button);
        buttonPanel.add(new WButton("Submit"));
    }

    /**
     * Initialise the WRepeater data.
     * 
     * @param request the request being processed
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        if (!isInitialised())
        {
            repeat.setData(getNames());
            setInitialised(true);
        }
    }

    /**
     * @return a list of names
     */
    private List<NameBean> getNames()
    {
        List<NameBean> names = new ArrayList<NameBean>();
        names.add(new NameBean("id1", "John", "Smith"));
        names.add(new NameBean("id2", "Peter", "Parker"));
        names.add(new NameBean("id3", "Clark", "Kent", true, "Extra"));
        return names;
    }

    /**
     * Component used by the repeater, that demonstrates using ajax.
     */
    public static class RowComponent extends WBeanContainer
    {
        /** Checkbox that initiates ajax request. */
        private final WCheckBox extraBox = new WCheckBox();
        /** Text field that is displayed when the checkbox is selected. */
        private final WField extraField;
        /** First name TextField. */
        private final WTextField firstName = new WTextField();
        /** Last name TextField. */
        private final WTextField lastName = new WTextField();
        /** Extra text TextField. */
        private final WTextField extraText = new WTextField();

        /**
         * Construct the row component.
         */
        public RowComponent()
        {
            firstName.setBeanProperty("firstName");
            lastName.setBeanProperty("lastName");
            extraBox.setBeanProperty("extra");
            extraText.setBeanProperty("extraText");

            WFieldSet fieldset = new WFieldSet("Person Details");
            add(fieldset);
            fieldset.setMargin(new Margin(0, 0, 12, 0));
            
            WFieldLayout layout = new WFieldLayout();
            layout.setLabelWidth(30);
            layout.addField("First", firstName);
            layout.addField("Last", lastName);

            layout.addField("Extra text via ajax", extraBox);
            extraField = layout.addField("Extra text", extraText);

            fieldset.add(layout);

            // Set up an ajax request when the extraBox is clicked to refresh the row.
            WAjaxControl control = new WAjaxControl(extraBox, layout);
            add(control);
        }

        /**
         * Set "extraText" visible fi the extraBox has been selected.
         * 
         * @param request the request being processed
         */
        @Override
        protected void preparePaintComponent(final Request request)
        {
            extraField.setVisible(extraBox.isSelected());
        }
    }

    /**
     * Bean used by the WRepeater.
     */
    public static class NameBean implements Serializable
    {
        /** Unique id for bean. */
        private String nameId;
        /** First name. */
        private String firstName;
        /** Last name. */
        private String lastName;
        /** Extra information flag. */
        private boolean extra;
        /** Extra Text. */
        private String extraText;

        /**
         * Construct the Bean.
         */
        public NameBean()
        {
            // Do nothing
        }

        /**
         * @param nameId the unique name id
         * @param firstName the first name
         * @param lastName the last name
         */
        public NameBean(final String nameId, final String firstName, final String lastName)
        {
            this(nameId, firstName, lastName, false, null);
        }

        /**
         * @param nameId the unique name id
         * @param firstName the first name
         * @param lastName the last name
         * @param extra extra flag
         * @param extraText extra text
         */
        public NameBean(final String nameId, final String firstName, final String lastName, final boolean extra,
                        final String extraText)
        {
            this.nameId = nameId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.extra = extra;
            this.extraText = extraText;
        }

        /**
         * @return the unique name id
         */
        public String getNameId()
        {
            return nameId;
        }

        /**
         * @param nameId the unique name id
         */
        public void setNameId(final String nameId)
        {
            this.nameId = nameId;
        }

        /**
         * @return the first name
         */
        public String getFirstName()
        {
            return firstName;
        }

        /**
         * @param firstName the first name
         */
        public void setFirstName(final String firstName)
        {
            this.firstName = firstName;
        }

        /**
         * @return the last name
         */
        public String getLastName()
        {
            return lastName;
        }

        /**
         * @param lastName the last name
         */
        public void setLastName(final String lastName)
        {
            this.lastName = lastName;
        }

        /**
         * @return true if has extra text
         */
        public boolean isExtra()
        {
            return extra;
        }

        /**
         * @param extra true if has extra text
         */
        public void setExtra(final boolean extra)
        {
            this.extra = extra;
        }

        /**
         * @return the extra text
         */
        public String getExtraText()
        {
            return extraText;
        }

        /**
         * @param extraText the extra text
         */
        public void setExtraText(final String extraText)
        {
            this.extraText = extraText;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o)
        {
            return (o instanceof NameBean) && Util.equals(nameId, ((NameBean) o).getNameId());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return nameId.hashCode();
        }
    }
}
