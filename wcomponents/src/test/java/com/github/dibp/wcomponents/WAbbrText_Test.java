package com.github.dibp.wcomponents;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.github.dibp.wcomponents.TestLookupTable.DayOfWeekTable;

/**
 * Unit tests for {@link WAbbrText}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAbbrText_Test extends AbstractWComponentTestCase
{
    @Test 
    public void testDefaultConstructor()
    {
        WAbbrText abbr = new WAbbrText();
        Assert.assertNull("Default text should be null", abbr.getText());
        Assert.assertNull("Default abbrtext should be null", abbr.getAbbrText());
    }
    
    @Test 
    public void testTextConstructor()
    {
        String myText = "WAbbrText_Test.MyText";
        
        WAbbrText abbr = new WAbbrText(myText);
        Assert.assertEquals("Incorrect default text", myText, abbr.getText());
        Assert.assertNull("Default abbrtext should be null", abbr.getAbbrText());
    }
    
    @Test 
    public void testTextAbbrConstructor()
    {
        String myText = "WAbbrText_Test.MyText";
        String myAbbrText = "WAbbrText_Test.MyAbbr";
        
        WAbbrText abbr = new WAbbrText(myText, myAbbrText);
        Assert.assertEquals("Incorrect default text", myText, abbr.getText());
        Assert.assertEquals("Incorrect default abbr text", myAbbrText, abbr.getAbbrText());
    }
    
    @Test
    public void testSetAbbrText() throws Exception
    {
        WAbbrText abbr = new WAbbrText();
        abbr.setLocked(true);
        
        String myText = "WAbbrText_Test.MyText";

        // Set test for a users session
        setActiveContext(createUIContext());
        abbr.setAbbrText(myText);
        Assert.assertEquals("Should have session text for session 1", myText, abbr.getAbbrText());
        
        resetContext();
        Assert.assertNull("Default text should be null", abbr.getAbbrText());
    }
    
    @Test
    public void testSetTextWithDesc()
    {
        WAbbrText abbr = new WAbbrText();
        List<Object> data = new TestLookupTable().getTable(DayOfWeekTable.class);         
        TestLookupTable.TableEntry entry = (TestLookupTable.TableEntry) data.get(0); 
        
        abbr.setTextWithDesc(entry);
        Assert.assertEquals("Incorrect text", entry.getDesc(), abbr.getText());
        Assert.assertEquals("Incorrect abbr text", entry.getCode(), abbr.getAbbrText());
    }
    
    @Test
    public void testSetTextWithCode()
    {
        WAbbrText abbr = new WAbbrText();
        List<Object> data = new TestLookupTable().getTable(DayOfWeekTable.class);         
        TestLookupTable.TableEntry entry = (TestLookupTable.TableEntry) data.get(0); 
        
        abbr.setTextWithCode(entry);
        Assert.assertEquals("Incorrect text", entry.getCode(), abbr.getText());
        Assert.assertEquals("Incorrect abbr text", entry.getDesc(), abbr.getAbbrText());
    }
}
