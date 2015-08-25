package com.github.openborders;

import java.io.Serializable;

import junit.framework.Assert;

import org.junit.Test;

import com.github.openborders.WMenu;
import com.github.openborders.WMenuItem;
import com.github.openborders.WSubMenu;
import com.github.openborders.WebUtilities;
import com.github.openborders.util.mock.MockRequest;

/**
 * WSubMenu_Test - Unit tests for {@link WSubMenu}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSubMenu_Test extends AbstractWComponentTestCase
{
    /** Test text. */
    private static final String TEST_TEXT = "WSubMenu_Test.testText";
    
    @Test
    public void testAddSubMenu()
    {
        WSubMenu subMenu = new WSubMenu("");        
        WSubMenu subSubMenu = new WSubMenu("submenu");
        subMenu.add(subSubMenu);
        
        Assert.assertSame("Sub-menu should be ancestor of sub-sub-menu", subMenu, WebUtilities.getTop(subSubMenu));
    }
    
    @Test
    public void testAddMenuItem()
    {
        WSubMenu subMenu = new WSubMenu("");        
        WMenuItem menuItem = new WMenuItem("item");
        subMenu.add(menuItem);
        
        Assert.assertSame("Sub-menu should be ancestor of menu item", subMenu, WebUtilities.getTop(menuItem));
    }
    
    @Test
    public void testIsTopLevelMenu()
    {
        WMenu menu = new WMenu();
        WSubMenu subMenu = new WSubMenu("a");
        WSubMenu subSubMenu = new WSubMenu("b");
        
        Assert.assertFalse("Should not be a top-level menu when there is no parent", subMenu.isTopLevelMenu());
        
        menu.add(subMenu);
        subMenu.add(subSubMenu);
        
        Assert.assertTrue("isTopLevel should be true for top-level sub-menu", subMenu.isTopLevelMenu());
        Assert.assertFalse("isTopLevel should be false for second-level sub-menu", subSubMenu.isTopLevelMenu());
    }

    @Test
    public void testSetDisabled()
    {
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertFalse("Should not be disabled by default", subMenu.isDisabled()); 

        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setDisabled(true);

        Assert.assertTrue("Should be disabled in session ", subMenu.isDisabled());
        
        resetContext();
        Assert.assertFalse("Should not be disabled by default", subMenu.isDisabled());
    }
    
    @Test
    public void testSetAccessKey()
    {
        final char accessKey = 'X';

        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertNull("Default access key should be null", subMenu.getAccessKeyAsString());

        subMenu.setAccessKey(accessKey);
        Assert.assertEquals("Incorrect access key returned", accessKey, subMenu.getAccessKey());
    }

    @Test
    public void testGetText() throws Exception
    {
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        String userText = "WSubMenu_Test.testGetText.userText";

        Assert.assertEquals("Incorrect default text", TEST_TEXT, subMenu.getText());
        
        // Set test for a users session
        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setText(userText);
        Assert.assertEquals("Should have session text", userText, subMenu.getText());
        
        resetContext();
        Assert.assertEquals("Should have default text", TEST_TEXT, subMenu.getText());
        
        //Test nulls
        subMenu.setText("");
        Assert.assertEquals("text should be empty string", "", subMenu.getText());
        subMenu.setText(null);
        Assert.assertNull("text should be null", subMenu.getText());
    }
    
    @Test
    public void testSetSelectable()
    {
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertNull("Should not be selectable by default", subMenu.isSelectable());

        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setSelectable(true);

        Assert.assertTrue("in uic1 should be selectable", subMenu.isSelectable());
        
        resetContext();
        Assert.assertNull("Default selectable flag should not have changed", subMenu.isSelectable());
    }
    
    @Test
    public void testSetMultipleSelection()
    {
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertFalse("Should be single select by default", subMenu.isMultipleSelection());

        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setMultipleSelection(true);

        Assert.assertTrue("in uic1 should be muli-selectable", subMenu.isMultipleSelection());
        
        resetContext();
        Assert.assertFalse("Default multi-select flag should not have changed", subMenu.isMultipleSelection());
    }
    
    /**
     * Test setSelectMode.
     */
    @Test
    public void testSetSelectMode()
    {
        WSubMenu subMenu = new WSubMenu("sub");
        Assert.assertEquals("Should default to select mode none", WMenu.SelectMode.NONE, subMenu.getSelectMode());

        subMenu.setSelectMode(WMenu.SelectMode.SINGLE);
        
        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setSelectMode(WMenu.SelectMode.MULTIPLE);

        Assert.assertEquals("in uic1 should be multiple select mode", WMenu.SelectMode.MULTIPLE, subMenu.getSelectMode());
        
        resetContext();
        Assert.assertEquals("Default be single select mode", WMenu.SelectMode.SINGLE, subMenu.getSelectMode());
    }
    
    @Test
    public void testSetActionCommand()
    {
        String sharedValue = "WSubMenu_Test.testSetActionCommand.sharedValue";
        String value = "WSubMenu_Test.testSetActionCommand.value";

        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertNull("Action command should be null by default", subMenu.getActionCommand());

        subMenu.setActionCommand(sharedValue);
        Assert.assertEquals("Incorrect shared action command returned", sharedValue, subMenu.getActionCommand());

        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setActionCommand(value);
        Assert.assertEquals("Uic 1 action command should be returned for uic 1", value, subMenu.getActionCommand());

        resetContext();
        Assert.assertEquals("Incorrect shared action command returned", sharedValue, subMenu.getActionCommand());
    }

    @Test
    public void testSetActionObject()
    {
        Serializable sharedValue = "WSubMenu_Test.testSetActionCommand.sharedValue";
        Serializable value = "WSubMenu_Test.testSetActionCommand.value";

        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        Assert.assertNull("Action object should be null by default", subMenu.getActionObject());

        subMenu.setActionObject(sharedValue);
        Assert.assertEquals("Incorrect shared action object returned", sharedValue, subMenu.getActionObject());

        subMenu.setLocked(true);
        setActiveContext(createUIContext());
        subMenu.setActionObject(value);
        Assert.assertEquals("Uic 1 action object should be returned for uic 1", value, subMenu.getActionObject());

        resetContext();
        Assert.assertEquals("Incorrect shared action object returned", sharedValue, subMenu.getActionObject());
    }
 
    @Test
    public void testHandleRequest()
    {
        TestAction action = new TestAction();
        WMenu menu = new WMenu();
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        subMenu.setAction(action);
        menu.add(subMenu);
        
        setActiveContext(createUIContext());

        // Menu not in Request
        MockRequest request = new MockRequest();
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when sub-menu was not selected", action.wasTriggered());

        // Menu in Request but submenu not selected
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when sub-menu was not selected", action.wasTriggered());
        
        // Menu in Request and submenu selected
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        request.setParameter(subMenu.getId(), "x");
        menu.serviceRequest(request);
        Assert.assertTrue("Action should have been called when sub-menu is selected", action.wasTriggered());
    }
    
    @Test
    public void testHandleRequestSubMenuOpen()
    {
        WMenu menu = new WMenu();
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        menu.add(subMenu);
        
        setActiveContext(createUIContext());

        // Menu not in Request
        MockRequest request = new MockRequest();
        menu.serviceRequest(request);
        Assert.assertFalse("Submenu should not be open", subMenu.isOpen());

        // Menu in Request but submenu not open
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        menu.serviceRequest(request);
        Assert.assertFalse("Submenu should not be open", subMenu.isOpen());
        
        // Menu in Request and submenu open
        request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
        request.setParameter(subMenu.getId(), "x");
        request.setParameter(subMenu.getId() + ".open", "true");
        menu.serviceRequest(request);
        Assert.assertTrue("Submenu should be open", subMenu.isOpen());
    }
    
    @Test
    public void testHandleRequestWhenDisabled()
    {
        TestAction action = new TestAction();
        WMenu menu = new WMenu();
        WSubMenu subMenu = new WSubMenu(TEST_TEXT);
        subMenu.setAction(action);
        menu.add(subMenu);
        
        setActiveContext(createUIContext());
        subMenu.setDisabled(true);
        
        MockRequest request = new MockRequest();
        request.setParameter(menu.getId() + "-h", "x");
                
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called when sub-menu was not selected", action.wasTriggered());

        request.setParameter(subMenu.getId(), "x");
        menu.serviceRequest(request);
        Assert.assertFalse("Action should not have been called on a disabled sub-menu", action.wasTriggered());
    }
}
