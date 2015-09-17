package com.github.bordertech.wcomponents.velocity;

import org.junit.Assert;
import org.junit.Test;

/**
 * VelocityTemplateManager_Test - JUnit tests for {@link VelocityTemplateManager}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class VelocityTemplateManager_Test {

	@Test
	public void testToTemplateResourceName() {
		// An anonymous inner class for testing
		Object myObject = new Object() {
		};

		Assert.assertEquals("Incorrect URL for top-level class",
				"com/github/bordertech/wcomponents/velocity/VelocityTemplateManager_Test.vm",
				VelocityTemplateManager.toTemplateResourceName(VelocityTemplateManager_Test.class));
		Assert.assertEquals("Incorrect URL for inner class",
				"com/github/bordertech/wcomponents/velocity/MyClass.vm", VelocityTemplateManager.
				toTemplateResourceName(MyClass.class));
		Assert.assertEquals("Incorrect URL for anonymous class",
				"com/github/bordertech/wcomponents/velocity/1.vm", VelocityTemplateManager.
				toTemplateResourceName(myObject.getClass()));
	}

	/**
	 * An inner class for testing.
	 */
	public static final class MyClass {
	}
}
