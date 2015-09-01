package com.github.dibp.wcomponents.examples.repeater.link; 

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WDataRenderer;
import com.github.dibp.wcomponents.WDialog;

/** 
 * TODO Provide description 
 * 
 * @author Adam Millard 
 */
public class RepeaterLinkTab extends WDataRenderer
{
    private final RepeaterLinkComponent repeaterPanel;
    private final DisplayComponent displayDialog = new DisplayComponent();
    private final WDialog dialog = new WDialog(displayDialog);
    
    /**
     * Creates a RepeaterLinkTab.
     */
    public RepeaterLinkTab()
    {
        repeaterPanel = new RepeaterLinkComponent();
        repeaterPanel.setNameAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                showDetails(event);
            }
        });
        
        add(repeaterPanel);
        add(dialog);
        
        dialog.setTitle("Details");
    }

    /** 
     * Sets the data that this component displays/edits.
     * 
     * @param data the data to set. 
     */
    @Override
    public void setData(final Object data)
    {
        repeaterPanel.setData(data);
    }
    
    /** {@inheritDoc} */ 
    @Override
    public void updateComponent(final Object data)
    {
        repeaterPanel.setData(data);
    }
    
    public void showDetails(final ActionEvent event)
    {
        // Track down the data associated with this event.
        MyData data = (MyData) event.getActionObject();
        
        displayDialog.setData(data);
        dialog.display();
    }
}
