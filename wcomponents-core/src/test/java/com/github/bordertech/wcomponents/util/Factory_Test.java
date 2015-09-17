package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * Factory_Test - JUnit tests for {@link Factory}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class Factory_Test {

	@After
	public void restoreConfig() {
		Config.reset();
	}

	@Test
	public void testImplementationExists() {
		Assert.assertFalse("No implementation should exist", Factory.implementationExists(
				TestInterface.class));

		final String key = Factory.PREFIX + TestInterface.class.getName();
		Config.getInstance().setProperty(key, " ");
		Assert.assertFalse("No implementation should exist", Factory.implementationExists(
				TestInterface.class));

		Config.getInstance().setProperty(key, TestInterfaceImpl.class.getName());
		Assert.assertTrue("An implementation should exist", Factory.implementationExists(
				TestInterface.class));
	}

	@Test(expected = SystemException.class)
	public void testNewInstanceNoImpl() {
		Factory.newInstance(TestInterface.class);
	}

	@Test
	public void testNewInstanceWithImpl() {
		Config.getInstance().setProperty(Factory.PREFIX + TestInterface.class.getName(),
				TestInterfaceImpl.class.getName());
		Assert.assertTrue("Should be an instanceof TestInterface", Factory.newInstance(
				TestInterface.class) instanceof TestInterface);
	}

	/**
	 * A test interface to use with the factory.
	 */
	public interface TestInterface {
	}

	/**
	 * An implementation of the test interface.
	 */
	public static final class TestInterfaceImpl implements TestInterface {
	}
}
