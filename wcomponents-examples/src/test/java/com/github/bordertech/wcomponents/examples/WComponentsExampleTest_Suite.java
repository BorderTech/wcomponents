package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.examples.petstore.PetStore_Suite;
import com.github.bordertech.wcomponents.test.selenium.SeleniumTestSetup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech.wcomponents.examples} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
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
	TextDuplicatorHandleRequestImpl_Test.class,
	TextDuplicatorVelocityImpl_Test.class,
	TextDuplicatorSeleniumImpl_Test.class,
	TextAreaExample_Test.class,
	TextFieldExample_Test.class,
	WButtonExample_Test.class,
	WCheckBoxSelectExample_Test.class,
	WDialogExample_Test.class,
	WDropdownSubmitOnChangeExample_Test.class,
	WPopupExample_Test.class,
	WTextExample_Test.class,
	WRadioButtonSelectExample_Test.class,
	AjaxWDropdownExample_Test.class,
	PetStore_Suite.class
})

/**
 * WComponets example test suite.
 */
public class WComponentsExampleTest_Suite {

	/**
	 * Sets up the LDE used by the examples.
	 */
	@BeforeClass
	public void startLde() {
		SeleniumTestSetup.startLde();
	}

	/**
	 * Stops the LDE used by the examples.
	 */
	@AfterClass
	public void stopLde() {
		SeleniumTestSetup.stopLde();
	}
}
