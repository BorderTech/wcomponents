package com.github.dibp.wcomponents.examples.theme;

import java.util.Date;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.Margin;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WAjaxControl;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCancelButton;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WDateField;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WFieldSet;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.layout.BorderLayout;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;
import com.github.dibp.wcomponents.subordinate.Disable;
import com.github.dibp.wcomponents.subordinate.Enable;
import com.github.dibp.wcomponents.subordinate.Equal;
import com.github.dibp.wcomponents.subordinate.Rule;
import com.github.dibp.wcomponents.subordinate.WSubordinateControl;

/**
 * This example displays the features of {@link WDateField}.
 *
 * @author Ming Gao
 */
public class WDateFieldExample extends WContainer
{
    /** The WDateField used for getting and setting in this example */
    private final WDateField dateField = new WDateField();
    /** a readOnly WDateField */
    private final WDateField dateFieldReadOnly = new WDateField();
    /**The main WFieldLayoutused in the example */
    private final WFieldLayout layout = new WFieldLayout();
    
    /** Creates a WDateFieldExample. */
    public WDateFieldExample()
    {
        /* The readOnly state of WDateField is used to output non-interactive
         * content. It is still an input though and should usually have a label
         * of some kind. In this example all of the fields are aded to a
         * WFieldLayout.*/
        dateFieldReadOnly.setReadOnly(true);

        final WTextField textField = new WTextField();
        textField.setColumns(40);
        textField.setDisabled(true);

        WButton copyDateBtn = new WButton("Copy text from 'Date' to 'Text output'");
        copyDateBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                textField.setText(dateField.getText());
            }
        });
        copyDateBtn.setAjaxTarget(textField);

        WButton copyJavaDateBtn = new WButton("Copy date value from 'Date' to 'Text output'");
        copyJavaDateBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                Date date = dateField.getDate();

                if (date != null)
                {
                    textField.setText(date.toString());
                }
                else
                {
                    textField.setText(null);
                }
            }
        });
        copyJavaDateBtn.setAjaxTarget(textField);

        WButton setTodayBtn = new WButton("Set 'Date' to today");
        setTodayBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                dateField.setDate(new Date());
            }
        });
        setTodayBtn.setAjaxTarget(dateField);

        
        layout.addField("Date", dateField);
        layout.addField("Text output", textField);
        
        layout.addField("Read only date field", dateFieldReadOnly).getLabel().setHint("populated from the editable date field above");

        /*
         * WDateFields with a date on load.
         */
        WDateField todayDF = new WDateField();
        todayDF.setDate(new Date());
        layout.addField("Today", todayDF);

        WDateField todayDFro = new WDateField();
        todayDFro.setDate(new Date());
        todayDFro.setReadOnly(true);
        layout.addField("Today (read only)", todayDFro);
        
        /* disabled WDateField */
        WDateField disabledDateField = new WDateField();
        disabledDateField.setDisabled(true);
        layout.addField("Disabled", disabledDateField);
        
        disabledDateField = new WDateField();
        disabledDateField.setDate(new Date());
        disabledDateField.setDisabled(true);
        layout.addField("Disabled (today)", disabledDateField);

        //do the layout
        WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
        buttonPanel.setMargin(new Margin(12, 0, 0, 0));
        buttonPanel.setLayout(new BorderLayout());
        
        WPanel innerButtonPanel = new WPanel();
        innerButtonPanel.setLayout(new FlowLayout(Alignment.LEFT, 6, 0));
        buttonPanel.add(innerButtonPanel, BorderLayout.CENTER);
        innerButtonPanel.add(setTodayBtn);
        innerButtonPanel.add(copyDateBtn);
        innerButtonPanel.add(copyJavaDateBtn);
        /*
         * NOTE: the cancel button is here to test a previous race condition in the theme.
         * If you do not change the WDateFields it should not cause an unsaved changes warning.
         */
        WCancelButton cancelButton = new WCancelButton("Cancel");
        cancelButton.setAction(new Action(){
            @Override
            public void execute(ActionEvent event)
            {
                layout.reset();
            }
        });
        buttonPanel.add(cancelButton, BorderLayout.EAST);
        

        add(layout);
        add(buttonPanel);
        add(new WAjaxControl(dateField, dateFieldReadOnly));
        
        addDateRangeExample();
        addContraintExamples();
    }
    
    private void addDateRangeExample()
    {
        
        add(new WHeading(WHeading.MAJOR, "Example of a date range component"));
        WFieldSet dateRange = new WFieldSet("Enter the expected arrival and departure dates.");
        add(dateRange);
        
        WPanel dateRangePanel = new WPanel();
        dateRangePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dateRange.add(dateRangePanel);
        final WDateField arrivalDate = new WDateField();
        final WDateField departureDate = new WDateField();
        //One could add some validation rules around this so that "arrival" was always earlier than or equal to "departure"
        WLabel arrivalLabel = new WLabel("Arrival", arrivalDate);
        arrivalLabel.setHint("dd MMM yyyy");
        WLabel departureLabel = new WLabel("Departure", departureDate);
        departureLabel.setHint("dd MMM yyyy");
        dateRangePanel.add(arrivalLabel);
        dateRangePanel.add(arrivalDate);
        dateRangePanel.add(departureLabel);
        dateRangePanel.add(departureDate);
        
       //subordinate control to ensure that the departure date is only enabled if the arrival date is populated
        WSubordinateControl control = new WSubordinateControl();
        add(control);
        Rule rule = new Rule(new Equal(arrivalDate, null));
        control.addRule(rule);
        rule.addActionOnTrue(new Disable(departureDate));
        rule.addActionOnFalse(new Enable(departureDate));
        control.addRule(rule);
    }
    
    private void addContraintExamples()
    {
        add(new WHeading(WHeading.MAJOR, "Date fields with input constraints"));
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(33);
        add(layout);
        
        
        /* mandatory */
        WDateField constrainedDateField = new WDateField();
        constrainedDateField.setMandatory(true);
        layout.addField("Mandatory date field", constrainedDateField);
        
        /* min date */
        constrainedDateField = new WDateField();
        constrainedDateField.setMinDate(new Date());
        layout.addField("Minimum date today", constrainedDateField);

        /* max date */
        constrainedDateField = new WDateField();
        constrainedDateField.setMaxDate(new Date());
        layout.addField("Maximum date today", constrainedDateField);
    }

    /**
     * Reflect the value of dateField in the read only version.
     * @param request
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        dateFieldReadOnly.setDate(dateField.getDate());
    }
}