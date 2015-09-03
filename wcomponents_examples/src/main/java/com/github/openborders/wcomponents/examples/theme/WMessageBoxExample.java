package com.github.openborders.wcomponents.examples.theme;

import java.util.ArrayList;
import java.util.List;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WDropdown;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WFieldSet;
import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.WMessageBox;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WRadioButtonSelect;
import com.github.openborders.wcomponents.WTextArea;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;
import com.github.openborders.wcomponents.util.Util;

/**
 * This component shows the different usages of the {@link WMessageBox} component.
 * 
 * @author Ming Gao
 * @author Adam Millard
 * @author Yiannis Paschalidis
 */
public class WMessageBoxExample extends WPanel
{
    /** The message box to demonstrate. */
    private final WMessageBox messageBox = new WMessageBox(WMessageBox.SUCCESS);

    /** The text field used to enter messages. */
    private final WTextArea txtAdd = new WTextArea();

    /** The list of messages which are being displayed. */
    private final List<String> messageList = new ArrayList<String>();

    /** The dropdown used to select which message to remove. */
    private final WDropdown selRemove = new WDropdown();

    /** The button to remove a single message. */
    private final WButton btnRemove = new WButton("Remove selected message");

    /** The dropdown used to remove all messages. */
    private final WButton btnRemoveAll = new WButton("Remove all messages");

    /** the radio button select to control the message type. */
    private final WRadioButtonSelect messageBoxTypeSelect;

    /** check box group to make the message box visible/invisible. */
    private final WCheckBox cbVisible = new WCheckBox(true);

    /**
     * Creates a WMessageBoxExample.
     */
    public WMessageBoxExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL, 0, 10));

        add(messageBox);
        add(new WHorizontalRule());

        WFieldSet fieldSet = new WFieldSet("Configuration");
        WFieldLayout fieldLayout = new WFieldLayout();
        fieldSet.add(fieldLayout);

        messageBoxTypeSelect = new WRadioButtonSelect(WMessageBox.Type.values());
        messageBoxTypeSelect.setSelected(WMessageBox.Type.SUCCESS);
        messageBoxTypeSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);

        fieldLayout.addField("Message Box Type", messageBoxTypeSelect);
        fieldLayout.addField("Visible", cbVisible);

        // Apply Button
        WButton apply = new WButton("Apply");
        apply.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                applySettings();
            }
        });

        fieldSet.add(apply);
        add(fieldSet);

        // add message panel.
        WPanel addPanel = new WPanel();
        addPanel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));
        addPanel.add(txtAdd);
        WButton btnAddMessage = new WButton("Add new Message");
        addPanel.add(btnAddMessage, "btnAddMessage");
        fieldLayout.addField("Add new message", addPanel);
        btnAddMessage.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                String txt = txtAdd.getText();
                if (!Util.empty(txt))
                {
                    messageBox.addMessage(txt.trim());
                    applySettings();
                }
            }
        });

        WPanel removePanel = new WPanel();
        removePanel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));
        removePanel.add(selRemove);
        removePanel.add(btnRemove);
        removePanel.add(btnRemoveAll);
        fieldLayout.addField("Remove message", removePanel);

        btnRemove.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                int sel = messageList.indexOf(selRemove.getSelected());
                messageBox.removeMessages(sel);
                applySettings();
            }
        });

        btnRemoveAll.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                messageBox.clearMessages();
                applySettings();
            }
        });
    }

    /**
     * applySettings is used to apply the setting to the various controls on the page.  
     */
    public void applySettings()
    {
        messageList.clear();

        for (int i = 1; messageBox.getMessages().size() >= i; i++)
        {
            messageList.add(String.valueOf(i));
        }

        selRemove.setOptions(messageList);
        selRemove.resetData();
        btnRemove.setDisabled(messageList.isEmpty());
        btnRemoveAll.setDisabled(messageList.isEmpty());
        messageBox.setType((com.github.openborders.wcomponents.WMessageBox.Type) messageBoxTypeSelect.getSelected());
        messageBox.setVisible(cbVisible.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        if (!isInitialised())
        {
            messageBox.addMessage("Message Box Example");
            applySettings();
            setInitialised(true);
        }
    }

}
