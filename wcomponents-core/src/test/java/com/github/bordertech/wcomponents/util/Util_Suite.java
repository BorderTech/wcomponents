package com.github.bordertech.wcomponents.util;

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
	AbstractComparator_Test.class,
	AbstractTreeNode_Test.class,
	Base64Util_Test.class,
	DefaultInternalConfiguration_Test.class,
	Duplet_Test.class,
	EmptyIterator_Test.class,
	Factory_Test.class,
	I18nUtilities_Test.class,
	LookupTableHelper_Test.class,
	ObjectGraphDump_Test.class,
	ObjectGraphNode_Test.class,
	ReflectionUtil_Test.class,
	StepCountUtil_Test.class,
	TreeUtil_Test.class,
	Triplet_Test.class,
	Util_Test.class,
	WhiteSpaceFilterPrintWriter_Test.class
})
public class Util_Suite {
}
