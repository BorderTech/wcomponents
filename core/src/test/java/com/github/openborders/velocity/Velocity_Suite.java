package com.github.openborders.velocity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is the <a href="http://www.junit.org">JUnit</a> TestSuite for the classes within
 * {@link com.github.openborders.velocity} package.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses
({
    VelocityRenderer_Test.class,
    VelocityTemplateManager_Test.class
})
public class Velocity_Suite
{
}
