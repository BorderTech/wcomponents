package com.github.dibp.wcomponents.examples;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.dibp.wcomponents.examples.petstore.PetStore_Suite;
import com.github.dibp.wcomponents.examples.theme.Theme_Suite;
import com.github.dibp.wcomponents.test.selenium.SeleniumTestSetup;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.dibp} package.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses
({
    WCheckBoxTriggerActionExample_Test.class,
    WDropdownTriggerActionExample_Test.class,
    WRadioButtonTriggerActionExample_Test.class,
    WDropdownSpaceHandlingExample_Test.class,
    WDropdownSpecialCharHandlingExample_Test.class,

    AppPreferenceParameterExample_Test.class,
    AutoReFocusExample_Test.class,
    AutoReFocusRepeaterExample_Test.class,
    CheckBoxExample_Test.class,
    EntryFieldExample_Test.class,
    ErrorGenerator_Test.class,
    ForwardExample_Test.class,
    HtmlInjector_Test.class,
    InfoDump_Test.class,
    RadioButtonExample_Test.class,
    // TODO Not sure why not working
    // SimpleFileUpload_Test.class,
    SimpleTabs_Test.class,
    TextDuplicator_HandleRequestImpl_Test.class,
    TextDuplicator_VelocityImpl_Test.class,
    TextDuplicator_Test_SeleniumImpl.class,
    TextAreaExample_Test.class,
    TextFieldExample_Test.class,
    WButtonExample_Test.class,
    WCheckBoxSelectExample_Test.class,
    WDialogExample_Test.class,
    WDropdownSubmitOnChangeExample_Test.class,
    WPopupExample_Test.class,
    WTextExample_Test.class,
    Theme_Suite.class,
    
    PetStore_Suite.class
})
public class WComponent_Example_Suite
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
