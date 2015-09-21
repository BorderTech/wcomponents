package com.github.bordertech.wcomponents.subordinate.builder;

/**
 * Thrown to indicate that an expression is syntactically invalid.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class SyntaxException extends RuntimeException {

	/**
	 * Default serialisation identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a SyntaxException.
	 *
	 * @param message the detail message.
	 */
	SyntaxException(final String message) {
		super(message);
	}
}
