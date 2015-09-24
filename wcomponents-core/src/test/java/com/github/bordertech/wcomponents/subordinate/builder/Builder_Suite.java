package com.github.bordertech.wcomponents.subordinate.builder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * <a href="http://www.junit.org">JUnit</a> test suite for code in the
 * <b>com.github.bordertech.wcomponents.subordinate.builder</b> package.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	Action_Test.class,
	CompareExpression_Test.class,
	ExpressionBuilder_Test.class,
	GroupExpression_Test.class,
	SubordinateBuilder_Test.class,
	SyntaxException_Test.class
})
public class Builder_Suite {
}
