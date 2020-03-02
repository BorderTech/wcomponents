package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * A request was received that expected a User Context to already exist.
 */
public class NoContextException extends SystemException {

	/**
	 * @param message the error message
	 */
	public NoContextException(final String message) {
		super(message);
	}

}
