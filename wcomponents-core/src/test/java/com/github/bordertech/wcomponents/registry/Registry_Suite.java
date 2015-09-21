package com.github.bordertech.wcomponents.registry;

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
	UIRegistryAmicableImpl_Test.class,
	UIRegistryClassLoaderImpl_Test.class
})
public class Registry_Suite {
}
