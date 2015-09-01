package com.github.dibp.wcomponents.examples;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.MessageContainer;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCancelButton;
import com.github.dibp.wcomponents.WCheckBox;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WHorizontalRule;
import com.github.dibp.wcomponents.WMessages;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;

/**
 * <p>
 * This is a simple example to demonstrate the features of the
 * {@link WCancelButton}.
 * </p>
 * 
 * <p>
 * The {@link Action} of the "Apply to Server" {@link WButton} uses the state of
 * the "Unsaved Changes" {@link WCheckBox check box} to set/unset the
 * {@link WCancelButton#setUnsavedChanges(boolean) unsaved changes
 * flag} of the cancel button.
 * </p>
 * 
 * <p>
 * The "Cancel" button will display a confirmation prompt if either the check
 * box has been set and not submitted or the server side unsaved changes flag
 * has been set.
 * </p>
 * 
 * @author Steve Harney
 * @since 1.0.0
 */
public class SimpleCancelButtonExample extends WPanel implements MessageContainer
{

    /** message indicating that the state has been saved on the server. */
    private static final String UNSAVED_CHANGES = "Unsaved state on server";
    
    /** Used to display messages to the user. */
    private final WMessages messages = new WMessages();

    /** check box used to indicate that the save has been set to the server. */
    private final WCheckBox unsavedChanges = new WCheckBox();

    /** The cancel button. */
    private final WCancelButton cancelButton = new WCancelButton("Cancel");


    
    /**
     * Creates a WCancelButtonExample.
     */
    public SimpleCancelButtonExample()
    {
        setTitleText("WCancelButton Example");
        
        // Build UI
        setLayout(new FlowLayout(Alignment.VERTICAL));
        add(messages);
        WFieldLayout fieldLayout = new WFieldLayout();
        fieldLayout.addField("Unsaved Changes", unsavedChanges);

        WButton applyButton = new WButton("Apply to server");
        applyButton.setAction(new Action()
        {

            public void execute(final ActionEvent event)
            {
                cancelButton.setUnsavedChanges(unsavedChanges.isSelected());
                
                if (cancelButton.isUnsavedChanges())
                {
                    messages.info(UNSAVED_CHANGES);
                }
            }
        });

        add(fieldLayout);
        add(new WHorizontalRule());
        WPanel buttonPanel = new WPanel();
        buttonPanel.setLayout(new FlowLayout(Alignment.LEFT));
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel);

        cancelButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                reset();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public WMessages getMessages()
    {
        return messages;
    }


}
