package com.github.bordertech.wcomponents.util;

/**
 * This runtime exception can be thrown to indicate a system failure. This should only be thrown if there is either a
 * bug in the program code or a system configuration error. The operators should monitor for these exceptions and try to
 * fix the problem.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class SystemException extends RuntimeException {

	/**
	 * Creates a SystemException with the specified message.
	 *
	 * @param msg the message.
	 */
	public SystemException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a SystemException with the specified message and cause.
	 *
	 * @param msg the message.
	 * @param throwable the cause of the exception.
	 */
	public SystemException(final String msg, final Throwable throwable) {
		super(msg, throwable);
	}

	/**
	 * Creates a SystemException with the specified cause.
	 *
	 * @param throwable the cause of the exception.
	 */
	public SystemException(final Throwable throwable) {
		super(throwable);
	}
}
