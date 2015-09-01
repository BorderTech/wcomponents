package com.github.dibp.wcomponents.examples.validation.repeater; 

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.Margin;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCardManager;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.validation.ValidatingAction;
import com.github.dibp.wcomponents.validation.WValidationErrors;

/** 
 * TODO Provide description 
 * 
 * @author Adam Millard 
 */
public class RepeaterExample extends WContainer
{
    private final WCardManager pages = new WCardManager();

    private final WContainer basicFieldsPanel = new WContainer();
    private final WValidationErrors errors = new WValidationErrors();
    private final RepeaterFields repeaterFields = new RepeaterFields();
    
    private final WContainer successPanel = new WContainer();
    
    /** The component which is displayed when field validation passes. */
    private final SuccessComponent successComponent = new SuccessComponent();
    
    /**
     * Creates a RepeaterExample.
     */
    public RepeaterExample()
    {
        add(pages);
		basicFieldsPanel.add(errors);
		basicFieldsPanel.add(repeaterFields);
        pages.add(basicFieldsPanel);
        pages.add(successPanel);

        Action submitAction = new ValidatingAction(errors, repeaterFields)
        {
        	@Override
            public void executeOnValid(final ActionEvent event)
        	{
        	    next();
        	}
        };
        repeaterFields.setSubmitAction(submitAction);

        successPanel.add(successComponent);
        
        WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
        successPanel.add(buttonPanel);
        buttonPanel.setMargin(new Margin(12, 0, 0, 0));
        WButton backBtn = new WButton("Back"); 
        buttonPanel.add(backBtn);

        Action back = new Action()
        {
        	public void execute(final ActionEvent event)
        	{
        	    back();
        	}
        };
        backBtn.setAction(back);
    }
    
    /**
     * Navigates to the second screen.
     */
    public void next()
    {
        pages.makeVisible(successPanel);
        successComponent.setData(repeaterFields.getData());
    }
    
    /**
     * Navigates to the first screen.
     */
    public void back()
    {
        pages.makeVisible(basicFieldsPanel);
    }
    
    /**
     * Override preparepaint to initialise the data on first acecss by a user.
     * 
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        if (!isInitialised())
        {
            MyDataBean myBean = new MyDataBean();
            myBean.setName("My Bean");
            
            myBean.addBean(new SomeDataBean("blah", "more blah"));
            myBean.addBean(new SomeDataBean());
            
            repeaterFields.setData(myBean);
            
            setInitialised(true);
        }
    }
}
