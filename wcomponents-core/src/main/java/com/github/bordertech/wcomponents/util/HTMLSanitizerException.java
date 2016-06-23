package com.github.bordertech.wcomponents.util;


/**
 * This exception is thrown when the HtmlSanitizerUtil cannot initialise properly. This is almost
 * certainly due to AntiSamy Policy or configuration errors.
 *
 * @author Mark Reeves
 * @since 1.2.0
 */
public class HTMLSanitizerException extends Exception {

	/**
	 * Generate a HTMLSanitizerException from another exception.
	 * @param throwable the original exception.
	 */
	public HTMLSanitizerException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * Generate a HTMLSanitizerException with a specific message.
	 * @param message the error message
	 */
	public HTMLSanitizerException(final String message) {
		super(message);
	}

	/**
	 * Generate a HTMLSanitizerException from another exception and add a specific message.
	 * @param message the exception message to add
	 * @param throwable the originating exception
	 */
	public HTMLSanitizerException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
