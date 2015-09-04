package com.github.bordertech.wcomponents.examples.theme.ajax;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.bordertech.wcomponents.test.selenium.SeleniumTestSetup;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech.wcomponents.examples.theme.ajax} package.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses
({
    AjaxWDropdownExample_Test.class
})
public class Ajax_Suite
{
    /**
     * Sets up the LDE used by the examples. 
     */
    @BeforeClass
    public static void startLde()
    {
        SeleniumTestSetup.startLde();
    }
    
    /**
     * Stops the LDE used by the examples. 
     */
    @AfterClass
    public static void stopLde()
    {
        SeleniumTestSetup.stopLde();
    }
}
