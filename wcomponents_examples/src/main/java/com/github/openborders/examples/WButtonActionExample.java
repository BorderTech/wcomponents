package com.github.openborders.examples;

import java.util.Date;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WPanel;
import com.github.openborders.WText;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

/**
 * <p>This is a simple example of actions attached to a {@link WButton} component.
 * </p>
 * <p> The button press causes a trip to the server which returns the current time and date in a {@link WText} field.
 * </p>
 * @author Steve Harney
 * @since 1.0.0
 */
public class WButtonActionExample extends WPanel
{
    /** A text field for displaying a message. */
    private final WText message = new WText();

    /** the button. */
    private final WButton button = new WButton("Click Me");

    /**
     * Creates a WButtonActionExample.
     */
    public WButtonActionExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL,0,6));
        add(message);

        // Add the button to the panel.
        add(button);

        // Set the action on the button to be executed when the button is
        // pressed.
        button.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                message.setText("The \"" + event.getActionCommand() + "\" button was pressed at " + new Date());
            }
        });
    }
}
