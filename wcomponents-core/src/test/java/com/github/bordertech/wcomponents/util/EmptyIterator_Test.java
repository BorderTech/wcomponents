package com.github.bordertech.wcomponents.util;

import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

/**
 * EmptyIterator_Test - JUnit tests for {@link EmptyIterator}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class EmptyIterator_Test {

	@Test
	public void testIterator() {
		EmptyIterator iterator = new EmptyIterator();

		Assert.assertFalse("Should not have any elements", iterator.hasNext());

		try {
			iterator.next();
			Assert.fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
		}

		try {
			iterator.remove();
			Assert.fail("Should have thrown an IllegalStateException");
		} catch (IllegalStateException expected) {
			Assert.assertNotNull("Thrown exception should have a message", expected.getMessage());
		}
	}
}
