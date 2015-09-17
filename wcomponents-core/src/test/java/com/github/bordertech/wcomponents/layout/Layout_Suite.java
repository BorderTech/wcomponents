package com.github.bordertech.wcomponents.layout;

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
	BorderLayout_Test.class,
	ColumnLayout_Test.class,
	FlowLayout_Test.class,
	GridLayout_Test.class
})
public class Layout_Suite {
}
