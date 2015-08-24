package com.github.openborders;

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.AbstractNamingContextContainer;
import com.github.openborders.DefaultWComponent;
import com.github.openborders.NamingContextable;
import com.github.openborders.WBeanContainer;
import com.github.openborders.WComponent;

/**
 * Unit tests for {@link AbstractNamingContextContainer}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WBeanContainer_Test extends AbstractWComponentTestCase
{
    @Test
    public void testChildAccessors()
    {
        WBeanContainer container = new WBeanContainer();

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

    @Test
    public void testNamingContextAccessors()
    {
        assertAccessorsCorrect(new WBeanContainer(), "namingContext", false, true, false);
    }

    @Test
    public void testNamingContextIdAccessor()
    {
        String id = "test";
        NamingContextable naming = new WBeanContainer();
        naming.setIdName(id);
        Assert.assertEquals("Incorrect component id", id, naming.getId());
        Assert.assertEquals("Naming context should match component id", id, naming.getNamingContextId());
    }

}
