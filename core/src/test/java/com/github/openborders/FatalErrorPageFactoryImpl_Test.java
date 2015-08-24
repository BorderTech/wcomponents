package com.github.openborders; 

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.FatalErrorPage;
import com.github.openborders.FatalErrorPageFactoryImpl;
import com.github.openborders.WComponent;

/**
 * Unit tests for {@link FatalErrorPageFactoryImpl}.
 * 
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class FatalErrorPageFactoryImpl_Test extends AbstractWComponentTestCase
{
    @Test
    public void testCreateErrorPage()
    {
        FatalErrorPageFactoryImpl factory = new FatalErrorPageFactoryImpl();
        TestSampleException exception = new TestSampleException("sample exception");
        WComponent result = factory.createErrorPage(true, exception);
           
        Assert.assertTrue("result should be instance of FatalErrorPage", result instanceof FatalErrorPage);
    }
}
