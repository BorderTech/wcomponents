package com.github.bordertech.wcomponents.render;

import com.github.bordertech.wcomponents.render.webxml.WebXml_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.bordertech.wcomponents.render} package.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	WebXml_Suite.class
})
public class Render_Suite {
}
