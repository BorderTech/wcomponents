package com.github.openborders.wcomponents.examples.validation; 

import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WTabSet;

import com.github.openborders.wcomponents.examples.validation.basic.BasicFieldLayoutValidationExample;
import com.github.openborders.wcomponents.examples.validation.basic.BasicFieldsValidationExample;
import com.github.openborders.wcomponents.examples.validation.fields.FieldValidation;
import com.github.openborders.wcomponents.examples.validation.repeater.RepeaterExample;

/** 
 * This test component is used to provide the different ways of performing validation
 * with WComponents.
 * 
 * @author Adam Millard 
 */
public class ValidationExamples extends WContainer
{
    /** Creates a ValidationExamples. */
    public ValidationExamples()
    {   
        WTabSet tabs = new WTabSet();
        tabs.addTab(new BasicFieldsValidationExample(), "Basic", WTabSet.TAB_MODE_CLIENT);
        tabs.addTab(new BasicFieldLayoutValidationExample(), "Basic - using WFieldLayout", WTabSet.TAB_MODE_LAZY);
        tabs.addTab(new RepeaterExample(), "Repeater", WTabSet.TAB_MODE_LAZY);
        tabs.addTab(new FieldValidation(), "All Fields", WTabSet.TAB_MODE_LAZY);
        
        add(tabs);
    }
}
