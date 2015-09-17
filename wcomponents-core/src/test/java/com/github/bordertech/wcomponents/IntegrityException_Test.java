package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * IntegrityException_Test - unit tests for {@link IntegrityException}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class IntegrityException_Test {

	/**
	 * test message.
	 */
	private static final String TEST_MESSAGE = "test message for IntegrityException";

	@Test
	public void testConstructor() {
		IntegrityException exception = new IntegrityException(TEST_MESSAGE);

		Assert.assertEquals("message should be message set in constructor", TEST_MESSAGE, exception.
				getMessage());
	}
}
