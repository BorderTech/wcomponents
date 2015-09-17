package com.github.bordertech.wcomponents.lde;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech.wcomponents.util} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DevToolkit_Test.class,
	LdeSessionUtil_Test.class,
	PlainLauncher_Test.class
})
public class Lde_Suite {
}
