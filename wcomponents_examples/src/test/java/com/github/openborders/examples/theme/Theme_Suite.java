package com.github.openborders.examples.theme;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.openborders.examples.theme.ajax.Ajax_Suite;
import com.github.openborders.test.selenium.SeleniumTestSetup;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.openborders.examples.theme} package.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses
({
    Ajax_Suite.class,
    WRadioButtonSelectExample_Test.class
})
public class Theme_Suite
{
    @BeforeClass
    public static void startLde()
    {
        SeleniumTestSetup.startLde();
    }
    
    @AfterClass
    public static void stopLde()
    {
        SeleniumTestSetup.stopLde();
    }
}
