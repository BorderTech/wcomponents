package com.github.openborders.wcomponents.examples.validation.basic; 

import java.util.List;

import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.validation.Diagnostic;
import com.github.openborders.wcomponents.validation.ValidatingAction;

import com.github.openborders.wcomponents.examples.validation.ValidationContainer;

/** 
 * Same as BasicFieldsValidationExample except that the ValidatingAction has
 * been extended to add an extra validation.
 * 
 * @author Martin Shevchenko
 * @since 18/3/2008 
 */
public class BasicFieldsValidationExample2 extends ValidationContainer
{
    /**
     * Creates a BasicFieldsValidationExample2.
     */
    
    private static final BasicFields fields = new BasicFields();
    
    public BasicFieldsValidationExample2()
    {
        super(fields);
        fields.getField2().getLabel().setHint(fields.getField2().getLabel().getHint() + " and do not enter 'qwerty'");
       
        
        // Get access to the BasicFields object created above.
        final BasicFields basicFields = (BasicFields) getComponentToValidate();
        
        // Customise the validating action to include one additional test for Field 2.
        setValidatingAction(new ValidatingAction(getErrorsBox(), basicFields) 
        {
            @Override
            public void validate(final List<Diagnostic> diags)
            {
                super.validate( diags);

                String text2 = basicFields.getField2().getText();
                
                if ("qwerty".equals(text2))
                {
                    // Note that this error will hyperlink to Field 2.
                    diags.add(createErrorDiagnostic( basicFields.getField2(), "Field 2 must not contain the text \"qwerty\"."));
                }
            }
            
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                showSuccessDialog();
            }
        });
        
        // Make the user aware of the additional validation.
        WText overview = basicFields.getOverview();
        overview.setText(overview.getText()
	        + "\n<ul>"
	        + " <li>Field 2 must not contain the text \"qwerty\".</li>"
	        + "</ul>");
    }
}
