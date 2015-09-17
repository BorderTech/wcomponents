package com.github.bordertech.wcomponents;

/**
 * An IntegrityException is usually thrown to indicate a misconfiguration of a WComponent.
 *
 * @author Martin Shevchenko.
 * @since 1.0.0
 */
public class IntegrityException extends IllegalStateException {

	/**
	 * Creates an IntegrityException.
	 *
	 * @param message the message.
	 */
	public IntegrityException(final String message) {
		super(message);
	}
}
