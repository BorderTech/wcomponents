package com.github.openborders.wcomponents.examples.petstore;

import com.github.openborders.wcomponents.examples.petstore.CartPanel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.openborders.wcomponents.UIContextHolder;
import com.github.openborders.wcomponents.UIContextImpl;
import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WDataTable;
import com.github.openborders.wcomponents.util.mock.MockRequest;

/**
 * Unit tests for {@link CartPanel}.
 * 
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class CartPanel_Test
{
    /**
     * testConstructor - case2. check that the Cart is initially empty.
     */
    @Test
    public void testConstructorCase2()
    {
        CartPanel cartPanel = new CartPanel();

        UIContextHolder.pushContext(new UIContextImpl());
        MockRequest request = new MockRequest();
        cartPanel.handleRequest(request);

        WComponent comp = cartPanel.getChildAt(0);
        Assert.assertTrue("first child is a WDataTable", comp instanceof WDataTable);
        WDataTable table = (WDataTable) comp;

        Assert.assertEquals("list of cartbeans should be empty - nothing in cart", 0, table.getDataModel().getRowCount());
    }
    
    @After
    public void resetContext()
    {
        UIContextHolder.reset();
    }    
}
