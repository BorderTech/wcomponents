package com.github.dibp.wcomponents.examples.theme.ajax;

import java.util.Arrays;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.AjaxTarget;
import com.github.dibp.wcomponents.WAjaxControl;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCheckBoxSelect;
import com.github.dibp.wcomponents.WDateField;
import com.github.dibp.wcomponents.WDropdown;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WMultiDropdown;
import com.github.dibp.wcomponents.WMultiSelect;
import com.github.dibp.wcomponents.WMultiSelectPair;
import com.github.dibp.wcomponents.WMultiTextField;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WRadioButtonSelect;
import com.github.dibp.wcomponents.WShuffler;
import com.github.dibp.wcomponents.WSingleSelect;
import com.github.dibp.wcomponents.WTextField;

/**
 * This example is for internal testing. It comprises a set of controls which are each a trigger for a WAjaxControl.
 * There is then another WAjaxControl which replaces each of these original controls. The point of this is to ensure
 * that an ajax trigger remains an ajax trigger after it is replaced.
 * 
 * @author Mark Reeves
 * @since 1.0.0
 */
public class AjaxReplaceControllerExample extends WPanel
{
    /** a text field to act as an ajax target for the controller. */
    private final WTextField textField = new WTextField();
    /** a button to replace the controller with itself. */
    private final WButton ajaxButton = new WButton("Refresh controller control");

    /** data for the list controls. */
    private static final String[] DATA = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                                                       "Friday", "Saturday" };

    /**
     * Creates an AjaxReplaceControllerExample.
     */
    public AjaxReplaceControllerExample()
    {

        /** WShuffler was the orignal reason this test was created */
        final WShuffler controller = new WShuffler(Arrays.asList(DATA));
        controller.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller, textField));

        final WMultiSelect controller2 = new WMultiSelect(DATA);
        controller2.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller2.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller2, textField));
        final WSingleSelect controller3 = new WSingleSelect(DATA);
        controller3.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller3.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller3, textField));
        final WMultiSelectPair controller4 = new WMultiSelectPair(DATA);
        controller4.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller4.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller4, textField));

        final WMultiDropdown controller5 = new WMultiDropdown(DATA);
        controller5.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller5.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller5, textField));

        final WRadioButtonSelect controller6 = new WRadioButtonSelect(DATA);
        controller6.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller6.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller6, textField));

        final WCheckBoxSelect controller7 = new WCheckBoxSelect(DATA);
        controller7.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller7.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller7, textField));

        final WDropdown controller8 = new WDropdown(DATA);
        controller8.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller8.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller8, textField));

        final WMultiTextField controller9 = new WMultiTextField();
        controller9.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller9.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller9, textField));

        final WDateField controller10 = new WDateField();
        controller10.setActionOnChange(new Action()
        {

            public void execute(final ActionEvent event)
            {
                textField.setText(controller10.getValueAsString());
            }
        });
        // add the ajax control for the controller
        add(new WAjaxControl(controller10, textField));

        // the text field really just needs to be read only
        textField.setReadOnly(true);

        // the second ajax control is a button
        ajaxButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                // I don't care what this does so lets reset so we can start all over again
                reset();
            }
        });
        add(new WAjaxControl(ajaxButton, new AjaxTarget[] { controller, controller2, controller3, controller4,
                                                           controller5, controller6, controller7, controller8,
                                                           controller9, controller10 }));

        // do the layout
        final WFieldLayout layout = new WFieldLayout();
        add(layout);
        layout.addField("Make a selection", controller);
        layout.addField("Make a selection", controller2);
        layout.addField("Make a selection", controller3);
        layout.addField("Make a selection", controller4);
        layout.addField("Make a selection", controller5);
        layout.addField("Make a selection", controller6);
        layout.addField("Make a selection", controller7);
        layout.addField("Make a selection", controller8);
        layout.addField("Enter some text", controller9);
        layout.addField("Enter a date", controller10);
        layout.addField("Output", textField);
        layout.addField((WLabel) null, ajaxButton);

    }
}
