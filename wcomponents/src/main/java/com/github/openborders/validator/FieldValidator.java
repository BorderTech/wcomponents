package com.github.openborders.validator; 

import java.util.List;

import com.github.openborders.Input;
import com.github.openborders.validation.Diagnostic;

/** 
 * Validator used for validation of an individual field.
 * 
 * @author Adam Millard
 * @since 1.0.0
 */
public interface FieldValidator
{
    /** 
     * Sets the field to validate. 
     * @param input the field to validate.
     */
    void setInputField(Input input);

    /**
     * @return the field to validate.
     */
    Input getInputField();
    
    /**
     * Sets the error message to display on validation failure.
     * @param errorMessage the error message.
     */
    void setErrorMessage(String errorMessage);
    
    /**
     * @return the error message to display on validation failure.
     */
    String getErrorMessage();
    
    /**
     * Validates the input field.
     * 
     * @param diags the list of Diagnostics to add validation errors to.
     * 
     * @return the updated list of validation errors.
     */
    List<Diagnostic> validate(List<Diagnostic> diags);
}
