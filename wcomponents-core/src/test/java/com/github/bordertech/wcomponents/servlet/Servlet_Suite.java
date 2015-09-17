package com.github.bordertech.wcomponents.servlet;

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
	ServletRequest_Test.class,
	ServletResponse_Test.class,
	ThemeServlet_Test.class,
	WServlet_Test.class,
	WServletPerformance_Test.class
})
public class Servlet_Suite {
}
