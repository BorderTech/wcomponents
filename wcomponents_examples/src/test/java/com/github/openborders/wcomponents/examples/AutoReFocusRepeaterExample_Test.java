package com.github.openborders.wcomponents.examples; 

import com.github.openborders.wcomponents.examples.AutoReFocusRepeaterExample;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import com.github.openborders.wcomponents.WComponent;
import com.github.openborders.wcomponents.WDropdown;
import com.github.openborders.wcomponents.util.TreeUtil;

import com.github.openborders.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.openborders.wcomponents.test.selenium.WComponentSeleniumTestCase;

/**
 * Selenium unit tests for {@link AutoReFocusRepeaterExample}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class AutoReFocusRepeaterExample_Test extends WComponentSeleniumTestCase
{
    /**
     * Creates a new AutoReFocusRepeaterExample_Test.
     */
    public AutoReFocusRepeaterExample_Test()
    {
        super(new AutoReFocusRepeaterExample());
    }
    
    @Test
    public void testAutoReFocus()
    {
        String[] paths = 
        { 
            "WDropdownTriggerActionExample[0]/WDropdown", 
            "WDropdownTriggerActionExample[1]/WDropdown", 
            "WRadioButtonTriggerActionExample[0]/WRadioButton",
            "WRadioButtonTriggerActionExample[1]/WRadioButton" 
        };
        
        // Launch the web browser to the LDE
        WebDriver driver = getDriver();

        for (String path : paths)
        {
            driver.findElement(byWComponentPath(path)).click();
            
            // The dropdowns in the example need something to be selected to trigger the submit
            WComponent comp = TreeUtil.findWComponent(getUi(), path.split("/")).getComponent();
            
            if (comp instanceof WDropdown)
            {
                WDropdown dropdown = (WDropdown) comp;
                driver.findElement(byWComponentPath(path, dropdown.getOptions().get(0))).click();
            }
            
            Assert.assertEquals("Incorrect focus for " + path, 
                                driver.findElement(byWComponentPath(path)).getAttribute("id"),
                                driver.switchTo().activeElement().getAttribute("id"));
        }
    }
}
