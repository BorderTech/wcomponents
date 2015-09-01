package com.github.dibp.wcomponents.validation; 

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.github.dibp.wcomponents.AbstractWComponentTestCase;
import com.github.dibp.wcomponents.UIContext;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.validation.WValidationErrors.GroupedDiagnositcs;

/**
 * WValidationErrors_Test - unit tests for {@link WValidationErrors}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WValidationErrors_Test extends AbstractWComponentTestCase
{
    private List<Diagnostic> errors;
    private WValidationErrors wValidationErrors;
    
    @Before
    public void setUp()
    {
        wValidationErrors = new WValidationErrors();
        
        Diagnostic error = new DiagnosticImpl(Diagnostic.ERROR, new WTextField(), "Error");
        errors = new ArrayList<Diagnostic>();
        errors.add(error);
    }
    
    @Test
    public void testHasErrors()
    {
        wValidationErrors.setLocked(true);
        setActiveContext(createUIContext());
        Assert.assertFalse("Should not have errors by default", wValidationErrors.hasErrors());
        
        wValidationErrors.setErrors(errors);
        
        Assert.assertTrue("Should have errors", wValidationErrors.hasErrors());
        
        wValidationErrors.reset();
        Assert.assertFalse("Should not have errors after reset", wValidationErrors.hasErrors());
    }
    
    @Test
    public void testGetErrors()
    {
        wValidationErrors.setLocked(true);
        setActiveContext(createUIContext());
        
        Assert.assertTrue("Should not have errors by default", wValidationErrors.getErrors().isEmpty());
        
        wValidationErrors.setErrors(errors);
        
        Assert.assertEquals("Should have one error", 1, wValidationErrors.getErrors().size());
        
        wValidationErrors.reset();
        Assert.assertTrue("Should not have errors after reset", wValidationErrors.getErrors().isEmpty());
    }
    
    @Test
    public void testClearErrors()
    {
        wValidationErrors.setLocked(true);
        setActiveContext(createUIContext());
        
        wValidationErrors.setErrors(errors);
        wValidationErrors.clearErrors();
        
        Assert.assertFalse("Should not have errors after clear", wValidationErrors.hasErrors());
    }
    
    @Test
    public void testIsDefaultState()
    {
        wValidationErrors.setLocked(true);
        setActiveContext(createUIContext());
        
        Assert.assertTrue("Should be in default state by default", wValidationErrors.isDefaultState());
        
        wValidationErrors.setErrors(new ArrayList<Diagnostic>());
        
        Assert.assertTrue("Should be in default if there are no errors", wValidationErrors.isDefaultState());

        wValidationErrors.setErrors(errors);
        Assert.assertFalse("Should not be in default if there are errors", wValidationErrors.isDefaultState());
        
        wValidationErrors.reset();        
        Assert.assertTrue("Should be in default after reset", wValidationErrors.isDefaultState());
    }
    
    @Test
    public void testGetGroupedErrors()
    {
        wValidationErrors.setLocked(true);
        UIContext uic = createUIContext();
        setActiveContext(uic);
        
        errors.clear();
        
        List<GroupedDiagnositcs> groupedErrors = wValidationErrors.getGroupedErrors();
        Assert.assertTrue("Should not have any groups by default", groupedErrors.isEmpty());
        
        WComponent component1 = new WTextField();
        WComponent component2 = new WTextField();
        
        Diagnostic component1Error1 = new DiagnosticImpl(Diagnostic.ERROR, component1, "Error 1");
        Diagnostic component2Error1 = new DiagnosticImpl(Diagnostic.ERROR, component2, "Error 2");
        Diagnostic component2Error2 = new DiagnosticImpl(Diagnostic.ERROR, component2, "Error 3");
        
        errors.add(component1Error1);
        errors.add(component2Error1);
        errors.add(component2Error2);
        
        wValidationErrors.setErrors(errors);        
        groupedErrors = wValidationErrors.getGroupedErrors();
        
        Assert.assertEquals("Incorrect number of groups", 2, groupedErrors.size());
        
        GroupedDiagnositcs group1 = groupedErrors.get(0);
        GroupedDiagnositcs group2 = groupedErrors.get(1);
        
        Assert.assertEquals("Incorrect number of errors for group 1", 1, group1.getDiagnostics().size());
        Assert.assertSame("Incorrect diagnostic in group 1", component1Error1, group1.getDiagnostics().get(0));
        
        Assert.assertEquals("Incorrect number of errors for group 2", 2, group2.getDiagnostics().size());
        Assert.assertSame("Incorrect diagnostic in group 2", component2Error1, group2.getDiagnostics().get(0));
        Assert.assertSame("Incorrect diagnostic in group 2", component2Error2, group2.getDiagnostics().get(1));
    }
}
