package com.github.openborders.wcomponents.velocity;

import org.junit.Assert;
import org.junit.Test;


/**
 * VelocityTemplateManager_Test - JUnit tests for {@link VelocityTemplateManager}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class VelocityTemplateManager_Test
{
    @Test
    public void testToTemplateResourceName()
    {
        // An anonymous inner class for testing
        Object myObject = new Object() { };

        Assert.assertEquals("Incorrect URL for top-level class", "com/github/openborders/wcomponents/velocity/VelocityTemplateManager_Test.vm", VelocityTemplateManager.toTemplateResourceName(VelocityTemplateManager_Test.class));
        Assert.assertEquals("Incorrect URL for inner class", "com/github/openborders/wcomponents/1elocity/MyClass.vm", VelocityTemplateManager.toTemplateResourceName(MyClass.class));
        Assert.assertEquals("Incorrect URL for anonymous class", "com/github/openborders/wcomponents/1elocity/1.vm", VelocityTemplateManager.toTemplateResourceName(myObject.getClass()));
    }

    /** An inner class for testing. */
    public static final class MyClass
    {
    }
}
