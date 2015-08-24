package com.github.openborders;

import java.io.Serializable;

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.Action;
import com.github.openborders.WMenu;
import com.github.openborders.WMenuItem;
import com.github.openborders.util.mock.MockRequest;

/**
 * WMenuItem_Test - Unit tests for {@link WMenuItem}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItem_Test extends AbstractWComponentTestCase
{
    @Test
    public void testActionAccessors()
    {
        Action initAction = new TestAction();
        Action defaultAction = new TestAction();
        Action userAction = new TestAction();

        WMenuItem menuItem = new WMenuItem("");
        assertAccessorsCorrect(menuItem, "action", null, defaultAction, userAction);
        
        menuItem = new WMenuItem("", initAction);
        assertAccessorsCorrect(menuItem, "action", initAction, defaultAction, userAction);
        
        menuItem = new WMenuItem("", "http://localhost/");
        Assert.assertNotNull("URL should be set", menuItem.getUrl());
        Assert.assertNull("Action should be null", menuItem.getAction());
        Assert.assertFalse("Submit flag should be false", menuItem.isSubmit());        
        
        menuItem.setAction(new TestAction());
        Assert.assertNull("Url should be null", menuItem.getUrl());
        Assert.assertNotNull("Action should not be null", menuItem.getAction());
        Assert.assertTrue("Submit flag should be true", menuItem.isSubmit());
    }
    
    @Test
    public void testSetSelectable()
    {
        WMenuItem item = new WMenuItem("");
        Assert.assertNull("Selectable should be null by default", item.isSelectable());
        
        item.setSelectable(Boolean.FALSE);        
        item.setLocked(true);
        setActiveContext(createUIContext());        
        item.setSelectable(Boolean.TRUE);
        
        Assert.assertTrue("Should be selectable in session", item.isSelectable());
        
        resetContext();
        Assert.assertFalse("Default should not be selectable", item.isSelectable());
    }
    
    @Test
    public void testDisabled()
    {
        WMenuItem menuItem = new WMenuItem("");
        assertAccessorsCorrect(menuItem, "disabled", false, true, false);
    }
    
    @Test
    public void testTextAccessors()
    {
        String initText = "WSubMenu_Test.testTextAccessors.initText";
        String defaultText = "WSubMenu_Test.testTextAccessors.defaultText";
        String userText = "WSubMenu_Test.testTextAccessors.userText";

        WMenuItem menuItem = new WMenuItem(initText);
        assertAccessorsCorrect(menuItem, "text", initText, defaultText, userText);
        
        //Test nulls
        menuItem.setText("");
        Assert.assertEquals("text should be empty string", "", menuItem.getText());
        menuItem.setText(null);
        Assert.assertNull("text should be null", menuItem.getText());
    }
    
    @Test
    public void testTargetWindowAccessors()
    {
        String defaultWindow = "WSubMenu_Test.testTargetWindowAccessors.defaultWindow";
        String userWindow = "WSubMenu_Test.testTargetWindowAccessors.userWindow";

        WMenuItem menuItem = new WMenuItem("");
        assertAccessorsCorrect(menuItem, "targetWindow", null, defaultWindow, userWindow);
    }
    
    @Test
    public void testUrlAccessors()
    {
        String defaultUrl = "http://localhost/WSubMenu_Test.testUrlAccessors.defaultUrl";
        String userUrl = "http://localhost/WSubMenu_Test.testUrlAccessors.userUrl";

        WMenuItem menuItem = new WMenuItem("");
        assertAccessorsCorrect(menuItem, "url", null, defaultUrl, userUrl);
        
        menuItem = new WMenuItem("", new TestAction());
        Assert.assertNull("Url should be null", menuItem.getUrl());
        Assert.assertNotNull("Action should not be null", menuItem.getAction());
        Assert.assertTrue("Submit flag should be true", menuItem.isSubmit());
        
        menuItem.setUrl(defaultUrl);
        Assert.assertEquals("Incorrect URL", defaultUrl, menuItem.getUrl());
        Assert.assertNull("Action should be null", menuItem.getAction());
        Assert.assertFalse("Submit flag should be false", menuItem.isSubmit());
    }
    
    @Test
    public void testActionCommandAccessors()
    {
        String defaultValue = "WSubMenu_Test.testActionCommandAccessors.defaultValue";
        String userValue = "WSubMenu_Test.testActionCommandAccessors.userValue";

        WMenuItem item = new WMenuItem("");
        assertAccessorsCorrect(item, "actionCommand", null, defaultValue, userValue);
    }

    @Test
    public void testActionObjectAccessors()
    {
        Serializable defaultValue = "WSubMenu_Test.testActionObjectAccessors.defaultValue";
        Serializable userValue = "WSubMenu_Test.testActionObjectAccessors.userValue";

        WMenuItem item = new WMenuItem("");
        assertAccessorsCorrect(item, "actionObject", null, defaultValue, userValue);
    }

    @Test
    public void testAccessKeyAccessors()
    {
        Character initValue = new Character('\0');
        Character defaultValue = 'A';
        Character userValue = 'B';

        WMenuItem item = new WMenuItem("");
        assertAccessorsCorrect(item, "accessKey", initValue, defaultValue, userValue);
    }
    
    @Test
    public void testGetAccessKeyAsString()
    {
        WMenuItem item = new WMenuItem("");
        Assert.assertEquals("Incorrect acesskey as string", null, item.getAccessKeyAsString());
        
        item.setAccessKey('C');
        Assert.assertEquals("Incorrect acesskey as string", "C", item.getAccessKeyAsString());
        
        item.setAccessKey('\0');
        Assert.assertEquals("Incorrect acesskey as string", null, item.getAccessKeyAsString());
    }
    
    @Test
    public void testHandleRequest()
    {
        TestAction action = new TestAction();
        WMenu menu = new WMenu();
        WMenuItem item = new WMenuItem("", action);
        menu.add(item);
        
        menu.setLocked(true);
        
        // Menu not in request
        setActiveContext(createUIContext());
        MockRequest request = new MockRequest();
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when item was not selected", action.wasTriggered());

        // Menu in request, but item not selected
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when item was not selected", action.wasTriggered());

        // Menu in request and item selected
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        request.setParameter(item.getId(), "x");
        menu.serviceRequest(request);
        Assert.assertTrue("Action should have been called when item is selected", action.wasTriggered());
    }
    
    @Test
    public void testHandleRequestWhenDisabled()
    {
        TestAction action = new TestAction();
        WMenuItem item = new WMenuItem("", action);

        item.setLocked(true);
        setActiveContext(createUIContext());
        item.setDisabled(true);
        MockRequest request = new MockRequest();
                
        item.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when item was not selected", action.wasTriggered());

        request.setParameter(item.getId(), "x");
        item.serviceRequest(request);
        Assert.assertFalse("Action should not have been called on a disabled item", action.wasTriggered());
    }
}
