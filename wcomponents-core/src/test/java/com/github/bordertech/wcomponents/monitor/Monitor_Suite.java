package com.github.bordertech.wcomponents.monitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech} package.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	ProfileContainer_Test.class,
	UicStats_Test.class,
	UicStatsAsHtml_Test.class
})
public class Monitor_Suite {
}
