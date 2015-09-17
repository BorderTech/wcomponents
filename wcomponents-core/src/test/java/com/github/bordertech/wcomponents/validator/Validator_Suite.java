package com.github.bordertech.wcomponents.validator;

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
	AbstractFieldValidator_Test.class,
	DateFieldPivotValidator_Test.class,
	RegExFieldValidator_Test.class,
	Validation_Test.class
})
public class Validator_Suite {
}
