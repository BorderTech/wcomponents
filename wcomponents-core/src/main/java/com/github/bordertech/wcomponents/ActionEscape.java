package com.github.bordertech.wcomponents;

/**
 * Extensions of ActionEscape indicated that they must be handled during the action phase of processing. This is
 * important in a portal environment.
 *
 * @author Martin Shevchenko
 */
public class ActionEscape extends Escape {

	/**
	 * Default constructor.
	 */
	public ActionEscape() {
	}

	/**
	 * Constructor that allows a message and cause to be provided.
	 *
	 * @param message the exception message
	 * @param cause the original cause
	 */
	protected ActionEscape(final String message, final Throwable cause) {
		super(message, cause);
	}

}
