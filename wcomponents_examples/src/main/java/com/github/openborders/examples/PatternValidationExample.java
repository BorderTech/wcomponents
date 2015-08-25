package com.github.openborders.examples;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WHorizontalRule;
import com.github.openborders.WMessages;
import com.github.openborders.WMultiTextField;
import com.github.openborders.WPanel;
import com.github.openborders.WPhoneNumberField;
import com.github.openborders.WTextField;
import com.github.openborders.validation.Diagnostic;
import com.github.openborders.validation.ValidatingAction;

/**
 * Demonstrate how patterns can be used to validate text fields.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class PatternValidationExample extends WContainer
{
    /** Width of labels. */
    private static final int LABEL_WIDTH = 25;

    /**
     * Construct the example.
     */
    public PatternValidationExample()
    {
        final WMessages messages = new WMessages();
        final WTextField textField = new WTextField();
        final WPhoneNumberField phoneNumberField = new WPhoneNumberField();
        final WMultiTextField multiTextField = new WMultiTextField();

        add(messages);

        WPanel applyPanel = new WPanel();
        add(applyPanel);

        WFieldLayout applyLayout = new WFieldLayout();
        applyLayout.setLabelWidth(LABEL_WIDTH);
        applyPanel.add(applyLayout);

        final WTextField pattern = new WTextField()
        {
            @Override
            protected void validateComponent(final java.util.List<Diagnostic> diags)
            {
                super.validateComponent(diags);
                if (!isEmpty())
                {
                    try
                    {
                        Pattern.compile(getText());
                    }
                    catch (PatternSyntaxException e)
                    {
                        diags.add(createErrorDiagnostic("Invalid pattern syntax (" + e.getMessage() + ")"));
                    }
                }
            }
        };
        applyLayout.addField("Pattern for the text fields", pattern);

        WButton apply = new WButton("apply");
        add(apply);
        applyPanel.setDefaultSubmitButton(apply);

        apply.setAction(new ValidatingAction(messages.getValidationErrors(), applyLayout)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                String regex = pattern.getText();

                textField.setPattern(regex);
                textField.getLabel().setHint(regex);

                phoneNumberField.setPattern(regex);
                phoneNumberField.getLabel().setHint(regex);

                multiTextField.setPattern(regex);
                multiTextField.getLabel().setHint(regex);
            }
        });

        add(new WHorizontalRule());

        WPanel fieldsPanel = new WPanel();
        add(fieldsPanel);

        WFieldLayout fields = new WFieldLayout();
        fields.setLabelWidth(LABEL_WIDTH);
        fieldsPanel.add(fields);

        fields.addField("Text Field", textField);
        fields.addField("Phone Number", phoneNumberField);
        fields.addField("Multi Text", multiTextField);

        WButton validate = new WButton("Validate");
        add(validate);
        fieldsPanel.setDefaultSubmitButton(validate);

        validate.setAction(new ValidatingAction(messages.getValidationErrors(), fields)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                messages.success("All the fields are valid.");
            }
        });

    }
}
