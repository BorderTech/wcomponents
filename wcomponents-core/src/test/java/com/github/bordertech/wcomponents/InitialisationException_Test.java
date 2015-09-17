package com.github.bordertech.wcomponents;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InitialisationException}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class InitialisationException_Test extends AbstractWComponentTestCase {

	/**
	 * first test message.
	 */
	public static final String ERROR_MESSAGE = "error message for InitialisationException_Test";

	/**
	 * second test message.
	 */
	public static final String SECOND_ERROR_MESSAGE = "second error message for InitialisationException_Test";

	@Test
	public void testInitialisationExceptionString() {
		InitialisationException exception = new InitialisationException(ERROR_MESSAGE);
		Assert.assertTrue("error message should be ERROR_MESSAGE", exception.getMessage().equals(
				ERROR_MESSAGE));
		Assert.assertTrue("cause should be null", exception.getCause() == null);
	}

	@Test
	public void testInitialisationExceptionStringAndThrowable() {
		Throwable throwable = new TestSampleException(SECOND_ERROR_MESSAGE);
		InitialisationException exception = new InitialisationException(ERROR_MESSAGE, throwable);
		Assert.assertTrue("error message should be ERROR_MESSAGE", exception.getMessage().equals(
				ERROR_MESSAGE));
		Assert.assertTrue("cause should be TestSampleException", exception.getCause().getClass().
				getName()
				.equals(throwable.getClass().getName()));
	}

	@Test
	public void testInitialisationExceptionThrowable() {
		Throwable throwable = new TestSampleException(SECOND_ERROR_MESSAGE);
		InitialisationException exception = new InitialisationException(throwable);
		Assert.assertTrue("error message should contain SECOND_ERROR_MESSAGE", exception.
				getMessage()
				.indexOf(SECOND_ERROR_MESSAGE) != -1);
		Assert.assertTrue("cause should be TestSampleException", exception.getCause().getClass().
				getName()
				.equals(throwable.getClass().getName()));
	}
}
