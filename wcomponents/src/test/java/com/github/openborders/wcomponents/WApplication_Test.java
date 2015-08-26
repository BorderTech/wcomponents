package com.github.openborders.wcomponents;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Unit tests for {@link WApplication}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WApplication_Test extends AbstractWComponentTestCase
{
    @Test
    public void testUnsavedChangesAccessors() throws Exception
    {
        WApplication application = new WApplication();
        application.setLocked(true);

        UIContext uic1 = createUIContext();
        setActiveContext(uic1);
        
        // Flag should be false by default
        Assert.assertFalse("UnsavedChanges should be false by default", application.hasUnsavedChanges());
        
        // Set flag to true
        application.setUnsavedChanges(true);
        Assert.assertTrue("UnsavedChanges should be true", application.hasUnsavedChanges());
        
        // Set flag to false
        application.setUnsavedChanges( false);
        Assert.assertFalse("UnsavedChanges should be false", application.hasUnsavedChanges());
        
        // Test a second context
        UIContext uic2 = createUIContext();        
        setActiveContext(uic2);
        application.setUnsavedChanges(true);
        Assert.assertTrue("UnsavedChanges should be true for second context", application.hasUnsavedChanges());
        
        setActiveContext(uic1);
        Assert.assertFalse("UnsavedChanges should be false for first context", application.hasUnsavedChanges());
    }
    
    @Test
    public void testTitleAccessors()
    {
        assertAccessorsCorrect(new WApplication(), "title", null, "A", "B");
    }

    @Test
    public void testAppendIdAccessors()
    {
        assertAccessorsCorrect(new WApplication(), "appendID", false, true, false);
    }

    @Test
    public void testIdNameAccessors()
    {
        assertAccessorsCorrect(new WApplication(), "idName", WApplication.DEFAULT_APPLICATION_ID, "XX", "YY");
    }
    
    @Test
    public void testNamingContextDefault()
    {
        WApplication appl = new WApplication();
        Assert.assertEquals("Incorrect defualt naming context", "", appl.getNamingContextId());
        Assert.assertEquals("Incorrect defualt id", WApplication.DEFAULT_APPLICATION_ID, appl.getId());
        Assert.assertEquals("Incorrect defualt id name", WApplication.DEFAULT_APPLICATION_ID, appl.getIdName());
        Assert.assertFalse("Append should defualt to false", appl.isAppendID());
        
        // Put in another context
        WNamingContext context = new WNamingContext("TEST");
        context.add(appl);
        // Append false should be ignored
        Assert.assertEquals("Incorrect defualt naming context", "TEST-A", appl.getNamingContextId());
    }

    @Test
    public void testNamingContextDefaultWithAppend()
    {
        WApplication appl = new WApplication();
        appl.setAppendID(true);
        Assert.assertEquals("Incorrect defualt naming context with append", WApplication.DEFAULT_APPLICATION_ID, appl.getNamingContextId());
        Assert.assertEquals("Incorrect defualt id with append", WApplication.DEFAULT_APPLICATION_ID, appl.getId());
        Assert.assertEquals("Incorrect defualt id name with append", WApplication.DEFAULT_APPLICATION_ID, appl.getIdName());
        Assert.assertTrue("Append should be true", appl.isAppendID());
    }
    
    
}
