package com.github.openborders.examples.repeater.link; 

import java.util.Iterator;
import java.util.List;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.WButton;
import com.github.openborders.WFieldSet;
import com.github.openborders.WPanel;
import com.github.openborders.WRepeater;
import com.github.openborders.WTextArea;
import com.github.openborders.layout.FlowLayout;
import com.github.openborders.layout.FlowLayout.Alignment;

/** 
 * TODO Provide description
 * 
 * @author Adam Millard.
 */
public class RepeaterComponent extends WPanel
{
    private final WRepeater repeater = new WRepeater();
    private final WButton selectorBtn = new WButton("Show");
    private final WTextArea selectorText = new WTextArea();
    
    /**
     * Creates a RepeaterComponent.
     */
    public RepeaterComponent()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL));
        
        repeater.setRepeatedComponent(new BasicComponent());

        WFieldSet fieldset = new WFieldSet("Group");
        add(fieldset);
        fieldset.add(repeater);
        
        selectorBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                show();
            }
        });
        add(selectorBtn);
        
        selectorText.setRows(10);
        selectorText.setColumns(50);
        selectorText.setReadOnly(true);
        add(selectorText);
    }

    public void setBeanList(final List beanList)
    {
        repeater.setBeanList(beanList);
    }
    
    public void show()
    {
        StringBuffer out = new StringBuffer();
        
        for (Iterator iter = repeater.getBeanList().iterator(); iter.hasNext();)
        {
            MyData data = (MyData) iter.next();
            out.append(data.getName()).append(" : ").append(data.getCount()).append('\n');
        }
        
        selectorText.setText(out.toString());
    }
}
