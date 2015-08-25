package com.github.openborders.examples.theme;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.github.openborders.ActionEvent;
import com.github.openborders.Message;
import com.github.openborders.WButton;
import com.github.openborders.WDateField;
import com.github.openborders.WField;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WFieldSet;
import com.github.openborders.WHeading;
import com.github.openborders.WLabel;
import com.github.openborders.WMessages;
import com.github.openborders.WPanel;
import com.github.openborders.WTextField;
import com.github.openborders.util.Util;
import com.github.openborders.validation.ValidatingAction;
import com.github.openborders.validator.AbstractFieldValidator;
import com.github.openborders.validator.RegExFieldValidator;

/**
 * This class demonstrates how nested {@link WField}s work with validation messages and labels.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldNestedExample extends WPanel
{
    /** Display validation messages. */
    private final WMessages messages = new WMessages();

    /**
     * Construct the example.
     */
    public WFieldNestedExample()
    {
        InnerFieldLayout innerLayout = new InnerFieldLayout();

        WFieldSet innerFieldSet = new WFieldSet("Enter date range");
        innerFieldSet.setFrameType(WFieldSet.FrameType.NONE);
        innerFieldSet.add(innerLayout);
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(20);
        WField field = layout.addField("Enter date range", innerFieldSet);
        WLabel layoutLabel = field.getLabel();
        
        innerLayout.getDateStart().addValidator(new InnerFieldLayoutValidator("{0} must be before {1} for {2}", layoutLabel, innerLayout));

        WButton button = new WButton("Validate");
        layout.addField((WLabel) null, button);
        button.setAction(new ValidatingAction(messages.getValidationErrors(), this)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                messages.addMessage(new Message(Message.SUCCESS_MESSAGE, "Valid."));
            }
        });

        add(messages);
        add(new WHeading(WHeading.MAJOR, "Nested WField Example With Validation"));
        add(layout);

    }

    /**
     * Panel with two WFields that will be nested in another WField.
     */
    private static class InnerFieldLayout extends WFieldLayout
    {
        /** Start text field. */
        private final WDateField dateStart = new WDateField();
        /** Finish text field. */
        private final WDateField dateFinish = new WDateField();
        /** Label for start text field. */
        private final WLabel labelStart;
        /** Label for finish text field. */
        private final WLabel labelFinish;

        /**
         * Construct the innerLayout.
         */
        public InnerFieldLayout()
        {
            setLabelWidth(25);
            dateStart.setMandatory(true);
            dateFinish.setMandatory(true);
            WField start = addField("Start", dateStart);
            labelStart = start.getLabel();
            WField finish = addField("Finish", dateFinish);
            labelFinish = finish.getLabel();
        }

        /**
         * @return the start text field.
         */
        public WDateField getDateStart()
        {
            return dateStart;
        }

        /**
         * @return the finish text field.
         */
        public WDateField getDateFinish()
        {
            return dateFinish;
        }

        /**
         * @return the label for the start text field.
         */
        public WLabel getLabelStart()
        {
            return labelStart;
        }

        /**
         * @return the label for the finish text field.
         */
        public WLabel getLabelFinish()
        {
            return labelFinish;
        }

    }

    /**
     * Validator that demonstrates how validation messages can be written for nested WFields.
     */
    private static class InnerFieldLayoutValidator extends AbstractFieldValidator
    {
        /** The label for the innerLayout in the top level WField. */
        private final WLabel layoutLabel;
        /** The innerLayout being validated. */
        private final InnerFieldLayout innerLayout;

        /**
         * Construct the validator.
         * 
         * @param msg the validation error message
         * @param layoutLabel the label for the innerLayout
         * @param innerLayout the innerLayout being validated
         */
        public InnerFieldLayoutValidator(final String msg, final WLabel layoutLabel, final InnerFieldLayout innerLayout)
        {
            super(msg);
            this.layoutLabel = layoutLabel;
            this.innerLayout = innerLayout;
        }

        /**
         * Validate the text fields on the innerLayout.
         * 
         * @return true if the innerLayout is valid
         */
        @Override
        protected boolean isValid()
        {
            // Assume Mandatory Validator Catches Empty Fields
            if (Util.empty(innerLayout.getDateStart().getText()) || Util.empty(innerLayout.getDateFinish().getText()))
            {
                return true;
            }

            Date start = innerLayout.getDateStart().getDate();
            Date finish = innerLayout.getDateFinish().getDate();

            // Check start is before finish
            if (start.compareTo(finish) > 0)
            {
                return false;
            }
            return true;
        }

        /**
         * Setup the arguments that can be used in the error message.
         * 
         * @return the list of arguments
         */
        @Override
        protected List<Serializable> getMessageArguments()
        {
            List<Serializable> arg = super.getMessageArguments();
            arg.add(innerLayout.getLabelFinish().getText());
            arg.add(layoutLabel.getText());
            return arg;
        }
    }

}
