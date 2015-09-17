package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * This is a pretty fatal kind of exception, the kind that is thrown from the very core of the UI framework when
 * initialisation completely fails.
 *
 * @author Darian Bridge
 * @version 1/10/2009
 * @since 1.0.0
 */
public class InitialisationException extends SystemException {

	/**
	 * Creates a InitialisationException with the specified message.
	 *
	 * @param msg the message.
	 */
	public InitialisationException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a InitialisationException with the specified message and cause.
	 *
	 * @param msg the message.
	 * @param throwable the cause of the exception.
	 */
	public InitialisationException(final String msg, final Throwable throwable) {
		super(msg, throwable);
	}

	/**
	 * Creates a InitialisationException with the specified cause.
	 *
	 * @param throwable the cause of the exception.
	 */
	public InitialisationException(final Throwable throwable) {
		super(throwable);
	}
}
