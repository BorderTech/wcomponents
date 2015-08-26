package com.github.openborders.wcomponents.examples.validation.repeater; 

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.Margin;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WDataRenderer;
import com.github.openborders.wcomponents.WField;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WRepeater;
import com.github.openborders.wcomponents.WTextField;
import com.github.openborders.wcomponents.layout.FlowLayout;

/** 
 * TODO Provide description 
 * 
 * @author Adam Millard 
 */
public class RepeaterFields extends WDataRenderer
{
    private final WTextField nameText;
    private final WRepeater repeater;
    private final WButton submitBtn;
    
    /**
     * Creates a RepeaterFields.
     */
    public RepeaterFields()
    {
        WFieldLayout fields = new WFieldLayout();
        fields.setMargin(new Margin(0, 0, 12, 0));
        add(fields);

        nameText = new WTextField();
        WField nameField = fields.addField("Name", nameText);
        nameText.setMandatory(true);
        nameField.getLabel().setHint("required");
        
        repeater = new WRepeater();
        repeater.setRepeatedComponent(new RepeaterComponent());
        add(repeater);
        WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
        buttonPanel.setMargin(new Margin(12, 0, 0, 0));
        add(buttonPanel);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        submitBtn = new WButton("Submit", 'S');
        buttonPanel.add(submitBtn);
    }
    
    
    
    
    public void setSubmitAction(final Action action)
    {
        submitBtn.setAction(action);
    }

    @Override
    public void updateComponent(final Object data)
    {
        MyDataBean myBean = (MyDataBean) data;
        nameText.setText(myBean.getName());
        repeater.setData(myBean.getMyBeans());
    }

    @Override
    public void updateData(final Object data)
    {
        MyDataBean myBean = (MyDataBean) data;
        myBean.setName(nameText.getText());
        myBean.setMyBeans(repeater.getBeanList());
    }
}
