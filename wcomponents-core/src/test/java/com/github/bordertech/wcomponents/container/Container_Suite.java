package com.github.bordertech.wcomponents.container;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech} package.
 *
 * @author Christina Harris
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AbstractContainerHelper_Test.class,
	AjaxDebugStructureInterceptor_Test.class,
	AjaxErrorInterceptor_Test.class,
	AjaxSetupInterceptor_Test.class,
	ContextCleanupInterceptor_Test.class,
	DataListInterceptor_Test.class,
	DebugStructureInterceptor_Test.class,
	HeadLineInterceptor_Test.class,
	InterceptorComponent_Test.class,
	ResponseCacheInterceptor_Test.class,
	SessionTokenAjaxInterceptor_Test.class,
	SessionTokenContentInterceptor_Test.class,
	SessionTokenInterceptor_Test.class,
	SubordinateControlInterceptor_Test.class,
	TargetableErrorInterceptor_Test.class,
	TargetableInterceptor_Test.class,
	TransformXMLInterceptor_Test.class,
	ValidateXMLInterceptor_Test.class,
	VelocityInterceptor_Test.class,
	WWindowInterceptor_Test.class,
	WhitespaceFilterInterceptor_Test.class,
	WrongStepAjaxInterceptor_Test.class,
	WrongStepContentInterceptor_Test.class,
	WrongStepServerInterceptor_Test.class
})
public class Container_Suite {
}
