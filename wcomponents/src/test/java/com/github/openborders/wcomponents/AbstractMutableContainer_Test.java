package com.github.openborders.wcomponents;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Unit tests for {@link AbstractMutableContainer}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AbstractMutableContainer_Test extends AbstractWComponentTestCase
{

    @Test
    public void testChildAccessors()
    {
        AbstractContainer container = new MyContainer();

        // Check no children
        Assert.assertEquals("Should have no child count", 0, container.getChildCount());

        // Add child
        WComponent child = new DefaultWComponent();
        container.add(child);

        // Check child
        Assert.assertEquals("Incorrect child count", 1, container.getChildCount());
        Assert.assertEquals("Incorrect child index", 0, container.getIndexOfChild(child));
        Assert.assertEquals("Incorrect child returned", child, container.getChildAt(0));

        // Remove child
        container.remove(child);

        // Check no children
        Assert.assertEquals("Should have no child count after removing", 0, container.getChildCount());
    }

    /**
     * Test instance of AbstractContainer.
     */
    private static class MyContainer extends AbstractMutableContainer
    {
    };

}
