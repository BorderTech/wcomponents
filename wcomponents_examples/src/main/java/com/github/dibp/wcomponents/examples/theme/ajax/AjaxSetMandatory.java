package com.github.dibp.wcomponents.examples.theme.ajax;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WAjaxControl;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WFieldSet;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WMultiSelect;
import com.github.dibp.wcomponents.WRadioButtonSelect;
import com.github.dibp.wcomponents.validation.ValidatingAction;
import com.github.dibp.wcomponents.validation.WValidationErrors;

/**
 * <p>This example is to test setting the mandatory on a field layout entry.</p>
 * <p>This should result in the mandatory marker appear next to the WMultiSelect</p>
 * 
 * @author Steve Harney
 */
public class AjaxSetMandatory extends WContainer
{

    /** online medium. */
    private static final String ONLINE = "Online";
    /** mail medium. */
    private static final String MAIL = "Mail";

    /** Label text for the online types drop down. */
    private static final String ONLINE_LABEL = "Contact channel";
    /** Options for the contact channel drop down. */
    private static final String[] ONLINE_TYPES = { "Web site form", "eMail", "Facebook", "Twitter" };

    /** Label text for the mail types drop down. */
    private static final String MAIL_LABEL = "Mail type";

    /** Options for the mail type drop down. */
    private static final String[] MAIL_TYPES = { "Form", "Letter", "Postcard" };

    /**
     * constructor.
     */
    public AjaxSetMandatory()
    {
        
        final WValidationErrors errors = new WValidationErrors();
        add(errors);
        
        WFieldSet fieldSet = new WFieldSet("Contact Method");
        WFieldLayout layout = new WFieldLayout();
        fieldSet.add(layout);
        add(fieldSet);

        // the radio button select setup.
        final WRadioButtonSelect rbSelect = new WRadioButtonSelect(new String[] { ONLINE, MAIL });
        rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
        rbSelect.setSelected(ONLINE);
        layout.addField("Medium", rbSelect);

        // medium type dropdown. 
        final WMultiSelect mediumSelection = new WMultiSelect(ONLINE_TYPES);

        final WLabel mediumSelectionLabel = new WLabel(ONLINE_LABEL, mediumSelection);
        layout.addField(mediumSelectionLabel, mediumSelection);
        
        
        // configure ajax triggers and targets.   
        WAjaxControl control = new WAjaxControl(rbSelect, mediumSelection);
        control.addTarget(mediumSelectionLabel);
        add(control);
        
        // configure the action. 
        rbSelect.setActionOnChange(new Action()
        {
            public void execute(final ActionEvent event)
            {
                if (ONLINE.equals(rbSelect.getSelected()))
                {
                    mediumSelectionLabel.setText(ONLINE_LABEL);
                    mediumSelection.setOptions(ONLINE_TYPES);
                    mediumSelection.setMandatory(false);
                }
                else
                {
                    mediumSelectionLabel.setText(MAIL_LABEL);
                    mediumSelection.setOptions(MAIL_TYPES);
                    mediumSelection.setMandatory(true);
                }
                mediumSelection.resetData();
            }

        });
        
        rbSelect.setSubmitOnChange(false);
        
        // create a submit button to actually confirm that the field 
        // is mandatory not just the marker. 
        WButton submit = new WButton("Submit");
        submit.setAction(new ValidatingAction(errors, fieldSet)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                //nop - we are valid this is reflected in the error box not appearing. 
            }
        });
        
        fieldSet.add(submit);

    }
}
