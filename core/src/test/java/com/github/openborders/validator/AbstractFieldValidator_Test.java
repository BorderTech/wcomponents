package com.github.openborders.validator; 

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.Input;
import com.github.openborders.WTextField;
import com.github.openborders.validator.AbstractFieldValidator;

/**
 * AbstractFieldValidator_Test - unit tests for {@link AbstractFieldValidator}. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class AbstractFieldValidator_Test
{
    @Test
    public void testInputFieldAccessors()
    {
        MyAbstractFieldValidator validator = new MyAbstractFieldValidator();
        Input input = new WTextField(); 
        
        validator.setInputField(input);
        Assert.assertSame("Incorrect input field returned", input, validator.getInputField());
    }
    
    @Test
    public void testErrorMessageAccessors()
    {
        MyAbstractFieldValidator validator = new MyAbstractFieldValidator();
        String errorMessage = "testErrorMessageAccessors.error message";
        
        validator.setErrorMessage(errorMessage);
        Assert.assertEquals("Incorrect error message returned", errorMessage, validator.getErrorMessage());
    }
    
    /**
     * A trivial AbstractFieldValidator implementation, which is always valid.
     */
    private static final class MyAbstractFieldValidator extends AbstractFieldValidator
    {
        @Override
        public boolean isValid()
        {
            return true;
        }
    }
}
