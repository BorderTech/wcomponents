package com.github.bordertech.wcomponents.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DiagnosticImpl_Test.class,
	ValidatingAction_Test.class,
	ValidationHelper_Test.class,
	WFieldErrorIndicator_Test.class,
	WFieldWarningIndicator_Test.class,
	WValidationErrors_Test.class
})
public class Validation_Suite {
}
