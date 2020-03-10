package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.util.SystemException;

/**
 * An invalid session token was provided on the request.
 * <p>
 * This usually occurs when a user interacts with a page and the session has expired.
 * </p>
 */
public class SessionTokenException extends SystemException {

	/**
	 * @param message the session token message
	 */
	public SessionTokenException(final String message) {
		super(message);
	}

}
