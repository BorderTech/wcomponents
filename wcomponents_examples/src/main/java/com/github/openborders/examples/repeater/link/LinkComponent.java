package com.github.openborders.examples.repeater.link; 

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WDataRenderer;
import com.github.openborders.WPanel;
import com.github.openborders.WText;
import com.github.openborders.layout.ColumnLayout;

/** 
 * A component which displays a {@link MyData} object's name and count attributes.
 * The name is rendered as a link and can have an action attached to it.
 * 
 * @author Adam Millard 
 */
public class LinkComponent extends WDataRenderer
{
    /** UI Column widths. */
    private static final int[] COLUMN_WIDTHS = {10, 5, 10, 10};
    
    /** Use to display the the {@link MyData} bean's name. */
    private final WButton name = new WButton();
    /** Use to display the the {@link MyData} bean's count. */
    private final WText count = new WText();
    
    /** Use to increment the the {@link MyData} bean's count. */
    private final WButton incBtn = new WButton("Increment");
    /** Use to decrement the the {@link MyData} bean's count. */
    private final WButton decBtn = new WButton("Decrement");

    /**
     * Creates a LinkComponent.
     */
    public LinkComponent()
    {
        WPanel panel = new WPanel();
        panel.setLayout(new ColumnLayout(COLUMN_WIDTHS));
        add(panel);
        
        name.setRenderAsLink(true);
        panel.add(name);
        panel.add(count);
        panel.add(incBtn);
        panel.add(decBtn);
        
        incBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                increment();
            }
        });
        
        
        decBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                decrement();
            }
        });
    }
    
    /**
     * Increments the {@link MyData} bean's count by 1.
     * Called by the action on {@link #incBtn}.
     */
    private void increment()
    {
        MyData data = (MyData) getData();
        data.setCount(data.getCount() + 1);
    }
    
    /**
     * Increments the {@link MyData} bean's count by 1.
     * Called by the action on {@link #decBtn}.
     */
    private void decrement()
    {
        MyData data = (MyData) getData();
        int dataCount = data.getCount();

        if (dataCount > 0)
        {
            dataCount--;
        }
        
        data.setCount(dataCount);
    }
    
    /** {@inheritDoc} */
    @Override
    public void updateComponent(final Object data)
    {
        MyData bean = (MyData) data;
        name.setText(bean.getName());
        name.setActionObject(bean);
        count.setText(String.valueOf(bean.getCount()));
        incBtn.setActionObject(bean);
        decBtn.setActionObject(bean);
    }

    /**
     * Sets the action to invoke when the {@link #name} button is used.
     * @param action the action for the name button.
     */
    public void setNameAction(final Action action)
    {
        name.setAction(action);
    }
}
