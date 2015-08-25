package com.github.openborders.examples; 

import com.github.openborders.examples.HtmlInjector;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.github.openborders.WebUtilities;

import com.github.openborders.test.selenium.MultiBrowserRunner;
import com.github.openborders.test.selenium.WComponentSeleniumTestCase;

/**
 * Selenium unit tests for {@link HtmlInjector}.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class HtmlInjector_Test extends WComponentSeleniumTestCase
{
    /**
     * Creates a new HtmlInjector_Test.
     */
    public HtmlInjector_Test()
    {
        super(new HtmlInjector());
    }
    
    @Test
    public void testExample()
    {
        // Launch the web browser to the LDE
        WebDriver driver = getDriver();
        
        String divText = "Hello world&";
        String html = "<div id='HtmlInjector_Test.id'>" + WebUtilities.encode(divText) + "</div>";
        
        driver.findElement(byWComponentPath("WTextArea")).sendKeys(html);
        driver.findElement(byWComponentPath("WButton")).click();
        
        Assert.assertEquals("Incorrect div text", divText, driver.findElement(By.id("HtmlInjector_Test.id")).getText());
    }    
}
