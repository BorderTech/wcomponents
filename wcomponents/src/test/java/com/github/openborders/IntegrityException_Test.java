package com.github.openborders; 

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.IntegrityException;

/**
* IntegrityException_Test - unit tests for {@link IntegrityException}.
* 
* @author Anthony O'Connor
* @since 1.0.0
*/
public class IntegrityException_Test
{
    /** test message. */
    private static final String TEST_MESSAGE = "test message for IntegrityException";
    
    @Test
    public void testConstructor()
    {
        IntegrityException exception = new IntegrityException(TEST_MESSAGE);
    
        Assert.assertEquals("message should be message set in constructor", TEST_MESSAGE, exception.getMessage());
    }
}
