package com.github.openborders.wcomponents.examples.theme; 

import com.github.openborders.wcomponents.examples.theme.WRadioButtonSelectExample;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebDriver;

import com.github.openborders.wcomponents.examples.SeleniumTests;
import com.github.openborders.wcomponents.test.selenium.WComponentSeleniumTestCase;

/**
 * Selenium unit tests for {@link WRadioButtonSelectExample}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
public class WRadioButtonSelectExample_Test extends WComponentSeleniumTestCase
{
    /**
     * Creates a new WRadioButtonSelectExample_Test.
     */
    public WRadioButtonSelectExample_Test()
    {
        super(new WRadioButtonSelectExample());
    }
    
    @Test
    public void testExample()
    {
        // Launch the web browser to the LDE
        WebDriver driver = getDriver();
        
        // Select NT and submit
        driver.findElement(byWComponentPath("WRadioButtonSelect[0]", "Northern Territory")).click();
        driver.findElement(byWComponentPath("WButton")).click();
        
        Assert.assertTrue("Northern Territory should be selected", 
                          driver.findElement(byWComponentPath("WRadioButtonSelect[0]", "Northern Territory")).isSelected());
        
        Assert.assertTrue("Incorrect selection text", driver.getPageSource().contains("The selected item is: Northern Territory"));
        
        // Select WA and submit
        driver.findElement(byWComponentPath("WRadioButtonSelect[0]", "Western Australia")).click();
        driver.findElement(byWComponentPath("WButton")).click();
        
        Assert.assertTrue("Western Australia should be selected", 
                          driver.findElement(byWComponentPath("WRadioButtonSelect[0]", "Western Australia")).isSelected());
        
        Assert.assertTrue("Incorrect selection text", driver.getPageSource().contains("The selected item is: Western Australia"));
    }    
}
