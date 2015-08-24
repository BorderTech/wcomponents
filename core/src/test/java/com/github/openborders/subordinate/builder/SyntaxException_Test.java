package com.github.openborders.subordinate.builder; 

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.subordinate.builder.SyntaxException;

/**
 * JUnit tests for the {@link SyntaxException} class.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class SyntaxException_Test
{
    @Test
    public void testConstructor()
    {
        String message = "SyntaxException_Test.testConstructor.message";
        SyntaxException exception = new SyntaxException(message);
        
        Assert.assertEquals("Incorrect message", message, exception.getMessage());
    }    
}
