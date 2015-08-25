package com.github.openborders.examples;

import com.github.openborders.ActionEvent;
import com.github.openborders.Request;
import com.github.openborders.WButton;
import com.github.openborders.WContainer;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WHeading;
import com.github.openborders.WMessageBox;
import com.github.openborders.WTextArea;
import com.github.openborders.validation.ValidatingAction;
import com.github.openborders.validation.WValidationErrors;

/**
 * Class to demonstrate components that can render whitespace.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WhiteSpaceExample extends WContainer
{
    /** Example message with whitespace. */
    private final static String EXAMPLE_MESSAGE = "Message with a \n linebreak";
    /** Example error message with whitespace. */
    private final static String EXAMPLE_ERROR_MESSAGE = "Error message with a \n linebreak";
    /** Example text with whitespace. */
    private final static String EXAMPLE_TEXT = "Text with a \n linebreak";
    /** TextArea to hold text with whitespace. */
    private final WTextArea textArea = new WTextArea();
    /** Validation error container to display errors with whitespace. */
    private final WValidationErrors errors = new WValidationErrors();

    /**
     * Construct the whitespace example.
     */
    public WhiteSpaceExample()
    {

        add(new WHeading(WHeading.SECTION, "Messages with Whitespace"));
        add(new WMessageBox(WMessageBox.INFO, EXAMPLE_MESSAGE));
        add(new WMessageBox(WMessageBox.SUCCESS, EXAMPLE_MESSAGE));
        add(new WMessageBox(WMessageBox.WARN, EXAMPLE_MESSAGE));
        add(new WMessageBox(WMessageBox.ERROR, EXAMPLE_MESSAGE));

        add(new WHeading(WHeading.SECTION, "Error Message with Whitespace"));
        add(errors);

        add(new WHeading(WHeading.SECTION, "Text Areas with Whitespace"));
        WFieldLayout layout = new WFieldLayout();
        layout.addField("Text area", textArea);
        WTextArea textArea2 = new WTextArea();
        textArea2.setMandatory(true, EXAMPLE_ERROR_MESSAGE);
        layout.addField("Text area to create validation error", textArea2);
        add(layout);

        WButton button = new WButton("Validate");
        button.setAction(new ValidatingAction(errors, this)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                // No Action
            }
        });
        add(button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        if (!isInitialised())
        {
            textArea.setText(EXAMPLE_TEXT);
            setInitialised(true);
        }
    }
}
